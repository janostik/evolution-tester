
import algorithm.Algorithm;
import algorithm.de.DISHbinDxover;
import model.tf.Cec2020;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author wiki
 */
public class CEC2020timeComplexity {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        long start, end, time;
        double x;
        
        /**
         * 
         * T0
         * 
         */
        
        start = System.currentTimeMillis();
        
        x = 0.55;
        
        for(int i = 1; i <= 1_000_000; i++) {

            x=x+x;
            x=x/2;
            x=x*x;
            x=Math.sqrt(x);
            x=Math.log(x);
            x=Math.exp(x);
            x=x/(x+2);
            
        }
        
        end = System.currentTimeMillis();
        
        time = end-start;
        
        System.out.println("T0: " + time);
        
        int dim = 15;
        
        /**
         * 
         * T1
         * 
         */
        
        TestFunction tf = new Cec2020(dim, 7);
        double[] vector = tf.generateTrial(dim);
        
        start = System.currentTimeMillis();
        for(int i = 0; i < 200_000; i++) {
            tf.fitness(vector);
        }
        end = System.currentTimeMillis();
        
        time = end-start;
        
        System.out.println("T1 " + dim + "D: " + time);
        
        /**
         * 
         * T2
         * 
         */
        
        int NPinit = (int) (25*2*Math.log(dim)*Math.sqrt(dim));
        int NPfinal = 4;
        int H = 5;
        int MAXFES = 200_000;
        tf = new Cec2020(dim, 7);
        util.random.Random generator;
        
        Algorithm dishxx;
        
        int runs = 5;
        double[] t2 = new double[runs];
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            dishxx = new DISHbinDxover(dim, MAXFES, tf, H, NPinit, generator, NPfinal);
            
            start = System.currentTimeMillis();
            dishxx.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("DISH-XX T2 " + dim + "D: " + new Mean().evaluate(t2));
        
    }
    
}
