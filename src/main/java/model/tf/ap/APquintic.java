package model.tf.ap;

import model.Individual;
import util.RandomUtil;

/**
 *
 * ACKLEY by POINTS {-5, 5}
 * 
 * @author adam
 */
public class APquintic extends APtf {

    double[][] points = new double[][]{{-1., 0.}, {-0.96, -0.0059007}, {-0.92, -0.0217055}, {-0.88, 
-0.0447879}, {-0.84, -0.0728039}, {-0.8, -0.10368}, {-0.76, 
-0.135601}, {-0.72, -0.166996}, {-0.68, -0.196529}, {-0.64, 
-0.223086}, {-0.6, -0.24576}, {-0.56, -0.263841}, {-0.52, -0.276804}, 
{-0.48, -0.284296}, {-0.44, -0.286124}, {-0.4, -0.28224}, {-0.36, 
-0.272735}, {-0.32, -0.257819}, {-0.28, -0.237817}, {-0.24, 
-0.213148}, {-0.2, -0.18432}, {-0.16, -0.151913}, {-0.12, -0.116569}, 
{-0.08, -0.0789793}, {-0.04, -0.0398721}, {0., 0.}, {0.04, 
  0.0398721}, {0.08, 0.0789793}, {0.12, 0.116569}, {0.16, 
  0.151913}, {0.2, 0.18432}, {0.24, 0.213148}, {0.28, 
  0.237817}, {0.32, 0.257819}, {0.36, 0.272735}, {0.4, 
  0.28224}, {0.44, 0.286124}, {0.48, 0.284296}, {0.52, 
  0.276804}, {0.56, 0.263841}, {0.6, 0.24576}, {0.64, 
  0.223086}, {0.68, 0.196529}, {0.72, 0.166996}, {0.76, 
  0.135601}, {0.8, 0.10368}, {0.84, 0.0728039}, {0.88, 
  0.0447879}, {0.92, 0.0217055}, {0.96, 0.0059007}, {1., 0.}};

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
    
        double[] vector = new double[]{1,2,13,11,11,14,14,14,20,20,20,20,20,20,20,20,20,20,20,20};
        APtf tf = new APquintic();
        
        System.out.println(tf.fitness(vector));
        
    }
    
}
