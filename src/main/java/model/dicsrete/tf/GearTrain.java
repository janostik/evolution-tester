package model.dicsrete.tf;

import model.dicsrete.Solution;
import util.random.UniformRandom;

/**
 *
 * Gear Train test function
 * 
 * @author wikki on 03/05/2019
 */
public class GearTrain implements DiscreteTestFunction {

    int dim = 4;
    int[] dimensions;

    public GearTrain() {
        
        this.dimensions = new int[dim];
        for(int i = 0; i < this.dim; i++) {
            this.dimensions[i] = 49;
        }
        
    }
    
    @Override
    public double fitness(Solution individual) {
        return fitness(individual.vector);
    }

    @Override
    public double fitness(int[] vector) {
        
        double ret = 1./6.931; 
        
        ret -= (double)((12+vector[0])*(12+vector[1]))/(double)((12+vector[2])*(12+vector[3]));
        ret = Math.pow(ret, 2);

 
        return ret;
    }

    @Override
    public int[] generateTrial() {
        
        UniformRandom randGen = new UniformRandom();
        
        int[] vector = new int[this.dim];
        for(int i = 0; i < vector.length; i++) {
            vector[i] = randGen.nextInt(this.dimensions[i]);
        }
        
        return vector;
    }

    @Override
    public double optimum() {
        return 0;
    }

    @Override
    public int max(int dim) {
        return 49;
    }

    @Override
    public int min(int dim) {
        return 0;
    }

    @Override
    public int[] getDimensions() {
        return dimensions;
    }

    @Override
    public String name() {
        return "MinimalBinary";
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    
        DiscreteTestFunction tf = new GearTrain();
        
        System.out.println(tf.fitness(new int[]{7,4,37,31}));
        
    
    }
    
}
