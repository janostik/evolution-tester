package model.tf.ap;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

/**
 *
 * EUR USD
 * 
 * @author adam
 */
public class AP3sine extends APtf {

    double[][] points = new double[][]{{-3.14159,0.},{-3.01336,-0.24949},{-2.88514,-0.45862},{-2.75691,-0.593997},{-2.62868,-0.635061},{-2.50045,-0.577911},{-2.37222,-0.436474},{-2.24399,-0.240787},{-2.11577,-0.0326137},{-1.98754,0.140974},{-1.85931,0.235095},{-1.73108,0.214526},{-1.60285,0.0599631},{-1.47463,-0.22787},{-1.3464,-0.62698},{-1.21817,-1.09598},{-1.08994,-1.57889},{-0.961712,-2.0123},{-0.833484,-2.33377},{-0.705255,-2.49055},{-0.577027,-2.44713},{-0.448799,-2.19064},{-0.320571,-1.73339},{-0.192342,-1.11196},{-0.0641141,-0.383106},{0.0641141,0.383106},{0.192342,1.11196},{0.320571,1.73339},{0.448799,2.19064},{0.577027,2.44713},{0.705255,2.49055},{0.833484,2.33377},{0.961712,2.0123},{1.08994,1.57889},{1.21817,1.09598},{1.3464,0.62698},{1.47463,0.22787},{1.60285,-0.0599631},{1.73108,-0.214526},{1.85931,-0.235095},{1.98754,-0.140974},{2.11577,0.0326137},{2.24399,0.240787},{2.37222,0.436474},{2.50045,0.577911},{2.62868,0.635061},{2.75691,0.593997},{2.88514,0.45862},{3.01336,0.24949},{3.14159,0.}};

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
        return "AP_ge_mathematica";
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
        APtf tf = new AP3sine();
        
        System.out.println(tf.fitness(vector));
        System.out.println(tf.ap.equation);
        
    }
    
}
