package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Tan implements AP_object{
    
    public double a;

    public AP_Tan() {
    }
    
    public AP_Tan(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        return Math.tan(a);
    }

    @Override
    public int argCount() {
        return 1;
    }
    
    @Override
    public String toString(){
        return "Tan";
    }

    @Override
    public double compute(List<Double> array) {
        return Math.tan(array.get(0));
    }
    
}
