package model.tf.ap.regression;

import model.Individual;
import model.tf.ap.APtf;
import util.RandomUtil;

/**
 *
 * ACKLEY by POINTS {-5, 5}
 * 
 * @author adam
 */
public class APsextic extends APtf {

    double[][] points = new double[][]{{-1.,0.},{-0.959184,0.00588331},{-0.918367,0.0206835},{-0.877551,0.0407041},{-0.836735,0.0629588},{-0.795918,0.0850978},{-0.755102,0.105338},{-0.714286,0.122398},{-0.673469,0.135431},{-0.632653,0.14397},{-0.591837,0.147866},{-0.55102,0.147239},{-0.510204,0.142426},{-0.469388,0.133934},{-0.428571,0.122398},{-0.387755,0.10854},{-0.346939,0.0931342},{-0.306122,0.0769704},{-0.265306,0.0608273},{-0.22449,0.0454442},{-0.183673,0.0314981},{-0.142857,0.0195837},{-0.102041,0.0101966},{-0.0612245,0.00372039},{-0.0204082,0.000416146},{0.0204082,0.000416146},{0.0612245,0.00372039},{0.102041,0.0101966},{0.142857,0.0195837},{0.183673,0.0314981},{0.22449,0.0454442},{0.265306,0.0608273},{0.306122,0.0769704},{0.346939,0.0931342},{0.387755,0.10854},{0.428571,0.122398},{0.469388,0.133934},{0.510204,0.142426},{0.55102,0.147239},{0.591837,0.147866},{0.632653,0.14397},{0.673469,0.135431},{0.714286,0.122398},{0.755102,0.105338},{0.795918,0.0850978},{0.836735,0.0629588},{0.877551,0.0407041},{0.918367,0.0206835},{0.959184,0.00588331},{1.,0.}};

    @Override
    protected double getDistance(double[] vector) {

        double sum = 0, a, b;
        
        for (double[] point : points) {
            a = ap.dsh(vector, point[0]);
            if(Double.isNaN(a) || Double.isInfinite(a)){
                return Double.MAX_VALUE;
            }
            b = point[1];
            sum += Math.abs(a-b);
        }
        
        return sum;

    }

    @Override
    public void constrain(Individual individual) {

        for (int i = 0; i < individual.vector.length; i++) {

            if (individual.vector[i] > this.max(individual.vector.length)) {
                individual.vector[i] = this.max(individual.vector.length);
            }
            if (individual.vector[i] < this.min(individual.vector.length)) {
                individual.vector[i] = this.min(individual.vector.length);
            }

        }

    }

    @Override
    public double fixedAccLevel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double optimum() {
        return 0;
    }

    @Override
    public String name() {
        return "AP_sin_function";
    }

    public static void main(String[] args) throws Exception {
    
        double[] vector = new double[]{0.2, 5.1953125, 7.0, 2.748046875, 0.15625, 0.0, 2.890625, 5.3125, 1.96484375, 0.3802490234375, 3.111328125, 5.921875, 0.75, 6.90234375, 3.59375, 0.0, 6.28515625, 3.748046875, 0, -1};
        APtf tf = new APsextic();
        
        System.out.println(tf.fitness(vector));
        System.out.println(tf.ap.equation);
        System.out.println(tf.countLength(vector));
        
    }
    
}
