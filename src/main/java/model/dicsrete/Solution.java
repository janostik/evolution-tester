package model.dicsrete;

/**
 *
 * Solution of the discrete optimization problem
 * 
 * @author wikki on 03/05/2019
 */
public class Solution {
    
    public double fitness;
    public int[] vector;
    public String id;
    public int FES;
    
    public Solution() { 
    }

    public Solution(int[] vector, double fitness) {
        this.fitness = fitness;
        this.vector = vector;
    }

    public Solution(String id, int[] vector, double fitness) {
        this.fitness = fitness;
        this.vector = vector;
        this.id = id;
    }
 
}
