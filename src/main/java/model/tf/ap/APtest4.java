package model.tf.ap;

import model.Individual;
import util.RandomUtil;

/**
 *
 * ACKLEY by POINTS {-5, 5}
 * 
 * @author adam
 */
public class APtest4 extends APtf {

    double[][] points = new double[][]{{-5., 12.6424}, {-4.9, 12.9664}, {-4.8, 13.6983}, {-4.7, 
  14.1716}, {-4.6, 14.3026}, {-4.5, 14.219}, {-4.4, 13.9773}, {-4.3, 
  13.5209}, {-4.2, 12.722}, {-4.1, 11.6639}, {-4., 11.0134}, {-3.9, 
  11.3045}, {-3.8, 12.0029}, {-3.7, 12.4418}, {-3.6, 12.5379}, {-3.5, 
  12.4187}, {-3.4, 12.1406}, {-3.3, 11.6471}, {-3.2, 10.8103}, {-3.1, 
  9.71369}, {-3., 9.02377}, {-2.9, 9.27462}, {-2.8, 9.93202}, {-2.7, 
  10.3291}, {-2.6, 10.3826}, {-2.5, 10.2198}, {-2.4, 9.89732}, {-2.3, 
  9.35844}, {-2.2, 8.47547}, {-2.1, 7.33165}, {-2., 6.5936}, {-1.9, 
  6.79535}, {-1.8, 7.40267}, {-1.7, 7.74871}, {-1.6, 7.75001}, {-1.5, 
  7.53404}, {-1.4, 7.15731}, {-1.3, 6.56308}, {-1.2, 5.62364}, {-1.1, 
  4.42221}, {-1., 3.62538}, {-0.9, 3.76718}, {-0.8, 4.31332}, {-0.7, 
  4.59695}, {-0.6, 4.53458}, {-0.5, 4.25365}, {-0.4, 3.81066}, {-0.3, 
  3.14882}, {-0.2, 2.14041}, {-0.1, 0.868609}, {0., 
  0.0}, {0.1, 0.868609}, {0.2, 2.14041}, {0.3, 
  3.14882}, {0.4, 3.81066}, {0.5, 4.25365}, {0.6, 4.53458}, {0.7, 
  4.59695}, {0.8, 4.31332}, {0.9, 3.76718}, {1., 3.62538}, {1.1, 
  4.42221}, {1.2, 5.62364}, {1.3, 6.56308}, {1.4, 7.15731}, {1.5, 
  7.53404}, {1.6, 7.75001}, {1.7, 7.74871}, {1.8, 7.40267}, {1.9, 
  6.79535}, {2., 6.5936}, {2.1, 7.33165}, {2.2, 8.47547}, {2.3, 
  9.35844}, {2.4, 9.89732}, {2.5, 10.2198}, {2.6, 10.3826}, {2.7, 
  10.3291}, {2.8, 9.93202}, {2.9, 9.27462}, {3., 9.02377}, {3.1, 
  9.71369}, {3.2, 10.8103}, {3.3, 11.6471}, {3.4, 12.1406}, {3.5, 
  12.4187}, {3.6, 12.5379}, {3.7, 12.4418}, {3.8, 12.0029}, {3.9, 
  11.3045}, {4., 11.0134}, {4.1, 11.6639}, {4.2, 12.722}, {4.3, 
  13.5209}, {4.4, 13.9773}, {4.5, 14.219}, {4.6, 14.3026}, {4.7, 
  14.1716}, {4.8, 13.6983}, {4.9, 12.9664}, {5., 12.6424}};
    
    @Override
    public double fitness(Individual individual) {
        return this.fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {

        Integer[] discrete = this.discretizeVector(vector);

        double fitness = 0;

        fitness = this.squaredDistance(discrete);

        return fitness;

    }

    public Integer[] discretizeVector(double[] vector) {
        int dim = vector.length;
        Integer[] discrete = new Integer[dim];

        for (int i = 0; i < dim; i++) {
            discrete[i] = (int) Math.round(vector[i]);
        }

        return discrete;
    }

    private double squaredDistance(Integer[] vector) {

        double sum = 0;
        
        for (double[] point : points) {
            sum += Math.pow(ap.dsh(vector, point[0]) - point[1], 2);
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
        APtf tf = new APtest4();
        
        System.out.println(tf.fitness(vector));
        
    }
    
}
