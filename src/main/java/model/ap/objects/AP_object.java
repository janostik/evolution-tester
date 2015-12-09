package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public interface AP_object {
    
    public int argCount();
    public double compute();
    public double compute(List<Double> array);
    
    @Override
    public String toString();
    
}
