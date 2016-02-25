package model.tf.ap.ann;

import model.tf.ap.APtf;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

/**
 * 
 * @author adam
 */
public class APannProb1 extends APtfann {

    double[][] points = new double[][]{{1,1,1},{1,0,1},{0,1,1},{-1,-1,0},{-1,0,0},{0,-1,0}};

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

        double sum = 0, a, b, length_add = 1;
        double[] distance_array = new double[points.length];
        Integer[] gfs_vector = ap.getGFScode(ap.discretizeVector(vector));
        
        for(int i = 0; i < gfs_vector.length; i++){
            if(gfs_vector[i] == -1){
                length_add = 1/(double) (gfs_vector.length - i);
                break;
            }
        }
        
        for (int i = 0; i < points.length; i++) {
            a = ap.dsh(vector, new double[]{points[i][0],points[i][1]});
            if(Double.isNaN(a) || Double.isInfinite(a)){
                return Double.MAX_VALUE;
            }
            b = points[i][2];

            distance_array[i] = Math.abs(a-b);
        }
        
        return (new Sum().evaluate(distance_array)) + length_add;
//        return (new Median().evaluate(distance_array)) + (new Mean().evaluate(distance_array));

    }

    @Override
    public String name() {
        return "AP_ann_lin_sep";
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
    
        /**
         * Nastaveni pro vyreseni se sinem
         */
        double[] vector = new double[]{-99,-99,20,20,20,-99,-99,45,45,45,45,-99,45,45,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        APtfann tf = new APannProb1();
        
        System.out.println(tf.fitness(vector));
        System.out.println(tf.ap.equation);
        
    }
    
}
