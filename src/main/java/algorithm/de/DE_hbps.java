package algorithm.de;

import algorithm.Algorithm;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
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
public class DE_hbps extends DErand1bin {
    
    public double[] score_total;
    protected final double favor;
    protected final double punish;
    
    public DE_hbps(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR, int favor, int punish) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
        this.favor = favor;
        this.punish = punish;
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
                trial = makeIndividualFromVector(v, x);
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
                    parrentArray[1].score_pos[0] += this.favor;
                    parrentArray[2].score_pos[1] += this.favor;
                    parrentArray[3].score_pos[2] += this.favor;
                } else {
                    newPop.add(x);
                    /**
                     * Added for history based random selection
                     */
                    if(parrentArray[1].score_pos[0] > this.punish) {
                        parrentArray[1].score_pos[0] -= this.punish;
                    }
                    if(parrentArray[2].score_pos[1] > this.punish) {
                        parrentArray[2].score_pos[1] -= this.punish;
                    }
                    if(parrentArray[2].score_pos[2] > this.punish) {
                        parrentArray[2].score_pos[2] -= this.punish;
                    }
                }
                this.score_total = this.countScoreTotal();

            }

            P = newPop;

        }
    }
    
    /**
     *
     * @param vector
     * @param x
     * @return
     */
    protected Individual makeIndividualFromVector(double[] vector, Individual x) {

        Individual ind = new Individual();
        ind.id = x.id;
        ind.vector = vector;
        ind.score = x.score;
        ind.score_pos = x.score_pos.clone();
        constrain(ind);
        ind.fitness = tf.fitness(vector);
        FES++;
        isBest(ind);
        writeHistory();

        return (ind);
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
        int index, score_index;
        double[] score_max = new double[3];

        for (int i = 0; i < NP; i++) {
            if (i != xIndex) {
                indexes.add(i);
            }
        }

        parrentArray[0] = P.get(xIndex);
        score_max[0] = this.score_total[0] - parrentArray[0].score_pos[0];
        score_max[1] = this.score_total[1] - parrentArray[0].score_pos[1];
        score_max[2] = this.score_total[2] - parrentArray[0].score_pos[2];

        /**
         * a
         */
        score_index = rndGenerator.nextInt((int)score_max[0]) + 1;
        for(index = 0; index < indexes.size(); index++) {
            
            score_index -= (P.get(indexes.get(index))).score_pos[0];
            if(score_index <= 0) {
                break;
            }
            
        }
        parrentArray[1] = P.get(indexes.get(index));
        indexes.remove(index);
        score_max[1] -= parrentArray[1].score_pos[1];
        score_max[2] -= parrentArray[1].score_pos[2];

        /**
         * b
         */
        score_index = rndGenerator.nextInt((int)score_max[1]) + 1;
        for(index = 0; index < indexes.size(); index++) {
            
            score_index -= (P.get(indexes.get(index))).score_pos[1];
            if(score_index <= 0) {
                break;
            }
            
        }
        parrentArray[2] = P.get(indexes.get(index));
        indexes.remove(index);
        score_max[2] -= parrentArray[2].score_pos[2];

        /**
         * c
         */
        score_index = rndGenerator.nextInt((int)score_max[2]) + 1;
        for(index = 0; index < indexes.size(); index++) {
            
            score_index -= (P.get(indexes.get(index))).score_pos[2];
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
    protected double[] countScoreTotal() {
        
        double[] score = new double[]{0, 0, 0};
        
        this.P.stream().map((ind) -> {
            score[0] += ind.score_pos[0];
            return ind;
        }).map((ind) -> {
            score[1] += ind.score_pos[1];
            return ind;
        }).forEach((ind) -> {
            score[2] += ind.score_pos[2];
        });
        
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
        int favor = 1, punish = 1;

        Algorithm de;

        int runs = 30;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new DE_hbps(dimension, NP, MAXFES, tf, generator, f, cr, favor, punish);

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
