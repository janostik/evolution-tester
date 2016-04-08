package model.tf.ap.regression;

import model.tf.ap.APtf;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

/**
 * 
 * @author adam
 * f() = 45.5
 */
public class APconstantTF extends APtf {

    double[][] points = new double[][]{{0., 45.5}, {0.526316, 45.5}, {1.05263, 45.5}, {1.57895, 
  45.5}, {2.10526, 45.5}, {2.63158, 45.5}, {3.15789, 45.5}, {3.68421, 
  45.5}, {4.21053, 45.5}, {4.73684, 45.5}, {5.26316, 45.5}, {5.78947, 
  45.5}, {6.31579, 45.5}, {6.84211, 45.5}, {7.36842, 45.5}, {7.89474, 
  45.5}, {8.42105, 45.5}, {8.94737, 45.5}, {9.47368, 45.5}, {10., 
  45.5}};

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
        return "AP_constant";
    }

    @Override
    public double max(int dim) {
        return 100;
    }

    @Override
    public double min(int dim) {
        return -100;
    }
    
    public static void main(String[] args) throws Exception {

        double[] vector = new double[]{-100.0, 62.570453387051586, 100.0, 100.0, -99.91160783756236, -99.51932613863342, -10.070959340434955, 76.00809933117542, -98.2519684499834, -99.15170364849503, 21.447149412517273, 20.032150723509503, 43.89915600737092, 39.75124344240357, -32.649069360682056, -92.00802792153219};
        APtf tf = new APconstantTF();
        
        System.out.println(tf.fitness(vector));
        System.out.println(tf.ap.equation);
        
    }
    
}
