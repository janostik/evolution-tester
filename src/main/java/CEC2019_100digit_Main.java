import algorithm.de.ACID_100digit;
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
import model.tf.Cec2019_100digit_manual;
import model.tf.TestFunction;
import org.apache.commons.math3.ml.distance.ChebyshevDistance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author wiki on 15/03/2019
 */
public class CEC2019_100digit_Main {
    
    /**
     * Main for solving single function from CEC2019 100 digit challenge
     * 
     * @param path
     * @param func_num
     * @param startIndex
     * @throws Exception 
     */
    public static void ACID_CEC2019(String path, int func_num, int startIndex) throws Exception {

        System.out.println("ACID - CEC2019 - " + func_num + " runs " + (startIndex) + " - " + (startIndex+(runs-1)));
        System.out.println(new Date());
        System.out.println("==============================");
        
        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();

        ACID_100digit dish;

        PrintWriter writer;
        
        tf = new Cec2019_100digit_manual(func_num);
        double eps = Math.abs((tf.max(0)-tf.min(0)))/100.0;
        double resolution = Math.pow(10, -10);
        
        for (int k = 0; k < runs; k++) {

                dish = new ACID_100digit(dimension, MAXFES, tf, H, NPinit, generator, NPfinal, eps, 3, new ChebyshevDistance(), resolution);
                dish.run();
                
                System.out.println(new Date() + " || " + (k+startIndex) + ". run: " + String.format(Locale.US, "%.10f", dish.getBest().fitness - tf.optimum()));

                writer = new PrintWriter(home_dir + path + func_num + "-" + (k+startIndex) + "-100digit.txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < dish.getRes_history().size(); i++) {

                    writer.print("{" + dish.getRes_history().get(i)[0] + ", " + String.format(Locale.US, "%.10f", dish.getRes_history().get(i)[1]) + "}");

                    if (i != dish.getRes_history().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

            }
        
        System.out.println();
        System.out.println(new Date());
        System.out.println("\n==============================\n");
        
    }
    
    /**
     * Main for solving single function from CEC2019 100 digit challenge
     * 
     * @param path
     * @param func_num
     * @param startIndex
     * @throws Exception 
     */
    public static void DISH_CEC2019(String path, int func_num, int startIndex) throws Exception{

        System.out.println("DISH - CEC2019 - " + func_num + " runs " + (startIndex) + " - " + (startIndex+(runs-1)));
        System.out.println(new Date());
        System.out.println("==============================");
        
        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();

        DISH_100digit dish;

        double[] bestArray;
        PrintWriter writer, res_writer;
        double best,worst,median,mean,std;

//        res_writer = new PrintWriter(home_dir + path + "results_" + func_num + ".txt", "UTF-8");
        
//        res_writer.print("{");
        
        tf = new Cec2019_100digit_manual(func_num);
        for (int k = 0; k < runs; k++) {

                dish = new DISH_100digit(dimension, MAXFES, tf, H, NPinit, generator, NPfinal);
                dish.run();
                
                System.out.println(new Date() + " || " + (k+startIndex) + ". run: " + String.format(Locale.US, "%.10f", dish.getBest().fitness - tf.optimum()));

                writer = new PrintWriter(home_dir + path + func_num + "-" + (k+startIndex) + "-100digit.txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < dish.getRes_history().size(); i++) {

                    writer.print("{" + dish.getRes_history().get(i)[0] + ", " + String.format(Locale.US, "%.10f", dish.getRes_history().get(i)[1]) + "}");

                    if (i != dish.getRes_history().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

//                res_writer.print(String.format(Locale.US, "%.10f", dish.getBest().fitness - tf.optimum()));

            }
        
//        res_writer.print("}");
 
//        res_writer.close();
        
        System.out.println();
        System.out.println(new Date());
        System.out.println("\n==============================\n");
        
    }
    
    /**
     * Overall
     */
    public static int dimension = 10;
    public static int MAXFES = 10000 * dimension;
    public static int runs = 50; 
    public static int NPinit = (int) (25*Math.log(10)*Math.sqrt(10));
    public static int NPfinal = 4;
    public static int H = 5;
    public static String home_dir = "C:\\Users\\wikki\\Documents\\";
    
    /**
     * 
     * @param path
     * @throws Exception 
     */
    public static void ACID_CEC2019_whole(String path) throws Exception {
    
        for(int f = 1; f < 11; f++) {
            
            dimension = 10;
            NPinit = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
            
            switch(f){
                case 1:
                    
                    dimension = 9;
                    MAXFES = 90000;
                    NPinit = 164;
                    ACID_CEC2019(path, f, 0);
                    
                    break;
                case 2:
                    
                    dimension = 16;
                    MAXFES = 320000;
                    NPinit = 277;
                    ACID_CEC2019(path, f, 0);
                    
                    break;
                case 3:
                    
                    dimension = 18;
                    MAXFES = 1800000;
                    NPinit = 306;
                    ACID_CEC2019(path, f, 0);
                    
                    break;
                case 4:
                    MAXFES = 1000000;
                    NPinit = 728;
                    ACID_CEC2019(path, f, 0);
                    break;
                case 5:
                    MAXFES = 100000;
                    ACID_CEC2019(path, f, 0);
                    break;
                case 6:
                    MAXFES = 100000;
                    ACID_CEC2019(path, f, 0);
                    break;
                case 7:
                    MAXFES = 20000000;
                    NPinit = 1820;
                    ACID_CEC2019(path, f, 0);
                    break;
                case 8:
                    MAXFES = 20000000;
                    NPinit = 1820;
                    ACID_CEC2019(path, f, 0);
                    break;
                case 9:
                    MAXFES = 1000000;
                    ACID_CEC2019(path, f, 0);
                    break;
                case 10:
                    MAXFES = 600000;
                    ACID_CEC2019(path, f, 0);
                    break;
                    
            }
            
        }
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int func_num;
        String path;
        
        /**
         * test
         */
        path = "CEC2019\\ACID\\";

        try {
            ACID_CEC2019_whole(path);
        } catch (Exception ex) {
            Logger.getLogger(CEC2019_100digit_Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
