package util.kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import util.random.Random;
import util.random.UniformRandom;

/**
 *
 * Random Initialization of clusters
 * Random cluster is assigned to each point
 * 
 * @author wiki on 30/03/2017
 */
public class RandomPartition implements InitializationMethod {

    @Override
    public List<KMeans.Cluster> initialize(int cluster_count, Map<Integer, double[]> points) {
        
        if(points == null || points.isEmpty() || cluster_count > points.size()) {
            return null;
        }
        
        List<KMeans.Cluster> clusters = new ArrayList<>();
        
        for(int i = 0; i < cluster_count; i++) {
            clusters.add(new KMeans.Cluster(i));
        }
        
        int cluster_id;
        Random rnd = new UniformRandom();
        boolean break_bool;
        
        while(true) {
            
            break_bool = true;
            
            for(Entry<Integer, double[]> point : points.entrySet()) {
                cluster_id = rnd.nextInt(cluster_count);
                clusters.get(cluster_id).addPoint(point);
            }
            
            for(int i = 0; i < cluster_count; i++) {
                if(clusters.get(i).getSize() > 0) {
                    clusters.get(i).countCentroid();
                }
                else {
                    break_bool = false;
                }
            }
            
            if(break_bool) {
                break;
            }
            else {
                clusters = new ArrayList<>();
               for(int i = 0; i < cluster_count; i++) {
                    clusters.add(new KMeans.Cluster(i));
                } 
            }
            
        }
        
        return clusters;
        
    }

    @Override
    public String toString() {
        return "Random partition";
    }

}
