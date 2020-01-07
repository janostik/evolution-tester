package algorithm.de;

import algorithm.Algorithm;
import java.util.ArrayList;
import java.util.List;
import model.Individual;
import model.tf.Schwefel;
import model.tf.TestFunction;
import util.random.Random;

/**
 *
 * DE algorithm with current to pbest 1 mutation strategy
 * 
 * @author wiki on 12/04/2016
 */
public class DEcurtopbest extends DErand1bin {

    public DEcurtopbest(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
    }
    
    @Override
    public Individual runAlgorithm() {

        /**
         * Initial population
         */
        initializePopulation();
        if (checkFES()) {
            return best;
        }

        List<Individual> newPop, pbestList;
        Individual x, trial;
        double[] u, v;
        Individual[] parrentArray;
        double pmin = 2 / (double) this.NP;
        int Psize;
        
        /**
         * generation itteration
         */
        while (true) {

            G++;
            newPop = new ArrayList<>();

            /**
             * Iteration through all individuals in generation.
             */
            for (int xIter = 0; xIter < NP; xIter++) {

                Psize = (int) (rndGenerator.nextDouble(pmin, 0.2) * this.NP);
                if (Psize < 2) {
                    Psize = 2;
                }
                
                pbestList = new ArrayList<>();
                pbestList.addAll(this.P);
                pbestList = this.resizePop(pbestList, Psize);
                
                /**
                 * Parent selection
                 */
                parrentArray = getParents(pbestList, P, xIter);
                x = parrentArray[0];

                /**
                 * Mutation
                 */
                u = mutation(parrentArray, F);

                /**
                 * Crossover
                 */
                v = crossover(x.vector, u, CR);

                /**
                 * Trial
                 */
                trial = makeIndividualFromVector(v);
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
     *
     * @param list
     * @return
     */
    protected Individual getBestFromList(List<Individual> list) {

        Individual b = null;

        for (Individual ind : list) {

            if (b == null) {
                b = ind;
            } else if (ind.fitness < b.fitness) {
                b = ind;
            }
        }

        return b;

    }
    
    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<Individual> resizePop(List<Individual> list, int size) {

        if(size >= list.size()){
            return list;
        }
        
        List<Individual> toRet = new ArrayList<>();
        List<Individual> tmp = new ArrayList<>();
        tmp.addAll(list);
        Individual bestInd;

        for (int i = 0; i < size; i++) {
            bestInd = this.getBestFromList(tmp);
            toRet.add(bestInd);
            tmp.remove(bestInd);
        }

        return toRet;

    }
    
    /**
     *
     * List of parents for mutation x, pbest, r1, r2
     *
     * @param pbestList
     * @param pList
     * @param xIndex
     * @return
     */
    protected Individual[] getParents(List<Individual> pbestList, List<Individual> pList, int xIndex) {

        Individual[] parentArray = new Individual[4];
        List<Integer> indexes = new ArrayList<>();
        int index;
        parentArray[0] = pList.get(xIndex);
        
        //Select pbest different from x
        parentArray[1] = pbestList.get(rndGenerator.nextInt(pbestList.size()));
        while(parentArray[1] == parentArray[0]){
            parentArray[1] = pbestList.get(rndGenerator.nextInt(pbestList.size()));
        }

        //Add indexes of the rest for selection of the r1 and r2
        for (int i = 0; i < pList.size(); i++) {
            if (pList.get(i) != parentArray[0] && pList.get(i) != parentArray[1]) {
                indexes.add(i);
            }
        }

        /**
         * r1
         */
        index = rndGenerator.nextInt(indexes.size());
        parentArray[2] = pList.get(indexes.get(index));
        
        //removes index of r1 from selection
        indexes.remove(index);

        /**
         * r2
         */
        index = rndGenerator.nextInt(indexes.size());
        parentArray[3] = pList.get(indexes.get(index));

        return parentArray;

    }
    
    /**
     *
     * @param parentArray
     * @param F
     * @return
     */
    @Override
    protected double[] mutation(Individual[] parentArray, double F) {

        double[] u = new double[D];
        double[] x = parentArray[0].vector;
        double[] pbest = parentArray[1].vector;
        double[] r1 = parentArray[2].vector;
        double[] r2 = parentArray[3].vector;

        for (int i = 0; i < D; i++) {

            u[i] = x[i] + F * (pbest[i] - x[i]) + F * (r1[i] - r2[i]);

        }

        return u;

    }

    @Override
    public String getName() {
        return "DEcurtopbest";
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int dimension = 10;
        int NP = 100;
        int MAXFES = 10000 * dimension;
        TestFunction tf = new Schwefel();
        util.random.Random generator = new util.random.UniformRandom();
        double f = 0.5, cr = 0.8, min;

        Algorithm de = new DEcurtopbest(dimension, NP, MAXFES, tf, generator, f, cr);
        
        de.runAlgorithm();
        
        System.out.println(de.getBest().fitness);
        
    }

    
    
}
