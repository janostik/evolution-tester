package algorithm.de;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Ackley;
import model.tf.Cec2015;
import model.tf.Cec2017;
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
 * jSO algorithm
 * 
 * @author adam on 25/06/2018
 */
public class EB_DISH_analysis extends SHADE_analysis {

    protected final int minPopSize;
    protected final int maxPopSize;
    protected double[] M_FCP;
    
    public EB_DISH_analysis(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize) {
        super(D, MAXFES, f, H, NP, rndGenerator);
        this.minPopSize = minPopSize;
        this.maxPopSize = NP;
    }
 
    @Override
    public String getName() {
        return "EB_DISH";
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
        this.M_FCP = new double[this.H];

        for (int h = 0; h < this.H; h++) {
            this.M_F[h] = 0.5;
            this.M_CR[h] = 0.8;
            this.M_FCP[h] = 0.5;
            
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
        int r, Psize, pbestIndex, mutationIndex;
        double Fg, CRg, Fw, FCP, w1, w2, deltaW;
        List<Individual> newPop, pBestArray, sortedList, toSort;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial, pbestInd, pr1Ind, pr2Ind;
        Individual x;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2;
        int k = 0;
        double pmin = 0.125, pmax = 0.25, p;
        List<double[]> parents;
        double gg;
        List<Integer> mutationIndexes;

        EuclideanDistance euclid = new EuclideanDistance();
        
        while (true) {

            this.G++;
            this.S_F = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            mutationIndexes = new ArrayList<>();
            wS = new ArrayList<>();

            newPop = new ArrayList<>();
            
            p = ((pmax - pmin)/(double) this.MAXFES) * this.FES + pmin;

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
                r = rndGenerator.nextInt(this.H);
                if(r == this.H - 1) {
                    this.M_F[r] = 0.9;
                    this.M_CR[r] = 0.9;
                }
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
                
                /**
                 * new in jSO
                 */
                gg = (double) this.FES / (double) this.MAXFES;
                if (gg < 0.25) {
                    
                    if(CRg < 0.7) {
                        CRg = 0.7;
                    }
                    
                }
                else if (gg < 0.5) {
                    
                    if(CRg < 0.6) {
                        CRg = 0.6;
                    }
                    
                }
                
                if (gg < 0.6 && Fg > 0.7) {
                    Fg = 0.7;
                }

                Psize = (int) (rndGenerator.nextDouble(p, 0.2) * this.NP);
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
                pr1Ind = this.P.get(rIndexes[0]);
                pr1 = pr1Ind.vector.clone();
                if (rIndexes[1] > this.NP - 1) {
                    pr2Ind = this.Aext.get(rIndexes[1] - this.NP);
                    pr2 = pr2Ind.vector.clone();
                } else {
                    pr2Ind = this.P.get(rIndexes[1]);
                    pr2 = pr2Ind.vector.clone();
                }
                parents = new ArrayList<>();
                parents.add(x.vector);
                
                /**
                 * Classic current-to-pbest
                 */
                if(this.rndGenerator.nextDouble() <= this.M_FCP[r]) {
                    parents.add(pbest);
                    parents.add(pr1);
                    parents.add(pr2);
                    mutationIndex = 0;
                }
                /**
                 * current-to-ord_pbest
                 */
                else {
                    toSort = new ArrayList<>();
                    toSort.add(pbestInd);
                    toSort.add(pr1Ind);
                    toSort.add(pr2Ind);
                    sortedList = this.sortOrd(toSort);
                    
                    parents.add(sortedList.get(0).vector.clone());
                    parents.add(sortedList.get(1).vector.clone());
                    parents.add(sortedList.get(2).vector.clone());
                    mutationIndex = 1;
                }
                
                
                
                if (gg < 0.2) {
                    Fw = 0.7 * Fg;
                }
                else if (gg < 0.4) {
                    Fw = 0.8 * Fg;
                }
                else {
                    Fw = 1.2 * Fg;
                }
                
                /**
                 * Mutation
                 */               
                v = mutation(parents, Fg, Fw);

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
                    mutationIndexes.add(mutationIndex);
                    
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
                
                /**
                 * M_FCP update
                 */
                w1 = 0;
                w2 = 0;
                for(int m = 0; m < mutationIndexes.size(); m++) {
                    if(mutationIndexes.get(m) == 0) {
                        w1 += wS.get(m);
                    }
                    else {
                        w2 += wS.get(m);
                    }
                }
                deltaW = w1/(w1 + w2);
                if(deltaW < 0.2) {
                    deltaW = 0.2;
                }
                if(deltaW > 0.8) {
                    deltaW = 0.8;
                }
                this.M_FCP[k] = 0.5*this.M_FCP[k] + 0.5*deltaW;
                
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
                    this.M_F[k] = ((meanS_F1 / meanS_F2) + this.M_F[k])/2;
                }
                if(meanS_CR2 != 0) {
                    this.M_CR[k] = ((meanS_CR1 / meanS_CR2) + this.M_CR[k])/2;
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
            
//            if(G % (this.MAXFES/this.maxPopSize/10) == 0) {
//                System.out.println(((double) this.FES/(double) this.MAXFES)*100 + "%");
//            }
            
            this.M_Fhistory.add(this.M_F.clone());
            this.M_CRhistory.add(this.M_CR.clone());

        }

//        System.out.println("100%");
        return this.best;

    }
    
    protected List<Individual> sortOrd(List<Individual> list) {
        
        List<Individual> sortedList = new ArrayList<>();
        sortedList.addAll(list);
        
        sortedList.sort(new IndividualComparator());
        
        return sortedList;
    }
    
    /**
     * 
     * @param parents
     * @param F
     * @param Fw
     * @return 
     */
    protected double[] mutation(List<double[]> parents, double F, double Fw){
        
        /**
         * Parents:
         * x
         * pbest
         * pr1
         * pr2
         */
        
        double[] v = new double[this.D];
        for (int j = 0; j < this.D; j++) {

            v[j] = parents.get(0)[j] + Fw * (parents.get(1)[j] - parents.get(0)[j]) + F * (parents.get(2)[j] - parents.get(3)[j]);

        }
        
        return v;
        
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
        int NP = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
        int minNP = 4;
        int MAXFES = 100 * dimension;
        int funcNumber = 10;
        TestFunction tf = new Cec2017(dimension, funcNumber);
        int H = 5;
        long seed = 10304050L;
        util.random.Random generator = new util.random.UniformRandom();

        EB_DISH_analysis shade;

        int runs = 1;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new EB_DISH_analysis(dimension, MAXFES, tf, H, NP, generator, minNP);

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
