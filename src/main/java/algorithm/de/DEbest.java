package algorithm.de;

import algorithm.Algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Cec2015;
import model.tf.nwf.Network2;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.random.Random;

/**
 *
 * @author adam on 30/11/2015
 */
public class DEbest extends DErand1bin {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        int dimension = 40;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 5;
        TestFunction tf = new Network2();
        util.random.Random generator = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8, min;

        Algorithm de;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new DEbest(dimension, NP, MAXFES, tf, generator, f, cr);

            de.run();

            bestArray[k] = de.getBest().fitness;
            System.out.println("Individual: " + Arrays.toString(de.getBest().vector));
            System.out.println("Profit: " + de.getBest().fitness);
            tf.fitness(de.getBest());
            System.out.println("Path: " + ((Network2)tf).getNode_path().toString());
            System.out.println("Built paths: ");
            ((Network2)tf).getBuilt_path().stream().forEach((pa) -> {
                System.out.println("[" + pa[0] + ", " + pa[1] + "]");
            });
            
            for(Individual ind : ((DEbest)de).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");

//            System.out.println(de.getBest().fitness - ((DErand1bin) de).getBestHistory().get(MAXFES-1).fitness);
            
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
        

    }

    public DEbest(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
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

}
