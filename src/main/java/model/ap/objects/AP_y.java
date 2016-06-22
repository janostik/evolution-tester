package model.ap.objects;

import model.ap.objects.AP_object;
import java.util.List;

/**
 *
 * @author adam
 */
public class AP_y implements AP_object{
    
    public double x;

    public AP_y() {
    }
    
    public AP_y(double x) {
        this.x = x;
    }
    
    @Override
    public double compute(){
        return x;
    }

    @Override
    public int argCount() {
        return 0;
    }
    
    @Override
    public String toString(){
        return "y";
    }

    @Override
    public double compute(List<Double> array) {
        return array.get(0);
    }
    
    @Override
    public String createEq(List<String> array) {
        return array.get(0);
    }
    
}
