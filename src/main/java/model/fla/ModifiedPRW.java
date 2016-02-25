package model.fla;

import java.util.ArrayList;
import model.Individual;
import model.tf.Ackley;
import model.tf.TestFunction;
import util.random.DissipativeRandom;
import util.random.Random;
import util.random.UniformRandom;

/**
 *
 * @author adam on 20/01/2016
 */
public class ModifiedPRW extends PRW {

    protected Random chaos;
    protected double changeProbability;
    
    public ModifiedPRW(int dimension, int stepCount, double stepBoundary, int[] startZone, TestFunction ffunction, double changeProbability) {
        super(dimension, stepCount, stepBoundary, startZone, ffunction);
        this.chaos = new DissipativeRandom();
        this.changeProbability = changeProbability;
    }
    
    @Override
    public void walk(){
        
        double[] position = new double[this.dimension];
        Random rnd = new UniformRandom();
        double r;
        double boundary = (this.ffunction.max(dimension) - this.ffunction.min(dimension));
        double halfInterval = boundary/2;
        Individual curInd;
        
        /**
         * First point
         */
        for(int i=0; i<this.dimension; i++){
            r = rnd.nextDouble(0, halfInterval);
            if(this.startZone[i] == 1){
                position[i] = this.ffunction.max(dimension) - r;
            } else {
                position[i] = this.ffunction.min(dimension) + r;
            }
        }
        
        int rD = rnd.nextInt(this.dimension);
        
        /**
         * Set one feature to the edge of dimension
         */
        if(this.startZone[rD] == 1) {
            position[rD] = this.ffunction.max(dimension);
        } else {
            position[rD] = this.ffunction.min(dimension);
        }
        
        this.walkIndividuals = new ArrayList<>();
        curInd = new Individual("0", position, this.ffunction.fitness(position));
        this.walkIndividuals.add(curInd);
        this.best = curInd;
        
        double maxStepLength = boundary * this.stepBoundary;
        int changedDimension;
        
        double[] newPosition;
        changedDimension = -1;
        /**
         * step iteartion
         */
        for(int i=1; i<this.stepCount; i++) {

            newPosition = new double[this.dimension];
            
            /**
             * dimension iteration
             */
            for(int dim=0; dim<this.dimension; dim++ ){
                
                r = this.chaos.nextDouble(0, maxStepLength);
                
                /**
                 * Direction change (switch direction) modification
                 */
                if(rnd.nextDouble() <= this.changeProbability && dim != changedDimension){
                    this.startZone[dim] = (this.startZone[dim]+1) % 2;
                    changedDimension = dim;
                }
                
                if(this.startZone[dim] == 1){
                    r = -r;
                }
                newPosition[dim] = position[dim] + r;
                if(newPosition[dim] > this.ffunction.max(dimension)){
                    newPosition[dim] = this.ffunction.max(dimension) - (newPosition[dim] - this.ffunction.max(dimension));
                    this.startZone[dim] = (this.startZone[dim]+1) % 2;
                }
                if(newPosition[dim] < this.ffunction.min(dimension)){
                    newPosition[dim] = this.ffunction.min(dimension) - (newPosition[dim] - this.ffunction.min(dimension));
                    this.startZone[dim] = (this.startZone[dim]+1) % 2;
                }
                
            }
            
            curInd = new Individual("0", newPosition, this.ffunction.fitness(newPosition));
            this.walkIndividuals.add(curInd);
            if(curInd.fitness <= this.best.fitness){
                this.best = curInd;
            }
            position = newPosition.clone();
            
        }
        
    }
    
    public Random getChaos() {
        return chaos;
    }

    public void setChaos(Random chaos) {
        this.chaos = chaos;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        TestFunction ff = new Ackley();
        int dimension = 2;
        int stepCount = 10000;
        double stepBoundary = 0.1;
        int[] startZone = {1,0};
        double changeProbability = 0.05;
        
        ModifiedPRW prw = new ModifiedPRW(dimension, stepCount, stepBoundary, startZone, ff, changeProbability);
        prw.walk();
        
        for(Individual ind : prw.getWalkIndividuals()){
            System.out.println(ind);
        }
        System.out.println("========================");
        System.out.println(prw.getBest());
        
    }

    
    
}
