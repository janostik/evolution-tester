package util;

/**
 *
 * @author wiki on 15/03/2019
 */
public class CEC2019c {

    public native void init(int func);
    public native double fitness(double[] vector);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //TODO
        System.loadLibrary("CEC2019lib");
        
        CEC2019c test = new CEC2019c();
        
        test.init(1);
        
        double res = 0;
        
        for(int i = 0; i < 1000000; i++) {
            res = test.fitness(new double[]{100,0,0,0,0,0,0,0,0});
        }
        
        System.out.println(res);
        
//        res = test.fitness(10, 4, new double[]{0,0,0,0,0,0,0,0,0,0});
        
//        System.out.println(res);
        
    }
    
}
