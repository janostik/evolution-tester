package util;

import static java.lang.System.out;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.tf.Cec2020;
import model.tf.TestFunction;

/**
 * Tests the sensitivity of each parameter for the CEC test functions
 * @author adam on 15/01/2021
 */
public class CECsensitivityAnalysis {

    private DecimalFormat df;
    private int max_prec = 16;
    
    public int[] getFuntionSensitivity(TestFunction f) {
        
        double[] opt = f.optimumPosition();
        double[] tester = opt.clone();
        int D = opt.length;
        int[] res = new int[D];
        int prec = this.max_prec;
        
        for(int i = 0; i < D; i++) {
            
            while(true) {
                tester[i] = this.trim(opt[i], prec);
                if(f.fitness(tester) != f.fitness(opt)) {
                    res[i] = prec;
                    break;
                } else {
                    prec--;
                }
                if(prec < 0)
                    break;
            }
            
        }
        
        return res;
    }
    
    private double trim(double in, int precision) {
        
        String pattern = "#.";
        for(int i = 0; i < precision; i++) {
            pattern += "#";
        }
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        df = new DecimalFormat(pattern, otherSymbols);
        df.setRoundingMode(RoundingMode.DOWN);
        
        double res = Double.parseDouble(df.format(in));
        
        return res;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        CECsensitivityAnalysis an = new CECsensitivityAnalysis();
        
        try {
            TestFunction tf = new Cec2020(20, 10);
            System.out.println(Arrays.toString(an.getFuntionSensitivity(tf)));
        } catch (Exception ex) {
            Logger.getLogger(CECsensitivityAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
