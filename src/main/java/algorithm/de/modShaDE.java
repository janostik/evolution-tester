package algorithm.de;

import algorithm.Algorithm;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.RandomUtil;

/**
 *
 * modified ShaDE algorithm
 *
 * @see <a href="http://goo.gl/eYB26Z">Original SHADE paper from CEC2013</a>
 * modification lies in memory (F, CR) using for single dimensions.
 *
 * @author adam on 16/11/2015
 */
public class modShaDE implements Algorithm {

    private int D;
    private int G;
    private int NP;
    private List<Individual> Aext;
    private List<Individual> P;
    private int FES;
    private int MAXFES;
    private TestFunction f;
    private Individual best;
    private List<Individual> bestHistory;
    private double[] M_F;
    private double[] M_CR;
    private List<double[]> S_F;
    private List<double[]> S_CR;
    private int H;

    public modShaDE(int D, int MAXFES, TestFunction f, int H, int NP) {
        this.D = D;
        this.MAXFES = MAXFES;
        this.f = f;
        this.H = H;
        this.NP = NP;
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
        double[] newM_F, newM_CR;

        /**
         * Initial population
         */
        int id = 0;
        double[] features;
        this.P = new ArrayList<>();
        Individual ind;

        for (int i = 0; i < this.NP; i++) {
            id = i;
            features = this.f.generateTrial(this.D).clone();
            ind = new Individual(String.valueOf(id), features, this.f.fitness(features));
            this.isBest(ind);
            this.P.add(ind);
            this.FES++;
            this.writeHistory();
        }

        this.M_F = new double[this.D];
        this.M_CR = new double[this.D];

        for (int d = 0; d < this.D; d++) {
            this.M_F[d] = 0.5;
            this.M_CR[d] = 0.5;
        }

        /**
         * Generation iteration;
         */
        int r, Psize;
        double Fg, CRg, jrand;
        List<Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial;
        Individual x;
        List<Double> wS;
        double wSsum;
        double[] meanS_F1, meanS_F2, meanS_CR;
        int k = 0;
        double pmin = 2 / this.NP;

        while (true) {

            this.G++;
            this.S_F = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();

            newPop = new ArrayList<>();

            for (int i = 0; i < this.NP; i++) {
                
                newM_F = new double[this.D];
                newM_CR = new double[this.D];

                x = this.P.get(i);

                Psize = (int) RandomUtil.nextDouble(pmin, 0.2);
                if (Psize < 2) {
                    Psize = 2;
                }

                pBestArray = new ArrayList<>();
                pBestArray.addAll(this.P);
                pBestArray = this.resize(pBestArray, Psize);

                /**
                 * Base equation
                 */
                v = new double[this.D];
                pbest = this.getBestFromList(pBestArray).vector.clone();
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size());
                pr1 = this.P.get(rIndexes[0]).vector.clone();
                if (rIndexes[1] > this.NP - 1) {
                    pr2 = this.Aext.get(rIndexes[1] - this.NP).vector.clone();
                } else {
                    pr2 = this.P.get(rIndexes[1]).vector.clone();
                }

                /**
                 * current-to-pbest/1 mutation and crossover
                 */
                u = new double[this.D];
                jrand = RandomUtil.nextInt(this.D);
                
                for (int j = 0; j < this.D; j++) {

                    Fg = RandomUtil.cauchy(this.M_F[j], 0.1);
                    newM_F[j] = Fg;
                    v[j] = x.vector[j] + Fg * (pbest[j] - x.vector[j]) + Fg * (pr1[j] - pr2[j]);
                    
                    CRg = RandomUtil.normal(this.M_CR[j], 0.1);
                    newM_CR[j] = CRg;
                    if (RandomUtil.nextDouble() <= CRg || j == jrand) {
                        u[j] = v[j];
                    } else {
                        u[j] = x.vector[j];
                    }

                }

                /**
                 * Constrain check
                 */
                for (int d = 0; d < this.D; d++) {
                    if (u[d] < this.f.min(this.D)) {
                        u[d] = (this.f.min(this.D) + x.vector[d]) / 2.0;
                    } else if (u[d] > this.f.max(this.D)) {
                        u[d] = (this.f.max(this.D) + x.vector[d]) / 2.0;
                    }
                }

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
                    this.S_F.add(newM_F.clone());
                    this.S_CR.add(newM_CR.clone());
//                    this.M_F = newM_F.clone();
//                    this.M_CR = newM_CR.clone();
                    this.Aext.add(x);
                    wS.add(Math.abs(trial.fitness - x.fitness));
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
                meanS_F1 = new double[this.D];
                meanS_F2 = new double[this.D];
                meanS_CR = new double[this.D];

                for (int s = 0; s < this.S_F.size(); s++) {
                    for(int d = 0; d < this.D; d++){
                        meanS_F1[d] += (wS.get(s) / wSsum) * this.S_F.get(s)[d] * this.S_F.get(s)[d];
                        meanS_F2[d] += (wS.get(s) / wSsum) * this.S_F.get(s)[d];
                        meanS_CR[d] += (wS.get(s) / wSsum) * this.S_CR.get(s)[d];
                    }
                }

                for(int d = 0; d < this.D; d++){
                    this.M_F[d] = meanS_F1[d] / meanS_F2[d];
                    this.M_CR[d] = meanS_CR[d];
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

    @Override
    public List<? extends Individual> getPopulation() {
        return this.P;
    }

    @Override
    public TestFunction getTestFunction() {
        return this.f;
    }

    @Override
    public String getName() {
        return "ShaDE";
    }

    /**
     *
     * @param list
     * @param size
     * @return
     */
    private List<Individual> resizeAext(List<Individual> list, int size) {

        List<Individual> toRet = new ArrayList<>();
        toRet.addAll(list);
        int index;

        while (toRet.size() > size) {

            index = RandomUtil.nextInt(toRet.size());
            toRet.remove(index);

        }

        return toRet;

    }

    /**
     *
     * @param list
     * @param size
     * @return
     */
    private List<Individual> resize(List<Individual> list, int size) {

        List<Individual> toRet = new ArrayList<>();
        List<Individual> tmp = new ArrayList<>();
        tmp.addAll(list);
        int bestIndex;

        for (int i = 0; i < size; i++) {
            bestIndex = this.getIndexOfBestFromList(tmp);
            toRet.add(tmp.get(bestIndex));
            tmp.remove(bestIndex);
        }

        return toRet;

    }

    /**
     *
     * @param list
     * @return
     */
    private int getIndexOfBestFromList(List<Individual> list) {

        Individual b = null;
        int i = 0;
        int index = -1;

        for (Individual ind : list) {

            if (b == null) {
                b = ind;
                index = i;
            } else if (ind.fitness < b.fitness) {
                b = ind;
                index = i;
            }
            i++;
        }

        return index;

    }

    /**
     *
     * @param list
     * @return
     */
    private Individual getBestFromList(List<Individual> list) {

        Individual b = null;

        for (Individual ind : list) {

            if (b == null) {
                b = ind;
            } else if (ind.fitness < b.fitness) {
                b = ind;
            }

        }

        return b;

    }

    /**
     *
     * @param max1
     * @param max2
     * @return
     */
    private int[] genRandIndexes(int index, int max1, int max2) {

        int a, b;

        a = RandomUtil.nextInt(max1);
        b = RandomUtil.nextInt(max2);

        while (a == b || a == index || b == index) {
            a = RandomUtil.nextInt(max1);
            b = RandomUtil.nextInt(max2);
        }

        return new int[]{a, b};
    }

    /**
     *
     */
    private void writeHistory() {
        this.bestHistory.add(this.best);
    }

    /**
     *
     * @param ind
     * @return
     */
    private boolean isBest(Individual ind) {

        if (this.best == null || ind.fitness < this.best.fitness) {
            this.best = ind;
            return true;
        }

        return false;

    }

    public int getD() {
        return D;
    }

    public void setD(int D) {
        this.D = D;
    }

    public int getG() {
        return G;
    }

    public void setG(int G) {
        this.G = G;
    }

    public int getNP() {
        return NP;
    }

    public void setNP(int NP) {
        this.NP = NP;
    }

    public List<Individual> getAext() {
        return Aext;
    }

    public void setAext(List<Individual> Aext) {
        this.Aext = Aext;
    }

    public List<Individual> getP() {
        return P;
    }

    public void setP(List<Individual> P) {
        this.P = P;
    }

    public int getFES() {
        return FES;
    }

    public void setFES(int FES) {
        this.FES = FES;
    }

    public int getMAXFES() {
        return MAXFES;
    }

    public void setMAXFES(int MAXFES) {
        this.MAXFES = MAXFES;
    }

    public TestFunction getF() {
        return f;
    }

    public void setF(TestFunction f) {
        this.f = f;
    }

    public Individual getBest() {
        return best;
    }

    public void setBest(Individual best) {
        this.best = best;
    }

    public List<Individual> getBestHistory() {
        return bestHistory;
    }

    public void setBestHistory(List<Individual> bestHistory) {
        this.bestHistory = bestHistory;
    }

    public double[] getM_F() {
        return M_F;
    }

    public void setM_F(double[] M_F) {
        this.M_F = M_F;
    }

    public double[] getM_CR() {
        return M_CR;
    }

    public void setM_CR(double[] M_CR) {
        this.M_CR = M_CR;
    }

    public List<double[]> getS_F() {
        return S_F;
    }

    public void setS_F(List<double[]> S_F) {
        this.S_F = S_F;
    }

    public List<double[]> getS_CR() {
        return S_CR;
    }

    public void setS_CR(List<double[]> S_CR) {
        this.S_CR = S_CR;
    }

    public int getH() {
        return H;
    }

    public void setH(int H) {
        this.H = H;
    }

    public static void main(String[] args) throws Exception {

        int dimension = 10;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 10;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 100;

        modShaDE shade;

        int runs = 10;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new modShaDE(dimension, MAXFES, tf, H, NP);

            shade.run();

            PrintWriter writer;

            try {
                writer = new PrintWriter("CEC2015-" + funcNumber + "-modshade" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(modShaDE.class.getName()).log(Level.SEVERE, null, ex);
            }

            bestArray[k] = shade.getBest().fitness;
            System.out.println(shade.getBest().fitness);
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        System.out.println("Solution error: " + (new Mean().evaluate(bestArray) - (funcNumber * 100)));

    }

}
