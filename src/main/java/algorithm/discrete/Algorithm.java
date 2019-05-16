package algorithm.discrete;

import java.util.List;
import model.dicsrete.Solution;
import model.dicsrete.tf.DiscreteTestFunction;

/**
 *
 * Algorithm interface for discrete optimization
 * 
 * @author wikki on 03/05/2019
 */
public interface Algorithm {
  
    Solution run();

    List<? extends Solution> getPopulation();

    DiscreteTestFunction getTestFunction();

    default Solution getBest() {
        Solution best = getPopulation().get(0);
        for (Solution individual : getPopulation()) {
            if (individual.fitness < best.fitness) best = individual;
        }
        return best;
    }
    
    List<Solution> getBestHistory();

    String getName();
    
}
