package algorithm.de;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Ackley;
import model.tf.Dejong;
import model.tf.Rastrigin;
import model.tf.Rosenbrock;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.random.Random;

/**
 *
 * Canonical DE with score added for each individual - for clustering purposes
 * Weights are added to the score of each individual
 * w_x = 1-CR
 * w_1 = CR/(1+2F)
 * w_2 = w_3 = F*CR/(1+2F)
 * 
 * Sum of the weights = 1.
 * 
 * @author wiki on 29/11/2016
 */
public class DE_hbrs2 extends DE_hbrs {

    public double w_x, w_1, w_2, w_3;
    
    public DE_hbrs2(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR, int favor, int punish) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR, favor, punish);
        this.w_x = 1 - this.CR;
        this.w_1 = this.CR/(1.0+2*this.F);
        this.w_2 = this.F*this.CR/(1.0+2*this.F);
        this.w_3 = this.F*this.CR/(1.0+2*this.F);
    }
    
    @Override
    public Individual runAlgorithm() {

        /**
         * Initial population
         */
        initializePopulation();
        if (checkFES()) {
            return best;
        }
        
        /**
         * Ranking initialization
         */
        this.initializeRankHistory();
        this.writeToRankHistory();

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
                    parrentArray[0].score += this.w_x*this.favor;
                    parrentArray[1].score += this.w_1*this.favor;
                    parrentArray[2].score += this.w_2*this.favor;
                    parrentArray[3].score += this.w_3*this.favor;
                } else {
                    newPop.add(x);
                    /**
                     * Added for history based random selection
                     */
                    if(parrentArray[1].score > this.punish) {
                        parrentArray[1].score -= this.punish;
                    }
                    if(parrentArray[2].score > this.punish) {
                        parrentArray[2].score -= this.punish;
                    }
                    if(parrentArray[2].score > this.punish) {
                        parrentArray[2].score -= this.punish;
                    }
                }
                this.score_total = this.countScoreTotal();

            }

            P = newPop;
            this.writeToRankHistory();

        }
    }
    
    /**
     * 
     * @param tfs
     * @param dimension
     * @param NP
     * @param iter 
     */
    public static void writeStatisticsOfScore(TestFunction[] tfs, int dimension, int NP, int iter) {

        
        for(int i = 0; i < tfs.length; i++) {

            int MAXFES = iter * NP;
            util.random.Random generator = new util.random.UniformRandom();
            util.random.Random chaos = new util.random.UniformRandom();
            double f = 0.5, cr = 0.8;
            int favor = 1, punish = 0;
            String path;
            String path2;

            DE_hbrs2 de;

            int runs = 30;
            double[] bestArray = new double[runs];

            for (int k = 0; k < runs; k++) {

                path = "C:\\Users\\wiki\\Documents\\RankHistory\\HBRS2_" + favor + "f" + punish + "p\\" + tfs[i].name() + "_h_" + dimension + "d_" + NP + "ind_" + k + ".txt";
                path2 = "C:\\Users\\wiki\\Documents\\RankHistory\\HBRS2_" + favor + "f" + punish + "p\\" + tfs[i].name() + "_v_" + dimension + "d_" + NP + "ind_" + k + ".txt";

                de = new DE_hbrs2(dimension, NP, MAXFES, tfs[i], generator, f, cr, favor, punish);

                de.runAlgorithm();

                bestArray[k] = de.getBest().fitness - tfs[i].optimum();
                System.out.println(de.getBest().fitness - tfs[i].optimum());

                de.writeRankHistoryToFile(path);
                de.writeFitnessValueHistoryToFile(path2);

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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
                int dimension;
                int NP;
                int iter;
                TestFunction[] tfs;
                iter = 1000;

//                int[] NPs = {20,30,40,50,60,70,80,90,100};
//                int[] dims = {10,30,50};
//
//                writeCECstatistics(dims, NPs, iter);

                dimension = 5;
                NP = 20;
                tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 10;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 20;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 5;
                NP = 30;
                tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 10;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 20;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 5;
                NP = 30;
                tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 10;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 20;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 5;
                NP = 40;
                tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 10;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 20;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 5;
                NP = 50;
                tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 10;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 20;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 5;
                NP = 60;
                tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 10;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 20;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 5;
                NP = 70;
                tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 10;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 20;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 5;
                NP = 80;
                tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 10;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 20;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 5;
                NP = 90;
                tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 10;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 20;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 5;
                NP = 100;
                tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 10;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
                dimension = 20;
                writeStatisticsOfScore(tfs, dimension, NP, iter);
        
    }

    
    
}
