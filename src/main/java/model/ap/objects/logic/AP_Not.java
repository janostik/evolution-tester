package model.ap.objects.logic;

import model.ap.objects.*;
import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Not implements AP_object{
    
    public double a;

    public AP_Not() {
    }
    
    public AP_Not(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        return (a+1) % 2;
    }

    @Override
    public int argCount() {
        return 1;
    }
    
    @Override
    public String toString(){
        return "Not";
    }

    @Override
    public double compute(List<Double> array) {
        return (array.get(0)+1) % 2;
    }
    
    @Override
    public String createEq(List<String> array) {
        return "Mod[" + array.get(0) + "+1, 2]";
    }
    
}
