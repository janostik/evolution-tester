
import algorithm.de.Lfv_SHADE;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;
import model.tf.TestFunction;
import model.tf.nwf.Spalovny_projekt;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author wiki on 31/01/2017
 */
public class SpalovnyProjektMain {

    public static int maxfes = 1000000;
    public static int runs = 3;
    
    /**
     * 
     * Main to solve spalovny in Olomoucky kraj
     * 
     * @throws Exception 
     */
    public static void olomoucMain() throws Exception {
        
        /**
         * Zlinsky kraj
         */
        int[] use_prod = new int[]{93,94,95,96,97,98,99,100,101,102,103,104};
        int[] use_inc = new int[]{7,8,21};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);;

        double[] bestArray = new double[runs];
        int i, best;
        double min;

        System.out.println("START\n" + new Date());
        System.out.println("SETTINGS:\n=================================");
        System.out.println("=================================");
        System.out.println("Problem: " + tf.name());
        System.out.println("Dimension: " + dimension);
        System.out.println("-------------");
        System.out.println("Algorithm: " + shade.getName());
        System.out.println("NP: " + NP);
        System.out.println("NP final: " + NPfinal);
        System.out.println("Max. OFEs: " + MAXFES);
        System.out.println("H: " + H);
        System.out.println("Runs: " + runs);
        System.out.println("=================================");
        System.out.println("=================================");
        
        for (int k = 0; k < runs; k++) {
            
            generator = new util.random.UniformRandom();
            shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

            shade.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println("===============SOLUTION " + (k+1) + "===============");
            System.out.println("TIME\n" + new Date());
            System.out.println("OFV\n" + (shade.getBest().fitness - tf.optimum()));
            System.out.println("SOLUTION\n" + Arrays.toString(shade.getBest().vector));
            
            Map<String, List> map = ((Spalovny_projekt)tf).getOutput(shade.getBest().vector);
            
            System.out.println("=================================");
            String line;
          
            if(map != null){
                for(Map.Entry<String,List> entry : map.entrySet()){
                    line = "";
                    System.out.print(entry.getKey() + " = ");
                    line += "{";
    //                System.out.print("{");
                    for(int pup = 0; pup < entry.getValue().size(); pup++){
    //                    System.out.print(entry.getValue().get(pup));
                        line += entry.getValue().get(pup);
                        if(pup != entry.getValue().size()-1){
    //                       System.out.print(","); 
                           line += ",";
                        }
                    }
    //                System.out.println("}");
                    line += "};";
                    line = line.replace("[", "{");
                    line = line.replace("]", "}");
                    System.out.println(line);

                }
            }
            
            System.out.println("=================================");
            
//            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
//                i++;
//                if(ind.fitness < min){
//                    min = ind.fitness;
//                    best = i;
//                }
//            }
//            System.out.println("Best solution found in " + best + " CFE");
            
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
    }
    
    /**
     * 
     * Main to solve spalovny in Jihomoravsky kraj
     * 
     * @throws Exception 
     */
    public static void jmMain() throws Exception {
        
        /**
         * JM kraj
         */
        int[] use_prod = new int[]{18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38};
        int[] use_inc = new int[]{1,6,34};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);;

        double[] bestArray = new double[runs];
        int i, best;
        double min;

        System.out.println("START\n" + new Date());
        System.out.println("SETTINGS:\n=================================");
        System.out.println("=================================");
        System.out.println("Problem: " + tf.name());
        System.out.println("Dimension: " + dimension);
        System.out.println("-------------");
        System.out.println("Algorithm: " + shade.getName());
        System.out.println("NP: " + NP);
        System.out.println("NP final: " + NPfinal);
        System.out.println("Max. OFEs: " + MAXFES);
        System.out.println("H: " + H);
        System.out.println("Runs: " + runs);
        System.out.println("=================================");
        System.out.println("=================================");
        
        for (int k = 0; k < runs; k++) {
            
            generator = new util.random.UniformRandom();
            shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

            shade.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println("===============SOLUTION " + (k+1) + "===============");
            System.out.println("TIME\n" + new Date());
            System.out.println("OFV\n" + (shade.getBest().fitness - tf.optimum()));
            System.out.println("SOLUTION\n" + Arrays.toString(shade.getBest().vector));
            
            Map<String, List> map = ((Spalovny_projekt)tf).getOutput(shade.getBest().vector);
            
            System.out.println("=================================");
            String line;
          
            if(map != null){
                for(Map.Entry<String,List> entry : map.entrySet()){
                    line = "";
                    System.out.print(entry.getKey() + " = ");
                    line += "{";
    //                System.out.print("{");
                    for(int pup = 0; pup < entry.getValue().size(); pup++){
    //                    System.out.print(entry.getValue().get(pup));
                        line += entry.getValue().get(pup);
                        if(pup != entry.getValue().size()-1){
    //                       System.out.print(","); 
                           line += ",";
                        }
                    }
    //                System.out.println("}");
                    line += "};";
                    line = line.replace("[", "{");
                    line = line.replace("]", "}");
                    System.out.println(line);

                }
            }
            
            System.out.println("=================================");
            
//            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
//                i++;
//                if(ind.fitness < min){
//                    min = ind.fitness;
//                    best = i;
//                }
//            }
//            System.out.println("Best solution found in " + best + " CFE");
            
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
    }
    
    /**
     * 
     * Main to solve spalovny in Zlinksy kraj
     * 
     * @throws Exception 
     */
    public static void zlinMain() throws Exception {
        
        /**
         * Zlinsky kraj
         */
        int[] use_prod = new int[]{193,194,195,196,197,198,199,200,201,202,203,204,205};
        int[] use_inc = new int[]{16,17,20,28,29,33};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);;

        double[] bestArray = new double[runs];
        int i, best;
        double min;

        System.out.println("START\n" + new Date());
        System.out.println("SETTINGS:\n=================================");
        System.out.println("=================================");
        System.out.println("Problem: " + tf.name());
        System.out.println("Dimension: " + dimension);
        System.out.println("-------------");
        System.out.println("Algorithm: " + shade.getName());
        System.out.println("NP: " + NP);
        System.out.println("NP final: " + NPfinal);
        System.out.println("Max. OFEs: " + MAXFES);
        System.out.println("H: " + H);
        System.out.println("Runs: " + runs);
        System.out.println("=================================");
        System.out.println("=================================");
        
        for (int k = 0; k < runs; k++) {
            
            generator = new util.random.UniformRandom();
            shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

            shade.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println("===============SOLUTION " + (k+1) + "===============");
            System.out.println("TIME\n" + new Date());
            System.out.println("OFV\n" + (shade.getBest().fitness - tf.optimum()));
            System.out.println("SOLUTION\n" + Arrays.toString(shade.getBest().vector));
            
            Map<String, List> map = ((Spalovny_projekt)tf).getOutput(shade.getBest().vector);
            
            System.out.println("=================================");
            String line;
          
            if(map != null){
                for(Map.Entry<String,List> entry : map.entrySet()){
                    line = "";
                    System.out.print(entry.getKey() + " = ");
                    line += "{";
    //                System.out.print("{");
                    for(int pup = 0; pup < entry.getValue().size(); pup++){
    //                    System.out.print(entry.getValue().get(pup));
                        line += entry.getValue().get(pup);
                        if(pup != entry.getValue().size()-1){
    //                       System.out.print(","); 
                           line += ",";
                        }
                    }
    //                System.out.println("}");
                    line += "};";
                    line = line.replace("[", "{");
                    line = line.replace("]", "}");
                    System.out.println(line);

                }
            }
            
            System.out.println("=================================");
            
//            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
//                i++;
//                if(ind.fitness < min){
//                    min = ind.fitness;
//                    best = i;
//                }
//            }
//            System.out.println("Best solution found in " + best + " CFE");
            
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
    }
    
    /**
     * 
     * Main to solve spalovny in CR
     * 
     * @throws Exception 
     */
    public static void spalovnyProjektMain() throws Exception {

        int dimension = 206+40; //38
        int NP = 100;
        int NPfinal = 20;
        int MAXFES = 100000 * NP; //100000 * NP;
        TestFunction tf = new Spalovny_projekt();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;
        double min;

        System.out.println("START\n" + new Date());
        System.out.println("SETTINGS:\n=================================");
        System.out.println("=================================");
        System.out.println("Problem: " + tf.name());
        System.out.println("Dimension: " + dimension);
        System.out.println("-------------");
        System.out.println("Algorithm: " + shade.getName());
        System.out.println("NP: " + NP);
        System.out.println("NP final: " + NPfinal);
        System.out.println("Max. OFEs: " + MAXFES);
        System.out.println("H: " + H);
        System.out.println("Runs: " + runs);
        System.out.println("=================================");
        System.out.println("=================================");
        
        for (int k = 0; k < runs; k++) {
            
            generator = new util.random.UniformRandom();
            shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

            shade.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println("===============SOLUTION " + (k+1) + "===============");
            System.out.println("TIME\n" + new Date());
            System.out.println("OFV\n" + (shade.getBest().fitness - tf.optimum()));
            System.out.println("SOLUTION\n" + Arrays.toString(shade.getBest().vector));
            
            Map<String, List> map = ((Spalovny_projekt)tf).getOutput(shade.getBest().vector);
            
            System.out.println("=================================");
            String line;
          
            if(map != null){
                for(Map.Entry<String,List> entry : map.entrySet()){
                    line = "";
                    System.out.print(entry.getKey() + " = ");
                    line += "{";
    //                System.out.print("{");
                    for(int pup = 0; pup < entry.getValue().size(); pup++){
    //                    System.out.print(entry.getValue().get(pup));
                        line += entry.getValue().get(pup);
                        if(pup != entry.getValue().size()-1){
    //                       System.out.print(","); 
                           line += ",";
                        }
                    }
    //                System.out.println("}");
                    line += "};";
                    line = line.replace("[", "{");
                    line = line.replace("]", "}");
                    System.out.println(line);

                }
            }
            
            System.out.println("=================================");
            
//            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
//                i++;
//                if(ind.fitness < min){
//                    min = ind.fitness;
//                    best = i;
//                }
//            }
//            System.out.println("Best solution found in " + best + " CFE");
            
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        
        zlinMain();
        
        System.out.println("------------------");
        System.out.println("------------------");
        System.out.println("------------------");
        
        olomoucMain();
        
        System.out.println("------------------");
        System.out.println("------------------");
        System.out.println("------------------");
        
        jmMain();
        
    }
    
}