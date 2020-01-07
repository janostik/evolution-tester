package algorithm.de;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.ChebyshevDistance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.IndividualComparator;
import util.OtherDistributionsUtil;
import util.distance.EuclideanDistance;
import util.random.Random;

/**
 *
 * half DISH algorithm.
 * 
 * First half
 * Shortest distance -> largest weight
 * 
 * Second half
 * Longtest distacne -> largest weight
 * 
 * @author adam on 20/05/2019
 */
public class halfDISH_analysis extends DISH_analysis {

    public halfDISH_analysis(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize) {
        super(D, MAXFES, f, H, NP, rndGenerator, minPopSize);
    }

    @Override
    public String getName() {
        return "halfDISH";
    }

    @Override
    public Individual runAlgorithm() {

        /**
         * Initialization
         */
        this.G = 0;
        this.Aext = new ArrayList<>();
        this.best = null;
        this.bestHistory = new ArrayList<>();
        this.M_Fhistory = new ArrayList<>();
        this.M_CRhistory = new ArrayList<>();
        
        /**
         * Diversity and clustering
         */
        this.P_div_history = new ArrayList<>();
        this.Cluster_history = new ArrayList<>();
        this.Noise_history = new ArrayList<>();
        this.cl_eps = Math.abs((this.f.max(0)-this.f.min(0)))/100.0;
        this.cl_minPts = 4;
        this.cl_distance = new ChebyshevDistance();

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
        
        this.M_Fhistory.add(this.M_F.clone());
        this.M_CRhistory.add(this.M_CR.clone());
        
        /**
         * Diversity and clustering
         */
        int[] cl_res;
        DBSCANClusterer clusterer = new DBSCANClusterer(this.cl_eps, this.cl_minPts, this.cl_distance);
        this.P_div_history.add(this.calculateDiversity(this.P));
        cl_res = this.clusteringViaDBSCAN(P, clusterer);
        this.Cluster_history.add(cl_res[0]);
        this.Noise_history.add(cl_res[1]);

        /**
         * Generation iteration;
         */
        int r, Psize, pbestIndex, memoryIndex, k = 0;
        double Fg, CRg, Fw, gg, pmin = 0.125, pmax = 0.25, p, wSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2;
        Individual trial, x;
        double[] v, pbest, pr1, pr2, u, wsList = new double[NP], SFlist = new double[NP], SCRlist = new double[NP];
        int[] rIndexes;
        double[][] parents;
        List<Individual> newPop, pBestArray;

        EuclideanDistance euclid = new EuclideanDistance();
        
        while (true) {

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
                    newPop.add(trial);
                    this.Aext.add(x);

                    SFlist[memoryIndex] = Fg;
                    SCRlist[memoryIndex] = CRg;
                    /**
                     * inverse distance
                     */
                    if(gg <= 0.5) {
                        wsList[memoryIndex] = 1.0/euclid.getDistance(x.vector, trial.vector);
                    }
                    else {
                        wsList[memoryIndex] = euclid.getDistance(x.vector, trial.vector);
                    }

                    memoryIndex++;

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
            
            /**
             * Diversity and clustering
             */
            this.P_div_history.add(this.calculateDiversity(this.P));
            cl_res = this.clusteringViaDBSCAN(P, clusterer);
            this.Cluster_history.add(cl_res[0]);
            this.Noise_history.add(cl_res[1]);
            
            this.M_Fhistory.add(this.M_F.clone());
            this.M_CRhistory.add(this.M_CR.clone());

        }

        return this.best;

    }

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        int NP = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
        int minNP = 4;
        int MAXFES = 10000 * dimension;
        int funcNumber = 3;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 5;
        long seed = 10304050L;
        util.random.Random generator = new util.random.UniformRandom();

        halfDISH_analysis shade;

        int runs = 10;
        double[] bestArray = new double[runs];

        System.out.println("START: " + new Date());
        
        for (int k = 0; k < runs; k++) {

            shade = new halfDISH_analysis(dimension, MAXFES, tf, H, NP, generator, minNP);

            shade.runAlgorithm();

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
