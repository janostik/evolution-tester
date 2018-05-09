package model;

import java.util.Arrays;
import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * Created by jakub on 27/10/15.
 */
public class Individual implements Clusterable {

    public double[] vector;
    public double fitness;
    public String id;
    
    /**
     * Extended individual for History Based Random Selection - hbrs
     * - added int score - no. of successful mutations
     * 
     * 12/07/2016
     */
    public double score;
    
    /**
     * Extended individual for History Based Position Selection - hbps
     * - added int score for each position in mutation rand/1/bin
     * 
     * 12/07/2016
     */
    public double[] score_pos;

    public Individual() {
        this.score = 1;
        this.score_pos = new double[]{1,1,1};
    }

    public Individual(String id, double[] vector, double fitness) {
        this.id = id;
        this.vector = vector;
        this.fitness = fitness;
        this.score = 1;
        this.score_pos = new double[]{1,1,1};
    }
    
    public Individual(String id, double[] vector, double fitness, double score) {
        this.id = id;
        this.vector = vector;
        this.fitness = fitness;
        this.score = score;
        this.score_pos = new double[]{1,1,1};
    }
    
    public Individual(String id, double[] vector, double fitness, double score, double[] score_pos) {
        this.id = id;
        this.vector = vector;
        this.fitness = fitness;
        this.score = score;
        this.score_pos = score_pos;
    }

    public Individual(Individual individual) {
        this.id = individual.id;
        this.vector = individual.vector.clone();
        this.fitness = individual.fitness;
        this.score = individual.score;
        this.score_pos = individual.score_pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Individual that = (Individual) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Individual{" +
                "id=" + id +
                ", vector=" + Arrays.toString(vector) +
                ", fitness=" + fitness +
                ", score=" + score +
                '}';
    }

    @Override
    public double[] getPoint() {
        return this.vector;
    }
}
