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
        util.random.Random chaos = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8;
        Net net;

        Algorithm de;

        int runs = 10;
        double[] bestArray = new double[runs];
        PrintWriter pw_start, pw_middle, pw_end;

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

            pw_start = new PrintWriter("C:\\Users\\wiki\\Dropbox\\PhD\\NetData\\RomanData\\newData\\DEbest\\start_" + tf.name() + "_" + chaos.toString() + "_" + (k+1) + ".csv");
            pw_middle = new PrintWriter("C:\\Users\\wiki\\Dropbox\\PhD\\NetData\\RomanData\\newData\\DEbest\\middle_" + tf.name() + "_" + chaos.toString() + "_" + (k+1) + ".csv");
            pw_end = new PrintWriter("C:\\Users\\wiki\\Dropbox\\PhD\\NetData\\RomanData\\newData\\DEbest\\end_" + tf.name() + "_" + chaos.toString() + "_" + (k+1) + ".csv");
           
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
