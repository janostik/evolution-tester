package util;

import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 *
 * TEMPORARY SOLUTION FOR CAUCHY AND GAUSSIAN DISTRIBUTION IN SHADE.
 * 
 * @author adam on 25/11/2015
 */
public class OtherDistributionsUtil {
    
    public static double cauchy(double a, double b){
        
        CauchyDistribution cauchy = new CauchyDistribution(a,b);
        return cauchy.sample();
        
    }
    
    public static double normal(double mu, double sigma){
        
        NormalDistribution normal = new NormalDistribution(mu,sigma);
        return normal.sample();
        
    }
    
}
