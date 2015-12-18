package model.tf.ap;

import model.Individual;
import util.RandomUtil;

/**
 *
 * ACKLEY by POINTS {-5, 5}
 * 
 * @author adam
 */
public class APsextic extends APtf {

    double[][] points = new double[][]{{-1., 0.}, {-0.96, 0.00566467}, {-0.92, 0.0199691}, {-0.88, 
  0.0394134}, {-0.84, 0.0611553}, {-0.8, 0.082944}, {-0.76, 
  0.103056}, {-0.72, 0.120237}, {-0.68, 0.13364}, {-0.64, 
  0.142775}, {-0.6, 0.147456}, {-0.56, 0.147751}, {-0.52, 
  0.143938}, {-0.48, 0.136462}, {-0.44, 0.125894}, {-0.4, 
  0.112896}, {-0.36, 0.0981845}, {-0.32, 0.0825022}, {-0.28, 
  0.0665888}, {-0.24, 0.0511556}, {-0.2, 0.036864}, {-0.16, 
  0.0243061}, {-0.12, 0.0139883}, {-0.08, 0.00631834}, {-0.04, 
  0.00159488}, {0., 0.}, {0.04, 0.00159488}, {0.08, 
  0.00631834}, {0.12, 0.0139883}, {0.16, 0.0243061}, {0.2, 
  0.036864}, {0.24, 0.0511556}, {0.28, 0.0665888}, {0.32, 
  0.0825022}, {0.36, 0.0981845}, {0.4, 0.112896}, {0.44, 
  0.125894}, {0.48, 0.136462}, {0.52, 0.143938}, {0.56, 
  0.147751}, {0.6, 0.147456}, {0.64, 0.142775}, {0.68, 
  0.13364}, {0.72, 0.120237}, {0.76, 0.103056}, {0.8, 
  0.082944}, {0.84, 0.0611553}, {0.88, 0.0394134}, {0.92, 
  0.0199691}, {0.96, 0.00566467}, {1., 0.}};

    @Override
    protected double getDistance(double[] vector) {

        double sum = 0, a, b;
        
        for (double[] point : points) {
            a = ap.dsh(vector, point[0]);
            if(Double.isNaN(a) || Double.isInfinite(a)){
                return Double.MAX_VALUE;
            }
            b = point[1];
            sum += Math.pow(a-b, 2);
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
    public double[] generateTrial(int dim) {
        double[] trial = new double[dim];
        for (int i = 0; i < dim; i++) {
            trial[i] = RandomUtil.nextInt((int) (this.max(dim) + 1));
        }
        return trial;
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
    public double max(int dim) {
        return this.ap.getGFSsize();
    }

    @Override
    public double min(int dim) {
        return 0;
    }

    @Override
    public String name() {
        return "AP_sin_function";
    }

    public static void main(String[] args) throws Exception {
    
        double[] vector = new double[]{2.78997802734375, 5.1953125, 7.0, 2.748046875, 0.15625, 0.0, 2.890625, 5.3125, 1.96484375, 0.3802490234375, 3.111328125, 5.921875, 0.75, 6.90234375, 3.59375, 0.0, 6.28515625, 3.748046875, 6.226318359375, 3.03125};
        APtf tf = new APsextic();
        
        System.out.println(tf.fitness(vector));
        
    }
    
}
