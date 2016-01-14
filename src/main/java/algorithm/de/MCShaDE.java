package algorithm.de;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.chaos.RankedChaosGenerator;
import model.tf.Cec2014;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;

/**
 *
 * @author adam on 25/11/2015
 */
public class MCShaDE extends ShaDE {

    List<RankedChaosGenerator> chaosGenerator;
    int chosenChaos;


    public MCShaDE(int D, int MAXFES, TestFunction f, int H, int NP, util.random.Random rndGenerator) {
        super(D, MAXFES, f, H, NP, rndGenerator);
        chaosGenerator = RankedChaosGenerator.getAllChaosGenerators();
    }

    @Override
    public Individual run() {

        /**
         * Initialization
         */
        this.G = 0;
        this.Aext = new ArrayList<>();
        this.best = null;
        this.bestHistory = new ArrayList<>();

        /**
         * Initial population
         */
        initializePopulation();

        this.M_F = new double[this.H];
        this.M_CR = new double[this.H];

        for (int h = 0; h < this.H; h++) {
            this.M_F[h] = 0.5;
            this.M_CR[h] = 0.5;
        }

        /**
         * Generation iteration;
         */
        int r, Psize;
        double Fg, CRg;
        List<Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial;
        Individual x;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR;
        int k = 0;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;

        while (true) {

            this.G++;
            this.S_F = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();

            newPop = new ArrayList<>();

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
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

                Psize = (int) (rndGenerator.nextDouble(pmin, 0.2) * this.NP);
                if (Psize < 2) {
                    Psize = 2;
                }

                pBestArray = new ArrayList<>();
                pBestArray.addAll(this.P);
                pBestArray = this.resize(pBestArray, Psize);

                /**
                 * Parent selection
                 */
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size());
                pbest = this.getRandBestFromList(pBestArray).vector.clone();
                pr1 = this.P.get(rIndexes[0]).vector.clone();
                if (rIndexes[1] > this.NP - 1) {
                    pr2 = this.Aext.get(rIndexes[1] - this.NP).vector.clone();
                } else {
                    pr2 = this.P.get(rIndexes[1]).vector.clone();
                }
                parents = new ArrayList<>();
                parents.add(x.vector);
                parents.add(pbest);
                parents.add(pr1);
                parents.add(pr2);

                /**
                 * Mutation
                 */
                v = mutation(parents, Fg);

                /**
                 * Crossover
                 */
                u = crossover(CRg, v, x.vector);

                /**
                 * Constrain check
                 */
                u = constrainCheck(u, x.vector);

                /**
                 * Trial ready
                 */
                id++;
                trial = new Individual(String.valueOf(id), u, f.fitness(u));

                /**
                 * Trial is better
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                    this.S_F.add(Fg);
                    this.S_CR.add(CRg);
                    this.Aext.add(x);
                    wS.add(Math.abs(trial.fitness - x.fitness));

                    /**
                     * Chosen chaos rank update
                     */
                    updateRankings();

                } else {
                    newPop.add(x);
                }

                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (this.FES >= this.MAXFES) {
                    break;
                }

            }

            if (this.FES >= this.MAXFES) {
                break;
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

            /**
             * Resize of population and archives
             */
            this.P = new ArrayList<>();
            this.P.addAll(newPop);
            this.Aext = this.resizeAext(this.Aext, this.NP);

        }

        return this.best;

    }
    
    /**
     *
     * @param list
     * @return
     */
    @Override
    protected Individual getRandBestFromList(List<Individual> list) {

        int index = chaosGenerator.get(chosenChaos).chaos.nextInt(list.size());

        return list.get(index);

    }
    
    /**
     * Updates rank values for chaos generators.
     */
    protected void updateRankings() {

        if(chaosGenerator.get(chosenChaos).rank < 0.6){
            double rankSum = 0, difference;

            rankSum = chaosGenerator.stream().map((chaos) -> chaos.rank).reduce(rankSum, (accumulator, _item) -> accumulator + _item);

            difference = rankSum / 100.0;
            chaosGenerator.get(chosenChaos).rank += difference;

            for (RankedChaosGenerator chaos : chaosGenerator) {
                chaos.rank = chaos.rank / (rankSum + difference);
            }
        }


    }

    public void printOutRankings() {

        System.out.println("Ranking");
        chaosGenerator.stream().forEach((chaos) -> {
            System.out.print(chaos.rank + " ");
        });
        System.out.println("");

    }

    @Override
    public String getName() {
        return "MC-ShaDE";
    }

    /**
     *
     * @return
     */
    @Override
    protected double getRandomCR() {
        return rndGenerator.nextDouble();
    }

    /**
     *
     * @param index
     * @param max1
     * @param max2
     * @return
     */
    @Override
    protected int[] genRandIndexes(int index, int max1, int max2) {

        /**
         * TODO choose chaos base on rank
         */
        double prob = rndGenerator.nextDouble();
        int chIndex = 0;

        do {
            prob -= chaosGenerator.get(chIndex).rank;
            chIndex++;
            if (chIndex == chaosGenerator.size()) {
                break;
            }
        } while (prob > 0);

        chIndex -= 1;
        chosenChaos = chIndex;

        int a, b;

        a = chaosGenerator.get(chIndex).chaos.nextInt(max1);
        b = chaosGenerator.get(chIndex).chaos.nextInt(max2);

        while (a == b || a == index || b == index) {
            a = chaosGenerator.get(chIndex).chaos.nextInt(max1);
            b = chaosGenerator.get(chIndex).chaos.nextInt(max2);
        }

        return new int[]{a, b};
    }

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        int dimension = 10;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 14;
        TestFunction tf = new Cec2014(dimension, funcNumber);
        int H = 1;
        util.random.Random generator = new util.random.UniformRandom();

        MCShaDE shade;

        int runs = 2;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new MCShaDE(dimension, MAXFES, tf, H, NP, generator);

            shade.run();
            shade.printOutRankings();

//            PrintWriter writer;
//
//            try {
//                writer = new PrintWriter("CEC2015-" + funcNumber + "-shade" + k + ".txt", "UTF-8");
//
//                writer.print("{");
//
//                for (int i = 0; i < shade.getBestHistory().size(); i++) {
//
//                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(i).fitness));
//
//                    if (i != shade.getBestHistory().size() - 1) {
//                        writer.print(",");
//                    }
//
//                }
//
//                writer.print("}");
//
//                writer.close();
//
//            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
//                Logger.getLogger(ShaDE.class.getName()).log(Level.SEVERE, null, ex);
//            }
            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println(shade.getBest().fitness - tf.optimum());
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
