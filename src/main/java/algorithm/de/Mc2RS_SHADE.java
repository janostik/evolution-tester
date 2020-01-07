package algorithm.de;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.Individual;
import model.chaos.RankedGenerator;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.random.Random;

/**
 * 
 * This algorithm incorporates restart of the search after exhibiting stagnation
 * Stagnation is detected by stagnationGen consequent generations without improvement in whole population.
 * After the stagnation occurs, the algorithm is restarted with the initial settings
 * - new random population
 * - initial memories
 * - empty archive
 * - generators with the same probability
 * 
 * @author wiki on 23/11/2016
 */
public class Mc2RS_SHADE extends Mc2_SHADE {

    protected final int stagnationGen = 1;
    protected final List<RankedGenerator> initGenerators;
    protected Individual actualBest;
    
    public Mc2RS_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, List<RankedGenerator> generators) {
        super(D, MAXFES, f, H, NP, rndGenerator, generators);
        this.initGenerators = new ArrayList<>();
        this.initGenerators.addAll(generators);
        
    }
    
    @Override
    public Individual runAlgorithm() {

        /**
         * Initialization
         */
        this.G = 0;
        this.Aext = new ArrayList<>();
        this.best = null;
        this.actualBest = null;
        this.bestHistory = new ArrayList<>();
        this.increment = this.initInc;
        int stagCount = 0;

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
        Individual trial;
        Individual x, pbestInd;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2;
        int k = 0;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;
        boolean stagnationFlag;

        while (true) {

            this.G++;
            this.S_F = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();

            newPop = new ArrayList<>();
            
            stagnationFlag = true;

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
                pbestInd = this.getRandBestFromList(pBestArray,x.id);
                pbestIndex = this.getPbestIndex(pbestInd);
                pbest = pbestInd.vector.clone();
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size(), pbestIndex);
                pr1 = this.P.get(rIndexes[0]).vector.clone();
                if (rIndexes[1] > this.NP - 1) {
                    pr2 = this.Aext.get(rIndexes[1] - this.NP).vector.clone();
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
                    this.Aext.add(x);
                    wS.add(Math.abs(trial.fitness - x.fitness));
                    stagnationFlag = false;

                    /**
                     * Chosen chaos rank update
                     */
                    updateRankings();
                    
                } else {
                    this.increment += (0.1/NP);
                    newPop.add(x);
                }
                
                writeChaosProbabilities();

                this.FES++;
                this.isBest(trial);
                if(this.isActualBest(trial)) {
                    stagnationFlag = false;
                }
                
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
                meanS_CR1 = 0;
                meanS_CR2 = 0;

                for (int s = 0; s < this.S_F.size(); s++) {
                    meanS_F1 += (wS.get(s) / wSsum) * this.S_F.get(s) * this.S_F.get(s);
                    meanS_F2 += (wS.get(s) / wSsum) * this.S_F.get(s);
                    meanS_CR1 += (wS.get(s) / wSsum) * this.S_CR.get(s) * this.S_CR.get(s);
                    meanS_CR2 += (wS.get(s) / wSsum) * this.S_CR.get(s);
                }

                this.M_F[k] = (meanS_F1 / meanS_F2);
                this.M_CR[k] = (meanS_CR1 / meanS_CR2);

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
            
            /**
             * Stagnation detected, therefore restart
             */
            if(stagnationFlag) {
                stagCount++;
            }
            else {
                stagCount = 0;
            }
            
            if(stagCount >= this.stagnationGen) {
                /**
                 * RESTART
                 */
                this.writePopulation();
                
                this.Aext = new ArrayList<>();
                this.Aext.addAll(this.P);
                this.actualBest = null;
                this.increment = this.initInc;
                stagCount = 0;
                k = 0;

                /**
                 * Initial population
                 */
                initializePopulation();
                this.G++;

                this.M_F = new double[this.H];
                this.M_CR = new double[this.H];

                for (int h = 0; h < this.H; h++) {
                    this.M_F[h] = 0.5;
                    this.M_CR[h] = 0.5;
                }
                
                this.chaosGenerator = new ArrayList<>();
                this.chaosGenerator.addAll(this.initGenerators);
            }
            

        }

        return this.best;

    }
    
    /**
     * Creation of initial population.
     */
    @Override
    protected void initializePopulation(){
        
        /**
         * Initial population
         */
        id = 0;
        double[] features;
        this.P = new ArrayList<>();
        Individual ind;

        for (int i = 0; i < this.NP; i++) {
            id = i;
//            features = this.f.generateTrial(this.D).clone();
            features = new double[this.D];
            for(int j = 0; j < this.D; j++){
                features[j] = this.rndGenerator.nextDouble(this.f.min(this.D), this.f.max(this.D));
            }
            ind = new Individual(String.valueOf(id), features, this.f.fitness(features));
            this.isBest(ind);
            this.isActualBest(ind);
            this.P.add(ind);
            this.FES++;
            this.writeHistory();
        }
        
    }
    
    /**
     *
     * @param ind
     * @return
     */
    protected boolean isActualBest(Individual ind) {

        if (this.actualBest == null || ind.fitness < this.actualBest.fitness) {
            this.actualBest = ind;
            return true;
        }

        return false;

    }
    
    @Override
    public String getName() {
        return "Mc2RS_SHADE";
    }
    
    public void writePopulation() {
        
        Individual ind;
        
        System.out.println("{");
        
        for(int i = 0; i < this.P.size(); i++) {
            ind = this.P.get(i);
            System.out.print("{");
            System.out.print("{");
                for(int d = 0; d < this.D; d++) {
                    System.out.print(String.format(Locale.US, "%.10f", ind.vector[d]));
                    if(d != this.D - 1) {
                        System.out.print(", ");
                    }
                }
            System.out.print("}, ");
            System.out.print(String.format(Locale.US, "%.10f", ind.fitness));
            System.out.print("}");
            if(i != this.P.size() - 1) {
                System.out.println(", ");
            }
            else {
                System.out.println("");
            }
        }
        
        System.out.println("}");
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        int NP = 100;
        int MAXFES = 1000 * NP;
        int funcNumber = 7;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 10;
        util.random.Random generator;

        Mc2RS_SHADE shade;
        List<RankedGenerator> gens;

        int runs = 1;
        double[] bestArray = new double[runs];
        int i, best;
        double min;

        for (int k = 0; k < runs; k++) {
            
            generator = new util.random.UniformRandom();
            gens = RankedGenerator.getAllChaosGeneratorsV4();
            shade = new Mc2RS_SHADE(dimension, MAXFES, tf, H, NP, generator, gens);

            shade.runAlgorithm();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

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
//                Logger.getLogger(Mc2RS_SHADE.class.getName()).log(Level.SEVERE, null, ex);
//            }

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println(shade.getBest().fitness - tf.optimum());
            System.out.println(Arrays.toString(shade.getBest().vector));

            shade.printOutRankings();
            
            for(Individual ind : ((Mc2RS_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
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
