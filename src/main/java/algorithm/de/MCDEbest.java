package algorithm.de;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Individual;
import model.chaos.RankedGenerator;
import model.tf.TestFunction;
import util.random.Random;

/**
 *
 * @author wiki
 */
public class MCDEbest extends DEbest {

    List<RankedGenerator> chaosGenerator;
    List<Double[]> chaosProbabilities = new ArrayList<>();
    int chosenChaos;

    public MCDEbest(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
        chaosGenerator = RankedGenerator.getAllChaosGenerators();
    }
    
    private void writeChaosProbabilities(){
        
        Double[] probs = new Double[this.chaosGenerator.size()];
        
        for(int i = 0; i < this.chaosGenerator.size();i++){
            probs[i] = this.chaosGenerator.get(i).rank;
        }
        
        this.chaosProbabilities.add(probs);
        
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
     * List of parents for mutation x, a, b, c
     *
     * @param xIndex
     * @return
     */
    @Override
    protected Individual[] getParents(int xIndex) {

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
        
        Individual[] parrentArray = new Individual[4];
        List<Integer> indexes = new ArrayList<>();
        int index;
        
        parrentArray[0] = P.get(xIndex);
        
        /**
         * best
         */
        index = getBestIndividualInPopulation();
        parrentArray[1] = P.get(index);

        for (int i = 0; i < NP; i++) {
            if (i != xIndex && i != index) {
                indexes.add(i);
            }
        }

        /**
         * b
         */
        index = chaosGenerator.get(chIndex).chaos.nextInt(indexes.size());
        parrentArray[2] = P.get(indexes.get(index));

        indexes.remove(index);

        /**
         * c
         */
        index = chaosGenerator.get(chIndex).chaos.nextInt(indexes.size());
        parrentArray[3] = P.get(indexes.get(index));

        return parrentArray;

    }

    /**
     * 
     * @return 
     */
    @Override
    public String getName() {
        return "MC-DEbest";
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
            Logger.getLogger(MCDEbest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * 
     * @return 
     */
    @Override
    public Individual run() {

        /**
         * Initial population
         */
        initializePopulation();
        if (checkFES()) {
            return best;
        }

        List<Individual> newPop;
        Individual x, trial;
        double[] u, v;
        Individual[] parrentArray;

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
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
