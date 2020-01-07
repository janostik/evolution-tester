package algorithm.de;

import algorithm.Algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.net.Net;
import model.tf.Schwefel;
import model.tf.nwf.Network3;
import model.tf.nwf.Network4;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.random.Random;

/**
 *
 * @author adam 3/11/2015
 */
public class uDE implements Algorithm {

    protected int D;
    protected int G;
    protected int NP;
    protected List<uIndividual> P;
    protected int FES;
    protected int MAXFES;
    protected TestFunction tf;
    protected uIndividual best;
    protected List<uIndividual> bestHistory;
    protected util.random.Random rndGenerator;
    protected int id;

    /**
     * Individual extended with F and CR values
     */
    public class uIndividual extends Individual {
        
        protected double F;
        protected double CR;

        public double getF() {
            return F;
        }

        public void setF(double F) {
            this.F = F;
        }

        public double getCR() {
            return CR;
        }

        public void setCR(double CR) {
            this.CR = CR;
        }

    }
    
    public uDE(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator) {
        this.D = D;
        this.G = 0;
        this.NP = NP;
        this.MAXFES = MAXFES;
        this.tf = f;
        this.rndGenerator = rndGenerator;
        this.id = 0;
    }

    @Override
    public uIndividual runAlgorithm() {

        /**
         * Initial population
         */
        initializePopulation();
        if (checkFES()) {
            return best;
        }

        List<uIndividual> newPop;
        uIndividual x, trial;
        double[] u, v;
        double F, CR, Fv, CRv, Fu, CRu;
        uIndividual[] parrentArray;

        /**
         * generation itteration
         */
        while (true) {

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
                //Get average values for mutation and crossover
                F = this.getAverageF(parrentArray);
                CR = this.getAverageCR(parrentArray);

                /**
                 * Mutation
                 */
                u = mutation(parrentArray, F);
                //mutate F and CR as well
                Fu = this.mutationF(parrentArray, F);
                CRu = this.mutationCR(parrentArray, F);

                /**
                 * Crossover
                 */
                v = crossover(x.vector, u, CR);
                //crossover F and CR as well
                Fv = this.crossoverF(F, Fu, CR);
                CRv = this.crossoverCR(CR, CRu, CR);

                /**
                 * Trial
                 */
                trial = makeIndividualFromVector(v, Fv, CRv);
                if (checkFES()) {
                    return best;
                }

                /**
                 * New generation building
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                } else {
                    newPop.add(x);
                }

            }

            P = newPop;

        }
    }

    /**
     * Crossover of F parameter
     * 
     * @param F
     * @param Fv
     * @param CR
     * @return 
     */
    protected double crossoverF(double F, double Fv, double CR) {
        
        if(rndGenerator.nextDouble() < CR) {
            return Fv;
        }
        else {
            return F;
        }
        
    }
    
    /**
     * Crossover of CR parameter
     * 
     * @param CR
     * @param CRv
     * @param CRold
     * @return 
     */
    protected double crossoverCR(double CRold, double CRv, double CR) {
        
        if(rndGenerator.nextDouble() < CR) {
            return CRv;
        }
        else {
            return CRold;
        }
        
    }
    
    /**
     * Mutation of F parameter
     * 
     * @param parrentArray
     * @param F
     * @return 
     */
    protected double mutationF(uIndividual[] parrentArray, double F) {
        
        double Fn;
        
        Fn = parrentArray[1].F + F * (parrentArray[2].F - parrentArray[3].F);
        
        if(Fn > 1) {
            Fn = 1;
        } else if(Fn < 0) {
            Fn = 0;
        }
        
        return Fn;
        
    }
    
    /**
     * Mutation of CR parameter
     * 
     * @param parrentArray
     * @param F
     * @return 
     */
    protected double mutationCR(uIndividual[] parrentArray, double F) {
        
        double CRn;
        
        CRn = parrentArray[1].CR + F * (parrentArray[2].CR - parrentArray[3].CR);
        
        if(CRn > 1) {
            CRn = 1;
        } else if(CRn < 0) {
            CRn = 0;
        }
        
        return CRn;
        
    }
    
    /**
     * Coputes average F from parents
     * 
     * @param parrentArray
     * @return 
     */
    protected double getAverageF(uIndividual[] parrentArray) {
        
        double F = 0;
        int size = parrentArray.length;
        
        for(int i = 0; i < size; i++) {
            F +=parrentArray[i].F;
        }
        
        return F/size;
        
    }
    
    /**
     * Coputes average CR from parents
     * 
     * @param parrentArray
     * @return 
     */
    protected double getAverageCR(uIndividual[] parrentArray) {
        
        double CR = 0;
        int size = parrentArray.length;
        
        for(int i = 0; i < size; i++) {
            CR +=parrentArray[i].CR;
        }
        
        return CR/size;
        
    }
    
    /**
     *
     * @param x
     * @param u
     * @param CR
     * @return
     */
    protected double[] crossover(double[] x, double[] u, double CR) {

        double[] v = new double[D];
        int jrand = rndGenerator.nextInt(D);

        for (int i = 0; i < D; i++) {

            if (i == jrand || rndGenerator.nextDouble() < CR) {
                v[i] = u[i];
            } else {
                v[i] = x[i];
            }

        }

        return v;

    }

    protected void constrain(uIndividual individual){
        
        tf.constrain(individual);
        
    }
    
    /**
     *
     * @param parentArray
     * @param F
     * @return
     */
    protected double[] mutation(uIndividual[] parentArray, double F) {

        double[] u = new double[D];
        double[] a = parentArray[1].vector;
        double[] b = parentArray[2].vector;
        double[] c = parentArray[3].vector;

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
    protected uIndividual[] getParents(int xIndex) {

        uIndividual[] parrentArray = new uIndividual[4];
        List<Integer> indexes = new ArrayList<>();
        int index;

        for (int i = 0; i < NP; i++) {
            if (i != xIndex) {
                indexes.add(i);
            }
        }

        parrentArray[0] = P.get(xIndex);

        /**
         * a
         */
        index = rndGenerator.nextInt(indexes.size());
        parrentArray[1] = P.get(indexes.get(index));

        indexes.remove(index);

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
     */
    protected void initializePopulation() {

        P = new ArrayList<>();
        double[] vector;
        double F, CR;

        for (int i = 0; i < NP; i++) {

            if (checkFES()) {
                return;
            }
            vector = tf.generateTrial(D);
            CR = rndGenerator.nextDouble();
            F = rndGenerator.nextDouble();
            P.add(makeIndividualFromVector(vector, F, CR));
            
        }

    }

    /**
     *
     * @return
     */
    protected boolean checkFES() {
        return (FES >= MAXFES);
    }

    /**
     *
     * @param vector
     * @param F
     * @param CR
     * @return
     */
    protected uIndividual makeIndividualFromVector(double[] vector, double F, double CR) {

        uIndividual ind = new uIndividual();
        ind.id = String.valueOf(id);
        ind.F = F;
        ind.CR = CR;
        id++;
        ind.vector = vector;
        constrain(ind);
        ind.fitness = tf.fitness(vector);
        FES++;
        isBest(ind);
        writeHistory();

        return (ind);
    }

    /**
     *
     */
    protected void writeHistory() {
        if (bestHistory == null) {
            bestHistory = new ArrayList<>();
        }
        bestHistory.add(best);
    }

    /**
     *
     * @param ind
     * @return
     */
    protected boolean isBest(uIndividual ind) {

        if (best == null || ind.fitness < best.fitness) {
            best = ind;
            return true;
        }

        return false;

    }

    @Override
    public List<? extends Individual> getPopulation() {
        return P;
    }

    @Override
    public TestFunction getTestFunction() {
        return tf;
    }

    @Override
    public String getName() {
        return "DErand1bin";
    }

    // <editor-fold defaultstate="collapsed" desc="getters and setters">
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

    public List<uIndividual> getP() {
        return P;
    }

    public void setP(List<uIndividual> P) {
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

    public TestFunction getTf() {
        return tf;
    }

    public void setTf(TestFunction f) {
        this.tf = f;
    }

    public List<uIndividual> getBestHistory() {
        return bestHistory;
    }

    public void setBestHistory(List<uIndividual> bestHistory) {
        this.bestHistory = bestHistory;
    }

    public Random getRndGenerator() {
        return rndGenerator;
    }

    public void setRndGenerator(Random rndGenerator) {
        this.rndGenerator = rndGenerator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception {
    
        int dimension = 10;
        int NP = 20;
        int iter = 100;
        int MAXFES = iter * NP;
        int funcNumber = 5;
        TestFunction tf = new Schwefel();
        util.random.Random generator = new util.random.UniformRandom();
        double[] Farr, CRarr;

        Algorithm de;

        int runs = 30;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            Farr = new double[NP];
            CRarr = new double[NP];
            
            de = new uDE(dimension, NP, MAXFES, tf, generator);

            de.runAlgorithm();

            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
            for(int i = 0; i < NP; i++) {
                Farr[i] = ((uDE)de).P.get(i).F;
                CRarr[i] = ((uDE)de).P.get(i).CR;
            }
            
            System.out.println("F: " + Arrays.toString(Farr));
            System.out.println("F mean: " + new Mean().evaluate(Farr));
            System.out.println("CR: " + Arrays.toString(CRarr));
            System.out.println("CR mean: " + new Mean().evaluate(CRarr));
            
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
