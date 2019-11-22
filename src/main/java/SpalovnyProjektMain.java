
import algorithm.de.DISH_analysis;
import algorithm.de.Lfv_SHADE;
import algorithm.discrete.aco.RBAS;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.DoubleStream;
import model.Individual;
import model.dicsrete.Solution;
import model.dicsrete.tf.DiscreteTestFunction;
import model.dicsrete.tf.Spalovny_projektDiscrete;
import model.tf.TestFunction;
import model.tf.nwf.Spalovny_combined;
import model.tf.nwf.Spalovny_combinedProb;
import model.tf.nwf.Spalovny_combinedSmall;
import model.tf.nwf.Spalovny_projekt;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author wiki on 31/01/2017
 */
public class SpalovnyProjektMain {
    
    /**
     * JM
     */
//    int[] use_prod = new int[]{18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38};
//    int[] use_inc = new int[]{1,6,34};
    
    /**
     * Zlin
     */
//    int[] use_prod = new int[]{193,194,195,196,197,198,199,200,201,202,203,204,205};
//    int[] use_inc = new int[]{16,17,20,28,29,33};
    
    /**
    * Olomoucky kraj
    */
//    int[] use_prod = new int[]{93,94,95,96,97,98,99,100,101,102,103,104,105};
//    int[] use_inc = new int[]{7,8,21};
    
    /**
    * Moravskoslezsky
    */
//    int[] use_prod = new int[]{71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92};
//    int[] use_inc = new int[]{27,32,35,36,37,38,39};
    
    /**
    * Pardubicky
    */
//    int[] use_prod = new int[]{106,107,108,109,110,111,112,113,114,115,116,117,118,119,120};
//    int[] use_inc = new int[]{10};
    
    /**
    * Vysocina
    */
//    int[] use_prod = new int[]{178,179,180,181,182,183,184,185,186,187,188,189,190,191,192};
//    int[] use_inc = new int[]{12,23,25};
    
    /**
    * Jihocesky
    */
//    int[] use_prod = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};
//    int[] use_inc = new int[]{9,19};
    
    /**
    * Plzensky
    */
//    int[] use_prod = new int[]{121,122,123,124,125,126,127,128,129,130,131,132,133,134,135};
//    int[] use_inc = new int[]{3,30};
    
    /**
    * Praha
    */
//    int[] use_prod = new int[]{0};
//    int[] use_inc = new int[]{0};
    
    /**
    * Stredocesky
    */
//    int[] use_prod = new int[]{136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161};
//    int[] use_inc = new int[]{4,5,31};
    
    /**
    * Karlovarsky
    */
//    int[] use_prod = new int[]{39,40,41,42,43,44,45};
//    int[] use_inc = new int[]{15,22,24};
    
    /**
    * Ustecky
    */
//    int[] use_prod = new int[]{162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177};
//    int[] use_inc = new int[]{11,13,26};
    
    /**
    * Liberecky
    */
//    int[] use_prod = new int[]{61,62,63,64,65,66,67,68,69,70};
//    int[] use_inc = new int[]{2};
    
    /**
    * Kralovehradecky
    */
//    int[] use_prod = new int[]{46,47,48,49,50,51,52,53,54,55,56,57,58,59,60};
//    int[] use_inc = new int[]{14,18};
    
    /**
     * 
     * Main to solve spalovny in (všechny ?eské, tj. jiho?esky, plzensky, praha, stredocesky, karlovarsky, ustecky, liberecky, kralovehradecky, pardubiocky, vysocina)
     * 
     * @throws Exception 
     */
    public static void desetKrajuMain() throws Exception {
        
        /**
         * 10 kraju
         */
        int[] use_prod = new int[]{178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,39,40,41,42,43,44,45,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,0};
        int[] use_inc = new int[]{12,23,25,10,14,18,2,11,13,26,15,22,24,4,5,31,9,19,3,30,0};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "10kraju\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
            
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
     * Main to solve spalovny in JM+Olm+Zlin+Moravskoslezsky+Pardubicky+Vysocina+Jihocesky+kralovehrad+stredocesky+Praha+Liberec
     * 
     * @throws Exception 
     */
    public static void jedenactKrajuMain() throws Exception {
        
        /**
         * JM kraj + Olomoucky kraj + Zlinsky
         */
        int[] use_prod = new int[]{0,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205,61,62,63,64,65,66,67,68,69,70};
        int[] use_inc = new int[]{0,4,5,31,14,18,9,19,12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33,2};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "11kraju\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
            
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
     * Main to solve spalovny in JM+Olm+Zlin+Moravskoslezsky+Pardubicky+Vysocina+Jihocesky+kralovehrad+stredocesky+Praha
     * 
     * @throws Exception 
     */
    public static void deset2KrajuMain() throws Exception {
        
        /**
         * JM kraj + Olomoucky kraj + Zlinsky
         */
        int[] use_prod = new int[]{0,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
        int[] use_inc = new int[]{0,4,5,31,14,18,9,19,12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "10kraju2\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
            
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
     * Main to solve spalovny in JM+Olm+Zlin+Moravskoslezsky+Pardubicky+Vysocina+Jihocesky+kralovehrad+stredocesky
     * 
     * @throws Exception 
     */
    public static void devetKrajuMain() throws Exception {
        
        /**
         * JM kraj + Olomoucky kraj + Zlinsky
         */
        int[] use_prod = new int[]{136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
        int[] use_inc = new int[]{4,5,31,14,18,9,19,12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "9kraju\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
            
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
     * Main to solve spalovny in JM+Olm+Zlin+Moravskoslezsky+Pardubicky+Vysocina+Jihocesky+kralovehrad
     * 
     * @throws Exception 
     */
    public static void osmKrajuMain() throws Exception {
        
        /**
         * JM kraj + Olomoucky kraj + Zlinsky
         */
        int[] use_prod = new int[]{46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
        int[] use_inc = new int[]{14,18,9,19,12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "8kraju\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
            
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
     * Main to solve spalovny in JM+Olm+Zlin+Moravskoslezsky+Pardubicky+Vysocina+Jihocesky
     * 
     * @throws Exception 
     */
    public static void sedmKrajuMain() throws Exception {
        
        /**
         * JM kraj + Olomoucky kraj + Zlinsky
         */
        int[] use_prod = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
        int[] use_inc = new int[]{9,19,12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "7kraju\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
            
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
     * Main to solve spalovny in JM+Olm+Zlin+Moravskoslezsky+Pardubicky+Vysocina
     * 
     * @throws Exception 
     */
    public static void sestKrajuMain() throws Exception {
        
        /**
         * JM kraj + Olomoucky kraj + Zlinsky
         */
        int[] use_prod = new int[]{178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
        int[] use_inc = new int[]{12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "6kraju\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
            
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
     * Main to solve spalovny in JM+Olm+Zlin+Moravskoslezsky+Pardubicky
     * 
     * @throws Exception 
     */
    public static void petKrajuMain() throws Exception {
        
        /**
         * JM kraj + Olomoucky kraj + Zlinsky
         */
        int[] use_prod = new int[]{106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
        int[] use_inc = new int[]{10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "5kraju\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
            
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
     * Main to solve spalovny in JM+Olm+Zlin+Moravskoslezsky
     * 
     * @throws Exception 
     */
    public static void ctyriKrajeMain() throws Exception {
        
        /**
         * JM kraj + Olomoucky kraj + Zlinsky
         */
        int[] use_prod = new int[]{71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
        int[] use_inc = new int[]{27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "4kraje\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
            
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
     * Main to solve spalovny in JM+Olm+Zlin
     * 
     * @throws Exception 
     */
    public static void jmZlinOlmMain() throws Exception {
        
        /**
         * JM kraj + Olomoucky kraj + Zlinsky
         */
        int[] use_prod = new int[]{93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
        int[] use_inc = new int[]{7,8,21,1,6,34,16,17,20,28,29,33};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "JmZlinOlm\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
            
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
     * Main to solve spalovny in JM+Olm
     * 
     * @throws Exception 
     */
    public static void jmOlmMain() throws Exception {
        
        /**
         * JM kraj + Olomoucky kraj
         */
        int[] use_prod = new int[]{93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38};
        int[] use_inc = new int[]{7,8,21,1,6,34};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "JmOlm\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
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
     * Main to solve spalovny in Zlin+Olm
     * 
     * @throws Exception 
     */
    public static void zlinOlmMain() throws Exception {
        
        /**
         * Zlinsky kraj + Olomoucky kraj
         */
        int[] use_prod = new int[]{93,94,95,96,97,98,99,100,101,102,103,104,105,193,194,195,196,197,198,199,200,201,202,203,204,205};
        int[] use_inc = new int[]{7,8,21,16,17,20,28,29,33};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "ZlinOlm\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
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
     * Main to solve spalovny in Zlin+JM
     * 
     * @throws Exception 
     */
    public static void zlinJmMain() throws Exception {
        
        /**
         * Zlinsky kraj + Jihomoravsky kraj
         */
        int[] use_prod = new int[]{18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
        int[] use_inc = new int[]{1,6,34,16,17,20,28,29,33};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "JmZlin\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
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
    public static void discreteOlmMain() throws Exception {

        /**
         * Olomoucky kraj
         */
        int[] use_prod = new int[]{93,94,95,96,97,98,99,100,101,102,103,104,105};
        int[] use_inc = new int[]{7,8,21};
        int dimension = use_prod.length + use_inc.length;
        int MAXFES = maxfes; //100000 * NP;
        DiscreteTestFunction tf = new Spalovny_projektDiscrete(use_inc, use_prod);

        int NP = 1000;
        double alpha = 0.2;
        double sigma = 0.68;
        double sigma2 = 0.1;
        double p = 0.2;
        double q0 = 0.5;
        RBAS alg;
        
        alg = new RBAS(tf, MAXFES, NP, alpha, sigma, sigma2, p, q0);
        
        Solution sol;

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
        System.out.println("Algorithm: " + alg.getName());
        System.out.println("NP: " + NP);
        System.out.println("Max. OFEs: " + MAXFES);
        System.out.println("Runs: " + runs);
        System.out.println("=================================");
        System.out.println("=================================");
        
        for (int k = 0; k < runs; k++) {

            alg = new RBAS(tf, MAXFES, NP, alpha, sigma, sigma2, p, q0);

            alg.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = alg.getBest().fitness - tf.optimum();
            System.out.println("===============SOLUTION " + (k+1) + "===============");
            System.out.println("TIME\n" + new Date());
            System.out.println("OFV\n" + (alg.getBest().fitness - tf.optimum()));
            System.out.println("SOLUTION\n" + Arrays.toString(alg.getBest().vector));
            
            Map<String, List> map = ((Spalovny_projektDiscrete)tf).getOutput(alg.getBest().vector);
            
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
            System.out.println("Best solution found in " + alg.getBestHistory().get(alg.getBestHistory().size()-1).FES + " CFE");
            
            /**
             * History write
             */
            writeDiscreteResult(home_dir + "Olm\\res" + k + ".txt", alg.getBestHistory(), alg.getBest());
            
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
     * Main to solve spalovny in Olomoucky kraj
     * 
     * @throws Exception 
     */
    public static void olmMain() throws Exception {
        
        /**
         * Olomoucky kraj
         */
        int[] use_prod = new int[]{93,94,95,96,97,98,99,100,101,102,103,104,105};
        int[] use_inc = new int[]{7,8,21};
        int dimension = use_prod.length + use_inc.length;

        int NP = 100;
        int NPfinal = 20;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_projekt(use_inc, use_prod);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "Olm\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
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

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "JM\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
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

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "Zlin\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
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
     * Main to solve spalovny in Olomoucky kraj
     * 
     * @throws Exception 
     */
    public static void celaCRMain() throws Exception {
        
        /**
         * CR
         */
//        int[] use_prod = new int[]{71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
//        int[] use_inc = new int[]{27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
        int dimension = 40;

        int NP = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
        int NPfinal = 4;
        int MAXFES = maxfes;
        TestFunction tf = new Spalovny_combined();
        int H = 5;
        util.random.Random generator = new util.random.UniformRandom();

        DISH_analysis shade = new DISH_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            tf = new Spalovny_combined();
            generator = new util.random.UniformRandom();
            shade = new DISH_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);

            shade.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println("===============SOLUTION " + (k+1) + "===============");
            System.out.println("TIME\n" + new Date());
            System.out.println("OFV\n" + (shade.getBest().fitness - tf.optimum()));
            System.out.println("SOLUTION\n" + Arrays.toString(shade.getBest().vector));
            
            Map<String, List> map = ((Spalovny_combined)tf).getBest_map();
            
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
            
//            for(Individual ind : ((DISH_analysis)shade).getBestHistory()){
//                i++;
//                if(ind.fitness < min){
//                    min = ind.fitness;
//                    best = i;
//                }
//            }
            System.out.println("Best solution found in " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " CFE");
            
            /**
             * History write
             */
            writeCombinedResult(home_dir + "CR\\res" + k + ".txt", shade.getImp_hist(), shade.getBest());
            
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
        int MAXFES = maxfes; //100000 * NP;
        TestFunction tf = new Spalovny_projekt();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        Lfv_SHADE shade = new Lfv_SHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

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
            
            for(Individual ind : ((Lfv_SHADE)shade).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            /**
             * History write
             */
            writeResult(home_dir + "CR\\res" + k + ".txt", shade.getBestHistory(), shade.getBest());
            
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
    public static void discreteCR_RBAS_Main() throws Exception {

        int dimension = 206+40; //38
        int MAXFES = maxfes; //100000 * NP;
        DiscreteTestFunction tf = new Spalovny_projektDiscrete();

        int NP = 1000;
        double alpha = 0.2;
        double sigma = 0.68;
        double sigma2 = 0.1;
        double p = 0.2;
        double q0 = 0.5;
        RBAS alg;
        
        alg = new RBAS(tf, MAXFES, NP, alpha, sigma, sigma2, p, q0);
        
        Solution sol;

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
        System.out.println("Algorithm: " + alg.getName());
        System.out.println("NP: " + NP);
        System.out.println("Max. OFEs: " + MAXFES);
        System.out.println("Runs: " + runs);
        System.out.println("=================================");
        System.out.println("=================================");
        
        for (int k = 0; k < runs; k++) {

            alg = new RBAS(tf, MAXFES, NP, alpha, sigma, sigma2, p, q0);

            alg.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = alg.getBest().fitness - tf.optimum();
            System.out.println("===============SOLUTION " + (k+1) + "===============");
            System.out.println("TIME\n" + new Date());
            System.out.println("OFV\n" + (alg.getBest().fitness - tf.optimum()));
            System.out.println("SOLUTION\n" + Arrays.toString(alg.getBest().vector));
            
            Map<String, List> map = ((Spalovny_projektDiscrete)tf).getOutput(alg.getBest().vector);
            
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
            System.out.println("Best solution found in " + alg.getBestHistory().get(alg.getBestHistory().size()-1).FES + " CFE");
            
            /**
             * History write
             */
            writeDiscreteResult(home_dir + "CR\\res" + k + ".txt", alg.getBestHistory(), alg.getBest());
            
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
     * @param path
     * @param history
     * @param best
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException 
     */
    public static void writeCombinedResult(String path, List<double[]> history, Individual best) throws FileNotFoundException, UnsupportedEncodingException {
        
        PrintWriter writer;
        
        writer = new PrintWriter(path, "UTF-8");
        
        writer.print("{");

        writer.print("{" + String.format(Locale.US, "%.10f", best.fitness) + ", {");
        
        for(int i = 0; i < best.vector.length; i++) {
            
            writer.print(String.format(Locale.US, "%.10f", best.vector[i]));
            
            if(i != best.vector.length - 1) {
                writer.print(", ");
            }
            
        }
        
        writer.print("}, ");
        writer.print("{");

        for (int i = 0; i < history.size(); i++) {

            writer.print("{" + String.format(Locale.US, "%d", (int)history.get(i)[0]) + "," + String.format(Locale.US, "%.10f", history.get(i)[1]) + "}");

            if (i != history.size() - 1) {
                writer.print(",");
            }

        }

        writer.print("}");
        writer.print("}");

        writer.close();
        
    }
    
    /**
     * 
     * @param path
     * @param history
     * @param best
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException 
     */
    public static void writeResult(String path, List<Individual> history, Individual best) throws FileNotFoundException, UnsupportedEncodingException {
        
        PrintWriter writer;
        
        writer = new PrintWriter(path, "UTF-8");
        
        writer.print("{");

        writer.print("{" + String.format(Locale.US, "%.10f", best.fitness) + ", {");
        
        for(int i = 0; i < best.vector.length; i++) {
            
            writer.print(String.format(Locale.US, "%.10f", best.vector[i]));
            
            if(i != best.vector.length - 1) {
                writer.print(", ");
            }
            
        }
        
        writer.print("}, ");
        writer.print("{");

        for (int i = 0; i < history.size(); i++) {

            writer.print(String.format(Locale.US, "%.10f", history.get(i).fitness));

            if (i != history.size() - 1) {
                writer.print(",");
            }

        }

        writer.print("}");
        writer.print("}");

        writer.close();
        
    }
    
    /**
     * 
     * @param path
     * @param history
     * @param best
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException 
     */
    public static void writeDiscreteResult(String path, List<Solution> history, Solution best) throws FileNotFoundException, UnsupportedEncodingException {
        
        PrintWriter writer;
        
        writer = new PrintWriter(path, "UTF-8");
        
        writer.print("{");

        writer.print("{" + best.FES +  "," + String.format(Locale.US, "%.10f", best.fitness) + ", {");
        
        for(int i = 0; i < best.vector.length; i++) {
            
            writer.print(best.vector[i]);
            
            if(i != best.vector.length - 1) {
                writer.print(", ");
            }
            
        }
        
        writer.print("}, ");
        writer.print("{");

        for (int i = 0; i < history.size(); i++) {

            writer.print("{" + history.get(i).FES + "," + String.format(Locale.US, "%.10f", history.get(i).fitness) + "}");

            if (i != history.size() - 1) {
                writer.print(",");
            }

        }

        writer.print("}");
        writer.print("}");

        writer.close();
        
    }

    public static class Kraje {
        
        public int[] use_prod;
        public int[] use_inc;
        public String path;
        public String setting;
        
        public Kraje(String setting) {
            
            this.setting = setting;
            
            switch(this.setting) {
                case "1":
                    use_prod = new int[]{93,94,95,96,97,98,99,100,101,102,103,104,105};
                    use_inc = new int[]{7,8,21};
                    path = "1kraj";
                    break;
                case "1A":
                    use_prod = new int[]{193,194,195,196,197,198,199,200,201,202,203,204,205};
                    use_inc = new int[]{16,17,20,28,29,33};
                    path = "1Akraj";
                    break;
                case "2":
                    use_prod = new int[]{18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
                    use_inc = new int[]{1,6,34,16,17,20,28,29,33};
                    path = "2kraje";
                    break;
                case "3":
                    use_prod = new int[]{93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
                    use_inc = new int[]{7,8,21,1,6,34,16,17,20,28,29,33};
                    path = "3kraje";                    
                    break;
                case "4":
                    use_prod = new int[]{71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
                    use_inc = new int[]{27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
                    path = "4kraje";                    
                    break;
                case "5":
                    use_prod = new int[]{106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
                    use_inc = new int[]{10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
                    path = "5kraju";                    
                    break;
                case "6":
                    use_prod = new int[]{178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
                    use_inc = new int[]{12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
                    path = "6kraju";                    
                    break;
                case "7":
                    use_prod = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
                    use_inc = new int[]{9,19,12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
                    path = "7kraju";                    
                    break;
                case "8":
                    use_prod = new int[]{46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
                    use_inc = new int[]{14,18,9,19,12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
                    path = "8kraju";                    
                    break;
                case "9":
                    use_prod = new int[]{136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
                    use_inc = new int[]{4,5,31,14,18,9,19,12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
                    path = "9kraju";                    
                    break;
                case "10":
                    use_prod = new int[]{0,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205};
                    use_inc = new int[]{0,4,5,31,14,18,9,19,12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33};
                    path = "10kraju";                    
                    break;
                case "10A":
                    use_prod = new int[]{178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,39,40,41,42,43,44,45,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,0};
                    use_inc = new int[]{12,23,25,10,14,18,2,11,13,26,15,22,24,4,5,31,9,19,3,30,0};
                    path = "10Akraju";                    
                    break;
                case "11":
                    use_prod = new int[]{0,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205,61,62,63,64,65,66,67,68,69,70};
                    use_inc = new int[]{0,4,5,31,14,18,9,19,12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33,2};
                    path = "11kraju";                    
                    break;
                case "12":
                    use_prod = new int[]{0,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205,61,62,63,64,65,66,67,68,69,70,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135};
                    use_inc = new int[]{0,4,5,31,14,18,9,19,12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33,2,3,30};
                    path = "12kraju";                    
                    break;
                case "13":
                    use_prod = new int[]{0,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,193,194,195,196,197,198,199,200,201,202,203,204,205,61,62,63,64,65,66,67,68,69,70,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177};
                    use_inc = new int[]{0,4,5,31,14,18,9,19,12,23,25,10,27,32,35,36,37,38,39,7,8,21,1,6,34,16,17,20,28,29,33,2,3,30,11,13,26};
                    path = "13kraju";                    
                    break;
                default:
                    use_prod = null;
                    use_inc = null;
                    path = "CR";                    
                    break;
            }
            
        }
        
    }

    public static String getTimeFromMilis(long m) {

        long milis = m;
        
        long hours = (((milis / 1000) / 60) / 60);
        milis = milis - hours*60*60*1000;
        long minutes = ((milis / 1000) / 60);
        milis = milis - minutes*60*1000;
        long seconds = milis/1000;
        
        return String.format("%2d:%2d:%2d",hours,minutes,seconds);
        
    }
    
    /**
     * 
     * 
     * COMBINED approach
     * 
     * 
     */
    
    public static void combinedMain(Kraje kraje) throws Exception {

        boolean wholeCR;
        
        if(kraje.use_prod == null && kraje.use_inc == null) {
            wholeCR = true;
        }
        else {
            wholeCR = false;
        }
        
        int[] use_prod = null;
        int[] use_inc = null;
        int dimension;
        TestFunction tf;
        
        if(!wholeCR) {
            use_prod = kraje.use_prod.clone();
            use_inc = kraje.use_inc.clone();
            dimension = use_inc.length;
            tf = new Spalovny_combined(use_inc, use_prod);
        } else {
            dimension = 40;
            tf = new Spalovny_combined();
        }
        

        int NP = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
        int NPfinal = 4;
        int MAXFES = maxfes;
        
        int H = 5;
        util.random.Random generator = new util.random.UniformRandom();
        DISH_analysis shade = new DISH_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);

        double[] bestArray = new double[runs];
        int i, best;
        double min;

        System.out.println("START\n" + new Date());
        System.out.println("SETTINGS:\n=================================");
        System.out.println("=================================");
        System.out.println("Problem: " + tf.name());
        System.out.println("Instance: " + kraje.setting);
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
        
        
        long start,end;
        
        start = System.currentTimeMillis();
        
        for (int k = 0; k < runs; k++) {
            
            if(!wholeCR)
                tf = new Spalovny_combined(use_inc, use_prod);
            else
                tf = new Spalovny_combined();
            generator = new util.random.UniformRandom();
            shade = new DISH_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);

            shade.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println("===============SOLUTION " + (k+1) + "===============");
            System.out.println("TIME\n" + new Date());
            System.out.println("OFV\n" + (shade.getBest().fitness - tf.optimum()));
            System.out.println("SOLUTION\n" + Arrays.toString(shade.getBest().vector));
            
            Map<String, List> map = ((Spalovny_combined)tf).getBest_map();
            
            System.out.println("=================================");
            String line;
          
            if(map != null){
                for(Map.Entry<String,List> entry : map.entrySet()){
                    line = "";
                    System.out.print(entry.getKey() + " = ");
                    line += "{";
                    for(int pup = 0; pup < entry.getValue().size(); pup++){
                        line += entry.getValue().get(pup);
                        if(pup != entry.getValue().size()-1){
                           line += ",";
                        }
                    }
                    line += "};";
                    line = line.replace("[", "{");
                    line = line.replace("]", "}");
                    System.out.println(line);

                }
            }
            
            System.out.println("=================================");

            System.out.println("Best solution found in " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " CFE");
            
            /**
             * History write
             */
            writeCombinedResult(home_dir + kraje.path + "\\res" + k + ".txt", shade.getImp_hist(), shade.getBest());
            
        }
        
        end = System.currentTimeMillis();

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("Time: " + getTimeFromMilis(end-start));
        System.out.println("Instance: " + kraje.setting);
        System.out.println("=================================");
        
    } 
    
    /**
     * 
     * 
     * COMBINED Small incinerators first
     * 
     * 
     */
    
    public static void combinedSmallMain(Kraje kraje) throws Exception {

        boolean wholeCR;
        
        if(kraje.use_prod == null && kraje.use_inc == null) {
            wholeCR = true;
        }
        else {
            wholeCR = false;
        }
        
        int[] use_prod = null;
        int[] use_inc = null;
        int dimension;
        TestFunction tf;
        
        if(!wholeCR) {
            use_prod = kraje.use_prod.clone();
            use_inc = kraje.use_inc.clone();
            dimension = use_inc.length;
            tf = new Spalovny_combinedSmall(use_inc, use_prod);
        } else {
            dimension = 40;
            tf = new Spalovny_combinedSmall();
        }
        

        int NP = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
        int NPfinal = 4;
        int MAXFES = maxfes;
        
        int H = 5;
        util.random.Random generator = new util.random.UniformRandom();
        DISH_analysis shade = new DISH_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);

        double[] bestArray = new double[runs];
        int i, best;
        double min;

        System.out.println("START\n" + new Date());
        System.out.println("SETTINGS:\n=================================");
        System.out.println("=================================");
        System.out.println("Problem: " + tf.name());
        System.out.println("Instance: " + kraje.setting);
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
        
        
        long start,end;
        
        start = System.currentTimeMillis();
        
        for (int k = 0; k < runs; k++) {
            
            if(!wholeCR)
                tf = new Spalovny_combinedSmall(use_inc, use_prod);
            else
                tf = new Spalovny_combinedSmall();
            generator = new util.random.UniformRandom();
            shade = new DISH_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);

            shade.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println("===============SOLUTION " + (k+1) + "===============");
            System.out.println("TIME\n" + new Date());
            System.out.println("OFV\n" + (shade.getBest().fitness - tf.optimum()));
            System.out.println("SOLUTION\n" + Arrays.toString(shade.getBest().vector));
            
            Map<String, List> map = ((Spalovny_combinedSmall)tf).getBest_map();
            
            System.out.println("=================================");
            String line;
          
            if(map != null){
                for(Map.Entry<String,List> entry : map.entrySet()){
                    line = "";
                    System.out.print(entry.getKey() + " = ");
                    line += "{";
                    for(int pup = 0; pup < entry.getValue().size(); pup++){
                        line += entry.getValue().get(pup);
                        if(pup != entry.getValue().size()-1){
                           line += ",";
                        }
                    }
                    line += "};";
                    line = line.replace("[", "{");
                    line = line.replace("]", "}");
                    System.out.println(line);

                }
            }
            
            System.out.println("=================================");

            System.out.println("Best solution found in " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " CFE");
            
            /**
             * History write
             */
            writeCombinedResult(home_dir + kraje.path + "\\res" + k + ".txt", shade.getImp_hist(), shade.getBest());
            
        }
        
        end = System.currentTimeMillis();

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("Time: " + getTimeFromMilis(end-start));
        System.out.println("Instance: " + kraje.setting);
        System.out.println("=================================");
        
    } 
    
    /**
     * 
     * 
     * COMBINED Probability
     * 
     * 
     */
    
    public static void combinedProbMain(Kraje kraje) throws Exception {

        boolean wholeCR;
        
        if(kraje.use_prod == null && kraje.use_inc == null) {
            wholeCR = true;
        }
        else {
            wholeCR = false;
        }
        
        int[] use_prod = null;
        int[] use_inc = null;
        int dimension;
        TestFunction tf;
        
        if(!wholeCR) {
            use_prod = kraje.use_prod.clone();
            use_inc = kraje.use_inc.clone();
            dimension = use_inc.length;
            tf = new Spalovny_combinedProb(use_inc, use_prod);
        } else {
            dimension = 40;
            tf = new Spalovny_combinedProb();
        }
        

        int NP = (int) (25*Math.log(dimension)*Math.sqrt(dimension));
        int NPfinal = 4;
        int MAXFES = maxfes;
        
        int H = 5;
        util.random.Random generator = new util.random.UniformRandom();
        DISH_analysis shade = new DISH_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);

        double[] bestArray = new double[runs];
        int i, best;
        double min;

        System.out.println("START\n" + new Date());
        System.out.println("SETTINGS:\n=================================");
        System.out.println("=================================");
        System.out.println("Problem: " + tf.name());
        System.out.println("Instance: " + kraje.setting);
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
        
        long start,end;
        
        start = System.currentTimeMillis();
        
        for (int k = 0; k < runs; k++) {
            
            if(!wholeCR)
                tf = new Spalovny_combinedProb(use_inc, use_prod);
            else
                tf = new Spalovny_combinedProb();
            generator = new util.random.UniformRandom();
            shade = new DISH_analysis(dimension, MAXFES, tf, H, NP, generator, NPfinal);

            shade.run();
            
            best = 0;
            i = 0;
            min = Double.MAX_VALUE;

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println("===============SOLUTION " + (k+1) + "===============");
            System.out.println("TIME\n" + new Date());
            System.out.println("OFV\n" + (shade.getBest().fitness - tf.optimum()));
            System.out.println("SOLUTION\n" + Arrays.toString(shade.getBest().vector));
            
            Map<String, List> map = ((Spalovny_combinedProb)tf).getBest_map();
            
            System.out.println("=================================");
            String line;
          
            if(map != null){
                for(Map.Entry<String,List> entry : map.entrySet()){
                    line = "";
                    System.out.print(entry.getKey() + " = ");
                    line += "{";
                    for(int pup = 0; pup < entry.getValue().size(); pup++){
                        line += entry.getValue().get(pup);
                        if(pup != entry.getValue().size()-1){
                           line += ",";
                        }
                    }
                    line += "};";
                    line = line.replace("[", "{");
                    line = line.replace("]", "}");
                    System.out.println(line);

                }
            }
            
            System.out.println("=================================");

            System.out.println("Best solution found in " + shade.getImp_hist().get(shade.getImp_hist().size()-1)[0] + " CFE");
            
            /**
             * History write
             */
            writeCombinedResult(home_dir + kraje.path + "\\res" + k + ".txt", shade.getImp_hist(), shade.getBest());
            
        }
        
        end = System.currentTimeMillis();

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("Time: " + getTimeFromMilis(end-start));
        System.out.println("Instance: " + kraje.setting);
        System.out.println("=================================");
        
    } 
    
    public static int maxfes = 100_000;
    public static int runs = 10;
//    public static String home_dir = "E:\\results\\Spalovny\\S PENALIZACI\\";
    public static String home_dir = "D:\\results\\Spalovny\\";
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        home_dir = "D:\\results\\Spalovny\\COMBINED_APPROACH\\";
        
        /**
         * Combined
         */
        combinedMain(new Kraje("1"));
        combinedMain(new Kraje("1A"));
        combinedMain(new Kraje("2"));
        combinedMain(new Kraje("3"));
        combinedMain(new Kraje("4"));
        combinedMain(new Kraje("5"));
        combinedMain(new Kraje("6"));
        combinedMain(new Kraje("7"));
        combinedMain(new Kraje("8"));
        combinedMain(new Kraje("9"));
        combinedMain(new Kraje("10"));
        combinedMain(new Kraje("10A"));
        combinedMain(new Kraje("11"));
        combinedMain(new Kraje("12"));
        combinedMain(new Kraje("13"));
        combinedMain(new Kraje("CR"));
        
        /**
         * Probability
         */
//        combinedProbMain(new Kraje("1"));
//        combinedProbMain(new Kraje("1A"));
//        combinedProbMain(new Kraje("2"));
//        combinedProbMain(new Kraje("3"));
//        combinedProbMain(new Kraje("4"));
//        combinedProbMain(new Kraje("5"));
//        combinedProbMain(new Kraje("6"));
//        combinedProbMain(new Kraje("7"));
//        combinedProbMain(new Kraje("8"));
//        combinedProbMain(new Kraje("9"));
//        combinedProbMain(new Kraje("10"));
//        combinedProbMain(new Kraje("10A"));
//        combinedProbMain(new Kraje("11"));
//        combinedProbMain(new Kraje("12"));
//        combinedProbMain(new Kraje("13"));
//        combinedProbMain(new Kraje("CR"));

        /**
         * Small
         */
//        combinedSmallMain(new Kraje("1"));
/*        combinedSmallMain(new Kraje("1A"));
        combinedSmallMain(new Kraje("2"));
        combinedSmallMain(new Kraje("3"));
        combinedSmallMain(new Kraje("4"));
        combinedSmallMain(new Kraje("5"));
        combinedSmallMain(new Kraje("6"));
        combinedSmallMain(new Kraje("7"));
        combinedSmallMain(new Kraje("8"));
        combinedSmallMain(new Kraje("9"));
        combinedSmallMain(new Kraje("10"));
        combinedSmallMain(new Kraje("10A"));
        combinedSmallMain(new Kraje("11"));
        combinedSmallMain(new Kraje("12"));
        combinedSmallMain(new Kraje("13"));
        combinedSmallMain(new Kraje("CR"));
*/



        
    }
    
}
