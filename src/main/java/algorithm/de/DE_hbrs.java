package algorithm.de;

import algorithm.Algorithm;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.net.Net;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.random.Random;

/**
 *
 * @author wiki
 */
public class DE_hbrs extends DErand1bin {
    
    public int score_total;
    
    public DE_hbrs(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
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

        /**
         * generation itteration
         */
        while (true) {

            this.score_total = this.countScoreTotal();
            
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
                trial = makeIndividualFromVector(v);
                if (checkFES()) {
                    return best;
                }

                /**
                 * New generation building
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                    /**
                     * Added for history based random selection
                     */
                    parrentArray[1].score += 1;
                    parrentArray[2].score += 1;
                    parrentArray[3].score += 1;
                } else {
                    newPop.add(x);
                    /**
                     * Added for history based random selection
                     */
                    if(parrentArray[1].score > 1) {
                        parrentArray[1].score -= 1;
                    }
                    if(parrentArray[2].score > 1) {
                        parrentArray[2].score -= 1;
                    }
                    if(parrentArray[2].score > 1) {
                        parrentArray[2].score -= 1;
                    }
                }
                this.score_total = this.countScoreTotal();

            }

            P = newPop;

        }
    }
    
    /**
     *
     * List of parents for mutation x, a, b, c
     * 
     * a, b, c are randomly selected, but the selection is biased towards the individuals whose score is higher.
     *
     * @param xIndex
     * @return
     */
    @Override
    protected Individual[] getParents(int xIndex) {

        Individual[] parrentArray = new Individual[4];
        List<Integer> indexes = new ArrayList<>();
        int index, score_index, score_max;

        for (int i = 0; i < NP; i++) {
            if (i != xIndex) {
                indexes.add(i);
            }
        }

        parrentArray[0] = P.get(xIndex);
        score_max = this.score_total - parrentArray[0].score;

        /**
         * a
         */
        score_index = rndGenerator.nextInt(score_max) + 1;
        for(index = 0; index < indexes.size(); index++) {
            
            score_index -= (P.get(indexes.get(index))).score;
            if(score_index <= 0) {
                break;
            }
            
        }
        parrentArray[1] = P.get(indexes.get(index));
        indexes.remove(index);
        score_max -= parrentArray[1].score;

        /**
         * b
         */
        score_index = rndGenerator.nextInt(score_max) + 1;
        for(index = 0; index < indexes.size(); index++) {
            
            score_index -= (P.get(indexes.get(index))).score;
            if(score_index <= 0) {
                break;
            }
            
        }
        parrentArray[2] = P.get(indexes.get(index));
        indexes.remove(index);
        score_max -= parrentArray[2].score;

        /**
         * c
         */
        score_index = rndGenerator.nextInt(score_max) + 1;
        for(index = 0; index < indexes.size(); index++) {
            
            score_index -= (P.get(indexes.get(index))).score;
            if(score_index <= 0) {
                break;
            }
            
        }
        parrentArray[3] = P.get(indexes.get(index));

        return parrentArray;

    }
   /**
    * Sum score of the whole population.
    * 
    * @return 
    */
    protected int countScoreTotal() {
        
        int score = 0;
        
        score = this.P.stream().map((ind) -> ind.score).reduce(score, Integer::sum);
        
        return score;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int dimension = 10;
        int NP = 20;
        int iter = 100;
        int MAXFES = iter * NP;
        int funcNumber = 5;
        TestFunction tf = new Schwefel();
        util.random.Random generator = new util.random.UniformRandom();
        util.random.Random chaos = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8;
        Net net;

        Algorithm de;

        int runs = 30;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new DE_hbrs(dimension, NP, MAXFES, tf, generator, f, cr);

            de.run();

            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
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
