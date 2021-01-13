package model.tf.nwf;

import java.util.ArrayList;
import java.util.List;
import model.Individual;
import model.tf.TestFunction;
import util.IndividualUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 * Created by adam on 22/02/16.
 */
public class Network implements TestFunction {

    private int nodeCount = 20;
    private int[][] path;
    private double[][] pathCreate;
    private double[][] pathCost;
    
    private int[] X;
    private int start;
    private double cost = 1;
    private double load_cost = 0;
    private List<Integer> node_path;
    private List<Integer[]> built_path;
    
    @Override
    public double fitness(Individual individual) {
        return fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {

        //HRABEC data
        //20 nodes
        nodeCount = 20;
        path = new int[][]{
        {-1, 1, -1, -1, -1, -1, 1, -1, -1, 1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1},
        {1, -1, -1, 1, -1, -1, 1, -1, 1, -1, -1, 1, -1, 1, -1, -1, 1, -1, -1, -1},
        {-1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1},
        {-1, 1, 1, -1, 1, 1, 1, -1, -1, -1, 1, -1, -1, 1, 1, 1, -1, 1, 1, 1},
        {-1, -1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, -1, 1},
        {-1, -1, 1, 1, 1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, 1, -1, 1, 1, 1},
        {1, 1, -1, 1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, 1, -1},
        {-1, 1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, 1, -1, 1, 1, 1, -1, -1, 1},
        {1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1},
        {-1, -1, -1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, 1, -1, 1},
        {1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1},
        {-1, 1, -1, 1, -1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1, 1, -1, 1, 1, 1},
        {1, -1, 1, 1, -1, -1, -1, -1, 1, -1, -1, -1, -1, 1, -1, -1, 1, 1, 1, 1},
        {-1, -1, -1, 1, -1, 1, -1, -1, 1, -1, 1, -1, 1, 1, -1, -1, 1, -1, -1, -1},
        {-1, 1, -1, -1, 1, -1, -1, -1, 1, -1, -1, -1, -1, -1, 1, 1, -1, 1, 1, 1},
        {-1, -1, 1, 1, 1, 1, 1, -1, -1, 1, 1, -1, 1, 1, 1, -1, 1, -1, 1, 1},
        {-1, -1, -1, 1, -1, 1, 1, 1, -1, -1, -1, -1, -1, 1, 1, -1, 1, 1, -1, -1},
        {-1, -1, -1, 1, 1, 1, -1, -1, 1, -1, 1, -1, 1, 1, 1, -1, 1, 1, -1, -1},
        };
        pathCreate = new double[][]{
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };
        pathCost = new double[][]{
        {0., 7.79795, 0., 0., 0., 0., 11.0742, 0., 0., 5.90089, 0., 3.35335, 0., 0., 17.5896, 0., 0., 0., 0., 0.},
        {7.79795, 0., 0., 11.7714, 0., 0., 9.60583, 0., 19.3132, 0., 0., 5.0603, 0., 29.0799, 0., 0., 24.7506, 0., 0., 0.},
        {0., 0., 0., 9.43539, 16.5082, 18.4677, 0., 0., 0., 0., 0., 0., 0., 0., 6.90107, 0., 0., 15.6466, 0., 0.},
        {0., 11.7714, 9.43539, 0., 17.8316, 20.2026, 2.7945, 0., 0., 0., 20.4695, 0., 0., 22.179, 4.48402, 6.74983, 0., 14.0993, 4.01969, 19.893},
        {0., 0., 16.5082, 17.8316, 0., 2.37984, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 4.506, 5.42305, 0., 8.422},
        {0., 0., 18.4677, 20.2026, 2.37984, 0., 0., 0., 0., 0., 0.327185, 0., 0., 17.2695, 0., 13.4927, 0., 7.50561, 22.024, 8.90829},
        {11.0742, 9.60583, 0., 2.7945, 0., 0., 0., 0., 10.0355, 0., 0., 0., 0., 0., 0., 0., 0., 16.8925, 4.68551, 0.},
        {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 1.70347, 0., 0., 0., 0., 19.0744, 0.},
        {0., 19.3132, 0., 0., 0., 0., 10.0355, 0., 0., 18.1834, 0., 0., 11.5863, 0., 3.16131, 2.59089, 6.83594, 0., 0., 14.9727},
        {5.90089, 0., 0., 0., 0., 0., 0., 0., 18.1834, 0., 0., 0., 0., 0., 0., 0., 0., 26.251, 0., 0.},
        {0., 0., 0., 20.4695, 0., 0.327185, 0., 0., 0., 0., 0., 0., 7.13717, 0., 0., 13.7681, 0., 7.83059, 0., 9.16272},
        {3.35335, 5.0603, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.},
        {0., 0., 0., 0., 0., 0., 0., 0., 11.5863, 0., 7.13717, 0., 0., 0., 0., 10.6569, 0., 3.17877, 0., 3.56097},
        {0., 29.0799, 0., 22.179, 0., 17.2695, 0., 1.70347, 0., 0., 0., 0., 0., 0., 21.0916, 17.8186, 0., 12.8773, 20.7444, 8.38863},
        {17.5896, 0., 6.90107, 4.48402, 0., 0., 0., 0., 3.16131, 0., 0., 0., 0., 21.0916, 0., 0., 9.69717, 10.8789, 7.8419, 17.2257},
        {0., 0., 0., 6.74983, 0., 13.4927, 0., 0., 2.59089, 0., 13.7681, 0., 10.6569, 17.8186, 0., 0., 6.71656, 0., 0., 0.},
        {0., 24.7506, 0., 0., 4.506, 0., 0., 0., 6.83594, 0., 0., 0., 0., 0., 9.69717, 6.71656, 0., 2.51271, 15.1506, 8.75167},
        {0., 0., 15.6466, 14.0993, 5.42305, 7.50561, 16.8925, 0., 0., 26.251, 7.83059, 0., 3.17877, 12.8773, 10.8789, 0., 2.51271, 0., 15.131, 6.59381},
        {0., 0., 0., 4.01969, 0., 22.024, 4.68551, 19.0744, 0., 0., 0., 0., 0., 20.7444, 7.8419, 0., 15.1506, 15.131, 0., 0.},
        {0., 0., 0., 19.893, 8.422, 8.90829, 0., 0., 14.9727, 0., 9.16272, 0., 3.56097, 8.38863, 17.2257, 0., 8.75167, 6.59381, 0., 0.},
        };

        X = new int[]{20, -150, 14, 10, 14, 14, 14, 11, 18, 18, 10, 15, 13, 19, 10, -125, 17, 20, 19, 19};
        start = 1;



        double profit = 0;
        List<Integer> reachable_nodes = new ArrayList<>();
        int next_node, amount, cur_node = start, min_wanted, possible_amount_add;
        this.node_path = new ArrayList<>();
        this.built_path = new ArrayList<>();
        
        amount = X[start];
        X[start] = 0;
        this.node_path.add(start);
        
        for(int i=0; i<vector.length; i++){
           
            min_wanted = this.getMinimumWanted();
            possible_amount_add = this.getPossibleAmountAdd();
            
            if(min_wanted == 0){
                return profit;
            }
            
            if(Math.abs(amount + possible_amount_add) < min_wanted){
                return profit;
            }
            
            next_node = (int) Math.round(vector[i]);
            reachable_nodes = this.getReachableNodes(cur_node);
            next_node = reachable_nodes.get(next_node % reachable_nodes.size());
            
            //path creation
            this.node_path.add(next_node);
            
            //create path
            if(this.pathCreate[cur_node][next_node] > 0){
                profit += this.pathCreate[cur_node][next_node];
                this.pathCreate[cur_node][next_node] = 0;
                this.built_path.add(new Integer[]{cur_node, next_node});
            }
            
            //path cost
            profit += this.pathCost[cur_node][next_node];
            
            //amount change
            if(amount + this.X[next_node] <= 0 ){
                if(amount <= (amount + this.X[next_node])) {
                    //sell case
                    amount += this.X[next_node];
                    profit -= (this.X[next_node] * this.cost);
                    this.X[next_node] = 0;
                    
                }
                else {
                    //load case
                    amount += this.X[next_node];
                    profit += (this.X[next_node] * this.load_cost);
                    this.X[next_node] = 0;
                }
            }
           
            cur_node = next_node;
           
        }
        
        return profit;
        
    }
    
    private List<Integer> getReachableNodes(int curNode){
        
        List<Integer> list = new ArrayList<>();
        
        for(int i=0;i<nodeCount;i++){
            
            if(path[curNode][i] > -1){
                list.add(i);
            }
            
        }
        
        return list;
        
    }
    
    private int getPossibleAmountAdd(){
        
        int ret = 0;
        
        for(int i=0; i<X.length; i++){
        
            if(X[i] < ret){
                ret = X[i];
            }
            
        }
        
        return ret;
        
    }
    
    private int getMinimumWanted(){
        
        int ret = Integer.MAX_VALUE;
        boolean empty = true;
        
        for(int i=0; i<X.length; i++){
            
            if(X[i] > 0 && X[i] < ret){
                ret = X[i];
                empty = false;
            }
            
        }
        
        return empty ? 0 : ret;
    }

    @Override
    public void constrain(Individual individual) {
        IndividualUtil.clipInBounds(individual, 0, nodeCount);
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] vector = new double[dim];
        Random rnd = new UniformRandom();
        for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(0, nodeCount);
        return vector;
    }

    @Override
    public double fixedAccLevel() {
        return 10E-7;
    }

    @Override
    public double optimum() {
        return Double.MIN_VALUE;
    }

    @Override
    public double max(int dim) {
        return nodeCount;
    }

    @Override
    public double min(int dim) {
        return 0;
    }

    @Override
    public String name() {
        return "Network";
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public int[][] getPath() {
        return path;
    }

    public void setPath(int[][] path) {
        this.path = path;
    }

    public double[][] getPathCreate() {
        return pathCreate;
    }

    public void setPathCreate(double[][] pathCreate) {
        this.pathCreate = pathCreate;
    }

    public double[][] getPathCost() {
        return pathCost;
    }

    public void setPathCost(double[][] pathCost) {
        this.pathCost = pathCost;
    }

    public int[] getX() {
        return X;
    }

    public void setX(int[] X) {
        this.X = X;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getLoad_cost() {
        return load_cost;
    }

    public void setLoad_cost(double load_cost) {
        this.load_cost = load_cost;
    }

    public List<Integer> getNode_path() {
        return node_path;
    }

    public void setNode_path(List<Integer> node_path) {
        this.node_path = node_path;
    }

    public List<Integer[]> getBuilt_path() {
        return built_path;
    }

    public void setBuilt_path(List<Integer[]> built_path) {
        this.built_path = built_path;
    }
    
    public static void main(String[] args) throws Exception {
    
        double[] vector = new double[]{15.49306463073848, 12.255331378829208, 6.545714809245419, 11.291510091739896, 16.847609677972667, 3.453637956295934, 3.611302426342884, 10.207357571951128, 8.391493526645487, 18.7795772019138};
        TestFunction tf = new Network();
        
        System.out.println(tf.fitness(vector));
        System.out.println(((Network)tf).getNode_path().toString());
        
    }

    @Override
    public double[] optimumPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
