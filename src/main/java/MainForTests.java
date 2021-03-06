
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import util.distance.Distance;
import util.distance.EuclideanDistance;
import util.distance.SquaredEuclideanDistance;
import util.kmeans.Forgy;
import util.kmeans.KMeans;

/**
 *
 * @author wiki
 */
public class MainForTests {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        
        double number = -6.8857e-9;
        
        System.out.println(String.format(Locale.US, "%.10f", number));
        
//        double[][] data_array = new double[][]{{1,6.95823},{2,5.72046},{3,3.69541},{4,8.2794},{5,8.07507},{6,13.5255},{7,11.2484},{8,19.3141},{9,21.2487},{10,16.1882},{11,17.3731},{12,21.7097},{13,30.0264},{14,30.5398},{15,33.2217},{16,36.7747},{17,37.2571},{18,32.4902},{19,41.284},{20,43.7988},{21,43.8994},{22,43.1335},{23,42.3654},{24,47.8101},{25,47.0841},{26,50.701},{27,55.018},{28,55.7302},{29,57.7139},{30,58.7748},{31,59.7984},{32,66.7561},{33,65.8787},{34,68.2909},{35,73.064},{36,69.736},{37,78.6401},{38,72.408},{39,82.7844},{40,82.8187},{41,83.2628},{42,83.4515},{43,84.3698},{44,85.9758},{45,87.6655},{46,88.9595},{47,94.3834},{48,96.9993},{49,96.8178},{50,95.4017}};
//        List<double[]> data = Arrays.asList(data_array);
//        Regression regr = new LinearRegression();
//        
//        double[] params = regr.getRegressionParameters(data);
//        String sign = "+";
//        if(params[1] < 0) {
//            sign = "-";
//        }
//        
//        System.out.println(params[0] + "*x " + sign + " " + params[1]);
//        
//        double[] A = new double[]{-1,-8,-7};
//        double[] B = new double[]{-2,5,-9};
//        double[] C = new double[]{-4,0,7};
//        double[] D = new double[]{9,5,8};
//        double[] E = new double[]{10,8,3};
//        double[] F = new double[]{6,-4,-2};
//        double[] G = new double[]{1,9,-9};
//        double[] H = new double[]{-1,-4,2};
//        double[] I = new double[]{6,5,-10};
//        double[] J = new double[]{7,-7,-7};
//        Map<Integer, double[]> points = new HashMap<>();
//        points.put(1,A);
//        points.put(2,B);
//        points.put(3,C);
//        points.put(4,D);
//        points.put(5,E);
//        points.put(6,F);
//        points.put(7,G);
//        points.put(8,H);
//        points.put(9,I);
//        points.put(10,J);
//        Distance d = new EuclideanDistance();
//        KMeans kmeans = new KMeans(3, points, new Forgy(), new SquaredEuclideanDistance());
//        kmeans.run();
//        
//        System.out.println(kmeans);
//        
//        
//        Net network = new Net();
//        Individual one, two, three, four;
//        Edge edge;
//        
//        one = new Individual("1", new double[10], 0);
//        two = new Individual("2", new double[10], 0);
//        three = new Individual("3", new double[10], 0);
//        four = new Individual("4", new double[10], 0);
//        
//        edge = new UnidirectionalEdge(one, one, 0.3);
//        network.addEdge(edge);
//        edge = new UnidirectionalEdge(two, one, 0.2);
//        network.addEdge(edge);
//        edge = new UnidirectionalEdge(three, one, 0.4);
//        network.addEdge(edge);
//        edge = new UnidirectionalEdge(four, one, 0.1);
//        network.addEdge(edge);
//        edge = new UnidirectionalEdge(one, two, 0.8);
//        network.addEdge(edge);
//        
//        Map<Individual, Double> map = network.getDegreeMap();
//
//        System.out.println("Oh");
        
        
        
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
//
//        
//        util.random.IkedaRandom rndGen = new IkedaRandom();
//        int count = 5000;
//        double rnd;
//        
//        double[] arr = new double[count];
//        
//        System.out.println("{");
//        
//        for(int i =0; i < count; i++) {
//            rnd = rndGen.nextDouble();
//            
//            System.out.print(String.format(Locale.US, "%.10f", rnd));
//            if(i < count-1) {
//              System.out.print(",");  
//            }
//            
//            arr[i] = rnd;
//        }
//        
//        System.out.println("\n}");
//        
//        System.out.println("Mean value: " + new Mean().evaluate(arr));
//        System.out.println("Min value: " + new Min().evaluate(arr));
//        System.out.println("Max value: " + new Max().evaluate(arr));
//        System.out.println("STD value: " + new StandardDeviation().evaluate(arr));
        
    
    }

}
