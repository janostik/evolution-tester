
import algorithm.de.CmDbL_SHADE;
import algorithm.de.CmDb_SHADE;
import algorithm.de.DIbL_SHADE_analysis;
import algorithm.de.DIb_SHADE_analysis;
import algorithm.de.DbL_SHADE_analysis;
import algorithm.de.Db_SHADE_analysis;
import algorithm.de.L_SHADE_analysis;
import algorithm.de.SHADE_analysis;
import algorithm.de.liteSHADE2_analysis;
import algorithm.de.liteSHADE_analysis;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.tf.Cec2015;
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
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.10f", bestArray[z]));
                
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
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.10f", bestArray[z]));
                
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
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.10f", bestArray[z]));
                
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
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.10f", bestArray[z]));
                
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
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.10f", bestArray[z]));
                
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
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.10f", bestArray[z]));
                
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
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.10f", bestArray[z]));
                
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
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.10f", bestArray[z]));
                
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
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.10f", bestArray[z]));
                
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
                
                shade.writeMFhistory(home_dir + mfpath + "mf" + funcNumber + "-" + k + ".txt");
                shade.writeMCRhistory(home_dir + mfpath + "mcr" + funcNumber + "-" + k + ".txt");
                shade.writePopDiversityHistory(home_dir + mfpath + "PopDiv" + funcNumber + "-" + k + ".txt");
                shade.writeClusteringHistory(home_dir + mfpath + "Cluster" + funcNumber + "-" + k + ".txt");

            }
            
            for(int z = 0; z < bestArray.length; z++) {
                final_writer.print(String.format(Locale.US, "%.10f", bestArray[z]));
                
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
        final_writer.close();
        
    }
    
    /**
     * Overall
     */
    public static int dimension = 10;
    public static int MAXFES = 10000 * dimension;
    public static final int runs = 51; //51
    public static String home_dir = "C:\\Users\\wiki\\Documents\\";
    
    /**
     * DE
     */
    public static final int NP = 100;
    public static final int NPinit = 100;
    public static final int NPfinal = 20;
    public static final double f = 0.5;
    public static final double cr = 0.8;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        /**
         * population diversity and clustering
         */
        
        int H = 10;
        int wDis, wImp;
        String path;
        home_dir = "";
        
        dimension = 10;
        MAXFES = 10000 * dimension;

        System.out.println("\n\nTime: " + new Date() + " start liteSHADE2 " + dimension + "D\n\n");
        
        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-liteSHADE2-" + dimension + "/";
        
        liteShade2CEC2015(path, path);
        
        dimension = 30;
        MAXFES = 10000 * dimension;

        System.out.println("\n\nTime: " + new Date() + " start liteSHADE2 " + dimension + "D\n\n");
        
        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-liteSHADE2-" + dimension + "/";
        
        liteShade2CEC2015(path, path);
        
        dimension = 50;
        MAXFES = 10000 * dimension;

        System.out.println("\n\nTime: " + new Date() + " start liteSHADE2 " + dimension + "D\n\n");
        
        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-liteSHADE2-" + dimension + "/";
        
        liteShade2CEC2015(path, path);
        
//        System.out.println("\n\nTime: " + new Date() + " start SHADE " + dimension + "D\n\n");
//        
//        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-SHADE-" + dimension + "/";
//        
//        shadeCEC2015(path, H, path);
//        
//        System.out.println("\n\nTime: " + new Date() + " start L_SHADE " + dimension + "D\n\n");
//        
//        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-L_SHADE-" + dimension + "/";
//        
//        lshadeCEC2015(path, H, path);
//
//        System.out.println("\n\nTime: " + new Date() + " start Db_SHADE " + dimension + "D\n\n");
//        
//        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-Db_SHADE-" + dimension + "/";
//        
//        DBshadeCEC2015(path, H, path);
//        
//        System.out.println("\n\nTime: " + new Date() + " start DbL_SHADE " + dimension + "D\n\n");
//        
//        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DbL_SHADE-" + dimension + "/";
//        
//        DBlshadeCEC2015(path, H, path);
//        
//        /**
//         * 1 1
//         */
//        
//        dimension = 10;
//        MAXFES = 10000 * dimension;
//        wDis = 1;
//        wImp = 1;
//
////        System.out.println("\n\nTime: " + new Date() + " start DIb_SHADE " + dimension + "D\n\n");
////        
////        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DIb" + wDis + "" + wImp + "_SHADE-" + dimension + "/";
////        
////        DIBshadeCEC2015(path, H, path, wDis, wImp);
////        
////        dimension = 30;
////        MAXFES = 10000 * dimension;
////        
////        System.out.println("\n\nTime: " + new Date() + " start DIb_SHADE " + dimension + "D\n\n");
////        
////        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DIb" + wDis + "" + wImp + "_SHADE-" + dimension + "/";
////        
////        DIBshadeCEC2015(path, H, path, wDis, wImp);
////        
////        dimension = 10;
////        MAXFES = 10000 * dimension;
////        wDis = 1;
////        wImp = 1;
////
////        System.out.println("\n\nTime: " + new Date() + " start DIbL_SHADE " + dimension + "D\n\n");
////        
////        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DIbL" + wDis + "" + wImp + "_SHADE-" + dimension + "/";
////        
////        DIBlshadeCEC2015(path, H, path, wDis, wImp);
////        
////        dimension = 30;
////        MAXFES = 10000 * dimension;
////        
////        System.out.println("\n\nTime: " + new Date() + " start DIbL_SHADE " + dimension + "D\n\n");
////        
////        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DIbL" + wDis + "" + wImp + "_SHADE-" + dimension + "/";
////        
////        DIBlshadeCEC2015(path, H, path, wDis, wImp);
//        
//        /**
//         * 3 1
//         */
//        
//        dimension = 10;
//        MAXFES = 10000 * dimension;
//        wDis = 3;
//        wImp = 1;
//
////        System.out.println("\n\nTime: " + new Date() + " start DIb_SHADE " + dimension + "D\n\n");
////        
////        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DIb" + wDis + "" + wImp + "_SHADE-" + dimension + "/";
////        
////        DIBshadeCEC2015(path, H, path, wDis, wImp);
//        
//        dimension = 30;
//        MAXFES = 10000 * dimension;
//        
//        System.out.println("\n\nTime: " + new Date() + " start DIb_SHADE " + dimension + "D\n\n");
//        
//        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DIb" + wDis + "" + wImp + "_SHADE-" + dimension + "/";
//        
//        DIBshadeCEC2015(path, H, path, wDis, wImp);
//        
//        dimension = 10;
//        MAXFES = 10000 * dimension;
//        wDis = 3;
//        wImp = 1;
//
////        System.out.println("\n\nTime: " + new Date() + " start DIbL_SHADE " + dimension + "D\n\n");
////        
////        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DIbL" + wDis + "" + wImp + "_SHADE-" + dimension + "/";
////        
////        DIBlshadeCEC2015(path, H, path, wDis, wImp);
//        
//        dimension = 30;
//        MAXFES = 10000 * dimension;
//        
//        System.out.println("\n\nTime: " + new Date() + " start DIbL_SHADE " + dimension + "D\n\n");
//        
//        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DIbL" + wDis + "" + wImp + "_SHADE-" + dimension + "/";
//        
//        DIBlshadeCEC2015(path, H, path, wDis, wImp);
//        
//        /**
//         * 1 3
//         */
//        
//        dimension = 10;
//        MAXFES = 10000 * dimension;
//        wDis = 1;
//        wImp = 3;
//
////        System.out.println("\n\nTime: " + new Date() + " start DIb_SHADE " + dimension + "D\n\n");
////        
////        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DIb" + wDis + "" + wImp + "_SHADE-" + dimension + "/";
////        
////        DIBshadeCEC2015(path, H, path, wDis, wImp);
//        
//        dimension = 30;
//        MAXFES = 10000 * dimension;
//        
//        System.out.println("\n\nTime: " + new Date() + " start DIb_SHADE " + dimension + "D\n\n");
//        
//        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DIb" + wDis + "" + wImp + "_SHADE-" + dimension + "/";
//        
//        DIBshadeCEC2015(path, H, path, wDis, wImp);
//        
//        dimension = 10;
//        MAXFES = 10000 * dimension;
//        wDis = 1;
//        wImp = 3;
//
////        System.out.println("\n\nTime: " + new Date() + " start DIbL_SHADE " + dimension + "D\n\n");
////        
////        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DIbL" + wDis + "" + wImp + "_SHADE-" + dimension + "/";
////        
////        DIBlshadeCEC2015(path, H, path, wDis, wImp);
//        
//        dimension = 30;
//        MAXFES = 10000 * dimension;
//        
//        System.out.println("\n\nTime: " + new Date() + " start DIbL_SHADE " + dimension + "D\n\n");
//        
//        path = "E:\\results\\ANALYSIS\\CLUSTERING\\CEC2015-DIbL" + wDis + "" + wImp + "_SHADE-" + dimension + "/";
//        
//        DIBlshadeCEC2015(path, H, path, wDis, wImp);
//        
//        System.out.println("\n\nTime: " + new Date() + " END of EVERYTHING");
        
    }
    
}
