package model.tf.ap.regression;

import model.tf.ap.regression.AP4sine;
import model.tf.ap.APtf;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

/**
 *
 * EUR USD
 * 
 * @author adam
 */
public class AP4sine extends APtf {

    double[][] points = new double[][]{{-3.14159,0.},{-3.01336,0.241228},{-2.88514,0.396523},{-2.75691,0.405489},{-2.62868,0.251538},{-2.50045,-0.0323762},{-2.37222,-0.372404},{-2.24399,-0.674671},{-2.11577,-0.852786},{-1.98754,-0.854406},{-1.85931,-0.679317},{-1.73108,-0.383585},{-1.60285,-0.067914},{-1.47463,0.147397},{-1.3464,0.154851},{-1.21817,-0.108797},{-1.08994,-0.640426},{-0.961712,-1.36407},{-0.833484,-2.14261},{-0.705255,-2.80566},{-0.577027,-3.18741},{-0.448799,-3.16557},{-0.320571,-2.69206},{-0.192342,-1.80764},{-0.0641141,-0.636761},{0.0641141,0.636761},{0.192342,1.80764},{0.320571,2.69206},{0.448799,3.16557},{0.577027,3.18741},{0.705255,2.80566},{0.833484,2.14261},{0.961712,1.36407},{1.08994,0.640426},{1.21817,0.108797},{1.3464,-0.154851},{1.47463,-0.147397},{1.60285,0.067914},{1.73108,0.383585},{1.85931,0.679317},{1.98754,0.854406},{2.11577,0.852786},{2.24399,0.674671},{2.37222,0.372404},{2.50045,0.0323762},{2.62868,-0.251538},{2.75691,-0.405489},{2.88514,-0.396523},{3.01336,-0.241228},{3.14159,0.}};

//    public Integer[] discretizeVector(double[] vector) {
//        int dim = vector.length;
//        Integer[] discrete = new Integer[dim];
//
//        for (int i = 0; i < dim; i++) {
//
//            
//            discrete[i] = Float.valueOf(Math.round(vector[i])).intValue();
//            
//        }
//
//        return discrete;
//    }
    
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

    }

    @Override
    public String name() {
        return "AP_4sine_mathematica";
    }

    public static void main(String[] args) throws Exception {
    
        double[] vector = new double[]{7, 0.2988774600764819, 3.2619342867613716, 2.417692945770849, 0.499045747255485, 0.9209323684654931, 1.6620944589216458, 0.6130622157511904, 2.766998074369041, 1.4192090068665326, 1.949000337196657, 1.0447484649823418, 2.1765843898135895, 1.244411083489031, 1.9509499646606838, 0.9606638699824362, 2.320503015045442, 1.4470304488832277, 1.6784563561064758, 0.5987344181373808, 2.7995362789049327, 2.080422271521834, 0.8412068895505901, 0.5078937636883215, 2.53534065819039, 1.0779756253942605, 2.413268377610194, 1.663387442949218, 1.3570321474766684, 0.1604917604997035, 3.3838366925740804, 2.854611645940546, 0.1827648401266467, 1.2401505604996692, 0.778518238772481, 2.2747508860218586, 0.49966077516567736, 3.2661087369145196, 2.8240586286522804, 0.18968868856637508, 1.243656526796264, 0.7690961718484056, 2.2925213829410844, 0.5345816533903982, 3.2156284923921072};
        APtf tf = new AP4sine();
        
        System.out.println(tf.fitness(vector));
        System.out.println(tf.ap.equation);
        
    }
    
}
