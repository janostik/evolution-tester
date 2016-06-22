package model.ap.objects.logic;

import model.ap.objects.AP_object;
import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Nor implements AP_object{
    
    public double a;
    public double b;

    public AP_Nor() {
    }
    
    public AP_Nor(double a, double b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public double compute(){
        if(a == 1 || b == 1){
            return 0;
        }
        
        return 1;
    }

    @Override
    public int argCount() {
        return 2;
    }
    
    @Override
    public String toString(){
        return "BitNor";
    }

    @Override
    public double compute(List<Double> array) {
        if(array.get(0) == 1 || array.get(1) == 1){
            return 0;
        }
        
        return 1;
    }
    
    @Override
    public String createEq(List<String> array) {

        return "Mod[BitOr[" + array.get(1) + "," + array.get(0) + "]+1, 2]";
    }
    
}
