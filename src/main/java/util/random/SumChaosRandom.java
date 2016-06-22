package util.random;

import util.random.Random;

/**
 *
 * @author wiki
 */
public class SumChaosRandom implements Random {

    final private static model.chaos.Burgers rndBurgers = new model.chaos.Burgers();
    final private static model.chaos.Dissipative rndDissipative = new model.chaos.Dissipative();
    final private static model.chaos.Lozi rndLozi = new model.chaos.Lozi();
    final private static model.chaos.Tinkerbell rndTinkerbell = new model.chaos.Tinkerbell();
    final private static model.chaos.DelayedLogistic rndDelayedLogistic = new model.chaos.DelayedLogistic();
    
    private double[] rndDoubleArray; 
    private int arraySize = 1000;
    private int iterator;

    public SumChaosRandom() {
        
        this.initializeArray();
        
    }
    
    private void initializeArray(){
        
        iterator = 0;
        rndDoubleArray = new double[arraySize];
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE, sum;
        
        for(int i = 0; i < arraySize; i++){
            
//            sum = rndBurgers.getRndReal() + rndDissipative.getRndReal() + rndLozi.getRndReal() + rndTinkerbell.getRndReal() + rndDelayedLogistic.getRndReal();
            sum = rndLozi.getRndReal() + rndDelayedLogistic.getRndReal();
            rndDoubleArray[i] = sum;
            if(sum < min){
                min = sum;
            }
            if(sum > max){
                max = sum;
            }
            
        }
        
        for(int i = 0; i < arraySize; i++){
            
            rndDoubleArray[i] = (rndDoubleArray[i]-min)/(max-min);
            
        }
        
    }

    @Override
    public double nextDouble() {
        
        if(iterator == arraySize){
            this.initializeArray();
        }
        
        double ret = rndDoubleArray[iterator];
        iterator++;
        
        return ret;
        
    }

    @Override
    public int nextInt(int bound) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        SumChaosRandom rnd = new SumChaosRandom();
        int count = 1000;
        
        System.out.println("{");
        
        for(int i = 0; i < count; i++){
            
            System.out.print(rnd.nextDouble());
            
            if(i != count-1){
                System.out.println(",");
            }
            
        }
        
        System.out.println("}");
        
    }
    
}
