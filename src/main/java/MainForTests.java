
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import util.random.NormalRandom;

/**
 *
 * @author wiki
 */
public class MainForTests {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        
//        PrintWriter pw = new PrintWriter("histogram_data.txt");
//        
//        int max = 10000;
//        
//        pw.append("{");
//        
//        pw.append("{");
//        for(int i = 0; i < max; i++){
//            
//            pw.append(RandomUtil.nextDouble().toString());
//            
//            if(i < max-1){
//                pw.append(",");
//            }
//            
//        }
//        pw.append("}");
//        
//        pw.append(",");
//        
//        pw.append("{");
//        for(int i = 0; i < max; i++){
//            
//            pw.append(LoziRandomUtil.nextDouble().toString());
//            
//            if(i < max-1){
//                pw.append(",");
//            }
//            
//        }
//        pw.append("}");
//        
//        pw.append("}");
//        
//        pw.close();
//        
//        int nodeCount = 20;
//
//        double[][] pathCreate = new double[][]{
//        {0., 18.3919, 0., 0., 0., 22.3921, 0., 0., 0., 24.4829, 0., 0., 0., 0., 27.5678, 0., 0., 0., 3.17758, 0.},
//        {18.3919, 0., 7.24055, 0., 0., 0., 2.18299, 18.8106, 0., 8.18228, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.},
//        {0., 7.24055, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 8.35618, 0.},
//        {0., 0., 0., 0., 0., 8.95128, 0., 0., 6.60562, 0., 0., 0., 6.18314, 0., 0., 0., 0., 0., 0., 4.48173},
//        {0., 0., 0., 0., 0., 0., 0., 0., 12.7432, 2.14104, 13.7607, 0., 0., 0., 0., 0., 0., 0., 0., 0.},
//        {22.3921, 0., 0., 8.95128, 0., 0., 0., 0., 2.95456, 9.67173, 0., 0., 0., 4.88336, 0., 0., 13.6207, 6.89529, 0., 0.},
//        {0., 2.18299, 0., 0., 0., 0., 0., 0., 18.677, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.},
//        {0., 18.8106, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 16.2325, 5.22335, 0., 0., 0., 8.99876, 0., 0.},
//        {0., 0., 0., 6.60562, 12.7432, 2.95456, 18.677, 0., 0., 0., 11.1393, 0., 0., 1.98855, 0., 0., 0., 0., 0., 0.},
//        {24.4829, 8.18228, 0., 0., 2.14104, 9.67173, 0., 0., 0., 0., 15.097, 0., 15.2763, 0., 0., 0., 17.8025, 0., 21.6259, 0.},
//        {0., 0., 0., 0., 13.7607, 0., 0., 0., 11.1393, 15.097, 0., 0., 0.758777, 11.5939, 0., 4.31367, 0., 0., 0., 0.},
//        {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 11.1516, 0., 5.15709, 15.067, 0., 0., 0., 3.35598},
//        {0., 0., 0., 6.18314, 0., 0., 0., 16.2325, 0., 15.2763, 0.758777, 11.1516, 0., 11.0117, 0., 0., 0., 0., 0., 0.},
//        {0., 0., 0., 0., 0., 4.88336, 0., 5.22335, 1.98855, 0., 11.5939, 0., 11.0117, 0., 5.20044, 0., 0., 10.2545, 0., 9.59911},
//        {27.5678, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 5.15709, 0., 5.20044, 0., 0., 0., 0., 0., 0.},
//        {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 4.31367, 15.067, 0., 0., 0., 0., 0., 0., 0., 0.},
//        {0., 0., 0., 0., 0., 13.6207, 0., 0., 0., 17.8025, 0., 0., 0., 0., 0., 0., 0., 7.4694, 7.92899, 0.},
//        {0., 0., 0., 0., 0., 6.89529, 0., 8.99876, 0., 0., 0., 0., 0., 10.2545, 0., 0., 7.4694, 0., 0., 0.},
//        {3.17758, 0., 8.35618, 0., 0., 0., 0., 0., 0., 21.6259, 0., 0., 0., 0., 0., 0., 7.92899, 0., 0., 0.},
//        {0., 0., 0., 4.48173, 0., 0., 0., 0., 0., 0., 0., 3.35598, 0., 9.59911, 0., 0., 0., 0., 0., 0.},
//        };
//        double[][] pathCost = new double[][]{
//        {0., 18.3919, 0., 0., 0., 22.3921, 0., 0., 0., 24.4829, 0., 0., 0., 0., 27.5678, 0., 0., 0., 3.17758, 0.},
//        {18.3919, 0., 7.24055, 0., 0., 0., 2.18299, 18.8106, 0., 8.18228, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.},
//        {0., 7.24055, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 8.35618, 0.},
//        {0., 0., 0., 0., 0., 8.95128, 0., 0., 6.60562, 0., 0., 0., 6.18314, 0., 0., 0., 0., 0., 0., 4.48173},
//        {0., 0., 0., 0., 0., 0., 0., 0., 12.7432, 2.14104, 13.7607, 0., 0., 0., 0., 0., 0., 0., 0., 0.},
//        {22.3921, 0., 0., 8.95128, 0., 0., 0., 0., 2.95456, 9.67173, 0., 0., 0., 4.88336, 0., 0., 13.6207, 6.89529, 0., 0.},
//        {0., 2.18299, 0., 0., 0., 0., 0., 0., 18.677, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.},
//        {0., 18.8106, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 16.2325, 5.22335, 0., 0., 0., 8.99876, 0., 0.},
//        {0., 0., 0., 6.60562, 12.7432, 2.95456, 18.677, 0., 0., 0., 11.1393, 0., 0., 1.98855, 0., 0., 0., 0., 0., 0.},
//        {24.4829, 8.18228, 0., 0., 2.14104, 9.67173, 0., 0., 0., 0., 15.097, 0., 15.2763, 0., 0., 0., 17.8025, 0., 21.6259, 0.},
//        {0., 0., 0., 0., 13.7607, 0., 0., 0., 11.1393, 15.097, 0., 0., 0.758777, 11.5939, 0., 4.31367, 0., 0., 0., 0.},
//        {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 11.1516, 0., 5.15709, 15.067, 0., 0., 0., 3.35598},
//        {0., 0., 0., 6.18314, 0., 0., 0., 16.2325, 0., 15.2763, 0.758777, 11.1516, 0., 11.0117, 0., 0., 0., 0., 0., 0.},
//        {0., 0., 0., 0., 0., 4.88336, 0., 5.22335, 1.98855, 0., 11.5939, 0., 11.0117, 0., 5.20044, 0., 0., 10.2545, 0., 9.59911},
//        {27.5678, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 5.15709, 0., 5.20044, 0., 0., 0., 0., 0., 0.},
//        {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 4.31367, 15.067, 0., 0., 0., 0., 0., 0., 0., 0.},
//        {0., 0., 0., 0., 0., 13.6207, 0., 0., 0., 17.8025, 0., 0., 0., 0., 0., 0., 0., 7.4694, 7.92899, 0.},
//        {0., 0., 0., 0., 0., 6.89529, 0., 8.99876, 0., 0., 0., 0., 0., 10.2545, 0., 0., 7.4694, 0., 0., 0.},
//        {3.17758, 0., 8.35618, 0., 0., 0., 0., 0., 0., 21.6259, 0., 0., 0., 0., 0., 0., 7.92899, 0., 0., 0.},
//        {0., 0., 0., 4.48173, 0., 0., 0., 0., 0., 0., 0., 3.35598, 0., 9.59911, 0., 0., 0., 0., 0., 0.},
//        };
//        int[][] pathLoad = new int[][]{
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {42, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 38, 0, 0, 74, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 13, 12, 0, 0, 0, 0, 20, 0, 28, 37, 0, 0, 11, 12, 0, 0},
//        };
//
//        int[] X = new int[]{11, 19, 16, 13, 13, 12, 13, 19, 10, 15, 20, 16, 20, 18, 11, 17, 11, 12, -133, -133};
//        int[] nodeLoad = new int[]{11, 19, 16, 13, 13, 12, 13, 19, 10, 15, 20, 16, 20, 18, 11, 17, 11, 12, 0, 0};
//        //{x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,0,0}
//        //{1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0}
//        double cost = 100;
//        
//        double profit = 0;
//        
//        //count the profit
//        for(int i = 0; i < nodeCount; i++){
//            
//            profit -= (nodeLoad[i]*cost);
//            
//            for(int j = 0; j < nodeCount; j++){
//                profit += (pathLoad[i][j]*pathCost[i][j]);
//                
//                if(pathLoad[i][j] > 0){
//
//                    if(pathCreate[i][j] > 0){
//                        profit += pathCreate[i][j];
//                    }
//                }
//            } 
//        }
//
//        System.out.println(profit);
//
//
//    
//
//            PrintWriter writer;
//            int out=1000000;
//
//            try {
//                writer = new PrintWriter("hist.txt", "UTF-8");
//
//                writer.print("{");
//
//                util.random.Random generator = new util.random.UniformRandom();
//
//                for(int i = 0; i < out; i++){
//
//                    writer.print(generator.nextDouble(1, 10));
//
//                    if(i != out-1){
//                        writer.println(",");
//                    }
//
//                }
//
//                writer.print("}");
//
//                writer.close();
//
//            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
//                
//            }
//
//        long seed = 10204050L;
//        double x;
//        for(int k = 0; k < 2; k++){
//            
//            System.out.println((k+1) + ". try");
//            
//            util.random.Random rnd = new util.random.UniformRandomSeed(seed);
//            for(int i = 0; i < 1000; i++) {
//                x = rnd.nextDouble(-1,1);
//                if(i % 100 == 0)
//                    System.out.println(x);
//            }
//        }

        
        util.random.Random rndGen = new NormalRandom(0.1, 0.1);
        
        double[] arr = new double[1000];
        
        for(int i =0; i < 1000; i++) {
            arr[i] = rndGen.nextDouble();
        }
        
        System.out.println("Mean value: " + new Mean().evaluate(arr));
        System.out.println("Min value: " + new Min().evaluate(arr));
        System.out.println("Max value: " + new Max().evaluate(arr));
        System.out.println("STD value: " + new StandardDeviation().evaluate(arr));
        
    
    }

}
