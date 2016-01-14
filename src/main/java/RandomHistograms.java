
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import util.LoziRandomUtil;
import util.RandomUtil;

/**
 *
 * @author wiki
 */
public class RandomHistograms {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        
        PrintWriter pw = new PrintWriter("histogram_data.txt");
        
        int max = 10000;
        
        pw.append("{");
        
        pw.append("{");
        for(int i = 0; i < max; i++){
            
            pw.append(RandomUtil.nextDouble().toString());
            
            if(i < max-1){
                pw.append(",");
            }
            
        }
        pw.append("}");
        
        pw.append(",");
        
        pw.append("{");
        for(int i = 0; i < max; i++){
            
            pw.append(LoziRandomUtil.nextDouble().toString());
            
            if(i < max-1){
                pw.append(",");
            }
            
        }
        pw.append("}");
        
        pw.append("}");
        
        pw.close();
        
    }
    
}
