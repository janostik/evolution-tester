package model;

import java.util.Random;

/**
 *
 * @author wikki on 11/11/2019
 */
public class Mask {
    
    private int[] mask;
    private double quality;
    private Random rnd;

    public Mask(int D, double CR) {
        
        rnd = new Random();
        boolean one = false;
        
        mask = new int[D];
        for(int i = 0; i < D; i++) {
            if(rnd.nextDouble() <= CR) {
                mask[i] = 1;
                one = true;
            }
            else 
                mask[i] = 0;
        }
        
        if(!one) {
            mask[rnd.nextInt(D)] = 1;
        }
        
        this.quality = 0;
        
    }
    
    public Mask randomizeMask(double prob) {
        
        boolean one = false;
        
        for(int i = 0; i < mask.length; i++) {
            if(rnd.nextDouble() <= prob) {
                mask[i] = (mask[i]+1) % 2;
            }
            if(mask[i] == 1)
                one = true;
        }
        
        if(!one) {
            mask[rnd.nextInt(mask.length)] = 1;
        }
        
        return this;
    }

    public void updateQuality(double qual) {
        this.quality += qual;
    }
    
    public void setQuality(double quality) {
        this.quality = quality;
    }
    
    public int[] getMask() {
        return mask;
    }

    public double getQuality() {
        return quality;
    }
    
    

}
