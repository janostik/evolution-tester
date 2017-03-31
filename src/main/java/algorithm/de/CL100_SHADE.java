/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm.de;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.distance.SquaredEuclideanDistance;
import util.kmeans.KMeans;
import util.kmeans.KMeans.Cluster;
import util.kmeans.RandomPartition;
import util.random.Random;
import util.regression.LinearRegression;
import util.regression.Regression;

/**
 *
 * @author wiki
 */
public class CL100_SHADE extends SHADE {

    Map<Integer, List<double[]>> score_map;
    Map<Integer, double[]> clustering_map;
    
    public CL100_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator) {
        super(D, MAXFES, f, H, NP, rndGenerator);
        score_map = new HashMap<>();
        clustering_map = new HashMap<>();
    }
    
    /**
     * 
     * Writes the score of current population itno the score_map
     * 
     */
    public void writeScoreToMap() {
        
        List<double[]> score_list;
        
        for(Individual ind : this.P) {
            
            score_list = this.score_map.get(Integer.parseInt(ind.id));
            if(score_list == null) {
                score_list = new ArrayList<>();
            }
            
            score_list.add(new double[]{this.G, ind.score});
            
            this.score_map.put(Integer.parseInt(ind.id), score_list);
        }
        
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
        
        /**
         * Initial score written to map
         */
        this.writeScoreToMap();
        
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
        Individual[] parentArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial;
        Individual x, pbestInd;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR;
        int k = 0;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;
        List<String> succ_cluster_IDs = new ArrayList<>();

        boolean use_cluster = false;

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
                
                if(!use_cluster) {
                    pBestArray = new ArrayList<>();
                    pBestArray.addAll(this.P);
                    pBestArray = this.resize(pBestArray, Psize);
                }
                else {
                    /**
                     * Use individuals from the most successful cluster
                     */
                    pBestArray = new ArrayList<>();
                    if(succ_cluster_IDs == null || succ_cluster_IDs.isEmpty()) {
                        pBestArray.addAll(this.P);
                        pBestArray = this.resize(pBestArray, Psize);
                    }
                    else {
                       pBestArray = getIndividualByIDs(succ_cluster_IDs); 
                    } 
                }

                /**
                 * Parent selection
                 */
                /**
                 * Parent selection
                 */
                parentArray = new Individual[4];
                parentArray[0] = x;
                parentArray[1] = this.getRandBestFromList(pBestArray, x.id);
                pbestIndex = this.getPbestIndex(parentArray[1]);
                pbest = parentArray[1].vector.clone();
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size(), pbestIndex);
                parentArray[2] = this.P.get(rIndexes[0]);
                pr1 = parentArray[2].vector.clone();
                if (rIndexes[1] > this.NP - 1) {
                    pr2 = this.Aext.get(rIndexes[1] - this.NP).vector.clone();
                    parentArray[3] = null;
                } else {
                    parentArray[3] = this.P.get(rIndexes[1]);
                    pr2 = parentArray[3].vector.clone();
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
//                id++;
                trial = new Individual(x.id, u, f.fitness(u), x.score);

                /**
                 * Trial is better
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                    this.S_F.add(Fg);
                    this.S_CR.add(CRg);
                    this.Aext.add(x);
                    wS.add(x.fitness - trial.fitness);
                    
                    parentArray[0].score += 1;
                    parentArray[1].score += 1;
                    parentArray[2].score += 1;
                    parentArray[3].score += 1;
                    
                } else {
                    newPop.add(x);
                }

                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (this.FES >= this.MAXFES) {
                    break;
                }

                this.Aext = this.resizeAext(this.Aext, this.Asize);
                
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
            
            this.writeScoreToMap();
            
            if((G+1) % 100 == 0) {
                /**
                 * Count clusters
                 * Clear score of each individual
                 */
                use_cluster = true;
                succ_cluster_IDs = this.getSuccessfulClusterIDs();
                this.P.stream().forEach((ind) -> {
                    ind.score = 1;
                });
            }
            

        }

//        System.out.println("Archive hits: " + archive_hit + " - " + (double) archive_hit / (double) this.MAXFES * 100 + "%");
//        System.out.println("Good hits: " + archive_good_hit + " - " + (double) archive_good_hit / (double) archive_hit * 100 + "%");
        
        return this.best;

    }
    
    /**
     * 
     * Gets individuals by their specified IDs
     * 
     * @param ids
     * @return 
     */
    public List<Individual> getIndividualByIDs(List<String> ids) {
        
        if(ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Individual> inds = new ArrayList<>();
        
        for(Individual ind : this.P) {
            if(ids.contains(ind.id)) {
                inds.add(ind);
            }
        }
        
        return inds;
        
    }
    
    /**
     * 
     * This method returns the IDs of individuals in the successful cluster
     * 
     * @return 
     */
    public List<String> getSuccessfulClusterIDs() {
        
        double slope;
        Regression regr = new LinearRegression(); 
        
        /**
         * Create linear slopes for clustering
         */
        for(Entry<Integer, List<double[]>> individual : this.score_map.entrySet()) {
            slope = regr.getRegressionParameters(individual.getValue())[0];
            this.clustering_map.put(individual.getKey(), new double[]{slope});
        }
        
        /**
         * Clustering part
         */
        KMeans kmeans = new KMeans(3, clustering_map, new RandomPartition(), new SquaredEuclideanDistance());
        kmeans.run();
        
        /**
         * Selection of the most successful cluster
         */
        slope = -1;
        Cluster c;
        int successful = -1;
        
        for(int i = 0; i < kmeans.clusters.size(); i++) {
            
            c = kmeans.clusters.get(i);
            
            if(c.getCentroid() == null || c.getCentroid().length == 0) {
                continue;
            }
            
            if(c.getCentroid()[0] > slope) {
                slope = c.getCentroid()[0];
                successful = i;
            }
            
        }
        
        if(successful == -1) {
            return null;
        }
        
        c = kmeans.clusters.get(successful);
        
        List<String> ids = new ArrayList<>();
        for(Entry<Integer, double[]> entry : c.points.entrySet()) {
            ids.add(String.valueOf(entry.getKey()));
        }
        
        return ids;
        
    }
    
    @Override
    public String getName() {
        return "CL100_SHADE";
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        int dimension = 10; //38
        int NP = 100;
        int MAXFES = 1000 * NP;
        int funcNumber = 1;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 10;
        util.random.Random generator;

        SHADE shade;

        int runs = 3;
        double[] bestArray = new double[runs];
        int i, best;
        double min;

        for (int k = 0; k < runs; k++) {
            
            generator = new util.random.UniformRandom();
            shade = new CL100_SHADE(dimension, MAXFES, tf, H, NP, generator);

            shade.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println(shade.getBest().fitness - tf.optimum());

            for(Individual ind : ((CL100_SHADE)shade).getBestHistory()){
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
