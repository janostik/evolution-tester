package model.dicsrete.tf;

import model.dicsrete.Solution;
import util.random.UniformRandom;

/**
 *
 * Dummy test function
 * Returns a sum of attribute values
 * 
 * @author wikki on 03/05/2019
 */
public class MinBin implements DiscreteTestFunction {

    int dim;
    int[] dimensions;

    public MinBin(int dim) {
        this.dim = dim;
        
        this.dimensions = new int[dim];
        for(int i = 0; i < this.dim; i++) {
            this.dimensions[i] = 2;
        }
        
    }
    
    @Override
    public double fitness(Solution individual) {
        return fitness(individual.vector);
    }

    @Override
    public double fitness(int[] vector) {
        
        double ret = 0;
        
        for(int i = 0; i < vector.length; i++) {
            ret += vector[i];
        }
        
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
        return 1;
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
    
}
