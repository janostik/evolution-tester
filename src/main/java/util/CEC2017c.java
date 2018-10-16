package util;

/**
 *
 * @author wiki on 09/11/2018
 */
public class CEC2017c {

    public native void init(int dim, int func);
    public native double fitness(double[] vector);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.loadLibrary("CEC2017lib");
        
        CEC2017c test = new CEC2017c();
        
        test.init(10, 4);
        
        double res;
        
        res = test.fitness(new double[]{100,0,0,0,0,0,0,0,0,0});
        
        System.out.println(res);
        
//        res = test.fitness(10, 4, new double[]{0,0,0,0,0,0,0,0,0,0});
        
//        System.out.println(res);
        
    }
    
}
