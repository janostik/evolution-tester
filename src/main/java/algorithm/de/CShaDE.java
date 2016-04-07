package algorithm.de;

import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.nwf.Network2;
import model.tf.nwf.Network3;
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
        int dimension = 50;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        int funcNumber = 14;
//        TestFunction tf = new Cec2015(dimension, funcNumber);
        TestFunction tf = new Network3();        
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();
        util.random.Random chGenerator = new util.random.BurgersRandom();

        CShaDE shade;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;
        double min;
        Integer[] pa;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            shade = new CShaDE(dimension, MAXFES, tf, H, NP, generator, chGenerator);

            shade.run();

            bestArray[k] = shade.getBest().fitness;
            System.out.println("Individual: " + Arrays.toString(shade.getBest().vector));
            System.out.println("Profit: " + shade.getBest().fitness);
            tf.fitness(shade.getBest());
            
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
            
            
            for(Individual ind : ((CShaDE)shade).getBestHistory()){
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
    
}
