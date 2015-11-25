package model.chaos;

import java.util.Random;

/**
 *
 * @author adam
 */
public final class Burgers extends Chaos {

    private double A;
    private double B;

    public Burgers(){

        A = 0.75;
        B = 1.75;
        Random rnd = new Random();
        super.xRndStart = -((rnd.nextDouble() * 0.09) + 0.01);
        super.yRndStart = (rnd.nextDouble() * 0.09) + 0.01;
        super.generateChaoticData();
    }

    /**
     * 
     * OVERRIDE THIS METHOD FOR DIFFERENT TYPES OF CHAOS.
     * 
     * @param x
     * @param y
     * @return 
     */
    @Override
    protected Double[] getNextParticle(double x, double y){

        double xn,yn;

        xn = A*x - Math.pow(y,2);
        yn = B*y + x*y;
        
        return new Double[]{xn, yn};
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Burgers dch = new Burgers();
        
        double rnd;
        double sum = 0;
        
        for(int i=0; i< 10; i++){
            rnd = dch.getRndReal();
            sum += rnd;
            System.out.println(rnd);
        }
        
        System.out.println("=====================");
        System.out.println(sum/5000);

        
    }
    
}
