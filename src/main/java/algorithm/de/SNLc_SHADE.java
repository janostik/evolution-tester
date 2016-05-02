package algorithm.de;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.NetworkIndividual;
import model.net.Net;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.random.Random;

/**
 *
 * SHADE algorithm with linear decrease of population size.
 * Individuals that are going to be killed are selected based on their centrality.
 * 
 * Randomization here is seeded, therefore every run can end up in the same way.
 * 
 * @author wiki
 */
public class SNLc_SHADE extends SNLfv_SHADE {

    List<NetworkIndividual> dead_list;
    
    public SNLc_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize) {
        super(D, MAXFES, f, H, NP, rndGenerator, minPopSize);
        this.dead_list = new ArrayList<>();
    }
    
    /**
     *
     * @param list
     * @param size
     * @return
     */
    @Override
    protected List<Individual> resizePop(List<Individual> list, int size) {

        if(size == list.size()){
            return list;
        }
        
        List<Individual> toRet = new ArrayList<>();
        List<Individual> tmp = new ArrayList<>();
        Net tmpNet = new Net(this.net);
        Individual tmpInd;
        int bestIt = 0;
        tmp.addAll(list);
        
        for(int i = 0; i < bestIt; i++) {
            tmpInd = this.getBestFromList(tmp);
            //add node to return array
            toRet.add(tmpInd);
            //remove it from temporary list of nodes that are left
            tmp.remove(tmpInd);
            //remove edges from temporary network
            tmpNet.removeEdgesForNode(tmpInd);
        }

        for (int i = bestIt; i < size; i++) {
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
            tmpNet.removeBidirectionalEdgesForNode(tmpInd);
        }
        
        this.addToDeadlist(tmp);
        
        //remove edges for the nodes which did not survive to the next gen
        tmp.stream().forEach(this.net::removeBidirectionalEdgesForNode);

        return toRet;

    }
    
    @Override
    public String getName() {
        return "SNLc_SHADE";
    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        int NP = 100;
        int minNP = 20;
        int MAXFES = 100 * NP;
        int funcNumber = 14;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 10;
        util.random.Random generator;

        long seed = 10304020L;
        SNLc_SHADE shade;

        int runs = 1;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            generator = new util.random.UniformRandomSeed(seed);
            shade = new SNLc_SHADE(dimension, MAXFES, tf, H, NP, generator, minNP);

            shade.run();

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

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println(shade.getBest().fitness - tf.optimum());
            System.out.println(Arrays.toString(shade.getBest().vector));
            
            System.out.println("-------------------");
            ((SNLc_SHADE)shade).net.getDegreeMap().entrySet().stream().forEach((entry) -> {
                System.out.println("ID: " + entry.getKey().id + " - degree: " + entry.getValue() + " - fitness: " + entry.getKey().fitness);
            });
            System.out.println("-------------------");
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
