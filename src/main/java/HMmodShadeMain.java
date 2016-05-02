
import algorithm.de.FCR_SHADE;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.tf.Cec2015;
import model.tf.TestFunction;

/**
 *
 * Class for creation of HeatMap files.
 * modShaDE algorithm with different settigns for Finit and CRinit.
 * 
 * @author adam on 20/11/2015
 */
public class HMmodShadeMain {

    public static double countMean(TestFunction tf, int D, int NP, int H, int MAXFES, double Finit, double CRinit, int runs){
        
        double[] res = new double[runs];
        FCR_SHADE shade;
        
        for(int i=0; i<runs; i++){
            
            shade = new FCR_SHADE(D, MAXFES, tf, H, NP, Finit, CRinit);
            shade.run();
            
            res[i] = shade.getBest().fitness;
            
        }
        
        return DoubleStream.of(res).average().getAsDouble();
        
    }
    
    public static void printOutHMdata(TestFunction tf, String home_dir) throws Exception{
        
        int D = 10;
        int NP = 100;
        double CRmin = 0.0;
        double CRmax = 1.;
        double CRstep = 0.1;
        double Fmin = 0.1;
        double Fmax = 1.5;
        double Fstep = 0.1;
        int MAXFES = 10000*D - (D*1000);
//        int MAXFES = 1000;
        int H = 1;
        int runs = 20;

        PrintWriter writer = new PrintWriter(home_dir + "HM-" + tf.name() + ".txt", "UTF-8");
        double result;
        
        System.out.println("===============================");
        System.out.println("STARTED TESTING - " + tf.name());
        System.out.println((new Date()).toString());

        writer.print("{");
        /**
         * CR loop
         */
        for(double cr = CRmin; cr <= CRmax; cr = Math.round((cr+CRstep)*10)/10.0){
            
            /**
             * F loop
             */
            for(double f = Fmin; f <= Fmax; f = Math.round((f+Fstep)*10)/10.0){
                
                result = countMean(tf, D, NP, H, MAXFES, f, cr, runs);
                writer.print("{" + f + "," + cr + "," + String.format(Locale.ENGLISH, "%.10f", result) + "}");
                
                if(f != Fmax){
                    writer.print(",");
                }
                
            }
            
            if(cr != CRmax){
                writer.print(",");
            }
            
        }
        writer.print("}");
        writer.close();
        
        System.out.println("ENDED TESTING - " + tf.name());
        System.out.println((new Date()).toString());
        System.out.println("===============================");
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        
        TestFunction tf;
        String home_dir = "/Users/adam/HM-modShade/";
        int dimension = 10;
        
        for(int i=1; i<16;i++){
            
            tf = new Cec2015(dimension, i);
            
            printOutHMdata(tf, home_dir);
            
        }
        
        
    }
    
}
