package algorithm.de;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.distance.EuclideanDistance;

/**
 *
 * @author wikki on 08/11/2019
 */
public class DISH_mask_test extends DISH_analysis {

    private ArrayList<int[]> succ_mask_history;
    private ArrayList<double[]> succ_diff_history;
    private ArrayList<double[][]> population_history;

    public DISH_mask_test(int D, int MAXFES, TestFunction f, int H, int NP, util.random.Random rndGenerator, int minPopSize) {
        super(D, MAXFES, f, H, NP, rndGenerator, minPopSize);
    }

    @Override
    public Individual runAlgorithm() {

        /**
         * Masking
         */
        this.succ_diff_history = new ArrayList<>();
        this.succ_mask_history = new ArrayList<>();
        int[] basic_mask = new int[this.D];
        double[] basic_diff = new double[this.D];
        this.population_history = new ArrayList<>();
        
        /**
         * Initialization
         */
        this.G = 0;
        this.Aext = new ArrayList<>();
        this.best = null;
        this.bestHistory = new ArrayList<>();

        /**
         * Initial population
         */
        initializePopulation();
        
        this.succ_mask_history.add(basic_mask);
        this.succ_diff_history.add(basic_diff);
        this.population_history.add(this.getPopulationArray());

        this.M_F = new double[this.H];
        this.M_CR = new double[this.H];

        for (int h = 0; h < this.H; h++) {
            this.M_F[h] = 0.5;
            this.M_CR[h] = 0.8;
            
            if(h == this.H-1) {
                this.M_F[h] = 0.9;
                this.M_CR[h] = 0.9;
            }
        }

        /**
         * Generation iteration;
         */
        int r, Psize, pbestIndex, memoryIndex, k = 0;
        double Fg, CRg, Fw, gg, pmin = 0.125, pmax = 0.25, p, wSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2, newTrialCF;
        Individual trial, x;
        double[] v, pbest, pr1, pr2, u, wsList = new double[NP], SFlist = new double[NP], SCRlist = new double[NP], newTrial;
        int[] rIndexes, mask;
        double[][] parents;
        List<Individual> newPop, pBestArray;
        int counter_imp = 0, counter_mask = 0;

        EuclideanDistance euclid = new EuclideanDistance();
        
        while (true) {

            /**
             * Masking
             */
            this.succ_mask_history.add(basic_mask);
            this.succ_diff_history.add(basic_diff);
            
            this.G++;
            newPop = new ArrayList<>();
                
            memoryIndex = 0;

            p = ((pmax - pmin)/(double) this.MAXFES) * this.FES + pmin;

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
                r = rndGenerator.nextInt(this.H);
                Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                while (Fg <= 0) {
                    Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                }
                if(Fg > 1) {
                    Fg = 1;
                }


                CRg = OtherDistributionsUtil.normal(this.M_CR[r], 0.1);
                if(CRg > 1) {
                    CRg = 1;
                }
                else if(CRg < 0) {
                    CRg = 0;
                }

                /**
                 * new in jSO
                 */
                gg = (double) this.FES / (double) this.MAXFES;
                if(gg < 0.25) {

                    if(CRg < 0.7) {
                        CRg = 0.7;
                    }

                }
                else if(gg < 0.5) {

                    if(CRg < 0.6) {
                        CRg = 0.6;
                    }

                }

                if(gg < 0.6 && Fg > 0.7) {
                    Fg = 0.7;
                }

                Psize = (int) (rndGenerator.nextDouble(p, 0.2) * this.NP);
                if(Psize < 2) {
                    Psize = 2;
                }

                pBestArray = this.resize(this.P, Psize);

                /**
                 * Parent selection
                 */
                pbestIndex = this.getIndexOfRandBestFromList(pBestArray, x.id);
                pbest = this.P.get(pbestIndex).vector.clone();
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size(), pbestIndex);
                pr1 = this.P.get(rIndexes[0]).vector.clone();
                if(rIndexes[1] > this.NP - 1) {
                    pr2 = this.Aext.get(rIndexes[1] - this.NP).vector.clone();
                }else {
                    pr2 = this.P.get(rIndexes[1]).vector.clone();
                }
                parents = new double[4][D];
                parents[0] = x.vector;
                parents[1] = pbest;
                parents[2] = pr1;
                parents[3] = pr2;

                if(gg < 0.2) {
                    Fw = 0.7 * Fg;
                }
                else if(gg < 0.4) {
                    Fw = 0.8 * Fg;
                }
                else {
                    Fw = 1.2 * Fg;
                }

                /**
                 * Mutation
                 */               
                v = mutationDISH(parents, Fg, Fw);

                /**
                 * Crossover
                 */
                u = crossover(CRg, v, x.vector);

                /**
                 * Constrain check
                 */
                u = constrainCheck(u, x.vector);

                /**
                 * Trial ready
                 */
                id++;
                trial = new Individual(String.valueOf(id), u, f.fitness(u));

                /**
                 * Trial is better
                 */
                if(trial.fitness <= x.fitness) {
                    counter_imp++;
                    newPop.add(trial);
                    this.Aext.add(x);

                    SFlist[memoryIndex] = Fg;
                    SCRlist[memoryIndex] = CRg;
                    wsList[memoryIndex] = euclid.getDistance(x.vector, trial.vector);

                    memoryIndex++;
                    
                    /**
                     * Mask evaluation
                     */
                    mask = this.generateMask();
                    newTrial = this.maskTrial(x.vector, u, mask);
                    newTrialCF = f.fitness(newTrial);
                    if(newTrialCF < x.fitness) {
                        /**
                         * TODO
                         * save mask
                         * save difference
                         */
                        counter_mask++;
                        this.succ_mask_history.set(this.succ_mask_history.size()-1, this.sumMask(this.succ_mask_history.get(this.succ_mask_history.size()-1), mask));
                        this.succ_diff_history.set(this.succ_diff_history.size()-1, this.sumDiff(this.succ_diff_history.get(this.succ_diff_history.size()-1), mask, x.fitness - newTrialCF));
                        
                    }

                }else {
                    newPop.add(x);
                }

                this.FES++;
                if(this.isBest(trial)) {
                    this.writeHistory();
                }
                if (this.FES >= this.MAXFES) {
                    break;
                }

                this.Aext = this.resizeAext(this.Aext, this.NP);

            }

            if(this.FES >= this.MAXFES) {
                break;
            }

            /**
             * Memories update - new
             */
            if(memoryIndex > 0) {
                wSsum = 0;
                for(int i = 0; i < memoryIndex; i++) {
                    wSsum += wsList[i];
                }

                meanS_F1 = 0;
                meanS_F2 = 0;
                meanS_CR1 = 0;
                meanS_CR2 = 0;

                for (int s = 0; s < memoryIndex; s++) {
                    meanS_F1 += (wsList[s] / wSsum) * SFlist[s] * SFlist[s];
                    meanS_F2 += (wsList[s] / wSsum) * SFlist[s];
                    meanS_CR1 += (wsList[s] / wSsum) * SCRlist[s] * SCRlist[s];
                    meanS_CR2 += (wsList[s] / wSsum) * SCRlist[s];
                }

                if(meanS_F2 != 0) {
                    this.M_F[k] = ((meanS_F1 / meanS_F2) + this.M_F[k])/2;
                }
                if(meanS_CR2 != 0) {
                    this.M_CR[k] = ((meanS_CR1 / meanS_CR2) + this.M_CR[k])/2;
                }

                k++;
                if (k >= (this.H - 1)) {
                    k = 0;
                }
            }
            
            /**
             * Resize of population and archives
             */
            this.P = new ArrayList<>();
            this.P.addAll(newPop);
            NP = (int) Math.round(this.maxPopSize - ((double) this.FES/(double) this.MAXFES)*(this.maxPopSize - this.minPopSize));
            P = this.resizePop(P, NP);
            this.population_history.add(this.getPopulationArray());

        }

        System.out.println("Improvements: " + counter_imp);
        System.out.println("Masks: " + counter_mask);
        
        return this.best;

    }
    
    /**
     * Gets population array from this.P
     * @return 
     */
    private double[][] getPopulationArray() {
        
        double[][] pop = new double[this.P.size()][this.D];
        
        for(int i = 0; i < this.P.size(); i++) {
            pop[i] = this.P.get(i).vector.clone();
        }
        
        return pop;
    }
    
    /**
     * 
     * @param path
     * @param func
     * @param run 
     */
    public void printMaskHistory(String path, int func, int run) {
        
        try {
            PrintWriter pw = new PrintWriter(path + "mask" + func + "-" + run + ".txt", "UTF-8");
            
            pw.write("{");
            
            for(int i = 0; i < this.succ_mask_history.size(); i++) {
                pw.write("{");
                
                for(int j = 0; j < this.succ_mask_history.get(i).length; j++) {

                    pw.write(String.format(Locale.US, "%d",this.succ_mask_history.get(i)[j]));

                    if(j < this.succ_mask_history.get(i).length-1){
                        pw.write(",");
                    }
                }
                
                pw.write("}");
                
                if(i < this.succ_mask_history.size()-1){
                    pw.write(",");
                }
            }
            
            pw.write("}");
            
            pw.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(DISH_mask_test.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            PrintWriter pw = new PrintWriter(path + "diff" + func + "-" + run + ".txt", "UTF-8");
            
            pw.write("{");
            
            for(int i = 0; i < this.succ_diff_history.size(); i++) {
                pw.write("{");
                
                for(int j = 0; j < this.succ_diff_history.get(i).length; j++) {

                    pw.write(String.format(Locale.US, "%.10f",this.succ_diff_history.get(i)[j]));

                    if(j < this.succ_diff_history.get(i).length-1){
                        pw.write(",");
                    }
                }
                
                pw.write("}");
                
                if(i < this.succ_diff_history.size()-1){
                    pw.write(",");
                }
            }
            
            pw.write("}");
            
            pw.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(DISH_mask_test.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            PrintWriter pw = new PrintWriter(path + "population" + func + "-" + run + ".txt", "UTF-8");
            
            pw.write("{");
            
            for(int i = 0; i < this.population_history.size(); i++) {
                pw.write("{");
                
                for(int j = 0; j < this.population_history.get(i).length; j++) {

                    pw.write("{");
                    
                    for(int k = 0; k < this.population_history.get(i)[j].length; k++) {
                        
                        pw.write(String.format(Locale.US, "%.10f",this.population_history.get(i)[j][k]));
                        
                        if(k < this.population_history.get(i)[j].length-1){
                            pw.write(",");
                        }
                        
                    } 
                    
                    pw.write("}");

                    if(j < this.population_history.get(i).length-1){
                        pw.write(",");
                    }
                }
                
                pw.write("}");
                
                if(i < this.population_history.size()-1){
                    pw.write(",");
                }
            }
            
            pw.write("}");
            
            pw.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(DISH_mask_test.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * 
     * @param act
     * @param mask
     * @return 
     */
    private int[] sumMask(int[] act, int[] mask) {
        
        int[] sum = act.clone();
        for(int i = 0; i < this.D; i++) {
            sum[i] += mask[i];
        }
        
        return sum;
        
    }
    
    /**
     * 
     * @param act
     * @param diff
     * @return 
     */
    private double[] sumDiff(double[] act, int[] mask, double diff) {
        
        double[] sum = act.clone();
        for(int i = 0; i < this.D; i++) {
            if(mask[i] == 1)
                sum[i] += diff;
        }
        
        return sum;
        
    }
    
    /**
     * Generates mask with 0.5 probability.
     * @return 
     */
    private int[] generateMask() {
        
        int[] mask = new int[this.D];
        
        for(int i = 0; i < this.D; i++) {
            mask[i] = new Random().nextInt(2);
        }
        
        return mask;
    }
    
    /**
     * Returns new trial based on mask.
     * @param original
     * @param mutant
     * @param mask
     * @return 
     */
    private double[] maskTrial(double[] original, double[] mutant, int[] mask) {
        
        double[] trial = original.clone();
        
        for(int i = 0; i < this.D; i++) {
            if(mask[i] == 1)
                trial[i] = mutant[i];
        }
        
        return trial;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        int NP = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
        int minNP = 4;
        int MAXFES = 10000 * dimension;
        int funcNumber = 6;
//        TestFunction tf = new Cec2015(dimension, funcNumber);

        TestFunction tf = new Cec2015(dimension, funcNumber);
//        TestFunction tf = new CuttingStock1D();
        
        int H = 5;
        long seed = 10304050L;
        util.random.Random generator = new util.random.UniformRandom();

        DISH_mask_test shade;

        int runs = 1;
        double[] bestArray = new double[runs];

        System.out.println("START: " + new Date());
        
        for (int k = 0; k < runs; k++) {

            shade = new DISH_mask_test(dimension, MAXFES, tf, H, NP, generator, minNP);

            shade.runAlgorithm();

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println(shade.getBest().fitness - tf.optimum());
            System.out.println(shade.getNiceVector(shade.getBest().vector));
            System.out.println("Best solution found in: " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0]);

        }

        System.out.println("END: " + new Date());
        
        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
    }
    
}
