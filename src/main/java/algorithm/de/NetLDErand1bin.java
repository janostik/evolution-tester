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
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.random.Random;

/**
 *
 * @author adam on 18/01/2016
 */
public class NetLDErand1bin extends CDErand1bin {

    Net net = new Net();
    private final int minPopSize;
    private final int maxPopSize;

    public NetLDErand1bin(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR, Random chaosGenerator, int minPopSize) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR, chaosGenerator);
        this.minPopSize = minPopSize;
        this.maxPopSize = NP;
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

        List<Individual> newPop;
        Individual x, trial;
        double[] u, v;
        Individual[] parrentArray;
        Edge edge;

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
                trial = makeIndividualFromVector(v, parrentArray[0].id);
                if (checkFES()) {
                    return best;
                }

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

            }

            P = newPop;
            NP = (int) Math.round(this.maxPopSize - ((double) this.FES/(double) this.MAXFES)*(this.maxPopSize - this.minPopSize));
            P = this.resize(P, NP);

        }
    }
    
    /**
     *
     * @param list
     * @param size
     * @return
     */
    private List<Individual> resize(List<Individual> list, int size) {

        List<Individual> toRet = new ArrayList<>();
        List<Individual> tmp = new ArrayList<>();
        Net tmpNet = new Net(this.net);
        Individual tmpInd;
        tmp.addAll(list);

        for (int i = 0; i < size; i++) {
            tmpInd = tmpNet.getNodeWithHighestDegree();
            if(tmpInd == null){
                //If there are no nodes left with the edges, select the best according to fitness value
                tmpInd = this.getBestFromList(tmp);
            }
            //add node to return array
            toRet.add(tmpInd);
            //remove it from temporary list of nodes that are left
            tmp.remove(tmpInd);
            //remove edges from temporary network
            tmpNet.removeEdgesForNode(tmpInd);
        }
        
        //remove edges for the nodes which did not survive to the next gen
        tmp.stream().forEach(this.net::removeEdgesForNode);

        return toRet;

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
     *
     * @param vector
     * @return
     */
    protected Individual makeIndividualFromVector(double[] vector, String id) {

        Individual ind = new Individual();
        ind.id = id;
//        id++;
        ind.vector = vector;
        constrain(ind);
        ind.fitness = tf.fitness(vector);
        FES++;
        isBest(ind);
        writeHistory();

        return (ind);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 30;
        int NP = 100;
        int minNP = 4;
        int MAXFES = 100 * NP;
        int funcNumber = 5;
        TestFunction tf = new Schwefel();
        util.random.Random generator = new util.random.UniformRandom();
        util.random.Random chaos = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8;
        Net net;

        Algorithm de;

        int runs = 1;
        double[] bestArray = new double[runs];
        PrintWriter pw;

        for (int k = 0; k < runs; k++) {

            de = new NetLDErand1bin(dimension, NP, MAXFES, tf, generator, f, cr, chaos, minNP);

            de.run();
            
            System.out.println(((NetLDErand1bin)de).G);
            System.out.println("Node");
            if(((NetLDErand1bin) de).net.getNodeWithHighestDegree() != null) {
                System.out.println("ID: " + ((NetLDErand1bin) de).net.getNodeWithHighestDegree().id);
                System.out.println("Fitness: " + ((NetLDErand1bin) de).net.getNodeWithHighestDegree().fitness);
                System.out.println("Degree: " + ((NetLDErand1bin) de).net.getHighestDegree());
            }
            else {
                System.out.println("No edges in the net.");
            }

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
