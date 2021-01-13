package model.tf;

import model.tf.TestFunction;
import algorithm.de.SNLs_SHADE;
import java.util.ArrayList;
import java.util.List;
import model.Individual;
import model.NetworkIndividual;
import util.IndividualUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 *
 * Test function for metaheuristic
 * 
 * @author wiki on 03/05/2016
 */
public class SNLs_SHADE_tf implements TestFunction{

    private long seed;
    private TestFunction tf;
    private int dimension;
    private int max_fes;
    private int H;
    private int NPinit;
    private int NPfinal;
    util.random.Random rndGenerator;

    public SNLs_SHADE_tf(long seed, TestFunction tf, int dimension, int max_fes, int H, int NPinit, int NPfinal) {
        this.seed = seed;
        this.tf = tf;
        this.dimension = dimension;
        this.max_fes = max_fes;
        this.H = H;
        this.NPinit = NPinit;
        this.NPfinal = NPfinal;
    }
    
    @Override
    public double fitness(Individual individual) {
        
        return this.fitness(individual.vector);
        
    }

    @Override
    public double fitness(double[] vector) {
        
        this.rndGenerator = new util.random.UniformRandomSeed(seed);
        String[] id_list = this.getIDs(vector);
        SNLs_SHADE shade = new SNLs_SHADE(dimension, max_fes, tf, H, NPinit, rndGenerator, NPfinal, id_list);
        shade.runAlgorithm();
        
        return shade.getBest().fitness;
    }
    
    public List<NetworkIndividual> getDeadList(Individual individual) {
        
        return this.getDeadList(individual.vector);
        
    }

    public List<NetworkIndividual> getDeadList(double[] vector) {
        
        this.rndGenerator = new util.random.UniformRandomSeed(seed);
        String[] id_list = this.getIDs(vector);
        SNLs_SHADE shade = new SNLs_SHADE(dimension, max_fes, tf, H, NPinit, rndGenerator, NPfinal, id_list);
        shade.runAlgorithm();
        
        return shade.dead_list;
    }
    
    /**
     * 
     * Gets ID list of individuals to be killed
     * 
     * @param vector
     * @return 
     */
    private String[] getIDs(double[] vector){
        
        List<String> id_list = new ArrayList<>();
        
        for(int i = 0; i < this.NPinit; i++){
            
            id_list.add(String.valueOf(i));
            
        }
        
        int size, index;
        String[] toRet = new String[vector.length];
        
        for(int i = 0; i < vector.length; i++){
            
            size = id_list.size();
            index = ((int) Math.round(vector[i]*size)) % size;
            
            toRet[i] = id_list.get(index);
            id_list.remove(index);
            
        }
        
        return toRet;
        
    }

    @Override
    public double optimum() {
        return this.tf.optimum();
    }

    @Override
    public double max(int dim) {
        return 1;
    }

    @Override
    public double min(int dim) {
        return 0;
    }

    @Override
    public String name() {
        return "SNLs_SHADE_tf";
    }

    @Override
    public void constrain(Individual individual) {
        IndividualUtil.clipInBounds(individual, 0, 1);
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] vector = new double[dim];
        Random rnd = new UniformRandom();
        for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble();
        return vector;
    }

    @Override
    public double fixedAccLevel() {
        return 10^-7;
    }

    @Override
    public double[] optimumPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
