package algorithm.de;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * SHADE with intelligent removal of individuals in stagnation.
 * Stagnation is detected when no change in individuals occur during one generation.
 * Squared Euclidean distance between individuals is evaluated and only those which differ less than
 * minDiff = 0.1% * range^2 * D
 * are removed, the better of the two compared ones is always kept
 * 
 * @author wiki on 24/11/2016
 */
public class Mc2IR_SHADE extends Mc2RS_SHADE {

    protected double minDifference;
    
    public Mc2IR_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, List<RankedGenerator> generators) {
        super(D, MAXFES, f, H, NP, rndGenerator, generators);
        this.minDifference = this.getMinDifference();
    }
    
    @Override
    public Individual run() {

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
        double wSsum, meanS_F1, meanS_F2, meanS_CR;
        int k = 0;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;
        boolean stagnationFlag;
        Set<Integer> toReplace;

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
                
                this.actualBest = null;
                stagCount = 0;

                toReplace = this.getToReplace();
                
                for(Integer index : toReplace) {
                    /**
                     * replaced individual goes to archive
                     */
                    this.Aext.add(this.P.get(index));
                    /**
                     * replace individual
                     */
                    this.ReplaceIndividual(index);
                    if (this.FES >= this.MAXFES) {
                        break;
                    }
                }
                
                this.Aext = this.resizeAext(this.Aext, this.NP);
                
                if (this.FES >= this.MAXFES) {
                    break;
                }
                
//                this.writePopulation();                
//                this.Aext = new ArrayList<>();
//                this.Aext.addAll(this.P);                
//                this.increment = this.initInc; 
//                k = 0;
//                /**
//                 * Initial population
//                 */
//                initializePopulation();
//                this.G++;
//                this.M_F = new double[this.H];
//                this.M_CR = new double[this.H];
//
//                for (int h = 0; h < this.H; h++) {
//                    this.M_F[h] = 0.5;
//                    this.M_CR[h] = 0.5;
//                }
//                
//                this.chaosGenerator = new ArrayList<>();
//                this.chaosGenerator.addAll(this.initGenerators);
            }
            

        }

        return this.best;

    }

    /**
     * Replaces individual at given position by a new one. retains the id.
     * 
     * @param index 
     */
    protected void ReplaceIndividual(int index) {
        
        Individual newInd, oldInd;
        
        oldInd = this.P.get(index);

        double[] features = new double[this.D];
        for(int j = 0; j < this.D; j++){
            features[j] = this.rndGenerator.nextDouble(this.f.min(this.D), this.f.max(this.D));
        }      
        newInd = new Individual(oldInd.id, features, this.f.fitness(features));
        this.isBest(newInd);
        this.isActualBest(newInd);
        this.P.remove(index);
        this.P.add(index, newInd);
        this.FES++;
        this.writeHistory();
        
    }
    
    /**
     * Returns indexes of individuals which should be replaced in the population
     * 
     * @return 
     */
    protected Set<Integer> getToReplace() {
        
        Set<Integer> toReplace = new HashSet<>();
        double distance;
        Individual a, b;
        
        for(int i = 0; i < this.P.size()-1; i++) {
            
            a = this.P.get(i);
            
            for(int j = i+1; j < this.P.size(); j++) {
                
                b = this.P.get(j);
                
                distance = this.getSquaredEuclideanDistance(a.vector, b.vector);
                
                if(distance < this.minDifference) {
                    
                    if(a.fitness < b.fitness) {
                        toReplace.add(j);
                    } else {
                        toReplace.add(i);
                    }
                    
                }
                
            }
            
        }
        
    
        return toReplace;
        
    }
    
    /**
     * returns minimum difference under which individuals are taken as at the same place
     * 
     * @return 
     */
    protected final double getMinDifference() {
        
        double difference = 0;
        
        for(int i = 0; i < this.D; i++) {
            difference += Math.pow(this.f.max(i)-this.f.min(i), 2)*0.001;
        }
        
        return difference;
        
    }
    
    /**
     * counts squared eculidean distance between two vectors
     * 
     * @param v1
     * @param v2
     * @return 
     */
    protected double getSquaredEuclideanDistance(double[] v1, double[] v2) {
        
        double sum = 0;
        
        for(int i = 0; i < v1.length; i++) {
            sum += Math.pow((v1[i] - v2[i]), 2);
        }
        
        return sum;
    }
    
    @Override
    public String getName() {
        return "Mc2IR_SHADE";
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        int NP = 100;
        int MAXFES = 1000 * NP;
        int funcNumber = 4;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 10;
        util.random.Random generator;

        Mc2IR_SHADE shade;
        List<RankedGenerator> gens;

        int runs = 30;
        double[] bestArray = new double[runs];
        int i, best;
        double min;

        for (int k = 0; k < runs; k++) {
            
            generator = new util.random.UniformRandom();
            gens = RankedGenerator.getAllChaosGeneratorsV4();
            shade = new Mc2IR_SHADE(dimension, MAXFES, tf, H, NP, generator, gens);

            shade.run();
            
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
