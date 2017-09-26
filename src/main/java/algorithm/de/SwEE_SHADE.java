package algorithm.de;

import algorithm.Algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Cec2015;
import model.tf.PupilCostFunction;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.distance.EuclideanDistance;

/**
 *
 * SHADE switching between two F memories, one for exploitation (objective function value based) and one for exploration (distance based)
 *
 * @author adam on 26/06/2017
 */
public class SwEE_SHADE implements Algorithm {
    
    int D;
    int G;
    int NP;
    List<Individual> Aext;
    List<Individual> P;
    int FES;
    int MAXFES;
    TestFunction f;
    Individual best;
    List<Individual> bestHistory;
    double[] M_F;
    double[] M_dF;
    double[] M_CR;
    List<Double> S_F;
    List<Double> S_dF;
    List<Double> S_CR;
    int H;
    util.random.Random rndGenerator;
    int id;
    int Asize;

    public SwEE_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, util.random.Random rndGenerator) {
        this.D = D;
        this.MAXFES = MAXFES;
        this.f = f;
        this.H = H;
        this.NP = NP;
        this.rndGenerator = rndGenerator;
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
        this.M_dF = new double[this.H];
        this.M_CR = new double[this.H];

        for (int h = 0; h < this.H; h++) {
            this.M_F[h] = 0.5;
            this.M_dF[h] = 0.5;
            this.M_CR[h] = 0.5;
        }

        /**
         * Generation iteration;
         */
        boolean exploration = false; //false - exploitation, true - exploration
        boolean improvement;
        int r, Psize, pbestIndex;
        double Fg, CRg;
        List<Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial;
        Individual x, pbestInd;
        List<Double> wS, dwS;
        double wSsum, dwSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2, meanS_dF1, meanS_dF2;
        int k = 0;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;
        
        EuclideanDistance euclid = new EuclideanDistance();

        while (true) {
            
            improvement = false;
            
            this.G++;
            this.S_F = new ArrayList<>();
            this.S_dF = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();
            dwS = new ArrayList<>();

            newPop = new ArrayList<>();

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
                r = rndGenerator.nextInt(this.H);
                
                if(exploration) {
                    
                    Fg = OtherDistributionsUtil.cauchy(this.M_dF[r], 0.1);
                    while (Fg <= 0) {
                        Fg = OtherDistributionsUtil.cauchy(this.M_dF[r], 0.1);
                    }
                    if (Fg > 2) {
                        Fg = 2;
                    }
                    
                } else {
                
                    Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                    while (Fg <= 0) {
                        Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                    }
                    if (Fg > 1) {
                        Fg = 1;
                    }
                    
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
                    this.S_dF.add(Fg);
                    this.S_CR.add(CRg);
                    this.Aext.add(x);
                    wS.add(x.fitness - trial.fitness);
                    dwS.add(euclid.getDistance(x.vector, trial.vector));
                    
                    improvement = true;
                    
                } else {
                    newPop.add(x);
                }

                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (this.FES >= this.MAXFES) {
                    break;
                }

                this.Aext = this.resizeAext(this.Aext, this.Asize);
                
            }

            if (this.FES >= this.MAXFES) {
                break;
            }

            /**
             * Memories update
             */
            if (this.S_F.size() > 0) {
                wSsum = 0;
                dwSsum = 0;
                for (Double num : wS) {
                    wSsum += num;
                }
                for (Double num : dwS) {
                    dwSsum += num;
                }
                meanS_F1 = 0;
                meanS_F2 = 0;
                meanS_dF1 = 0;
                meanS_dF2 = 0;
                meanS_CR1 = 0;
                meanS_CR2 = 0;

                for (int s = 0; s < this.S_F.size(); s++) {
                    meanS_F1 += (wS.get(s) / wSsum) * this.S_F.get(s) * this.S_F.get(s);
                    meanS_F2 += (wS.get(s) / wSsum) * this.S_F.get(s);
                    meanS_dF1 += (dwS.get(s) / dwSsum) * this.S_dF.get(s) * this.S_dF.get(s);
                    meanS_dF2 += (dwS.get(s) / dwSsum) * this.S_dF.get(s);
                    meanS_CR1 += (wS.get(s) / wSsum) * this.S_CR.get(s) * this.S_CR.get(s);
                    meanS_CR2 += (wS.get(s) / wSsum) * this.S_CR.get(s);
                }

                this.M_F[k] = (meanS_F1 / meanS_F2);
                this.M_dF[k] = (meanS_dF1 / meanS_dF2);
                this.M_CR[k] = (meanS_CR1 / meanS_CR2);

                k++;
                if (k >= this.H) {
                    k = 0;
                }
            }
            
            if(!improvement) {
                exploration = !exploration;
            }
            
            /**
             * Resize of population and archives
             */
            this.P = new ArrayList<>();
            this.P.addAll(newPop);
            

        }

        return this.best;

    }
    
    /**
     * Gets the index of pbest in current population
     * 
     * @param pbest
     * @return 
     */
    protected int getPbestIndex(Individual pbest) {
        
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
    
    protected double[] mutation(List<double[]> parents, double F){
        
        /**
         * Parents:
         * x
         * pbest
         * pr1
         * pr2
         */
        
        double[] v = new double[this.D];
        for (int j = 0; j < this.D; j++) {

            v[j] = parents.get(0)[j] + F * (parents.get(1)[j] - parents.get(0)[j]) + F * (parents.get(2)[j] - parents.get(3)[j]);

        }
        
        return v;
        
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
     * Creation of initial population.
     */
    protected void initializePopulation(){
        
        /**
         * Initial population
         */
        id = 0;
        double[] features = new double[this.D];
        this.P = new ArrayList<>();
        Individual ind;

        for (int i = 0; i < this.NP; i++) {
            id = i;
            features = this.f.generateTrial(this.D).clone();
//            features = new double[this.D];
//            for(int j = 0; j < this.D; j++){
//                features[j] = this.rndGenerator.nextDouble(this.f.min(this.D), this.f.max(this.D));
//            }
            ind = new Individual(String.valueOf(id), features, this.f.fitness(features));
            this.isBest(ind);
            this.P.add(ind);
            this.FES++;
            this.writeHistory();
        }
        
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
        return "SwEE_SHADE";
    }

    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<Individual> resizeAext(List<Individual> list, int size) {

        if(size <= 0) {
            return new ArrayList<>();
        }
        
        if(size >= list.size()){
            return list;
        }

        List<Individual> toRet = new ArrayList<>();
        toRet.addAll(list);
        int index;

        while (toRet.size() > size) {

            index = rndGenerator.nextInt(toRet.size());
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
    protected List<Individual> resize(List<Individual> list, int size) {

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
    protected int getIndexOfBestFromList(List<Individual> list) {

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
     * @param id
     * @return
     */
    protected Individual getRandBestFromList(List<Individual> list, String id) {
        
        int index = rndGenerator.nextInt(list.size());
        
        while(list.get(index).id.equals(id)) {
            index = rndGenerator.nextInt(list.size());
        }

        return list.get(index);

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
     */
    protected void writeHistory() {
        
        this.bestHistory.add(this.best);
        /**
         * NOTE - only for huge problems with lots of generations
         */
//        if(this.bestHistory.size() >= 1000) {
//            System.out.println("TIME at " + this.FES + " OFEs\n" + new Date());
//            System.out.println("OFV\n" + this.best.fitness);
//            System.out.println("SOLUTION\n" + Arrays.toString(this.best.vector));
//            System.out.println("-------------");
//            this.bestHistory = new ArrayList<>();
//        }
    }

    /**
     *
     * @param ind
     * @return
     */
    protected boolean isBest(Individual ind) {

        if (this.best == null || ind.fitness < this.best.fitness) {
            this.best = ind;
            return true;
        }

        return false;

    }
    
    /**
     *
     * @param list
     * @return
     */
    protected Individual getBestFromList(List<Individual> list) {

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

    @Override
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

    public List<Double> getS_F() {
        return S_F;
    }

    public void setS_F(List<Double> S_F) {
        this.S_F = S_F;
    }

    public List<Double> getS_CR() {
        return S_CR;
    }

    public void setS_CR(List<Double> S_CR) {
        this.S_CR = S_CR;
    }

    public int getH() {
        return H;
    }

    public void setH(int H) {
        this.H = H;
    }

    public util.random.Random getRndGenerator() {
        return rndGenerator;
    }

    public void setRndGenerator(util.random.Random rndGenerator) {
        this.rndGenerator = rndGenerator;
    }
    //</editor-fold>

    public int getAsize() {
        return Asize;
    }

    public void setAsize(int Asize) {
        this.Asize = Asize;
    }

    public static void main(String[] args) throws Exception {

//        double prec = 0;
//        int min = 1, max = 128;
//        
//        for(int i = min; i <= max; i++) {
//            
//            prec += runOneIris(i);
//            
//        }
//        
//        System.out.println("Overall precision: " + prec/max*100 + "%");
//        
//        String path = "C:\\Users\\wiki\\Documents\\NetBeansProjects\\PupilCostFunctions\\12"; 
        
        int dimension = 10; //38
        int NP = 100;
        int MAXFES = 1000 * NP;
        int funcNumber = 3;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 10;
        util.random.Random generator;

        SwEE_SHADE shade;

        int runs = 3;
        double[] bestArray = new double[runs];
        int i, best;
        double min;

        for (int k = 0; k < runs; k++) {
            
            generator = new util.random.UniformRandom();
            shade = new SwEE_SHADE(dimension, MAXFES, tf, H, NP, generator);

            shade.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

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
//            System.out.println(Arrays.toString(((PupilCostFunction)tf).getCoords(shade.getBest().vector)));
//            
//            Map<String, List> map = ((Spalovny3kraje_2)tf).getOutput(shade.getBest().vector);
//            
//            System.out.println("=================================");
//            String line;
//          
//            for(Entry<String,List> entry : map.entrySet()){
//                line = "";
//                System.out.println(entry.getKey());
//                line += "{";
////                System.out.print("{");
//                for(int pup = 0; pup < entry.getValue().size(); pup++){
////                    System.out.print(entry.getValue().get(pup));
//                    line += entry.getValue().get(pup);
//                    if(pup != entry.getValue().size()-1){
////                       System.out.print(","); 
//                       line += ",";
//                    }
//                }
////                System.out.println("}");
//                line += "}";
//                line = line.replace("[", "{");
//                line = line.replace("]", "}");
//                System.out.println(line);
//                
//            }
//            
//            System.out.println("=================================");
            
            for(Individual ind : ((SwEE_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
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