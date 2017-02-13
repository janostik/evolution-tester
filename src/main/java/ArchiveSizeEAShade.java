
import algorithm.de.EAL_SHADE;
import algorithm.de.EA_SHADE;
import algorithm.de.SHADE;
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
 * @author adam on 18/11/2015
 */
public class ArchiveSizeEAShade {
    
    public static void EASHADEarchiveSize(String path, int H, int Asize) throws Exception{

        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();
        int maxFuncNum = 15;

        EA_SHADE shade;

        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(path + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber <= maxFuncNum; funcNumber++){
        
            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new EA_SHADE(dimension, MAXFES, tf, H, NP, generator);
                shade.setAsize(Asize);
                shade.run();

                bestArray[k] = shade.getBest().fitness - tf.optimum();

            }
            
            best = DoubleStream.of(bestArray).min().getAsDouble();
            worst = DoubleStream.of(bestArray).max().getAsDouble();
            median = new Median().evaluate(bestArray);
            mean = new Mean().evaluate(bestArray);
            std = new StandardDeviation().evaluate(bestArray);

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
     * Overall
     */
    public static int dimension = 10;
    public static int MAXFES = 10000 * dimension;
    public static final int runs = 51; //51
    
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
    public static final int NP = 100;
    public static final int NPinit = 100;
    public static final int NPfinal = 20;
    public static final double f = 0.5;
    public static final double cr = 0.8;
    
    
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        
        util.random.Random chGenerator;
        String chaosName, path; 
        int H = 10;
        int Asize;
        String alg = "EA_SHADE";
        
        dimension = 10;
        MAXFES = 10000 * dimension;
        
        Asize = 0;
        path = "E:\\results\\CEC2015-Archive_Size-10\\" + alg + "\\" + Asize + "/";
        EASHADEarchiveSize(path, H, Asize);
        
        Asize = 10;
        path = "E:\\results\\CEC2015-Archive_Size-10\\" + alg + "\\" + Asize + "/";
        EASHADEarchiveSize(path, H, Asize);
        
        Asize = 20;
        path = "E:\\results\\CEC2015-Archive_Size-10\\" + alg + "\\" + Asize + "/";
        EASHADEarchiveSize(path, H, Asize);
        
        Asize = 30;
        path = "E:\\results\\CEC2015-Archive_Size-10\\" + alg + "\\" + Asize + "/";
        EASHADEarchiveSize(path, H, Asize);
        
        Asize = 40;
        path = "E:\\results\\CEC2015-Archive_Size-10\\" + alg + "\\" + Asize + "/";
        EASHADEarchiveSize(path, H, Asize);
        
        Asize = 50;
        path = "E:\\results\\CEC2015-Archive_Size-10\\" + alg + "\\" + Asize + "/";
        EASHADEarchiveSize(path, H, Asize);
        
        Asize = 60;
        path = "E:\\results\\CEC2015-Archive_Size-10\\" + alg + "\\" + Asize + "/";
        EASHADEarchiveSize(path, H, Asize);
        
        Asize = 70;
        path = "E:\\results\\CEC2015-Archive_Size-10\\" + alg + "\\" + Asize + "/";
        EASHADEarchiveSize(path, H, Asize);
        
        Asize = 80;
        path = "E:\\results\\CEC2015-Archive_Size-10\\" + alg + "\\" + Asize + "/";
        EASHADEarchiveSize(path, H, Asize);
        
        Asize = 90;
        path = "E:\\results\\CEC2015-Archive_Size-10\\" + alg + "\\" + Asize + "/";
        EASHADEarchiveSize(path, H, Asize);
        
        Asize = 100;
        path = "E:\\results\\CEC2015-Archive_Size-10\\" + alg + "\\" + Asize + "/";
        EASHADEarchiveSize(path, H, Asize);
        
        
        
    }
    
}
