package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Ceiling implements AP_object{
    
    public double a;

    public AP_Ceiling() {
    }
    
    public AP_Ceiling(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        return Math.ceil(a);
    }

    @Override
    public int argCount() {
        return 1;
    }
    
    @Override
    public String toString(){
        return "Ceiling";
    }

    @Override
    public double compute(List<Double> array) {
        return Math.ceil(array.get(0));
    }
    
    @Override
    public String createEq(List<String> array) {
        return "Ceiling[" + array.get(0) + "]";
    }
    
}
