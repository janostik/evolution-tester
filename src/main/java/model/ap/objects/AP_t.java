package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_t implements AP_object{
    
    public double t;

    public AP_t() {
    }
    
    public AP_t(double t) {
        this.t = t;
    }
    
    @Override
    public double compute(){
        return t;
    }

    @Override
    public int argCount() {
        return 0;
    }
    
    @Override
    public String toString(){
        return "t";
    }

    @Override
    public double compute(List<Double> array) {
        return array.get(0);
    }
    
}
