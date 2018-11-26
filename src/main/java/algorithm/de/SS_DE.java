package algorithm.de;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import model.Individual;
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
import util.distance.SquaredEuclideanDistance;
import util.kmeans.ClusterComparator;
import util.kmeans.KMeans;
import util.kmeans.RandomPartition;
import util.random.Random;
import util.regression.LinearRegression;
import util.regression.Regression;

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
public class SS_DE extends DErand1bin {
    
    public double score_total;
    public List[] rank_history;
    public double CRr;
    public double[] std;
    public double[] Fxr2_xr3;
    
    Map<Integer, List<double[]>> score_map;
    Map<Integer, double[]> clustering_map;
    public int[] cluster_sizes;
    
    public SS_DE(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
        
        score_map = new HashMap<>();
        clustering_map = new HashMap<>();
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
     * 
     * Writes the score of current population itno the score_map
     * 
     */
    public void writeScoreToMap() {
        
        List<double[]> score_list;
        
        for(Individual ind : this.P) {
            
            score_list = this.score_map.get(Integer.parseInt(ind.id));
            if(score_list == null) {
                score_list = new ArrayList<>();
            }
            
            score_list.add(new double[]{this.G, ind.score_pos[0]});
            
            this.score_map.put(Integer.parseInt(ind.id), score_list);
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
    public Individual run() {

        /**
         * Initial population
         */
        initializePopulation();
        if (checkFES()) {
            return best;
        }
        
        /**
         * Initial score written to map
         */
        this.writeScoreToMap();
        
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
                     * Count cluster sizes
                     */
                    this.cluster_sizes = this.getClusterSizes(3);
                    
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
            
            this.writeScoreToMap();

        }
    }
    
    /**
     * 
     * Counts cluster sizes
     * 
     * @param cluster_count
     * @return 
     */
    public int[] getClusterSizes(int cluster_count) {
        
        int[] cluster_sizes_ar = new int[cluster_count];
        
        for(int i = 0; i < cluster_count; i++) {
            cluster_sizes_ar[i] = 0;
        }
        
        /**
         * clustering
         */
        
        double slope;
        Regression regr = new LinearRegression(); 
        
        /**
         * Create linear slopes for clustering
         */
        for(Map.Entry<Integer, List<double[]>> individual : this.score_map.entrySet()) {
            slope = regr.getRegressionParameters(individual.getValue())[0];
            this.clustering_map.put(individual.getKey(), new double[]{slope});
        }
        
        /**
         * Clustering part
         */
        KMeans kmeans = new KMeans(cluster_count, clustering_map, new RandomPartition(), new SquaredEuclideanDistance());
        kmeans.run();
        
        List<KMeans.Cluster> sorted_clusters = new ArrayList();
        sorted_clusters.addAll(kmeans.clusters);
        sorted_clusters.sort(new ClusterComparator());
        
        for(int i = 0; i < sorted_clusters.size(); i++) {
            cluster_sizes_ar[i] = sorted_clusters.get(i).getSize();
        }
        
        return cluster_sizes_ar;
        
    }
    
    /**
     * Counts the average division of the subtracted vector divided by std
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
            Logger.getLogger(SS_DE.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(SS_DE.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void writeStatisticsOfScore(TestFunction[] tfs, int dimension, int NP, int iter, int runs) {
        
        
        
        for(int i = 0; i < tfs.length; i++) {

            int MAXFES = iter * NP;
            util.random.Random generator = new util.random.UniformRandom();
            double f = 0.5, cr = 0.8;
            String path; //ranking
            String path2; //fitness
            String path3; //cluster sizes

            SS_DE de;

            path3 = "E:\\results\\CEC2015-SS_DE-10\\" + tfs[i].name() + "_cluster-sizes_" + dimension + "d_" + NP + "ind.txt";
            String cluster_sizes_string;
            int[] clus_sizes;
            
            cluster_sizes_string = "{";

            double[] bestArray = new double[runs];

            for (int k = 0; k < runs; k++) {

                path = "E:\\results\\CEC2015-SS_DE-10\\" + tfs[i].name() + "_rank_" + dimension + "d_" + NP + "ind_" + k + ".txt";
                path2 = "E:\\results\\CEC2015-SS_DE-10\\" + tfs[i].name() + "_fitness_" + dimension + "d_" + NP + "ind_" + k + ".txt";

                de = new SS_DE(dimension, NP, MAXFES, tfs[i], generator, f, cr);

                de.run();
                
                clus_sizes = de.cluster_sizes;
                cluster_sizes_string += "{";
                for(int m = 0; m < clus_sizes.length; m++) {
                    cluster_sizes_string += clus_sizes[m];
                    if(m != clus_sizes.length-1) {
                        cluster_sizes_string += ", ";
                    }
                }
                cluster_sizes_string += "}";
                if(k != runs-1) {
                    cluster_sizes_string += ", ";
                }

                bestArray[k] = de.getBest().fitness - tfs[i].optimum();
                System.out.println(de.getBest().fitness - tfs[i].optimum());

                de.writeRankHistoryToFile(path);
                de.writeFitnessValueHistoryToFile(path2);

            }
            
            cluster_sizes_string += "}";
            
            try {
                PrintWriter res_writer = new PrintWriter(path3, "UTF-8");
                res_writer.print(cluster_sizes_string);
                res_writer.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SS_DE.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SS_DE.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public static void writeCECstatistics(int[] dims, int[] NPs, int iter, int runs) throws Exception {
        
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
                
                writeStatisticsOfScore(tfs, dimension, NP, iter, runs);
                
            }
            
        }
        
    }
    
    public static void writeSelectedstatistics(int[] dims, int[] NPs, int iter, int runs) throws Exception {
        
        int dimension;
        int NP;
        TestFunction[] tfs;
        
        for(int dimiter = 0; dimiter < dims.length; dimiter++) {
            
            dimension = dims[dimiter];
            
            for(int npiter = 0; npiter < NPs.length; npiter++) {
                
                NP = NPs[npiter];
                
                tfs = new TestFunction[]{new Ackley(),new Dejong(), new Rastrigin(), new Rosenbrock(), new Schwefel()};
                
                writeStatisticsOfScore(tfs, dimension, NP, iter, runs);
                
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
        
        /**
         * Whole cec run
         */
        int iter;
        int runs;
        iter = 200;
        runs = 51;        

        int[] NPs = {20,30,40,50,60,70,80,90,100};
        int[] dims = {5,10,20};
 
        writeSelectedstatistics(dims, NPs, iter, runs);
        
        /**
         * Only selected functions
         */
//        int runs;
//        int iter;
//        int dimension;
//        int NP;
//        TestFunction[] tfs;  
//   
//        
//        dimension = 20;
//        NP = 20;
//        iter = 200;
//        runs = 30;
//        tfs = new TestFunction[]{new Schwefel(), new Ackley(), new Dejong(), new Rosenbrock(), new Rastrigin()};
//        writeStatisticsOfScore(tfs, dimension, NP, iter, runs);

        /**
         * One function
         */
//        int dimension = 10;
//        int NP = 20;
//        int MAXFES = 200 * NP;
//        int funcNumber = 5;
//        TestFunction tf = new Schwefel();
//        util.random.Random generator = new util.random.UniformRandom();
//        double f = 0.5, cr = 0.8;
//
//        Algorithm de;
//
//        int runs = 1;
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
