package util;

import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author wiki
 */
public class Dijsktra {

    public static class Vertex implements Comparable<Vertex>
    {
        public final int id;
        public Edge[] adjacencies;
        public double minDistance = Double.POSITIVE_INFINITY;
        public Vertex previous;

        public Vertex(int argId) { id = argId; }

        @Override
        public String toString() { return String.valueOf(id); }

        @Override
        public int compareTo(Vertex other)
        {
            return Double.compare(minDistance, other.minDistance);
        }

    }


    public static class Edge
    {
        public final Vertex target;
        public final double weight;
        public Edge(Vertex argTarget, double argWeight)
        { target = argTarget; weight = argWeight; }
    }
    
    public static void computePaths(Vertex source)
    {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();

                // Visit each edge exiting u
                for (Edge e : u.adjacencies)
                {
                    Vertex v = e.target;
                    double weight = e.weight;
                    double distanceThroughU = u.minDistance + weight;
            if (distanceThroughU < v.minDistance) {
                vertexQueue.remove(u);

                v.minDistance = distanceThroughU ;
                v.previous = u;
                vertexQueue.add(v);
            }
                }
            }
    }

    public static List<Vertex> getShortestPathTo(Vertex target)
    {
        List<Vertex> path = new ArrayList<>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);

        Collections.reverse(path);
        return path;
    }
    
    public static Vertex[] getShortestPathsFromAdjacencyMatrix(double[][] adjacency_matrix, int start_position){
        
        Vertex[] vertices = new Vertex[adjacency_matrix.length];
        
        //Create vertices
        for(int i = 0; i < adjacency_matrix.length; i++) {
            
            vertices[i] = new Vertex(i);
            
        }
        
        List<Edge> adjacencies;
        Edge[] adjacenciesArr;
        
        //Initialize paths
        for(int i = 0; i < adjacency_matrix.length; i++) {
            
            adjacencies = new ArrayList<>();
            
            for(int j = 0; j < adjacency_matrix[i].length; j++) {
                
                if(adjacency_matrix[i][j] != 0) {
                    adjacencies.add(new Edge(vertices[j],adjacency_matrix[i][j]));
                }
                
            }
            adjacenciesArr = new Edge[adjacencies.size()];
            vertices[i].adjacencies = adjacencies.toArray(adjacenciesArr);
            
        }
        
        computePaths(vertices[start_position]);
        
//        System.out.println("Distance to " + vertices[10] + ": " + vertices[10].minDistance);
//        List<Vertex> path = getShortestPathTo(vertices[10]);
//        System.out.println("Path: " + path);
        
        return vertices;
    }

    public static void main(String[] args)
    {
        
        double[][] adj = new double[][] {{0,0,0,0,0,0,8,0,0,0,0},{0,0,11,0,0,0,0,0,0,0,0},{0,11,0,0,0,0,0,0,0,0,0},{0,0,0,0,23,0,0,0,0,0,0},{0,0,0,0,0,0,0,40,0,0,0},
        {0,0,0,0,25,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,8,0},{0,0,0,0,40,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,18},{0,0,0,0,0,0,0,0,15,0,0},{0,0,0,0,0,0,0,0,18,0,0}};
        
        getShortestPathsFromAdjacencyMatrix(adj, 0);
        
//        Dijsktra dj = new Dijsktra();
//
//        // mark all the vertices 
//        Vertex A = new Vertex(1);
//        Vertex B = new Vertex(2);
//        Vertex D = new Vertex(3);
//        Vertex F = new Vertex(4);
//        Vertex K = new Vertex(5);
//        Vertex J = new Vertex(6);
//        Vertex M = new Vertex(7);
//        Vertex O = new Vertex(8);
//        Vertex P = new Vertex(9);
//        Vertex R = new Vertex(10);
//        Vertex Z = new Vertex(11);
//
//        // set the edges and weight
//        A.adjacencies = new Edge[]{ new Edge(M, 8) };
//        B.adjacencies = new Edge[]{ new Edge(D, 11) };
//        D.adjacencies = new Edge[]{ new Edge(B, 11) };
//        F.adjacencies = new Edge[]{ new Edge(K, 23) };
//        K.adjacencies = new Edge[]{ new Edge(O, 40) };
//        J.adjacencies = new Edge[]{ new Edge(K, 25) };
//        M.adjacencies = new Edge[]{ new Edge(R, 8) };
//        O.adjacencies = new Edge[]{ new Edge(K, 40) };
//        P.adjacencies = new Edge[]{ new Edge(Z, 18) };
//        R.adjacencies = new Edge[]{ new Edge(P, 15) };
//        Z.adjacencies = new Edge[]{ new Edge(P, 18) };
//
//
//        computePaths(Z); // run Dijkstra
//        System.out.println("Distance to " + A + ": " + A.minDistance);
//        List<Vertex> path = getShortestPathTo(A);
//        System.out.println("Path: " + path);
    }
    
}
