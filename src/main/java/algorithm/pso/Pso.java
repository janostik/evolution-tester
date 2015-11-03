package algorithm.pso;

import algorithm.Algorithm;
import exception.CostFunctionEvaluationLimitException;
import model.Individual;
import model.tf.TestFunction;
import util.RandomUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakub on 27/10/15.
 */
public class Pso implements Algorithm {

    protected static final int CF_EVALUTION_LIMIT = 100000;

    public double[] gbestValues = new double[1000];

    List<Particle> particles = new ArrayList<>();
    Particle best;
    TestFunction testFunction;
    // Constants
    int swarmSize, iterationLimit, dim;

    int cfEvalutionCounter = 0;

    double c1, c2, velocityLimit, inertialWeight = 0.9, finalInertialWeight = 0.4;

    public Pso(int swarmSize, int iterationLimit, int dim, double c1, double c2, double velocityLimit, TestFunction testFunction) {
        this.swarmSize = swarmSize;
        this.iterationLimit = iterationLimit;
        this.dim = dim;
        this.testFunction = testFunction;
        this.c1 = c1;
        this.c2 = c2;
        this.velocityLimit = velocityLimit;

        for (int i = 0; i < swarmSize; i++) {
            double[] vector = testFunction.generateTrial(dim);
            particles.add(new Particle(String.valueOf(i), vector, testFunction.fitness(vector)));
        }
        best = new Particle((Particle) getBest());
    }

    @Override
    public Individual run() {
        for (int i = 0; i < iterationLimit; i++) {
            //Do iteration
            try {
//                particles.forEach(this::updateParticle);

                for (Particle particle : particles) {
                    updateParticle(particle, i);
                    if (cfEvalutionCounter > 0 && cfEvalutionCounter % 100 == 0) {
                        updateGBest();
                        gbestValues[cfEvalutionCounter / 100 - 1] = best.fitness;
                    }
                }


                updateGBest();
                if (shouldExit()) break;
                finalizeIteration(i);
            } catch (CostFunctionEvaluationLimitException ex) {
                updateGBest();
                break;
            }
        }
        return best;
    }

    private void updateGBest() {
        if (getBest().fitness < best.fitness) {
            best = new Particle((Particle) getBest());
        }
    }

    protected void finalizeIteration(int i) {
        //NOOP
    }

    protected boolean shouldExit() {
        return false;
    }

    protected void updateInertia(int i) {
        // Updating the inertial weight with linear decaiment
        inertialWeight = (inertialWeight - finalInertialWeight) * ((iterationLimit - i) / (double) iterationLimit) + finalInertialWeight;
    }

    protected void updateParticle(Particle particle, int iteration) throws CostFunctionEvaluationLimitException {
        particle.moveParticle(best, c1, c2, inertialWeight, velocityLimit);
        testFunction.constrain(particle);
        particle.fitness = testFunction.fitness(particle.vector);
        if (cfEvalutionCounter++ > CF_EVALUTION_LIMIT) throw new CostFunctionEvaluationLimitException();
        if (particle.fitness < particle.bestFitness) {
            particle.bestVector = Arrays.copyOf(particle.vector, particle.vector.length);
            particle.bestFitness = particle.fitness;
        }
        updateInertia(iteration);
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
        return "Basic PSO with inertial weight updated with linear decaiment";
    }

    protected class Particle extends Individual {

        double[] bestVector;
        double[] velocity;
        double bestFitness;


        public Particle(String id, double[] vector, double fitness) {
            super(id, vector, fitness);
            bestVector = vector;
            bestFitness = fitness;
            velocity = new double[vector.length];
            for (int i = 0; i < velocity.length; i++) velocity[i] = RandomUtil.nextDouble();
        }

        public Particle(Particle best) {
            super(best);
            this.bestVector = best.bestVector;
            this.bestFitness = best.bestFitness;
            this.velocity = best.velocity;
        }

        public void moveParticle(Particle best, double c1, double c2, double inertialWeight, double velocityLimit) {
            double r1 = RandomUtil.nextDouble();
            double r2 = RandomUtil.nextDouble();

            for (int i = 0; i < vector.length; i++) {
                velocity[i] = (inertialWeight * velocity[i])
                        + (c1 * r1 * (this.bestVector[i] - this.vector[i]))
                        + (c2 * r2 * (best.bestVector[i] - this.vector[i]));
                if ((testFunction.max(i) - testFunction.min(i)) * velocityLimit < velocity[i]) {
                    velocity[i] = (testFunction.max(i) - testFunction.min(i)) * velocityLimit;
                }
                vector[i] += velocity[i];
            }
        }

        public void repulseParticle(Particle best, double c1, double c2, double inertialWeight, double velocityLimit) {
            for (int i = 0; i < vector.length; i++) {
                velocity[i] = (inertialWeight * velocity[i])
                        - (c1 * RandomUtil.nextDouble() * (bestVector[i] - vector[i]))
                        - (c2 * RandomUtil.nextDouble() * (best.vector[i] - vector[i]));
                if ((testFunction.max(i) - testFunction.min(i)) * velocityLimit < velocity[i])
                    velocity[i] = (testFunction.max(i) - testFunction.min(i)) * velocityLimit;
                vector[i] = vector[i] + velocity[i];
            }
        }
    }
}
