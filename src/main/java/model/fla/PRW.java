package model.fla;

import java.util.ArrayList;
import java.util.List;
import model.Individual;
import model.tf.Ackley;
import model.tf.TestFunction;
import util.random.Random;
import util.random.UniformRandom;

/**
 *
 * @author adam on 20/01/2016
 */
public class PRW {

    protected int dimension;
    protected int stepCount;
    protected double stepBoundary;
    protected int[] startZone;
    protected TestFunction ffunction;
    protected double changeProbability;
    
    protected Individual best;
    protected List<Individual> walkIndividuals;

    public PRW(int dimension, int stepCount, double stepBoundary, int[] startZone, TestFunction ffunction) {
        this.dimension = dimension;
        this.stepCount = stepCount;
        this.stepBoundary = stepBoundary;
        this.startZone = startZone;
        this.ffunction = ffunction;
    }
    
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
        
        double[] newPosition;
        /**
         * step iteartion
         */
        for(int i=1; i<this.stepCount; i++) {

            newPosition = new double[this.dimension];
            
            /**
             * dimension iteration
             */
            for(int dim=0; dim<this.dimension; dim++ ){
                
                r = rnd.nextDouble(0, maxStepLength);
                
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

    // <editor-fold defaultstate="collapsed" desc="getters and setters">
    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public double getStepBoundary() {
        return stepBoundary;
    }

    public void setStepBoundary(double stepBoundary) {
        this.stepBoundary = stepBoundary;
    }

    public int[] getStartZone() {
        return startZone;
    }

    public void setStartZone(int[] startZone) {
        this.startZone = startZone;
    }

    public TestFunction getFfunction() {
        return ffunction;
    }

    public void setFfunction(TestFunction ffunction) {
        this.ffunction = ffunction;
    }

    public double getChangeProbability() {
        return changeProbability;
    }

    public void setChangeProbability(double changeProbability) {
        this.changeProbability = changeProbability;
    }

    public Individual getBest() {
        return best;
    }

    public void setBest(Individual best) {
        this.best = best;
    }

    public List<Individual> getWalkIndividuals() {
        return walkIndividuals;
    }

    public void setWalkIndividuals(List<Individual> walkIndividuals) {
        this.walkIndividuals = walkIndividuals;
    }

//    </editor-fold>
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        TestFunction ff = new Ackley();
        int dimension = 2;
        int stepCount = 10000;
        double stepBoundary = 0.1;
        int[] startZone = {1,0};
        
        PRW prw = new PRW(dimension, stepCount, stepBoundary, startZone, ff);
        prw.walk();
        
        for(Individual ind : prw.walkIndividuals){
            System.out.println(ind);
        }
        System.out.println("========================");
        System.out.println(prw.best);
        
    }
    
}
