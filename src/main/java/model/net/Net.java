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
    Map<Individual, Double> degreeMap = new HashMap<>();

    public Net() {
    }

    public Net(Net net) {
        this.edges.addAll(net.getEdges());
        this.degreeMap.putAll(net.getDegreeMap());
    }

    public void addEdge(Edge edge) {
        double v, vv;
        v = degreeMap.getOrDefault(edge.target, 0.0) + edge.getWeight();
        vv = degreeMap.getOrDefault(edge.source, 0.0) + edge.getWeight();
        Edge newEdge;
        if(edge.getClass().equals(UnidirectionalEdge.class)){
            newEdge = new UnidirectionalEdge(edge.source, edge.target, v);
        }
        else {
            newEdge = new BidirectionalEdge(edge.source, edge.target, v);
        }
        newEdge.iter = edge.iter;

        edges.add(newEdge);
        if(edge.getClass().equals(UnidirectionalEdge.class)){
//            degreeMap.put(edge.source, degreeMap.getOrDefault(edge.source, 0) + 1);
            degreeMap.remove(edge.target);
            degreeMap.put(edge.target, v);
        }
        else {
//            degreeMap.put(edge.source, degreeMap.getOrDefault(edge.source, 0) + 1);
//            degreeMap.put(edge.target, degreeMap.getOrDefault(edge.target, 0) + 1);
            degreeMap.remove(edge.target);
            degreeMap.put(edge.target, v);
            degreeMap.remove(edge.source);
            degreeMap.put(edge.source, vv);
        }
    }

    public double getHighestDegree() {
        Double val = MapUtil.getHighestValue(degreeMap);
        return val == null ? 0 : val;
    }

    public Individual getNodeWithHighestDegree() {
        return MapUtil.getKeyForMaxValue(degreeMap);
    }
    
    public double getLowestDegree() {
        Double val = MapUtil.getLowestValue(degreeMap);
        return val == null ? 0 : val;
    }
    
    public Individual getNodeWithLowestDegree() {
        return MapUtil.getKeyForMinValue(degreeMap);
    }

    public void removeEdgesForNode(Individual node) {
        if (node == null) return;
        degreeMap.remove(node);
        
        edges.removeAll(edges.stream().filter(edge -> edge.source.equals(node)).collect(Collectors.toList()));
    }
    
    public void removeBidirectionalEdgesForNode(Individual node) {
        if (node == null) return;
        degreeMap.remove(node);
        
        edges.removeAll(edges.stream().filter(edge -> edge.source.equals(node)).collect(Collectors.toList()));
        edges.removeAll(edges.stream().filter(edge -> edge.target.equals(node)).collect(Collectors.toList()));
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Map<Individual, Double> getDegreeMap() {
        return degreeMap;
    }

}
