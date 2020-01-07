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
import util.random.Random;

/**
 *
 * DE current to pbest 1 algorithm with multi chaotic framework
 * 
 * @author wiki on 12/04/2016
 */
public class MCDEcurtopbest extends DEcurtopbest {

    List<RankedGenerator> chaosGenerator;
    List<Double[]> chaosProbabilities = new ArrayList<>();
    int chosenChaos;
    
    public MCDEcurtopbest(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
        chaosGenerator = RankedGenerator.getAllChaosGeneratorsV1();
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

        List<Individual> newPop, pbestList;
        Individual x, trial;
        double[] u, v;
        Individual[] parrentArray;
        double pmin = 2 / (double) this.NP;
        int Psize;
        
        /**
         * generation itteration
         */
        while (true) {

            G++;
            newPop = new ArrayList<>();

            /**
             * Iteration through all individuals in generation.
             */
            for (int xIter = 0; xIter < NP; xIter++) {

                Psize = (int) (rndGenerator.nextDouble(pmin, 0.2) * this.NP);
                if (Psize < 2) {
                    Psize = 2;
                }
                
                pbestList = new ArrayList<>();
                pbestList.addAll(this.P);
                pbestList = this.resizePop(pbestList, Psize);
                
                /**
                 * Parent selection
                 */
                parrentArray = getParents(pbestList, P, xIter);
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
                trial = makeIndividualFromVector(v);
                if (checkFES()) {
                    return best;
                }

                /**
                 * New generation building
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                    updateRankings();
                } else {
                    newPop.add(x);
                }
                
                this.writeChaosProbabilities();

            }

            P = newPop;

        }
    }
    
    /**
     *
     * List of parents for mutation x, pbest, r1, r2
     *
     * @param pbestList
     * @param pList
     * @param xIndex
     * @return
     */
    @Override
    protected Individual[] getParents(List<Individual> pbestList, List<Individual> pList, int xIndex) {

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
        
        Individual[] parentArray = new Individual[4];
        List<Integer> indexes = new ArrayList<>();
        int index;
        parentArray[0] = pList.get(xIndex);
        
        //Select pbest different from x
        parentArray[1] = pbestList.get(chaosGenerator.get(chIndex).chaos.nextInt(pbestList.size()));
        while(parentArray[1] == parentArray[0]){
            parentArray[1] = pbestList.get(chaosGenerator.get(chIndex).chaos.nextInt(pbestList.size()));
        }

        //Add indexes of the rest for selection of the r1 and r2
        for (int i = 0; i < pList.size(); i++) {
            if (pList.get(i) != parentArray[0] && pList.get(i) != parentArray[1]) {
                indexes.add(i);
            }
        }

        /**
         * r1
         */
        index = chaosGenerator.get(chIndex).chaos.nextInt(indexes.size());
        parentArray[2] = pList.get(indexes.get(index));
        
        //removes index of r1 from selection
        indexes.remove(index);

        /**
         * r2
         */
        index = chaosGenerator.get(chIndex).chaos.nextInt(indexes.size());
        parentArray[3] = pList.get(indexes.get(index));

        return parentArray;

    }
    
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
     * Updates rank values for chaos generators.
     */
    protected void updateRankings() {

        if(chaosGenerator.get(chosenChaos).rank < 0.6){
            double rankSum = 0, difference;

            rankSum = chaosGenerator.stream().map((chaos) -> chaos.rank).reduce(rankSum, (accumulator, _item) -> accumulator + _item);

            difference = rankSum / 100.0;
            chaosGenerator.get(chosenChaos).rank += difference;

            for (RankedGenerator chaos : chaosGenerator) {
                if(chaos.rank / (rankSum + difference) >= 0.1) {
                    chaos.rank = chaos.rank / (rankSum + difference);
                }
            }
        }

    }
    
    /**
     * 
     * Writes probabilities into the file given by the path in argument.
     * 
     * @param path 
     */
    public void writeProbsToFile(String path){
        
        try {
            int stop = this.chaosProbabilities.size() - (this.chaosProbabilities.size() % 100) - 1;
            
            PrintWriter pw = new PrintWriter(path, "UTF-8");
            
            pw.write("{");
            
            for(int k = 0; k < this.chaosProbabilities.size(); k++){
                
                if((k+1) % 100 == 0) {
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
                }
                
            }
            
            pw.write("}");
            
            pw.close();
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(MCDEcurtopbest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    @Override
    public String getName() {
        return "MC-DEcurtopbest";
    }
    
    
    
    /**
     * Overrided because of the chaos probabilities history.
     */
    @Override
    protected void initializePopulation() {

        P = new ArrayList<>();
        double[] vector;

        for (int i = 0; i < NP; i++) {

            if (checkFES()) {
                return;
            }
            vector = tf.generateTrial(D);
            P.add(makeIndividualFromVector(vector));
            this.writeChaosProbabilities();
            
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
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            int dimension = 10;
            int NP = 100;
            int MAXFES = 1000 * dimension;
            int funcNumber = 4;
            TestFunction tf = new Cec2015(dimension, funcNumber);
            util.random.Random generator = new util.random.UniformRandom();
            double f = 0.5, cr = 0.8;
            MCDEcurtopbest de;
            int runs = 10;
            double[] bestArray = new double[runs];
            for (int k = 0; k < runs; k++) {
                
                de = new MCDEcurtopbest(dimension, NP, MAXFES, tf, generator, f, cr);
                de.runAlgorithm();
                de.printOutRankings();
                
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
            
        } catch (Exception ex) {
            Logger.getLogger(MCDEcurtopbest.class.getName()).log(Level.SEVERE, null, ex);
        }

}
    
}
