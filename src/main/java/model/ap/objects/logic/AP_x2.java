package model.ap.objects.logic;

import model.ap.objects.AP_object;
import java.util.List;

/**
 *
 * @author adam
 */
public class AP_x2 implements AP_object{
    
    public double x;
    private int div = 2;

    public AP_x2() {
    }
    
    public AP_x2(double x) {
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
        return "x[["+ div +"]]";
    }

    @Override
    public double compute(List<Double> array) {
        return ((array.get(0).intValue() & (1 << (4-div))) > 0) ? 1 : 0;
    }
    
    @Override
    public String createEq(List<String> array) {
        return "x[["+ div +"]]";
    }
    
}
