package algorithm.de;

import algorithm.Algorithm;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.net.BidirectionalEdge;
import model.net.Edge;
import model.net.Net;
import model.tf.Cec2015;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.IndividualComparator;
import util.random.Random;

/**
 *
 * Population evaluation only for active/2 and best/2 of individuals.
 * 
 * @author adam on 15/03/2017
 */
public class APE_DErand1bin extends DErand1bin {

    Net net = new Net();
    private final int maxPopSize;

    public APE_DErand1bin(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
        this.maxPopSize = NP;
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

        List<Individual> newPop;
        Individual x, trial;
        double[] u, v;
        Individual[] parrentArray;
        Edge edge;
        
        /**
         * Evaluation population
         * - in the first generation, all individuals are considered active and to be evaluated
         */
        List<Integer> eval_population = new ArrayList<>();
        for(int i = 0; i < NP; i++) {
            eval_population.add(i);
        }
        
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
                 * Individual should be evaluated
                 */
                if(eval_population.contains(xIter)) {
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
                    u = crossover(x.vector, v, CR);

                    /**
                     * Constrain check
                     */
                    u = constrainCheck(u, x.vector);
                    
                    /**
                     * Trial
                     */
                    trial = new Individual(x.id, u, tf.fitness(u));

                    /**
                     * New generation building
                     */
                    if (trial.fitness < x.fitness) {
                        newPop.add(trial);

                        for (int par = 1; par < parrentArray.length; par++ ) {
                            edge = new BidirectionalEdge(parrentArray[par], trial);
                            edge.iter = G;
                            net.addEdge(edge);
                        }

                    } else {
                        newPop.add(x);
                    }
                    
                    this.FES++;
                    this.isBest(trial);
                    this.writeHistory();
                    if (checkFES()) {
                        break;
                    }
                    
                }
                /**
                 * Individual is not evaluated
                 */
                else {
                    x = this.P.get(xIter);
                    newPop.add(x);
                }

            }
            
            if (this.FES >= this.MAXFES) {
                break;
            }

            P = newPop;
            
            /**
             * Get individuals for evaluation
             */
            eval_population = this.getEvalIndividuals();
            
//            System.out.println(eval_population.size() + " - " + eval_population.toString());
            
            net = new Net();

        }
        
        return this.best;
    }
    
    /**
     * Return ids of individuals that should be evaluated in the next generation
     * half is from best individuals
     * other half is from active individuals
     * 
     * @return 
     */
    protected List<Integer> getEvalIndividuals() {
        
        List<Integer> list_to_ret = new ArrayList<>();
        List<Individual> sortedPop = this.getSortedPopulation();
        List<Individual> activePop = this.getActivePopulation();
        int activeIndCount, bestSize, activeSize;
        Random rnd = new util.random.UniformRandom();
        int rndIndex;
        double ratio;
        
//        activeIndCount = (int) Math.round(this.NP - ((double) this.FES/(double) this.MAXFES)*(NP - 4));
        
        activeIndCount = activePop.size();
        ratio = (double) activeIndCount / (double) NP;
        
        if(activeIndCount == NP) {
            for(int i = 0; i < NP; i++) {
                list_to_ret.add(i);  
            }
            return list_to_ret;
        }
        
        if(activeIndCount < NP/5) {
            bestSize = NP/5;
            activeSize = activeIndCount;
        }
        else {
//            bestSize = activeIndCount/2;
//            activeSize = activeIndCount - bestSize;

//            activeSize = (int) (ratio * activeIndCount);
//            bestSize = activeIndCount - activeSize;

            bestSize = NP/5;
            activeSize = activeIndCount;
        }
        
        /**
         * Best individuals added
         */
        for(int i = 0; i < bestSize; i++) {
            list_to_ret.add(Integer.parseInt(sortedPop.get(i).id));
            activePop.remove(sortedPop.get(i));
        }
        
        /**
         * Random active individuals added
         */
        for(int i = 0; i < activeSize; i++) {
            if(activePop.isEmpty()) {
                break;
            }
            rndIndex = rnd.nextInt(activePop.size());
            list_to_ret.add(Integer.parseInt(activePop.get(rndIndex).id));
            activePop.remove(activePop.get(rndIndex));
        }
        
        return list_to_ret;
        
    }
    
    /**
     * Return active population
     * 
     * @return 
     */
    protected List<Individual> getActivePopulation() {
        
        List<Individual> activePop = new ArrayList<>();
        
        this.P.stream().filter((ind) -> (this.getActiveNode(ind) == 1)).forEach((ind) -> {
            activePop.add(ind);
        });
        
        return activePop;
        
    }
    
    /**
     * Returns sorted population
     * 
     * @return 
     */
    protected List<Individual> getSortedPopulation() {
        
        List<Individual> sortedList = new ArrayList<>();
        sortedList.addAll(P);
        
        sortedList.sort(new IndividualComparator());
        
        return sortedList;
        
    }
    
    /**
     * 
     * @param ind
     * @return 
     */
    protected int getActiveNode(Individual ind) {

        return this.net.getDegreeMap().get(ind) == null ? 0 : 1;
        
    }
    
    /**
     *
     * @param list
     * @return
     */
    private Individual getBestFromList(List<Individual> list) {

        Individual b = null;

        for (Individual ind : list) {

            if (b == null) {
                b = ind;
            } else if (ind.fitness < b.fitness) {
                b = ind;
            }
        }

        return b;

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        int NP = 100;
        int MAXFES = 100 * NP;
        int funcNumber = 5;
        TestFunction tf = new Schwefel();
//        TestFunction tf = new Cec2015(dimension, funcNumber);
        util.random.Random generator = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8;
        Net net;

        Algorithm de;

        int runs = 1;
        double[] bestArray = new double[runs];
        PrintWriter pw;

        for (int k = 0; k < runs; k++) {

            de = new APE_DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);

            de.runAlgorithm();
            
//            System.out.println(((APE_DErand1bin)de).G);
//            System.out.println("Node");
//            if(((APE_DErand1bin) de).net.getNodeWithHighestDegree() != null) {
//                System.out.println("ID: " + ((APE_DErand1bin) de).net.getNodeWithHighestDegree().id);
//                System.out.println("Fitness: " + ((APE_DErand1bin) de).net.getNodeWithHighestDegree().fitness);
//                System.out.println("Degree: " + ((APE_DErand1bin) de).net.getHighestDegree());
//            }
//            else {
//                System.out.println("No edges in the net.");
//            }

            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
           /**
            * NET manipulating
            */

//           pw = new PrintWriter("/Users/adam/Documents/RomanData/DErand/net_50iter_" + tf.name() + "_" + chaos.toString() + ".csv");
//
//           pw.println("source,target,iter;directed");
//           
//           net = ((NetLDErand1bin) de).net;
//
//           for(Edge edge : net.getEdges()){
//               
//               pw.println(edge.getSource().id + "," + edge.getTarget().id + "," + edge.iter + ",TRUE");
//               
//           }
//
//           pw.close();
            
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
