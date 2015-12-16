package model.ap.objects.logic;

import model.ap.objects.*;
import java.util.List;

/**
 *
 * @author adam
 */
public class AP_And implements AP_object{
    
    public double a;
    public double b;

    public AP_And() {
    }
    
    public AP_And(double a, double b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public double compute(){
        return a*b;
    }

    @Override
    public int argCount() {
        return 2;
    }
    
    @Override
    public String toString(){
        return "BitAnd";
    }

    @Override
    public double compute(List<Double> array) {
        return array.get(1)*array.get(0);
    }
    
    @Override
    public String createEq(List<String> array) {

        return "BitAnd[" + array.get(1) + "," + array.get(0) + "]";
    }
    
}
