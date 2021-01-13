package model.tf.ap;

import model.Individual;
import model.ap.APconst;
import model.tf.TestFunction;

/**
 *
 * @author wiki
 */
public class APtf implements TestFunction {

    public APconst ap = new APconst();
    /**
     * Change generator.
     */
    util.random.Random generator = (util.random.Random) new util.random.UniformRandom();
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
            trial[i] = generator.nextDouble(this.min(dim), this.max(dim));
        }
        return trial;
    }

    @Override
    public double max(int dim) {
//        return (this.ap.getGFSsize()-1);
        return ap.max;
    }

    @Override
    public double min(int dim) {
//        return 0;
        return ap.min;
    }
    
    public int countLength(double[] vector){
        
        Integer[] v, gfs_code;
        int length = vector.length;
        
        v = ap.discretizeVector(vector);
        gfs_code = ap.getGFScode(v);

        for(int j = 0; j < vector.length; j++){
            if(gfs_code[j] == -1){
                length = j;
                break;
            }
        }
        
        return length;
        
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

    @Override
    public double[] optimumPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
