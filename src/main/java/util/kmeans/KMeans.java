package util.kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import util.distance.Distance;

/**
 *
 * K means clustering algorithm
 * 
 * @author wiki on 30/03/2017
 */
public class KMeans {

    /**
     * Cluster class for individual clusters
     * with centroid and dimension
     */
    public static class Cluster {
        
        private final int id;
        private double[] centroid;
        public Map<Integer, double[]> points;
        private int dimension;

        public Cluster(int id) {
            this.id = id;
            this.points = new HashMap<>();
        }
        
        public Cluster(int id, Map<Integer, double[]> points) {
            this.id = id;
            addPoints(points);
            countCentroid();
        }

        public void addPoint(Entry<Integer, double[]> point) {
            
            if(points == null){
                return;
            }
            this.dimension = point.getValue().length;
            this.points.put(point.getKey(), point.getValue());
            
        }
        
        public void addPoints(Map<Integer, double[]> points) {
            
            if(points == null || points.isEmpty()){
                this.dimension = 0;
                return;
            }
            
            for(Entry<Integer, double[]> point : points.entrySet()) {
                this.dimension = point.getValue().length;
                break;
            }
            
            
            this.points = points;
            
        }
        
        public void countCentroid() {
         
            if(this.points == null || this.points.isEmpty()) {
                this.centroid = null;
                return;
            }
            
            double[] centroid = new double[dimension];
            for(int i = 0; i < dimension; i++) {
                centroid[i] = 0;
            }
            
            for(double[] point : this.points.values()){
                for(int i = 0; i < dimension; i++) {
                    centroid[i] += point[i];
                }
            }
            
            for(int i = 0; i < dimension; i++) {
                centroid[i] = centroid[i] / this.points.size();
            }
            
            this.centroid = centroid;
            
        }
        
        public double[] getCentroid(){
        
            return centroid;
            
        }

        public void setCentroid(double[] centroid) {
            this.centroid = centroid;
        }
        
        public double getMeanCentroid() {
            return new Mean().evaluate(this.centroid);
        }
        
        public int getSize(){
            if(this.points == null) {
                return 0;
            }
            return this.points.size();
        }

        @Override
        public String toString() {
            return "Cluster{" + "id=" + id + ", centroid=" + Arrays.toString(centroid) + ", size=" + points.size() + ", dimension=" + dimension + '}';
        }

        @Override
        public int hashCode() {
            int hash = 5;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Cluster other = (Cluster) obj;
            if(this.points.size() != other.points.size()) {
                return false;
            }
            return points.entrySet().stream().noneMatch((point) -> (!other.points.containsKey(point.getKey())));
        }
        
    }
    
    public static class ClusterComparator implements Comparator<Cluster> {
    
    @Override
    public int compare(Cluster t, Cluster t1) {

        if(t.getCentroid() == null || t.getCentroid().length == 0) {
            return -1;
        }
        else if(t1.getCentroid() == null || t1.getCentroid().length == 0) {
            return 1;
        }
        else if(t.getMeanCentroid() < t1.getMeanCentroid()) {
            return -1;
        }
        else if(t.getMeanCentroid() == t1.getMeanCentroid()){
            return 0;
        }
        else {
            return 1;
        }

    }
    
}
    
    private final int cluster_count;
    private final Map<Integer, double[]> data;
    public List<Cluster> clusters;
    private final InitializationMethod initialization_method;
    private final Distance distance_method;

    public KMeans(int cluster_count, Map<Integer, double[]> data, InitializationMethod initialization_method, Distance distance_method) {
        this.cluster_count = cluster_count;
        this.data = data;
        this.initialization_method = initialization_method;
        this.distance_method = distance_method;
    }
    
    /**
     * 
     * Main method with implementation of K-means
     * 
     * @return 
     */
    public List<Cluster> run() {
        
        /**
         * Initialization
         */
        this.clusters = initialization_method.initialize(cluster_count, data);
        
        int cluster_id;
        double distance, tmp_distance;
        List<Cluster> newClusters;
        boolean break_bool;
        
        while(true) {
        
            break_bool = true;

            /**
             * switch in clusters
             */
            newClusters = new ArrayList<>();
            for(int i = 0; i < cluster_count; i++) {
                newClusters.add(new Cluster(i));
            }
            
            /**
             * Cycle through data
             */
            for(Entry<Integer, double[]> point : this.data.entrySet()) {

                distance = Double.MAX_VALUE;
                cluster_id = -1;

                for(int i = 0; i < cluster_count; i++) {
                    tmp_distance = distance_method.getDistance(point.getValue(), clusters.get(i).centroid);
                    if(tmp_distance < distance) {
                        cluster_id = i;
                        distance = tmp_distance;
                    }
                }

                newClusters.get(cluster_id).addPoint(point);

            }

            for(int i = 0; i < cluster_count; i++) {
                newClusters.get(i).countCentroid();
                if(!this.clusters.get(i).equals(newClusters.get(i))) {
                    break_bool = false;
                }
            }
            
            if(break_bool) {
                break;
            }
            
            this.clusters = newClusters;
        
        }
        
        return this.clusters;
        
    }

    @Override
    public String toString() {
        
        String return_string = "K-means\n======\nCluster count = " + cluster_count + "\nInit method = " + initialization_method.toString() + "\nData =\n======";
        for(Entry<Integer, double[]> point : data.entrySet()) {
            return_string += "\n" + point.getKey() + " - " + Arrays.toString(point.getValue());
        }
        return_string += "\n======\nClusters = \n======";
        for(int i = 0; i < clusters.size(); i++) {
            return_string += "\n" + clusters.get(i).toString();
        }
        
        return return_string;
    }
    
    
    
}
