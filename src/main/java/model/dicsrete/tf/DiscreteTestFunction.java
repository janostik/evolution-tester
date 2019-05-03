package model.dicsrete.tf;

import model.dicsrete.Solution;

/**
 *
 * Discrete test fucntion interface
 * 
 * @author wikki on 03/05/2019
 */
public interface DiscreteTestFunction {
    
    double fitness(Solution individual);

    /**
     * Convenience override for fitness(Individiual individual)
     *
     * @param vector
     * @return fitness for given vector
     */
    double fitness(int[] vector);

    int[] generateTrial();

    double optimum();

    int max(int dim);

    int min(int dim);
    
    int[] getDimensions();
    
    String name();
    
}
