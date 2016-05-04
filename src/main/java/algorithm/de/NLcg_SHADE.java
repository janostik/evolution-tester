package algorithm.de;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.random.Random;

/**
 *
 * SHADE algorithm with linear decrease of population.
 * The individual to be killed is selected based on its centrality order position.
 * The centrality order position is taken from the gaussian distribution with mean 0.5 and std 0.1.
 * 
 * @author adam on 04/05/2016
 */
public class NLcg_SHADE extends NLc_SHADE {

    public NLcg_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize) {
        super(D, MAXFES, f, H, NP, rndGenerator, minPopSize);
    }
    
    @Override
    public String getName() {
        return "NLcg_SHADE";
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
        Individual tmpInd;
        toRet.addAll(list);
        int diff = list.size() - size;

        for (int i = 0; i < diff; i++) {
            
            tmpInd = this.getIndividualToRemove(toRet);
            toRet.remove(tmpInd);
            this.net.removeBidirectionalEdgesForNode(tmpInd);
            
        }

        return toRet;

    }
    
    /**
     * 
     * Gets the individual which should be removed based on the gaussian distribution.
     * 
     * @param list
     * @return 
     */
    protected Individual getIndividualToRemove(List<Individual> list) {
        
        double gaus_value = OtherDistributionsUtil.normal(0.5, 0.1);
        if(gaus_value > 1){
            gaus_value = 1;
        }
        else if(gaus_value < 0) {
            gaus_value = 0;
        }
        int rank = ((int) (gaus_value * list.size())) % list.size();
        
        for(Individual ind : list){
            if(this.getPositionCentrality(ind) == rank) {
                return ind;
            }
        }
        
        return list.get(0);
    }
    
    /**
     * 
     * @param ind
     * @return 
     */
    protected int getPositionCentrality(Individual ind) {
        
        Map<Individual, Integer> degMap = this.net.getDegreeMap();
        int position = 0;
        
        int ind_degree = this.net.getDegreeMap().get(ind) == null ? 0 : this.net.getDegreeMap().get(ind);
        
        position = degMap.entrySet().stream().filter((entry) -> (entry.getValue() < ind_degree)).map((_item) -> 1).reduce(position, Integer::sum);
        
        return position;
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int dimension = 10;
        int NP = 100;
        int minNP = 20;
        int MAXFES = 100 * NP;
        int funcNumber = 14;
        TestFunction tf = new Schwefel();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        NLcg_SHADE shade;

        int runs = 10;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new NLcg_SHADE(dimension, MAXFES, tf, H, NP, generator, minNP);

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
            ((NLcg_SHADE)shade).net.getDegreeMap().entrySet().stream().forEach((entry) -> {
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
