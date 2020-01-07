package algorithm.de;

import algorithm.Algorithm;
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
import util.random.Random;

/**
 *
 * @author adam on 30/11/2015
 */
public class ADEbest extends DEbest{

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
    
    double killCount;
    int stagnationStart;
    int stagnationEnd;
    double agingConstant;
    double maxAgeChange;
    
    List<Double> std;
    
    public ADEbest(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR, double killCount, int stagnationStart, int stagnationEnd, double agingConstant, double maxAgeChange) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
        this.killCount = killCount;
        this.stagnationStart = stagnationStart;
        this.stagnationEnd = stagnationEnd;
        this.agingConstant = agingConstant;
        this.maxAgeChange = maxAgeChange;
    }

    /**
     * 
     * @return 
     */
    @Override
    public Individual runAlgorithm() {

        /**
         * How many individuals should die
         */
        int kill = (int) (killCount * NP);
        
        /**
         * Initial population
         */
        initializePopulation();
        if (checkFES()) {
            return best;
        }

        List<AgingIndividual> newPop;
        AgingIndividual x, trial;
        double[] u, v;
        AgingIndividual[] parrentArray;
        double deltaAct, deltaBest, ageChange;
        
        std = new ArrayList<>();

        /**
         * generation itteration
         */
        while (true) {

            G++;
            newPop = new ArrayList<>();
            std.add(countStdOfPopulation());

            /**
             * Iteration through all individuals in generation.
             */
            for (int xIter = 0; xIter < NP; xIter++) {

                /**
                 * Parent selection
                 */
                parrentArray = (AgingIndividual[]) getParents(xIter);
                x = parrentArray[0];

                /**
                 * Mutation
                 */
                u = mutation(parrentArray, F);

                /**
                 * Crossover
                 */
                v = crossover(x.vector, u, CR);

                /**
                 * Trial
                 */
                trial = (AgingIndividual) makeIndividualFromVector(v);
                trial.age = 0;
                if (checkFES()) {
                    return best;
                }

                /**
                 * New generation building
                 */
                if (trial.fitness < x.fitness) {
                    
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
                    x.age += agingConstant;
                    
                    newPop.add(x);
                }

            }

            if(stdDecrease()){
//                newPop = replaceOldWithNew(newPop, kill);
                newPop = replaceBadWithNew(newPop, kill);
                for (AgingIndividual newPop1 : newPop) {
                    newPop1.age = 0;
                }
            }
            
            if (this.FES >= this.MAXFES) {
                return best;
            }
            
            P = new ArrayList<>();
            P.addAll(newPop);

        }
    }
    
    /**
     * 
     * @return 
     */
    protected double countStdOfPopulation(){
        
        double[] ageList = new double[this.P.size()];
        int i = 0;
        
        for (Iterator<Individual> it = this.P.iterator(); it.hasNext();) {
            AgingIndividual ind = (AgingIndividual) it.next();
            ageList[i] = ind.age;
            i++;
        }
        
        return new StandardDeviation().evaluate(ageList);
        
    }
    
    /**
     * 
     * @return 
     */
    protected boolean stdDecrease(){

        
        int stdMinSize = (int) (stagnationStart - (FES/(double)MAXFES)*(stagnationStart-stagnationEnd));
        
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
            features = tf.generateTrial(this.D).clone();
            id++;
            toAppend = new AgingIndividual(0, String.valueOf(id), features, tf.fitness(features));
            toRet.set(killIndex, toAppend);
            isBest(toAppend);
            this.FES++;
            if (this.FES >= this.MAXFES) {
                return toRet;
            }
            writeHistory();
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
            features = tf.generateTrial(this.D).clone();
            id++;
            toAppend = new AgingIndividual(0, String.valueOf(id), features, tf.fitness(features));
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
     * @param vector
     * @return
     */
    @Override
    protected Individual makeIndividualFromVector(double[] vector) {

        AgingIndividual ind = new AgingIndividual(0);
        ind.id = String.valueOf(id);
        id++;
        ind.vector = vector;
        ind.fitness = tf.fitness(vector);
        FES++;
        isBest(ind);
        writeHistory();

        return (ind);
    }
    
    /**
     *
     * List of parents for mutation x, a, b, c
     *
     * @param xIndex
     * @return
     */
    @Override
    protected Individual[] getParents(int xIndex) {

        AgingIndividual[] parrentArray = new AgingIndividual[4];
        List<Integer> indexes = new ArrayList<>();
        int index;

        for (int i = 0; i < NP; i++) {
            if (i != xIndex) {
                indexes.add(i);
            }
        }

        parrentArray[0] = (AgingIndividual) P.get(xIndex);

        /**
         * a
         */
        index = rndGenerator.nextInt(indexes.size());
        parrentArray[1] = (AgingIndividual) P.get(indexes.get(index));

        indexes.remove(index);

        /**
         * b
         */
        index = rndGenerator.nextInt(indexes.size());
        parrentArray[2] = (AgingIndividual) P.get(indexes.get(index));

        indexes.remove(index);

        /**
         * c
         */
        index = rndGenerator.nextInt(indexes.size());
        parrentArray[3] = (AgingIndividual) P.get(indexes.get(index));

        return parrentArray;

    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 11;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        util.random.Random generator = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8;
        
        /**
         * Age constants
         */
        double killCount = 0.1;
        int stagnationStart = 50;
        int stagnationEnd = 20;
        double agingConstant = 1;
        double maxAgeChange = 3;
        
        Algorithm de;

        int runs = 10;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            //DErand1bin(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR)
            de = new ADEbest(dimension, NP, MAXFES, tf, generator, f, cr, killCount, stagnationStart, stagnationEnd, agingConstant, maxAgeChange);

            de.runAlgorithm();

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
