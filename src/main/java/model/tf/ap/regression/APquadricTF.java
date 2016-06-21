package model.tf.ap.regression;

import model.tf.ap.APtf;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

/**
 * 
 * @author adam
 * f(x) = 2.3x2 - 20x - 5.6
 */
public class APquadricTF extends APtf {

    double[][] points = new double[][]{{0.,-5.6},{0.526316,-15.4892},{1.05263,-24.1042},{1.57895,-31.4449},{2.10526,-37.5114},{2.63158,-42.3036},{3.15789,-45.8216},{3.68421,-48.0654},{4.21053,-49.0349},{4.73684,-48.7302},{5.26316,-47.1512},{5.78947,-44.2981},{6.31579,-40.1706},{6.84211,-34.769},{7.36842,-28.0931},{7.89474,-20.1429},{8.42105,-10.9186},{8.94737,-0.419945},{9.47368,11.3529},{10.,24.4}};

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
    public double max(int dim) {
        return 10;
    }

    @Override
    public double min(int dim) {
        return 0;
    }
    
    @Override
    public String name() {
        return "AP_quadric";
    }
    
    public static void main(String[] args) throws Exception {

        double[] vector = new double[]{-100.0, 62.570453387051586, 100.0, 100.0, -99.91160783756236, -99.51932613863342, -10.070959340434955, 76.00809933117542, -98.2519684499834, -99.15170364849503, 21.447149412517273, 20.032150723509503, 43.89915600737092, 39.75124344240357, -32.649069360682056, -92.00802792153219};
        APtf tf = new APquadricTF();
        
        System.out.println(tf.fitness(vector));
        System.out.println(tf.ap.equation);
        
    }
    
}
