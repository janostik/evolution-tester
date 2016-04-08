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
public class APquintic extends APtf {

    double[][] points = new double[][]{{-1.,0.},{-0.959184,-0.00613366},{-0.918367,-0.022522},{-0.877551,-0.0463838},{-0.836735,-0.0752434},{-0.795918,-0.106918},{-0.755102,-0.139502},{-0.714286,-0.171357},{-0.673469,-0.201095},{-0.632653,-0.227565},{-0.591837,-0.249843},{-0.55102,-0.267212},{-0.510204,-0.279155},{-0.469388,-0.285338},{-0.428571,-0.285595},{-0.387755,-0.27992},{-0.346939,-0.268446},{-0.306122,-0.251437},{-0.265306,-0.229272},{-0.22449,-0.202433},{-0.183673,-0.17149},{-0.142857,-0.137086},{-0.102041,-0.0999269},{-0.0612245,-0.0607664},{-0.0204082,-0.0203912},{0.0204082,0.0203912},{0.0612245,0.0607664},{0.102041,0.0999269},{0.142857,0.137086},{0.183673,0.17149},{0.22449,0.202433},{0.265306,0.229272},{0.306122,0.251437},{0.346939,0.268446},{0.387755,0.27992},{0.428571,0.285595},{0.469388,0.285338},{0.510204,0.279155},{0.55102,0.267212},{0.591837,0.249843},{0.632653,0.227565},{0.673469,0.201095},{0.714286,0.171357},{0.755102,0.139502},{0.795918,0.106918},{0.836735,0.0752434},{0.877551,0.0463838},{0.918367,0.022522},{0.959184,0.00613366},{1.,0.}};

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
        return "AP_quintic_function";
    }

    public static void main(String[] args) throws Exception {
    
        double[] vector = new double[]{1,2,13,11,11,14,14,14,20,20,20,20,20,20,20,20,20,20,20,20};
        APtf tf = new APquintic();
        
        System.out.println(tf.fitness(vector));
        
    }
    
}
