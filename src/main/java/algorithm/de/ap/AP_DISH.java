package algorithm.de.ap;

import algorithm.Algorithm;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.TestFunction;
import model.tf.ap.APtf;
import model.tf.ap.regression.APharmonicTF;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.IndividualComparator;
import util.OtherDistributionsUtil;
import util.distance.EuclideanDistance;
import util.random.Random;

/**
 *
 * jSO algorithm
 * 
 * @author adam on 25/06/2018
 */
public class AP_DISH implements Algorithm {

    int D;
    int G;
    int NP;
    List<AP_Individual> Aext;
    List<AP_Individual> P;
    int FES;
    int MAXFES;
    TestFunction f;
    AP_Individual best;
    List<AP_Individual> bestHistory;
    double[] M_F;
    double[] M_CR;
    List<Double> S_F;
    List<Double> S_CR;
    int H;
    util.random.Random rndGenerator;
    int id;
    int Asize;
    List<double[]> M_Fhistory;
    List<double[]> M_CRhistory;

    protected final int minPopSize;
    protected final int maxPopSize;

    @Override
    public List<AP_Individual> getPopulation() {
        return P;
    }

    @Override
    public TestFunction getTestFunction() {
        return f;
    }

    @Override
    public AP_Individual getBest() {
        return this.best;
    }
    
    public class AP_Individual extends Individual{
        
        public String equation;
        public int length;

        private AP_Individual(String valueOf, double[] u, double fitness) {
            super(valueOf, u, fitness);
        }
 
    }
    
    /**
     * Creation of initial population.
     */
    protected void initializePopulation(){
        
        /**
         * Initial population
         */
        id = 0;
        double[] features = new double[this.D];
        this.P = new ArrayList<>();
        AP_Individual ind;

        for (int i = 0; i < this.NP; i++) {
            id = i;
            features = this.f.generateTrial(this.D).clone();
//            features = new double[this.D];
//            for(int j = 0; j < this.D; j++){
//                features[j] = this.rndGenerator.nextDouble(this.f.min(this.D), this.f.max(this.D));
//            }
            ind = new AP_Individual(String.valueOf(id), features, this.f.fitness(features));
            this.isBest(ind);
            this.P.add(ind);
            ind.equation = ((APtf) f).ap.equation;
            ind.length = ((APtf) f).countLength(features);
            this.FES++;
            this.writeHistory();
        }
        
    }
    
    /**
     *
     */
    protected void writeHistory() {
        
        this.bestHistory.add(this.best);
    }
    
    /**
     *
     * @param ind
     * @return
     */
    protected boolean isBest(AP_Individual ind) {

        if (this.best == null || ind.fitness <= this.best.fitness) {
            this.best = ind;
            return true;
        }

        return false;

    }
    
    /**
     *
     * @param list
     * @param id
     * @return
     */
    protected AP_Individual getRandBestFromList(List<AP_Individual> list, String id) {
        
        int index = rndGenerator.nextInt(list.size());
        
        while(list.get(index).id.equals(id)) {
            index = rndGenerator.nextInt(list.size());
        }

        return list.get(index);

    }
    
    /**
     * Gets the index of pbest in current population
     * 
     * @param pbest
     * @return 
     */
    protected int getPbestIndex(AP_Individual pbest) {
        
        int toRet = -1;
        Individual cur;
        
        for(int i = 0; i < this.P.size(); i++){
            
            cur = this.P.get(i);
            
            if(cur == pbest){
                toRet = i;
            }
            
        }
        
        return toRet;
        
    }
    
    /**
     * 
     * @param u
     * @param x
     * @return 
     */
    protected double[] constrainCheck(double[] u, double[] x){
        /**
         * Constrain check
         */
        for (int d = 0; d < this.D; d++) {
            if (u[d] < this.f.min(this.D)) {
                u[d] = (this.f.min(this.D) + x[d]) / 2.0;
            } else if (u[d] > this.f.max(this.D)) {
                u[d] = (this.f.max(this.D) + x[d]) / 2.0;
            }
        }
        
        return u;
    }
    
    /**
     * 
     * @param CR
     * @param v
     * @param x
     * @return 
     */
    protected double[] crossover(double CR, double[] v, double[] x){
        
        /**
         * Crossover
         */
        double[] u = new double[this.D];
        int jrand = rndGenerator.nextInt(this.D);

        for (int j = 0; j < this.D; j++) {
            if (getRandomCR() <= CR || j == jrand) {
                u[j] = v[j];
            } else {
                u[j] = x[j];
            }
        }
        
        return u;
        
    }
    
    /**
     * 
     * @return 
     */
    protected double getRandomCR(){
        return rndGenerator.nextDouble();
    }
    
    /**
     *
     * @param index
     * @param max1
     * @param max2
     * @param pbest
     * @return
     */
    protected int[] genRandIndexes(int index, int max1, int max2, int pbest) {

        int a, b;

        a = rndGenerator.nextInt(max1);
        
        while(a == pbest || a == index){
            a = rndGenerator.nextInt(max1);
        }
        
        b = rndGenerator.nextInt(max2);

        while (b == a || b == index || b == pbest) {
            b = rndGenerator.nextInt(max2);
        }

        return new int[]{a, b};
    }
    
    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<AP_Individual> resizeAext(List<AP_Individual> list, int size) {

        if(size <= 0) {
            return new ArrayList<>();
        }
        
        if(size >= list.size()){
            return list;
        }

        List<AP_Individual> toRet = new ArrayList<>();
        toRet.addAll(list);
        int index;

        while (toRet.size() > size) {

            index = rndGenerator.nextInt(toRet.size());
            toRet.remove(index);

        }

        return toRet;

    }
    
    public List<AP_Individual> getBestHistory() {
        return bestHistory;
    }
    
    public AP_DISH(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize) {
        
        this.D = D;
        this.MAXFES = MAXFES;
        this.f = f;
        this.H = H;
        this.NP = NP;
        this.rndGenerator = rndGenerator;
        this.minPopSize = minPopSize;
        this.maxPopSize = NP;
    }
 
    @Override
    public String getName() {
        return "DISH";
    }
    
    @Override
    public AP_Individual runAlgorithm() {

        /**
         * Initialization
         */
        this.G = 0;
        this.Aext = new ArrayList<>();
        this.best = null;
        this.bestHistory = new ArrayList<>();
        this.M_Fhistory = new ArrayList<>();
        this.M_CRhistory = new ArrayList<>();

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
        
        this.M_Fhistory.add(this.M_F.clone());
        this.M_CRhistory.add(this.M_CR.clone());


        /**
         * Generation iteration;
         */
        int r, Psize, pbestIndex;
        double Fg, CRg, Fw;
        List<AP_Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        AP_Individual trial, pbestInd;
        AP_Individual x;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2;
        int k = 0;
        double pmin = 0.125, pmax = 0.25, p;
        List<double[]> parents;
        double gg;

        EuclideanDistance euclid = new EuclideanDistance();
        
        while (true) {

            this.G++;
            this.S_F = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();

            newPop = new ArrayList<>();
            
            p = ((pmax - pmin)/(double) this.MAXFES) * this.FES + pmin;

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
                r = rndGenerator.nextInt(this.H);
                if(r == this.H - 1) {
                    this.M_F[r] = 0.9;
                    this.M_CR[r] = 0.9;
                }
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
                 * new in jSO
                 */
                gg = (double) this.FES / (double) this.MAXFES;
                if (gg < 0.25) {
                    
                    if(CRg < 0.7) {
                        CRg = 0.7;
                    }
                    
                }
                else if (gg < 0.5) {
                    
                    if(CRg < 0.6) {
                        CRg = 0.6;
                    }
                    
                }
                
                if (gg < 0.6 && Fg > 0.7) {
                    Fg = 0.7;
                }

                Psize = (int) (rndGenerator.nextDouble(p, 0.2) * this.NP);
                if (Psize < 2) {
                    Psize = 2;
                }

                pBestArray = new ArrayList<>();
                pBestArray.addAll(this.P);
                pBestArray = this.resize(pBestArray, Psize);

                /**
                 * Parent selection
                 */
                pbestInd = this.getRandBestFromList(pBestArray, x.id);
                pbestIndex = this.getPbestIndex(pbestInd);
                pbest = pbestInd.vector.clone();
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size(), pbestIndex);
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
                
                if (gg < 0.2) {
                    Fw = 0.7 * Fg;
                }
                else if (gg < 0.4) {
                    Fw = 0.8 * Fg;
                }
                else {
                    Fw = 1.2 * Fg;
                }
                
                /**
                 * Mutation
                 */               
                v = mutation(parents, Fg, Fw);

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
                trial = new AP_Individual(String.valueOf(id), u, f.fitness(u));
                trial.equation = ((APtf) f).ap.equation;
                trial.length = ((APtf) f).countLength(u);
                
                /**
                 * Trial is better
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                    this.S_F.add(Fg);
                    this.S_CR.add(CRg);
                    this.Aext.add(x);
                    wS.add(euclid.getDistance(x.vector, trial.vector));
                    
                } else {
                    newPop.add(x);
                }

                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (this.FES >= this.MAXFES) {
                    break;
                }

                this.Aext = this.resizeAext(this.Aext, this.NP);
                
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
                meanS_CR1 = 0;
                meanS_CR2 = 0;

                for (int s = 0; s < this.S_F.size(); s++) {
                    meanS_F1 += (wS.get(s) / wSsum) * this.S_F.get(s) * this.S_F.get(s);
                    meanS_F2 += (wS.get(s) / wSsum) * this.S_F.get(s);
                    meanS_CR1 += (wS.get(s) / wSsum) * this.S_CR.get(s) * this.S_CR.get(s);
                    meanS_CR2 += (wS.get(s) / wSsum) * this.S_CR.get(s);
                }

                if(meanS_F2 != 0) {
                    this.M_F[k] = ((meanS_F1 / meanS_F2) + this.M_F[k])/2;
                }
                if(meanS_CR2 != 0) {
                    this.M_CR[k] = ((meanS_CR1 / meanS_CR2) + this.M_CR[k])/2;
                }

                k++;
                if (k >= (this.H-1)) {
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
            
            this.M_Fhistory.add(this.M_F.clone());
            this.M_CRhistory.add(this.M_CR.clone());

        }

//        System.out.println("100%");
        return this.best;

    }
    
        /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<AP_Individual> resize(List<AP_Individual> list, int size) {

        List<AP_Individual> toRet = new ArrayList<>();
        toRet.addAll(list);
        
        toRet.sort(new IndividualComparator());
        toRet = toRet.subList(0, size);

        return toRet;

    }
    
    /**
     * 
     * @param parents
     * @param F
     * @param Fw
     * @return 
     */
    protected double[] mutation(List<double[]> parents, double F, double Fw){
        
        /**
         * Parents:
         * x
         * pbest
         * pr1
         * pr2
         */
        
        double[] v = new double[this.D];
        for (int j = 0; j < this.D; j++) {

            v[j] = parents.get(0)[j] + Fw * (parents.get(1)[j] - parents.get(0)[j]) + F * (parents.get(2)[j] - parents.get(3)[j]);

        }
        
        return v;
        
    }
    
    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<AP_Individual> resizePop(List<AP_Individual> list, int size) {

        if(size == list.size()){
            return list;
        }
        
        List<AP_Individual> toRet = new ArrayList<>();
        toRet.addAll(list);
        toRet.sort(new IndividualComparator());
        toRet = toRet.subList(0, size);

        return toRet;

    }
    
    /**
     *
     * @param list
     * @return
     */
    protected AP_Individual getBestFromList(List<AP_Individual> list) {

        AP_Individual b = null;

        for (AP_Individual ind : list) {

            if (b == null) {
                b = ind;
            } else if (ind.fitness < b.fitness) {
                b = ind;
            }
        }

        return b;

    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        int minNP = 4;
        long seed = 10304050L;

        AP_DISH shade;

        
        int dimension = 16+9;
        int NP = 45;
        int H = 5;
        int generations = 300;
        int MAXFES = generations * NP;

        APtf tf = new APharmonicTF();
        /**
         * Random generator for parent selection, F and CR selection
         */
        Random generator = (Random) new util.random.UniformRandom();
        double min;
//        APconst ap = new APconst();

        Algorithm de;

        int runs = 100;
        double[] bestArray = new double[runs];
        int i, best;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new AP_ShaDE(dimension, MAXFES, tf, H, NP, generator);

            de.runAlgorithm();

//            PrintWriter writer;
//
//            try {
//                writer = new PrintWriter(tf.name() + "-shade" + k + ".txt", "UTF-8");
//
//                writer.print("{");
//
//                for (int z = 0; z < ((AP_ShaDE)de).getBestHistory().size(); z++) {
//
//                    writer.print(String.format(Locale.US, "%.10f", ((AP_ShaDE)de).getBestHistory().get(z).fitness));
//
//                    if (z != ((AP_ShaDE)de).getBestHistory().size() - 1) {
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
//                
//            }
            
            
            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
            /**
             * Final AP equation.
             */

//            System.out.println("=================================");
//            System.out.println("Equation: \n" + ((AP_Individual) de.getBest()).equation);
//            System.out.println("Vector: \n" + Arrays.toString(((AP_Individual) de.getBest()).vector));
//            System.out.println("=================================");
//            
//            for(AP_Individual ind : ((AP_ShaDE)de).getBestHistory()){
//                i++;
//                if(ind.fitness < min){
//                    min = ind.fitness;
//                    best = i;
//                }
//                if(ind.fitness == 0){
//                    System.out.println("Solution found in " + i + " CFE");
//                    break;
//                }
//            }
//            System.out.println("Best solution found in " + best + " CFE");
            
//            System.out.println("Length in 000. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(0));
//            System.out.println("Length in 001. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(1));
//            System.out.println("Length in 010. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(10));
//            System.out.println("Length in 020. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(20));
//            System.out.println("Length in 050. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(50));
//            System.out.println("Length in 100. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(100));
//            System.out.println("Length in 200. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(200));
//            System.out.println("Length in 299. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(299));
            
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
