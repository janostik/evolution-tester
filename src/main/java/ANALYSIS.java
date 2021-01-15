import algorithm.Algorithm;
import algorithm.de.ACID;
import algorithm.de.CmDbL_SHADE;
import algorithm.de.CmDb_SHADE;
import algorithm.de.DISH;
import algorithm.de.DISH_PRT_analysis;
import algorithm.de.DISH_XXCR;
import algorithm.de.DISH_adaptive_mask;
import algorithm.de.DISH_analysis;
import algorithm.de.DISH_mask_test;
import algorithm.de.DISHaXover;
import algorithm.de.DISH_XX;
import algorithm.de.DISH_XXL;
import algorithm.de.DISH_randXX;
import algorithm.de.DISHsec;
import algorithm.de.DISHsecBinX;
import algorithm.de.DISHsuperXover;
import algorithm.de.DISHswitchBinX;
import algorithm.de.DISHv2_analysis;
import algorithm.de.DISHxover1v1;
import algorithm.de.DIbL_SHADE_analysis;
import algorithm.de.DIb_SHADE_analysis;
import algorithm.de.DbL_SHADE_analysis;
import algorithm.de.Db_SHADE_analysis;
import algorithm.de.Db_jSO_analysis;
import algorithm.de.EB_DISH_analysis;
import algorithm.de.IDISH_analysis;
import algorithm.de.L_SHADE_analysis;
import algorithm.de.SHADE_analysis;
import algorithm.de.UDb_jSO_analysis;
import algorithm.de.halfDISH_analysis;
import algorithm.de.jSO_analysis;
import algorithm.de.liteSHADE2_analysis;
import algorithm.de.liteSHADE_analysis;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;
import model.tf.Cec2013;
import model.tf.Cec2014;
import model.tf.Cec2015;
import model.tf.Cec2017;
import model.tf.Cec2020;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;


/**
 * Serves as a main class for analysis
 * @author wiki on 18/12/2017
 */
public class ANALYSIS {

    /**
     * Main class for SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void shadeCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        SHADE_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new SHADE_analysis(dimension, MAXFES, tf, H, NP, generator);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void lshadeCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        L_SHADE_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new L_SHADE_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for DIb_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @param wDis
     * @param wImp
     * @throws Exception 
     */
    public static void DIBshadeCEC2015(String path, int H, String mfpath, int wDis, int wImp) throws Exception {

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        DIb_SHADE_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new DIb_SHADE_analysis(dimension, MAXFES, tf, H, NP, generator, wDis, wImp);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for CmDb_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void CmDBshadeCEC2015(String path, int H, String mfpath) throws Exception {

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        CmDb_SHADE shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new CmDb_SHADE(dimension, MAXFES, tf, H, NP, generator);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for Db_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DBshadeCEC2013(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 28;

        Db_SHADE_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2013(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new Db_SHADE_analysis(dimension, MAXFES, tf, H, NP, generator);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for Db_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DBshadeCEC2014(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 28;

        Db_SHADE_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2014(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new Db_SHADE_analysis(dimension, MAXFES, tf, H, NP, generator);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for Db_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DBshadeCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        Db_SHADE_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new Db_SHADE_analysis(dimension, MAXFES, tf, H, NP, generator);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for DIbL_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @param wDis
     * @param wImp
     * @throws Exception 
     */
    public static void DIBlshadeCEC2015(String path, int H, String mfpath, int wDis, int wImp) throws Exception {

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        DIbL_SHADE_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new DIbL_SHADE_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal, wDis, wImp);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for CmDb_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void CmDBlshadeCEC2015(String path, int H, String mfpath) throws Exception {

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        CmDbL_SHADE shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new CmDbL_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for Db_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DBlshadeCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        DbL_SHADE_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new DbL_SHADE_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for Db_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DBlshadeCEC2013(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 28;

        DbL_SHADE_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2013(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new DbL_SHADE_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for Db_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DBlshadeCEC2014(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 28;

        DbL_SHADE_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2014(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new DbL_SHADE_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for Db_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void jSOCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        jSO_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new jSO_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for UDb_jSO algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void UDBjSOCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        UDb_jSO_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new UDb_jSO_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for DISHv2 algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DISHv2CEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        DISHv2_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new DISHv2_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for halfDISH algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void halfDISHCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        halfDISH_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){

            System.out.println("START: " + new Date());
            
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new halfDISH_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getImp_hist().size(); i++) {

                    writer.print("{" + String.format(Locale.US, "%d", (int)shade.getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", shade.getImp_hist().get(i)[1]) + "}");

                    if (i != shade.getImp_hist().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                System.out.println((k+1) + ". run FES: " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " OFV: " + (shade.getImp_hist().get(shade.getImp_hist().size()-1)[1] - tf.optimum()));
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            System.out.println("END: " + new Date());
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for IDISH algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void IDISHCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        IDISH_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){

            System.out.println("START: " + new Date());
            
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new IDISH_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getImp_hist().size(); i++) {

                    writer.print("{" + String.format(Locale.US, "%d", (int)shade.getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", shade.getImp_hist().get(i)[1]) + "}");

                    if (i != shade.getImp_hist().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                System.out.println((k+1) + ". run FES: " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " OFV: " + (shade.getImp_hist().get(shade.getImp_hist().size()-1)[1] - tf.optimum()));
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            System.out.println("END: " + new Date());
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for ACID algorithm
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void ACIDCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        ACID shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;
        
        double eps;
        double resolution = Math.pow(10, -8);

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){

            System.out.println("START: " + new Date());
            
            tf = new Cec2015(dimension, funcNumber);
            eps = Math.sqrt(dimension * (Math.pow((tf.max(0)-tf.min(0)),2)/100.0));
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new ACID(dimension, MAXFES, tf, H, NP, generator, NPfinal, eps, 3, new org.apache.commons.math3.ml.distance.EuclideanDistance(), resolution);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getImp_hist().size(); i++) {

                    writer.print("{" + String.format(Locale.US, "%d", (int)shade.getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", shade.getImp_hist().get(i)[1]) + "}");

                    if (i != shade.getImp_hist().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                System.out.println((k+1) + ". run FES: " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " OFV: " + (shade.getImp_hist().get(shade.getImp_hist().size()-1)[1] - tf.optimum()));
                
//                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
//                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
//                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
//                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            System.out.println("END: " + new Date());
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for DISH algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DISHCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        DISH_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){

            System.out.println("START: " + new Date());
            
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new DISH_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getImp_hist().size(); i++) {

                    writer.print("{" + String.format(Locale.US, "%d", (int)shade.getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", shade.getImp_hist().get(i)[1]) + "}");

                    if (i != shade.getImp_hist().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                System.out.println((k+1) + ". run FES: " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " OFV: " + (shade.getImp_hist().get(shade.getImp_hist().size()-1)[1] - tf.optimum()));
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            System.out.println("END: " + new Date());
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for DISH algorithm with mask analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DISHadaptiveMaskCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        DISH_adaptive_mask shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){

            System.out.println("START: " + new Date());
            
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new DISH_adaptive_mask(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getImp_hist().size(); i++) {

                    writer.print("{" + String.format(Locale.US, "%d", (int)shade.getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", shade.getImp_hist().get(i)[1]) + "}");

                    if (i != shade.getImp_hist().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                System.out.println((k+1) + ". run FES: " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " OFV: " + (shade.getImp_hist().get(shade.getImp_hist().size()-1)[1] - tf.optimum()));

                /**
                 * Mask writing to files
                 */
                shade.printMaskHistory(home_dir + path, funcNumber, k);
                
            }
            
            System.out.println("END: " + new Date());
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for DISH algorithm with mask analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DISHmaskCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        DISH_mask_test shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){

            System.out.println("START: " + new Date());
            
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new DISH_mask_test(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getImp_hist().size(); i++) {

                    writer.print("{" + String.format(Locale.US, "%d", (int)shade.getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", shade.getImp_hist().get(i)[1]) + "}");

                    if (i != shade.getImp_hist().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                System.out.println((k+1) + ". run FES: " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " OFV: " + (shade.getImp_hist().get(shade.getImp_hist().size()-1)[1] - tf.optimum()));

                /**
                 * Mask writing to files
                 */
                shade.printMaskHistory(home_dir + path, funcNumber, k);
                
            }
            
            System.out.println("END: " + new Date());
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for DISH_PRT algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DISHprtCEC2015(String path, int H, String mfpath, double PRT) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        DISH_PRT_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){

            System.out.println("START: " + new Date());
            
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new DISH_PRT_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal, PRT);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getImp_hist().size(); i++) {

                    writer.print("{" + String.format(Locale.US, "%d", (int)shade.getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", shade.getImp_hist().get(i)[1]) + "}");

                    if (i != shade.getImp_hist().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                System.out.println((k+1) + ". run FES: " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " OFV: " + (shade.getImp_hist().get(shade.getImp_hist().size()-1)[1] - tf.optimum()));
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            System.out.println("END: " + new Date());
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for Db_jSO algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DBjSOCEC2015(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        Db_jSO_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){

            System.out.println("START: " + new Date());
            
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new Db_jSO_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                System.out.println("\n" + (k+1) + ". run - best OFV: " + (shade.getBest().fitness - tf.optimum()));
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            System.out.println("END: " + new Date());
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for Db_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void DBjSOCEC2017(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 28;

        Db_jSO_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
            
            tf = new Cec2017(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new Db_jSO_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for Db_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void EBDISHCEC2017(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 28;

        EB_DISH_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2017(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new EB_DISH_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for Db_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void liteShadeCEC2015(String path, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        liteSHADE_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new liteSHADE_analysis(dimension, MAXFES, tf, NP, generator);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * Main class for Db_SHADE algorithm and POPULATION DIVERSITY/CLUSTERING analysis
     * 
     * @param path
     * @param H
     * @param mfpath
     * @throws Exception 
     */
    public static void liteShade2CEC2015(String path, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        liteSHADE2_analysis shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new liteSHADE2_analysis(dimension, MAXFES, tf, NP, generator);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.16f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    /**
     * MULTITHREAD TESTING
     */

    
    public static void DISH_mt_cec2020(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        int maxFuncNum = 10;

        DISH shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer, prob_writer;
        double best,worst,median,mean,std;
        String hit_path;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){

            System.out.println("START: " + new Date());
            bestArray = new double[runs];
            
            ExecutorService pool = Executors.newFixedThreadPool(30);
            
            for (int k = 0; k < runs; k++) {

                shade = new DISH(dimension, MAXFES, new Cec2020(dimension, funcNumber), H, NP, new util.random.UniformRandom(), NPfinal);
                hit_path = home_dir + path + "oh-" + funcNumber + "-" + k + ".txt";
                shade.initOptimumHit(hit_path);
                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");
                prob_writer = new PrintWriter(home_dir + path + "xoverProbability" + funcNumber + "-" + k + ".txt", "UTF-8");
                
                pool.submit(new SingleThread(shade, writer, prob_writer, bestArray, new Cec2020(dimension, funcNumber), k));
                
                

            }

            pool.shutdown();
            
            if(pool.awaitTermination(10, TimeUnit.DAYS)) {
            
                System.out.println("END: " + new Date());

                for(int z = 0; z < bestArray.length; z++) {
                    final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));

                    if(z != bestArray.length-1) {
                        final_writer.print(',');
                    }
                    else {
                        final_writer.println();
                    }
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
                sol_writer.print(String.format(Locale.US, "%.16f", best));
                sol_writer.print(",");
                sol_writer.print(String.format(Locale.US, "%.16f", worst));
                sol_writer.print(",");
                sol_writer.print(String.format(Locale.US, "%.16f", median));
                sol_writer.print(",");
                sol_writer.print(String.format(Locale.US, "%.16f", mean));
                sol_writer.print(",");
                sol_writer.print(String.format(Locale.US, "%.16f", std));
                sol_writer.print("}");

                sol_writer.close();

                System.out.println("Function f" + funcNumber);
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
                res_writer.print(String.format(Locale.US, "%.16f", best));
                res_writer.print(",");
                res_writer.print(String.format(Locale.US, "%.16f", worst));
                res_writer.print(",");
                res_writer.print(String.format(Locale.US, "%.16f", median));
                res_writer.print(",");
                res_writer.print(String.format(Locale.US, "%.16f", mean));
                res_writer.print(",");
                res_writer.print(String.format(Locale.US, "%.16f", std));
                res_writer.print("}");

                if(funcNumber < maxFuncNum){
                   res_writer.print(",\n");
                }
            
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    public static void DISH_XX_mt_cec2020(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        int maxFuncNum = 10;

        DISH_XX shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer, prob_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){

            System.out.println("START: " + new Date());
            bestArray = new double[runs];
            
            ExecutorService pool = Executors.newFixedThreadPool(30);
            
            for (int k = 0; k < runs; k++) {

                shade = new DISH_XX(dimension, MAXFES, new Cec2020(dimension, funcNumber), H, NP, new util.random.UniformRandom(), NPfinal);
                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");
                prob_writer = new PrintWriter(home_dir + path + "xoverProbability" + funcNumber + "-" + k + ".txt", "UTF-8");
                
                pool.submit(new SingleThreadDISHaXover(shade, writer, prob_writer, bestArray, new Cec2020(dimension, funcNumber), k));

            }

            pool.shutdown();
            
            if(pool.awaitTermination(10, TimeUnit.DAYS)) {
            
                System.out.println("END: " + new Date());

                for(int z = 0; z < bestArray.length; z++) {
                    final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));

                    if(z != bestArray.length-1) {
                        final_writer.print(',');
                    }
                    else {
                        final_writer.println();
                    }
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
                sol_writer.print(String.format(Locale.US, "%.16f", best));
                sol_writer.print(",");
                sol_writer.print(String.format(Locale.US, "%.16f", worst));
                sol_writer.print(",");
                sol_writer.print(String.format(Locale.US, "%.16f", median));
                sol_writer.print(",");
                sol_writer.print(String.format(Locale.US, "%.16f", mean));
                sol_writer.print(",");
                sol_writer.print(String.format(Locale.US, "%.16f", std));
                sol_writer.print("}");

                sol_writer.close();

                System.out.println("Function f" + funcNumber);
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
                res_writer.print(String.format(Locale.US, "%.16f", best));
                res_writer.print(",");
                res_writer.print(String.format(Locale.US, "%.16f", worst));
                res_writer.print(",");
                res_writer.print(String.format(Locale.US, "%.16f", median));
                res_writer.print(",");
                res_writer.print(String.format(Locale.US, "%.16f", mean));
                res_writer.print(",");
                res_writer.print(String.format(Locale.US, "%.16f", std));
                res_writer.print("}");

                if(funcNumber < maxFuncNum){
                   res_writer.print(",\n");
                }
            
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    public static void DISH_XXL_mt_cec2020(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        int maxFuncNum = 10;

        DISH_XXL shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer, prob_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){

            System.out.println("START: " + new Date());
            bestArray = new double[runs];
            
            ExecutorService pool = Executors.newFixedThreadPool(30);
            
            for (int k = 0; k < runs; k++) {

                shade = new DISH_XXL(dimension, MAXFES, new Cec2020(dimension, funcNumber), H, NP, new util.random.UniformRandom(), NPfinal);
                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");
                prob_writer = new PrintWriter(home_dir + path + "xoverProbability" + funcNumber + "-" + k + ".txt", "UTF-8");
                
                pool.submit(new SingleThreadDISHXXL(shade, writer, prob_writer, bestArray, new Cec2020(dimension, funcNumber), k));

            }

            pool.shutdown();
            
            if(pool.awaitTermination(10, TimeUnit.DAYS)) {
            
                System.out.println("END: " + new Date());

                for(int z = 0; z < bestArray.length; z++) {
                    final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));

                    if(z != bestArray.length-1) {
                        final_writer.print(',');
                    }
                    else {
                        final_writer.println();
                    }
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
                sol_writer.print(String.format(Locale.US, "%.16f", best));
                sol_writer.print(",");
                sol_writer.print(String.format(Locale.US, "%.16f", worst));
                sol_writer.print(",");
                sol_writer.print(String.format(Locale.US, "%.16f", median));
                sol_writer.print(",");
                sol_writer.print(String.format(Locale.US, "%.16f", mean));
                sol_writer.print(",");
                sol_writer.print(String.format(Locale.US, "%.16f", std));
                sol_writer.print("}");

                sol_writer.close();

                System.out.println("Function f" + funcNumber);
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
                res_writer.print(String.format(Locale.US, "%.16f", best));
                res_writer.print(",");
                res_writer.print(String.format(Locale.US, "%.16f", worst));
                res_writer.print(",");
                res_writer.print(String.format(Locale.US, "%.16f", median));
                res_writer.print(",");
                res_writer.print(String.format(Locale.US, "%.16f", mean));
                res_writer.print(",");
                res_writer.print(String.format(Locale.US, "%.16f", std));
                res_writer.print("}");

                if(funcNumber < maxFuncNum){
                   res_writer.print(",\n");
                }
            
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }

    public static class SingleThreadDISHXXL implements Runnable {

        Algorithm algorithm;
        PrintWriter writer;
        PrintWriter prob_writer;
        double[] bestArray;
        TestFunction tf;
        int runNo;
        
        public SingleThreadDISHXXL(Algorithm alg, PrintWriter writer, PrintWriter prob_writer, double[] bestArray, TestFunction tf, int runNo) {
            this.algorithm = alg;
            this.writer = writer;
            this.prob_writer = prob_writer;
            this.bestArray = bestArray;
            this.tf = tf;
            this.runNo = runNo;
        }
        
        @Override
        public void run() {
            
            this.algorithm.runAlgorithm();

            this.writer.print("{");

            for (int i = 0; i < ((DISH_XXL)this.algorithm).getImp_hist().size(); i++) {

                writer.print("{" + String.format(Locale.US, "%d", (int)((DISH_XXL)this.algorithm).getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", ((DISH_XXL)this.algorithm).getImp_hist().get(i)[1]) + ",{");
               
                for(int f = 2; f < ((DISH_XXL)this.algorithm).getImp_hist().get(i).length; f++) {
                    writer.print(String.format(Locale.US, "%.16f", ((DISH_XXL)this.algorithm).getImp_hist().get(i)[f]));
                    
                    if(f < ((DISH_XXL)this.algorithm).getImp_hist().get(i).length-1) {
                        writer.print(",");
                    }
                    
                }
                writer.print("}}");

                if (i != ((DISH_XXL)this.algorithm).getImp_hist().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();
            
//            this.prob_writer.print("{");
//
//            for (int i = 0; i < ((DISHsuperXover)this.algorithm).getXoverProbability().size(); i++) {
//
//                prob_writer.print(String.format(Locale.US, "%.3f", ((DISHsuperXover)this.algorithm).getXoverProbability().get(i)));
//
//                if (i != ((DISHsuperXover)this.algorithm).getXoverProbability().size() - 1) {
//                    prob_writer.print(",");
//                }
//
//            }
//
//            prob_writer.print("}");

            prob_writer.close();

            bestArray[this.runNo] = ((DISH_XXL)this.algorithm).getBest().fitness - tf.optimum();

            System.out.println((this.runNo+1) + ". run FES: " + ((DISH_XXL)this.algorithm).getImp_hist().get(((DISH_XXL)this.algorithm).getImp_hist().size()-1)[0] + " OFV: " + (((DISH_XXL)this.algorithm).getImp_hist().get(((DISH_XXL)this.algorithm).getImp_hist().size()-1)[1] - tf.optimum()));
            
        }
        
    }
    
    public static class SingleThreadDISHaXover implements Runnable {

        Algorithm algorithm;
        PrintWriter writer;
        PrintWriter prob_writer;
        double[] bestArray;
        TestFunction tf;
        int runNo;
        
        public SingleThreadDISHaXover(Algorithm alg, PrintWriter writer, PrintWriter prob_writer, double[] bestArray, TestFunction tf, int runNo) {
            this.algorithm = alg;
            this.writer = writer;
            this.prob_writer = prob_writer;
            this.bestArray = bestArray;
            this.tf = tf;
            this.runNo = runNo;
        }
        
        @Override
        public void run() {
            
            this.algorithm.runAlgorithm();

            this.writer.print("{");

            for (int i = 0; i < ((DISH_XX)this.algorithm).getImp_hist().size(); i++) {

                writer.print("{" + String.format(Locale.US, "%d", (int)((DISH_XX)this.algorithm).getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", ((DISH_XX)this.algorithm).getImp_hist().get(i)[1]) + ",{");
               
                for(int f = 2; f < ((DISH_XX)this.algorithm).getImp_hist().get(i).length; f++) {
                    writer.print(String.format(Locale.US, "%.16f", ((DISH_XX)this.algorithm).getImp_hist().get(i)[f]));
                    
                    if(f < ((DISH_XX)this.algorithm).getImp_hist().get(i).length-1) {
                        writer.print(",");
                    }
                    
                }
                writer.print("}}");

                if (i != ((DISH_XX)this.algorithm).getImp_hist().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();
            
//            this.prob_writer.print("{");
//
//            for (int i = 0; i < ((DISHsuperXover)this.algorithm).getXoverProbability().size(); i++) {
//
//                prob_writer.print(String.format(Locale.US, "%.3f", ((DISHsuperXover)this.algorithm).getXoverProbability().get(i)));
//
//                if (i != ((DISHsuperXover)this.algorithm).getXoverProbability().size() - 1) {
//                    prob_writer.print(",");
//                }
//
//            }
//
//            prob_writer.print("}");

            prob_writer.close();

            bestArray[this.runNo] = ((DISH_XX)this.algorithm).getBest().fitness - tf.optimum();

            System.out.println((this.runNo+1) + ". run FES: " + ((DISH_XX)this.algorithm).getImp_hist().get(((DISH_XX)this.algorithm).getImp_hist().size()-1)[0] + " OFV: " + (((DISH_XX)this.algorithm).getImp_hist().get(((DISH_XX)this.algorithm).getImp_hist().size()-1)[1] - tf.optimum()));
            
        }
        
    }
    
    public static class SingleThreadDISHxxCR implements Runnable {

        Algorithm algorithm;
        PrintWriter writer;
        PrintWriter prob_writer;
        double[] bestArray;
        TestFunction tf;
        int runNo;
        
        public SingleThreadDISHxxCR(Algorithm alg, PrintWriter writer, PrintWriter prob_writer, double[] bestArray, TestFunction tf, int runNo) {
            this.algorithm = alg;
            this.writer = writer;
            this.prob_writer = prob_writer;
            this.bestArray = bestArray;
            this.tf = tf;
            this.runNo = runNo;
        }
        
        @Override
        public void run() {
            
            this.algorithm.runAlgorithm();

            this.writer.print("{");

            for (int i = 0; i < ((DISH_XXCR)this.algorithm).getImp_hist().size(); i++) {

                writer.print("{" + String.format(Locale.US, "%d", (int)((DISH_XXCR)this.algorithm).getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", ((DISH_XXCR)this.algorithm).getImp_hist().get(i)[1]) + ",{");
               
                for(int f = 2; f < ((DISH_XXCR)this.algorithm).getImp_hist().get(i).length; f++) {
                    writer.print(String.format(Locale.US, "%.16f", ((DISH_XXCR)this.algorithm).getImp_hist().get(i)[f]));
                    
                    if(f < ((DISH_XXCR)this.algorithm).getImp_hist().get(i).length-1) {
                        writer.print(",");
                    }
                    
                }
                writer.print("}}");

                if (i != ((DISH_XXCR)this.algorithm).getImp_hist().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();
            
//            this.prob_writer.print("{");
//
//            for (int i = 0; i < ((DISHsuperXover)this.algorithm).getXoverProbability().size(); i++) {
//
//                prob_writer.print(String.format(Locale.US, "%.3f", ((DISHsuperXover)this.algorithm).getXoverProbability().get(i)));
//
//                if (i != ((DISHsuperXover)this.algorithm).getXoverProbability().size() - 1) {
//                    prob_writer.print(",");
//                }
//
//            }
//
//            prob_writer.print("}");

            prob_writer.close();

            bestArray[this.runNo] = ((DISH_XXCR)this.algorithm).getBest().fitness - tf.optimum();

            System.out.println((this.runNo+1) + ". run FES: " + ((DISH_XXCR)this.algorithm).getImp_hist().get(((DISH_XXCR)this.algorithm).getImp_hist().size()-1)[0] + " OFV: " + (((DISH_XXCR)this.algorithm).getImp_hist().get(((DISH_XXCR)this.algorithm).getImp_hist().size()-1)[1] - tf.optimum()));
            
        }
        
    }
    
    public static class SingleThread implements Runnable {

        Algorithm algorithm;
        PrintWriter writer;
        PrintWriter prob_writer;
        double[] bestArray;
        TestFunction tf;
        int runNo;
        
        public SingleThread(Algorithm alg, PrintWriter writer, PrintWriter prob_writer, double[] bestArray, TestFunction tf, int runNo) {
            this.algorithm = alg;
            this.writer = writer;
            this.prob_writer = prob_writer;
            this.bestArray = bestArray;
            this.tf = tf;
            this.runNo = runNo;
        }
        
        @Override
        public void run() {
            
            this.algorithm.runAlgorithm();

            ((DISH)this.algorithm).deinitOptimumHit();
            
            this.writer.print("{");

            for (int i = 0; i < ((DISH)this.algorithm).getImp_hist().size(); i++) {

                writer.print("{" + String.format(Locale.US, "%d", (int)((DISH)this.algorithm).getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", ((DISH)this.algorithm).getImp_hist().get(i)[1]) + ",{");
               
                for(int f = 2; f < ((DISH)this.algorithm).getImp_hist().get(i).length; f++) {
                    writer.print(String.format(Locale.US, "%.16f", ((DISH)this.algorithm).getImp_hist().get(i)[f]));
                    
                    if(f < ((DISH)this.algorithm).getImp_hist().get(i).length-1) {
                        writer.print(",");
                    }
                    
                }
                writer.print("}}");

                if (i != ((DISH)this.algorithm).getImp_hist().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();
            
//            this.prob_writer.print("{");
//
//            for (int i = 0; i < ((DISHsuperXover)this.algorithm).getXoverProbability().size(); i++) {
//
//                prob_writer.print(String.format(Locale.US, "%.3f", ((DISHsuperXover)this.algorithm).getXoverProbability().get(i)));
//
//                if (i != ((DISHsuperXover)this.algorithm).getXoverProbability().size() - 1) {
//                    prob_writer.print(",");
//                }
//
//            }
//
//            prob_writer.print("}");

            prob_writer.close();

            bestArray[this.runNo] = ((DISH)this.algorithm).getBest().fitness - tf.optimum();

            System.out.println((this.runNo+1) + ". run FES: " + ((DISH)this.algorithm).getImp_hist().get(((DISH)this.algorithm).getImp_hist().size()-1)[0] + " OFV: " + (((DISH)this.algorithm).getImp_hist().get(((DISH)this.algorithm).getImp_hist().size()-1)[1] - tf.optimum()));
            
        }
        
    }
    
    public static void DISH_randXX_CEC2020(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 10;

        DISH_randXX shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2020(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new DISH_randXX(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getImp_hist().size(); i++) {

                    writer.print("{" + String.format(Locale.US, "%d", (int)shade.getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", shade.getImp_hist().get(i)[1]) + ",{");
               
                    for(int f = 2; f < shade.getImp_hist().get(i).length; f++) {
                        writer.print(String.format(Locale.US, "%.16f", shade.getImp_hist().get(i)[f]));

                        if(f < shade.getImp_hist().get(i).length-1) {
                            writer.print(",");
                        }

                    }
                    writer.print("}}");

                    if (i != shade.getImp_hist().size() - 1) {
                        writer.print(",");
                    }

                }
                
                System.out.println((k+1) + ". run FES: " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " OFV: " + (shade.getImp_hist().get(shade.getImp_hist().size()-1)[1] - tf.optimum()));

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
//                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
//                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
//                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
//                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    
    public static void DISH_XXCR_CEC2020(String path, int H, String mfpath) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 10;

        DISH_XXCR shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer, final_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results.txt", "UTF-8");
        final_writer = new PrintWriter(home_dir + path + "final_res.csv", "UTF-8");
        
        res_writer.print("{");

        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2020(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new DISH_XXCR(dimension, MAXFES, tf, H, NP, generator, NPfinal);
                shade.runAlgorithm();

                writer = new PrintWriter(home_dir + path + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getImp_hist().size(); i++) {

                    writer.print("{" + String.format(Locale.US, "%d", (int)shade.getImp_hist().get(i)[0]) + "," + String.format(Locale.US, "%.16f", shade.getImp_hist().get(i)[1]) + ",{");
               
                    for(int f = 2; f < shade.getImp_hist().get(i).length; f++) {
                        writer.print(String.format(Locale.US, "%.16f", shade.getImp_hist().get(i)[f]));

                        if(f < shade.getImp_hist().get(i).length-1) {
                            writer.print(",");
                        }

                    }
                    writer.print("}}");

                    if (i != shade.getImp_hist().size() - 1) {
                        writer.print(",");
                    }
   
                }

                System.out.println((k+1) + ". run FES: " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " OFV: " + (shade.getImp_hist().get(shade.getImp_hist().size()-1)[1] - tf.optimum()));

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();
                
//                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
//                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
//                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
//                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.16f", bestArray[z]));
                
                if(z != bestArray.length-1) {
                    final_writer.print(',');
                }
                else {
                    final_writer.println();
                }
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
            sol_writer.print(String.format(Locale.US, "%.16f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.16f", std));
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
            res_writer.print(String.format(Locale.US, "%.16f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.16f", std));
            res_writer.print("}");
        
            if(funcNumber < maxFuncNum){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        final_writer.close();
        
    }
    /**
     * Overall
     */
    public static int dimension = 10;
    public static int MAXFES = 10000 * dimension;
    public static final int runs = 30; //51
    public static String home_dir = "C:\\Users\\wiki\\Documents\\";
    
    /**
     * DE
     */
    public static int NP = 100;
    public static int NPinit = 100;
    public static int NPfinal = 20;
    public static final double f = 0.5;
    public static final double cr = 0.8;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        /**
         * population diversity and clustering
         */
        
        int H;
        String path;
        home_dir = "D:\\results\\Optimum_hit\\CEC2020\\";

//        dimension = 5;
//        MAXFES = 10000 * dimension;
//        NPinit = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
//        NPfinal = 4;
//        H = 5;
//        
//        path= "DISH-" + dimension + "\\";
//        
//        DISH_mt_cec2020(path, H, path);
//        
//        dimension = 10;
//        MAXFES = 100000 * dimension;
//        NPinit = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
//        NPfinal = 4;
//        H = 5;
//        
//        path= "DISH-" + dimension + "\\";
//        
//        DISH_mt_cec2020(path, H, path);
        
        dimension = 15;
        MAXFES = 200000 * dimension;
        NPinit = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
        NPfinal = 4;
        H = 5;
        
        path= "DISH-" + dimension + "\\";
        
        DISH_mt_cec2020(path, H, path);
        
        dimension = 20;
        MAXFES = 500000 * dimension;
        NPinit = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
        NPfinal = 4;
        H = 5;
        
        path= "DISH-" + dimension + "\\";
        
        DISH_mt_cec2020(path, H, path);

             
    }
    
}
