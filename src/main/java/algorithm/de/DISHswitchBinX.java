package algorithm.de;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.IndividualComparator;
import util.OtherDistributionsUtil;
import util.distance.EuclideanDistance;
import util.random.Random;

/**
 * 
 * DISH algorithm - multithreaded - crossover switch (binomial/shuffled exponential) and bin crossover with history
 * 
 * @author adam on 15/01/2020
 */
public class DISHswitchBinX extends SHADE_analysis implements Runnable {

    protected final int minPopSize;
    protected final int maxPopSize;
    
    protected List<double[]> imp_hist;
    
    public DISHswitchBinX(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize) {
        super(D, MAXFES, f, H, NP, rndGenerator);
        this.minPopSize = minPopSize;
        this.maxPopSize = NP;
        this.imp_hist = new ArrayList<>();
    }
 
    @Override
    public String getName() {
        return "DISHswitchBinX";
    }
    
    /**
     *
     * @param ind
     * @return
     */
    @Override
    protected boolean isBest(Individual ind) {

        if (this.best == null || ind.fitness < this.best.fitness) {
            this.best = ind;
            return true;
        }

        return false;

    }
    
    /**
     * Creation of initial population.
     */
    @Override
    protected void initializePopulation(){
        
        /**
         * Initial population
         */
        id = 0;
        double[] features;
        this.P = new ArrayList<>();
        Individual ind;

        for (int i = 0; i < this.NP; i++) {
            id = i;
            features = this.f.generateTrial(this.D).clone();
            ind = new Individual(String.valueOf(id), features, this.f.fitness(features));
            this.P.add(ind);
            this.FES++;
            if(this.isBest(ind)) {
                this.writeHistory();
            }
            
        }
        
    }
    
    /**
     *
     */
    @Override
    protected void writeHistory() {
        
        double[] input = new double[2+this.D];
        input[0] = this.FES;
        input[1] = this.best.fitness;
        System.arraycopy(this.best.vector, 0, input, 2, this.D);
        
        this.imp_hist.add(input);

    }
    
    /**
     * For multithread purposes
     */
    @Override
    public void run() {
        this.runAlgorithm();
    }
    
    @Override
    public Individual runAlgorithm() {

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
            this.M_CR[h] = 0.8;
            
            if(h == this.H-1) {
                this.M_F[h] = 0.9;
                this.M_CR[h] = 0.9;
            }
        }

        /**
         * Generation iteration;
         */
        int r, Psize, pbestIndex, memoryIndex, k = 0;
        double Fg, CRg, Fw, gg, pmin = 0.125, pmax = 0.25, p, wSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2;
        Individual trial, x;
        double[] v, pbest, pr1, pr2, u, wsList = new double[NP], SFlist = new double[NP], SCRlist = new double[NP], histX;
        int[] rIndexes;
        double[][] parents;
        List<Individual> newPop, pBestArray;
        boolean sec = false;

        EuclideanDistance euclid = new EuclideanDistance();
        
        while (true) {

            this.G++;
            newPop = new ArrayList<>();
                
            memoryIndex = 0;

            p = ((pmax - pmin)/(double) this.MAXFES) * this.FES + pmin;

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
                r = rndGenerator.nextInt(this.H);
                Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                while (Fg <= 0) {
                    Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                }
                if(Fg > 1) {
                    Fg = 1;
                }


                CRg = OtherDistributionsUtil.normal(this.M_CR[r], 0.1);
                if(CRg > 1) {
                    CRg = 1;
                }
                else if(CRg < 0) {
                    CRg = 0;
                }

                /**
                 * new in jSO
                 */
                gg = (double) this.FES / (double) this.MAXFES;
                if(gg < 0.25) {

                    if(CRg < 0.7) {
                        CRg = 0.7;
                    }

                }
                else if(gg < 0.5) {

                    if(CRg < 0.6) {
                        CRg = 0.6;
                    }

                }

                if(gg < 0.6 && Fg > 0.7) {
                    Fg = 0.7;
                }

                Psize = (int) (rndGenerator.nextDouble(p, 0.2) * this.NP);
                if(Psize < 2) {
                    Psize = 2;
                }

                pBestArray = this.resize(this.P, Psize);

                /**
                 * Parent selection
                 */
                pbestIndex = this.getIndexOfRandBestFromList(pBestArray, x.id);
                pbest = this.P.get(pbestIndex).vector.clone();
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size(), pbestIndex);
                pr1 = this.P.get(rIndexes[0]).vector.clone();
                if(rIndexes[1] > this.NP - 1) {
                    pr2 = this.Aext.get(rIndexes[1] - this.NP).vector.clone();
                }else {
                    pr2 = this.P.get(rIndexes[1]).vector.clone();
                }
                parents = new double[4][D];
                parents[0] = x.vector;
                parents[1] = pbest;
                parents[2] = pr1;
                parents[3] = pr2;

                if(gg < 0.2) {
                    Fw = 0.7 * Fg;
                }
                else if(gg < 0.4) {
                    Fw = 0.8 * Fg;
                }
                else {
                    Fw = 1.2 * Fg;
                }

                /**
                 * Mutation
                 */               
                v = mutationDISH(parents, Fg, Fw);

                /**
                 * Crossover
                 */

                histX = new double[this.D];
                System.arraycopy(this.getImp_hist().get(rndGenerator.nextInt(this.getImp_hist().size())), 2, histX, 0, this.D);
                
                if(sec) {
                    u = secCrossover(CRg, v, x.vector);
                    u = crossover(CRg, u, histX);
                }
                else {
                    u = crossover(CRg, v, x.vector);
                    u = crossover(CRg, u, histX);
                }
                sec = !sec;

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
                if(trial.fitness <= x.fitness) {
                    newPop.add(trial);
                    this.Aext.add(x);

                    SFlist[memoryIndex] = Fg;
                    SCRlist[memoryIndex] = CRg;
                    /**
                     * inverse distance
                     */
                    wsList[memoryIndex] = euclid.getDistance(x.vector, trial.vector);

                    memoryIndex++;

                }else {
                    newPop.add(x);
                }

                this.FES++;
                if(this.isBest(trial)) {
                    this.writeHistory();
                }
                if (this.FES >= this.MAXFES) {
                    break;
                }

                this.Aext = this.resizeAext(this.Aext, this.NP);

            }

            if(this.FES >= this.MAXFES) {
                break;
            }

            /**
             * Memories update - new
             */
            if(memoryIndex > 0) {
                wSsum = 0;
                for(int i = 0; i < memoryIndex; i++) {
                    wSsum += wsList[i];
                }

                meanS_F1 = 0;
                meanS_F2 = 0;
                meanS_CR1 = 0;
                meanS_CR2 = 0;

                for (int s = 0; s < memoryIndex; s++) {
                    meanS_F1 += (wsList[s] / wSsum) * SFlist[s] * SFlist[s];
                    meanS_F2 += (wsList[s] / wSsum) * SFlist[s];
                    meanS_CR1 += (wsList[s] / wSsum) * SCRlist[s] * SCRlist[s];
                    meanS_CR2 += (wsList[s] / wSsum) * SCRlist[s];
                }

                if(meanS_F2 != 0) {
                    this.M_F[k] = ((meanS_F1 / meanS_F2) + this.M_F[k])/2;
                }
                if(meanS_CR2 != 0) {
                    this.M_CR[k] = ((meanS_CR1 / meanS_CR2) + this.M_CR[k])/2;
                }

                k++;
                if (k >= (this.H - 1)) {
                    k = 0;
                }
            }
            
            /**
             * Resize of population and archives
             */
            this.P = new ArrayList<>();
            this.P.addAll(newPop);
            NP = (int) Math.round(this.maxPopSize - ((double) this.FES/(double) this.MAXFES)*(this.maxPopSize - this.minPopSize));
            P = this.resizePop(P, NP);

        }

        return this.best;

    }
    
    /**
     * Shuffled Exponential Crossover
     * @param CR
     * @param v
     * @param x
     * @return 
     */
    protected double[] secCrossover(double CR, double[] v, double[] x){
        
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for(int i = 0; i < this.D; i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);
        
        /**
         * Sequential Exponential Crossover
         */
        double[] u;
        int k = 0;
        int j;

        u = x.clone();
        
        do {
            j = indexes.get(k);
            u[j] = v[j];
            k++;
            
        } while((getRandomCR() < CR) && (k < (this.D-1)));
        
        return u;
        
    }
    
    /**
     *
     * @param list
     * @param id
     * @return
     */
    protected int getIndexOfRandBestFromList(List<Individual> list, String id) {
        
        int index = rndGenerator.nextInt(list.size());
        
        while(list.get(index).id.equals(id)) {
            index = rndGenerator.nextInt(list.size());
        }

        return index;

    }
    
    /**
     *
     * @param parentArray
     * @param F
     * @return
     */
    protected double[] mutationRand1(double[][] parentArray, double F) {

        double[] u = new double[D];
        double[] a = parentArray[1];
        double[] b = parentArray[2];
        double[] c = parentArray[3];

        for (int i = 0; i < D; i++) {

            u[i] = a[i] + F * (b[i] - c[i]);

        }

        return u;

    }
    
    /**
     *
     * List of parents for mutation x, a, b, c
     *
     * @param xIndex
     * @return
     */
    protected double[][] getParents(int xIndex) {

        int r1, r2, r3;

        r1 = rndGenerator.nextInt(NP);
        
        while(r1 == xIndex){
            r1 = rndGenerator.nextInt(NP);
        }
        
        r2 = rndGenerator.nextInt(NP);

        while (r2 == r1 || r2 == xIndex) {
            r2 = rndGenerator.nextInt(NP);
        }
        
        r3 = rndGenerator.nextInt(NP);

        while (r3 == r2 || r3 == r1 || r3 == xIndex) {
            r3 = rndGenerator.nextInt(NP);
        }
        
        double[][] parrentArray = new double[4][D];

        parrentArray[0] = P.get(xIndex).vector;
        parrentArray[1] = P.get(r1).vector;
        parrentArray[2] = P.get(r2).vector;
        parrentArray[3] = P.get(r3).vector;

        return parrentArray;

    }
    
    /**
     * 
     * @param parents
     * @param F
     * @param Fw
     * @return 
     */
    protected double[] mutationDISH(double[][] parents, double F, double Fw){
        
        /**
         * Parents:
         * x
         * pbest
         * pr1
         * pr2
         */
        
        double[] v = new double[this.D];
        for (int j = 0; j < this.D; j++) {

            v[j] = parents[0][j] + Fw * (parents[1][j] - parents[0][j]) + F * (parents[2][j] - parents[3][j]);

        }
        
        return v;
        
    }
    
    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<Individual> resizePop(List<Individual> list, int size) {

        if(size == list.size()){
            return list;
        }
        
        List<Individual> toRet = new ArrayList<>();
        toRet.addAll(list);
        toRet.sort(new IndividualComparator());
        toRet = toRet.subList(0, size);

        return toRet;

    }

    public List<double[]> getImp_hist() {
        return imp_hist;
    }

    protected String getNiceVector(double[] vector) {
        
        String out = "";
        out += "{";
        
        for(int i = 0; i < vector.length; i++) {
            out += String.format(Locale.US, "%.4f", vector[i]);
            if(i != vector.length-1)
                out +=", ";
        }
        
        out += "}";
        
        return out;
    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        
//        int dimension = 10;
//        int NP = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
//        int minNP = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
        int dimension = 10;
        int NP = 200;
        int minNP = 4;
        int MAXFES = 10000 * dimension;
        int funcNumber = 6;
        TestFunction tf = new Cec2015(dimension, funcNumber);

//        int[][] order;
//        int[] stock = new int[]{5840};
//        int cut_through = 4;
//        order = new int[][]{{3665,2},{2660,4},{2650,12},{2625,4},{2615,4},{2425,2},{2405,10},{2395,6},{2385,8},{2295,16},{2290,4},{2045,4},{1925,2},{1680,2},{765,2},{595,2},{565,2}};
//        
//        TestFunction tf = new CuttingStock1D(order, stock, cut_through);
//        TestFunction tf = new CuttingStock1D();
        
        int H = 5;
        long seed = 10304050L;
        util.random.Random generator = new util.random.UniformRandom();

        DISHswitchBinX shade;

        int runs = 10;
        double[] bestArray = new double[runs];

        System.out.println("START: " + new Date());
        
        for (int k = 0; k < runs; k++) {

            shade = new DISHswitchBinX(dimension, MAXFES, tf, H, NP, generator, minNP);

            shade.runAlgorithm();

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println(shade.getBest().fitness - tf.optimum());
            System.out.println(shade.getNiceVector(shade.getBest().vector));
            System.out.println("Best solution found in: " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0]);

        }

        System.out.println("END: " + new Date());
        
        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");

    }
    
}
