package model.tf.ap.ann;

import org.apache.commons.math3.stat.descriptive.summary.Sum;

/**
 * 
 * @author adam
 */
public class APannIris extends APtfann {

    double[][] points = new double[][]{
        {5.1, 3.5, 1.4, 0.2, 1},
        {4.9, 3.0, 1.4, 0.2, 1},
        {4.7, 3.2, 1.3, 0.2, 1},
        {4.6, 3.1, 1.5, 0.2, 1},
        {5.0, 3.6, 1.4, 0.2, 1},
        {5.4, 3.9, 1.7, 0.4, 1},
        {4.6, 3.4, 1.4, 0.3, 1},
        {5.0, 3.4, 1.5, 0.2, 1},
        {4.4, 2.9, 1.4, 0.2, 1},
        {4.9, 3.1, 1.5, 0.1, 1},
        {5.4, 3.7, 1.5, 0.2, 1},
        {4.8, 3.4, 1.6, 0.2, 1},
        {4.8, 3.0, 1.4, 0.1, 1},
        {4.3, 3.0, 1.1, 0.1, 1},
        {5.8, 4.0, 1.2, 0.2, 1},
        {5.7, 4.4, 1.5, 0.4, 1},
        {5.4, 3.9, 1.3, 0.4, 1},
        {5.1, 3.5, 1.4, 0.3, 1},
        {5.7, 3.8, 1.7, 0.3, 1},
        {5.1, 3.8, 1.5, 0.3, 1},
        {5.4, 3.4, 1.7, 0.2, 1},
        {5.1, 3.7, 1.5, 0.4, 1},
        {4.6, 3.6, 1.0, 0.2, 1},
        {5.1, 3.3, 1.7, 0.5, 1},
        {4.8, 3.4, 1.9, 0.2, 1},
        {5.0, 3.0, 1.6, 0.2, 1},
        {5.0, 3.4, 1.6, 0.4, 1},
        {5.2, 3.5, 1.5, 0.2, 1},
        {5.2, 3.4, 1.4, 0.2, 1},
        {4.7, 3.2, 1.6, 0.2, 1},
        {4.8, 3.1, 1.6, 0.2, 1},
        {5.4, 3.4, 1.5, 0.4, 1},
        {5.2, 4.1, 1.5, 0.1, 1},
        {5.5, 4.2, 1.4, 0.2, 1},
        {4.9, 3.1, 1.5, 0.1, 1},
        {5.0, 3.2, 1.2, 0.2, 1},
        {5.5, 3.5, 1.3, 0.2, 1},
        {4.9, 3.1, 1.5, 0.1, 1},
        {4.4, 3.0, 1.3, 0.2, 1},
        {5.1, 3.4, 1.5, 0.2, 1},
        {5.0, 3.5, 1.3, 0.3, 1},
        {4.5, 2.3, 1.3, 0.3, 1},
        {4.4, 3.2, 1.3, 0.2, 1},
        {5.0, 3.5, 1.6, 0.6, 1},
        {5.1, 3.8, 1.9, 0.4, 1},
        {4.8, 3.0, 1.4, 0.3, 1},
        {5.1, 3.8, 1.6, 0.2, 1},
        {4.6, 3.2, 1.4, 0.2, 1},
        {5.3, 3.7, 1.5, 0.2, 1},
        {5.0, 3.3, 1.4, 0.2, 1},
        {7.0, 3.2, 4.7, 1.4, 2},
        {6.4, 3.2, 4.5, 1.5, 2},
        {6.9, 3.1, 4.9, 1.5, 2},
        {5.5, 2.3, 4.0, 1.3, 2},
        {6.5, 2.8, 4.6, 1.5, 2},
        {5.7, 2.8, 4.5, 1.3, 2},
        {6.3, 3.3, 4.7, 1.6, 2},
        {4.9, 2.4, 3.3, 1.0, 2},
        {6.6, 2.9, 4.6, 1.3, 2},
        {5.2, 2.7, 3.9, 1.4, 2},
        {5.0, 2.0, 3.5, 1.0, 2},
        {5.9, 3.0, 4.2, 1.5, 2},
        {6.0, 2.2, 4.0, 1.0, 2},
        {6.1, 2.9, 4.7, 1.4, 2},
        {5.6, 2.9, 3.6, 1.3, 2},
        {6.7, 3.1, 4.4, 1.4, 2},
        {5.6, 3.0, 4.5, 1.5, 2},
        {5.8, 2.7, 4.1, 1.0, 2},
        {6.2, 2.2, 4.5, 1.5, 2},
        {5.6, 2.5, 3.9, 1.1, 2},
        {5.9, 3.2, 4.8, 1.8, 2},
        {6.1, 2.8, 4.0, 1.3, 2},
        {6.3, 2.5, 4.9, 1.5, 2},
        {6.1, 2.8, 4.7, 1.2, 2},
        {6.4, 2.9, 4.3, 1.3, 2},
        {6.6, 3.0, 4.4, 1.4, 2},
        {6.8, 2.8, 4.8, 1.4, 2},
        {6.7, 3.0, 5.0, 1.7, 2},
        {6.0, 2.9, 4.5, 1.5, 2},
        {5.7, 2.6, 3.5, 1.0, 2},
        {5.5, 2.4, 3.8, 1.1, 2},
        {5.5, 2.4, 3.7, 1.0, 2},
        {5.8, 2.7, 3.9, 1.2, 2},
        {6.0, 2.7, 5.1, 1.6, 2},
        {5.4, 3.0, 4.5, 1.5, 2},
        {6.0, 3.4, 4.5, 1.6, 2},
        {6.7, 3.1, 4.7, 1.5, 2},
        {6.3, 2.3, 4.4, 1.3, 2},
        {5.6, 3.0, 4.1, 1.3, 2},
        {5.5, 2.5, 4.0, 1.3, 2},
        {5.5, 2.6, 4.4, 1.2, 2},
        {6.1, 3.0, 4.6, 1.4, 2},
        {5.8, 2.6, 4.0, 1.2, 2},
        {5.0, 2.3, 3.3, 1.0, 2},
        {5.6, 2.7, 4.2, 1.3, 2},
        {5.7, 3.0, 4.2, 1.2, 2},
        {5.7, 2.9, 4.2, 1.3, 2},
        {6.2, 2.9, 4.3, 1.3, 2},
        {5.1, 2.5, 3.0, 1.1, 2},
        {5.7, 2.8, 4.1, 1.3, 2},
        {6.3, 3.3, 6.0, 2.5, 3},
        {5.8, 2.7, 5.1, 1.9, 3},
        {7.1, 3.0, 5.9, 2.1, 3},
        {6.3, 2.9, 5.6, 1.8, 3},
        {6.5, 3.0, 5.8, 2.2, 3},
        {7.6, 3.0, 6.6, 2.1, 3},
        {4.9, 2.5, 4.5, 1.7, 3},
        {7.3, 2.9, 6.3, 1.8, 3},
        {6.7, 2.5, 5.8, 1.8, 3},
        {7.2, 3.6, 6.1, 2.5, 3},
        {6.5, 3.2, 5.1, 2.0, 3},
        {6.4, 2.7, 5.3, 1.9, 3},
        {6.8, 3.0, 5.5, 2.1, 3},
        {5.7, 2.5, 5.0, 2.0, 3},
        {5.8, 2.8, 5.1, 2.4, 3},
        {6.4, 3.2, 5.3, 2.3, 3},
        {6.5, 3.0, 5.5, 1.8, 3},
        {7.7, 3.8, 6.7, 2.2, 3},
        {7.7, 2.6, 6.9, 2.3, 3},
        {6.0, 2.2, 5.0, 1.5, 3},
        {6.9, 3.2, 5.7, 2.3, 3},
        {5.6, 2.8, 4.9, 2.0, 3},
        {7.7, 2.8, 6.7, 2.0, 3},
        {6.3, 2.7, 4.9, 1.8, 3},
        {6.7, 3.3, 5.7, 2.1, 3},
        {7.2, 3.2, 6.0, 1.8, 3},
        {6.2, 2.8, 4.8, 1.8, 3},
        {6.1, 3.0, 4.9, 1.8, 3},
        {6.4, 2.8, 5.6, 2.1, 3},
        {7.2, 3.0, 5.8, 1.6, 3},
        {7.4, 2.8, 6.1, 1.9, 3},
        {7.9, 3.8, 6.4, 2.0, 3},
        {6.4, 2.8, 5.6, 2.2, 3},
        {6.3, 2.8, 5.1, 1.5, 3},
        {6.1, 2.6, 5.6, 1.4, 3},
        {7.7, 3.0, 6.1, 2.3, 3},
        {6.3, 3.4, 5.6, 2.4, 3},
        {6.4, 3.1, 5.5, 1.8, 3},
        {6.0, 3.0, 4.8, 1.8, 3},
        {6.9, 3.1, 5.4, 2.1, 3},
        {6.7, 3.1, 5.6, 2.4, 3},
        {6.9, 3.1, 5.1, 2.3, 3},
        {5.8, 2.7, 5.1, 1.9, 3},
        {6.8, 3.2, 5.9, 2.3, 3},
        {6.7, 3.3, 5.7, 2.5, 3},
        {6.7, 3.0, 5.2, 2.3, 3},
        {6.3, 2.5, 5.0, 1.9, 3},
        {6.5, 3.0, 5.2, 2.0, 3},
        {6.2, 3.4, 5.4, 2.3, 3},
        {5.9, 3.0, 5.1, 1.8, 3}
    };

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
        Integer[] gfs_vector = ap.getGFScode(ap.discretizeVector(vector));
        
        for(int i = 0; i < gfs_vector.length; i++){
            if(gfs_vector[i] == -1){
                length_add = 1/(double) (gfs_vector.length - i);
                break;
            }
        }
        
        for (int i = 0; i < points.length; i++) {
            a = ap.dsh(vector, points[i]);
            if(Double.isNaN(a) || Double.isInfinite(a)){
                return Double.MAX_VALUE;
            }
            
            switch((int)points[i][4]){
                case 1:
                    if(a < -1){
                        b = 0;
                    }
                    else{
                        b = 1;
                    }
                    break;
                case 2:
                    if(a >= -1 && a <= 1){
                        b = 0;
                    }
                    else {
                        b = 1;
                    }
                    break;
                default:
                    if(a > 1){
                        b = 0;
                    }
                    else {
                        b = 1;
                    }
                    
            }

            sum += b;
        }
        
        return sum + length_add;
//        return (new Median().evaluate(distance_array)) + (new Mean().evaluate(distance_array));

    }

    @Override
    public String name() {
        return "AP_ann_iris";
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
        double[] vector = new double[]{-73.71822145729683, 46.38661781005663, -99.9157690142776, 30.58482047521635, -33.37720563547538, 94.9222090625282, -100.0, -46.37684393208294, -56.64306882018529, 73.01657633105404, -69.68620310119915, -9.616350456769371, -63.809497852952546, -15.074862490627767, 39.207951418896414, 11.479179982282302, 100.0, -23.8736689384203, -98.56970542428338, -60.27871372373294, -49.55741583319592, 12.77996725583382, -10.875618271878746, 66.67175664757876, 41.68038787194352, 0.19843519189455333, 7.941780675499988, -100.0, 34.849529273176984, -100.0};
        APtfann tf = new APannIris();
        
        System.out.println(tf.fitness(vector));
        System.out.println(tf.ap.equation);
        
    }
    
}