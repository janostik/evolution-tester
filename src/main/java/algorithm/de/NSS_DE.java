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
import model.net.Edge;
import model.net.Net;
import model.net.UnidirectionalEdge;
import model.tf.Ackley;
import model.tf.Cec2015;
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
 * Differential Evolution algorithm with Scaled Score accoridng to crossover and mutation
 * 
 * w_i = (1 - CRr)
 * w_r1 = CRr * (1/(1+2*AVG(abs(F(x_r1-x_r2))/std)))
 * w_r2 = CRr * AVG(abs(F(x_r1-x_r2))/std)/(1+2*AVG(abs(F(x_r1-x_r2))/std))
 * w_r3 = CRr * AVG(abs(F(x_r1-x_r2))/std)/(1+2*AVG(abs(F(x_r1-x_r2))/std))
 * 
 * @author wiki on 11/04/2017
 */
public class NSS_DE extends DErand1bin {
    
    public double score_total;
    public List[] rank_history;
    public double CRr;
    public double[] std;
    public double[] Fxr2_xr3;
    
    Net basic_net = new Net();
    Net scaled_net = new Net();
    
    public NSS_DE(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
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
            this.rank_history[Integer.parseInt(ind.id)].add(new double[]{Double.parseDouble(ind.id), ind.fitness - tf.optimum(), ind.score, ind.score_pos[0], ind.score_pos[1]});
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
        double w_i, w_r1, w_r2, w_r3, avg_division;
        Edge edge;
        
        /**
         * generation itteration
         */
        while (true) {

            this.countStd();
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
                v = mutation(parrentArray, F);

                /**
                 * Crossover
                 */
                u = crossover(CR, v , x.vector);

                /**
                 * Trial
                 */
                trial = makeIndividualFromVector(u, x);
                if (checkFES()) {
                    
                    /**
                     * write nets
                     */
                    this.writeNet(basic_net, "E:\\results\\NSS_DE-nets\\" + tf.name() + "_basic_" + D + "d_" + NP + "ind");
                    this.writeNet(scaled_net, "E:\\results\\NSS_DE-nets\\" + tf.name() + "_scaled_" + D + "d_" + NP + "ind");
                    
                    return best;
                }

                /**
                 * New generation building
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);

                    avg_division = this.countAverageDivision(this.Fxr2_xr3, this.std);
                    
                    w_i = (1-this.CRr);
                    w_r1 = this.CRr*(1/(1+2*avg_division));
                    w_r2 = this.CRr*avg_division/(1+2*avg_division);
                    w_r3 = this.CRr*avg_division/(1+2*avg_division);
                    
                    //x_i edge - not here
//                    edge = new UnidirectionalEdge(parrentArray[0], trial, w_i);
//                    edge.iter = G;
//                    basic_net.addEdge(edge);
                    //x_r1 edge
                    edge = new UnidirectionalEdge(parrentArray[1], trial, 1);
                    edge.iter = G;
                    basic_net.addEdge(edge);
                    //x_r2 edge
                    edge = new UnidirectionalEdge(parrentArray[2], trial, 1);
                    edge.iter = G;
                    basic_net.addEdge(edge);
                    //x_r3 edge
                    edge = new UnidirectionalEdge(parrentArray[3], trial, 1);
                    edge.iter = G;
                    basic_net.addEdge(edge);
                    
                    //x_i edge - not here
                    edge = new UnidirectionalEdge(parrentArray[0], trial, w_i);
                    edge.iter = G;
                    scaled_net.addEdge(edge);
                    //x_r1 edge
                    edge = new UnidirectionalEdge(parrentArray[1], trial, w_r1);
                    edge.iter = G;
                    scaled_net.addEdge(edge);
                    //x_r2 edge
                    edge = new UnidirectionalEdge(parrentArray[2], trial, w_r2);
                    edge.iter = G;
                    scaled_net.addEdge(edge);
                    //x_r3 edge
                    edge = new UnidirectionalEdge(parrentArray[3], trial, w_r3);
                    edge.iter = G;
                    scaled_net.addEdge(edge);
                    
                    parrentArray[0].score += w_i;
                    parrentArray[1].score += w_r1;
                    parrentArray[2].score += w_r2;
                    parrentArray[3].score += w_r3;
                    
                    parrentArray[0].score_pos[0] += 1;
                    parrentArray[1].score_pos[0] += 1;
                    parrentArray[2].score_pos[0] += 1;
                    parrentArray[3].score_pos[0] += 1;
                    
                    parrentArray[0].score_pos[1] += w_i;
                    parrentArray[1].score_pos[1] += this.CRr*(1/(1+2*F));
                    parrentArray[2].score_pos[1] += this.CRr*(F/(1+2*F));
                    parrentArray[3].score_pos[1] += this.CRr*(F/(1+2*F));
                    
                } else {
                    newPop.add(x);
                }
                this.score_total = this.countScoreTotal();

            }
            
            P = newPop;
            this.writeToRankHistory();

        }
    }
    
    /**
     * Writes net into selected file by path
     * 
     * @param net
     * @param path 
     */
    protected void writeNet(Net net, String path) {
        
        try {
            PrintWriter start_writer = new PrintWriter(path + "_start.csv", "UTF-8");
            PrintWriter middle_writer = new PrintWriter(path + "_middle.csv", "UTF-8");
            PrintWriter end_writer = new PrintWriter(path + "_end.csv", "UTF-8");
            
            start_writer.println("source,target,iter,weight,directed");
            middle_writer.println("source,target,iter,weight,directed");
            end_writer.println("source,target,iter,weight,directed");

            for(int i = 0; i < NP; i++) {
                start_writer.println(i + "," + i + "," + 0 + "," + 0 + ",TRUE");
                middle_writer.println(i + "," + i + "," + 0 + "," + 0 + ",TRUE");
                end_writer.println(i + "," + i + "," + 0 + "," + 0 + ",TRUE");
            }
            
            for(Edge edge : net.getEdges()){
                if(edge.iter < 10) {
                    start_writer.println(edge.getSource().id + "," + edge.getTarget().id + "," + edge.iter + "," + edge.weight + ",TRUE");
                }
                else if(edge.iter > 19 && edge.iter < 30) {
                    middle_writer.println(edge.getSource().id + "," + edge.getTarget().id + "," + edge.iter + "," + edge.weight + ",TRUE");
                }
                else if(edge.iter > 39 && edge.iter < 50) {
                    end_writer.println(edge.getSource().id + "," + edge.getTarget().id + "," + edge.iter + "," + edge.weight + ",TRUE");
                }


            }

            start_writer.close();
            middle_writer.close();
            end_writer.close();
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(NSS_DE.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Counts teh average division of the subtracted vector divided by std
     * 
     * @param a
     * @param b
     * @return 
     */
    protected double countAverageDivision(double[] a, double[] b) {
        
        double[] array = new double[this.D];
        for(int i = 0; i < this.D; i++) {
            array[i] = a[i]/b[i];
        }
        
        return new Mean().evaluate(array);
        
    }
    
    /**
     * Counts Standard Deviation from the population
     */
    protected void countStd() {
        
        this.std = new double[D];
        
        double[][] components = new double[this.D][this.P.size()];
        double[] vect;
        
        for(int i = 0; i < this.P.size(); i++) {
            vect = this.P.get(i).vector;
            for(int j = 0; j < this.D; j++) {
                components[j][i] = vect[j];
            }
        }

        for(int i = 0; i < this.D; i++) {
            this.std[i] = new StandardDeviation().evaluate(components[i]);
        }

    }
    
    /**
     *
     * @param parentArray
     * @param F
     * @return
     */
    @Override
    protected double[] mutation(Individual[] parentArray, double F) {

        double[] u = new double[D];
        double[] a = parentArray[1].vector;
        double[] b = parentArray[2].vector;
        double[] c = parentArray[3].vector;

        this.Fxr2_xr3 = new double[D];
        
        for (int i = 0; i < D; i++) {

            this.Fxr2_xr3[i] = F * (b[i] - c[i]);
            u[i] = a[i] + this.Fxr2_xr3[i];
            this.Fxr2_xr3[i] = Math.abs(this.Fxr2_xr3[i]);

        }

        return u;

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
                    
//                    res_writer.print(this.rank_history[i].get(j));
                    
                    res_writer.print("{");
                    for(int k = 0; k < ((double[]) this.rank_history[i].get(j)).length; k++) {
                        res_writer.print(String.format(Locale.US, "%.10f",((double[]) this.rank_history[i].get(j))[k]));
                        
                        if(k != ((double[]) this.rank_history[i].get(j)).length-1) {
                            res_writer.print(", ");
                        }
                    }
                    res_writer.print("}");

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
            Logger.getLogger(NSS_DE.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(NSS_DE.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void writeStatisticsOfScore(TestFunction[] tfs, int dimension, int NP, int iter) {
        
        
        
        for(int i = 0; i < tfs.length; i++) {

            int MAXFES = iter * NP;
            util.random.Random generator = new util.random.UniformRandom();
            double f = 0.5, cr = 0.8;
            String path;
            String path2;

            NSS_DE de;

            int runs = 1;
            double[] bestArray = new double[runs];

            for (int k = 0; k < runs; k++) {

                path = "E:\\results\\CEC2015-SS_DE-10\\" + tfs[i].name() + "_rank_" + dimension + "d_" + NP + "ind_" + k + ".txt";
                path2 = "E:\\results\\CEC2015-SS_DE-10\\" + tfs[i].name() + "_fitness_" + dimension + "d_" + NP + "ind_" + k + ".txt";

                de = new NSS_DE(dimension, NP, MAXFES, tfs[i], generator, f, cr);

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
     * 
     * @return 
     */
    protected double getRandomCR(){
        return rndGenerator.nextDouble();
    }
    
    /**
     * 
     * @param CR
     * @param v
     * @param x
     * @return 
     */
    protected double[] crossover(double CR, double[] v, double[] x){
        
        int CRcount = 0;
        
        /**
         * Crossover
         */
        double[] u = new double[this.D];
        int jrand = rndGenerator.nextInt(this.D);

        for (int j = 0; j < this.D; j++) {
            if (getRandomCR() <= CR || j == jrand) {
                u[j] = v[j];
                CRcount++;
            } else {
                u[j] = x[j];
            }
        }
        
        this.CRr = (double) CRcount / (double) this.D;
        
        return u;
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        int dimension;
        int NP;
        int iter;
        TestFunction[] tfs;
        iter = 50;
//
        int[] NPs = {20};
        int[] dims = {10};
//        
//        writeCECstatistics(dims, NPs, iter);
//        
        dimension = 5;
        NP = 30;
        tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
        writeStatisticsOfScore(tfs, dimension, NP, iter);

//        int dimension = 10;
//        int NP = 100;
//        int MAXFES = 100 * NP;
//        int funcNumber = 5;
//        TestFunction tf = new Schwefel();
//        util.random.Random generator = new util.random.UniformRandom();
//        double f = 0.5, cr = 0.8;
//        Net net;
//
//        Algorithm de;
//
//        int runs = 10;
//        double[] bestArray = new double[runs];
//
//        for (int k = 0; k < runs; k++) {
//
//            de = new SS_DE(dimension, NP, MAXFES, tf, generator, f, cr);
//
//            de.run();
//
//            bestArray[k] = de.getBest().fitness - tf.optimum();
//            System.out.println(de.getBest().fitness - tf.optimum());
//            
//        }
//
//        System.out.println("=================================");
//        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
//        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
//        System.out.println("Mean: " + new Mean().evaluate(bestArray));
//        System.out.println("Median: " + new Median().evaluate(bestArray));
//        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
//        System.out.println("=================================");
        
        
        
    }

    
    
}
