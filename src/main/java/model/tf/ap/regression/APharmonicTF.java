package model.tf.ap.regression;

import model.tf.ap.APtf;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

/**
 * 
 * @author adam
 * f(x) = 3.65*sin(2x)
 */
public class APharmonicTF extends APtf {

    double[][] points = new double[][]{{0.,0.},{0.526316,3.17086},{1.05263,3.14097},{1.57895,-0.0595},{2.10526,-3.19991},{2.63158,-3.11025},{3.15789,0.118984},{3.68421,3.22811},{4.21053,3.07869},{4.73684,-0.178437},{5.26316,-3.25545},{5.78947,-3.04632},{6.31579,0.237842},{6.84211,3.28192},{7.36842,3.01314},{7.89474,-0.297184},{8.42105,-3.30753},{8.94737,-2.97916},{9.47368,0.356447},{10.,3.33225}};

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
        return "AP_harmonic";
    }
    
    public static void main(String[] args) throws Exception {

        double[] vector = new double[]{-100.0, 62.570453387051586, 100.0, 100.0, -99.91160783756236, -99.51932613863342, -10.070959340434955, 76.00809933117542, -98.2519684499834, -99.15170364849503, 21.447149412517273, 20.032150723509503, 43.89915600737092, 39.75124344240357, -32.649069360682056, -92.00802792153219};
        APtf tf = new APharmonicTF();
        
        System.out.println(tf.fitness(vector));
        System.out.println(tf.ap.equation);
        
    }
    
}
