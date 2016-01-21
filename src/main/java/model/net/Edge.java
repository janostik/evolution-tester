package model.net;

import model.Individual;

/**
 * Created by jakub on 27/10/15.
 */
public abstract class Edge {

    protected final Individual source;
    protected final Individual target;

    public Edge(Individual source, Individual target) {
        this.source = source;
        this.target = target;
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

}
