package algorithm.de;

import algorithm.Algorithm;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.net.Edge;
import model.net.Net;
import model.tf.Ackley;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.random.Random;

/**
 *
 * @author adam on 19/01/2016
 */
public class NetCDEbest extends NetCDErand1bin {

    public NetCDEbest(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR, Random chaosGenerator) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR, chaosGenerator);
    }
    
    /**
     *
     * List of parents for mutation x, a, b, c
     *
     * @param xIndex
     * @return
     */
    @Override
    protected Individual[] getParents(int xIndex) {

        Individual[] parrentArray = new Individual[4];
        List<Integer> indexes = new ArrayList<>();
        int index;
        
        parrentArray[0] = P.get(xIndex);
        
        /**
         * best
         */
        index = getBestIndividualInPopulation();
        parrentArray[1] = P.get(index);

        for (int i = 0; i < NP; i++) {
            if (i != xIndex && i != index) {
                indexes.add(i);
            }
        }

        /**
         * b
         */
        index = rndGenerator.nextInt(indexes.size());
        parrentArray[2] = P.get(indexes.get(index));

        indexes.remove(index);

        /**
         * c
         */
        index = rndGenerator.nextInt(indexes.size());
        parrentArray[3] = P.get(indexes.get(index));

        return parrentArray;

    }

    /**
     *
     * @return
     */
    private int getBestIndividualInPopulation() {

        double fitness = Double.MAX_VALUE;
        int index = -1;
        int i = 0;

        for (Individual ind : P) {

            if (ind.fitness < fitness) {
                fitness = ind.fitness;
                index = i;
            }
            i++;
        }

        return index;

    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        
        int dimension = 30;
        int NP = 50;
        int iter = 50;
        int MAXFES = iter * NP;
        int funcNumber = 5;
        TestFunction tf = new Schwefel();
        util.random.Random generator = new util.random.UniformRandom();
        util.random.Random chaos = new util.random.DissipativeRandom();
        double f = 0.5, cr = 0.8;
        Net net;

        Algorithm de;

        int runs = 1;
        double[] bestArray = new double[runs];
        PrintWriter pw;

        for (int k = 0; k < runs; k++) {

            de = new NetCDEbest(dimension, NP, MAXFES, tf, generator, f, cr, chaos);

            de.run();
            
            System.out.println("Node");
            System.out.println("ID: " + ((NetCDEbest) de).net.getNodeWithHighestDegree().id);
            System.out.println("Fitness: " + ((NetCDEbest) de).net.getNodeWithHighestDegree().fitness);
            System.out.println("Degree: " + ((NetCDEbest) de).net.getHighestDegree());
            System.out.println("Edges: " + ((NetCDEbest) de).net.getEdges().size());

            bestArray[k] = de.getBest().fitness;
            System.out.println(de.getBest().fitness);
            
           /**
            * NET manipulating
            */

           pw = new PrintWriter("/Users/adam/Documents/RomanData/DEbest/net_50iter_" + tf.name() + "_" + chaos.toString() + ".csv");

           pw.println("source,target,iter;directed");
           
           net = ((NetCDErand1bin) de).net;

           for(Edge edge : net.getEdges()){
               
               pw.println(edge.getSource().id + "," + edge.getTarget().id + "," + edge.iter + ",TRUE");
               
           }

           pw.close();
            
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
