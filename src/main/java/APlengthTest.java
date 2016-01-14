
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import model.ap.AP;
import model.tf.ap.APtf;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Min;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author wiki
 */
public class APlengthTest {

    public void countLengths() throws FileNotFoundException{
        
        AP ap = new AP();
        APtf aptf = new APtf();
        int dim;
        Integer[] vector, gfs_code;
        int runs = 1001;
        double[] length_array;
        double length;
        PrintWriter pw;
        
        pw = new PrintWriter("AP_10_length_lozi.txt");
        pw.append("{");
        dim = 10;
        length_array = new double[runs];
        for(int i=0; i<runs; i++){
            
            vector = ap.discretizeVector(aptf.generateTrial(dim));
            gfs_code = ap.getGFScode(vector);
            
            length = dim;
            
            for(int j = 0; j < dim; j++){
                if(gfs_code[j] == -1){
                    length = j;
                    break;
                }
            }
            
            length_array[i] = length;
            
            pw.append(String.valueOf(length));
            
            if(i < runs-1){
                pw.append(",");
            }

        }
        pw.append("}");
        pw.close();
        

        
        System.out.println("DIM: " + dim);
        System.out.println("RUNS: " + runs);
        System.out.println("===================");
        System.out.println("Mean: " + new Mean().evaluate(length_array));
        System.out.println("Median: " + new Median().evaluate(length_array));
        System.out.println("Min: " + new Min().evaluate(length_array));
        System.out.println("Max: " + new Max().evaluate(length_array));
        System.out.println("===================");
        
        pw = new PrintWriter("AP_50_length_lozi.txt");
        pw.append("{");
        dim = 50;
        length_array = new double[runs];
        for(int i=0; i<runs; i++){
            
            vector = ap.discretizeVector(aptf.generateTrial(dim));
            gfs_code = ap.getGFScode(vector);
            
            length = dim;
            
            for(int j = 0; j < dim; j++){
                if(gfs_code[j] == -1){
                    length = j;
                    break;
                }
            }
            
            length_array[i] = length;
            
            pw.append(String.valueOf(length));
            
            if(i < runs-1){
                pw.append(",");
            }

        }
        pw.append("}");
        pw.close();
        
        System.out.println("DIM: " + dim);
        System.out.println("RUNS: " + runs);
        System.out.println("===================");
        System.out.println("Mean: " + new Mean().evaluate(length_array));
        System.out.println("Median: " + new Median().evaluate(length_array));
        System.out.println("Min: " + new Min().evaluate(length_array));
        System.out.println("Max: " + new Max().evaluate(length_array));
        System.out.println("===================");
        
        pw = new PrintWriter("AP_100_length_lozi.txt");
        pw.append("{");
        dim = 100;
        length_array = new double[runs];
        for(int i=0; i<runs; i++){
            
            vector = ap.discretizeVector(aptf.generateTrial(dim));
            gfs_code = ap.getGFScode(vector);
            
            length = dim;
            
            for(int j = 0; j < dim; j++){
                if(gfs_code[j] == -1){
                    length = j;
                    break;
                }
            }
            
            length_array[i] = length;
            
            pw.append(String.valueOf(length));
            
            if(i < runs-1){
                pw.append(",");
            }

        }
        pw.append("}");
        pw.close();
        
        System.out.println("DIM: " + dim);
        System.out.println("RUNS: " + runs);
        System.out.println("===================");
        System.out.println("Mean: " + new Mean().evaluate(length_array));
        System.out.println("Median: " + new Median().evaluate(length_array));
        System.out.println("Min: " + new Min().evaluate(length_array));
        System.out.println("Max: " + new Max().evaluate(length_array));
        System.out.println("===================");
    }
    
    public void getGraphData() throws FileNotFoundException{
        
        AP ap = new AP();
        APtf aptf = new APtf();
        int dim;
        int dim_min = 10, dim_max = 100;
        Integer[] vector, gfs_code;
        int runs = 1001;
        double[] length_array;
        double length;
        PrintWriter pw;
        
        pw = new PrintWriter("AP_graph_lozi.txt");
        pw.append("{");
        
        for(dim = dim_min; dim <= dim_max; dim++){
        
            length_array = new double[runs];
            for(int i=0; i<runs; i++){

                vector = ap.discretizeVector(aptf.generateTrial(dim));
                gfs_code = ap.getGFScode(vector);

                length = dim;

                for(int j = 0; j < dim; j++){
                    if(gfs_code[j] == -1){
                        length = j;
                        break;
                    }
                }

                length_array[i] = length;
            }
            
            pw.append(String.valueOf(new Mean().evaluate(length_array)));

            if(dim < dim_max){
                pw.append(",");
            }
            
        }
        
        pw.append("}");
        pw.close();
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        
        APlengthTest t = new APlengthTest();
        
        t.getGraphData();
        
    }
    
}
