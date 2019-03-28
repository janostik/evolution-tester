import algorithm.de.DISH_100digit;
import algorithm.de.jSO_analysis;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import model.tf.Cec2015;
import model.tf.Cec2019_100digit;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author wiki on 15/03/2019
 */
public class CEC2019_100digit_Main {
    
    
    
    /**
     * Main for spolving single function from CEC2019 100 digit challenge
     * 
     * @param path
     * @param func_num
     * @throws Exception 
     */
    public static void DISH_CEC2019(String path, int func_num) throws Exception{

        System.out.println("DISH - CEC2019 - " + func_num);
        System.out.println(new Date());
        System.out.println("==============================");
        
        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();

        DISH_100digit dish;

        double[] bestArray;
        PrintWriter writer, res_writer;
        double best,worst,median,mean,std;

        res_writer = new PrintWriter(home_dir + path + "results_" + func_num + ".txt", "UTF-8");
        
        res_writer.print("{");
        
        tf = new Cec2019_100digit(func_num);
        for (int k = 0; k < runs; k++) {

                dish = new DISH_100digit(dimension, MAXFES, tf, H, NPinit, generator, NPfinal);
                dish.run();
                
                System.out.println(new Date() + " || " + (k+1) + ". run: " + String.format(Locale.US, "%.10f", dish.getBest().fitness - tf.optimum()));

                writer = new PrintWriter(home_dir + path + func_num + "-" + k + "-100digit.txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < dish.getRes_history().size(); i++) {

                    writer.print("{" + dish.getRes_history().get(i)[0] + ", " + String.format(Locale.US, "%.10f", dish.getRes_history().get(i)[1]) + "}");

                    if (i != dish.getRes_history().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                res_writer.print(String.format(Locale.US, "%.10f", dish.getBest().fitness - tf.optimum()));

            }
        
        res_writer.print("}");
 
        res_writer.close();
        
        System.out.println();
        System.out.println(new Date());
        System.out.println("\n==============================\n");
        
    }
    
    /**
     * Overall
     */
    public static int dimension = 10;
    public static int MAXFES = 10000 * dimension;
    public static int runs = 50; //50
    public static int NPinit = (int) (25*Math.log(10)*Math.sqrt(10));
    public static int NPfinal = 4;
    public static int H = 5;
    public static String home_dir = "C:\\Users\\wikki\\Documents\\";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int func_num;
        String path;
        
        /**
         * test
         */
        path = "CEC2019\\test\\";
        func_num = 7;
        dimension = 10;
        MAXFES = 100000 * dimension;
        NPinit = (int) (100*25*Math.log(dimension)*Math.sqrt(dimension));
        
        try {
            DISH_CEC2019(path, func_num);
        } catch (Exception ex) {
            Logger.getLogger(CEC2019_100digit_Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
