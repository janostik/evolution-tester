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
import util.OtherDistributionsUtil;
import util.random.Random;

/**
 *
 * @author wiki on 13/07/2016
 */
public class FCRa_DE_hbps extends DE_hbps {

    double[] M_F;
    double[] M_CR;
    List<Double> S_F;
    List<Double> S_CR;
    int H;
    
    public FCRa_DE_hbps(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, int H, int favor, int punish) {
        super(D, NP, MAXFES, f, rndGenerator, 0.5, 0.5, favor, punish);
        this.H = H;
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
        
        this.M_F = new double[this.H];
        this.M_CR = new double[this.H];

        for (int h = 0; h < this.H; h++) {
            this.M_F[h] = 0.5;
            this.M_CR[h] = 0.5;
        }

        double Fg, CRg;
        double wSsum, meanS_F1, meanS_F2, meanS_CR;
        int k = 0;
        int r;
        List<Double> wS;
        
        List<Individual> newPop;
        Individual x, trial;
        double[] u, v;
        Individual[] parrentArray;

        /**
         * generation itteration
         */
        while (true) {

            this.score_total = this.countScoreTotal();
            
            this.S_F = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();
            
            G++;
            newPop = new ArrayList<>();

            /**
             * Iteration through all individuals in generation.
             */
            for (int xIter = 0; xIter < NP; xIter++) {

                /**
                 * Generation of F and CR values
                 */
                r = rndGenerator.nextInt(this.H);
                Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                while (Fg <= 0) {
                    Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                }
                if (Fg > 1) {
                    Fg = 1;
                }

                CRg = OtherDistributionsUtil.normal(this.M_CR[r], 0.1);
                if (CRg > 1) {
                    CRg = 1;
                }
                if (CRg < 0) {
                    CRg = 0;
                }
                
                /**
                 * Parent selection
                 */
                parrentArray = getParents(xIter);
                x = parrentArray[0];

                /**
                 * Mutation
                 */
                u = mutation(parrentArray, Fg);

                /**
                 * Crossover
                 */
                v = crossover(x.vector, u, CRg);

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
                    /**
                     * Added for update of F and CR memories
                     */
                    this.S_F.add(Fg);
                    this.S_CR.add(CRg);
                    wS.add(x.fitness - trial.fitness);
                    
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
            
            /**
             * Memories update
             */
            if (this.S_F.size() > 0) {
                wSsum = 0;
                for (Double num : wS) {
                    wSsum += num;
                }
                meanS_F1 = 0;
                meanS_F2 = 0;
                meanS_CR = 0;

                for (int s = 0; s < this.S_F.size(); s++) {
                    meanS_F1 += (wS.get(s) / wSsum) * this.S_F.get(s) * this.S_F.get(s);
                    meanS_F2 += (wS.get(s) / wSsum) * this.S_F.get(s);
                    meanS_CR += (wS.get(s) / wSsum) * this.S_CR.get(s);
                }

                this.M_F[k] = (meanS_F1 / meanS_F2);
                this.M_CR[k] = meanS_CR;

                k++;
                if (k >= this.H) {
                    k = 0;
                }
            }

            P = newPop;

        }
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
        int H = 5;
        int favor = 1, punish = 2;

        Algorithm de;

        int runs = 30;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new FCRa_DE_hbps(dimension, NP, MAXFES, tf, generator, H, favor, punish);

            de.runAlgorithm();

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
