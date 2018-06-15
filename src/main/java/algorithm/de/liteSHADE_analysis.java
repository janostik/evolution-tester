package algorithm.de;

import algorithm.Algorithm;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Cec2015;
import model.tf.PupilCostFunction;
import model.tf.TestFunction;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.ChebyshevDistance;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.distance.EuclideanDistance;

/**
 *
 * liteSHADE algorithm
 * 
 * No archive
 * Single memory cells for F and CR with best value only
 * Distance based parameter adaptation
 *
 * @author adam on 11/6/2018
 */
public class liteSHADE_analysis implements Algorithm {
    
    int D;
    int G;
    int NP;
    List<Individual> P;
    int FES;
    int MAXFES;
    TestFunction f;
    Individual best;
    List<Individual> bestHistory;
    double M_F;
    double M_CR;
    double M_dist;
    util.random.Random rndGenerator;
    int id;
    int Asize;
    List<Double> M_Fhistory;
    List<Double> M_CRhistory;
    
    /**
     * Population diversity and clustering analysis
     */
    List<Double> P_div_history;
    List<Integer> Cluster_history;
    List<Integer> Noise_history;
    int cl_minPts;
    double cl_eps;
    DistanceMeasure cl_distance;
    
    
    /**
     * @param D
     * @param MAXFES
     * @param f
     * @param NP
     * @param rndGenerator 
     */
    public liteSHADE_analysis(int D, int MAXFES, TestFunction f, int NP, util.random.Random rndGenerator) {
        this.D = D;
        this.MAXFES = MAXFES;
        this.f = f;
        this.NP = NP;
        this.rndGenerator = rndGenerator;
    }
    
    /**
     * Population diversity according to Polakova
     * @param pop
     * @return 
     */
    public double calculateDiversity(List<Individual> pop) {
        
        if(pop == null || pop.isEmpty()) {
            return -1;
        }
        
        double[] means = new double[this.D];
        for(int i = 0; i < this.D; i++) {
              means[i] = 0;  
        }
        pop.stream().forEach((ind) -> {
            for(int i = 0; i < this.D; i++) {
                means[i] += (ind.vector[i]/(double) pop.size());
            }
        });
        
        double DI = 0;
        
        for(Individual ind : pop) {
            for(int i = 0; i < this.D; i++) {
                DI += Math.pow(ind.vector[i] - means[i], 2);
            }
        }
        
        DI = Math.sqrt((1.0 / (double) pop.size())*DI);
        
        
        return DI;
        
    }
    
    /**
     * Clustering done via apache commons DBSCAN implementation
     * 
     * Returns array of [number of clusters, number of noise points]
     * 
     * @param pop
     * @param clusterer
     * @return 
     */
    public int[] clusteringViaDBSCAN(List<Individual> pop, DBSCANClusterer clusterer) {
        
        if(pop == null || pop.isEmpty()) {
            return null;
        }

        List<DoublePoint> pop_points = new ArrayList<>();
        pop.stream().forEach((ind) -> {
            pop_points.add(new DoublePoint(ind.vector));
        });
        
        List<Cluster<DoublePoint>> clusters = clusterer.cluster(pop_points);
        
        int[] result = new int[2];
        result[0] = clusters.size();
        result[1] = pop.size();
        
        clusters.stream().forEach((cl) -> {
            result[1] -= cl.getPoints().size();
        });
        
        return result;
        
    }

    @Override
    public Individual run() {

        /**
         * Initialization
         */
        this.G = 0;
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
        
        this.M_F = 0.8;
        this.M_CR = 0.5;

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
        int Psize, pbestIndex;
        double Fg, CRg, dist, memF = this.M_F, memCR = this.M_CR;
        List<Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial;
        Individual x, pbestInd;
        List<double[]> parents;

        Psize = (int) (0.1 * this.NP);
        if (Psize < 2) {
            Psize = 2;
        }

        EuclideanDistance euclid = new EuclideanDistance();
        
        while (true) {
            
            this.M_dist = -1;
            
            this.G++;

            newPop = new ArrayList<>();

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
                Fg = OtherDistributionsUtil.cauchy(this.M_F, 0.1);
                while (Fg <= 0 || Fg > 1) {
                    Fg = OtherDistributionsUtil.cauchy(this.M_F, 0.1);
                }

                CRg = OtherDistributionsUtil.normal(this.M_CR, 0.1);
                if (CRg > 1) {
                    CRg = 1;
                }
                if (CRg < 0) {
                    CRg = 0;
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
                rIndexes = this.genRandIndexes(i, this.NP, this.NP, pbestIndex);
                pr1 = this.P.get(rIndexes[0]).vector.clone();
                pr2 = this.P.get(rIndexes[1]).vector.clone();

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
                    dist = euclid.getDistance(x.vector, trial.vector);
                    
                    /**
                     * New parameters;
                     */
                    if (this.M_dist <= dist) {
                       this.M_dist = dist;
                       memF = Fg;
                       memCR = CRg;
                    }
                    
                } else {
                    newPop.add(x);
                }

                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (this.FES >= this.MAXFES) {
                    break;
                }
                
            }

            if (this.FES >= this.MAXFES) {
                break;
            }

            this.M_Fhistory.add(this.M_F);
            this.M_CRhistory.add(this.M_CR);
            
            this.M_F = memF;
            this.M_CR = memCR;
            
            /**
             * Resize of population and archives
             */
            this.P = new ArrayList<>();
            this.P.addAll(newPop);
            
            /**
             * Diversity and clustering
             */
            this.P_div_history.add(this.calculateDiversity(this.P));
            cl_res = this.clusteringViaDBSCAN(P, clusterer);
            this.Cluster_history.add(cl_res[0]);
            this.Noise_history.add(cl_res[1]);

        }
        
        return this.best;

    }
    
    /**
     * Writes MF history into predefined (by path) file
     * 
     * @param path 
     */
    public void writeMFhistory(String path) {
        
        double mf;
        
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            
            writer.print("{");
            
            for(int i = 0; i < this.M_Fhistory.size(); i++) {
                
                mf = this.M_Fhistory.get(i);

                writer.print(String.format(Locale.US, "%.10f", mf));

                if(i != this.M_Fhistory.size()-1) {
                    writer.print(",");
                }
                
            }
            
            writer.print("}");
            
            writer.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(liteSHADE_analysis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(liteSHADE_analysis.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    /**
     * Writes MCR history into predefined (by path) file
     * 
     * @param path 
     */
    public void writeMCRhistory(String path) {
        
        double mcr;
        
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            
            writer.print("{");
            
            for(int i = 0; i < this.M_CRhistory.size(); i++) {
                
                mcr = this.M_CRhistory.get(i);

                writer.print(String.format(Locale.US, "%.10f", mcr));

                if(i != this.M_CRhistory.size()-1) {
                    writer.print(",");
                }
                
            }
            
            writer.print("}");
            
            writer.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(liteSHADE_analysis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(liteSHADE_analysis.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    /**
     * Writes population diversity history into a file
     * 
     * @param path 
     */
    public void writePopDiversityHistory(String path) {
        
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            
            writer.print("{");
            
            for(int i = 0; i < this.P_div_history.size(); i++) {
                
                
                writer.print(String.format(Locale.US, "%.10f", this.P_div_history.get(i)));
                
                if(i != this.P_div_history.size()-1) {
                    writer.print(",");
                }
                
            }
            
            writer.print("}");
            
            writer.close();
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(liteSHADE_analysis.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    /**
     * Writes clustering history into a file
     * 
     * @param path 
     */
    public void writeClusteringHistory(String path) {
        
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            
            writer.print("{");
            
            for(int i = 0; i < this.Cluster_history.size(); i++) {
                
                writer.print("{");
                writer.print(this.Cluster_history.get(i));
                writer.print(",");
                writer.print(this.Noise_history.get(i));
                writer.print("}");
                
                
                if(i != this.P_div_history.size()-1) {
                    writer.print(",");
                }
                
            }
            
            writer.print("}");
            
            writer.close();
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(liteSHADE_analysis.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    /**
     * Gets the index of pbest in current population
     * 
     * @param pbest
     * @return 
     */
    protected int getPbestIndex(Individual pbest) {
        
        int toRet = -1;
        Individual cur;
        
        for(int i = 0; i < this.P.size(); i++){
            
            cur = this.P.get(i);
            
            if(cur == pbest){
                toRet = i;
            }
            
        }
        
        return toRet;
        
    }
    
    protected double[] mutation(List<double[]> parents, double F){
        
        /**
         * Parents:
         * x
         * pbest
         * pr1
         * pr2
         */
        
        double[] v = new double[this.D];
        for (int j = 0; j < this.D; j++) {

            v[j] = parents.get(0)[j] + F * (parents.get(1)[j] - parents.get(0)[j]) + F * (parents.get(2)[j] - parents.get(3)[j]);

        }
        
        return v;
        
    }
    
    /**
     * 
     * @param u
     * @param x
     * @return 
     */
    protected double[] constrainCheck(double[] u, double[] x){
        /**
         * Constrain check
         */
        for (int d = 0; d < this.D; d++) {
            if (u[d] < this.f.min(this.D)) {
                u[d] = (this.f.min(this.D) + x[d]) / 2.0;
            } else if (u[d] > this.f.max(this.D)) {
                u[d] = (this.f.max(this.D) + x[d]) / 2.0;
            }
        }
        
        return u;
    }
    
    /**
     * 
     * @param CR
     * @param v
     * @param x
     * @return 
     */
    protected double[] crossover(double CR, double[] v, double[] x){
        
        /**
         * Crossover
         */
        double[] u = new double[this.D];
        int jrand = rndGenerator.nextInt(this.D);

        for (int j = 0; j < this.D; j++) {
            if (getRandomCR() <= CR || j == jrand) {
                u[j] = v[j];
            } else {
                u[j] = x[j];
            }
        }
        
        return u;
        
    }
    
    /**
     * 
     * @return 
     */
    protected double getRandomCR(){
        return rndGenerator.nextDouble();
    }

    /**
     * Creation of initial population.
     */
    protected void initializePopulation(){
        
        /**
         * Initial population
         */
        id = 0;
        double[] features = new double[this.D];
        this.P = new ArrayList<>();
        Individual ind;

        for (int i = 0; i < this.NP; i++) {
            id = i;
            features = this.f.generateTrial(this.D).clone();
//            features = new double[this.D];
//            for(int j = 0; j < this.D; j++){
//                features[j] = this.rndGenerator.nextDouble(this.f.min(this.D), this.f.max(this.D));
//            }
            ind = new Individual(String.valueOf(id), features, this.f.fitness(features));
            this.isBest(ind);
            this.P.add(ind);
            this.FES++;
            this.writeHistory();
        }
        
    }
    
    @Override
    public List<? extends Individual> getPopulation() {
        return this.P;
    }

    @Override
    public TestFunction getTestFunction() {
        return this.f;
    }

    @Override
    public String getName() {
        return "liteSHADE";
    }

    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<Individual> resize(List<Individual> list, int size) {

        List<Individual> toRet = new ArrayList<>();
        List<Individual> tmp = new ArrayList<>();
        tmp.addAll(list);
        int bestIndex;

        for (int i = 0; i < size; i++) {
            bestIndex = this.getIndexOfBestFromList(tmp);
            toRet.add(tmp.get(bestIndex));
            tmp.remove(bestIndex);
        }

        return toRet;

    }

    /**
     *
     * @param list
     * @return
     */
    protected int getIndexOfBestFromList(List<Individual> list) {

        Individual b = null;
        int i = 0;
        int index = -1;

        for (Individual ind : list) {

            if (b == null) {
                b = ind;
                index = i;
            } else if (ind.fitness < b.fitness) {
                b = ind;
                index = i;
            }
            i++;
        }

        return index;

    }

    /**
     *
     * @param list
     * @param id
     * @return
     */
    protected Individual getRandBestFromList(List<Individual> list, String id) {
        
        int index = rndGenerator.nextInt(list.size());
        
        while(list.get(index).id.equals(id)) {
            index = rndGenerator.nextInt(list.size());
        }

        return list.get(index);

    }

    /**
     *
     * @param index
     * @param max1
     * @param max2
     * @param pbest
     * @return
     */
    protected int[] genRandIndexes(int index, int max1, int max2, int pbest) {

        int a, b;

        a = rndGenerator.nextInt(max1);
        
        while(a == pbest || a == index){
            a = rndGenerator.nextInt(max1);
        }
        
        b = rndGenerator.nextInt(max2);

        while (b == a || b == index || b == pbest) {
            b = rndGenerator.nextInt(max2);
        }

        return new int[]{a, b};
    }

    /**
     *
     */
    protected void writeHistory() {
        
        this.bestHistory.add(this.best);
        /**
         * NOTE - only for huge problems with lots of generations
         */
//        if(this.bestHistory.size() >= 1000) {
//            System.out.println("TIME at " + this.FES + " OFEs\n" + new Date());
//            System.out.println("OFV\n" + this.best.fitness);
//            System.out.println("SOLUTION\n" + Arrays.toString(this.best.vector));
//            System.out.println("-------------");
//            this.bestHistory = new ArrayList<>();
//        }
    }

    /**
     *
     * @param ind
     * @return
     */
    protected boolean isBest(Individual ind) {

        if (this.best == null || ind.fitness < this.best.fitness) {
            this.best = ind;
            return true;
        }

        return false;

    }
    
    /**
     *
     * @param list
     * @return
     */
    protected Individual getBestFromList(List<Individual> list) {

        Individual b = null;

        for (Individual ind : list) {

            if (b == null) {
                b = ind;
            } else if (ind.fitness < b.fitness) {
                b = ind;
            }
        }

        return b;

    }

    // <editor-fold defaultstate="collapsed" desc="getters and setters">
    public int getD() {
        return D;
    }

    public void setD(int D) {
        this.D = D;
    }

    public int getG() {
        return G;
    }

    public void setG(int G) {
        this.G = G;
    }

    public int getNP() {
        return NP;
    }

    public void setNP(int NP) {
        this.NP = NP;
    }

    public List<Individual> getP() {
        return P;
    }

    public void setP(List<Individual> P) {
        this.P = P;
    }

    public int getFES() {
        return FES;
    }

    public void setFES(int FES) {
        this.FES = FES;
    }

    public int getMAXFES() {
        return MAXFES;
    }

    public void setMAXFES(int MAXFES) {
        this.MAXFES = MAXFES;
    }

    public TestFunction getF() {
        return f;
    }

    public void setF(TestFunction f) {
        this.f = f;
    }

    @Override
    public Individual getBest() {
        return best;
    }

    public void setBest(Individual best) {
        this.best = best;
    }

    public List<Individual> getBestHistory() {
        return bestHistory;
    }

    public void setBestHistory(List<Individual> bestHistory) {
        this.bestHistory = bestHistory;
    }

    public util.random.Random getRndGenerator() {
        return rndGenerator;
    }

    public void setRndGenerator(util.random.Random rndGenerator) {
        this.rndGenerator = rndGenerator;
    }
    //</editor-fold>

    public static double runOneIris(int number) throws Exception {

        String path = "C:\\Users\\wiki\\Documents\\NetBeansProjects\\PupilCostFunctions\\" + number; 
        
        int dimension = 2; //38
        int NP = 20;
        int MAXFES = 100 * NP;
        int funcNumber = 14;
        TestFunction tf = new PupilCostFunction(path);
        util.random.Random generator;

        liteSHADE_analysis shade;

        int runs = 30;
        double[] bestArray = new double[runs];
        int i, best, count = 0;
        double min;

        for (int k = 0; k < runs; k++) {
            
            generator = new util.random.UniformRandom();
            shade = new liteSHADE_analysis(dimension, MAXFES, tf, NP, generator);

            shade.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = shade.getBest().fitness - tf.optimum();

            if(bestArray[k] == 0) {
                count++;
            }
            
        }

        System.out.println("=================================");
        System.out.println("Iris: " + number);
        System.out.println("Success: " + count + "/" + runs);
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");

        return count/runs;
        
    }

    public int getAsize() {
        return Asize;
    }

    public void setAsize(int Asize) {
        this.Asize = Asize;
    }

    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        int NP = 100;
        int MAXFES = 1000 * NP;
        int funcNumber = 1;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        util.random.Random generator;

        liteSHADE_analysis shade;

        int runs = 3;
        double[] bestArray = new double[runs];
        int i, best;
        double min;

        for (int k = 0; k < runs; k++) {
            
            generator = new util.random.UniformRandom();
            shade = new liteSHADE_analysis(dimension, MAXFES, tf, NP, generator);

            shade.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println(shade.getBest().fitness - tf.optimum());

            for(Individual ind : ((liteSHADE_analysis)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
//            System.out.println("Clustering");
//            System.out.println(Arrays.toString(shade.P_div_history.toArray()));
            
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