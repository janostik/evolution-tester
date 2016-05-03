package algorithm.de;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.random.Random;

/**
 *
 * SHADE algorithm with linear decrease of population based on static vector of individual ids.
 * 
 * @author wiki on 03/05/2016
 */
public class SNLs_SHADE extends SNLfv_SHADE {

    private String[] decrease_list;
    private int decrease_iter;
    
    public SNLs_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize, String[] decreaseList) {
        super(D, MAXFES, f, H, NP, rndGenerator, minPopSize);
        this.decrease_list = decreaseList;
        this.decrease_iter = 0;
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
        
        int diff = list.size() - size;
        List<String> ids_to_delete = new ArrayList<>();

        for(int i = 0; i < diff; i++){
            ids_to_delete.add(this.decrease_list[this.decrease_iter]);
            this.decrease_iter++;
        }
        
        List<Individual> toRet = new ArrayList<>();
        List<Individual> tmp = new ArrayList<>();
        toRet.addAll(list);

        list.stream().filter((ind) -> (ids_to_delete.contains(ind.id))).forEach((ind) -> {
            toRet.remove(ind);
            tmp.add(ind);
        });
        
        this.addToDeadlist(tmp);
        
        tmp.stream().forEach(this.net::removeBidirectionalEdgesForNode);

        return toRet;

    }
    
    @Override
    public String getName() {
        return "SNLs_SHADE";
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
        util.random.Random generator;

        String[] decrease_list = {"57","5","98","56","48","30","53","80","25","6","52","4","64","28","7","21","47","75","78","77","96","76","55","31","91","40","90","41","59","46","62","69","82","14","93","49","92","34","89","1","73","79","15","10","88","32","8","11","66","36","33","27","65","12","68","58","97","24","0","45","60","72","74","35","39","17","84","13","44","23","94","81","70","99","3","9","19","38","61","51"};
        long seed = 10304020L;
        SNLs_SHADE shade;

        int runs = 10;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            generator = new util.random.UniformRandomSeed(seed);
            shade = new SNLs_SHADE(dimension, MAXFES, tf, H, NP, generator, minNP, decrease_list);

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
            ((SNLs_SHADE)shade).net.getDegreeMap().entrySet().stream().forEach((entry) -> {
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
