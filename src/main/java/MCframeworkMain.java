
import algorithm.de.MCDEbest;
import algorithm.de.MCDEcurtopbest;
import algorithm.de.MCDErand;
import algorithm.de.MCShaDE;
import algorithm.de.ShaDE;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.tf.Ackley;
import model.tf.Cec2015;
import model.tf.Dejong;
import model.tf.Rastrigin;
import model.tf.Rosenbrock;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author wiki on 12/04/2016
 */
public class MCframeworkMain {
    
    /**
     * 
     * @param func
     * @param resultPath
     * @param probPath
     * @throws Exception 
     */
    public static void writeHistoryMCDEbest(TestFunction func, String resultPath, String probPath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();

        MCDEbest de;

        double[] bestArray;
        PrintWriter writer, sol_writer;
        double best,worst,median,mean,std;
        String prPath = "";
        
        tf = func;
        bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new MCDEbest(dimension, NP, MAXFES, tf, generator, f, cr);
            de.run();

            writer = new PrintWriter(home_dir + resultPath + tf.name() + "-" + k + ".txt", "UTF-8");

            writer.print("{");

            for (int i = 0; i < de.getBestHistory().size(); i++) {

                writer.print(String.format(Locale.US, "%.10f", de.getBestHistory().get(i).fitness));

                if (i != de.getBestHistory().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();

            bestArray[k] = de.getBest().fitness - tf.optimum();
            
            prPath = home_dir + probPath + "probs_" + tf.name() + "-" + k + ".txt";
            
            //writes chaos generator probabilities history into file
            de.writeProbsToFile(prPath);

        }

        best = DoubleStream.of(bestArray).min().getAsDouble();
        worst = DoubleStream.of(bestArray).max().getAsDouble();
        median = new Median().evaluate(bestArray);
        mean = new Mean().evaluate(bestArray);
        std = new StandardDeviation().evaluate(bestArray);

        sol_writer = new PrintWriter(home_dir + resultPath + "results_" + tf.name() + ".txt", "UTF-8");

        sol_writer.print("{");
        sol_writer.print(tf.name());
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", best));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", worst));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", median));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", mean));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", std));
        sol_writer.print("}");

        sol_writer.close();

        System.out.println(tf.name());
        System.out.println("=================================");
        System.out.println("Best: " + best);
        System.out.println("Worst: " + worst);
        System.out.println("Median: " + median);
        System.out.println("Mean: " + mean);
        System.out.println("Std: " + std);
        System.out.println("=================================");

    }
    
    /**
     * 
     * @param func
     * @param resultPath
     * @param probPath
     * @throws Exception 
     */
    public static void writeHistoryMCDErand1bin(TestFunction func, String resultPath, String probPath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();

        MCDErand de;

        double[] bestArray;
        PrintWriter writer, sol_writer;
        double best,worst,median,mean,std;
        String prPath = "";
        
        tf = func;
        bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new MCDErand(dimension, NP, MAXFES, tf, generator, f, cr);
            de.run();

            writer = new PrintWriter(home_dir + resultPath + tf.name() + "-" + k + ".txt", "UTF-8");

            writer.print("{");

            for (int i = 0; i < de.getBestHistory().size(); i++) {

                writer.print(String.format(Locale.US, "%.10f", de.getBestHistory().get(i).fitness));

                if (i != de.getBestHistory().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();

            bestArray[k] = de.getBest().fitness - tf.optimum();
            
            prPath = home_dir + probPath + "probs_" + tf.name() + "-" + k + ".txt";
            
            //writes chaos generator probabilities history into file
            de.writeProbsToFile(prPath);

        }

        best = DoubleStream.of(bestArray).min().getAsDouble();
        worst = DoubleStream.of(bestArray).max().getAsDouble();
        median = new Median().evaluate(bestArray);
        mean = new Mean().evaluate(bestArray);
        std = new StandardDeviation().evaluate(bestArray);

        sol_writer = new PrintWriter(home_dir + resultPath + "results_" + tf.name() + ".txt", "UTF-8");

        sol_writer.print("{");
        sol_writer.print(tf.name());
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", best));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", worst));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", median));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", mean));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", std));
        sol_writer.print("}");

        sol_writer.close();

        System.out.println(tf.name());
        System.out.println("=================================");
        System.out.println("Best: " + best);
        System.out.println("Worst: " + worst);
        System.out.println("Median: " + median);
        System.out.println("Mean: " + mean);
        System.out.println("Std: " + std);
        System.out.println("=================================");

    }
    
    /**
     * 
     * @param func
     * @param resultPath
     * @param probPath
     * @throws Exception 
     */
    public static void writeHistoryMCDEcurtopbest(TestFunction func, String resultPath, String probPath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();

        MCDEcurtopbest de;

        double[] bestArray;
        PrintWriter writer, sol_writer;
        double best,worst,median,mean,std;
        String prPath = "";
        
        tf = func;
        bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new MCDEcurtopbest(dimension, NP, MAXFES, tf, generator, f, cr);
            de.run();

            writer = new PrintWriter(home_dir + resultPath + tf.name() + "-" + k + ".txt", "UTF-8");

            writer.print("{");

            for (int i = 0; i < de.getBestHistory().size(); i++) {

                writer.print(String.format(Locale.US, "%.10f", de.getBestHistory().get(i).fitness));

                if (i != de.getBestHistory().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();

            bestArray[k] = de.getBest().fitness - tf.optimum();
            
            prPath = home_dir + probPath + "probs_" + tf.name() + "-" + k + ".txt";
            
            //writes chaos generator probabilities history into file
            de.writeProbsToFile(prPath);

        }

        best = DoubleStream.of(bestArray).min().getAsDouble();
        worst = DoubleStream.of(bestArray).max().getAsDouble();
        median = new Median().evaluate(bestArray);
        mean = new Mean().evaluate(bestArray);
        std = new StandardDeviation().evaluate(bestArray);

        sol_writer = new PrintWriter(home_dir + resultPath + "results_" + tf.name() + ".txt", "UTF-8");

        sol_writer.print("{");
        sol_writer.print(tf.name());
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", best));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", worst));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", median));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", mean));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", std));
        sol_writer.print("}");

        sol_writer.close();

        System.out.println(tf.name());
        System.out.println("=================================");
        System.out.println("Best: " + best);
        System.out.println("Worst: " + worst);
        System.out.println("Median: " + median);
        System.out.println("Mean: " + mean);
        System.out.println("Std: " + std);
        System.out.println("=================================");

    }
    
    /**
     * 
     * @param func
     * @param resultPath
     * @param probPath
     * @throws Exception 
     */
    public static void writeHistoryMCShade(TestFunction func, String resultPath, String probPath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();

        MCShaDE de;

        double[] bestArray;
        PrintWriter writer, sol_writer;
        double best,worst,median,mean,std;
        String prPath = "";
        
        tf = func;
        bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new MCShaDE(dimension, MAXFES, tf, H, NP, generator);
            de.run();

            writer = new PrintWriter(home_dir + resultPath + tf.name() + "-" + k + ".txt", "UTF-8");

            writer.print("{");

            for (int i = 0; i < de.getBestHistory().size(); i++) {

                writer.print(String.format(Locale.US, "%.10f", de.getBestHistory().get(i).fitness));

                if (i != de.getBestHistory().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();

            bestArray[k] = de.getBest().fitness - tf.optimum();
            
            prPath = home_dir + probPath + "probs_" + tf.name() + "-" + k + ".txt";
            
            //writes chaos generator probabilities history into file
            de.writeProbsToFile(prPath);

        }

        best = DoubleStream.of(bestArray).min().getAsDouble();
        worst = DoubleStream.of(bestArray).max().getAsDouble();
        median = new Median().evaluate(bestArray);
        mean = new Mean().evaluate(bestArray);
        std = new StandardDeviation().evaluate(bestArray);

        sol_writer = new PrintWriter(home_dir + resultPath + "results_" + tf.name() + ".txt", "UTF-8");

        sol_writer.print("{");
        sol_writer.print(tf.name());
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", best));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", worst));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", median));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", mean));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", std));
        sol_writer.print("}");

        sol_writer.close();

        System.out.println(tf.name());
        System.out.println("=================================");
        System.out.println("Best: " + best);
        System.out.println("Worst: " + worst);
        System.out.println("Median: " + median);
        System.out.println("Mean: " + mean);
        System.out.println("Std: " + std);
        System.out.println("=================================");

    }
    
    /**
     * 
     * @param func
     * @param resultPath
     * @param probPath
     * @throws Exception 
     */
    public static void writeHistoryShade(TestFunction func, String resultPath, String probPath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();

        ShaDE de;

        double[] bestArray;
        PrintWriter writer, sol_writer;
        double best,worst,median,mean,std;
        String prPath = "";
        
        tf = func;
        bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new ShaDE(dimension, MAXFES, tf, H, NP, generator);
            de.run();

            writer = new PrintWriter(home_dir + resultPath + tf.name() + "-" + k + ".txt", "UTF-8");

            writer.print("{");

            for (int i = 0; i < de.getBestHistory().size(); i++) {

                writer.print(String.format(Locale.US, "%.10f", de.getBestHistory().get(i).fitness));

                if (i != de.getBestHistory().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();

            bestArray[k] = de.getBest().fitness - tf.optimum();

        }

        best = DoubleStream.of(bestArray).min().getAsDouble();
        worst = DoubleStream.of(bestArray).max().getAsDouble();
        median = new Median().evaluate(bestArray);
        mean = new Mean().evaluate(bestArray);
        std = new StandardDeviation().evaluate(bestArray);

        sol_writer = new PrintWriter(home_dir + resultPath + "results_" + tf.name() + ".txt", "UTF-8");

        sol_writer.print("{");
        sol_writer.print(tf.name());
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", best));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", worst));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", median));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", mean));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", std));
        sol_writer.print("}");

        sol_writer.close();

        System.out.println(tf.name());
        System.out.println("=================================");
        System.out.println("Best: " + best);
        System.out.println("Worst: " + worst);
        System.out.println("Median: " + median);
        System.out.println("Mean: " + mean);
        System.out.println("Std: " + std);
        System.out.println("=================================");

    }
    
    /**
     * Overall
     */
    public static int dimension = 10;
    public static int FESmultiplier = 10000;
    public static int MAXFES = FESmultiplier * dimension;
    public static int runs = 51;
    public static String home_dir = "C:\\Users\\wiki\\Documents/";
    
    /**
     * DE
     */
    public static int NP = 100;
    public static double f = 0.5;
    public static double cr = 0.8;
    public static int H = 10;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        String path;
        TestFunction tf;
        
        /**
         * MC with DE rand1bin DE best DE current to pbest 1 and Shade
         */
        dimension = 10;
        MAXFES = FESmultiplier * dimension;
        
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        System.out.println(new Date());
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        
        /**
         * CEC 2015
         */
        for(int i = 1; i <= 15; i++) {
            
            tf = new Cec2015(dimension, i);
            
            path = "PPSN\\CEC2015-MC-DEbest_10/";
            writeHistoryMCDEbest(tf, path, path);
            path = "PPSN\\CEC2015-MC-DErand1bin_10/";
            writeHistoryMCDErand1bin(tf, path, path);
            path = "PPSN\\CEC2015-MC-DEcurtopbest_10/";
            writeHistoryMCDEcurtopbest(tf, path, path);
            path = "PPSN\\CEC2015-MC-Shade_10/";
            writeHistoryMCShade(tf, path, path);
            
            System.out.println("///////////////////////");
            System.out.println("///////////////////////");
            System.out.println(new Date());
            System.out.println("///////////////////////");
            System.out.println("///////////////////////");
            
        }

//        tf = new Ackley();
////        path = "MC_DEbest_10/";
////        writeHistoryMCDEbest(tf, path, path);
////        path = "MC_DErand_10/";
////        writeHistoryMCDErand1bin(tf, path, path);
////        path = "MC_DEcurtopbest_10/";
////        writeHistoryMCDEcurtopbest(tf, path, path);
////        path = "MC2_Shade_10/";
////        writeHistoryMCShade(tf, path, path);
//        path = "MC_Shade_10/";
//        writeHistoryMCShade(tf, path, path);
//        path = "CAN_Shade_10/";
//        writeHistoryShade(tf, path, path);
//        
//        System.out.println("///////////////////////");
//        System.out.println("///////////////////////");
//        System.out.println(new Date());
//        System.out.println("///////////////////////");
//        System.out.println("///////////////////////");
//        
//        tf = new Schwefel();
////        path = "MC_DEbest_10/";
////        writeHistoryMCDEbest(tf, path, path);
////        path = "MC_DErand_10/";
////        writeHistoryMCDErand1bin(tf, path, path);
////        path = "MC_DEcurtopbest_10/";
////        writeHistoryMCDEcurtopbest(tf, path, path);
////        path = "MC2_Shade_10/";
////        writeHistoryMCShade(tf, path, path);
//        path = "MC_Shade_10/";
//        writeHistoryMCShade(tf, path, path);
//        path = "CAN_Shade_10/";
//        writeHistoryShade(tf, path, path);
//        
//        System.out.println("///////////////////////");
//        System.out.println("///////////////////////");
//        System.out.println(new Date());
//        System.out.println("///////////////////////");
//        System.out.println("///////////////////////");
//        
//        tf = new Dejong();
////        path = "MC_DEbest_10/";
////        writeHistoryMCDEbest(tf, path, path);
////        path = "MC_DErand_10/";
////        writeHistoryMCDErand1bin(tf, path, path);
////        path = "MC_DEcurtopbest_10/";
////        writeHistoryMCDEcurtopbest(tf, path, path);
////        path = "MC2_Shade_10/";
////        writeHistoryMCShade(tf, path, path);
//        path = "MC_Shade_10/";
//        writeHistoryMCShade(tf, path, path);
//        path = "CAN_Shade_10/";
//        writeHistoryShade(tf, path, path);
//        
//        System.out.println("///////////////////////");
//        System.out.println("///////////////////////");
//        System.out.println(new Date());
//        System.out.println("///////////////////////");
//        System.out.println("///////////////////////");
//        
//        tf = new Rosenbrock();
//        path = "MC_DEbest_10/";
//        writeHistoryMCDEbest(tf, path, path);
//        path = "MC_DErand_10/";
//        writeHistoryMCDErand1bin(tf, path, path);
//        path = "MC_DEcurtopbest_10/";
//        writeHistoryMCDEcurtopbest(tf, path, path);
////        path = "MC2_Shade_10/";
////        writeHistoryMCShade(tf, path, path);
//        path = "MC_Shade_10/";
//        writeHistoryMCShade(tf, path, path);
//        path = "CAN_Shade_10/";
//        writeHistoryShade(tf, path, path);
//        
//        System.out.println("///////////////////////");
//        System.out.println("///////////////////////");
//        System.out.println(new Date());
//        System.out.println("///////////////////////");
//        System.out.println("///////////////////////");
//        
//        tf = new Rastrigin();
//        path = "MC_DEbest_10/";
//        writeHistoryMCDEbest(tf, path, path);
//        path = "MC_DErand_10/";
//        writeHistoryMCDErand1bin(tf, path, path);
//        path = "MC_DEcurtopbest_10/";
//        writeHistoryMCDEcurtopbest(tf, path, path);
////        path = "MC2_Shade_10/";
////        writeHistoryMCShade(tf, path, path);
//        path = "MC_Shade_10/";
//        writeHistoryMCShade(tf, path, path);
//        path = "CAN_Shade_10/";
//        writeHistoryShade(tf, path, path);
//        
//        System.out.println("///////////////////////");
//        System.out.println("///////////////////////");
//        System.out.println(new Date());
//        System.out.println("///////////////////////");
//        System.out.println("///////////////////////");
        
    }
    
}
