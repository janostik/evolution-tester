package util.regression;

import java.util.List;

/**
 *
 * Regression interface
 * 
 * @author wiki on 30/03/2017
 */
public interface Regression {
    
    public double[] getRegressionParameters(List<double[]> data);

    @Override
    public String toString();
    
}
