package model.tf.ap;

import model.Individual;
import util.RandomUtil;

/**
 *
 * @author adam
 */
public class APtest3 extends APtf {

    double[][] points = new double[][]{{0.,0.025558},{0.1,0.309421},{0.2,0.576137},{0.3,1.25146},{0.4,0.821847},
        {0.5,1.60975},{0.6,1.86817},{0.7,1.61597},{0.8,1.84074},{0.9,1.9345},{1.,2.33832},{1.1,2.72861},{1.2,2.41402},
        {1.3,2.58726},{1.4,3.00383},{1.5,3.41608},{1.6,3.32458},{1.7,3.6837},{1.8,3.61636},{1.9,3.01111},{2.,3.18687},
        {2.1,3.90113},{2.2,3.52191},{2.3,3.83007},{2.4,3.61809},{2.5,3.42586},{2.6,3.25226},{2.7,4.06768},{2.8,3.94398},
        {2.9,3.56281},{3.,3.79732},{3.1,3.95058},{3.2,3.68319},{3.3,4.12204},{3.4,3.42969},{3.5,3.5945},{3.6,3.49221},
        {3.7,3.90953},{3.8,3.64563},{3.9,3.30313},{4.,4.02242},{4.1,3.40968},{4.2,3.73951},{4.3,3.67926},{4.4,3.72934},
        {4.5,3.64336},{4.6,4.5613},{4.7,4.30702},{4.8,4.33471},{4.9,4.52348},{5.,4.55282},{5.1,4.51327},{5.2,5.06883},
        {5.3,5.34773},{5.4,4.65063},{5.5,5.29373},{5.6,5.68374},{5.7,5.91195},{5.8,5.85546},{5.9,5.8614},{6.,6.58645},
        {6.1,6.60575},{6.2,6.66998},{6.3,6.61395},{6.4,7.44607},{6.5,7.28114},{6.6,7.28965},{6.7,7.59973},{6.8,7.47134},
        {6.9,7.68673},{7.,7.8703},{7.1,8.55037},{7.2,8.11607},{7.3,8.44058},{7.4,8.72961},{7.5,8.69643},{7.6,9.22348},
        {7.7,9.6571},{7.8,9.11024},{7.9,9.02398},{8.,9.54197},{8.1,9.18387},{8.2,9.21094},{8.3,9.96259},{8.4,10.1074},
        {8.5,10.2579},{8.6,9.68969},{8.7,9.38334},{8.8,10.2642},{8.9,9.96242},{9.,9.49466},{9.1,10.3309},{9.2,10.2643},
        {9.3,9.42758},{9.4,10.1512},{9.5,9.70679},{9.6,10.0728},{9.7,9.6076},{9.8,9.48461},{9.9,9.70957},{10.,9.74695}};
    
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
        APtf tf = new APtest3();
        
        System.out.println(tf.fitness(vector));
        
    }
    
}
