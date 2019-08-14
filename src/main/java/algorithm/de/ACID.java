package algorithm.de;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.ChebyshevDistance;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.IndividualComparator;
import util.OtherDistributionsUtil;
import util.distance.EuclideanDistance;
import util.random.Random;

/**
 *
 * ACID - Adaptive differential evolution with Clustering and Inverse problem solving for maintaining population Diversity
 * 
 * - Population clustering: Cluster emerges, it exploits and the rest of the pop. is solving inverse problem
 * - Exploiting clusters: Use of the best/1/bin strategy
 * - Population decrease: Converged cluster is killed
 * - Tabu zones: Areas around previously converged clusters are prohibited in search
 * 
 * @author wikki on 29/07/2019
 */
public class ACID extends DISH_analysis {

    /**
     * 0 - no cluster -> classic convergence
     * 1 - cluster/s -> main pop. solving ivnerese problem for limited time (while population diversity increases)
     * 2 - not enough individuals left for the main population (4) -> main population dies.
     * 3 - only clusters converge, main population is stopped (after regime 1)
     */
    protected int regime;
    protected List<List<Individual>> converging_clusters;
    protected double resolution;
    protected List<double[]> tabu;
    
    public ACID(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize, double eps, int minPts, DistanceMeasure distance_measure, double resolution) {
        super(D, MAXFES, f, H, NP, rndGenerator, minPopSize);
        
        this.cl_eps = eps;
        this.cl_minPts = minPts;
        this.cl_distance = distance_measure;
        this.resolution = resolution;
        
    }

    @Override
    public String getName() {
        return "ACID";
    }
    
    @Override
    public Individual run() {

        /**
         * Initialization
         */
        this.regime = 0;
        this.tabu = new ArrayList<>();
        
        this.G = 0;
        this.Aext = new ArrayList<>();
        this.best = null;
        
//        this.bestHistory = new ArrayList<>();
//        this.M_Fhistory = new ArrayList<>();
//        this.M_CRhistory = new ArrayList<>();
        
        /**
         * Diversity and clustering
         */
//        this.P_div_history = new ArrayList<>();
//        this.Cluster_history = new ArrayList<>();
//        this.Noise_history = new ArrayList<>();
//        this.cl_eps = Math.abs((this.f.max(0)-this.f.min(0)))/100.0;
//        this.cl_minPts = 4;
//        this.cl_distance = new ChebyshevDistance();

        /**
         * Initial population
         */
        initializePopulation();

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
        
//        this.M_Fhistory.add(this.M_F.clone());
//        this.M_CRhistory.add(this.M_CR.clone());
        
        /**
         * Diversity and clustering
         */
//        int[] cl_res;
        DBSCANClusterer clusterer = new DBSCANClusterer(this.cl_eps, this.cl_minPts, this.cl_distance);
//        this.P_div_history.add(this.calculateDiversity(this.P));
//        cl_res = this.clusteringViaDBSCAN(P, clusterer);
//        this.Cluster_history.add(cl_res[0]);
//        this.Noise_history.add(cl_res[1]);

        /**
         * Generation iteration;
         */
        int r, Psize, pbestIndex, memoryIndex, k = 0;
        double Fg, CRg, Fw, gg, pmin = 0.125, pmax = 0.25, p, wSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2, PDtmp, cl_div;
        Individual trial, x;
        double[] v, pbest, pr1, pr2, u, wsList = new double[NP], SFlist = new double[NP], SCRlist = new double[NP];
        int[] rIndexes;
        double[][] parents;
        List<Individual> newPop, pBestArray, cluster;
        
        Individual cl_best, cl_x, cl_r1, cl_r2;
        double cl_F = 0.5;
        double cl_CR = 0.8;
        List<Individual> cl_newPop, cl_pop;
        Double PD = null;

        EuclideanDistance euclid = new EuclideanDistance();
        
        endless:
        while (true) {

            switch(this.regime) {
                
                //Standard
                case 0:
                    
                    this.G++;
                    newPop = new ArrayList<>();

                    memoryIndex = 0;

                    p = ((pmax - pmin)/(double) this.MAXFES) * this.FES + pmin;

                    for (int i = 0; i < this.P.size(); i++) {

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

                        Psize = (int) (rndGenerator.nextDouble(p, 0.2) * this.P.size());
                        if(Psize < 2) {
                            Psize = 2;
                        }

                        pBestArray = this.resize(this.P, Psize);

                        /**
                         * Parent selection
                         */
                        pbestIndex = this.getIndexOfRandBestFromList(pBestArray, x.id);
                        pbest = this.P.get(pbestIndex).vector.clone();
                        rIndexes = this.genRandIndexes(i, this.P.size(), this.P.size() + this.Aext.size(), pbestIndex);
                        pr1 = this.P.get(rIndexes[0]).vector.clone();
                        if(rIndexes[1] > this.P.size() - 1) {
                            pr2 = this.Aext.get(rIndexes[1] - this.P.size()).vector.clone();
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
                         * Tabu check
                         */
                        u = tabuCheck(u);
                        
                        /**
                         * Trial ready
                         */
                        id++;
                        trial = new Individual(String.valueOf(id), u, f.fitness(u));

                        /**
                         * Trial is better
                         */
                        if(trial.fitness <= x.fitness) {
                            newPop.add(trial);
                            this.Aext.add(x);

                            SFlist[memoryIndex] = Fg;
                            SCRlist[memoryIndex] = CRg;

                            wsList[memoryIndex] = euclid.getDistance(x.vector, trial.vector);

                            memoryIndex++;

                        }else {
                            newPop.add(x);
                        }

                        this.FES++;
                        if(this.isBest(trial)) {
                            this.writeHistory();
                        }
                        if (this.FES >= this.MAXFES) {
                            break endless;
                        }

                        this.Aext = this.resizeAext(this.Aext, this.P.size());

                    }

                    if(this.FES >= this.MAXFES) {
                        break endless;
                    }

                    /**
                     * Memories update
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
                     * Search for clusters
                     */
                    this.P = new ArrayList<>();
                    this.converging_clusters = this.searchForClusters(newPop, clusterer);
                    if(this.converging_clusters == null) {
//                        this.regime = 0; 
                        this.P.addAll(newPop);
                    }
                    else {
                        newPop = this.deleteClustersFromPop(newPop, this.converging_clusters);
                        if(newPop.size() < 4) {
                            this.converging_clusters = this.unifyClusters(this.converging_clusters, newPop);
                            this.regime = 2;
                            System.out.println("Regime changed from 0 to 2");
                        }
                        else {
                            this.regime = 1;
                            System.out.println("Regime changed from 0 to 1");
                            this.P.addAll(newPop);
                        }
                    }
                    
                    /**
                     * Deep search in clusters
                     */
                    cl_newPop = new ArrayList<>();
                    
                    if(this.converging_clusters != null && !this.converging_clusters.isEmpty()) {
                        for(int cl = 0; cl < this.converging_clusters.size(); cl++) {
                            cluster = this.converging_clusters.get(cl);
                            cl_div = this.calculateClusterDiversity(cluster);
//                            System.out.println("Cluster resolution: " + cl_div);
                            if(cl_div <= this.resolution) {
                                this.converging_clusters.remove(cluster);
                                this.tabu.add(this.getBestFromCluster(cluster).vector);
                            }
                            else {

                                cl_best = this.getBestFromCluster(cluster);

                                //population iterator
                                for(int i = 0; i < cluster.size(); i++) {

                                    cl_pop = new ArrayList<>();
                                    cl_pop.addAll(cluster);
                                    cl_pop.remove(cl_best);

                                    cl_x = cluster.get(i);
                                    cl_pop.remove(cl_x);
                                    cl_r1 = this.getRandomFromList(cl_pop);
                                    cl_pop.remove(cl_r1);
                                    cl_r2 = this.getRandomFromList(cl_pop);

                                    v = mutationDEbest(cl_F, cl_best.vector, cl_r1.vector, cl_r2.vector);

                                    /**
                                     * Crossover
                                     */
                                    u = crossover(cl_CR, v, cl_x.vector);

                                    /**
                                     * Constrain check
                                     */
                                    u = constrainCheck(u, cl_x.vector);

                                    /**
                                     * Trial ready
                                     */
                                    id++;
                                    trial = new Individual(String.valueOf(id), u, f.fitness(u));

                                    if(trial.fitness <= cl_x.fitness) {
                                        cl_newPop.add(trial);
                                    }
                                    else {
                                        cl_newPop.add(cl_x);
                                    }

                                    this.FES++;
                                    if(this.isBest(trial)) {
                                        this.writeHistory();
                                    }
                                    if (this.FES >= this.MAXFES) {
                                        break endless;
                                    }                                   
                                }  

                                cluster = new ArrayList<>();
                                cluster.addAll(cl_newPop);
                                this.converging_clusters.set(cl, cluster);

//                                if(this.calculateClusterDiversity(cluster) <= this.resolution) {
//                                    this.tabu.add(this.getBestFromCluster(cluster).vector);
//                                    this.converging_clusters.remove(cluster);
//                                    this.regime = 0;
//                                }
                               
                                
                            }
                        }
                    }
                    
//                    NP = (int) Math.round(this.maxPopSize - ((double) this.FES/(double) this.MAXFES)*(this.maxPopSize - this.minPopSize));
//                    P = this.resizePop(P, NP);
                    
                    /**
                     * Diversity and clustering
                     */
//                    this.P_div_history.add(this.calculateDiversity(this.P));
//                    cl_res = this.clusteringViaDBSCAN(P, clusterer);
//                    this.Cluster_history.add(cl_res[0]);
//                    this.Noise_history.add(cl_res[1]);

//                    this.M_Fhistory.add(this.M_F.clone());
//                    this.M_CRhistory.add(this.M_CR.clone());
                    
                    break;
                //Clusters exist
                case 1:
                    
                    this.G++;
                    newPop = new ArrayList<>();

                    memoryIndex = 0;

                    p = ((pmax - pmin)/(double) this.MAXFES) * this.FES + pmin;

                    for (int i = 0; i < this.P.size(); i++) {

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

                        Psize = (int) (rndGenerator.nextDouble(p, 0.2) * this.P.size());
                        if(Psize < 2) {
                            Psize = 2;
                        }

                        pBestArray = this.resizeInverse(this.P, Psize);

                        /**
                         * Parent selection
                         */
                        pbestIndex = this.getIndexOfRandBestFromList(pBestArray, x.id);
                        pbest = this.P.get(pbestIndex).vector.clone();
                        rIndexes = this.genRandIndexes(i, this.P.size(), this.P.size() + this.Aext.size(), pbestIndex);
                        pr1 = this.P.get(rIndexes[0]).vector.clone();
                        if(rIndexes[1] > this.P.size() - 1) {
                            pr2 = this.Aext.get(rIndexes[1] - this.P.size()).vector.clone();
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
                         * Tabu check
                         */
                        u = tabuCheck(u);

                        /**
                         * Trial ready
                         */
                        id++;
                        trial = new Individual(String.valueOf(id), u, f.fitness(u));

                        /**
                         * Trial is better - inverse problem
                         */
                        if(trial.fitness >= x.fitness) {
                            newPop.add(trial);
                            this.Aext.add(x);

                            SFlist[memoryIndex] = Fg;
                            SCRlist[memoryIndex] = CRg;

                            wsList[memoryIndex] = euclid.getDistance(x.vector, trial.vector);

                            memoryIndex++;

                        }else {
                            newPop.add(x);
                        }

                        this.FES++;
                        if(this.isBest(trial)) {
                            this.writeHistory();
                            this.regime = 0;
                            System.out.println("Regime changed from 1 to 0");
                        }
                        if (this.FES >= this.MAXFES) {
                            break endless;
                        }

                        this.Aext = this.resizeAext(this.Aext, this.P.size());

                    }

                    if(this.FES >= this.MAXFES) {
                        break endless;
                    }

                    /**
                     * Memories update
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
                    
                    this.P = new ArrayList<>();
                    this.P.addAll(newPop);
                    
                    /**
                     * Deep search in clusters
                     */
                    cl_newPop = new ArrayList<>();
                    
                    if(this.converging_clusters != null && !this.converging_clusters.isEmpty()) {
                        for(int cl = 0; cl < this.converging_clusters.size(); cl++) {
                            cluster = this.converging_clusters.get(cl);
                            cl_div = this.calculateClusterDiversity(cluster);
//                            System.out.println("Cluster resolution: " + cl_div);
                            if(cl_div <= this.resolution) {
                                this.converging_clusters.remove(cluster);
                                this.tabu.add(this.getBestFromCluster(cluster).vector);
                            }
                            else {

                                cl_best = this.getBestFromCluster(cluster);

                                //population iterator
                                for(int i = 0; i < cluster.size(); i++) {

                                    cl_pop = new ArrayList<>();
                                    cl_pop.addAll(cluster);
                                    cl_pop.remove(cl_best);

                                    cl_x = cluster.get(i);
                                    cl_pop.remove(cl_x);
                                    cl_r1 = this.getRandomFromList(cl_pop);
                                    cl_pop.remove(cl_r1);
                                    cl_r2 = this.getRandomFromList(cl_pop);

                                    v = mutationDEbest(cl_F, cl_best.vector, cl_r1.vector, cl_r2.vector);

                                    /**
                                     * Crossover
                                     */
                                    u = crossover(cl_CR, v, cl_x.vector);

                                    /**
                                     * Constrain check
                                     */
                                    u = constrainCheck(u, cl_x.vector);

                                    /**
                                     * Trial ready
                                     */
                                    id++;
                                    trial = new Individual(String.valueOf(id), u, f.fitness(u));

                                    if(trial.fitness <= cl_x.fitness) {
                                        cl_newPop.add(trial);
                                    }
                                    else {
                                        cl_newPop.add(cl_x);
                                    }

                                    this.FES++;
                                    if(this.isBest(trial)) {
                                        this.writeHistory();
                                    }
                                    if (this.FES >= this.MAXFES) {
                                        break endless;
                                    }                                   
                                }  

                                cluster = new ArrayList<>();
                                cluster.addAll(cl_newPop);
                                this.converging_clusters.set(cl, cluster);

//                                double bb = this.calculateClusterDiversity(cluster);
//                                if(bb <= this.resolution) {
//                                    this.tabu.add(this.getBestFromCluster(cluster).vector);
//                                    this.converging_clusters.remove(cluster);
//                                    this.regime = 0;
//                                }
                            }
                        }
                    }
                    else {
                        this.regime = 0;
                        System.out.println("Regime changed from 1 to 0");
                    }
                    
                    break;
                //Small pop, only one cluster to rule them all
                case 2:
                    
                    /**
                     * Deep search in clusters
                     */
                    cl_newPop = new ArrayList<>();
                    
                    if(this.converging_clusters != null && !this.converging_clusters.isEmpty()) {
                        for(int cl = 0; cl < this.converging_clusters.size(); cl++) {
                            cluster = this.converging_clusters.get(cl);
                            cl_div = this.calculateClusterDiversity(cluster);
//                            System.out.println("Cluster resolution: " + cl_div);
                            if(cl_div <= this.resolution) {
                                this.converging_clusters.remove(cluster);
                                this.tabu.add(this.getBestFromCluster(cluster).vector);
                            }
                            else {

                                cl_best = this.getBestFromCluster(cluster);

                                //population iterator
                                for(int i = 0; i < cluster.size(); i++) {

                                    cl_pop = new ArrayList<>();
                                    cl_pop.addAll(cluster);
                                    cl_pop.remove(cl_best);

                                    cl_x = cluster.get(i);
                                    cl_pop.remove(cl_x);
                                    cl_r1 = this.getRandomFromList(cl_pop);
                                    cl_pop.remove(cl_r1);
                                    cl_r2 = this.getRandomFromList(cl_pop);

                                    v = mutationDEbest(cl_F, cl_best.vector, cl_r1.vector, cl_r2.vector);

                                    /**
                                     * Crossover
                                     */
                                    u = crossover(cl_CR, v, cl_x.vector);

                                    /**
                                     * Constrain check
                                     */
                                    u = constrainCheck(u, cl_x.vector);

                                    /**
                                     * Trial ready
                                     */
                                    id++;
                                    trial = new Individual(String.valueOf(id), u, f.fitness(u));

                                    if(trial.fitness <= cl_x.fitness) {
                                        cl_newPop.add(trial);
                                    }
                                    else {
                                        cl_newPop.add(cl_x);
                                    }

                                    this.FES++;
                                    if(this.isBest(trial)) {
                                        this.writeHistory();
                                    }
                                    if (this.FES >= this.MAXFES) {
                                        break endless;
                                    }                                   
                                }  

                                cluster = new ArrayList<>();
                                cluster.addAll(cl_newPop);
                                this.converging_clusters.set(cl, cluster);

//                                if(this.calculateClusterDiversity(cluster) <= this.resolution) {
//                                    this.tabu.add(this.getBestFromCluster(cluster).vector);
//                                    this.converging_clusters.remove(cluster);
//                                    if(this.converging_clusters.isEmpty())
//                                        break endless;
//                                }
                            }
                        }
                    }
                    else {
                        break endless;
                    }
                    
                    break;
                    
                case 3:
                    
                    /**
                     * Deep search in clusters
                     */
                    cl_newPop = new ArrayList<>();
                    
                    if(this.converging_clusters != null && !this.converging_clusters.isEmpty()) {
                        for(int cl = 0; cl < this.converging_clusters.size(); cl++) {
                            cluster = this.converging_clusters.get(cl);
                            cl_div = this.calculateClusterDiversity(cluster);
//                            System.out.println("Cluster resolution: " + cl_div);
                            if(cl_div <= this.resolution) {
                                this.converging_clusters.remove(cluster);
                                this.tabu.add(this.getBestFromCluster(cluster).vector);
                            }
                            else {

                                cl_best = this.getBestFromCluster(cluster);

                                //population iterator
                                for(int i = 0; i < cluster.size(); i++) {

                                    cl_pop = new ArrayList<>();
                                    cl_pop.addAll(cluster);
                                    cl_pop.remove(cl_best);

                                    cl_x = cluster.get(i);
                                    cl_pop.remove(cl_x);
                                    cl_r1 = this.getRandomFromList(cl_pop);
                                    cl_pop.remove(cl_r1);
                                    cl_r2 = this.getRandomFromList(cl_pop);

                                    v = mutationDEbest(cl_F, cl_best.vector, cl_r1.vector, cl_r2.vector);

                                    /**
                                     * Crossover
                                     */
                                    u = crossover(cl_CR, v, cl_x.vector);

                                    /**
                                     * Constrain check
                                     */
                                    u = constrainCheck(u, cl_x.vector);

                                    /**
                                     * Trial ready
                                     */
                                    id++;
                                    trial = new Individual(String.valueOf(id), u, f.fitness(u));

                                    if(trial.fitness <= cl_x.fitness) {
                                        cl_newPop.add(trial);
                                    }
                                    else {
                                        cl_newPop.add(cl_x);
                                    }

                                    this.FES++;
                                    if(this.isBest(trial)) {
                                        this.writeHistory();
                                    }
                                    if (this.FES >= this.MAXFES) {
                                        break endless;
                                    }                                   
                                }  

                                cluster = new ArrayList<>();
                                cluster.addAll(cl_newPop);
                                this.converging_clusters.set(cl, cluster);

//                                if(this.calculateClusterDiversity(cluster) <= this.resolution) {
//                                    this.tabu.add(this.getBestFromCluster(cluster).vector);
//                                    this.converging_clusters.remove(cluster);
//                                    if(this.converging_clusters.isEmpty()) {
//                                        this.regime = 0;
//                                        break;
//                                    }
//                                }
                            }
                        }
                    }
                    else {
                        this.regime = 0;
                        System.out.println("Regime changed from 3 to 0");
                    }
                    
                    break;
            } // switch end
        
            if(this.regime == 1) {
                if(PD == null) {
                    PD = this.calculateDiversity(this.P);
                }
                else {
                    PDtmp = this.calculateDiversity(this.P);
                    if(PDtmp > PD) {
                        PD = PDtmp;
                    }
                    else {
                        this.regime = 3;
                        System.out.println("Regime changed from 1 to 3");
                        PD = null;
                    }
                }
            }
            

//            System.out.println("Regime: " + this.regime + " - PD: " + this.calculateDiversity(this.P));
            
        
        } //end of endless while

        this.writeHistory();
        return this.best;

    }
    
    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<Individual> resizeInverse(List<Individual> list, int size) {

        List<Individual> toRet = new ArrayList<>();
        toRet.addAll(list);
        
        toRet.sort(new IndividualComparator().reversed());
        toRet = toRet.subList(0, size);

        return toRet;

    }
    
    /**
     * Classic rand/1 mutation
     * 
     * @param F
     * @param best
     * @param r1
     * @param r2
     * @return 
     */
    protected double[] mutationDEbest(double F, double[] best, double[] r1, double[] r2) {
        
        double[] v = new double[this.D];
        
        for(int i = 0; i < this.D; i++) {
            v[i] = best[i] + F * (r1[i] - r2[i]);
        }
        
        return v;
    }
    
    /**
     * Checks whether new posiitioon is not inside tabu area and if it is, returns new random position.
     * 
     * @param point
     * @return 
     */
    protected double[] tabuCheck(double[] point) {
        
        if(this.tabu == null || this.tabu.isEmpty()) {
            return point;
        }

        endless2:
        while(true) {
            for(double[] tab : this.tabu) {
                if(this.cl_distance.compute(tab, point) < this.cl_eps) {
                    point = this.f.generateTrial(this.D).clone();
                }
                else {
                    break endless2;
                }
            }
        }
        
        return point;
    }
    
    /**
     * Returns random individual from list
     * 
     * @param list
     * @return 
     */
    protected Individual getRandomFromList(List<Individual> list) {
        
        int index = rndGenerator.nextInt(list.size());
        
        return list.get(index);
        
    }
    
    /**
     * Returns the best individual from cluster according to objective function value
     * 
     * @param cluster
     * @return 
     */
    protected Individual getBestFromCluster(List<Individual> cluster) {
        
        Double min = null;
        Individual best = null;
        
        for(Individual ind : cluster) {
            if(min == null || ind.fitness < min) {
                best = ind;
                min = ind.fitness;
            }
        }
        
        return best;
    }
    
    /**
     * Calculates the maximum difference in objective function values in a cluster
     * 
     * @param cluster
     * @return 
     */
    protected double calculateClusterDiversity(List<Individual> cluster) {
        
        Double res = 0., min = null, max = null;
        
        for(Individual ind : cluster) {
            if(min == null || ind.fitness < min) {
                min = ind.fitness;
            }
            if(max == null || ind.fitness > max) {
                max = ind.fitness;
            }
        }
        
        res = max - min;
        
        return res;
        
    }
    
    /**
     * 
     * Unifies all clusters into one, and adds the rest of the population
     * 
     * @param clusters
     * @param population
     * @return 
     */
    protected List<List<Individual>> unifyClusters(List<List<Individual>> clusters, List<Individual> population) {
        
        List<List<Individual>> new_clusters = new ArrayList<>();
        
        clusters.forEach((cluster) -> {
            cluster.forEach((ind) -> {
                population.add(ind);
            });
        });
        
        new_clusters.add(population);
        
        return new_clusters;
    }
    
    /**
     * Removes all individuals in clusters from the population
     * 
     * @param population
     * @param clusters
     * @return 
     */
    protected List<Individual> deleteClustersFromPop(List<Individual> population, List<List<Individual>> clusters) {
        
        clusters.forEach((cluster) -> {
            cluster.forEach((ind) -> {
                population.remove(ind);
            });
        });
        
        return population;
        
    }
    
    /**
     * 
     * Returns null if there are no clusters
     * Returns clusters if there are any in the population (cluster has to contain individual with the best fitness)
     * 
     * @param population
     * @param clusterer
     * @return 
     */
    protected List<List<Individual>> searchForClusters(List<Individual> population, DBSCANClusterer clusterer) {
        
        if(population.size() < this.cl_minPts+1) {
            return null;
        }
        
        List<Cluster<Individual>> clusters = clusterer.cluster(population);
        
        if(clusters == null || clusters.isEmpty()) {
            return null;
        }
        
        List<List<Individual>> cls = new ArrayList<>();
        
        for(Cluster<Individual> cluster : clusters) {
            if(cluster.getPoints().size() < this.cl_minPts+1) {
                continue;
            }
            for(Individual ind : cluster.getPoints()) {
                if(ind.fitness == this.best.fitness) {
                    cls.add(cluster.getPoints());
                    break;
                }
            }
        }
        
        if(cls.isEmpty()) {
            return null;
        }

        return cls;
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
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 5;
        long seed = 10304050L;
        util.random.Random generator = new util.random.UniformRandom();
        
        double eps = Math.sqrt(dimension * (Math.abs((tf.max(0)-tf.min(0)))/100.0));
        
        double resolution = Math.pow(10, -8);

        ACID shade;

        int runs = 10;
        double[] bestArray = new double[runs];

        System.out.println("START: " + new Date());
        
        for (int k = 0; k < runs; k++) {

            shade = new ACID(dimension, MAXFES, tf, H, NP, generator, minNP, eps, 3, new org.apache.commons.math3.ml.distance.EuclideanDistance(), resolution);

            shade.run();

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println(shade.getBest().fitness - tf.optimum());
            System.out.println(Arrays.toString(shade.getBest().vector));

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
