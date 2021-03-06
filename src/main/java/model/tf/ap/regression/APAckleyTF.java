package model.tf.ap.regression;

import model.Individual;
import model.tf.Ackley;
import model.tf.ap.APtf;
import util.RandomUtil;

/**
 *
 * @author adam
 */
public class APAckleyTF extends APtf {

    @Override
    public double fitness(Individual individual) {
        return this.fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {

//        Integer[] discrete = this.discretizeVector(vector);

        double min = -32, max = 32, x;
        double fitness = 0;

        for (int i = 0; i < 101; i++) {

            x = min + ((max - min) / 100.0) * i;
            fitness += this.getDistance(x, vector);

        }

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

    protected double getDistance(double x, double[] vector) {

        Ackley ack = new Ackley();
        double[] array = new double[]{x};
        
        return Math.pow(ap.dsh(vector, x) - ack.fitness(array), 2);

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

}
