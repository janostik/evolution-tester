package model.tf;

import java.util.logging.Level;
import java.util.logging.Logger;
import model.Individual;
import util.CEC2017c;
import util.IndividualUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 *
 * CEC 2017 benchmark from C++ jupiiii
 * 
 * @author wiki on 09/10/2018
 */
public class Cec2017 implements TestFunction {
    
    private final int dim, func_num;
    private final CEC2017c cec;
    
    public Cec2017(int dim, int func_num) throws Exception {
        
        this.dim = dim;
        this.func_num = func_num;
        
        System.loadLibrary("CEC2017lib");
        this.cec = new CEC2017c();
        this.cec.init(this.dim, this.func_num);
        
    }
    
    @Override
    public double fitness(Individual individual) {
        return this.fitness(individual.vector);
    }
    
    @Override
    public double fitness(double[] vector) {

        if(this.func_num == 2) {
            return 0;
        }
        
        return this.cec.fitness(vector);
    }

    @Override
    public void constrain(Individual individual) {
        IndividualUtil.randIfOutOfBounds(individual, -100, 100);
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] vector = new double[dim];
        Random rnd = new UniformRandom();
        for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-100, 100);
        return vector;
    }

    @Override
    public double fixedAccLevel() {
        return 0;
    }

    @Override
    public double optimum() {
        return this.func_num*100;
    }

    @Override
    public double max(int dim) {
        return 100;
    }

    @Override
    public double min(int dim) {
        return -100;
    }
        
    @Override
    public String name() {
        return "CEC2017-f" + this.func_num;
    }
    
    public static void main(String[] args) {
        
        Cec2017 test;
        try {
            test = new Cec2017(10, 2);
            double res = 0;

            res = test.fitness(new double[]{0,0,0,0,0,0,0,0,0,0});
            System.out.println(res);
            
            res = test.fitness(new double[]{0,0,0,0,0,0,0,0,0,0});
            System.out.println(res);

            
        } catch (Exception ex) {
            Logger.getLogger(Cec2017.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
