package algorithm.de;

import algorithm.Algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.net.Net;
import model.tf.Dejong;
import model.tf.Schwefel;
import model.tf.nwf.Network3;
import model.tf.nwf.Network4;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.random.Random;

/**
 *
 * @author adam 3/11/2015
 */
public class DErand1bin implements Algorithm {

    protected int D;
    protected int G;
    protected int NP;
    protected List<Individual> P;
    protected int FES;
    protected int MAXFES;
    protected TestFunction tf;
    protected Individual best;
    protected List<Individual> bestHistory;
    protected util.random.Random rndGenerator;
    protected int id;
    protected double F;
    protected double CR;

    public DErand1bin(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR) {
        this.D = D;
        this.G = 0;
        this.NP = NP;
        this.MAXFES = MAXFES;
        this.tf = f;
        this.rndGenerator = rndGenerator;
        this.id = 0;
        this.F = F;
        this.CR = CR;
    }

    @Override
    public Individual run() {

        /**
         * Initial population
         */
        initializePopulation();
        if (checkFES()) {
            return best;
        }

        List<Individual> newPop;
        Individual x, trial;
        double[] u, v;
        Individual[] parrentArray;

        /**
         * generation itteration
         */
        while (true) {

            G++;
            newPop = new ArrayList<>();

            /**
             * Iteration through all individuals in generation.
             */
            for (int xIter = 0; xIter < NP; xIter++) {

                /**
                 * Parent selection
                 */
                parrentArray = getParents(xIter);
                x = parrentArray[0];

                /**
                 * Mutation
                 */
                u = mutation(parrentArray, F);

                /**
                 * Crossover
                 */
                v = crossover(x.vector, u, CR);

                /**
                 * Trial
                 */
                trial = makeIndividualFromVector(v);
                if (checkFES()) {
                    return best;
                }

                /**
                 * New generation building
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                } else {
                    newPop.add(x);
                }

            }

            P = newPop;

        }
    }

    /**
     *
     * @param x
     * @param u
     * @param CR
     * @return
     */
    protected double[] crossover(double[] x, double[] u, double CR) {

        double[] v = new double[D];
        int jrand = rndGenerator.nextInt(D);

        for (int i = 0; i < D; i++) {

            if (i == jrand || rndGenerator.nextDouble() < CR) {
                v[i] = u[i];
            } else {
                v[i] = x[i];
            }

        }

        return v;

    }

    protected void constrain(Individual individual){
        
        tf.constrain(individual);
        
    }
    
    /**
     *
     * @param parentArray
     * @param F
     * @return
     */
    protected double[] mutation(Individual[] parentArray, double F) {

        double[] u = new double[D];
        double[] a = parentArray[1].vector;
        double[] b = parentArray[2].vector;
        double[] c = parentArray[3].vector;

        for (int i = 0; i < D; i++) {

            u[i] = a[i] + F * (b[i] - c[i]);

        }

        return u;

    }

    /**
     *
     * List of parents for mutation x, a, b, c
     *
     * @param xIndex
     * @return
     */
    protected Individual[] getParents(int xIndex) {

        Individual[] parrentArray = new Individual[4];
        List<Integer> indexes = new ArrayList<>();
        int index;

        for (int i = 0; i < NP; i++) {
            if (i != xIndex) {
                indexes.add(i);
            }
        }

        parrentArray[0] = P.get(xIndex);

        /**
         * a
         */
        index = rndGenerator.nextInt(indexes.size());
        parrentArray[1] = P.get(indexes.get(index));

        indexes.remove(index);

        /**
         * b
         */
        index = rndGenerator.nextInt(indexes.size());
        parrentArray[2] = P.get(indexes.get(index));

        indexes.remove(index);

        /**
         * c
         */
        index = rndGenerator.nextInt(indexes.size());
        parrentArray[3] = P.get(indexes.get(index));

        return parrentArray;

    }

    /**
     *
     */
    protected void initializePopulation() {

        P = new ArrayList<>();
        double[] vector;

        for (int i = 0; i < NP; i++) {

            if (checkFES()) {
                return;
            }
            vector = tf.generateTrial(D);
            P.add(makeIndividualFromVector(vector));
            
        }

    }

    /**
     *
     * @return
     */
    protected boolean checkFES() {
        return (FES >= MAXFES);
    }

    /**
     *
     * @param vector
     * @return
     */
    protected Individual makeIndividualFromVector(double[] vector) {

        Individual ind = new Individual();
        ind.id = String.valueOf(id);
        id++;
        ind.vector = vector;
        constrain(ind);
        ind.fitness = tf.fitness(vector);
        FES++;
        isBest(ind);
        writeHistory();

        return (ind);
    }

    /**
     *
     */
    protected void writeHistory() {
        if (bestHistory == null) {
            bestHistory = new ArrayList<>();
        }
        bestHistory.add(best);
    }

    /**
     *
     * @param ind
     * @return
     */
    protected boolean isBest(Individual ind) {

        if (best == null || ind.fitness < best.fitness) {
            best = ind;
            return true;
        }

        return false;

    }

    @Override
    public List<? extends Individual> getPopulation() {
        return P;
    }

    @Override
    public TestFunction getTestFunction() {
        return tf;
    }

    @Override
    public String getName() {
        return "DErand1bin";
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

    public TestFunction getTf() {
        return tf;
    }

    public void setTf(TestFunction f) {
        this.tf = f;
    }

    public List<Individual> getBestHistory() {
        return bestHistory;
    }

    public void setBestHistory(List<Individual> bestHistory) {
        this.bestHistory = bestHistory;
    }

    public Random getRndGenerator() {
        return rndGenerator;
    }

    public void setRndGenerator(Random rndGenerator) {
        this.rndGenerator = rndGenerator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getF() {
        return F;
    }

    public void setF(double F) {
        this.F = F;
    }

    public double getCR() {
        return CR;
    }

    public void setCR(double CR) {
        this.CR = CR;
    }
    //</editor-fold>

    public static void mainNetwork3(String[] args) throws Exception {

        int dimension = 50;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 5;
        TestFunction tf = new Network3();
        util.random.Random generator = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8, min;

        Algorithm de;

        int runs = 1;
        double[] bestArray = new double[runs];
        int i, best;
        Integer[] pa;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);

            de.run();

            bestArray[k] = de.getBest().fitness;
            System.out.println("Individual: " + Arrays.toString(de.getBest().vector));
            System.out.println("Profit: " + de.getBest().fitness);
            
            tf.fitness(de.getBest());
            
            System.out.println("NodeLoad: " + Arrays.toString(((Network3)tf).getNodeLoad()));
            
            System.out.println("PathLoad: ");
            System.out.print("{");
            for(int a = 0; a < ((Network3)tf).getNodeCount(); a++){
                System.out.print("{");
                for(int b = 0; b < ((Network3)tf).getNodeCount(); b++){
                    
                    System.out.print(((Network3)tf).getPathLoad()[a][b]);
                    
                    if(b != ((Network3)tf).getNodeCount() - 1){
                        System.out.print(", ");
                    }  
                    
                }
                System.out.print("}");
                
                if(a != ((Network3)tf).getNodeCount() - 1){
                    System.out.println(", ");
                } 
            }
            System.out.println("}");
            
            System.out.println("Path: ");
            System.out.print("{");
            for(int p = 0; p < ((Network3)tf).getNode_path().size(); p++) {
                pa = ((Network3)tf).getNode_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network3)tf).getNode_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Built paths: ");
            System.out.print("{");
            for(int p = 0; p < ((Network3)tf).getBuilt_path().size(); p++) {
                pa = ((Network3)tf).getBuilt_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network3)tf).getBuilt_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            for(Individual ind : ((DErand1bin)de).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");

//            System.out.println(de.getBest().fitness - ((DErand1bin) de).getBestHistory().get(MAXFES-1).fitness);
            
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
        

    }
    
    public static void mainNetwork4(String[] args) throws Exception {

        int dimension = 50;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 5;
        TestFunction tf = new Network4();
        util.random.Random generator = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8, min;

        Algorithm de;

        int runs = 1;
        double[] bestArray = new double[runs];
        int i, best;
        Integer[] pa;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);

            de.run();

            bestArray[k] = de.getBest().fitness;
            System.out.println("Individual: " + Arrays.toString(de.getBest().vector));
            System.out.println("Profit: " + de.getBest().fitness);
            
            tf.fitness(de.getBest());
            
            System.out.println("NodeLoad: " + Arrays.toString(((Network4)tf).getNodeLoad()));
            
            System.out.println("PathLoad: ");
            System.out.print("{");
            for(int a = 0; a < ((Network4)tf).getNodeCount(); a++){
                System.out.print("{");
                for(int b = 0; b < ((Network4)tf).getNodeCount(); b++){
                    
                    System.out.print(((Network4)tf).getPathLoad()[a][b]);
                    
                    if(b != ((Network4)tf).getNodeCount() - 1){
                        System.out.print(", ");
                    }  
                    
                }
                System.out.print("}");
                
                if(a != ((Network4)tf).getNodeCount() - 1){
                    System.out.println(", ");
                } 
            }
            System.out.println("}");
            
            System.out.println("Path: ");
            System.out.print("{");
            for(int p = 0; p < ((Network4)tf).getNode_path().size(); p++) {
                pa = ((Network4)tf).getNode_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network4)tf).getNode_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Built paths: ");
            System.out.print("{");
            for(int p = 0; p < ((Network4)tf).getBuilt_path().size(); p++) {
                pa = ((Network4)tf).getBuilt_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network4)tf).getBuilt_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            for(Individual ind : ((DErand1bin)de).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");

//            System.out.println(de.getBest().fitness - ((DErand1bin) de).getBestHistory().get(MAXFES-1).fitness);
            
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");

    }
    
    public static void main(String[] args) throws Exception {
    
        int dimension = 10;
        int NP = 20;
        int iter = 10;
        int MAXFES = iter * NP;
        int funcNumber = 5;
        TestFunction tf = new Dejong();
        util.random.Random generator = new util.random.UniformRandom();
        util.random.Random chaos = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8;
        Net net;

        Algorithm de;

        int runs = 30;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);

            de.run();

            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
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
