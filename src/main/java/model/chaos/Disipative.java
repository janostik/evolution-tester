package model.chaos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author adam
 */
public final class Disipative {

    private List<Double[]> chaoticData;
    private double maxVal;
    private int index;
    private int reevaluation;
    private double xRndStart;
    private double yRndStart;
    private int maxRun;
    private double B;
    private double k;

    public Disipative(){
        
        Random rnd = new Random();
        double rndStart = rnd.nextDouble() * 0.1;
        this.xRndStart = rndStart;
        this.yRndStart = rndStart;
        this.reevaluation = 0;
        this.maxRun = 500_000;
        this.B = 0.6;
        this.k = 8.8;
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
            
            yn = (this.B * y + this.k * Math.sin(x)) % (2*Math.PI);
            if(yn < 0){
                yn += (2*Math.PI);
            }
            xn = (x + yn) % (2*Math.PI);
            if(xn < 0){
                xn += (2*Math.PI);
            }
            
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
            this.xRndStart = this.chaoticData.get(this.index)[0];
            this.yRndStart = this.chaoticData.get(this.index)[1];
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
            this.xRndStart = this.chaoticData.get(this.index)[0];
            this.yRndStart = this.chaoticData.get(this.index)[1];
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
        
        Disipative dch = new Disipative();
        
        double rnd;
        double sum = 0;
        
        for(int i=0; i< 5000; i++){
            rnd = dch.getRndReal();
            sum += rnd;
//            System.out.println(rnd);
        }
        
        System.out.println("=====================");
        System.out.println(sum/5000);
        
        sum = 0;
        
        for(int i=0; i< 5000; i++){
            rnd = dch.getRndReal();
            sum += rnd;
//            System.out.println(rnd);
        }
        
        System.out.println("=====================");
        System.out.println(sum/5000);
        
    }
    
}
