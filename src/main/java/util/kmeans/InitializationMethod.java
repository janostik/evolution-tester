package util.kmeans;

import java.util.List;
import java.util.Map;

/**
 *
 * Interface for clustering initialization methods.
 * 
 * @author wiki on 30/03/2017
 */
public interface InitializationMethod {
    
    public List<KMeans.Cluster> initialize(int cluster_count, Map<Integer, double[]> points);
    @Override
    public String toString();
    
}
