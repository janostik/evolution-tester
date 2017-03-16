package model.net;

import model.Individual;

/**
 * Created by jakub on 27/10/15.
 */
public abstract class Edge {

    protected final Individual source;
    protected final Individual target;
    protected final double weight;
    public int iter;

    public Edge(Individual source, Individual target) {
        this.source = source;
        this.target = target;
        this.weight = 1;
    }

    public Edge(Individual source, Individual target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }
    
    @Override
    public String toString() {
        return "Edge{" +
                "source=" + source +
                ", target=" + target +
                '}';
    }

    public Individual getSource() {
        return source;
    }

    public Individual getTarget() {
        return target;
    }

    public double getWeight() {
        return weight;
    }

}
