package util;

import java.util.Random;
import model.chaos.Disipative;
import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Should contain static access to disipative chaos Random functions.
 * Created by adam on 24/11/15.
 */
public class DisipativeRandomUtil {

    private static Disipative chaos = new Disipative();
    private static Random rnd = new Random();

    /*
    Class not to be instantiated
     */
    private DisipativeRandomUtil() {
    }
    
    public static double cauchy(double a, double b){
        
        CauchyDistribution cauchy = new CauchyDistribution(a,b);
        return cauchy.sample();
        
    }
    
    public static double normal(double mu, double sigma){
        
        NormalDistribution normal = new NormalDistribution(mu,sigma);
        return normal.sample();
        
    }
    
    public static Double nextNormal() {
        return rnd.nextGaussian();
    }
    
    public static Double nextDouble() {
        return chaos.getRndReal();
    }

    public static int nextInt(int bound) {
        return chaos.getRndInt(bound);
    }

    public static double nextDouble(double min, double max) {
        return nextDouble() * (max - min) + min;
    }

}
