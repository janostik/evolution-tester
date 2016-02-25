package algorithm.de;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.Individual;
import model.chaos.RankedChaosGenerator;
import model.tf.Network2;
import model.tf.Network3;
import model.tf.Network4;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;

/**
 *
 * @author adam on 25/11/2015
 */
public class MCShaDE extends ShaDE {

    List<RankedChaosGenerator> chaosGenerator;
    int chosenChaos;


    public MCShaDE(int D, int MAXFES, TestFunction f, int H, int NP, util.random.Random rndGenerator) {
        super(D, MAXFES, f, H, NP, rndGenerator);
        chaosGenerator = RankedChaosGenerator.getAllChaosGenerators();
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
        this.M_CR = new double[this.H];

        for (int h = 0; h < this.H; h++) {
            this.M_F[h] = 0.5;
            this.M_CR[h] = 0.5;
        }

        /**
         * Generation iteration;
         */
        int r, Psize;
        double Fg, CRg;
        List<Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial;
        Individual x;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR;
        int k = 0;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;

        while (true) {

            this.G++;
            this.S_F = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();

            newPop = new ArrayList<>();

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
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
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size());
                pbest = this.getRandBestFromList(pBestArray).vector.clone();
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
                    this.S_CR.add(CRg);
                    this.Aext.add(x);
                    wS.add(Math.abs(trial.fitness - x.fitness));

                    /**
                     * Chosen chaos rank update
                     */
                    updateRankings();

                } else {
                    newPop.add(x);
                }

                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (this.FES >= this.MAXFES) {
                    break;
                }

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
                meanS_CR = 0;

                for (int s = 0; s < this.S_F.size(); s++) {
                    meanS_F1 += (wS.get(s) / wSsum) * this.S_F.get(s) * this.S_F.get(s);
                    meanS_F2 += (wS.get(s) / wSsum) * this.S_F.get(s);
                    meanS_CR += (wS.get(s) / wSsum) * this.S_CR.get(s);
                }

                this.M_F[k] = (meanS_F1 / meanS_F2);
                this.M_CR[k] = meanS_CR;

                k++;
                if (k >= this.H) {
                    k = 0;
                }
            }

            /**
             * Resize of population and archives
             */
            this.P = new ArrayList<>();
            this.P.addAll(newPop);
            this.Aext = this.resizeAext(this.Aext, this.NP);

        }

        return this.best;

    }
    
    /**
     *
     * @param list
     * @return
     */
    @Override
    protected Individual getRandBestFromList(List<Individual> list) {

        int index = chaosGenerator.get(chosenChaos).chaos.nextInt(list.size());

        return list.get(index);

    }
    
    /**
     * Updates rank values for chaos generators.
     */
    protected void updateRankings() {

        if(chaosGenerator.get(chosenChaos).rank < 0.6){
            double rankSum = 0, difference;

            rankSum = chaosGenerator.stream().map((chaos) -> chaos.rank).reduce(rankSum, (accumulator, _item) -> accumulator + _item);

            difference = rankSum / 100.0;
            chaosGenerator.get(chosenChaos).rank += difference;

            for (RankedChaosGenerator chaos : chaosGenerator) {
                chaos.rank = chaos.rank / (rankSum + difference);
            }
        }


    }

    public void printOutRankings() {

        System.out.println("Ranking");
        chaosGenerator.stream().forEach((chaos) -> {
            System.out.print(chaos.rank + " ");
        });
        System.out.println("");

    }

    @Override
    public String getName() {
        return "MC-ShaDE";
    }

    /**
     *
     * @return
     */
    @Override
    protected double getRandomCR() {
        return rndGenerator.nextDouble();
    }

    /**
     *
     * @param index
     * @param max1
     * @param max2
     * @return
     */
    @Override
    protected int[] genRandIndexes(int index, int max1, int max2) {

        /**
         * TODO choose chaos base on rank
         */
        double prob = rndGenerator.nextDouble();
        int chIndex = 0;

        do {
            prob -= chaosGenerator.get(chIndex).rank;
            chIndex++;
            if (chIndex == chaosGenerator.size()) {
                break;
            }
        } while (prob > 0);

        chIndex -= 1;
        chosenChaos = chIndex;

        int a, b;

        a = chaosGenerator.get(chIndex).chaos.nextInt(max1);
        b = chaosGenerator.get(chIndex).chaos.nextInt(max2);

        while (a == b || a == index || b == index) {
            a = chaosGenerator.get(chIndex).chaos.nextInt(max1);
            b = chaosGenerator.get(chIndex).chaos.nextInt(max2);
        }

        return new int[]{a, b};
    }

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void mainNetwork2(String[] args) throws Exception {
        int dimension = 50;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 14;
        TestFunction tf = new Network2();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        MCShaDE shade;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            shade = new MCShaDE(dimension, MAXFES, tf, H, NP, generator);

            shade.run();
            shade.printOutRankings();

            bestArray[k] = shade.getBest().fitness;
            
            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + shade.getBest().fitness);
            tf.fitness(shade.getBest());
            
            System.out.println("Path: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2)tf).getNode_path().size(); p++) {
                System.out.print(((Network2)tf).getNode_path().get(p));
                
                if(p != ((Network2)tf).getNode_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Built paths: ");
            System.out.print("{");
            for(int p = 0; p < ((Network2)tf).getBuilt_path().size(); p++) {
                pa = ((Network2)tf).getBuilt_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network2)tf).getBuilt_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            
            for(Individual ind : ((MCShaDE)shade).getBestHistory()){
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
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void mainNetwork3(String[] args) throws Exception {
        int dimension = 50;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 14;
        TestFunction tf = new Network3();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        MCShaDE shade;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            shade = new MCShaDE(dimension, MAXFES, tf, H, NP, generator);

            shade.run();
            shade.printOutRankings();

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
            bestArray[k] = shade.getBest().fitness;
            
            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + shade.getBest().fitness);
            tf.fitness(shade.getBest());
            
            System.out.println("X: " + Arrays.toString(((Network4)tf).getX()));
            System.out.println("NodeLoad: " + Arrays.toString(((Network3)tf).getNodeLoad()));
            
            System.out.println("PathLoad: ");
            System.out.print("{");
            for(int a = 0; a < ((Network3)tf).getNodeCount(); a++){
                System.out.print("{");
                for(int b = 0; b < ((Network3)tf).getNodeCount(); b++){
                    
                    System.out.print(((Network3)tf).getPathLoad()[a][b]);
                    
                    if(b != ((Network3)tf).getNodeCount() - 1){
                        System.out.print(", ");
                    }  
                    
                }
                System.out.print("}");
                
                if(a != ((Network3)tf).getNodeCount() - 1){
                    System.out.println(", ");
                } 
            }
            System.out.println("}");
            
            System.out.println("Path: ");
            System.out.print("{");
            for(int p = 0; p < ((Network3)tf).getNode_path().size(); p++) {
                pa = ((Network3)tf).getNode_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network3)tf).getNode_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Built paths: ");
            System.out.print("{");
            for(int p = 0; p < ((Network3)tf).getBuilt_path().size(); p++) {
                pa = ((Network3)tf).getBuilt_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network3)tf).getBuilt_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            
            for(Individual ind : ((MCShaDE)shade).getBestHistory()){
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
    
    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void mainNetwork4(String[] args) throws Exception {
        int dimension = 100;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 14;
        TestFunction tf = new Network4();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        MCShaDE shade;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;
        
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            shade = new MCShaDE(dimension, MAXFES, tf, H, NP, generator);

            shade.run();
            shade.printOutRankings();

            PrintWriter writer;

            try {
                writer = new PrintWriter(tf.name() + "-mcshade" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int z = 0; z < shade.getBestHistory().size(); z++) {

                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(z).fitness));

                    if (z != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                
            }
            bestArray[k] = shade.getBest().fitness;
            
            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + shade.getBest().fitness);
            tf.fitness(shade.getBest());
            
            System.out.println("X: " + Arrays.toString(((Network4)tf).getX()));
            System.out.println("NodeLoad: " + Arrays.toString(((Network4)tf).getNodeLoad()));
            
            System.out.println("PathLoad: ");
            System.out.print("{");
            for(int a = 0; a < ((Network4)tf).getNodeCount(); a++){
                System.out.print("{");
                for(int b = 0; b < ((Network4)tf).getNodeCount(); b++){
                    
                    System.out.print(((Network4)tf).getPathLoad()[a][b]);
                    
                    if(b != ((Network4)tf).getNodeCount() - 1){
                        System.out.print(", ");
                    }  
                    
                }
                System.out.print("}");
                
                if(a != ((Network4)tf).getNodeCount() - 1){
                    System.out.println(", ");
                } 
            }
            System.out.println("}");
            
            System.out.println("Path: ");
            System.out.print("{");
            for(int p = 0; p < ((Network4)tf).getNode_path().size(); p++) {
                pa = ((Network4)tf).getNode_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network4)tf).getNode_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            System.out.println("Built paths: ");
            System.out.print("{");
            for(int p = 0; p < ((Network4)tf).getBuilt_path().size(); p++) {
                pa = ((Network4)tf).getBuilt_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network4)tf).getBuilt_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
            
            
            for(Individual ind : ((MCShaDE)shade).getBestHistory()){
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
    
    /**
     * MAIN
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    
//        MCShaDE.mainNetwork2(args);
//        MCShaDE.mainNetwork3(args);     
        MCShaDE.mainNetwork4(args);    
    }

}
