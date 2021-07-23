package model.tf;

import model.tf.TestFunction;
import model.Individual;

import java.util.stream.DoubleStream;
import util.IndividualUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 * Created by jakub on 27/10/15.
 */
public class Dejong implements TestFunction {
    
    public int dim;

    public Dejong(int dim) {
        this.dim = dim;
    }

    public Dejong() {
    }
    
    @Override
    public double fitness(Individual individual) {
        return fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {
        return DoubleStream.of(vector).map(d -> d * d).sum();
    }

    @Override
    public void constrain(Individual individual) {
        IndividualUtil.clipInBounds(individual, -10, 10);
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] vector = new double[dim];
        Random rnd = new UniformRandom();
        for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-10, 10);
        return vector;
    }

    @Override
    public double fixedAccLevel() {
        return 10E-2;
    }

    @Override
    public double optimum() {
        return 0.0;
    }

    @Override
    public double max(int dim) {
        return 10;
    }

    @Override
    public double min(int dim) {
        return -10;
    }

    @Override
    public String name() {
        return "Dejong";
    }

    @Override
    public double[] optimumPosition() {
        return new double[this.dim];
    }
    
    /**
     * 
     * @return 
     */
    private double[][] findLimits() {
        
        double[][] limits = new double[dim][2];
        
        double threshold = Math.pow(10,-15), sens = Math.pow(10, -8);
        double mid = 0, left, right, res;
        double[] active;
        
        for(int i = 0; i < dim; i++) {
            //Finds the lower optimum limit
            left = this.min(dim);
            right = this.optimumPosition()[i];
            while(Math.abs(left-right) > threshold) {
                
                mid = (left+right)/2.0;
                active = this.optimumPosition();
                active[i] = mid;
                res = this.fitness(active);
                if(Math.abs(res - this.optimum()) <= sens) {
                    right = mid;
                } else {
                    left = mid;
                }
                
            }
            limits[i][0] = mid;
            
            //Finds the upper optimum limit
            right = this.max(dim);
            left = this.optimumPosition()[i];
            while(Math.abs(left-right) > threshold) {
                
                mid = (left+right)/2.0;
                active = this.optimumPosition();
                active[i] = mid;
                res = this.fitness(active);
                if(Math.abs(res - this.optimum()) <= sens) {
                    left = mid;
                } else {
                    right = mid;
                }
                
            }
            limits[i][1] = mid;
            
        }
        
        return limits;
    }
    
    public static void main(String[] args) {
    
        Dejong sphere = new Dejong(2);
        double[][] limits = sphere.findLimits();

        for(int i = 0; i < limits.length; i++) {

            System.out.println("dim: " + (i+1) + " - [" + limits[i][0] + ", " + limits[i][1] + "]");

        }
        
    }
    
}
