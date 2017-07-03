package util.kmeans;

import java.util.Comparator;

/**
 *
 * Comparator for clusters.
 * Compares the slopes of linear regression.
 * 
 * @author wiki
 */

public class ClusterComparator implements Comparator<KMeans.Cluster> {

   @Override
   public int compare(KMeans.Cluster t, KMeans.Cluster t1) {

       if(t.getCentroid() == null && t1.getCentroid() == null) {
           return 0;
       }
       else if(t.getCentroid() == null) {
           return -1;
       }
       else if(t1.getCentroid() == null) {
           return 1;
       }
       
       if(t.getCentroid()[0] > t1.getCentroid()[0]) {
           return 1;
       }
       else if(t.getCentroid()[0] < t1.getCentroid()[0]) {
           return -1;
       }
       else {
           return 0;
       }

   }

}
