package util;

import java.util.Map;

/**
 * Created by jakub on 27/10/15.
 */
public class MapUtil {

    /**
     * Iterates map and returns key for entry set with highest value.
     * TODO: Rewrite out to Optional. Also for other methods
     *
     * @param map
     * @param <K> Key type
     * @param <V> Value type -> Needs to implement comparable
     * @return Key with highest value
     */
    public static <K, V extends Comparable> K getKeyForMaxValue(Map<K, V> map) {
        if (map.isEmpty()) return null;
        return map.entrySet().stream()
                .max((o1, o2) -> o1.getValue().compareTo(o2.getValue()))
                .get().getKey();
    }

    public static <K, V extends Comparable> V getHighestValue(Map<K, V> map) {
        if (map.isEmpty()) return null;
        return map.values().stream()
                .max((o1, o2) -> o1.compareTo(o2))
                .get();
    }
    
    /**
     * Iterates map and returns key for entry set with lowest value.
     * TODO: Rewrite out to Optional. Also for other methods
     *
     * @param map
     * @param <K> Key type
     * @param <V> Value type -> Needs to implement comparable
     * @return Key with highest value
     */
    public static <K, V extends Comparable> K getKeyForMinValue(Map<K, V> map) {
        if (map.isEmpty()) return null;
        return map.entrySet().stream()
                .min((o1, o2) -> o1.getValue().compareTo(o2.getValue()))
                .get().getKey();
    }

    public static <K, V extends Comparable> V getLowestValue(Map<K, V> map) {
        if (map.isEmpty()) return null;
        return map.values().stream()
                .min((o1, o2) -> o1.compareTo(o2))
                .get();
    }
}
