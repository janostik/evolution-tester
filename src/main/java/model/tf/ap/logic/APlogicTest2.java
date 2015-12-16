package model.tf.ap.logic;

import model.Individual;
import util.RandomUtil;

/**
 * 
 * @author adam
 */
public class APlogicTest2 extends APlogictf {

    double[] y = new double[]{0,1,1,1,0,0,0,1};
    
    @Override
    public double fitness(Individual individual) {
        return this.fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {

        Integer[] discrete = this.discretizeVector(vector);

        double fitness = 0;

        fitness = this.countFitness(discrete);

        return fitness;

    }

    public Integer[] discretizeVector(double[] vector) {
        int dim = vector.length;
        Integer[] discrete = new Integer[dim];

        for (int i = 0; i < dim; i++) {
            discrete[i] = (int) Math.round(vector[i]);
        }

        return discrete;
    }

    private double countFitness(Integer[] vector) {

        double sum = 0, tmp, length_add = 1;
        Integer[] gfs_vector = ap.getGFScode(vector);
        
        for(int i = 0; i < gfs_vector.length; i++){
            if(gfs_vector[i] == -1){
                length_add = 1/(double) (gfs_vector.length - i);
                break;
            }
        }
        
        
        for (int i = 0; i < y.length; i++) {
            
            
            
            tmp = ap.dsh(vector, i);
            sum += ((tmp + y[i]) % 2);
        }
        
        return sum + length_add;

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
            trial[i] = RandomUtil.nextInt((int) (this.max(dim) + 1));
        }
        return trial;
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
    public double max(int dim) {
        return this.ap.getGFSsize()-1;
    }

    @Override
    public double min(int dim) {
        return 0;
    }

    @Override
    public String name() {
        return "AP_sin_function";
    }

    public static void main(String[] args) throws Exception {
    
        double[] vector = new double[]{0,1,2,3,4,5};
        APlogictf tf = new APlogicTest2();
        
        System.out.println(tf.fitness(vector));
        System.out.println(tf.ap.equation);
        
    }
    
}
