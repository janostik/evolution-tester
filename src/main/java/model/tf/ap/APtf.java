package model.tf.ap;

import model.Individual;
import model.ap.APconst;
import model.tf.TestFunction;
import util.LoziRandomUtil;
import util.RandomUtil;

/**
 *
 * @author wiki
 */
public class APtf implements TestFunction {

    public APconst ap = new APconst();
//    public AP ap = new AP();
    
    @Override
    public double fitness(Individual individual) {
        return this.fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {
        
        return this.getDistance(vector);
        
    }

    protected double getDistance(double[] vector){
        return 0;
    }
    
    @Override
    public void constrain(Individual individual) {
        for (int i = 0; i < individual.vector.length; i++) {

            if (individual.vector[i] > this.max(individual.vector.length)) {
                individual.vector[i] = this.max(individual.vector.length);
            }
            if (individual.vector[i] < this.min(individual.vector.length)) {
                individual.vector[i] = this.min(individual.vector.length);
            }

        }
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] trial = new double[dim];
        for (int i = 0; i < dim; i++) {
//            trial[i] = LoziRandomUtil.nextInt((int) (this.max(dim) + 1));
            trial[i] = LoziRandomUtil.nextDouble(this.min(dim), this.max(dim));
//            trial[i] = RandomUtil.nextDouble(this.min(dim), this.max(dim));
        }
        return trial;
    }

    @Override
    public double max(int dim) {
//        return (this.ap.getGFSsize()-1);
        return 100;
    }

    @Override
    public double min(int dim) {
//        return 0;
        return -100;
    }
    
    @Override
    public double fixedAccLevel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double optimum() {
        return 0;
    }

    

    @Override
    public String name() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
