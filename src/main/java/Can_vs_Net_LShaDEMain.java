
import algorithm.de.LShaDE;
import algorithm.de.NetLShaDE;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.tf.Ackley;
import model.tf.Cec2015;
import model.tf.Dejong;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author wiki on 07/04/2016
 */
public class Can_vs_Net_LShaDEMain {

    /**
     * Overall
     */
    public static int dimension = 10;
    public static int FESmultiplier = 10000;
    public static int MAXFES = FESmultiplier * dimension;
    public static int runs = 51;
    public static final String home_dir = "C:\\Users\\wiki\\Documents/";
    
    /**
     * Algorithmic
     */
    public static final int NPinit = 100;
    public static final int NPfinal = 20;
    public static final int H = 10;
    
    /**
     * 
     * @param path
     * @param func
     * @throws Exception 
     */
    public static void lshadeMain(String path, TestFunction func) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        tf = func;
        
        LShaDE shade = new LShaDE(dimension, MAXFES, tf, H, NPinit, generator, NPfinal);

        double[] bestArray;
        PrintWriter writer, sol_writer;
        double best,worst,median,mean,std;

        bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new LShaDE(dimension, MAXFES, tf, H, NPinit, generator, NPfinal);
            shade.run();

            writer = new PrintWriter(home_dir + path + shade.getName() + "-" + func.name() + "-" + dimension + "_" + k + ".txt", "UTF-8");

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

        }

        best = DoubleStream.of(bestArray).min().getAsDouble();
        worst = DoubleStream.of(bestArray).max().getAsDouble();
        median = new Median().evaluate(bestArray);
        mean = new Mean().evaluate(bestArray);
        std = new StandardDeviation().evaluate(bestArray);

        sol_writer = new PrintWriter(home_dir + path + "results_" + shade.getName() + "-" + func.name() + "-" + dimension + ".txt", "UTF-8");

        sol_writer.print("{");
        sol_writer.print(func.name());
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
     * @param path
     * @param func
     * @throws Exception 
     */
    public static void netlshadeMain(String path, TestFunction func) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        tf = func;
        
        NetLShaDE shade = new NetLShaDE(dimension, MAXFES, tf, H, NPinit, generator, NPfinal);

        double[] bestArray;
        PrintWriter writer, sol_writer;
        double best,worst,median,mean,std;

        bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new NetLShaDE(dimension, MAXFES, tf, H, NPinit, generator, NPfinal);
            shade.run();

            writer = new PrintWriter(home_dir + path + shade.getName() + "-" + func.name() + "-" + dimension + "_" + k + ".txt", "UTF-8");

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

        }

        best = DoubleStream.of(bestArray).min().getAsDouble();
        worst = DoubleStream.of(bestArray).max().getAsDouble();
        median = new Median().evaluate(bestArray);
        mean = new Mean().evaluate(bestArray);
        std = new StandardDeviation().evaluate(bestArray);

        sol_writer = new PrintWriter(home_dir + path + "results_" + shade.getName() + "-" + func.name() + "-" + dimension + ".txt", "UTF-8");

        sol_writer.print("{");
        sol_writer.print(func.name());
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
     * @param path
     * @throws Exception 
     */
    public static void lshadeMainCec2015(String path) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        LShaDE shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new LShaDE(dimension, MAXFES, tf, H, NPinit, generator, NPfinal);
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
     * 
     * @param path
     * @param H
     * @throws Exception 
     */
    public static void netlshadeMainCec2015(String path) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        NetLShaDE shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new NetLShaDE(dimension, MAXFES, tf, H, NPinit, generator, NPfinal);
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
        
        String path;
        FESmultiplier = 100;
        runs = 2;
        TestFunction tf;
        
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        System.out.println(new Date());
        System.out.println("START");
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        
        /**
         * Elementary functions:
         * Dejong
         * Ackley
         * Schwefel
         */
        
        path = "L-SHADE_vs_Net-L-SHADE_elementary/";
        /**
         * Dejong
         */
        tf = new Dejong();
        dimension = 10;
        MAXFES = FESmultiplier * dimension;
        lshadeMain(path, tf);
        netlshadeMain(path, tf);
        
        dimension = 30;
        MAXFES = FESmultiplier * dimension;
        lshadeMain(path, tf);
        netlshadeMain(path, tf);
        
        dimension = 50;
        MAXFES = FESmultiplier * dimension;
        lshadeMain(path, tf);
        netlshadeMain(path, tf);
        
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        System.out.println(new Date());
        System.out.println("AFTER DEJONG");
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        
        /**
         * Ackley
         */
        tf = new Ackley();
        dimension = 10;
        MAXFES = FESmultiplier * dimension;
        lshadeMain(path, tf);
        netlshadeMain(path, tf);
        
        dimension = 30;
        MAXFES = FESmultiplier * dimension;
        lshadeMain(path, tf);
        netlshadeMain(path, tf);
        
        dimension = 50;
        MAXFES = FESmultiplier * dimension;
        lshadeMain(path, tf);
        netlshadeMain(path, tf);
        
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        System.out.println(new Date());
        System.out.println("AFTER ACKLEY");
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        
        /**
         * Schwefel
         */
        tf = new Schwefel();
        dimension = 10;
        MAXFES = FESmultiplier * dimension;
        lshadeMain(path, tf);
        netlshadeMain(path, tf);
        
        dimension = 30;
        MAXFES = FESmultiplier * dimension;
        lshadeMain(path, tf);
        netlshadeMain(path, tf);
        
        dimension = 50;
        MAXFES = FESmultiplier * dimension;
        lshadeMain(path, tf);
        netlshadeMain(path, tf);
        
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        System.out.println(new Date());
        System.out.println("AFTER SCHWEFEL");
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        
        /**
         * CEC 2015
         */
        
        /**
         * L-SHADE
         */
        dimension = 10;
        MAXFES = FESmultiplier * dimension;
        path = "CEC2015-Lshade-10/";
        lshadeMainCec2015(path);
        
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        System.out.println(new Date());
        System.out.println("AFTER L-SHADE 10 DIM");
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        
        dimension = 30;
        MAXFES = FESmultiplier * dimension;
        path = "CEC2015-Lshade-30/";
        lshadeMainCec2015(path);
        
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        System.out.println(new Date());
        System.out.println("AFTER L-SHADE 30 DIM");
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        
        /**
         * NET L-SHADE
         */
        dimension = 10;
        MAXFES = FESmultiplier * dimension;
        path = "CEC2015-netLshade-10/";
        netlshadeMainCec2015(path);
        
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        System.out.println(new Date());
        System.out.println("AFTER NET L-SHADE 10 DIM");
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        
        dimension = 30;
        MAXFES = FESmultiplier * dimension;
        path = "CEC2015-netLshade-30/";
        netlshadeMainCec2015(path);
        
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        System.out.println(new Date());
        System.out.println("AFTER NET L-SHADE 30 DIM");
        System.out.println("///////////////////////");
        System.out.println("///////////////////////");
        
    }
    
}
