package model.tf.ap;

import model.Individual;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * EUR USD
 * 
 * @author adam
 */
public class AP4sine extends APtf {

    double[][] points = new double[][]{{-3.14159,0.},{-3.01593,0.236986},{-2.89027,0.392845},{-2.7646,0.409622},{-2.63894,0.269375},{-2.51327,0.},{-2.38761,-0.3317},{-2.26195,-0.638104},{-2.13628,-0.835347},{-2.01062,-0.867911},{-1.88496,-0.726543},{-1.75929,-0.454382},{-1.63363,-0.139096},{-1.50796,0.107617},{-1.3823,0.178463},{-1.25664,0.},{-1.13097,-0.444363},{-1.00531,-1.10397},{-0.879646,-1.86643},{-0.753982,-2.57842},{-0.628319,-3.07768},{-0.502655,-3.22894},{-0.376991,-2.95553},{-0.251327,-2.25932},{-0.125664,-1.2239},{0.,0.},{0.125664,1.2239},{0.251327,2.25932},{0.376991,2.95553},{0.502655,3.22894},{0.628319,3.07768},{0.753982,2.57842},{0.879646,1.86643},{1.00531,1.10397},{1.13097,0.444363},{1.25664,0.},{1.3823,-0.178463},{1.50796,-0.107617},{1.63363,0.139096},{1.75929,0.454382},{1.88496,0.726543},{2.01062,0.867911},{2.13628,0.835347},{2.26195,0.638104},{2.38761,0.3317},{2.51327,0.},{2.63894,-0.269375},{2.7646,-0.409622},{2.89027,-0.392845},{3.01593,-0.236986},{3.14159,0.}};

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
        
        return (new Median().evaluate(distance_array)) + (new Mean().evaluate(distance_array));

    }

    @Override
    public String name() {
        return "AP_ge_mathematica";
    }

    public static void main(String[] args) throws Exception {
    
        double[] vector = new double[]{2.78997802734375, 5.1953125, 7.0, 2.748046875, 0.15625, 0.0, 2.890625, 5.3125, 1.96484375, 0.3802490234375, 3.111328125, 5.921875, 0.75, 6.90234375, 3.59375, 0.0, 6.28515625, 3.748046875, 6.226318359375, 3.03125};
        APtf tf = new AP4sine();
        
        System.out.println(tf.fitness(vector));
        
    }
    
}
