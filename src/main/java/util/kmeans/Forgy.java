package util.kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import util.distance.Distance;
import util.distance.SquaredEuclideanDistance;
import util.random.Random;
import util.random.UniformRandom;

/**
 *
 * Forgy method of initialization
 * 
 * @author wiki on 30/03/2017
 */
public class Forgy implements InitializationMethod {

    @Override
    public List<KMeans.Cluster> initialize(int cluster_count, Map<Integer, double[]> points) {
        
        if(points == null || points.isEmpty() || cluster_count > points.size()) {
            return null;
        }
        
        List<KMeans.Cluster> clusters = new ArrayList<>();
        
        for(int i = 0; i < cluster_count; i++) {
            clusters.add(new KMeans.Cluster(i));
        }
        
        List<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < points.size(); i++) {
            indexes.add(i);
        }
        
        Random rnd = new UniformRandom();
        int rnd_index;
        
        for(int i = 0; i < cluster_count; i++) {
            
            rnd_index = rnd.nextInt(indexes.size());
            clusters.get(i).setCentroid(points.get(indexes.get(rnd_index)));
            
            indexes.remove(rnd_index);
            
        }
        
        double distance, tmp_distance;
        int cluster_id;
        Distance distance_method = new SquaredEuclideanDistance();
        
        for(Entry<Integer, double[]> point : points.entrySet()) {

            distance = Double.MAX_VALUE;
            cluster_id = -1;

            for(int i = 0; i < cluster_count; i++) {
                tmp_distance = distance_method.getDistance(point.getValue(), clusters.get(i).getCentroid());
                if(tmp_distance < distance) {
                    cluster_id = i;
                    distance = tmp_distance;
                }
            }

            clusters.get(cluster_id).addPoint(point);

        }
        
        return clusters;
        
    }

    @Override
    public String toString() {
        return "Forgy";
    }

}
