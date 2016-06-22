package model.tf;

import model.tf.TestFunction;
import model.Individual;
import util.IndividualUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 * Created by jakub on 27/10/15.
 */
public class Rastrigin implements TestFunction {

    @Override
    public double fitness(Individual individual) {
        return fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {
        double D = vector.length;
        double s = 0;

        for (int i = 0; i < vector.length; i++) {
            s += ((vector[i] * vector[i]) - 10 * Math.cos(2*Math.PI*vector[i]));

        }
        return 10*D + s;
    }

    @Override
    public void constrain(Individual individual) {
        IndividualUtil.clipInBounds(individual, -5.12, 5.12);
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] vector = new double[dim];
        Random rnd = new UniformRandom();
        for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-5.12, 5.12);
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
        return 5.12;
    }

    @Override
    public double min(int dim) {
        return -5.12;
    }

    @Override
    public String name() {
        return "Rastrigin";
    }
}
