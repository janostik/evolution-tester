package model.net;

import model.Individual;
import util.MapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jakub on 27/10/15.
 */
public class Net {

    List<Edge> edges = new ArrayList<>();
    Map<Individual, Integer> degreeMap = new HashMap<>();

    public void addEdge(Edge edge) {
        edges.add(edge);
        degreeMap.put(edge.target, degreeMap.getOrDefault(edge.target, 0) + 1);
    }

    public int getHighestDegree() {
        Integer val = MapUtil.getHighestValue(degreeMap);
        return val == null ? 0 : val;
    }

    public Individual getNodeWithHighestDegree() {
        return MapUtil.getKeyForMaxValue(degreeMap);
    }

    public void removeEdgesForNode(Individual node) {
        if (node == null) return;
        degreeMap.remove(node);
        edges.removeAll(edges.stream().filter(edge -> edge.source.equals(node)).collect(Collectors.toList()));
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Map<Individual, Integer> getDegreeMap() {
        return degreeMap;
    }

}
