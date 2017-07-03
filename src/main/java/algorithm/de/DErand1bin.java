package algorithm.de;

import algorithm.Algorithm;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.net.Net;
import model.tf.PupilCostFunction;
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
                v = mutation(parrentArray, F);

                /**
                 * Crossover
                 */
                u = crossover(x.vector, v, CR);

                /**
                 * Constrain check
                 */
                u = constrainCheck(u, x.vector);
                
                /**
                 * Trial
                 */
                trial = new Individual(x.id, u, tf.fitness(u));

                /**
                 * New generation building
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                } else {
                    newPop.add(x);
                }
                
                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (checkFES()) {
                    break;
                }

            }
            
            if (checkFES()) {
                break;
            }

            P = newPop;

        }
        
        return best;
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
            if (u[d] < this.tf.min(this.D)) {
                u[d] = (this.tf.min(this.D) + x[d]) / 2.0;
            } else if (u[d] > this.tf.max(this.D)) {
                u[d] = (this.tf.max(this.D) + x[d]) / 2.0;
            }
        }
        
        return u;
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

        int r1, r2, r3;

        r1 = rndGenerator.nextInt(NP);
        
        while(r1 == xIndex){
            r1 = rndGenerator.nextInt(NP);
        }
        
        r2 = rndGenerator.nextInt(NP);

        while (r2 == r1 || r2 == xIndex) {
            r2 = rndGenerator.nextInt(NP);
        }
        
        r3 = rndGenerator.nextInt(NP);

        while (r3 == r2 || r3 == r1 || r3 == xIndex) {
            r3 = rndGenerator.nextInt(NP);
        }
        
        Individual[] parrentArray = new Individual[4];

        parrentArray[0] = P.get(xIndex);
        parrentArray[1] = P.get(r1);
        parrentArray[2] = P.get(r2);
        parrentArray[3] = P.get(r3);

        return parrentArray;

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
            features = this.tf.generateTrial(this.D).clone();
//            features = new double[this.D];
//            for(int j = 0; j < this.D; j++){
//                features[j] = this.rndGenerator.nextDouble(this.f.min(this.D), this.f.max(this.D));
//            }
            ind = new Individual(String.valueOf(id), features, this.tf.fitness(features));
            this.isBest(ind);
            this.P.add(ind);
            this.FES++;
            this.writeHistory();
        }
        
    }

    /**
     *
     * @return
     */
    protected boolean checkFES() {
        return (FES > MAXFES);
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
    
    public static double runOneIris(int number) throws Exception {
    
        String path = "C:\\Users\\wiki\\Documents\\NetBeansProjects\\PupilCostFunctions\\" + number;     
        
        int dimension = 2;
        int NP = 20;
        int iter = 100;
        int MAXFES = iter * NP;
        int funcNumber = 5;
        TestFunction tf = new PupilCostFunction(path);
        util.random.Random generator = new util.random.UniformRandom();
        util.random.Random chaos = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8;
        Net net;

        Algorithm de;

        int runs = 30;
        double[] bestArray = new double[runs];
        int i, best, count = 0;
        double min;

        for (int k = 0; k < runs; k++) {

            de = new DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);

            de.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = de.getBest().fitness - tf.optimum();
//            System.out.println(de.getBest().fitness - tf.optimum());
//            System.out.println(Arrays.toString(((PupilCostFunction)tf).getCoords(de.getBest().vector)));
            
//            System.out.println("=================================");
//            
//            for(Individual ind : ((DErand1bin)de).getBestHistory()){
//                i++;
//                if(ind.fitness < min){
//                    min = ind.fitness;
//                    best = i;
//                }
//            }
//            System.out.println("Best solution found in " + best + " CFE");
            
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
    
    public static void main(String[] args) throws Exception {
    
        int dimension = 10;
        int NP = 100;
        int MAXFES = 100 * NP;
        int funcNumber = 5;
        TestFunction tf = new Schwefel();
        util.random.Random generator = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8;
        Net net;

        Algorithm de;

        int runs = 10;
        double[] bestArray = new double[runs];
        PrintWriter pw;

        for (int k = 0; k < runs; k++) {

            de = new DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);

            de.run();
            
//            System.out.println(((APE_DErand1bin)de).G);
//            System.out.println("Node");
//            if(((APE_DErand1bin) de).net.getNodeWithHighestDegree() != null) {
//                System.out.println("ID: " + ((APE_DErand1bin) de).net.getNodeWithHighestDegree().id);
//                System.out.println("Fitness: " + ((APE_DErand1bin) de).net.getNodeWithHighestDegree().fitness);
//                System.out.println("Degree: " + ((APE_DErand1bin) de).net.getHighestDegree());
//            }
//            else {
//                System.out.println("No edges in the net.");
//            }

            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
           /**
            * NET manipulating
            */

//           pw = new PrintWriter("/Users/adam/Documents/RomanData/DErand/net_50iter_" + tf.name() + "_" + chaos.toString() + ".csv");
//
//           pw.println("source,target,iter;directed");
//           
//           net = ((NetLDErand1bin) de).net;
//
//           for(Edge edge : net.getEdges()){
//               
//               pw.println(edge.getSource().id + "," + edge.getTarget().id + "," + edge.iter + ",TRUE");
//               
//           }
//
//           pw.close();
            
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
