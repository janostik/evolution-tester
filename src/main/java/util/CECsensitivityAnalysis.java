package util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
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
    
    public HashMap<Integer, int[][]> testCEC2020sensitivity(String path) {
        
        HashMap<Integer, int[][]> res = new HashMap<>();
        int[] dims = {5,10,15,20};
        int[] fns = {1,2,3,4,5,6,7,8,9,10};
        PrintWriter pw;
        
        for(int D : dims) {
            int[][] value = new int[fns.length][D];
            for(int ff : fns) {
                try {
                    int[] sens = this.getFuntionSensitivity(new Cec2020(D, ff));
                    value[ff-1] = sens;
                } catch (Exception ex) {
                    Logger.getLogger(CECsensitivityAnalysis.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            res.put(D, value);
            try {
                pw = new PrintWriter(path + String.valueOf(D) + ".txt", "UTF-8");
                pw.print("{");
                for(int i = 0; i < value.length; i++) {
                    
                    pw.print("{");
                    for(int j = 0; j < value[i].length; j++) {

                        pw.print(value[i][j]);

                        if(j != value[i].length-1) {
                            pw.print(",");
                        }
                    }
                    pw.print("}");
                    
                    
                    if(i != value.length-1) {
                        pw.print(",");
                    }
                }
                pw.print("}");
                pw.close();
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CECsensitivityAnalysis.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(CECsensitivityAnalysis.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return res;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        CECsensitivityAnalysis an = new CECsensitivityAnalysis();
        
        HashMap<Integer,int[][]> hm = an.testCEC2020sensitivity("D:\\results\\CEC sensitivity\\Java\\CEC2020\\");
        
        for(int key : hm.keySet()) {
            System.out.println(key);
            int[][] value = hm.get(key);
            int fn = 1;
            for(int[] dd : value) {
                System.out.println(fn + " - " + Arrays.toString(dd));
                fn++;
            }
        }
        
    }
    
}
