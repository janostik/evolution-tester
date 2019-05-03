package util;

import java.util.Comparator;
import model.dicsrete.Solution;

/**
 *
 * @author wiki on 17/06/2016
 */
public class SolutionComparator implements Comparator<Solution> {
    
    @Override
    public int compare(Solution t, Solution t1) {

        if(t.fitness < t1.fitness) {
            return -1;
        }
        else if(t.fitness == t1.fitness){
            return 0;
        }
        else {
            return 1;
        }

    }
    
}
