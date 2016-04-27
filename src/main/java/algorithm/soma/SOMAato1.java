package algorithm.soma;

import algorithm.Algorithm;
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
import util.random.UniformRandom;

/**
 *
 * @author wiki on 21/04/2016
 */
public class SOMAato1 implements Algorithm {

    /**
     * Parameters
     */
    private TestFunction tf;
    private double path_length;
    private double step_length;
    private double PRT;
    private int dimension;
    private int pop_size;
    private int MAXFES;

    /**
     * Internal
     */
    private int current_FES;
    private List<Individual> P;
    private int id;
    private Individual best;
    List<Individual> best_history;
    util.random.Random rnd_generator = new UniformRandom();
    
    /**
     * Constructor
     * 
     * @param tf
     * @param path_length
     * @param step_length
     * @param PRT
     * @param dimension
     * @param pop_size
     * @param MAXFES 
     */
    public SOMAato1(TestFunction tf, double path_length, double step_length, double PRT, int dimension, int pop_size, int MAXFES) {
        this.tf = tf;
        this.path_length = path_length;
        this.step_length = step_length;
        this.PRT = PRT;
        this.dimension = dimension;
        this.pop_size = pop_size;
        this.MAXFES = MAXFES;
        this.id = 0;
    }

    /**
     * Run method
     * Evolution takes palce here
     * 
     * @return 
     */
    @Override
    public Individual run() {
        
        // Creation of the initial population
        this.initializePopulation();
        
        //MAXFES reached, return best individual
        if(checkFES()) {
            return best;
        }
        
        List<Individual> new_pop;
        Individual leader, current;
        
        //main cycle
        while(true) {
            
            new_pop = new ArrayList<>();
            //Get leader of the population
            leader = this.getLeader(P);

            //Cycle through population
            for(int i = 0; i < pop_size; i++) {
                
                //Get current individual
                current = this.P.get(i);
                
                //Perturbate and change it
                //Add it to new population
                new_pop.add(this.perturbation(current, leader));
                
                //MAXFES reached
                if(checkFES()) {
                    return best;
                }
            }
            
            P = new ArrayList<>();
            P.addAll(new_pop);
            
        }
        
    }

    /**
     * Whole perturbation process is covered here.
     * NOTE:
     * This version perturbates differentily than in Zelinka scripts
     * 
     * @param current
     * @param leader
     * @return 
     */
    protected Individual perturbation(Individual current, Individual leader) {
        
        if(current == leader) {
            return current;
        }
        
        Individual ind_to_ret = new Individual(current);
        Individual ind_new_pos = new Individual(current);
        Individual ind_start = new Individual(current);
        int step_count = (int) (path_length / step_length);
        double[] new_position = ind_new_pos.vector.clone();
        int[] prt_vector;
        
        //Cycle through individual steps
        for(int step = 1; step <= step_count; step++){
            
            //Get PRT vector for perturbation
            prt_vector = this.getPrtVector();
            
            //Cycle through dimensions, creation of the new position
            for(int d = 0; d < dimension; d++){
                
                new_position[d] += ((leader.vector[d] - ind_start.vector[d])*step_length*prt_vector[d]);
                
            }
            
            ind_new_pos = this.makeIndividualFromVector(new_position, Integer.parseInt(current.id));
            
            if(ind_new_pos.fitness < ind_to_ret.fitness){
                ind_to_ret = new Individual(ind_new_pos);
            }
            
            if(checkFES()){
                return ind_to_ret;
            }

        }
        
        return ind_to_ret;
        
    }
    
    /**
     * 
     * @return 
     */
    protected int[] getPrtVector() {
        
        int[] prt_vector = new int[dimension];
        boolean is_there_zero = false;
        boolean is_there_one = false;
        
        for(int d = 0; d < dimension; d++) {
            prt_vector[d] = (rnd_generator.nextDouble() < PRT) ? 1 : 0;
            if(prt_vector[d] == 0) {
                is_there_zero = true;
            }
            else {
                is_there_one = true;
            }
        }
        
        if(!is_there_zero) {
            prt_vector[rnd_generator.nextInt(dimension)] = 0;
        }
        if(!is_there_one) {
            prt_vector[rnd_generator.nextInt(dimension)] = 1;
        }
        
        return prt_vector;
        
    }
    
    /**
     * Finds the best individual in list
     * 
     * @param list
     * @return 
     */
    protected Individual getLeader(List<Individual> list) {
        
        Individual leader = null;
        
        for(Individual ind : list){
            
            if(leader == null || ind.fitness < leader.fitness){
                leader = ind;
            }
            
        }
        
        return leader;
        
    }
    
    /**
     * Checks if the maximum number of cost function evaluations was reached.
     * Returns true if it was, otherwise false.
     * 
     * @return 
     */
    protected boolean checkFES() {
        
        return current_FES >= MAXFES;
        
    }
    
    /**
     *
     */
    protected void initializePopulation() {

        P = new ArrayList<>();
        double[] vector;

        for (int i = 0; i < pop_size; i++) {

            if (checkFES()) {
                return;
            }
            vector = tf.generateTrial(dimension);
            P.add(makeIndividualFromVector(vector, id));
            id++;
            
        }

    }
    
    /**
     *
     * @param vector
     * @param id
     * @return
     */
    protected Individual makeIndividualFromVector(double[] vector, int id) {

        Individual ind = new Individual();
        ind.id = String.valueOf(id);
//        id++;
        ind.vector = vector.clone();
        constrain(ind);
        ind.fitness = tf.fitness(vector);
        current_FES++;
        isBest(ind);
        writeHistory();

        return (ind);
    }
    
    /**
     * 
     * @param individual 
     */
    protected void constrain(Individual individual){
        
        tf.constrain(individual);
        
    }
    
    /**
     *
     */
    protected void writeHistory() {
        if (best_history == null) {
            best_history = new ArrayList<>();
        }
        best_history.add(best);
    }
    
    /**
     *
     * @param ind
     * @return
     */
    protected boolean isBest(Individual ind) {

        if (best == null || ind.fitness < best.fitness) {
            best = ind;
            return true;
        }

        return false;

    }
    
    /**
     * 
     * @return 
     */
    @Override
    public List<? extends Individual> getPopulation() {
        return P;
    }

    /**
     * 
     * @return 
     */
    @Override
    public TestFunction getTestFunction() {
        return tf;
    }

    /**
     * 
     * @return 
     */
    public List<Individual> getBestHistory() {
        return best_history;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String getName() {
        return "SOMA-all-to-1";
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        int dimension = 10;
        int NP = 60;
        int MAXFES = 200000;
        int funcNumber = 5;
        TestFunction tf = new Schwefel();
        util.random.Random generator = new util.random.UniformRandom();
        double  min, path_length = 3, step_length = 0.3, PRT = 0.1;

        Algorithm soma;

        int runs = 10;
        double[] bestArray = new double[runs];
        int i, best;

        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            soma = new SOMAato1(tf, path_length, step_length, PRT, dimension, NP, MAXFES);

            soma.run();

            bestArray[k] = soma.getBest().fitness;
            System.out.println("Individual: " + Arrays.toString(soma.getBest().vector));
            System.out.println("Fitness: " + soma.getBest().fitness);
            System.out.println("Evals: " + ((SOMAato1)soma).getBestHistory().size());
            
            for(Individual ind : ((SOMAato1)soma).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");

//            System.out.println(de.getBest().fitness - ((DErand1bin) de).getBestHistory().get(MAXFES-1).fitness);
            
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
