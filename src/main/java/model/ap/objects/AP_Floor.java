package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Floor implements AP_object{
    
    public double a;

    public AP_Floor() {
    }
    
    public AP_Floor(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        return Math.floor(a);
    }

    @Override
    public int argCount() {
        return 1;
    }
    
    @Override
    public String toString(){
        return "Floor";
    }

    @Override
    public double compute(List<Double> array) {
        return Math.floor(array.get(0));
    }
    
    @Override
    public String createEq(List<String> array) {
        return "Floor[" + array.get(0) + "]";
    }
    
}
