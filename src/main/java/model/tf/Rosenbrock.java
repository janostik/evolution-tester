package model.tf;

import model.tf.TestFunction;
import model.Individual;
import util.IndividualUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 * Created by jakub on 27/10/15.
 */
public class Rosenbrock implements TestFunction {

    @Override
    public double fitness(Individual individual) {
        return fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {

        double s = 0;

        for (int i = 0; i < vector.length - 1; i++) {
            s += ((100*Math.pow(vector[i+1] - Math.pow(vector[i],2), 2)) + Math.pow(vector[i] - 1, 2));

        }
        return s;
    }

    @Override
    public void constrain(Individual individual) {
        IndividualUtil.clipInBounds(individual, -2.048, 2.048);
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] vector = new double[dim];
        Random rnd = new UniformRandom();
        for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-2.048, 2.048);
        return vector;
    }

    @Override
    public double fixedAccLevel() {
        return 10E-7;
    }

    @Override
    public double optimum() {
        return 0.0;
    }

    @Override
    public double max(int dim) {
        return 2.048;
    }

    @Override
    public double min(int dim) {
        return -2.048;
    }

    @Override
    public String name() {
        return "Rosenbrock";
    }

    @Override
    public double[] optimumPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
