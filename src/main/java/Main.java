import algorithm.Algorithm;
import algorithm.pso.NetPso;
import algorithm.pso.Pso;
import model.Ackley;
import model.TestFunction;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * TODO: Will include program runner.
 * Created by jakub on 27/10/15.
 */
public class Main {

    private static final int reps = 30;
    private static final int swarmSize = 30;
    private static final int iterations = 2000;
    private static final int dim = 10;
    private static final double c1 = 1.63;
    private static final double c2 = 0.62;
    private static final double maxVelocity = 0.2;
    private static final TestFunction testFunction = new Ackley();

    private static final int degreeLimit = 20;
    private static final int repulsiveRoundsLimit = 20;
    private static final int maxRepulsionIteration = 1900;

    public static void main(String[] args) {
        Algorithm pso = new Pso(swarmSize, iterations, dim, c1, c2, maxVelocity, testFunction);


        double[] fitnesses = new double[reps];
        for (int i = 0; i < reps; i++) {
            fitnesses[i] = pso.run().fitness;
        }
        System.out.println("PSO");
        System.out.println("Best: " + DoubleStream.of(fitnesses).average().getAsDouble());

        Algorithm netPso = new NetPso(swarmSize, iterations, dim, c1, c2, maxVelocity, testFunction, degreeLimit, repulsiveRoundsLimit, maxRepulsionIteration);
        fitnesses = new double[reps];
        int[] roundsInIterations = new int[reps];
        int[] repulsiveCycles = new int[reps];
        for (int i = 0; i < reps; i++) {
            fitnesses[i] = netPso.run().fitness;
            roundsInIterations[i] = ((NetPso) netPso).totalIterationsInRepulsive;
            repulsiveCycles[i] = ((NetPso) netPso).repulsiveCycles;
        }
        System.out.println("--------------------------------------------------");
        System.out.println("NETPSO");
        System.out.println("Best: " + DoubleStream.of(fitnesses).average().getAsDouble());
        System.out.println("Avg rounds in repulsive: " + IntStream.of(roundsInIterations).average().getAsDouble());
        System.out.println("Repulsive cycles: " + IntStream.of(repulsiveCycles).average().getAsDouble());

    }
}
