package algorithm.de;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Ackley;
import model.tf.TestFunction;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.ChebyshevDistance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.distance.EuclideanDistance;
import util.random.Random;

/**
 *
 * L-SHADE with F & CR weights according to distance rather than fitness difference 
 * 
 * @author adam on 26/06/2017
 */
public class DbL_SHADE_analysis extends SHADE_analysis {

    protected final int minPopSize;
    protected final int maxPopSize;
    
    public DbL_SHADE_analysis(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize) {
        super(D, MAXFES, f, H, NP, rndGenerator);
        this.minPopSize = minPopSize;
        this.maxPopSize = NP;
    }
 
    @Override
    public String getName() {
        return "DbL_SHADE";
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
            this.M_CR[h] = 0.5;
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
        int r, Psize, pbestIndex;
        double Fg, CRg;
        List<Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial, pbestInd;
        Individual x;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2;
        int k = 0;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;

        EuclideanDistance euclid = new EuclideanDistance();
        
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
                pbestInd = this.getRandBestFromList(pBestArray, x.id);
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
                    wS.add(euclid.getDistance(x.vector, trial.vector));
                    
                } else {
                    newPop.add(x);
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
                meanS_CR1 = 0;
                meanS_CR2 = 0;

                for (int s = 0; s < this.S_F.size(); s++) {
                    meanS_F1 += (wS.get(s) / wSsum) * this.S_F.get(s) * this.S_F.get(s);
                    meanS_F2 += (wS.get(s) / wSsum) * this.S_F.get(s);
                    meanS_CR1 += (wS.get(s) / wSsum) * this.S_CR.get(s) * this.S_CR.get(s);
                    meanS_CR2 += (wS.get(s) / wSsum) * this.S_CR.get(s);
                }

                if(meanS_F2 != 0) {
                    this.M_F[k] = (meanS_F1 / meanS_F2);
                }
                if(meanS_CR2 != 0) {
                    this.M_CR[k] = (meanS_CR1 / meanS_CR2);
                }

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

        DbL_SHADE_analysis shade;

        int runs = 1;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new DbL_SHADE_analysis(dimension, MAXFES, tf, H, NP, generator, minNP);

            shade.runAlgorithm();

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
