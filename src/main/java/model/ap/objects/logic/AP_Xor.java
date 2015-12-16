package model.ap.objects.logic;

import model.ap.objects.*;
import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Xor implements AP_object{
    
    public double a;
    public double b;

    public AP_Xor() {
    }
    
    public AP_Xor(double a, double b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public double compute(){
        
        return (a+b)%2;
    }

    @Override
    public int argCount() {
        return 2;
    }
    
    @Override
    public String toString(){
        return "BitXor";
    }

    @Override
    public double compute(List<Double> array) {
        
        return (array.get(0)+array.get(1)) % 2;
    }
    
    @Override
    public String createEq(List<String> array) {

        return "BitXor[" + array.get(1) + "," + array.get(0) + "]";
    }
    
}
