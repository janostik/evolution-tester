package algorithm.de;

import algorithm.Algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
 * ShaDE algorithm modified for F and CR initialization values.
 *
 * @see <a href="http://goo.gl/eYB26Z">Original paper from CEC2013</a>
 *
 * @author adam on 20/11/2015
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
    private List<double[]> M_F;
    private List<double[]> M_CR;
    private List<double[]> S_F;
    private List<double[]> S_CR;
    private int H;
    private double Finit;
    private double CRinit;

    public modShaDE(int D, int MAXFES, TestFunction f, int H, int NP) {
        this.D = D;
        this.MAXFES = MAXFES;
        this.f = f;
        this.H = H;
        this.NP = NP;
        this.CRinit = 0.5;
        this.Finit = 0.5;
    }
    
    public modShaDE(int D, int MAXFES, TestFunction f, int H, int NP, double Finit, double CRinit) {
        this.D = D;
        this.MAXFES = MAXFES;
        this.f = f;
        this.H = H;
        this.NP = NP;
        this.Finit = Finit;
        this.CRinit = CRinit;
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

//        this.M_F = new double[this.H];
//        this.M_CR = new double[this.H];
        this.M_F = new ArrayList<>();
        this.M_CR = new ArrayList<>();
        double[] single_M_F;
        double[] single_M_CR;

        for (int h = 0; h < this.H; h++) {
            single_M_F = new double[this.D];
            single_M_CR = new double[this.D];
            for(int dim = 0; dim < this.D; dim++) {
                single_M_F[h] = this.Finit;
                single_M_CR[h] = this.CRinit;
            }
            this.M_F.add(single_M_F);
            this.M_CR.add(single_M_CR);
        }

        /**
         * Generation iteration;
         */
        int r, Psize;
        double[] Fg, CRg;
        double jrand, wSsum;
        List<Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial;
        Individual x;
        List<Double> wS;
        double[] meanS_F1, meanS_F2, meanS_F, meanS_CR;
        int k = 0;
        double pmin = 2 / this.NP;

        while (true) {

            this.G++;
            this.S_F = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();

            newPop = new ArrayList<>();

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
                r = RandomUtil.nextInt(this.H);
                Fg = new double[this.D];
                CRg = new double[this.D];
                for (int dim = 0; dim < this.D; dim++) {
                    Fg[dim] = RandomUtil.cauchy(this.M_F.get(r)[dim], 0.1);
                    while (Fg[dim] <= 0) {
                        Fg[dim] = RandomUtil.cauchy(this.M_F.get(r)[dim], 0.1);
                    }
                    if (Fg[dim] > 1){
                        Fg[dim] = 1;
                    }
                    
                    CRg[dim] = RandomUtil.normal(this.M_CR.get(r)[dim], 0.1);
                    if (CRg[dim] > 1) {
                        CRg[dim] = 1;
                    }
                    if (CRg[dim] < 0) {
                        CRg[dim] = 0;
                    }
                }
                
//                Fg = RandomUtil.cauchy(this.M_F[r], 0.1);
//                while (Fg <= 0) {
//                    Fg = RandomUtil.cauchy(this.M_F[r], 0.1);
//                }
//                if (Fg > 1) {
//                    Fg = 1;
//                }
//
//                CRg = RandomUtil.normal(this.M_CR[r], 0.1);
//                if (CRg > 1) {
//                    CRg = 1;
//                }
//                if (CRg < 0) {
//                    CRg = 0;
//                }

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

                for (int j = 0; j < this.D; j++) {

                    v[j] = x.vector[j] + Fg[j] * (pbest[j] - x.vector[j]) + Fg[j] * (pr1[j] - pr2[j]);

                }

                /**
                 * Crossover
                 */
                u = new double[this.D];
                jrand = RandomUtil.nextInt(this.D);

                for (int j = 0; j < this.D; j++) {
                    if (RandomUtil.nextDouble() <= CRg[j] || j == jrand) {
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
                    this.S_F.add(Fg);
                    this.S_CR.add(CRg);
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
                for (int kk = 0; kk < this.H; kk++){               
                    System.out.println(kk + ". F - " + Arrays.toString(this.M_F.get(kk)));
                    System.out.println(kk + ". CR - " + Arrays.toString(this.M_CR.get(kk)));
                }
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
//                meanS_F1 = 0;
//                meanS_F2 = 0;
//                meanS_CR = 0;
                meanS_F1 = new double[this.D];
                meanS_F2 = new double[this.D];
                meanS_CR = new double[this.D];
                meanS_F = new double[this.D];

                for (int dim = 0; dim < this.D; dim++ ) {
                    meanS_F1[dim] = 0;
                    meanS_F2[dim] = 0;
                    meanS_CR[dim] = 0;
                }
                    
                for (int s = 0; s < this.S_F.size(); s++) {

                    for (int dim = 0; dim < this.D; dim++ ) {

                        meanS_F1[dim] += (wS.get(s) / wSsum) * this.S_F.get(s)[dim] * this.S_F.get(s)[dim];
                        meanS_F2[dim] += (wS.get(s) / wSsum) * this.S_F.get(s)[dim];
                        meanS_CR[dim] += (wS.get(s) / wSsum) * this.S_CR.get(s)[dim];

                    }
                }
                
                for (int dim = 0; dim < this.D; dim++ ) {
                    meanS_F[dim] = meanS_F1[dim]/meanS_F2[dim];
                }
 
                this.M_F.set(k,meanS_F);
                this.M_CR.set(k, meanS_CR);

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
        int funcNumber = 5;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 1;
        double CR = 0.5;
        double F = 0.5;

        modShaDE shade;

        int runs = 10;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new modShaDE(dimension, MAXFES, tf, H, NP, F, CR);

            shade.run();

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
