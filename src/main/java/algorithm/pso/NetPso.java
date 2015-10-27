package algorithm.pso;

import model.TestFunction;
import model.net.Net;
import model.net.UnidirectionalEdge;

import java.util.Arrays;

/**
 * Created by jakub on 27/10/15.
 */
public class NetPso extends Pso {

    //TEMP
    public int totalIterationsInRepulsive = 0;
    public int repulsiveCycles = 0;
    Net net = new Net();
    boolean isRepulsive = false;
    private int roundsInRepulsive = 0;
    private int degreeLimit = 50;
    private int repulsiveRoundsLimit = 15;
    private int repulsionEnabledIterations;

    public NetPso(int swarmSize, int iterationLimit, int dim, double c1, double c2, double velocityLimit, TestFunction testFunction, int degreeLimit, int repulsiveRoundsLimit, int repulsionEnabledIterations) {
        super(swarmSize, iterationLimit, dim, c1, c2, velocityLimit, testFunction);
        this.repulsiveRoundsLimit = repulsiveRoundsLimit;
        this.repulsionEnabledIterations = repulsionEnabledIterations;
        this.degreeLimit = degreeLimit;
    }

    @Override
    protected void updateParticle(Pso.Particle particle) {
        if (isRepulsive) {
            particle.repulseParticle(best, c1, c2, inertialWeight, velocityLimit);
            testFunction.constrain(particle);
        } else {

            particle.moveParticle(best, c1, c2, inertialWeight, velocityLimit);
            testFunction.constrain(particle);
            particle.fitness = testFunction.fitness(particle);
            if (particle.fitness < particle.bestFitness) {
                particle.bestVector = Arrays.copyOf(particle.vector, particle.vector.length);
                particle.bestFitness = particle.fitness;
                net.addEdge(new UnidirectionalEdge(particle, best));
            }
        }

    }

    @Override
    protected void finalizeIteration(int i) {
        for (Particle particle : particles) {
            particle.id = particle.id + i;
        }

        if (i > repulsionEnabledIterations) {
            isRepulsive = false;
            return;
        }
        if (roundsInRepulsive > repulsiveRoundsLimit) {
            roundsInRepulsive = 0;
            isRepulsive = false;
            return;
        }
        if (isRepulsive) {
            roundsInRepulsive++;
            totalIterationsInRepulsive++;
            return;
        }
        if (net.getHighestDegree() >= degreeLimit) {
            isRepulsive = true;
            net.removeEdgesForNode(net.getNodeWithHighestDegree());
            repulsiveCycles++;
        }
    }
}
