package model.net;

import model.Individual;

/**
 * Created by jakub on 27/10/15.
 */
public class UnidirectionalEdge extends Edge {


    public UnidirectionalEdge(Individual source, Individual target) {
        super(source, target);
    }

    public UnidirectionalEdge(Individual source, Individual target, double weight) {
        super(source, target, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (!source.equals(edge.source)) return false;
        return target.equals(edge.target);
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + target.hashCode();
        return result;
    }
}
