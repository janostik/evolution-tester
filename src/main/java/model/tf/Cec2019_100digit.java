package model.tf;

import java.util.logging.Level;
import java.util.logging.Logger;
import model.Individual;
import util.CEC2017c;
import util.CEC2019c;
import util.IndividualUtil;
import util.RandomUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 *
 * CEC 2019 benchmark - 100 digit
 * 
 * @author wiki on 15/03/2019
 */
public class Cec2019_100digit implements TestFunction {
    
    private final int dim, func_num;
    private final CEC2019c cec;
    
    public Cec2019_100digit(int func_num) throws Exception {
        
        switch(func_num)
        {
            case 1:
                this.dim = 9;
                break;
            case 2:
                this.dim = 16;
                break;
            case 3:
                this.dim = 18;
                break;
            default:
                this.dim = 10;
        }
        this.func_num = func_num;
        
        //TODO
        System.loadLibrary("CEC2019lib");
        this.cec = new CEC2019c();
        this.cec.init(this.func_num);
        
    }
    
    @Override
    public double fitness(Individual individual) {
        return this.fitness(individual.vector);
    }
    
    @Override
    public double fitness(double[] vector) {
        
        return this.cec.fitness(vector);
    }

    @Override
    public void constrain(Individual individual) {
        switch(this.dim)
        {
            case 1:
                IndividualUtil.randIfOutOfBounds(individual, -8192, 8192);
                break;
            case 2:
                IndividualUtil.randIfOutOfBounds(individual, -16384, 16384);
                break;
            case 3:
                IndividualUtil.randIfOutOfBounds(individual, -4, 4);
                break;
            default:
                IndividualUtil.randIfOutOfBounds(individual, -100, 100);
        }
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] vector = new double[dim];
        Random rnd = new UniformRandom();
        switch(this.dim)
        {
            case 1:
                for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-8192, 8192);
                break;
            case 2:
                for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-16384, 16384);
                break;
            case 3:
                for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-4, 4);
                break;
            default:
                for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-100, 100);
        }
        return vector;
    }

    @Override
    public double fixedAccLevel() {
        return 0;
    }

    @Override
    public double optimum() {
        return 1;
    }

    @Override
    public double max(int dim) {
        switch(this.dim)
        {
            case 1:
                return 8192;
            case 2:
                return 16384;
            case 3:
                return 4;
            default:
                return 100;
        }
    }

    @Override
    public double min(int dim) {
        switch(this.dim)
        {
            case 1:
                return -8192;
            case 2:
                return -16384;
            case 3:
                return -4;
            default:
                return -100;
        }
    }
        
    @Override
    public String name() {
        return "CEC2019-f" + this.func_num;
    }
    
    public static void main(String[] args) throws Exception {
        
        Cec2019_100digit test;
        double res = 0;
        double[] arr;
        
        for(int j = 1; j < 11; j++) {
            
            test = new Cec2019_100digit(j);
            
            for(int i = 0; i < 100000; i++) {
                arr = new double[test.dim];
                for(int a = 0; a < test.dim; a++) {
                    arr[a] = RandomUtil.nextDouble(test.min(a), test.max(a));
                }
                res = test.fitness(arr);
            }

            System.out.println(res);
            
        }
  
    }

}
