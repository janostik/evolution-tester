package model.net;

import model.Individual;

/**
 * Created by jakub on 27/10/15.
 */
public class BidirectionalEdge extends Edge {

    public BidirectionalEdge(Individual source, Individual target) {
        super(source, target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return (source.equals(edge.source) && target.equals(edge.target)) || (target.equals(edge.source) && source.equals(edge.target));
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = result + target.hashCode();
        return result;
    }
}
