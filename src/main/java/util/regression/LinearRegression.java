package util.regression;

import java.util.List;

/**
 *
 * Linear regression model using least squares
 * 
 * @author wiki on 30/03/2017
 */
public class LinearRegression implements Regression {

    @Override
    public double[] getRegressionParameters(List<double[]> data) {
        
        if(data == null || data.isEmpty()) {
            return null;
        }
        
        double sum_x = 0, sum_y = 0, x_bar, y_bar;
        
        for(double[] point : data) {
            sum_x += point[0];
            sum_y += point[1];
        }
        
        x_bar = sum_x / data.size();
        y_bar = sum_y / data.size();
        
        double xx_bar = 0, xy_bar = 0;
        
        for(double[] point : data) {
            xx_bar += (point[0] - x_bar) * (point[0] - x_bar);
            xy_bar += (point[0] - x_bar) * (point[1] - y_bar);
        }
        double beta1 = xy_bar / xx_bar;
        double beta0 = y_bar - beta1 * x_bar;
        
        return new double[]{beta1, beta0};
        
    }

    @Override
    public String toString() {
        return "Linear regression";
    }
    
    
    
}
