package model.tf;

import model.Individual;
import util.IndividualUtil;
import util.RandomUtil;

/**
 * Created by jakub on 27/10/15.
 */
public class Ackley implements TestFunction {

    @Override
    public double fitness(Individual individual) {
        return fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {
        double D = vector.length;
        double s1 = 0;
        double s2 = 0;

        for (int i = 0; i < vector.length; i++) {
            s1 += vector[i] * vector[i];
            s2 += Math.cos(2 * Math.PI * vector[i]);
        }
        return -20 * Math.exp(-0.2 * Math.sqrt((1 / D) * s1)) - Math.exp((1 / D) * s2) + 20 + Math.E;
    }

    @Override
    public void constrain(Individual individual) {
        IndividualUtil.clipInBounds(individual, -32, 32);
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] vector = new double[dim];
        for (int i = 0; i < dim; i++) vector[i] = RandomUtil.nextDouble(-32, 32);
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
        return 32;
    }

    @Override
    public double min(int dim) {
        return -32;
    }

    @Override
    public String name() {
        return "Ackley";
    }
}
