package model.chaos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author adam
 */
public final class Lozi {

    private List<Double[]> chaoticData;
    private double maxVal;
    private int index;
    private int reevaluation;
    private double xRndStart;
    private double yRndStart;
    private int maxRun;
    private double A;
    private double B;

    public Lozi(){
        
        Random rnd = new Random();
//        double rndStart = rnd.nextDouble() * 0.1;
        double rndStart = 0.1;
        this.xRndStart = rndStart;
        this.yRndStart = rndStart;
        this.reevaluation = 0;
        this.maxRun = 500_000;
        this.A = 1.7;
        this.B = 0.5;
        this.generateChaoticData();
    }
    
    public void generateChaoticData(){
        
        Double[] start = new Double[]{this.xRndStart, this.yRndStart};
        List<Double[]> chaoticList = new ArrayList<>();
        chaoticList.add(start);
        
        this.reevaluation++;
        this.index = 1;
        
        Double[] particle;
        double x, y, xn, yn;
        
        for(int i=0; i<maxRun-1; i++){
            
            x = chaoticList.get(i)[0];
            y = chaoticList.get(i)[1];
            
            yn = x;
            xn = 1 - this.A*Math.abs(x) + this.B * y;
            
            particle = new Double[]{xn, yn};
            chaoticList.add(particle);
        }
        
        this.chaoticData = chaoticList;
        this.maxVal = this.findMaxVal();

    }
    
    /**
     * 
     * @return 
     */
    public double getRndReal(){
        
        if(this.index == this.maxRun){
            this.xRndStart = this.chaoticData.get(this.index - 1)[0];
            this.yRndStart = this.chaoticData.get(this.index - 1)[1];
            this.generateChaoticData();
        }
        
        double x = this.chaoticData.get(this.index)[0];
        this.index++;
        
        return Math.abs(x)/this.maxVal;
        
    }
    
    /**
     * 
     * @param bound
     * @return 
     */
    public int getRndInt(int bound){
        
        if(this.index == this.maxRun){
            this.xRndStart = this.chaoticData.get(this.index - 1)[0];
            this.yRndStart = this.chaoticData.get(this.index - 1)[1];
            this.generateChaoticData();
        }
        
        double x = this.chaoticData.get(this.index)[0];
        this.index++;
        
        return (int) Math.round((Math.abs(x)/this.maxVal)*(bound-1));
        
    }

    /**
     * 
     * @return 
     */
    private double findMaxVal(){
        
        double xmaxVal = Double.MIN_VALUE;
        
        for(Double[] particle : this.chaoticData){
            if(Math.abs(particle[0]) > xmaxVal){
                xmaxVal = Math.abs(particle[0]);
            }
        }
        
        return xmaxVal;
        
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
