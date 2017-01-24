package algorithm.de;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Ackley;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.random.Random;

/**
 *
 * SHADE algorithm with linear decrease of population size.
 * Inidividuals that are going to be killed are selected based on their fitness value.
 * 
 * @author adam on 05/04/2016
 */
public class EAL_SHADE extends SHADE {

    protected final int minPopSize;
    protected final int maxPopSize;
    
    public EAL_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize) {
        super(D, MAXFES, f, H, NP, rndGenerator);
        this.minPopSize = minPopSize;
        this.maxPopSize = NP;
    }
 
    @Override
    public String getName() {
        return "Lfv_SHADE";
    }
    
    @Override
    public Individual run() {

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

        this.M_F = new double[this.H];
        this.M_CR = new double[this.H];

        for (int h = 0; h < this.H; h++) {
            this.M_F[h] = 0.5;
            this.M_CR[h] = 0.5;
        }

        /**
         * Generation iteration;
         */
        int r, Psize, pbestIndex;
        double Fg, CRg;
        List<Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial, pbestInd;
        Individual x;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR;
        int k = 0;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;
        /**
         * Archive hits
         */
        int archive_hit = 0;
        int archive_good_hit = 0;
        boolean hit = false;

        while (true) {

            this.G++;
            this.S_F = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();

            newPop = new ArrayList<>();

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
                r = rndGenerator.nextInt(this.H);
                Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                while (Fg <= 0) {
                    Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                }
                if (Fg > 1) {
                    Fg = 1;
                }
                

                CRg = OtherDistributionsUtil.normal(this.M_CR[r], 0.1);
                if (CRg > 1) {
                    CRg = 1;
                }
                if (CRg < 0) {
                    CRg = 0;
                }

                Psize = (int) (rndGenerator.nextDouble(pmin, 0.2) * this.NP);
                if (Psize < 2) {
                    Psize = 2;
                }

                pBestArray = new ArrayList<>();
                pBestArray.addAll(this.P);
                pBestArray = this.resize(pBestArray, Psize);

                /**
                 * Parent selection
                 */
                pbestInd = this.getRandBestFromList(pBestArray);
                pbestIndex = this.getPbestIndex(pbestInd);
                pbest = pbestInd.vector.clone();
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size(), pbestIndex);
                pr1 = this.P.get(rIndexes[0]).vector.clone();
                /**
                 * Archive hit
                 */
                hit = false;
                if (rIndexes[1] > this.NP - 1) {
                    pr2 = this.Aext.get(rIndexes[1] - this.NP).vector.clone();
                    hit = true;
                    archive_hit++;
                } else {
                    pr2 = this.P.get(rIndexes[1]).vector.clone();
                }
                parents = new ArrayList<>();
                parents.add(x.vector);
                parents.add(pbest);
                parents.add(pr1);
                parents.add(pr2);
                
                /**
                 * Mutation
                 */               
                v = mutation(parents, Fg);

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
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                    this.S_F.add(Fg);
                    this.S_CR.add(CRg);
                    //this.Aext.add(x);
                    wS.add(Math.abs(trial.fitness - x.fitness));
                    
                    /**
                     * Archive hit
                     */
                    if(hit) {
                        archive_good_hit++;
                    }
                    
                } else {
                    newPop.add(x);
                    this.Aext.add(trial);
                }

                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (this.FES >= this.MAXFES) {
                    break;
                }

                this.Aext = this.resizeAext(this.Aext, this.NP);
                
            }

            if (this.FES >= this.MAXFES) {
                break;
            }

            /**
             * Memories update
             */
            if (this.S_F.size() > 0) {
                wSsum = 0;
                for (Double num : wS) {
                    wSsum += num;
                }
                meanS_F1 = 0;
                meanS_F2 = 0;
                meanS_CR = 0;

                for (int s = 0; s < this.S_F.size(); s++) {
                    meanS_F1 += (wS.get(s) / wSsum) * this.S_F.get(s) * this.S_F.get(s);
                    meanS_F2 += (wS.get(s) / wSsum) * this.S_F.get(s);
                    meanS_CR += (wS.get(s) / wSsum) * this.S_CR.get(s);
                }

                this.M_F[k] = (meanS_F1 / meanS_F2);
                this.M_CR[k] = meanS_CR;

                k++;
                if (k >= this.H) {
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
            this.Aext = this.resizeAext(this.Aext, this.NP);

        }

        System.out.println("{" + archive_hit + ", " + (double) archive_hit / (double) (this.MAXFES - this.NP) * 100 + ", " + archive_good_hit + ", " + (double) archive_good_hit / (double) archive_hit * 100 + ", " + String.format(Locale.US, "%.10f", this.best.fitness - this.f.optimum()) + "}");
        
        return this.best;

    }
    
    /**
     *
     * Pruning of the worst individuals in archive
     * 
     * @param list
     * @param size
     * @return
     */
    @Override
    protected List<Individual> resizeAext(List<Individual> list, int size) {

        if(size >= list.size()){
            return list;
        }

        List<Individual> toRet = new ArrayList<>();
        toRet.addAll(list);
        int index, worst_index;
        Individual worst;

        while (toRet.size() > size) {

            worst_index = -1;
            worst = null;
            
            for(int i = 0; i < list.size(); i++) {
                
                if(worst == null || list.get(i).fitness > worst.fitness) {
                    worst = list.get(i);
                    worst_index = i;
                }
                
            }

            toRet.remove(worst_index);

        }

        return toRet;

    }
    
    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<Individual> resizePop(List<Individual> list, int size) {

        if(size == list.size()){
            return list;
        }
        
        List<Individual> toRet = new ArrayList<>();
        List<Individual> tmp = new ArrayList<>();
        tmp.addAll(list);
        Individual bestInd;

        for (int i = 0; i < size; i++) {
            bestInd = this.getBestFromList(tmp);
            toRet.add(bestInd);
            tmp.remove(bestInd);
        }

        return toRet;

    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        int NP = 100;
        int minNP = 20;
        int MAXFES = 100 * NP;
        int funcNumber = 3;
        TestFunction tf = new Ackley();
        int H = 10;
        long seed = 10304050L;
        util.random.Random generator = new util.random.UniformRandom();

        EAL_SHADE shade;

        int runs = 1;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new EAL_SHADE(dimension, MAXFES, tf, H, NP, generator, minNP);

            shade.run();

//            PrintWriter writer;
//
//            try {
//                writer = new PrintWriter("CEC2015-" + funcNumber + "-shade" + k + ".txt", "UTF-8");
//
//                writer.print("{");
//
//                for (int i = 0; i < shade.getBestHistory().size(); i++) {
//
//                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(i).fitness));
//
//                    if (i != shade.getBestHistory().size() - 1) {
//                        writer.print(",");
//                    }
//
//                }
//
//                writer.print("}");
//
//                writer.close();
//
//            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
//                Logger.getLogger(ShaDE.class.getName()).log(Level.SEVERE, null, ex);
//            }

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println(shade.getBest().fitness - tf.optimum());
            System.out.println(Arrays.toString(shade.getBest().vector));
            
//            for(Individual ind : shade.P){
//                System.out.println("ID: " + ind.id + " - fitness: " + ind.fitness);
//            }
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");

    }
    
}
