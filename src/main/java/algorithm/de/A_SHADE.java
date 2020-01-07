package algorithm.de;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.random.Random;

/**
 *
 * Aging ShaDE
 * 
 * @author adam on 25/11/2015
 */
public class A_SHADE extends SHADE {

    List<Double> avgAgeList;
    List<Double> stdAgeList;
    List<Double> bestIterHistory;
    List<Double> std;
    
    /**
     * New individual with age value.
     */
    protected class AgingIndividual extends Individual {
        
        double age;

        public AgingIndividual(double age) {
            this.age = age;
        }

        public AgingIndividual(double age, String id, double[] vector, double fitness) {
            super(id, vector, fitness);
            this.age = age;
        }

        public AgingIndividual(double age, Individual individual) {
            super(individual);
            this.age = age;
        }
        
    }
    
    public A_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator) {
        super(D, MAXFES, f, H, NP, rndGenerator);
    }
    
    @Override
    public String getName() {
        return "A_SHADE";
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
            this.M_CR[h] = 0.5;
        }

        /**
         * Generation iteration;
         */
        int r, Psize, pbestIndex;
        double Fg, CRg;
        List<Individual>  pBestArray;
        List<AgingIndividual> newPop;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        AgingIndividual x, trial, pbestInd;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2, deltaAct, deltaBest, ageChange;
        int k = 0, maxAgeChange = 3, agingConstant = 1;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;
        int ageAdaptationStart = (int) ((MAXFES/NP)*(1.0/3.0));
        int ageKillMod = (int) ((MAXFES/NP)*0.1);
        int killCount = (int) (NP*0.1);
        
        double[] stats;
        avgAgeList = new ArrayList<>();        
        stdAgeList = new ArrayList<>();
        bestIterHistory = new ArrayList<>();
        std = new ArrayList<>();
        
        while (true) {

            stats = countAgeStatistics();
            avgAgeList.add(stats[0]);
            stdAgeList.add(stats[1]);
            std.add(stats[1]);
            bestIterHistory.add(best.fitness - f.optimum());
            
            this.G++;
            this.S_F = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();
            newPop = new ArrayList<>();

            for (int i = 0; i < this.NP; i++) {

                x = (AgingIndividual) this.P.get(i);
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
                pbestInd = (AgingIndividual) this.getRandBestFromList(pBestArray, x.id);
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
                trial = new AgingIndividual(0, String.valueOf(id), u, f.fitness(u));

                /**
                 * Trial is better
                 */
                if (trial.fitness < x.fitness) {
                    
                    this.S_F.add(Fg);
                    this.S_CR.add(CRg);
                    this.Aext.add(x);
                    wS.add(Math.abs(trial.fitness - x.fitness));
                    /**
                     * Aging added
                     */
                    deltaAct = Math.abs(trial.fitness - x.fitness);
                    deltaBest = Math.abs(best.fitness - trial.fitness);
                    ageChange = ((deltaAct/deltaBest) < maxAgeChange) ? (deltaAct/deltaBest) : maxAgeChange;
                    x.age -= ageChange;
                    trial.id = x.id;
                    trial.age = x.age;
                    newPop.add(trial);
                } else {
                    /**
                     * Aging added
                     */
                    x.age += agingConstant;
                    
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

                this.M_F[k] = (meanS_F1 / meanS_F2);
                this.M_CR[k] = (meanS_CR1 / meanS_CR2);

                k++;
                if (k >= this.H) {
                    k = 0;
                }
            }
            
//            if(stdDecrease()){
////                newPop = replaceOldWithNew(newPop, killCount);
//                newPop = replaceBadWithNew(newPop, killCount);
//                for (AgingIndividual newPop1 : newPop) {
//                    newPop1.age = 0;
//                }
//            }
            
//            if(G == ageAdaptationStart){
//                for (AgingIndividual newPop1 : newPop) {
//                    newPop1.age = newPop1.age / (double) ageAdaptationStart;
//                }
//            }
            
            /**
             * Repolacing of old part of the population.
             */
//            if((G % ageKillMod) == 0){
//                newPop = replaceOldWithNew(newPop, killCount);
//                for(AgingIndividual aind : newPop){
//                    aind.age = 0;
//                }
//            }
//            if(G >= ageAdaptationStart){
//                newPop = replaceOldWithNew(newPop, killCount);
//            }
            
            if (this.FES >= this.MAXFES) {
                break;
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
     * 
     * @return 
     */
    protected boolean stdDecrease(){
        
        int start = 20, end = 5;
        
        int stdMinSize = (int) (start - (FES/(double)MAXFES)*(start-end));
        
        if(std.size() < stdMinSize){
            return false;
        }
        
        int startIndex = std.size() - stdMinSize;
        double[] stdArray = new double[stdMinSize];
        
        for(int i = 0; i < stdMinSize; i++){
            stdArray[i] = std.get(startIndex + i);
        }
        
        if(new Mean().evaluate(stdArray) <= std.get(startIndex)){
            std = new ArrayList<>();
            return true;
        }
        
        return false;
        
    }
    
    /**
     * 
     * @return 
     */
    protected double[] countAgeStatistics(){
        
        double[] ageList = new double[this.P.size()];
        int i = 0;
        
        for (Iterator<Individual> it = this.P.iterator(); it.hasNext();) {
            AgingIndividual ind = (AgingIndividual) it.next();
            ageList[i] = ind.age;
            i++;
        }
        
        return new double[]{new Mean().evaluate(ageList), new StandardDeviation().evaluate(ageList)};
        
    }
    
    /**
     *
     * @param list
     * @param count
     * @return
     */
    protected List<AgingIndividual> replaceOldWithNew(List<AgingIndividual> list, int count) {

        List<AgingIndividual> toRet = new ArrayList<>();
        List<AgingIndividual> tmp = new ArrayList<>();
        AgingIndividual toAppend;
        double[] features;
        int killIndex;
        
        tmp.addAll(list);
        toRet.addAll(list);
        

        for (int i = 0; i < count; i++) {
            killIndex = getIndexToKill(tmp);
            features = this.f.generateTrial(this.D).clone();
            id++;
            toAppend = new AgingIndividual(0, String.valueOf(id), features, this.f.fitness(features));
            toRet.set(killIndex, toAppend);
            this.isBest(toAppend);
            this.FES++;
            if (this.FES >= this.MAXFES) {
                return toRet;
            }
            this.writeHistory();
            tmp.remove(killIndex);
        }

        return toRet;

    }
    
    /**
     *
     * @param list
     * @param count
     * @return
     */
    protected List<AgingIndividual> replaceBadWithNew(List<AgingIndividual> list, int count) {

        List<AgingIndividual> toRet = new ArrayList<>();
        List<AgingIndividual> tmp = new ArrayList<>();
        AgingIndividual toAppend;
        double[] features;
        int killIndex;
        
        tmp.addAll(list);
        toRet.addAll(list);
        

        for (int i = 0; i < count; i++) {
            killIndex = getIndexOfWorstToKill(tmp);
            features = this.f.generateTrial(this.D).clone();
            id++;
            toAppend = new AgingIndividual(0, String.valueOf(id), features, this.f.fitness(features));
            toRet.set(killIndex, toAppend);
            this.isBest(toAppend);
            this.FES++;
            if (this.FES >= this.MAXFES) {
                return toRet;
            }
            this.writeHistory();
            tmp.remove(killIndex);
        }

        return toRet;

    }
    
    /**
     *
     * @param list
     * @return
     */
    protected int getIndexOfWorstToKill(List<AgingIndividual> list) {

        AgingIndividual b = null;
        int i = 0;
        int index = -1;

        for (AgingIndividual ind : list) {

            if (b == null) {
                b = ind;
                index = i;
            } else if (ind.fitness >= b.fitness) {
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
    protected int getIndexToKill(List<AgingIndividual> list) {

        AgingIndividual b = null;
        int i = 0;
        int index = -1;

        for (AgingIndividual ind : list) {

            if (b == null) {
                b = ind;
                index = i;
            } else if (ind.age > b.age) {
                b = ind;
                index = i;
            }
            i++;
        }

        return index;

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
        AgingIndividual ind;

        for (int i = 0; i < this.NP; i++) {
            id = i;
            features = this.f.generateTrial(this.D).clone();
            ind = new AgingIndividual(0, String.valueOf(id), features, this.f.fitness(features));
            this.isBest(ind);
            this.P.add(ind);
            this.FES++;
            this.writeHistory();
        }
        
    }

    public List<Double> getAvgAgeList() {
        return avgAgeList;
    }

    public List<Double> getStdAgeList() {
        return stdAgeList;
    }

    public List<Double> getBestIterHistory() {
        return bestIterHistory;
    }

    
    /**
     * @param args the command line argume
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 1;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 1;
        util.random.Random generator = new util.random.UniformRandom();

        A_SHADE shade;

        int runs = 1;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new A_SHADE(dimension, MAXFES, tf, H, NP, generator);

            shade.runAlgorithm();

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
            System.out.println(shade.avgAgeList.size());
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
