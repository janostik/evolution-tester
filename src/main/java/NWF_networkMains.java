
import algorithm.de.DErand1bin;
import algorithm.de.Mc_SHADE;
import algorithm.de.SHADE;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.nwf.Network2;
import model.tf.nwf.Network2a;
import model.tf.nwf.Network2b;
import model.tf.nwf.Network3;
import model.tf.nwf.Network4;
import model.tf.TestFunction;
import model.tf.nwf.Network2c;
import model.tf.nwf.SpalovnyCR_10;
import model.tf.nwf.SpalovnyCR_14;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author wiki
 */
public class NWF_networkMains {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void mainNetwork2(String[] args) throws Exception {
        int dimension = 19;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 14;
        TestFunction tf = new Network2();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Mc_SHADE shade;

        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            shade = new Mc_SHADE(dimension, MAXFES, tf, H, NP, generator);

            shade.runAlgorithm();
//            shade.printOutRankings();

            bestArray[k] = shade.getBest().fitness;
            
            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + shade.getBest().fitness);
            tf.fitness(shade.getBest());
            
            System.out.println("Path: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2)tf).getNode_path().size(); p++) {
                System.out.print(((Network2)tf).getNode_path().get(p));
                
                if(p != ((Network2)tf).getNode_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Built paths: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2)tf).getBuilt_path().size(); p++) {
                pa = ((Network2)tf).getBuilt_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network2)tf).getBuilt_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            
            for(Individual ind : ((Mc_SHADE)shade).getBestHistory()){
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
    
    public static void mainNetwork2a(String[] args) throws Exception {
        int dimension = 50;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 14;
        TestFunction tf = new Network2a();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Mc_SHADE shade;

        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            shade = new Mc_SHADE(dimension, MAXFES, tf, H, NP, generator);

            shade.runAlgorithm();
//            shade.printOutRankings();

            PrintWriter writer;

            try {
                writer = new PrintWriter(tf.name() + "-mcshade" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int z = 0; z < shade.getBestHistory().size(); z++) {

                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(z).fitness));

                    if (z != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                
            }
            
            bestArray[k] = shade.getBest().fitness;
            
            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + shade.getBest().fitness);
            tf.fitness(shade.getBest());
            
            System.out.println("Path: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2a)tf).getNode_path().size(); p++) {
                System.out.print(((Network2a)tf).getNode_path().get(p));
                
                if(p != ((Network2a)tf).getNode_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Built paths: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2a)tf).getBuilt_path().size(); p++) {
                pa = ((Network2a)tf).getBuilt_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network2a)tf).getBuilt_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            
            for(Individual ind : ((Mc_SHADE)shade).getBestHistory()){
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
    
    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void mainNetwork2b(String[] args) throws Exception {
        int dimension = 20;
        int NP = 100;
        int MAXFES = 2000 * NP;
        
        TestFunction tf = new Network2b();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Mc_SHADE shade;

        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        long start_time, end_time;
        
        start_time = System.currentTimeMillis();
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            shade = new Mc_SHADE(dimension, MAXFES, tf, H, NP, generator);

            shade.runAlgorithm();
//            shade.printOutRankings();

//            PrintWriter writer;
//
//            try {
//                writer = new PrintWriter(tf.name() + "-mcshade" + k + ".txt", "UTF-8");
//
//                writer.print("{");
//
//                for (int z = 0; z < shade.getBestHistory().size(); z++) {
//
//                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(z).fitness));
//
//                    if (z != shade.getBestHistory().size() - 1) {
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
//                
//            }
            
            bestArray[k] = shade.getBest().fitness;
//            System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
//            
//            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + shade.getBest().fitness);
            tf.fitness(shade.getBest());
            
            System.out.println("Amount: " + ((Network2b)tf).getActualAmount());
//            System.out.println("Path: ");
//            System.out.print("{");
//            for(int p = 0; p < ((Network2b)tf).getNode_path().size(); p++) {
//                System.out.print(((Network2b)tf).getNode_path().get(p));
//                
//                if(p != ((Network2b)tf).getNode_path().size()-1){
//                    System.out.print(", ");
//                }
//                
//            }
//            System.out.println("}");
//            
//            System.out.println("Built paths: ");
//            System.out.print("{");
//            for(int p = 0; p < ((Network2b)tf).getBuilt_path().size(); p++) {
//                pa = ((Network2b)tf).getBuilt_path().get(p);
//                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
//                
//                if(p != ((Network2b)tf).getBuilt_path().size()-1){
//                    System.out.print(", ");
//                }
//                
//            }
//            System.out.println("}");
            
            
//            for(Individual ind : ((MCShaDE)shade).getBestHistory()){
//                i++;
//                if(ind.fitness < min){
//                    min = ind.fitness;
//                    best = i;
//                }
//            }
//            System.out.println("Best solution found in " + best + " CFE");
            
        }
        
        end_time = System.currentTimeMillis();

        long millis = end_time - start_time;
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;
        millis = millis % 1000;

        String time = String.format("%02d:%02d:%02d:%d", hour, minute, second, millis);
        
        System.out.println("=================================");
        System.out.println("Time: " + time);
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
    }
    
    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void mainNetwork2bSHADE(String[] args) throws Exception {
        int dimension = 20;
        int NP = 100;
        int MAXFES = 2000 * NP;
        
        TestFunction tf = new Network2b();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        SHADE shade;

        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        long start_time, end_time;
        
        start_time = System.currentTimeMillis();
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            shade = new SHADE(dimension, MAXFES, tf, H, NP, generator);

            shade.runAlgorithm();
//            shade.printOutRankings();

//            PrintWriter writer;
//
//            try {
//                writer = new PrintWriter(tf.name() + "-mcshade" + k + ".txt", "UTF-8");
//
//                writer.print("{");
//
//                for (int z = 0; z < shade.getBestHistory().size(); z++) {
//
//                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(z).fitness));
//
//                    if (z != shade.getBestHistory().size() - 1) {
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
//                
//            }
            
            bestArray[k] = shade.getBest().fitness;
//            System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
//            
//            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + shade.getBest().fitness);
            tf.fitness(shade.getBest());
            
            System.out.println("Amount: " + ((Network2b)tf).getActualAmount());
//            System.out.println("Path: ");
//            System.out.print("{");
//            for(int p = 0; p < ((Network2b)tf).getNode_path().size(); p++) {
//                System.out.print(((Network2b)tf).getNode_path().get(p));
//                
//                if(p != ((Network2b)tf).getNode_path().size()-1){
//                    System.out.print(", ");
//                }
//                
//            }
//            System.out.println("}");
//            
//            System.out.println("Built paths: ");
//            System.out.print("{");
//            for(int p = 0; p < ((Network2b)tf).getBuilt_path().size(); p++) {
//                pa = ((Network2b)tf).getBuilt_path().get(p);
//                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
//                
//                if(p != ((Network2b)tf).getBuilt_path().size()-1){
//                    System.out.print(", ");
//                }
//                
//            }
//            System.out.println("}");
            
            
//            for(Individual ind : ((MCShaDE)shade).getBestHistory()){
//                i++;
//                if(ind.fitness < min){
//                    min = ind.fitness;
//                    best = i;
//                }
//            }
//            System.out.println("Best solution found in " + best + " CFE");
            
        }
        
        end_time = System.currentTimeMillis();

        long millis = end_time - start_time;
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;
        millis = millis % 1000;

        String time = String.format("%02d:%02d:%02d:%d", hour, minute, second, millis);
        
        System.out.println("=================================");
        System.out.println("Time: " + time);
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
    }
    
    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void mainNetwork2bDE(String[] args) throws Exception {
        int dimension = 20;
        int NP = 100;
        int MAXFES = 2000 * NP;
        double f = 0.5, cr = 0.8;
        
        TestFunction tf = new Network2b();

        util.random.Random generator = new util.random.UniformRandom();

        DErand1bin de;

        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        long start_time, end_time;
        
        start_time = System.currentTimeMillis();
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);

            de.runAlgorithm();
//            shade.printOutRankings();

//            PrintWriter writer;
//
//            try {
//                writer = new PrintWriter(tf.name() + "-mcshade" + k + ".txt", "UTF-8");
//
//                writer.print("{");
//
//                for (int z = 0; z < shade.getBestHistory().size(); z++) {
//
//                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(z).fitness));
//
//                    if (z != shade.getBestHistory().size() - 1) {
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
//                
//            }
            
            bestArray[k] = de.getBest().fitness;
//            System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
//            
//            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + de.getBest().fitness);
            tf.fitness(de.getBest());
            
            System.out.println("Amount: " + ((Network2b)tf).getActualAmount());
//            System.out.println("Path: ");
//            System.out.print("{");
//            for(int p = 0; p < ((Network2b)tf).getNode_path().size(); p++) {
//                System.out.print(((Network2b)tf).getNode_path().get(p));
//                
//                if(p != ((Network2b)tf).getNode_path().size()-1){
//                    System.out.print(", ");
//                }
//                
//            }
//            System.out.println("}");
//            
//            System.out.println("Built paths: ");
//            System.out.print("{");
//            for(int p = 0; p < ((Network2b)tf).getBuilt_path().size(); p++) {
//                pa = ((Network2b)tf).getBuilt_path().get(p);
//                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
//                
//                if(p != ((Network2b)tf).getBuilt_path().size()-1){
//                    System.out.print(", ");
//                }
//                
//            }
//            System.out.println("}");
            
            
//            for(Individual ind : ((MCShaDE)shade).getBestHistory()){
//                i++;
//                if(ind.fitness < min){
//                    min = ind.fitness;
//                    best = i;
//                }
//            }
//            System.out.println("Best solution found in " + best + " CFE");
            
        }
        
        end_time = System.currentTimeMillis();

        long millis = end_time - start_time;
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;
        millis = millis % 1000;

        String time = String.format("%02d:%02d:%02d:%d", hour, minute, second, millis);
        
        System.out.println("=================================");
        System.out.println("Time: " + time);
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
    }
    
    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void mainNetwork2cMCSHADE(String[] args) throws Exception {
        
        int noOfStartingPositions = 2;
        int noOfNodes = 20;
        int dimension = noOfNodes + noOfStartingPositions - 1;
        int NP = 100;
        int MAXFES = 2000 * NP;
        
        TestFunction tf = new Network2c();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Mc_SHADE shade;

        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        long start_time, end_time;
        
        start_time = System.currentTimeMillis();
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            shade = new Mc_SHADE(dimension, MAXFES, tf, H, NP, generator);

            shade.runAlgorithm();
//            shade.printOutRankings();

//            PrintWriter writer;
//
//            try {
//                writer = new PrintWriter(tf.name() + "-mcshade" + k + ".txt", "UTF-8");
//
//                writer.print("{");
//
//                for (int z = 0; z < shade.getBestHistory().size(); z++) {
//
//                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(z).fitness));
//
//                    if (z != shade.getBestHistory().size() - 1) {
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
//                
//            }
            
            bestArray[k] = shade.getBest().fitness;
            System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
            
            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + shade.getBest().fitness);
            tf.fitness(shade.getBest());

            System.out.println("Amount: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2c)tf).getActualAmount().size(); p++) {
                System.out.print(((Network2c)tf).getActualAmount().get(p));
                
                if(p != ((Network2c)tf).getActualAmount().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Path: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2c)tf).getNode_path().size(); p++) {
                System.out.print(((Network2c)tf).getNode_path().get(p));
                
                if(p != ((Network2c)tf).getNode_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Built paths: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2c)tf).getBuilt_path().size(); p++) {
                pa = ((Network2c)tf).getBuilt_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network2c)tf).getBuilt_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            
            for(Individual ind : ((Mc_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
        }
        
        end_time = System.currentTimeMillis();

        long millis = end_time - start_time;
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;
        millis = millis % 1000;

        String time = String.format("%02d:%02d:%02d:%d", hour, minute, second, millis);
        
        System.out.println("=================================");
        System.out.println("Time: " + time);
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
    }
    
    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void mainNetwork2cSHADE(String[] args) throws Exception {
        
        int noOfStartingPositions = 2;
        int noOfNodes = 20;
        int dimension = noOfNodes + noOfStartingPositions - 1;
        int NP = 100;
        int MAXFES = 2000 * NP;
        
        TestFunction tf = new Network2c();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        SHADE shade;

        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        long start_time, end_time;
        
        start_time = System.currentTimeMillis();
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            shade = new SHADE(dimension, MAXFES, tf, H, NP, generator);

            shade.runAlgorithm();
//            shade.printOutRankings();

//            PrintWriter writer;
//
//            try {
//                writer = new PrintWriter(tf.name() + "-mcshade" + k + ".txt", "UTF-8");
//
//                writer.print("{");
//
//                for (int z = 0; z < shade.getBestHistory().size(); z++) {
//
//                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(z).fitness));
//
//                    if (z != shade.getBestHistory().size() - 1) {
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
//                
//            }
            
            bestArray[k] = shade.getBest().fitness;
            System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
            
            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + shade.getBest().fitness);
            tf.fitness(shade.getBest());
            
            System.out.println("Amount: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2c)tf).getActualAmount().size(); p++) {
                System.out.print(((Network2c)tf).getActualAmount().get(p));
                
                if(p != ((Network2c)tf).getActualAmount().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Path: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2c)tf).getNode_path().size(); p++) {
                System.out.print(((Network2c)tf).getNode_path().get(p));
                
                if(p != ((Network2c)tf).getNode_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Built paths: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2c)tf).getBuilt_path().size(); p++) {
                pa = ((Network2c)tf).getBuilt_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network2c)tf).getBuilt_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            
            for(Individual ind : ((SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
        }
        
        end_time = System.currentTimeMillis();

        long millis = end_time - start_time;
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;
        millis = millis % 1000;

        String time = String.format("%02d:%02d:%02d:%d", hour, minute, second, millis);
        
        System.out.println("=================================");
        System.out.println("Time: " + time);
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
    }
    
    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void mainNetwork2cDE(String[] args) throws Exception {
       
        int noOfStartingPositions = 2;
        int noOfNodes = 20;
        int dimension = noOfNodes + noOfStartingPositions - 1;
        int NP = 100;
        int MAXFES = 2000 * NP;
        double f = 0.5, cr = 0.8;
        
        TestFunction tf = new Network2c();

        util.random.Random generator = new util.random.UniformRandom();

        DErand1bin de;

        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        long start_time, end_time;
        
        start_time = System.currentTimeMillis();
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);

            de.runAlgorithm();
//            shade.printOutRankings();

//            PrintWriter writer;
//
//            try {
//                writer = new PrintWriter(tf.name() + "-mcshade" + k + ".txt", "UTF-8");
//
//                writer.print("{");
//
//                for (int z = 0; z < shade.getBestHistory().size(); z++) {
//
//                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(z).fitness));
//
//                    if (z != shade.getBestHistory().size() - 1) {
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
//                
//            }
            
            bestArray[k] = de.getBest().fitness;
            System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
            
            System.out.println("Individual: " + Arrays.toString(de.getBest().vector));
            System.out.println("Profit: " + de.getBest().fitness);
            tf.fitness(de.getBest());
            
            System.out.println("Amount: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2c)tf).getActualAmount().size(); p++) {
                System.out.print(((Network2c)tf).getActualAmount().get(p));
                
                if(p != ((Network2c)tf).getActualAmount().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Path: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2c)tf).getNode_path().size(); p++) {
                System.out.print(((Network2c)tf).getNode_path().get(p));
                
                if(p != ((Network2c)tf).getNode_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Built paths: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2c)tf).getBuilt_path().size(); p++) {
                pa = ((Network2c)tf).getBuilt_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network2c)tf).getBuilt_path().size()-1){
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
            
        }
        
        end_time = System.currentTimeMillis();

        long millis = end_time - start_time;
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;
        millis = millis % 1000;

        String time = String.format("%02d:%02d:%02d:%d", hour, minute, second, millis);
        
        System.out.println("=================================");
        System.out.println("Time: " + time);
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void mainNetwork3(String[] args) throws Exception {
        int dimension = 50;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 14;
        TestFunction tf = new Network3();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Mc_SHADE shade;

        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            shade = new Mc_SHADE(dimension, MAXFES, tf, H, NP, generator);

            shade.runAlgorithm();
//            shade.printOutRankings();

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
            bestArray[k] = shade.getBest().fitness;
            
            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + shade.getBest().fitness);
            tf.fitness(shade.getBest());
            
            System.out.println("X: " + Arrays.toString(((Network4)tf).getX()));
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
            
            
            for(Individual ind : ((Mc_SHADE)shade).getBestHistory()){
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
    
    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void mainNetwork4(String[] args) throws Exception {
        int dimension = 100;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 14;
        TestFunction tf = new Network4();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Mc_SHADE shade;

        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            shade = new Mc_SHADE(dimension, MAXFES, tf, H, NP, generator);

            shade.runAlgorithm();
//            shade.printOutRankings();

            PrintWriter writer;

            try {
                writer = new PrintWriter(tf.name() + "-mcshade" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int z = 0; z < shade.getBestHistory().size(); z++) {

                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(z).fitness));

                    if (z != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                
            }
            bestArray[k] = shade.getBest().fitness;
            
            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + shade.getBest().fitness);
            tf.fitness(shade.getBest());
            
            System.out.println("X: " + Arrays.toString(((Network4)tf).getX()));
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
            
            
            for(Individual ind : ((Mc_SHADE)shade).getBestHistory()){
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
    
    /**
     * 
     * Main to solve spalovny in CR - 10 facilities
     * 
     * @throws Exception 
     */
    public static void spalovnyCR10main() throws Exception {
        
        int dimension = 206+2*20; //38
        int NP = 100;
        int MAXFES = 1000 * NP; //40000 * NP;
        int funcNumber = 14;
        TestFunction tf = new SpalovnyCR_10();
        int H = 10;
        util.random.Random generator;

        Mc_SHADE shade;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;
        double min;

        for (int k = 0; k < runs; k++) {
            
            generator = new util.random.UniformRandom();
            shade = new Mc_SHADE(dimension, MAXFES, tf, H, NP, generator);

            shade.runAlgorithm();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println(shade.getBest().fitness - tf.optimum());
            System.out.println(Arrays.toString(shade.getBest().vector));
            
            Map<String, List> map = ((SpalovnyCR_10)tf).getOutput(shade.getBest().vector);
            
            System.out.println("=================================");
            String line;
          
            for(Map.Entry<String,List> entry : map.entrySet()){
                line = "";
                System.out.print(entry.getKey() + " = ");
                line += "{";
//                System.out.print("{");
                for(int pup = 0; pup < entry.getValue().size(); pup++){
//                    System.out.print(entry.getValue().get(pup));
                    line += entry.getValue().get(pup);
                    if(pup != entry.getValue().size()-1){
//                       System.out.print(","); 
                       line += ",";
                    }
                }
//                System.out.println("}");
                line += "};";
                line = line.replace("[", "{");
                line = line.replace("]", "}");
                System.out.println(line);
                
            }
            
            System.out.println("=================================");
            
            for(Individual ind : ((Mc_SHADE)shade).getBestHistory()){
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
    
    /**
     * The number of runs to perform
     */
    static int runs = 50;
    
    /**
     * MAIN FOR TESTING
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
//        NWF_networkMains.mainNetwork2(args);
//        NWF_networkMains.mainNetwork2a(args);
//        NWF_networkMains.mainNetwork2b(args);
//        NWF_networkMains.mainNetwork2cDE(args);
        NWF_networkMains.mainNetwork2cSHADE(args);
//        NWF_networkMains.mainNetwork2cMCSHADE(args);
//        NWF_networkMains.mainNetwork2bDE(args);
//        NWF_networkMains.mainNetwork2bSHADE(args);
//        NWF_networkMains.mainNetwork3(args);     
//        NWF_networkMains.mainNetwork4(args);
        
    }
    
}
