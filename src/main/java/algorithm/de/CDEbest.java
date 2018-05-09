package algorithm.de;

import java.util.ArrayList;
import java.util.List;
import model.Individual;
import model.tf.TestFunction;
import util.random.Random;

/**
 *
 * @author wiki on 5/1/2018
 */
public class CDEbest extends DEbest {

    util.random.Random chaosGenerator;
    
    public CDEbest(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR, Random chaosGenerator) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
        this.chaosGenerator = chaosGenerator;
    }
    
    /**
     *
     * List of parents for mutation x, a, b, c
     *
     * @param xIndex
     * @return
     */
    @Override
    protected Individual[] getParents(int xIndex) {

        Individual[] parrentArray = new Individual[4];
        List<Integer> indexes = new ArrayList<>();
        int index;
        
        parrentArray[0] = P.get(xIndex);
        
        /**
         * best
         */
        index = getBestIndividualInPopulation();
        parrentArray[1] = P.get(index);

        for (int i = 0; i < NP; i++) {
            if (i != xIndex && i != index) {
                indexes.add(i);
            }
        }

        /**
         * b
         */
        index = chaosGenerator.nextInt(indexes.size());
        parrentArray[2] = P.get(indexes.get(index));

        indexes.remove(index);

        /**
         * c
         */
        index = chaosGenerator.nextInt(indexes.size());
        parrentArray[3] = P.get(indexes.get(index));

        return parrentArray;

    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }

    
    
}
