package algorithm.de;

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
import model.tf.Ackley;
import model.tf.Dejong;
import model.tf.Rastrigin;
import model.tf.Rosenbrock;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.random.Random;

/**
 *
 * @author wiki
 */
public class DE_hbrs extends DErand1bin {
    
    public int score_total;
    public List[] rank_history; 
    protected final int favor;
    protected final int punish;
    
    public DE_hbrs(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR, int favor, int punish) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
        this.favor = favor;
        this.punish = punish;
    }
    
    /**
     * Creates rank history object
     */
    protected void initializeRankHistory() {
        
        this.rank_history = new ArrayList[this.NP];
        for(int i = 0; i < this.NP; i++) {
            this.rank_history[i] = new ArrayList<>();
        }

    }
    
    /**
     * Writes the rank of a population to rank history object
     */
    protected void writeToRankHistory() {
        
        for(Individual ind : this.P) {
            this.rank_history[Integer.parseInt(ind.id)].add(ind.score);
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
        
        /**
         * Ranking initialization
         */
        this.initializeRankHistory();
        this.writeToRankHistory();

        List<Individual> newPop;
        Individual x, trial;
        double[] u, v;
        Individual[] parrentArray;

        /**
         * generation itteration
         */
        while (true) {

            this.score_total = this.countScoreTotal();
            
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
                trial = makeIndividualFromVector(v, x);
                if (checkFES()) {
                    return best;
                }

                /**
                 * New generation building
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                    /**
                     * Added for history based random selection
                     */
                    parrentArray[1].score += this.favor;
                    parrentArray[2].score += this.favor;
                    parrentArray[3].score += this.favor;
                } else {
                    newPop.add(x);
                    /**
                     * Added for history based random selection
                     */
                    if(parrentArray[1].score > this.punish) {
                        parrentArray[1].score -= this.punish;
                    }
                    if(parrentArray[2].score > this.punish) {
                        parrentArray[2].score -= this.punish;
                    }
                    if(parrentArray[2].score > this.punish) {
                        parrentArray[2].score -= this.punish;
                    }
                }
                this.score_total = this.countScoreTotal();

            }

            P = newPop;
            this.writeToRankHistory();

        }
    }
    
    /**
     *
     * @param vector
     * @param x
     * @return
     */
    protected Individual makeIndividualFromVector(double[] vector, Individual x) {

        Individual ind = new Individual();
        ind.id = x.id;
        ind.vector = vector;
        ind.score = x.score;
        ind.score_pos = x.score_pos.clone();
        constrain(ind);
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
     * a, b, c are randomly selected, but the selection is biased towards the individuals whose score is higher.
     *
     * @param xIndex
     * @return
     */
    @Override
    protected Individual[] getParents(int xIndex) {

        Individual[] parrentArray = new Individual[4];
        List<Integer> indexes = new ArrayList<>();
        int index, score_index, score_max;

        for (int i = 0; i < NP; i++) {
            if (i != xIndex) {
                indexes.add(i);
            }
        }

        parrentArray[0] = P.get(xIndex);
        score_max = this.score_total - parrentArray[0].score;

        /**
         * a
         */
        score_index = rndGenerator.nextInt(score_max) + 1;
        for(index = 0; index < indexes.size(); index++) {
            
            score_index -= (P.get(indexes.get(index))).score;
            if(score_index <= 0) {
                break;
            }
            
        }
        parrentArray[1] = P.get(indexes.get(index));
        indexes.remove(index);
        score_max -= parrentArray[1].score;

        /**
         * b
         */
        score_index = rndGenerator.nextInt(score_max) + 1;
        for(index = 0; index < indexes.size(); index++) {
            
            score_index -= (P.get(indexes.get(index))).score;
            if(score_index <= 0) {
                break;
            }
            
        }
        parrentArray[2] = P.get(indexes.get(index));
        indexes.remove(index);
        score_max -= parrentArray[2].score;

        /**
         * c
         */
        score_index = rndGenerator.nextInt(score_max) + 1;
        for(index = 0; index < indexes.size(); index++) {
            
            score_index -= (P.get(indexes.get(index))).score;
            if(score_index <= 0) {
                break;
            }
            
        }
        parrentArray[3] = P.get(indexes.get(index));

        return parrentArray;

    }
   /**
    * Sum score of the whole population.
    * 
    * @return 
    */
    protected int countScoreTotal() {
        
        int score = 0;
        
        score = this.P.stream().map((ind) -> ind.score).reduce(score, Integer::sum);
        
        return score;
    }
    
    /**
     * Writes the rank history into specified file in Wolfram Mathematica format.
     * @param path 
     */
    public void writeRankHistoryToFile(String path) {
        
        try {
            PrintWriter res_writer = new PrintWriter(path, "UTF-8");
            
            res_writer.print("{");
            
            for(int i = 0; i < this.rank_history.length; i++) {
                
                res_writer.print("{");
                for(int j = 0; j < this.rank_history[i].size(); j++) {
                    
                    res_writer.print(this.rank_history[i].get(j));
                    
                    if(j != this.rank_history[i].size()-1) {
                        res_writer.print(", ");
                    }
                    
                }
                res_writer.print("}");
                if(i != this.rank_history.length-1) {
                        res_writer.print(", ");
                    }
            }
            
            res_writer.print("}");
            
            res_writer.close();
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(DE_hbrs.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Writes best fitness value history into file.
     * @param path 
     */
    public void writeFitnessValueHistoryToFile(String path) {
        
        try {
            PrintWriter res_writer = new PrintWriter(path, "UTF-8");
            
            res_writer.print("{");
            
            for(int i = 0; i < this.bestHistory.size(); i++) {
                
                res_writer.print(String.format(Locale.US, "%.10f",this.bestHistory.get(i).fitness));
                
                
                if(i != this.bestHistory.size()-1) {
                        res_writer.print(", ");
                    }
            }
            
            res_writer.print("}");
            
            res_writer.close();
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(DE_hbrs.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
//        TestFunction tf = new Schwefel();
//        TestFunction tf = new Ackley();
//        TestFunction tf = new Dejong();
//        TestFunction tf = new Rosenbrock();
        TestFunction tf = new Rastrigin();
        util.random.Random generator = new util.random.UniformRandom();
        util.random.Random chaos = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8;
        int favor = 1, punish = 2;
        String path = "C:\\Users\\wiki\\Documents\\RankHistory\\" + tf.name() + "_history_" + favor + "f" + punish + "p.txt";
        String path2 = "C:\\Users\\wiki\\Documents\\RankHistory\\" + tf.name() + "_value_" + favor + "f" + punish + "p.txt";

        DE_hbrs de;

        int runs = 1;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new DE_hbrs(dimension, NP, MAXFES, tf, generator, f, cr, favor, punish);

            de.run();

            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
            de.writeRankHistoryToFile(path);
            de.writeFitnessValueHistoryToFile(path2);
            
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
