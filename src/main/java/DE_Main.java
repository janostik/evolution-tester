
import algorithm.de.DErand1bin;
import algorithm.hybrid.SOF_DErand1bin_AP;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.tf.Ackley;
import model.tf.Dejong;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author wiki on 27/06/2016
 */
public class DE_Main {

    /**
     * Overall
     */
    public static int dimension = 10;
    public static int generations = 40;
    public static int runs = 51;
    public static String home_dir = "C:\\Users\\wiki\\Documents/";
    public static int NP = 20;
    public static double f = 0.5;
    public static double cr = 0.8;
    public static int MAXFES = generations * NP;
    
    /**
     * 
     * @param func
     * @param resultPath
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws Exception 
     */
    public static void getDErand1binResults(TestFunction func, String resultPath) throws Exception {
        
        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();

        DErand1bin de;

        double[] bestArray;
        PrintWriter writer, sol_writer;
        double best,worst,median,mean,std;
        
        tf = func;
        bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);
            de.runAlgorithm();

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
     * 
     * @param func
     * @param resultPath
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws Exception 
     */
    public static void getSOF_DErand1bin_APResults(TestFunction func, String resultPath) throws Exception {
        
        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();

        SOF_DErand1bin_AP de;

        double[] bestArray;
        PrintWriter writer, sol_writer;
        double best,worst,median,mean,std;
        
        tf = func;
        bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new SOF_DErand1bin_AP(dimension, NP, MAXFES, tf, generator);
            de.runAlgorithm();

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
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        String path;
        TestFunction[] tf_array = new TestFunction[]{new Dejong(), new Ackley(), new Schwefel()};
        TestFunction tf;
        
        dimension = 10;
        MAXFES = generations * NP;
        
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        System.out.println(new Date());
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        
        /**
         * 3 basic test functions
         */
        for(int i = 0; i <= 2; i++) {
            
            tf = tf_array[i];

            path = "ICNAAM_40G\\DErand1bin/";
            getDErand1binResults(tf, path);
            path = "ICNAAM_40G\\SOF_DErand1bin_AP/";
            getSOF_DErand1bin_APResults(tf, path);
            
            System.out.println("///////////////////////");
            System.out.println("///////////////////////");
            System.out.println(new Date());
            System.out.println("///////////////////////");
            System.out.println("///////////////////////");
            
        }
        
    }
    
}
