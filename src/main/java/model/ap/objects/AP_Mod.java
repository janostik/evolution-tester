package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Mod implements AP_object{
    
    public double a;
    public double b;

    public AP_Mod() {
    }
    
    public AP_Mod(double a, double b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public double compute(){
        if(b == 0){
            return Double.MAX_VALUE;
        }
        return a % b;
    }

    @Override
    public int argCount() {
        return 2;
    }
    
    @Override
    public String toString(){
        return "Mod";
    }

    @Override
    public double compute(List<Double> array) {
        if(array.get(0) == 0){
            return Double.MAX_VALUE;
        }
        return array.get(1) % array.get(0);
    }
    
}
