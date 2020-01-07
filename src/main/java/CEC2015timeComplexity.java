
import algorithm.Algorithm;
import algorithm.de.DbL_SHADE_analysis;
import algorithm.de.Db_SHADE_analysis;
import algorithm.de.Db_jSO_analysis;
import algorithm.de.L_SHADE_analysis;
import algorithm.de.SHADE_analysis;
import algorithm.de.jSO_analysis;
import algorithm.de.liteSHADE2_analysis;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author wiki
 */
public class CEC2015timeComplexity {

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
        
        for(int i = 1; i <= 1_000_000; i++) {
            
            x = 0.55 + (double)i;
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
        
        int dim = 10;
        
        /**
         * 
         * T1
         * 
         */
        
        TestFunction tf = new Cec2015(dim, 6);
        double[] vector = new double[dim];
        
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
        
        int NP = 100, NPfinal = 4;
        int MAXFES = 200_000;
        tf = new Cec2015(dim, 6);
        int H = 10;
        util.random.Random generator;
        
        Algorithm shade;
        
        int runs = 5;
        double[] t2 = new double[runs];
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new SHADE_analysis(dim, MAXFES, tf, H, NP, generator);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));

        t2 = new double[runs];
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new Db_SHADE_analysis(dim, MAXFES, tf, H, NP, generator);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("Db_SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = 18*dim;
        NPfinal = 4;
        H = 6;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new L_SHADE_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("L_SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = 18*dim;
        NPfinal = 4;
        H = 6;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new DbL_SHADE_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("DbL_SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = (int) (25*Math.log(dim)*Math.sqrt(dim));
        NPfinal = 4;
        H = 5;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new jSO_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("jSO T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = (int) (25*Math.log(dim)*Math.sqrt(dim));
        NPfinal = 4;
        H = 5;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new Db_jSO_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("Db_jSO T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        dim = 30;
        
        /**
         * 
         * T1
         * 
         */
        
        tf = new Cec2015(dim, 6);
        vector = new double[dim];
        
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
        
        NP = 100;
        MAXFES = 200_000;
        tf = new Cec2015(dim, 6);
        H = 10;

        
        runs = 5;
        t2 = new double[runs];
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new SHADE_analysis(dim, MAXFES, tf, H, NP, generator);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));

        t2 = new double[runs];
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new Db_SHADE_analysis(dim, MAXFES, tf, H, NP, generator);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("Db_SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = 18*dim;
        NPfinal = 4;
        H = 6;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new L_SHADE_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("L_SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = 18*dim;
        NPfinal = 4;
        H = 6;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new DbL_SHADE_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("DbL_SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = (int) (25*Math.log(dim)*Math.sqrt(dim));
        NPfinal = 4;
        H = 5;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new jSO_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("jSO T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = (int) (25*Math.log(dim)*Math.sqrt(dim));
        NPfinal = 4;
        H = 5;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new Db_jSO_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("Db_jSO T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        dim = 50;
        
        /**
         * 
         * T1
         * 
         */
        
        tf = new Cec2015(dim, 6);
        vector = new double[dim];
        
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
        
        NP = 100;
        MAXFES = 200_000;
        tf = new Cec2015(dim, 6);
        H = 10;

        
        runs = 5;
        t2 = new double[runs];
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new SHADE_analysis(dim, MAXFES, tf, H, NP, generator);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));

        t2 = new double[runs];
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new Db_SHADE_analysis(dim, MAXFES, tf, H, NP, generator);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("Db_SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = 18*dim;
        NPfinal = 4;
        H = 6;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new L_SHADE_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("L_SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = 18*dim;
        NPfinal = 4;
        H = 6;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new DbL_SHADE_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("DbL_SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = (int) (25*Math.log(dim)*Math.sqrt(dim));
        NPfinal = 4;
        H = 5;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new jSO_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("jSO T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = (int) (25*Math.log(dim)*Math.sqrt(dim));
        NPfinal = 4;
        H = 5;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new Db_jSO_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("Db_jSO T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        dim = 100;
        
        /**
         * 
         * T1
         * 
         */
        
        tf = new Cec2015(dim, 6);
        vector = new double[dim];
        
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
        
        NP = 100;
        MAXFES = 200_000;
        tf = new Cec2015(dim, 6);
        H = 10;

        
        runs = 5;
        t2 = new double[runs];
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new SHADE_analysis(dim, MAXFES, tf, H, NP, generator);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));

        t2 = new double[runs];
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new Db_SHADE_analysis(dim, MAXFES, tf, H, NP, generator);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("Db_SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = 18*dim;
        NPfinal = 4;
        H = 6;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new L_SHADE_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("L_SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = 18*dim;
        NPfinal = 4;
        H = 6;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new DbL_SHADE_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("DbL_SHADE T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = (int) (25*Math.log(dim)*Math.sqrt(dim));
        NPfinal = 4;
        H = 5;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new jSO_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("jSO T2 " + dim + "D: " + new Mean().evaluate(t2));
        
        t2 = new double[runs];
        NP = (int) (25*Math.log(dim)*Math.sqrt(dim));
        NPfinal = 4;
        H = 5;
        
        for(int i = 0; i < runs; i++) {
            
            generator = new util.random.UniformRandom();
            shade = new Db_jSO_analysis(dim, MAXFES, tf, H, NP, generator, NPfinal);
            
            start = System.currentTimeMillis();
            shade.runAlgorithm();
            end = System.currentTimeMillis();
        
            time = end-start;
            t2[i] = time;
            
        }
        
        System.out.println("Db_jSO T2 " + dim + "D: " + new Mean().evaluate(t2));
        
    }
    
}
