package model.tf;

import model.Individual;
import util.RandomUtil;

import java.util.stream.DoubleStream;

/**
 * Created by jakub on 27/10/15.
 */
public class Dejong implements TestFunction {
    @Override
    public double fitness(Individual individual) {
        return fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {
        return DoubleStream.of(vector).map(d -> d * d).sum();
    }

    @Override
    public void constrain(Individual individual) {
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] vector = new double[dim];
        for (int i = 0; i < dim; i++) vector[i] = RandomUtil.nextDouble(-10, 10);
        return vector;
    }

    @Override
    public double fixedAccLevel() {
        return 10E-2;
    }

    @Override
    public double optimum() {
        return 0.0;
    }

    @Override
    public double max(int dim) {
        return 10;
    }

    @Override
    public double min(int dim) {
        return -10;
    }
}
