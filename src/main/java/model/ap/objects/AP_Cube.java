package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Cube implements AP_object{
    
    public double a;

    public AP_Cube() {
    }
    
    public AP_Cube(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        return Math.pow(a, 3);
    }

    @Override
    public int argCount() {
        return 1;
    }
    
    @Override
    public String toString(){
        return "x^3";
    }

    @Override
    public double compute(List<Double> array) {
        return Math.pow(array.get(0), 3);
    }

    @Override
    public String createEq(List<String> array) {
        return "Power[" + array.get(0) + ",3]";
    }
    
}
