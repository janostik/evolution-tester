package model.tf.ap.regression;

import model.tf.ap.APtf;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

/**
 * 
 * @author adam
 * f(x) = 3x+0.65
 */
public class APlinearTF extends APtf {

    double[][] points = new double[][]{{0., 0.65}, {0.526316, 2.22895}, {1.05263, 3.80789}, {1.57895, 
  5.38684}, {2.10526, 6.96579}, {2.63158, 8.54474}, {3.15789, 
  10.1237}, {3.68421, 11.7026}, {4.21053, 13.2816}, {4.73684, 
  14.8605}, {5.26316, 16.4395}, {5.78947, 18.0184}, {6.31579, 
  19.5974}, {6.84211, 21.1763}, {7.36842, 22.7553}, {7.89474, 
  24.3342}, {8.42105, 25.9132}, {8.94737, 27.4921}, {9.47368, 
  29.0711}, {10., 30.65}};

    public Integer[] discretizeVector(double[] vector) {
        int dim = vector.length;
        Integer[] discrete = new Integer[dim];

        for (int i = 0; i < dim; i++) {

            
            discrete[i] = Float.valueOf(Math.round(vector[i])).intValue();
            
        }

        return discrete;
    }

    @Override
    protected double getDistance(double[] vector) {

        double sum = 0, a, b;
        double[] distance_array = new double[points.length];
        
        for (int i = 0; i < points.length; i++) {
            a = ap.dsh(vector, points[i][0]);
            if(Double.isNaN(a) || Double.isInfinite(a)){
                return Double.MAX_VALUE;
            }
            b = points[i][1];

            distance_array[i] = Math.abs(a-b);
        }
        
        return new Sum().evaluate(distance_array);
//        return (new Median().evaluate(distance_array)) + (new Mean().evaluate(distance_array));

    }

    @Override
    public String name() {
        return "AP_linear";
    }

    @Override
    public double max(int dim) {
        return 10;
    }

    @Override
    public double min(int dim) {
        return 0;
    }
    
    public static void main(String[] args) throws Exception {

        double[] vector = new double[]{-100.0, 62.570453387051586, 100.0, 100.0, -99.91160783756236, -99.51932613863342, -10.070959340434955, 76.00809933117542, -98.2519684499834, -99.15170364849503, 21.447149412517273, 20.032150723509503, 43.89915600737092, 39.75124344240357, -32.649069360682056, -92.00802792153219};
        APtf tf = new APlinearTF();
        
        System.out.println(tf.fitness(vector));
        System.out.println(tf.ap.equation);
        
    }
    
}
