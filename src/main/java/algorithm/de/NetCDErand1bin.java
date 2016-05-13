package algorithm.de;

import algorithm.Algorithm;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.net.Edge;
import model.net.Net;
import model.net.UnidirectionalEdge;
import model.tf.Ackley;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.random.Random;

/**
 *
 * @author adam on 18/01/2016
 */
public class NetCDErand1bin extends CDErand1bin {

    Net net = new Net();

    public NetCDErand1bin(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR, Random chaosGenerator) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR, chaosGenerator);
    }

    @Override
    public Individual run() {

        /**
         * Initial population
         */
        initializePopulation();
        if (checkFES()) {
            return best;
        }

        List<Individual> newPop;
        Individual x, trial;
        double[] u, v;
        Individual[] parrentArray;
        Edge edge;

        /**
         * generation itteration
         */
        while (true) {

            G++;
            newPop = new ArrayList<>();

            /**
             * Iteration through all individuals in generation.
             */
            for (int xIter = 0; xIter < NP; xIter++) {

                /**
                 * Parent selection
                 */
                parrentArray = getParents(xIter);
                x = parrentArray[0];

                /**
                 * Mutation
                 */
                u = mutation(parrentArray, F);

                /**
                 * Crossover
                 */
                v = crossover(x.vector, u, CR);

                /**
                 * Trial
                 */
                trial = makeIndividualFromVector(v, parrentArray[0].id);
                if (checkFES()) {
                    return best;
                }

                /**
                 * New generation building
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                    
                    for (int par = 1; par < parrentArray.length; par++ ) {
                        edge = new UnidirectionalEdge(parrentArray[par], trial);
                        edge.iter = G;
                        net.addEdge(edge);
                    }
                    
                } else {
                    newPop.add(x);
                }

            }

            P = newPop;

        }
    }
    
    /**
     *
     * @param vector
     * @return
     */
    protected Individual makeIndividualFromVector(double[] vector, String id) {

        Individual ind = new Individual();
        ind.id = id;
//        id++;
        ind.vector = vector;
        constrain(ind);
        ind.fitness = tf.fitness(vector);
        FES++;
        isBest(ind);
        writeHistory();

        return (ind);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 30;
        int NP = 50;
        int iter = 50;
        int MAXFES = iter * NP;
        int funcNumber = 5;
        TestFunction tf = new Schwefel();
        util.random.Random generator = new util.random.UniformRandom();
        util.random.Random chaos = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8;
        Net net;

        Algorithm de;

        int runs = 10;
        double[] bestArray = new double[runs];
        PrintWriter pw_start, pw_middle, pw_end;

        for (int k = 0; k < runs; k++) {

            de = new NetCDErand1bin(dimension, NP, MAXFES, tf, generator, f, cr, chaos);

            de.run();
            
            System.out.println("Node");
            System.out.println("ID: " + ((NetCDErand1bin) de).net.getNodeWithHighestDegree().id);
            System.out.println("Fitness: " + ((NetCDErand1bin) de).net.getNodeWithHighestDegree().fitness);
            System.out.println("Degree: " + ((NetCDErand1bin) de).net.getHighestDegree());

            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
           /**
            * NET manipulating
            */

            pw_start = new PrintWriter("C:\\Users\\wiki\\Dropbox\\PhD\\NetData\\RomanData\\newData\\DErand\\start_" + tf.name() + "_" + chaos.toString() + "_" + (k+1) + ".csv");
            pw_middle = new PrintWriter("C:\\Users\\wiki\\Dropbox\\PhD\\NetData\\RomanData\\newData\\DErand\\middle_" + tf.name() + "_" + chaos.toString() + "_" + (k+1) + ".csv");
            pw_end = new PrintWriter("C:\\Users\\wiki\\Dropbox\\PhD\\NetData\\RomanData\\newData\\DErand\\end_" + tf.name() + "_" + chaos.toString() + "_" + (k+1) + ".csv");
           
            pw_start.println("source,target,iter,directed");
            pw_middle.println("source,target,iter,directed");
            pw_end.println("source,target,iter,directed");

            net = ((NetCDErand1bin) de).net;

            for(Edge edge : net.getEdges()){

                if(edge.iter < 11){
                    pw_start.println(edge.getSource().id + "," + edge.getTarget().id + "," + edge.iter + ",TRUE");
                }
                else if(edge.iter > 19 && edge.iter < 30){
                    pw_middle.println(edge.getSource().id + "," + edge.getTarget().id + "," + edge.iter + ",TRUE");
                }
                else if(edge.iter > 39){
                    pw_end.println(edge.getSource().id + "," + edge.getTarget().id + "," + edge.iter + ",TRUE");
                }

            }

            pw_start.close();
            pw_middle.close();
            pw_end.close();
            
        }
        
        
        

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
    }

}
