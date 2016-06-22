package algorithm.hybrid;

import AP.algorithm.Algorithm;
import AP.algorithm.de.ap.AP_DEbest;
import algorithm.de.DErand1bin;
import AP.model.AP_Individual;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import AP.model.ap.objects.AP_Abs;
import AP.model.ap.objects.AP_Cos;
import AP.model.ap.objects.AP_Div;
import AP.model.ap.objects.AP_Exp;
import AP.model.ap.objects.AP_Ln;
import AP.model.ap.objects.AP_Multiply;
import AP.model.ap.objects.AP_Plus;
import AP.model.ap.objects.AP_Quad;
import AP.model.ap.objects.AP_Sin;
import AP.model.ap.objects.AP_Sqrt;
import AP.model.ap.objects.AP_Sub;
import AP.model.ap.objects.AP_Tan;
import AP.model.ap.objects.AP_aTOb;
import AP.model.ap.objects.AP_object;
import AP.model.ap.objects.AP_x1;
import model.tf.TestFunction;
import AP.model.tf.ap.regression.APdataset;
import AP.util.APIndividualComparator;
import model.tf.Schwefel;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.random.Random;

/**
 *
 * This algorithm uses analytical porgramming to synthesize objective function.
 * This synthetic objective function is used to find possible best combination of F and CR for real objective function solving.
 * 
 * It is generaly intended for costly objective function evaluations.
 * 
 * @author wiki on 22/06/2016
 */
public class SOF_DErand1bin_AP extends DErand1bin {

    /**
     * SOF dataset consisting of D lists of pairs of x and y values
     */
    protected List<List<double[]>> SOF_dataset;
    protected APdataset[] AP_datasets;
    protected double[][] AP_vector_solutions;
    protected List<AP_Individual>[] AP_populations;
    
    protected double[] F_values = new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
    protected double[] CR_values = new double[]{0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
    protected List<Double> F_success;
    protected List<Double> CR_success;
    
    
    
    /**
     * 
     * @param D
     * @param NP
     * @param MAXFES
     * @param f
     * @param rndGenerator
     */
    public SOF_DErand1bin_AP(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator) {
        
        super(D, NP, MAXFES, f, rndGenerator, 0.5, 0.8);
        
        SOF_dataset = new ArrayList<>();
        AP_populations = new ArrayList[this.D];
        for(int i = 0; i < this.D; i++){
            SOF_dataset.add(new ArrayList<>());
            AP_populations[i] = new ArrayList<>();
        }
        
        AP_datasets = new APdataset[this.D];

        this.F_success = new ArrayList<>();
        this.CR_success = new ArrayList<>();
        
    }
    
    /**
     * This function uses SOF to get the best combiantiopn of F and CR and returns vector produced by these values.
     * 
     * @param parentArray
     * @return 
     */
    protected double[] getBestFromSOF(Individual[] parentArray) {
        
        double[] u, v, best_vector = new double[this.D];
        double val, best_value = Double.MAX_VALUE, suc_F = 0, suc_CR = 0;

        /**
         * Cycle over F values
         */
        for(int f = 0; f < this.F_values.length; f++) {
            
            /**
             * Cycle over CR values
             */
            for(int cr = 0; cr < this.CR_values.length; cr++) {
                
                u = mutation(parentArray, F_values[f]);
                v = crossover(parentArray[0].vector, u, CR_values[cr]);
                val = this.getFitnessValueOnSOF(v);
                if(val <= best_value) {
                    best_vector = v.clone();
                    best_value = val;
                    suc_F = F_values[f];
                    suc_CR = CR_values[cr];
                }
                
            }
            
        }

        this.F_success.add(suc_F);
        this.CR_success.add(suc_CR);
        
        return best_vector;
        
    }
    
    /**
     * Evaluates the fitness value on synthesized OF.
     * 
     * @param vector
     * @return 
     */
    protected double getFitnessValueOnSOF(double[] vector) {
        
        double sum = 0;
        
        for(int i = 0; i < vector.length; i++) {
            
            sum += (AP_datasets[i].ap.dsh(AP_vector_solutions[i], new double[]{vector[i]}));
            
        }
        
        return sum;
        
    }
    
    /**
     * Synthesizes the objective function
     */
    protected void synthesizeOF() {
        
        AP_datasets = new APdataset[this.D];

        ArrayList<AP_object> GFS = new ArrayList<>();
        GFS.add(new AP_Plus()); //Addition
        GFS.add(new AP_Sub()); //Subtraction
        GFS.add(new AP_Multiply()); //Mulitplication
        GFS.add(new AP_Div()); //Divison
        GFS.add(new AP_aTOb());
        GFS.add(new AP_Cos());
        GFS.add(new AP_Sin());
        GFS.add(new AP_Tan());
        GFS.add(new AP_Ln());
        GFS.add(new AP_Exp());
        GFS.add(new AP_Abs());
        GFS.add(new AP_Sqrt());
        GFS.add(new AP_Quad());
        GFS.add(new AP_x1()); //Independent variable x1
//        GFS.add(new AP_Const_static()); //Constant object - value is evolved in the extension of the individuals
        
        double min_const = -10, max_const = 10;

        Algorithm AP_de;
        int AP_D = 18;
        int AP_NP = 20;
        int AP_G = 100;
        int AP_MAXFES = AP_G * AP_NP;
        double AP_F = 0.5, AP_CR = 0.8;
        AP.util.random.Random generator = new AP.util.random.UniformRandom();
        
        AP_vector_solutions = new double[this.D][AP_D];
        
        /**
         * Cycle over dimensions - single datasets
         */
        for(int i = 0; i < this.SOF_dataset.size(); i++) {
            this.AP_datasets[i] = new APdataset(this.SOF_dataset.get(i), GFS, min_const, max_const);
            AP_de = new AP_DEbest(AP_D, AP_NP, AP_MAXFES, this.AP_datasets[i], generator, AP_F, AP_CR, AP_populations[i]);
//            AP_de = new AP_DErand1bin(AP_D, AP_NP, AP_MAXFES, this.AP_datasets[i], generator, AP_F, AP_CR);
//            AP_de = new AP_ShaDE(AP_D, AP_NP, AP_MAXFES, this.AP_datasets[i], generator, 10);
            AP_de.run();
            AP_vector_solutions[i] = AP_de.getBest().vector.clone();
//            AP_populations[i] = (List<AP_Individual>) AP_de.getPopulation();
//            AP_populations[i] = this.getOnlyHalfOfThePopulation((List<AP_Individual>) AP_de.getPopulation());
            
//            System.out.println("Equation " + (i+1) + ": " + ((AP_Individual)AP_de.getBest()).equation);
        }

    }
    
    /**
     * 
     * @param population
     * @return 
     */
    private List<AP_Individual> getOnlyHalfOfThePopulation(List<AP_Individual> population) {
        
        population.sort(new APIndividualComparator());
        List<AP_Individual> listToRet = new ArrayList<>();
        
        for(int i = 0; i < population.size()/2; i++) {
            listToRet.add(population.get(i));
        }

        return listToRet;
        
    }
    
    /**
     * Adds individual to SOF dataset
     * 
     * @param ind 
     */
    protected void addIndividualToSOFdataset(Individual ind){
        
        if(this.SOF_dataset.get(0).size() >= 100) {
            return;
        }
        
        double[] point;
        
        for(int i = 0; i < this.D; i++) {
            
            point = new double[2];
            point[0] = ind.vector[i];
            point[1] = ind.fitness / this.D;
            
            this.SOF_dataset.get(i).add(point);
            
        }
        
    }
    
    /**
     *
     */
    @Override
    protected void initializePopulation() {

        P = new ArrayList<>();
        double[] vector;
        Individual indToAdd;

        for (int i = 0; i < NP; i++) {

            if (checkFES()) {
                return;
            }
            vector = tf.generateTrial(D);
            indToAdd = makeIndividualFromVector(vector);
            //Added to population
            P.add(indToAdd);
            //Added to SOF dataset
            this.addIndividualToSOFdataset(indToAdd);
            
        }

    }
    
    /**
     * 
     * @return 
     */
    @Override
    public Individual run() {

        /**
         * Initial population
         */
        initializePopulation();
        if (checkFES()) {
            return best;
        }
        /**
         * Initial dataset for SOF is initial population
         */
//        for(int i = 0; i < this.D; i++) {
//            
//            for(int j = 0; j < SOF_dataset.get(i).size(); j++) {
//                System.out.println(Arrays.toString(SOF_dataset.get(i).get(j)));
//            }
//            System.out.println("========================================");
//        }
        

        List<Individual> newPop;
        Individual x, trial;
        double[] u, v;
        Individual[] parentArray;

        /**
         * generation itteration
         */
        while (true) {

            /**
             * Synthesizing objective function with AP and SOF_dataset
             */
            this.synthesizeOF();
            
            G++;
            newPop = new ArrayList<>();

            /**
             * Iteration through all individuals in generation.
             */
            for (int xIter = 0; xIter < NP; xIter++) {

                /**
                 * Parent selection
                 */
                parentArray = getParents(xIter);
                x = parentArray[0];

                /**
                 * Selection of best vector for next generation
                 * 
                 * Using SOF to obtain best combiantion of F and CR values
                 * and to produce final vector
                 */
                v = this.getBestFromSOF(parentArray);
                
                /**
                 * Mutation
                 */
//                u = mutation(parentArray, F);

                /**
                 * Crossover
                 */
//                v = crossover(x.vector, u, CR);

                /**
                 * Trial
                 */
                trial = makeIndividualFromVector(v);
                this.addIndividualToSOFdataset(trial);
                if (checkFES()) {
                    return best;
                }

                /**
                 * New generation building
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                } else {
                    newPop.add(x);
                }

            }

            P = newPop;

        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int dimension = 10;
        int NP = 20;
        int iter = 10;
        int MAXFES = iter * NP;
        TestFunction tf = new Schwefel();
        util.random.Random generator = new util.random.UniformRandom();

        SOF_DErand1bin_AP de;

        int runs = 30;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            de = new SOF_DErand1bin_AP(dimension, NP, MAXFES, tf, generator);

            de.run();

            bestArray[k] = de.getBest().fitness - tf.optimum();
            System.out.println(de.getBest().fitness - tf.optimum());
            
//            double[] arrF = de.F_success.stream().mapToDouble(Double::doubleValue).toArray();
//            double[] arrCR = de.CR_success.stream().mapToDouble(Double::doubleValue).toArray();
//            
//            System.out.println("=============== F values ==================");
//            System.out.println("Min: " + DoubleStream.of(arrF).min().getAsDouble());
//            System.out.println("Max: " + DoubleStream.of(arrF).max().getAsDouble());
//            System.out.println("Mean: " + new Mean().evaluate(arrF));
//            System.out.println("Median: " + new Median().evaluate(arrF));
//            System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(arrF));
//            System.out.println("=============== CR values ==================");
//            System.out.println("Min: " + DoubleStream.of(arrCR).min().getAsDouble());
//            System.out.println("Max: " + DoubleStream.of(arrCR).max().getAsDouble());
//            System.out.println("Mean: " + new Mean().evaluate(arrCR));
//            System.out.println("Median: " + new Median().evaluate(arrCR));
//            System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(arrCR));

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
