import algorithm.de.CDEbest;
import algorithm.de.CDErand1bin;
import algorithm.de.C_SHADE;
import algorithm.de.CjDE;
import algorithm.de.DE_hbps;
import algorithm.de.DE_hbrs;
import algorithm.de.DEbest;
import algorithm.de.DErand1bin;
import algorithm.de.FCRa_DE_hbps;
import algorithm.de.SHADE;
import algorithm.de.jDE;
import algorithm.de.uDE;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author wiki on 12/07/2016
 */
public class CEC2015main {

    /**
     * Overall
     */
    public static int dimension = 10;
    public static int MAXFES = 10000 * dimension;
    public static final int runs = 51;
    public static final String home_dir = "Z:\\zaloha_exterak\\results\\ROMAN_CHAOS\\30D\\";
    
    /**
     * PSO
     */
    private static final int swarmSize = 40;
    private static final double c1 = 2;
    private static final double c2 = 2;
    private static final double maxVelocity = 0.2;
    
    /**
     * NetPSO
     */
    private static final int degreeLimit = 40;//20
    private static final int repulsiveRoundsLimit = 20;//20
    private static final int maxRepulsionIteration = 2400;
    
    /**
     * DE
     */
    public static final int NP = 50;
    public static final int NPinit = 50;
    public static final int NPfinal = 20;
    public static final int H = 10;
    public static final double f = 0.5;
    public static final double cr = 0.8;
    
    public static void DErand1bin_main(String path) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        DErand1bin de;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                de = new DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);
                de.run();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

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
                
                de.writePopDiversityHistory(home_dir + path + "PopDiv" + funcNumber + "-" + k + ".txt");

            }
            
            best = DoubleStream.of(bestArray).min().getAsDouble();
            worst = DoubleStream.of(bestArray).max().getAsDouble();
            median = new Median().evaluate(bestArray);
            mean = new Mean().evaluate(bestArray);
            std = new StandardDeviation().evaluate(bestArray);

            sol_writer = new PrintWriter(home_dir + path + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
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
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
    public static void CDErand1bin_main(String path, util.random.Random chGenerator) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        CDErand1bin de;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                de = new CDErand1bin(dimension, NP, MAXFES, tf, generator, f, cr, chGenerator);
                de.run();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

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
                
                de.writePopDiversityHistory(home_dir + path + "PopDiv" + funcNumber + "-" + k + ".txt");

            }
            
            best = DoubleStream.of(bestArray).min().getAsDouble();
            worst = DoubleStream.of(bestArray).max().getAsDouble();
            median = new Median().evaluate(bestArray);
            mean = new Mean().evaluate(bestArray);
            std = new StandardDeviation().evaluate(bestArray);

            sol_writer = new PrintWriter(home_dir + path + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
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
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
    public static void DEbest_main(String path) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        DEbest de;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                de = new DEbest(dimension, NP, MAXFES, tf, generator, f, cr);
                de.run();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

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
                
                de.writePopDiversityHistory(home_dir + path + "PopDiv" + funcNumber + "-" + k + ".txt");

            }
            
            best = DoubleStream.of(bestArray).min().getAsDouble();
            worst = DoubleStream.of(bestArray).max().getAsDouble();
            median = new Median().evaluate(bestArray);
            mean = new Mean().evaluate(bestArray);
            std = new StandardDeviation().evaluate(bestArray);

            sol_writer = new PrintWriter(home_dir + path + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
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
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
    public static void CDEbest_main(String path, util.random.Random chGenerator) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        CDEbest de;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                de = new CDEbest(dimension, NP, MAXFES, tf, generator, f, cr, chGenerator);
                de.run();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

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
                
                de.writePopDiversityHistory(home_dir + path + "PopDiv" + funcNumber + "-" + k + ".txt");

            }
            
            best = DoubleStream.of(bestArray).min().getAsDouble();
            worst = DoubleStream.of(bestArray).max().getAsDouble();
            median = new Median().evaluate(bestArray);
            mean = new Mean().evaluate(bestArray);
            std = new StandardDeviation().evaluate(bestArray);

            sol_writer = new PrintWriter(home_dir + path + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
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
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
    public static void jDE_main(String path) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        jDE de;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                de = new jDE(dimension, NP, MAXFES, tf, generator);
                de.run();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

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
                
                de.writePopDiversityHistory(home_dir + path + "PopDiv" + funcNumber + "-" + k + ".txt");

            }
            
            best = DoubleStream.of(bestArray).min().getAsDouble();
            worst = DoubleStream.of(bestArray).max().getAsDouble();
            median = new Median().evaluate(bestArray);
            mean = new Mean().evaluate(bestArray);
            std = new StandardDeviation().evaluate(bestArray);

            sol_writer = new PrintWriter(home_dir + path + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
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
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
    public static void CjDE_main(String path, util.random.Random chGenerator) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        CjDE de;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                de = new CjDE(dimension, NP, MAXFES, tf, generator, chGenerator);
                de.run();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

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
                
                de.writePopDiversityHistory(home_dir + path + "PopDiv" + funcNumber + "-" + k + ".txt");

            }
            
            best = DoubleStream.of(bestArray).min().getAsDouble();
            worst = DoubleStream.of(bestArray).max().getAsDouble();
            median = new Median().evaluate(bestArray);
            mean = new Mean().evaluate(bestArray);
            std = new StandardDeviation().evaluate(bestArray);

            sol_writer = new PrintWriter(home_dir + path + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
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
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
    public static void SHADE_main(String path, int H) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        SHADE shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new SHADE(dimension, MAXFES, tf, H, NP, generator);
                shade.run();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writePopDiversityHistory(home_dir + path + "PopDiv" + funcNumber + "-" + k + ".txt");

            }
            
            best = DoubleStream.of(bestArray).min().getAsDouble();
            worst = DoubleStream.of(bestArray).max().getAsDouble();
            median = new Median().evaluate(bestArray);
            mean = new Mean().evaluate(bestArray);
            std = new StandardDeviation().evaluate(bestArray);

            sol_writer = new PrintWriter(home_dir + path + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
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
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
    public static void CSHADE_main(String path, int H, util.random.Random chGenerator) throws Exception{
 
        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        C_SHADE shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new C_SHADE(dimension, MAXFES, tf, H, NP, generator, chGenerator);
                shade.run();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writePopDiversityHistory(home_dir + path + "PopDiv" + funcNumber + "-" + k + ".txt");

            }
            
            best = DoubleStream.of(bestArray).min().getAsDouble();
            worst = DoubleStream.of(bestArray).max().getAsDouble();
            median = new Median().evaluate(bestArray);
            mean = new Mean().evaluate(bestArray);
            std = new StandardDeviation().evaluate(bestArray);

            sol_writer = new PrintWriter(home_dir + path + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
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
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
    public static void uDE_main(String path) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        uDE de;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                de = new uDE(dimension, NP, MAXFES, tf, generator);
                de.run();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

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

            sol_writer = new PrintWriter(home_dir + path + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
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
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
    public static void DE_hbrs_main(String path, int favor, int punish) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        DE_hbrs de;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                de = new DE_hbrs(dimension, NP, MAXFES, tf, generator, f, cr, favor, punish);
                de.run();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

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

            sol_writer = new PrintWriter(home_dir + path + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
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
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
    public static void DE_hbps_main(String path, int favor, int punish) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        DE_hbps de;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                de = new DE_hbps(dimension, NP, MAXFES, tf, generator, f, cr, favor, punish);
                de.run();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

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

            sol_writer = new PrintWriter(home_dir + path + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
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
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
    public static void FCRa_DE_hbps_main(String path, int favor, int punish) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        FCRa_DE_hbps de;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                de = new FCRa_DE_hbps(dimension, NP, MAXFES, tf, generator, H, favor, punish);
                de.run();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

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

            sol_writer = new PrintWriter(home_dir + path + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
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
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        String path, chaosName;
        util.random.Random chGenerator;
        dimension = 50;
        MAXFES = 10000 * dimension;

        /**
         * CLASSIC algorithms
         */
        
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("Classic algorithms");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
        
//        /**
//         * DE rand/1/bin
//         */
//        path = "DErand/";
//        DErand1bin_main(path);
//        
//        /**
//         * DE best
//         */
//        path = "DEbest/";
//        DEbest_main(path);
//        
        /**
         * jDE
         */
        path = "jDE/";
        jDE_main(path);
//        
//        /**
//         * SHADE
//         */
//        path = "SHADE/";
//        SHADE_main(path, H);
//
//        /**
//         * Arnold
//         */
//        
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("Arnold cat chaos");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
//        
//        /**
//         * DE rand/1/bin
//         */
        chaosName = "Arnold/";
//        chGenerator = new util.random.ArnoldCatRandom();
//        path = "CDErand/"+chaosName;
//        CDErand1bin_main(path, chGenerator);
//        
//        /**
//         * DE best
//         */
//        chGenerator = new util.random.ArnoldCatRandom();
//        path = "CDEbest/"+chaosName;
//        CDEbest_main(path, chGenerator);
//        
        /**
         * jDE
         */
        chGenerator = new util.random.ArnoldCatRandom();
        path = "CjDE/"+chaosName;
        CjDE_main(path, chGenerator);
//        
//        /**
//         * SHADE
//         */
//        chGenerator = new util.random.ArnoldCatRandom();
//        path = "CSHADE/"+chaosName;
//        CSHADE_main(path, H, chGenerator);
//        
//        /**
//         * Burgers
//         */
//        
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("Burgers chaos");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
//        
//        /**
//         * DE rand/1/bin
//         */
        chaosName = "Burgers/";
//        chGenerator = new util.random.BurgersRandom();
//        path = "CDErand/"+chaosName;
//        CDErand1bin_main(path, chGenerator);
//        
//        /**
//         * DE best
//         */
//        chGenerator = new util.random.BurgersRandom();
//        path = "CDEbest/"+chaosName;
//        CDEbest_main(path, chGenerator);
//        
        /**
         * jDE
         */
        chGenerator = new util.random.BurgersRandom();
        path = "CjDE/"+chaosName;
        CjDE_main(path, chGenerator);
//        
//        /**
//         * SHADE
//         */
//        chGenerator = new util.random.BurgersRandom();
//        path = "CSHADE/"+chaosName;
//        CSHADE_main(path, H, chGenerator);
//        
//        /**
//         * DeLo
//         */
//        
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("Delayed Logistic chaos");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
//        
//        /**
//         * DE rand/1/bin
//         */
        chaosName = "DeLo/";
//        chGenerator = new util.random.DelayedLogisticRandom();
//        path = "CDErand/"+chaosName;
//        CDErand1bin_main(path, chGenerator);
//        
//        /**
//         * DE best
//         */
//        chGenerator = new util.random.DelayedLogisticRandom();
//        path = "CDEbest/"+chaosName;
//        CDEbest_main(path, chGenerator);
//        
        /**
         * jDE
         */
        chGenerator = new util.random.DelayedLogisticRandom();
        path = "CjDE/"+chaosName;
        CjDE_main(path, chGenerator);
//        
//        /**
//         * SHADE
//         */
//        chGenerator = new util.random.DelayedLogisticRandom();
//        path = "CSHADE/"+chaosName;
//        CSHADE_main(path, H, chGenerator);
//        
//        /**
//         * Dissipative
//         */
//        
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("Dissipative chaos");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
//        
//        /**
//         * DE rand/1/bin
//         */
        chaosName = "Dissipative/";
//        chGenerator = new util.random.DissipativeRandom();
//        path = "CDErand/"+chaosName;
//        CDErand1bin_main(path, chGenerator);
//        
//        /**
//         * DE best
//         */
//        chGenerator = new util.random.DissipativeRandom();
//        path = "CDEbest/"+chaosName;
//        CDEbest_main(path, chGenerator);
//        
        /**
         * jDE
         */
        chGenerator = new util.random.DissipativeRandom();
        path = "CjDE/"+chaosName;
        CjDE_main(path, chGenerator);
//        
//        /**
//         * SHADE
//         */
//        chGenerator = new util.random.DissipativeRandom();
//        path = "CSHADE/"+chaosName;
//        CSHADE_main(path, H, chGenerator);
//        
//        /**
//         * Henon
//         */
//        
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("Henon chaos");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
//        
//        /**
//         * DE rand/1/bin
//         */
        chaosName = "Henon/";
//        chGenerator = new util.random.HenonRandom();
//        path = "CDErand/"+chaosName;
//        CDErand1bin_main(path, chGenerator);
//        
//        /**
//         * DE best
//         */
//        chGenerator = new util.random.HenonRandom();
//        path = "CDEbest/"+chaosName;
//        CDEbest_main(path, chGenerator);
//        
        /**
         * jDE
         */
        chGenerator = new util.random.HenonRandom();
        path = "CjDE/"+chaosName;
        CjDE_main(path, chGenerator);
//        
//        /**
//         * SHADE
//         */
//        chGenerator = new util.random.HenonRandom();
//        path = "CSHADE/"+chaosName;
//        CSHADE_main(path, H, chGenerator);
//        
//        /**
//         * Ikeda
//         */
//        
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("Ikeda chaos");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
//        
//        /**
//         * DE rand/1/bin
//         */
        chaosName = "Ikeda/";
//        chGenerator = new util.random.IkedaRandom();
//        path = "CDErand/"+chaosName;
//        CDErand1bin_main(path, chGenerator);
//        
//        /**
//         * DE best
//         */
//        chGenerator = new util.random.IkedaRandom();
//        path = "CDEbest/"+chaosName;
//        CDEbest_main(path, chGenerator);
//        
        /**
         * jDE
         */
        chGenerator = new util.random.IkedaRandom();
        path = "CjDE/"+chaosName;
        CjDE_main(path, chGenerator);
//        
//        /**
//         * SHADE
//         */
//        chGenerator = new util.random.IkedaRandom();
//        path = "CSHADE/"+chaosName;
//        CSHADE_main(path, H, chGenerator);
//        
//        /**
//         * Lozi
//         */
//        
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("Lozi chaos");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
//        
//        /**
//         * DE rand/1/bin
//         */
        chaosName = "Lozi/";
//        chGenerator = new util.random.LoziRandom();
//        path = "CDErand/"+chaosName;
//        CDErand1bin_main(path, chGenerator);
//        
//        /**
//         * DE best
//         */
//        chGenerator = new util.random.LoziRandom();
//        path = "CDEbest/"+chaosName;
//        CDEbest_main(path, chGenerator);
//        
        /**
         * jDE
         */
        chGenerator = new util.random.LoziRandom();
        path = "CjDE/"+chaosName;
        CjDE_main(path, chGenerator);
//        
//        /**
//         * SHADE
//         */
//        chGenerator = new util.random.LoziRandom();
//        path = "CSHADE/"+chaosName;
//        CSHADE_main(path, H, chGenerator);
//        
//        /**
//         * Sinai
//         */
//        
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("Sinai chaos");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
//        
//        /**
//         * DE rand/1/bin
//         */
        chaosName = "Sinai/";
//        chGenerator = new util.random.SinaiRandom();
//        path = "CDErand/"+chaosName;
//        CDErand1bin_main(path, chGenerator);
//        
//        /**
//         * DE best
//         */
//        chGenerator = new util.random.SinaiRandom();
//        path = "CDEbest/"+chaosName;
//        CDEbest_main(path, chGenerator);
//        
        /**
         * jDE
         */
        chGenerator = new util.random.SinaiRandom();
        path = "CjDE/"+chaosName;
        CjDE_main(path, chGenerator);
//        
//        /**
//         * SHADE
//         */
//        chGenerator = new util.random.SinaiRandom();
//        path = "CSHADE/"+chaosName;
//        CSHADE_main(path, H, chGenerator);
//        
//        /**
//         * Tinkerbell
//         */
//        
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("Tinkerbell chaos");
        System.out.println("=================================");
        System.out.println("=================================");
        System.out.println("=================================");
//        
//        /**
//         * DE rand/1/bin
//         */
        chaosName = "Tinkerbell/";
//        chGenerator = new util.random.TinkerbellRandom();
//        path = "CDErand/"+chaosName;
//        CDErand1bin_main(path, chGenerator);
//        
//        /**
//         * DE best
//         */
//        chGenerator = new util.random.TinkerbellRandom();
//        path = "CDEbest/"+chaosName;
//        CDEbest_main(path, chGenerator);
//        
        /**
         * jDE
         */
        chGenerator = new util.random.TinkerbellRandom();
        path = "CjDE/"+chaosName;
        CjDE_main(path, chGenerator);
//        
//        /**
//         * SHADE
//         */
//        chGenerator = new util.random.TinkerbellRandom();
//        path = "CSHADE/"+chaosName;
//        CSHADE_main(path, H, chGenerator);
    }
    
}
