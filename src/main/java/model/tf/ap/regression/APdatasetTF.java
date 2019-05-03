package model.tf.ap.regression;

import model.tf.ap.APtf;
import org.apache.commons.math3.stat.descriptive.rank.Max;

/**
 * 
 * @author adam
 */
public class APdatasetTF extends APtf {

    double[][] points;
    
    public APdatasetTF(double[][] dataset) {
        
        this.points = dataset;
        
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
        
        double max = new Max().evaluate(distance_array);
        
        for (int i = 0; i < points.length; i++) {
            
            sum += (distance_array[i]/max)*distance_array[i];
        }
        
        return sum;

    }

    @Override
    public String name() {
        return "AP_unviersal_dataset";
    }
    
    public static void main(String[] args) throws Exception {
    
        /**
         * Nastaveni pro vyreseni se sinem
         */
        double[] vector = new double[]{-99,-99,20,20,20,-99,-99,45,45,45,45,-99,45,45,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        APtf tf = new APdatasetTF(new double[][]{});
        
        System.out.println(tf.fitness(vector));
        System.out.println(tf.ap.equation);
        
    }
    
}
