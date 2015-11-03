import algorithm.Algorithm;
import algorithm.pso.NetPso;
import model.tf.Ackley;
import model.tf.Cec2013;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * TODO: Will include program runner.
 * Created by jakub on 27/10/15.
 */
public class Main {

    private static final int reps = 51;
    private static final int swarmSize = 40;
    private static final int iterations = 100000;
    private static final int dim = 10;
    private static final double c1 = 2;
    private static final double c2 = 2;
    private static final double maxVelocity = 0.2;
    private static final TestFunction testFunction = new Ackley();

    private static final int degreeLimit = 40;//20
    private static final int repulsiveRoundsLimit = 20;//20
    private static final int maxRepulsionIteration = 2400;

    public static void main(String[] args) throws Exception {
        Algorithm algorithm = null;

        for (int funcNumber = 1; funcNumber <= 28; funcNumber++) {
            System.out.println("Test function;" + funcNumber);
//            System.out.println("--------------------------------------------------");


            double[] fitnesses = new double[reps];
//            for (int i = 0; i < reps; i++) {
//                algorithm = new Pso(swarmSize, iterations, dim, c1, c2, maxVelocity, new Cec2013(funcNumber));
//                fitnesses[i] = algorithm.run().fitness;
//            }
//            printAlgorithmName(algorithm);
//            printStats(fitnesses);
//
//            fitnesses = new double[reps];

            int[] roundsInIterations = new int[reps];
            int[] repulsiveCycles = new int[reps];
            double[][] gbestValues = new double[reps][1000];
            for (int i = 0; i < reps; i++) {
                algorithm = new NetPso(swarmSize, iterations, dim, c1, c2, maxVelocity, new Cec2013(funcNumber), degreeLimit, repulsiveRoundsLimit, maxRepulsionIteration);
                fitnesses[i] = algorithm.run().fitness;
                roundsInIterations[i] = ((NetPso) algorithm).totalIterationsInRepulsive;
                repulsiveCycles[i] = ((NetPso) algorithm).repulsiveCycles;
                gbestValues[i] = ((NetPso) algorithm).gbestValues;
            }

//            System.out.println("--------------------------------------------------");
//        printAlgorithmName(algorithm);
            printStats(fitnesses);
            System.out.println("Avg rounds in repulsive;" + IntStream.of(roundsInIterations).average().getAsDouble());
            System.out.println("Avg repulsive cycles;" + IntStream.of(repulsiveCycles).average().getAsDouble());
            StringBuilder builder = new StringBuilder();
            builder.append("gBest in every 100th CF evalution;");
            for (int i = 0; i < 1000; i++) {
                double avg = 0.0;
                for (int r = 0; r < reps; r++) {
                    avg += gbestValues[r][i];
                }
                builder.append(avg / reps).append(";");
            }
            System.out.println(builder.toString());
//            System.out.println("" + String.join(";", DoubleStream.of(((NetPso) algorithm).gbestValues).mapToObj(String::valueOf).collect(Collectors.toList())));
        }

    }

    public static void printStats(double[] values) {
        System.out.println("Min;" + DoubleStream.of(values).min().getAsDouble());
        System.out.println("Max;" + DoubleStream.of(values).max().getAsDouble());
        System.out.println("Mean;" + new Mean().evaluate(values));
        System.out.println("Median;" + new Median().evaluate(values));
        System.out.println("Std. Dev.;" + new StandardDeviation().evaluate(values));
    }

    public static void printAlgorithmName(Algorithm algorithm) {
        System.out.println(algorithm != null ? algorithm.getName() : "null");
    }
}
