
import algorithm.de.ShaDE;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.IrisDataset;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author adam on 19/11/2015
 */
public class IrisMain {

    public static Individual solveByShade(){
        
        int dimension = 12;
        int NP = 100;
        int MAXFES = 100000;
        TestFunction tf = new IrisDataset(false);
        int H = 100;
        util.random.Random generator = new util.random.UniformRandom();

        ShaDE shade;
        shade = new ShaDE(dimension, MAXFES, tf, H, NP, generator);

        shade.run();
        
//        shade.getBestHistory().stream().forEach((ind) -> {
//            System.out.println(ind.fitness);
//        });
        
        return shade.getBest();
        
    }
    
    public static Individual solveNormalizedByShade(){
        
        int dimension = 12;
        int NP = 100;
        int MAXFES = 100000;
        TestFunction tf = new IrisDataset(true);
        int H = 100;
        util.random.Random generator = new util.random.UniformRandom();

        ShaDE shade;
        shade = new ShaDE(dimension, MAXFES, tf, H, NP, generator);

        shade.run();
        
//        shade.getBestHistory().stream().forEach((ind) -> {
//            System.out.println(ind.fitness);
//        });
        
        return shade.getBest();
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        IrisDataset tf;
        Individual best;
        
        int runs = 51;
        double[] results;
        
        results = new double[runs];
        for(int i=0;i<runs;i++){
            
            tf = new IrisDataset(false);
            best = solveByShade();
            
            results[i] = tf.getAccuracyE(best);
            
        }

        System.out.println("Not normalized");
        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(results).max().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(results).min().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(results));
        System.out.println("Median: " + new Median().evaluate(results));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(results));
        System.out.println("=================================");
        
        results = new double[runs];
        for(int i=0;i<runs;i++){
            
            tf = new IrisDataset(true);
            best = solveNormalizedByShade();
            
            results[i] = tf.getAccuracyE(best);
            
        }

        System.out.println("Normalized");
        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(results).max().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(results).min().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(results));
        System.out.println("Median: " + new Median().evaluate(results));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(results));
        System.out.println("=================================");
        
    }
    
}
