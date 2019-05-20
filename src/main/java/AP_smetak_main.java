
import algorithm.Algorithm;
import algorithm.de.ap.AP_DISH;
import algorithm.de.ap.AP_ann_DISH;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import model.tf.ap.APtf;
import model.tf.ap.ann.APsmetak27A;
import model.tf.ap.ann.APsmetak27B;
import model.tf.ap.ann.APtfann;
import model.tf.ap.regression.APdatasetTF;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * Smetak prediction
 * 
 * @author wikki on 02/05/2019
 */
public class AP_smetak_main {

    private static final double[][] dataset0 = {{203,23120.7},{204,23184.1},{205,23273.},{206,23307.4},{207,23354.9},{208,23376.},{209,23391.},{210,23378.1},{211,23399.2},{212,23422.3},{213,23443.3},{214,23483.2},{215,23519.2},{216,23544.9},{217,23534.},{218,23510.6},{219,23488.9},{220,23459.3},{221,23400.9},{222,23400.2},{223,23387.4},{224,23385.5},{225,23421.8},{226,23472.8},{227,23492.7},{228,23537.2},{229,23618.5},{230,23688.5},{231,23837.7},{232,23972.4},{233,24114.3},{234,24183.1},{235,24223.1},{236,24210.9},{237,24230.4},{238,24249.6},{239,24314.5},{240,24403.4},{241,24462.8},{242,24527.3},{243,24608.6},{244,24658.6},{245,24686.8},{246,24741.5},{247,24762.},{248,24752.8},{249,24756.7},{250,24778.9},{251,24766.3},{252,24780.3},{253,24815.5},{254,24875.7},{255,24967.4},{256,25080.1},{257,25192.5},{258,25281.8},{259,25381.7},{260,25483.2},{261,25585.1},{262,25731.1},{263,25860.8},{264,25960.2},{265,26042.5},{266,26126.1},{267,26153.4},{268,26228.4},{269,26337.4},{270,26382.4},{271,26355.6},{272,26335.1},{273,26293.8},{274,26074.7},{275,25655.9},{276,25423.1},{277,25171.9},{278,24706.7},{279,24440.6},{280,24491.8},{281,24437.3},{282,24437.3},{283,24705.3},{284,24911.},{285,24983.7},{286,25015.2},{287,25029.},{288,25050.9},{289,25148.9},{290,25237.9},{291,25284.2},{292,25213.5},{293,25059.1},{294,24892.2},{295,24787.},{296,24741.5},{297,24798.7},{298,24958.2},{299,25019.},{300,25043.6},{301,25034.9},{302,25030.6}};
    private static final double[][] dataset1 = {{202,149.205},{203,152.14},{204,154.635},{205,156.466},{206,155.284},{207,153.852},{208,152.207},{209,151.189},{210,150.832},{211,150.936},{212,150.887},{213,150.476},{214,149.785},{215,149.254},{216,148.772},{217,148.468},{218,148.286},{219,148.098},{220,147.908},{221,147.32},{222,147.086},{223,147.048},{224,147.466},{225,148.072},{226,148.997},{227,149.536},{228,150.132},{229,150.52},{230,150.837},{231,151.273},{232,151.851},{233,152.738},{234,153.309},{235,153.417},{236,153.338},{237,153.348},{238,153.14},{239,153.416},{240,153.378},{241,153.463},{242,153.006},{243,152.594},{244,151.898},{245,151.708},{246,151.213},{247,151.213},{248,151.114},{249,151.094},{250,151.31},{251,151.69},{252,152.037},{253,153.158},{254,154.855},{255,156.529},{256,158.519},{257,160.417},{258,161.543},{259,162.039},{260,162.167},{261,162.243},{262,163.197},{263,164.176},{264,163.813},{265,163.706},{266,164.182},{267,163.532},{268,162.809},{269,163.793},{270,164.625},{271,164.104},{272,163.774},{273,163.166},{274,161.52},{275,158.694},{276,157.054},{277,155.103},{278,152.458},{279,150.864},{280,150.935},{281,150.32},{282,150.802},{283,152.486},{284,153.82},{285,154.572},{286,155.214},{287,154.898},{288,154.8},{289,155.28},{290,155.558},{291,155.932},{292,156.058},{293,155.852},{294,155.526},{295,155.36},{296,155.858},{297,156.338},{298,157.302},{299,157.964},{300,158.684},{301,158.644}};
    private static final double[][] dataset2 = {{3539,24897.4},{3540,24933.7},{3541,24961.8},{3542,24991.5},{3543,25017.2},{3544,25016.1},{3545,25006.1},{3546,24998.6},{3547,24994.8},{3548,24985.3},{3549,24975.5},{3550,24944.5},{3551,24915.9},{3552,24890.2},{3553,24866.1},{3554,24854.5},{3555,24864.1},{3556,24881.5},{3557,24891.4},{3558,24901.7},{3559,24906.3},{3560,24914.6},{3561,24916.3},{3562,24922.6},{3563,24931.1},{3564,24936.2},{3565,24939.9},{3566,24942.5},{3567,24944.4},{3568,24942.5},{3569,24936.4},{3570,24930},{3571,24922.3},{3572,24909.7},{3573,24897},{3574,24889.1},{3575,24880.7},{3576,24872.7},{3577,24873.6},{3578,24878.1},{3579,24877.5},{3580,24879},{3581,24883.6},{3582,24880.4},{3583,24874.6},{3584,24874.3},{3585,24873.4},{3586,24869.2},{3587,24871.2},{3588,24878.5},{3589,24884.5},{3590,24889.2},{3591,24896.7},{3592,24901.9},{3593,24904.5},{3594,24903.2},{3595,24900.1},{3596,24893.3},{3597,24890.6},{3598,24883.4},{3599,24878.1},{3600,24875.7},{3601,24876.7},{3602,24876.9},{3603,24886.8},{3604,24896.2},{3605,24905.9},{3606,24911.4},{3607,24914.9},{3608,24909},{3609,24905.5},{3610,24901.6},{3611,24902},{3612,24899.7},{3613,24900.8},{3614,24901.6},{3615,24897.4},{3616,24893.1},{3617,24896.1},{3618,24905.8},{3619,24912},{3620,24921.4},{3621,24922},{3622,24937.3},{3623,24941.3},{3624,24945.8},{3625,24957.5},{3626,24972.9},{3627,24974.2},{3628,24972.5},{3629,24984.9},{3630,24990.4},{3631,24991.3},{3632,24991.9},{3633,25002.6},{3634,25005.4},{3635,25008},{3636,25018.7},{3637,25016.8},{3638,25011.7}};
    private static final double[][] dataset3 = {{207,157.06},{208,156.958},{209,156.44},{210,155.792},{211,155.37},{212,154.854},{213,154.35},{214,154.226},{215,154.14},{216,153.89},{217,153.654},{218,153.49},{219,153.406},{220,153.56},{221,153.614},{222,153.714},{223,153.88},{224,154.112},{225,154.186},{226,154.27},{227,154.32},{228,154.168},{229,153.99},{230,153.716},{231,153.47},{232,153.262},{233,153.058},{234,152.704},{235,152.328},{236,152.084},{237,151.914},{238,151.832},{239,151.816},{240,151.844},{241,151.75},{242,151.634},{243,151.46},{244,151.246},{245,151.12},{246,151.048},{247,150.916},{248,150.886},{249,150.97},{250,151.128},{251,151.224},{252,151.522},{253,151.686},{254,151.708},{255,151.55},{256,151.25},{257,150.79},{258,150.424},{259,150.058},{260,149.598},{261,149.88},{262,150.194},{263,150.504},{264,150.804},{265,151.194},{266,151.054},{267,150.828},{268,150.656},{269,150.602},{270,150.666},{271,150.758},{272,150.952},{273,151.112},{274,151.298},{275,151.518},{276,151.616},{277,151.67},{278,151.766},{279,151.904},{280,151.96},{281,152.076},{282,152.196},{283,152.368},{284,152.49},{285,152.66},{286,152.84},{287,153},{288,153.088},{289,153.188},{290,153.246},{291,153.36},{292,153.394},{293,153.462},{294,153.572},{295,153.692},{296,153.816},{297,154.068},{298,154.25},{299,154.276},{300,154.35},{301,154.398},{302,154.354},{303,154.276},{304,154.254},{305,154.158},{306,153.958}};

    public static void datasetMain(double[][] dataset, String path) throws Exception {

        int dimension = 100;
        int NP = 1151;
        int NPfinal = 4;
        int H = 5;
        int MAXFES = 1_000_000;

        PrintWriter writer;
        writer = new PrintWriter(path + "equation.txt", "UTF-8");
        
        String bestEq = "";
        double bestEq_val = Double.MAX_VALUE;
        
        APtf tf = new APdatasetTF(dataset);
        /**
         * Random generator for parent selection, F and CR selection
         */
        util.random.Random generator = new util.random.UniformRandom();
        double min;
//        APconst ap = new APconst();

        Algorithm dish;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            dish = new AP_DISH(dimension, MAXFES, tf, H, NP, generator, NPfinal);

            dish.run();
 
            bestArray[k] = dish.getBest().fitness - tf.optimum();
            System.out.println(dish.getBest().fitness - tf.optimum());
            
            if(bestArray[k] <= bestEq_val) {
                bestEq_val = bestArray[k];
                bestEq = ((AP_DISH.AP_Individual) dish.getBest()).equation;
            }
            
            /**
             * Final AP equation.
             */

            System.out.println("=================================");
            System.out.println("Equation: \n" + ((AP_DISH.AP_Individual) dish.getBest()).equation);
            System.out.println("Vector: \n" + Arrays.toString(((AP_DISH.AP_Individual) dish.getBest()).vector));
            System.out.println("=================================");
            
            for(AP_DISH.AP_Individual ind : ((AP_DISH)dish).getBestHistory()){
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

        writer.print(bestEq);
        
        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");

        writer.close();
        
    }
    
    public static void datasetAMain(String path) throws Exception {

        int dimension = 150;
        int NP = 1534;
        int NPfinal = 4;
        int H = 5;
        int MAXFES = 90_000;

        PrintWriter writer;
        writer = new PrintWriter(path + "equation.txt", "UTF-8");
        
        String bestEq = "";
        double bestEq_val = Double.MAX_VALUE;
        
        APtfann tf = new APsmetak27A();
        /**
         * Random generator for parent selection, F and CR selection
         */
        util.random.Random generator = new util.random.UniformRandom();
        double min;
//        APconst ap = new APconst();

        Algorithm dish;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            dish = new AP_ann_DISH(dimension, MAXFES, tf, H, NP, generator, NPfinal);

            dish.run();
 
            bestArray[k] = dish.getBest().fitness - tf.optimum();
            System.out.println(dish.getBest().fitness - tf.optimum());
            
            if(bestArray[k] <= bestEq_val) {
                bestEq_val = bestArray[k];
                bestEq = ((AP_ann_DISH.AP_Individual) dish.getBest()).equation;
            }
            
            /**
             * Final AP equation.
             */

            System.out.println("=================================");
            System.out.println("Equation: \n" + ((AP_ann_DISH.AP_Individual) dish.getBest()).equation);
            System.out.println("Vector: \n" + Arrays.toString(((AP_ann_DISH.AP_Individual) dish.getBest()).vector));
            System.out.println("=================================");
            
            for(AP_ann_DISH.AP_Individual ind : ((AP_ann_DISH)dish).getBestHistory()){
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

        writer.print(bestEq);
        
        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");

        writer.close();
        
    }
    
    public static void datasetBMain(String path) throws Exception {

        int dimension = 150;
        int NP = 1534;
        int NPfinal = 4;
        int H = 5;
        int MAXFES = 90_000;

        PrintWriter writer;
        writer = new PrintWriter(path + "equation.txt", "UTF-8");
        
        String bestEq = "";
        double bestEq_val = Double.MAX_VALUE;
        
        APtfann tf = new APsmetak27B();
        /**
         * Random generator for parent selection, F and CR selection
         */
        util.random.Random generator = new util.random.UniformRandom();
        double min;
//        APconst ap = new APconst();

        Algorithm dish;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            dish = new AP_ann_DISH(dimension, MAXFES, tf, H, NP, generator, NPfinal);

            dish.run();
 
            bestArray[k] = dish.getBest().fitness - tf.optimum();
            System.out.println(dish.getBest().fitness - tf.optimum());
            
            if(bestArray[k] <= bestEq_val) {
                bestEq_val = bestArray[k];
                bestEq = ((AP_ann_DISH.AP_Individual) dish.getBest()).equation;
            }
            
            /**
             * Final AP equation.
             */

            System.out.println("=================================");
            System.out.println("Equation: \n" + ((AP_ann_DISH.AP_Individual) dish.getBest()).equation);
            System.out.println("Vector: \n" + Arrays.toString(((AP_ann_DISH.AP_Individual) dish.getBest()).vector));
            System.out.println("=================================");
            
            for(AP_ann_DISH.AP_Individual ind : ((AP_ann_DISH)dish).getBestHistory()){
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

        writer.print(bestEq);
        
        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");

        writer.close();
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here

        String path = "";
        
        path="C:\\Users\\wikki\\ownCloud\\PhD\\Predikce-Smetak\\data 20_5_2019\\dataset_A\\";
        datasetAMain(path);
        
        path="C:\\Users\\wikki\\ownCloud\\PhD\\Predikce-Smetak\\data 20_5_2019\\dataset_B\\";
        datasetBMain(path);

        
    }
    
}
