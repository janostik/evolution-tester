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
import model.tf.Cec2015;
import model.tf.Dejong;
import model.tf.Kromer;
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
    
    public double score_total;
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
    public Individual runAlgorithm() {

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
    * Sum score of the whole population.
    * 
    * @return 
    */
    protected double countScoreTotal() {
        
        double score = 0;
        
        score = this.P.stream().map((ind) -> ind.score).reduce(score, (accumulator, _item) -> accumulator + _item);
        
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
    
    public static void writeStatisticsOfScore(TestFunction[] tfs, int dimension, int NP, int iter) {
        
        
        
        for(int i = 0; i < tfs.length; i++) {

            int MAXFES = iter * NP;
            util.random.Random generator = new util.random.UniformRandom();
            util.random.Random chaos = new util.random.UniformRandom();
            double f = 0.5, cr = 0.8;
            int favor = 1, punish = 0;
            String path;
            String path2;

            DE_hbrs de;

            int runs = 30;
            double[] bestArray = new double[runs];

            for (int k = 0; k < runs; k++) {

                path = "C:\\Users\\wiki\\Documents\\RankHistory\\HBRS_" + favor + "f" + punish + "p\\" + tfs[i].name() + "_h_" + dimension + "d_" + NP + "ind_" + k + ".txt";
                path2 = "C:\\Users\\wiki\\Documents\\RankHistory\\HBRS_" + favor + "f" + punish + "p\\" + tfs[i].name() + "_v_" + dimension + "d_" + NP + "ind_" + k + ".txt";

                de = new DE_hbrs(dimension, NP, MAXFES, tfs[i], generator, f, cr, favor, punish);

                de.runAlgorithm();

                bestArray[k] = de.getBest().fitness - tfs[i].optimum();
                System.out.println(de.getBest().fitness - tfs[i].optimum());

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
    
    public static void writeCECstatistics(int[] dims, int[] NPs, int iter) throws Exception {
        
        int dimension;
        int NP;
        TestFunction[] tfs;
        
        for(int dimiter = 0; dimiter < dims.length; dimiter++) {
            
            dimension = dims[dimiter];
            
            for(int npiter = 0; npiter < NPs.length; npiter++) {
                
                NP = NPs[npiter];
                
                tfs = new TestFunction[15];
                for(int i = 1; i < 16; i++) {
                    tfs[i-1] = new Cec2015(dimension, i);
                }
                
                writeStatisticsOfScore(tfs, dimension, NP, iter);
                
            }
            
        }
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
//        int dimension;
//        int NP;
        int iter;
//        TestFunction[] tfs;
        iter = 1000;

        int[] NPs = {20,30,40,50,60,70,80,90,100};
        int[] dims = {10,30,50};
        
        writeCECstatistics(dims, NPs, iter);
        
//        dimension = 5;
//        NP = 50;
//        tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
//        writeStatisticsOfScore(tfs, dimension, NP, iter);

        
        
        
        
    }

    
    
}
