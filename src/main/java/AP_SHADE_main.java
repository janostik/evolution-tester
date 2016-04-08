
import algorithm.Algorithm;
import algorithm.de.ap.AP_ShaDE;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import model.tf.ap.regression.AP3sine;
import model.tf.ap.regression.AP4sine;
import model.tf.ap.regression.APquintic;
import model.tf.ap.regression.APsextic;
import model.tf.ap.APtf;
import model.tf.ap.regression.APbmwVariable;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author wiki
 */
public class AP_SHADE_main {

    public static void quinticMain(String[] args) throws Exception {

        int dimension = 90;
        int NP = 50;
        int H = 50;
        int generations = 4000;
        int MAXFES = generations * NP;

        APtf tf = new APquintic();
        /**
         * Random generator for parent selection, F and CR selection
         */
        util.random.Random generator = new util.random.UniformRandom();
        double min;
//        APconst ap = new APconst();

        Algorithm de;

        int runs = 30;
        double[] bestArray = new double[runs];
        int i, best;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new AP_ShaDE(dimension, MAXFES, tf, H, NP, generator);

            de.run();
 
            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
            /**
             * Final AP equation.
             */

            System.out.println("=================================");
            System.out.println("Equation: \n" + ((AP_ShaDE.AP_Individual) de.getBest()).equation);
            System.out.println("Vector: \n" + Arrays.toString(((AP_ShaDE.AP_Individual) de.getBest()).vector));
            System.out.println("=================================");
            
            for(AP_ShaDE.AP_Individual ind : ((AP_ShaDE)de).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
                if(ind.fitness == 0){
                    System.out.println("Solution found in " + i + " CFE");
                    break;
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
    
    public static void sexticMain(String[] args) throws Exception {

        int dimension = 90;
        int NP = 50;
        int H = 50;
        int generations = 4000;
        int MAXFES = generations * NP;

        APtf tf = new APsextic();
        /**
         * Random generator for parent selection, F and CR selection
         */
        util.random.Random generator = new util.random.UniformRandom();
        double min;
//        APconst ap = new APconst();

        Algorithm de;

        int runs = 30;
        double[] bestArray = new double[runs];
        int i, best;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new AP_ShaDE(dimension, MAXFES, tf, H, NP, generator);

            de.run();
 
            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
            /**
             * Final AP equation.
             */

            System.out.println("=================================");
            System.out.println("Equation: \n" + ((AP_ShaDE.AP_Individual) de.getBest()).equation);
            System.out.println("Vector: \n" + Arrays.toString(((AP_ShaDE.AP_Individual) de.getBest()).vector));
            System.out.println("=================================");
            
            for(AP_ShaDE.AP_Individual ind : ((AP_ShaDE)de).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
                if(ind.fitness == 0){
                    System.out.println("Solution found in " + i + " CFE");
                    break;
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
    
    public static void threeSineMain(String[] args) throws Exception {

        int dimension = 30;
        int NP = 100;
        int H = 100;
        int generations = 2000;
        int MAXFES = generations * NP;

        APtf tf = new AP3sine();
        /**
         * Random generator for parent selection, F and CR selection
         */
        util.random.Random generator = new util.random.UniformRandom();
        double min;
//        APconst ap = new APconst();

        Algorithm de;

        int runs = 2;
        double[] bestArray = new double[runs];
        int i, best;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new AP_ShaDE(dimension, MAXFES, tf, H, NP, generator);

            de.run();
 
            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
            /**
             * Final AP equation.
             */

            System.out.println("=================================");
            System.out.println("Equation: \n" + ((AP_ShaDE.AP_Individual) de.getBest()).equation);
            System.out.println("Vector: \n" + Arrays.toString(((AP_ShaDE.AP_Individual) de.getBest()).vector));
            System.out.println("=================================");
            
            for(AP_ShaDE.AP_Individual ind : ((AP_ShaDE)de).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
                if(ind.fitness == 0){
                    System.out.println("Solution found in " + i + " CFE");
                    break;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
//            System.out.println("Length in 000. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(0));
//            System.out.println("Length in 001. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(1));
//            System.out.println("Length in 010. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(10));
//            System.out.println("Length in 020. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(20));
//            System.out.println("Length in 050. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(50));
//            System.out.println("Length in 100. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(100));
//            System.out.println("Length in 200. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(200));
//            System.out.println("Length in 299. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(299));
            
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");

    }
    
    public static void fourSineMain(String[] args) throws Exception {

        int dimension = 45;
        int NP = 100;
        int H = 100;
        int generations = 2000;
        int MAXFES = generations * NP;

        APtf tf = new AP4sine();
        /**
         * Random generator for parent selection, F and CR selection
         */
        util.random.Random generator = new util.random.UniformRandom();
        double min;
//        APconst ap = new APconst();

        Algorithm de;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new AP_ShaDE(dimension, MAXFES, tf, H, NP, generator);

            de.run();
 
            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
            /**
             * Final AP equation.
             */

            System.out.println("=================================");
            System.out.println("Equation: \n" + ((AP_ShaDE.AP_Individual) de.getBest()).equation);
            System.out.println("Vector: \n" + Arrays.toString(((AP_ShaDE.AP_Individual) de.getBest()).vector));
            System.out.println("=================================");
            
            for(AP_ShaDE.AP_Individual ind : ((AP_ShaDE)de).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
                if(ind.fitness == 0){
                    System.out.println("Solution found in " + i + " CFE");
                    break;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
//            System.out.println("Length in 000. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(0));
//            System.out.println("Length in 001. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(1));
//            System.out.println("Length in 010. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(10));
//            System.out.println("Length in 020. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(20));
//            System.out.println("Length in 050. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(50));
//            System.out.println("Length in 100. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(100));
//            System.out.println("Length in 200. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(200));
//            System.out.println("Length in 299. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(299));
            
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
     * @param points
     * @throws Exception 
     */
    public static String mainBMWvariable(double[][] points) throws Exception {

        int dimension = 150;
        int NP = 200;
        int H = 10;
        int generations = 2000;
        int MAXFES = generations * NP;

        APtf tf = new APbmwVariable(points);
        /**
         * Random generator for parent selection, F and CR selection
         */
        util.random.Random generator = new util.random.UniformRandom();
        double min, bestMin = Double.MAX_VALUE;
//        APconst ap = new APconst();

        Algorithm de;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;
        String bestEq="", finalString="";

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new AP_ShaDE(dimension, MAXFES, tf, H, NP, generator);

            de.run();
 
            bestArray[k] = de.getBest().fitness;
            if(de.getBest().fitness < bestMin){
                bestEq = ((AP_ShaDE.AP_Individual) de.getBest()).equation;
                bestMin = de.getBest().fitness;
            }
//            System.out.println(de.getBest().fitness);
            
            /**
             * Final AP equation.
             */

//            System.out.println("=================================");
//            System.out.println("Equation: \n" + ((AP_ShaDE.AP_Individual) de.getBest()).equation);
//            System.out.println("Vector: \n" + Arrays.toString(((AP_ShaDE.AP_Individual) de.getBest()).vector));
//            System.out.println("=================================");
//            
//            for(AP_ShaDE.AP_Individual ind : ((AP_ShaDE)de).getBestHistory()){
//                i++;
//                if(ind.fitness < min){
//                    min = ind.fitness;
//                    best = i;
//                }
//                if(ind.fitness == 0){
//                    System.out.println("Solution found in " + i + " CFE");
//                    break;
//                }
//            }
//            System.out.println("Best solution found in " + best + " CFE");
            
//            System.out.println("Length in 000. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(0));
//            System.out.println("Length in 001. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(1));
//            System.out.println("Length in 010. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(10));
//            System.out.println("Length in 020. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(20));
//            System.out.println("Length in 050. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(50));
//            System.out.println("Length in 100. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(100));
//            System.out.println("Length in 200. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(200));
//            System.out.println("Length in 299. generation: " + ((AP_ShaDE)de).getAvgGenerationLength().get(299));
            
        }
        
        
        System.out.print(bestEq);
        
        
        finalString += ("=================================" + "\n");
        finalString += ("Min: " + DoubleStream.of(bestArray).min().getAsDouble() + "\n");
        finalString += ("Max: " + DoubleStream.of(bestArray).max().getAsDouble() + "\n");
        finalString += ("Mean: " + new Mean().evaluate(bestArray) + "\n");
        finalString += ("Median: " + new Median().evaluate(bestArray) + "\n");
        finalString += ("Std. Dev.: " + new StandardDeviation().evaluate(bestArray) + "\n");
        finalString += ("=================================" + "\n");

        return finalString;
        
    }
    
    public static void mainPredictionBMWAllParts() throws Exception{
        
        double[][] points;
        String results = "\n";
        
        
        //Generated by Wolfram Mathematica
        points = new double[][]{{0., 79.977}, {0.00309598, 80.379}, {0.00619195, 81.489}, {0.00928793, 83.268}, {0.0123839, 82.742}, {0.0154799, 83.026}, {0.0185759, 83.96}, {0.0216718, 82.844}, {0.0247678, 84.979}, {0.0278638, 87.419}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.0371517, 88.629}, {0.0402477, 91.829}, {0.0433437, 95.724}, {0.0464396, 99.137}, {0.0495356, 95.257}, {0.0526316, 96.096}, {0.0557276, 96.422}, {0.0588235, 96.751}, {0.0619195, 97.265}, {0.0650155, 99.836}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.0743034, 98.898}, {0.0773994, 95.907}, {0.0804954, 97.967}, {0.0835913, 96.984}, {0.0866873, 99.458}, {0.0897833, 100.29}, {0.0928793, 99.509}, {0.0959752, 99.5}, {0.0990712, 101.577}, {0.102167, 101.828}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.111455, 103.594}, {0.114551, 102.468}, {0.117647, 104.431}, {0.120743, 105.389}, {0.123839, 106.462}, {0.126935, 104.034}, {0.130031, 105.622}, {0.133127, 106.722}, {0.136223, 107.533}, {0.139319, 107.072}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.148607, 110.711}, {0.151703, 112.066}, {0.154799, 114.865}, {0.157895, 113.281}, {0.160991, 108.61}, {0.164087, 108.793}, {0.167183, 109.119}, {0.170279, 106.444}, {0.173375, 107.022}, {0.176471, 106.698}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.185759, 109.399}, {0.188854, 108.657}, {0.19195, 108.114}, {0.195046, 107.592}, {0.198142, 107.592}, {0.201238, 107.592}, {0.204334, 107.397}, {0.20743, 105.704}, {0.210526, 108.378}, {0.213622, 109.731}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.22291, 107.797}, {0.226006, 105.406}, {0.229102, 102.993}, {0.232198, 104.373}, {0.235294, 104.705}, {0.23839, 103.021}, {0.241486, 103.534}, {0.244582, 102.993}, {0.247678, 105.427}, {0.250774, 102.923}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.260062, 98.701}, {0.263158, 99.744}, {0.266254, 99.504}, {0.26935, 97.176}, {0.272446, 97.567}, {0.275542, 102.199}, {0.278638, 102.134}, {0.281734, 99.719}, {0.28483, 97.564}, {0.287926, 100.538}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.297214, 102.757}, {0.30031, 102.305}, {0.303406, 101.125}, {0.306502, 100.665}, {0.309598, 100.665}, {0.312693, 99.8}, {0.315789, 100.828}, {0.318885, 98.841}, {0.321981, 97.338}, {0.325077, 96.967}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.334365, 95.399}, {0.337461, 95.621}, {0.340557, 93.399}, {0.343653, 94.372}, {0.346749, 97.03}, {0.349845, 97.27}, {0.352941, 96.056}, {0.356037, 94.749}, {0.359133, 94.596}, {0.362229, 92.753}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.371517, 98.381}, {0.374613, 100.151}, {0.377709, 98.619}, {0.380805, 99.415}, {0.383901, 99.45}, {0.386997, 94.893}, {0.390093, 94.739}, {0.393189, 96.701}, {0.396285, 95.336}, {0.399381, 95.447}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.408669, 88.609}, {0.411765, 90.45}, {0.414861, 91.874}, {0.417957, 91.386}, {0.421053, 90.963}, {0.424149, 89.82}, {0.427245, 92.146}, {0.430341, 91.991}, {0.433437, 92.02}, {0.436533, 91.458}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.44582, 87.409}, {0.448916, 85.405}, {0.452012, 86.976}, {0.455108, 86.141}, {0.458204, 86.507}, {0.4613, 87.89}, {0.464396, 88.048}, {0.467492, 86.89}, {0.470588, 89.213}, {0.473684, 88.371}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.482972, 86.084}, {0.486068, 83.566}, {0.489164, 82.975}, {0.49226, 83.321}, {0.495356, 83.59}, {0.498452, 82.666}, {0.501548, 80.981}, {0.504644, 78.363}, {0.50774, 76.948}, {0.510836, 75.504}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.520124, 80.241}, {0.52322, 79.894}, {0.526316, 79.346}, {0.529412, 76.678}, {0.532508, 77.015}, {0.535604, 78.765}, {0.5387, 76.748}, {0.541796, 77.555}, {0.544892, 80.925}, {0.547988, 82.007}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.557276, 82.176}, {0.560372, 84.19}, {0.563467, 84.18}, {0.566563, 84.566}, {0.569659, 82.581}, {0.572755, 80.677}, {0.575851, 75.98}, {0.578947, 76.851}, {0.582043, 72.951}, {0.585139, 75.948}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.594427, 75.941}, {0.597523, 75.445}, {0.600619, 75.604}, {0.603715, 78.305}, {0.606811, 78.98}, {0.609907, 82.899}, {0.613003, 83.03}, {0.616099, 84.545}, {0.619195, 86.043}, {0.622291, 84.427}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.631579, 83.713}, {0.634675, 84.098}, {0.637771, 83.759}, {0.640867, 84.493}, {0.643963, 87.196}, {0.647059, 89.621}, {0.650155, 89.026}, {0.653251, 88.868}, {0.656347, 89.701}, {0.659443, 89.524}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.668731, 89.91}, {0.671827, 88.625}, {0.674923, 88.987}, {0.678019, 92.211}, {0.681115, 91.032}, {0.684211, 92.527}, {0.687307, 92.192}, {0.690402, 90.453}, {0.693498, 91.067}, {0.696594, 91.163}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.705882, 94.903}, {0.708978, 95.382}, {0.712074, 94.368}, {0.71517, 93.095}, {0.718266, 95.431}, {0.721362, 96.688}, {0.724458, 97.401}, {0.727554, 99.319}, {0.73065, 98.728}, {0.733746, 98.687}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.743034, 96.245}, {0.74613, 93.56}, {0.749226, 92.54}, {0.752322, 93.432}, {0.755418, 90.111}, {0.758514, 88.855}, {0.76161, 91.059}, {0.764706, 92.02}, {0.767802, 94.783}, {0.770898, 92.655}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.780186, 94.56}, {0.783282, 94.56}, {0.786378, 94.56}, {0.789474, 93.983}, {0.79257, 94.626}, {0.795666, 94.248}, {0.798762, 94.248}, {0.801858, 94.248}, {0.804954, 89.382}, {0.80805, 88.15}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.817337, 80.21}, {0.820433, 80.018}, {0.823529, 82.05}, {0.826625, 78.886}, {0.829721, 77.946}, {0.832817, 74.22}, {0.835913, 75.061}, {0.839009, 74.98}, {0.842105, 73.557}, {0.845201, 75.673}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.854489, 77.035}, {0.857585, 76.996}, {0.860681, 75.513}, {0.863777, 74.011}, {0.866873, 72.672}, {0.869969, 70.843}, {0.873065, 69.85}, {0.876161, 69.128}, {0.879257, 69.966}, {0.882353, 66.635}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.891641, 65.171}, {0.894737, 67.321}, {0.897833, 70.756}, {0.900929, 70.313}, {0.904025, 72.691}, {0.907121, 72.445}, {0.910217, 70.973}, {0.913313, 72.605}, {0.916409, 70.121}, {0.919505, 67.9}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.928793, 72.364}, {0.931889, 75.034}, {0.934985, 77.449}, {0.93808, 78.141}, {0.941176, 79.567}, {0.944272, 78.892}, {0.947368, 76.901}, {0.950464, 75.61}, {0.95356, 72.528}, {0.956656, 75.738}};
        results+=mainBMWvariable(points);
        System.out.println(",");
        points = new double[][]{{0.965944, 79.729}, {0.96904, 77.63}, {0.972136, 78.166}, {0.975232, 78.334}, {0.978328, 78.14}, {0.981424, 77.765}, {0.98452, 76.842}, {0.987616, 76.842}, {0.990712, 76.842}, {0.993808, 77.133}};
        results+=mainBMWvariable(points);

        System.out.println(results);
        
    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        
//        quinticMain(args);
//        sexticMain(args);
//        threeSineMain(args);
//        fourSineMain(args);
        mainPredictionBMWAllParts();

        
    }
    
}
