package algorithm.pso;

import algorithm.Algorithm;
import model.Individual;
import model.TestFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakub on 27/10/15.
 */
public class Pso implements Algorithm {

    List<Particle> particles = new ArrayList<>();
    Particle best;
    TestFunction testFunction;
    // Constants
    int swarmSize, iterationLimit, dim;

    public Pso(int swarmSize, int iterationLimit, int dim, TestFunction testFunction) {
        this.swarmSize = swarmSize;
        this.iterationLimit = iterationLimit;
        this.dim = dim;
        this.testFunction = testFunction;

        for (int i = 0; i < swarmSize; i++) {
            double[] vector = testFunction.generateTrial(dim);
            particles.add(new Particle(i, vector, testFunction.fitness(vector)));
        }
        best = (Particle) getBest();
    }

    @Override
    public Individual run() {
        return best;
    }

    @Override
    public List<? extends Individual> getPopulation() {
        return particles;
    }

    @Override
    public TestFunction getTestFunction() {
        return testFunction;
    }

    @Override
    public String getName() {
        return "Basic PSO";
    }

    private class Particle extends Individual {

        double[] bestVector;
        double bestFitness;

        public Particle(int id, double[] vector, double fitness) {
            super(id, vector, fitness);
            bestVector = vector;
            bestFitness = fitness;
        }
    }
}
