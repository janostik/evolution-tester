package model.chaos;

/**
 *
 * @author adam
 */
public final class Lozi extends Chaos {

    private double A;
    private double B;

    public Lozi(){

        A = 1.7;
        B = 0.5;
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
        
        yn = x;
        xn = 1 - this.A*Math.abs(x) + this.B * y;
        
        return new Double[]{xn, yn};
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Lozi dch = new Lozi();
        
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
