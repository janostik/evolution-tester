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
import model.chaos.RankedGenerator;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.IndividualComparator;
import util.OtherDistributionsUtil;
import util.random.Random;

/**
 * SHADE with two random number generators for selecting indices, one aggresive (Delayed Logistic), one random
 * @author wiki on 19/09/2016
 */
public class Pst_SHADE extends SHADE{

    List<RankedGenerator> chaosGenerator;
    List<Double[]> chaosProbabilities = new ArrayList<>();
    List<List<Individual>> population_history = new ArrayList<>();
    List<Individual> bestHistoryPopwise = new ArrayList<>();
    int chosenChaos;
    double maxProbability = 0.9;
    double threshold = 0.8;

    public Pst_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator) {
        super(D, MAXFES, f, H, NP, rndGenerator);
        chaosGenerator = RankedGenerator.getCompetitiveSystems();
    }

    /**
     * ALGORITHMIC PART
     */
    
    /**
     * Creation of initial population.
     * Overrided becauser of chaos probabilities history.
     */
    @Override
    protected void initializePopulation(){
        
        /**
         * Initial population
         */
        id = 0;
        double[] features;
        this.P = new ArrayList<>();
        Individual ind;

        for (int i = 0; i < this.NP; i++) {
            id = i;
            features = this.f.generateTrial(this.D).clone();
            ind = new Individual(String.valueOf(id), features, this.f.fitness(features));
            this.isBest(ind);
            this.P.add(ind);
            this.FES++;
            this.writeHistory();
//            this.writeChaosProbabilities();
        }
        
        
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
        this.bestHistoryPopwise = new ArrayList<>();

        /**
         * Initial population
         */
        initializePopulation();
        
        this.bestHistoryPopwise.add(this.best);
        this.population_history.add(this.P);
        this.writeChaosProbabilities();

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
        List<Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial;
        Individual x, pbestInd;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2;
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
                 * Sorting
                 */
                pBestArray = this.sortIndividuals(pBestArray);
                this.P = this.sortIndividuals(this.P);
                
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
                
//                this.writeChaosProbabilities();

                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (this.FES >= this.MAXFES) {
                    break;
                }

                this.Aext = this.resizeAext(this.Aext, this.NP);
                
            }

            if (this.FES >= this.MAXFES) {
                this.population_history.add(newPop);
                this.bestHistoryPopwise.add(this.best);
                this.writeChaosProbabilities();
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

                this.M_F[k] = (meanS_F1 / meanS_F2);
                this.M_CR[k] = (meanS_CR1 / meanS_CR2);

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
            
            
            /**
             * Test whether aggresive systems probability is higher than preset threshold
             */
            if(this.chaosGenerator.get(1).rank > this.threshold) {
                this.NP = (int) (this.NP/2);
                if(this.NP < 20) {
                    this.NP = 20;
                } else {
                    this.chaosGenerator.stream().forEach((RankedGenerator gen) -> {
                        gen.rank = 1.0/ (double) this.chaosGenerator.size();
                    });
                }
                
                
                
            }
            
            this.P = this.resizePop(this.P, this.NP);
            this.Aext = this.resizeAext(this.Aext, this.NP);
            
            this.population_history.add(this.P);
            this.bestHistoryPopwise.add(this.best);
            this.writeChaosProbabilities();
            

        }

        return this.best;

    }
    
    /**
     * POPULATION DECREASE
     */
    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<Individual> resizePop(List<Individual> list, int size) {

        if(size == list.size()){
            return list;
        }
        
        List<Individual> toRet = new ArrayList<>();
        List<Individual> tmp = new ArrayList<>();
        tmp.addAll(list);
        Individual bestInd;

        for (int i = 0; i < size; i++) {
            bestInd = this.getBestFromList(tmp);
            toRet.add(bestInd);
            tmp.remove(bestInd);
        }

        return toRet;

    }
    
    /**
     * SORTING
     */
    
    /**
     * Sorts individuals in list
     * 
     * @param list
     * @return 
     */
    protected List<Individual> sortIndividuals(List<Individual> list){

        list.sort(new IndividualComparator());
        
        return list;
    }
    
    /**
     * GENERATOR PART
     */
    
    /**
     * 
     */
    private void writeChaosProbabilities(){
        
        Double[] probs = new Double[this.chaosGenerator.size()];
        
        for(int i = 0; i < this.chaosGenerator.size();i++){
            probs[i] = this.chaosGenerator.get(i).rank;
        }
        
        this.chaosProbabilities.add(probs);
        
    }
    
    /**
     *
     * @param list
     * @return
     */
    @Override
    protected Individual getRandBestFromList(List<Individual> list, String id) {

        int index = chaosGenerator.get(chosenChaos).chaos.nextInt(list.size());

        while(list.get(index).id.equals(id)) {
            index = chaosGenerator.get(chosenChaos).chaos.nextInt(list.size());
        }
        
        return list.get(index);

    }
    
    /**
     * Updates rank values for chaos generators.
     */
    protected void updateRankings() {

        if(chaosGenerator.get(chosenChaos).rank < maxProbability){
            double rankSum = 0, difference;

            rankSum = chaosGenerator.stream().map((chaos) -> chaos.rank).reduce(rankSum, (accumulator, _item) -> accumulator + _item);

            difference = rankSum / 100.0;
            chaosGenerator.get(chosenChaos).rank += difference;

            for (RankedGenerator chaos : chaosGenerator) {
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
    
    /**
     *
     * @param index
     * @param max1
     * @param max2
     * @return
     */
    @Override
    protected int[] genRandIndexes(int index, int max1, int max2, int pbest) {

        /**
         * Choose generator base on success
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
        
        while(a == pbest || a == index){
            a = chaosGenerator.get(chIndex).chaos.nextInt(max1);
        }
        
        b = chaosGenerator.get(chIndex).chaos.nextInt(max2);

        while (b == a || b == index || b == pbest) {
            b = chaosGenerator.get(chIndex).chaos.nextInt(max2);
        }

        return new int[]{a, b};
    }

    /**
     * 
     * Writes probabilities into the file given by the path in argument.
     * 
     * @param path 
     */
    public void writeProbsToFile(String path){
        
        try {
            int stop = this.chaosProbabilities.size() - /*(this.chaosProbabilities.size() % 100) -*/ 1;
            
            PrintWriter pw = new PrintWriter(path, "UTF-8");
            
            pw.write("{");
            
            for(int k = 0; k < this.chaosProbabilities.size(); k++){
                
//                if((k+1) % 100 == 0) {
                    pw.write("{");

                    for(int i = 0; i < this.chaosProbabilities.get(k).length; i++) {
                       pw.write(String.format(Locale.US, "%.10f", this.chaosProbabilities.get(k)[i]));

                       if(i != this.chaosProbabilities.get(k).length-1){
                           pw.write(",");
                       }
                    }

                    pw.write("}");

                    if(k != stop){
                        pw.write(",");
                    }
//                }
                
            }
            
            pw.write("}");
            
            pw.close();
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Pst_SHADE.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public String getName() {
        return "Pst_SHADE";
    }
    
    /**
     * MAINS
     * @param func
     * @param resultPath
     * @param probPath
     * @throws java.lang.Exception
     */
    
    public static void rankingTest(TestFunction func, String resultPath, String probPath) throws Exception {
        
        
        int MAXFES = 100_000;
        int H = 10;
        int NP = 1000;
        
        
        TestFunction tf;
        util.random.Random generator = new util.random.UniformRandom();

        Pst_SHADE de;

        double[] bestArray;
        PrintWriter writer, sol_writer, pop_writer;
        double best,worst,median,mean,std;
        String prPath = "";
        
        tf = func;
        bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new Pst_SHADE(dimension, MAXFES, tf, H, NP, generator);
            de.runAlgorithm();

            writer = new PrintWriter(home_dir + resultPath + tf.name() + "-" + k + ".txt", "UTF-8");
            pop_writer = new PrintWriter(home_dir + resultPath + tf.name() + "-population-" + k + ".txt", "UTF-8");

            writer.print("{");

            for (int i = 0; i < de.bestHistoryPopwise.size(); i++) {

                writer.print(String.format(Locale.US, "%.10f", de.bestHistoryPopwise.get(i).fitness));

                if (i != de.bestHistoryPopwise.size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");
            
            /**
             * Population writer
             */
            int p = 0, st;
            pop_writer.print("{");
            for(List<Individual> pop : de.population_history){
                
                st = 0;
                
                pop_writer.print("{");
                
                for(Individual ind : pop) {
                    
                    pop_writer.print("{");
                    pop_writer.print("{");
                    
                    for(int d = 0; d < dimension; d++) {
                        
                        pop_writer.print(String.format(Locale.US, "%.10f", ind.vector[d]));
                        
                        if(d != dimension-1) {
                            pop_writer.print(",");
                        }
                        
                    }
                    pop_writer.print("}, ");
                    pop_writer.print(String.format(Locale.US, "%.10f", ind.fitness));
                    
                    pop_writer.print("}");

                    if(st != pop.size()-1){
                        pop_writer.print(",");
                    }
                    st++;
                    
                }
                
                pop_writer.print("}");
                
                if(p != de.population_history.size()-1) {
                    pop_writer.print(",");
                }
                p++;
                
            }
            pop_writer.print("}");

            writer.close();
            pop_writer.close();

            bestArray[k] = de.getBest().fitness - tf.optimum();
            
            prPath = home_dir + probPath + "probs_" + tf.name() + "-" + k + ".txt";
            
            //writes chaos generator probabilities history into file
            de.writeProbsToFile(prPath);

        }

        best = DoubleStream.of(bestArray).min().getAsDouble();
        worst = DoubleStream.of(bestArray).max().getAsDouble();
        median = new Median().evaluate(bestArray);
        mean = new Mean().evaluate(bestArray);
        std = new StandardDeviation().evaluate(bestArray);

        sol_writer = new PrintWriter(home_dir + resultPath + "results_" + tf.name() + ".txt", "UTF-8");

        sol_writer.print("{");
        sol_writer.print(tf.name());
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", best));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", worst));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", median));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", mean));
        sol_writer.print(",");
        sol_writer.print(String.format(Locale.US, "%.10f", std));
        sol_writer.print("}");

        sol_writer.close();

        System.out.println(tf.name());
        System.out.println("=================================");
        System.out.println("Best: " + best);
        System.out.println("Worst: " + worst);
        System.out.println("Median: " + median);
        System.out.println("Mean: " + mean);
        System.out.println("Std: " + std);
        System.out.println("=================================");
        
    }
    
    public static int dimension = 10;
    public static String home_dir = "C:\\Users\\wiki\\Documents/";
    public static int runs = 30;
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        TestFunction tf;
        String path;
        
        path = "Pst_SHADE_pop/";
        
//        tf = new Dejong();
//        rankingTest(tf, path, path);
//        
//        tf = new Ackley();
//        rankingTest(tf, path, path);
//        
//        tf = new Schwefel();
//        rankingTest(tf, path, path);
//        
//        tf = new Rastrigin();
//        rankingTest(tf, path, path);
//        
//        tf = new Rosenbrock();
//        rankingTest(tf, path, path);

        for(int i = 5; i < 6; i++) {
            
            tf = new Cec2015(dimension, i);
            rankingTest(tf, path, path);
            
        }
        
        
    }
    
}
