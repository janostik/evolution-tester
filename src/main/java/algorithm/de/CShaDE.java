package algorithm.de;

import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author adam on 25/11/2015
 */
public class CShaDE extends ShaDE {

    util.random.Random chaosGenerator;
    
    public CShaDE(int D, int MAXFES, TestFunction f, int H, int NP, util.random.Random rndGenerator, util.random.Random chGenerator) {
        super(D, MAXFES, f, H, NP, rndGenerator);
        chaosGenerator = chGenerator;
    }

    @Override
    public String getName() {
        return "CShaDE";
    }
    
    /**
     *
     * @param list
     * @return
     */
    @Override
    protected Individual getRandBestFromList(List<Individual> list) {

        int index = chaosGenerator.nextInt(list.size());

        return list.get(index);

    }
    
    /**
     * 
     * @return 
     */
    @Override
    protected double getRandomCR(){
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

        int a, b;

        a = chaosGenerator.nextInt(max1);
        b = chaosGenerator.nextInt(max2);

        while (a == b || a == index || b == index) {
            a = chaosGenerator.nextInt(max1);
            b = chaosGenerator.nextInt(max2);
        }

        return new int[]{a, b};
    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        int dimension = 10;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 14;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 1;
        util.random.Random generator = new util.random.UniformRandom();
        util.random.Random chGenerator = new util.random.BurgersRandom();

        CShaDE shade;

        int runs = 10;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new CShaDE(dimension, MAXFES, tf, H, NP, generator, chGenerator);

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
