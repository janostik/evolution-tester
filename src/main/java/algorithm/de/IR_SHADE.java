package algorithm.de;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Individual;
import model.tf.TestFunction;
import util.OtherDistributionsUtil;
import util.random.Random;

/**
 *
 * Classic SHADE with Intelligent Replacement of individuals.
 * 
 * @author wiki on 24/11/2016
 */
public class IR_SHADE extends SHADE {

    protected final int stagnationGen = 1;
    protected Individual actualBest;
    protected double minDifference;
    
    public IR_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator) {
        super(D, MAXFES, f, H, NP, rndGenerator);
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
                pbestInd = this.getRandBestFromList(pBestArray);
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

                    
                } else {
                    newPop.add(x);
                }


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
        return "IR_SHADE";
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }

    
    
}
