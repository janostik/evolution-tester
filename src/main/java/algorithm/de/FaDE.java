package algorithm.de;

import algorithm.Algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.random.Random;

/**
 *
 * @author wiki on 06/10/2016
 */
public class FaDE extends DErand1bin {

    private final int H;
    public double[] Fmemory;
    
    public FaDE(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR, int H) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
        this.H = H;
        this.Fmemory = new double[this.H];
        for(int i = 0; i < this.H; i++) {
                this.Fmemory[i] = F;
        }
        
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
        int k = 0,r;
        List<Double> wS;
        List<Double> S_F;
        double Fg, newF;
        double wSsum, meanS_F1, meanS_F2;

        /**
         * generation itteration
         */
        while (true) {

            G++;
            newPop = new ArrayList<>();
            wS = new ArrayList<>();
            S_F = new ArrayList<>();

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
                 * F per dimension selection
                 */
                r = this.rndGenerator.nextInt(this.H);
                Fg = OtherDistributionsUtil.cauchy(this.Fmemory[r], 0.1);
                while (Fg <= 0) {
                    Fg = OtherDistributionsUtil.cauchy(this.Fmemory[r], 0.1);
                }
                if (Fg > 1) {
                    Fg = 1;
                }

                /**
                 * Mutation
                 */
                u = mutation(parrentArray, Fg);

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
                    
                    wS.add(x.fitness - trial.fitness);
                    S_F.add(Fg);
                    
                } else {
                    newPop.add(x);
                }

            }

            P = newPop;
            
            /**
             * Memory update
             */
            if (S_F.size() > 0) {
                wSsum = 0;
                for (Double num : wS) {
                    wSsum += num;
                }
                    
                meanS_F1 = 0;
                meanS_F2 = 0;

                for (int s = 0; s < S_F.size(); s++) {
                    meanS_F1 += (wS.get(s) / wSsum) * S_F.get(s) * S_F.get(s);
                    meanS_F2 += (wS.get(s) / wSsum) * S_F.get(s);
                }

                newF = (meanS_F1 / meanS_F2);

                this.Fmemory[k] = newF;

                k++;
                if (k >= this.H) {
                    k = 0;
                }
            }
            
            k++;
            if (k >= this.H) {
                k = 0;
            }

        }
    }
    
    /**
     *
     * @param parentArray
     * @param Farray
     * @return
     */
    protected double[] mutation(Individual[] parentArray, double[] Farray) {

        double[] u = new double[D];
        double[] a = parentArray[1].vector;
        double[] b = parentArray[2].vector;
        double[] c = parentArray[3].vector;

        for (int i = 0; i < D; i++) {

            u[i] = a[i] + Farray[i] * (b[i] - c[i]);

        }

        return u;

    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int dimension = 10;
        int NP = 20;
        int iter = 1000;
        int MAXFES = iter * NP;
        int funcNumber = 5;
        TestFunction tf = new Schwefel();
        util.random.Random generator = new util.random.UniformRandom();
        util.random.Random chaos = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8;
        int H = 1;
        

        Algorithm de;

        int runs = 30;
        double[] fStat = new double[runs];
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new FaDE(dimension, NP, MAXFES, tf, generator, f, cr, H);

            de.run();

            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            fStat[k] = ((FaDE)de).Fmemory[0];
            System.out.println(Arrays.toString(((FaDE)de).Fmemory));
            
        }
        
        
        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(fStat).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(fStat).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(fStat));
        System.out.println("Median: " + new Median().evaluate(fStat));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(fStat));
        System.out.println("=================================");

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
    }

    
    
}
