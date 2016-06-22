package model.ap.objects.logic;

import model.ap.objects.AP_object;
import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Or implements AP_object{
    
    public double a;
    public double b;

    public AP_Or() {
    }
    
    public AP_Or(double a, double b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public double compute(){
        if(a == 1 || b == 1){
            return 1;
        }
        
        return 0;
    }

    @Override
    public int argCount() {
        return 2;
    }
    
    @Override
    public String toString(){
        return "BitOr";
    }

    @Override
    public double compute(List<Double> array) {
        if(array.get(0) == 1 || array.get(1) == 1){
            return 1;
        }
        
        return 0;
    }
    
    @Override
    public String createEq(List<String> array) {

        return "BitOr[" + array.get(1) + "," + array.get(0) + "]";
    }
    
}
