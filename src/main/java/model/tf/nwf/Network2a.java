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
 * VRP problem solution
 * No way back
 */
public class Network2a implements TestFunction {

    private int nodeCount = 20;
    private int[][] path;
    private double[][] pathCreate;
    private double[][] pathCost;
    
    private int[] X;
    private int start;
    private double cost = 100;
    private double load_cost = 0;
    private List<Integer> node_path;
    private List<Integer> visited_nodes;
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
        {-1, 1, -1, -1, -1, 1, -1, -1, -1, 1, -1, -1, -1, -1, 1, -1, -1, -1, 1, -1},
        {1, -1, 1, -1, -1, -1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1},
        {-1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, 1},
        {-1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {1, -1, -1, 1, -1, -1, -1, -1, 1, 1, -1, -1, -1, 1, -1, -1, 1, 1, -1, -1},
        {-1, 1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, -1, -1, -1, 1, -1, -1},
        {-1, -1, -1, 1, 1, 1, 1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, -1},
        {1, 1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, -1, -1, 1, -1, 1, -1},
        {-1, -1, -1, -1, 1, -1, -1, -1, 1, 1, -1, -1, 1, 1, -1, 1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, -1, 1},
        {-1, -1, -1, 1, -1, -1, -1, 1, -1, 1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, 1, -1, 1, 1, -1, 1, -1, 1, -1, 1, -1, -1, 1, -1, 1},
        {1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, 1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, 1, 1, -1},
        {-1, -1, -1, -1, -1, 1, -1, 1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1},
        {1, -1, 1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1},
        {-1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, -1, -1, -1, -1, -1, -1},
        };
        pathCreate = new double[][]{
        {0., 18.3919, 0., 0., 0., 22.3921, 0., 0., 0., 24.4829, 0., 0., 0., 0., 27.5678, 0., 0., 0., 3.17758, 0.},
        {18.3919, 0., 7.24055, 0., 0., 0., 2.18299, 18.8106, 0., 8.18228, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.},
        {0., 7.24055, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 8.35618, 0.},
        {0., 0., 0., 0., 0., 8.95128, 0., 0., 6.60562, 0., 0., 0., 6.18314, 0., 0., 0., 0., 0., 0., 4.48173},
        {0., 0., 0., 0., 0., 0., 0., 0., 12.7432, 2.14104, 13.7607, 0., 0., 0., 0., 0., 0., 0., 0., 0.},
        {22.3921, 0., 0., 8.95128, 0., 0., 0., 0., 2.95456, 9.67173, 0., 0., 0., 4.88336, 0., 0., 13.6207, 6.89529, 0., 0.},
        {0., 2.18299, 0., 0., 0., 0., 0., 0., 18.677, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.},
        {0., 18.8106, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 16.2325, 5.22335, 0., 0., 0., 8.99876, 0., 0.},
        {0., 0., 0., 6.60562, 12.7432, 2.95456, 18.677, 0., 0., 0., 11.1393, 0., 0., 1.98855, 0., 0., 0., 0., 0., 0.},
        {24.4829, 8.18228, 0., 0., 2.14104, 9.67173, 0., 0., 0., 0., 15.097, 0., 15.2763, 0., 0., 0., 17.8025, 0., 21.6259, 0.},
        {0., 0., 0., 0., 13.7607, 0., 0., 0., 11.1393, 15.097, 0., 0., 0.758777, 11.5939, 0., 4.31367, 0., 0., 0., 0.},
        {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 11.1516, 0., 5.15709, 15.067, 0., 0., 0., 3.35598},
        {0., 0., 0., 6.18314, 0., 0., 0., 16.2325, 0., 15.2763, 0.758777, 11.1516, 0., 11.0117, 0., 0., 0., 0., 0., 0.},
        {0., 0., 0., 0., 0., 4.88336, 0., 5.22335, 1.98855, 0., 11.5939, 0., 11.0117, 0., 5.20044, 0., 0., 10.2545, 0., 9.59911},
        {27.5678, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 5.15709, 0., 5.20044, 0., 0., 0., 0., 0., 0.},
        {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 4.31367, 15.067, 0., 0., 0., 0., 0., 0., 0., 0.},
        {0., 0., 0., 0., 0., 13.6207, 0., 0., 0., 17.8025, 0., 0., 0., 0., 0., 0., 0., 7.4694, 7.92899, 0.},
        {0., 0., 0., 0., 0., 6.89529, 0., 8.99876, 0., 0., 0., 0., 0., 10.2545, 0., 0., 7.4694, 0., 0., 0.},
        {3.17758, 0., 8.35618, 0., 0., 0., 0., 0., 0., 21.6259, 0., 0., 0., 0., 0., 0., 7.92899, 0., 0., 0.},
        {0., 0., 0., 4.48173, 0., 0., 0., 0., 0., 0., 0., 3.35598, 0., 9.59911, 0., 0., 0., 0., 0., 0.},
        };
        pathCost = new double[][]{
        {0., 18.3919, 0., 0., 0., 22.3921, 0., 0., 0., 24.4829, 0., 0., 0., 0., 27.5678, 0., 0., 0., 3.17758, 0.},
        {18.3919, 0., 7.24055, 0., 0., 0., 2.18299, 18.8106, 0., 8.18228, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.},
        {0., 7.24055, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 8.35618, 0.},
        {0., 0., 0., 0., 0., 8.95128, 0., 0., 6.60562, 0., 0., 0., 6.18314, 0., 0., 0., 0., 0., 0., 4.48173},
        {0., 0., 0., 0., 0., 0., 0., 0., 12.7432, 2.14104, 13.7607, 0., 0., 0., 0., 0., 0., 0., 0., 0.},
        {22.3921, 0., 0., 8.95128, 0., 0., 0., 0., 2.95456, 9.67173, 0., 0., 0., 4.88336, 0., 0., 13.6207, 6.89529, 0., 0.},
        {0., 2.18299, 0., 0., 0., 0., 0., 0., 18.677, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.},
        {0., 18.8106, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 16.2325, 5.22335, 0., 0., 0., 8.99876, 0., 0.},
        {0., 0., 0., 6.60562, 12.7432, 2.95456, 18.677, 0., 0., 0., 11.1393, 0., 0., 1.98855, 0., 0., 0., 0., 0., 0.},
        {24.4829, 8.18228, 0., 0., 2.14104, 9.67173, 0., 0., 0., 0., 15.097, 0., 15.2763, 0., 0., 0., 17.8025, 0., 21.6259, 0.},
        {0., 0., 0., 0., 13.7607, 0., 0., 0., 11.1393, 15.097, 0., 0., 0.758777, 11.5939, 0., 4.31367, 0., 0., 0., 0.},
        {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 11.1516, 0., 5.15709, 15.067, 0., 0., 0., 3.35598},
        {0., 0., 0., 6.18314, 0., 0., 0., 16.2325, 0., 15.2763, 0.758777, 11.1516, 0., 11.0117, 0., 0., 0., 0., 0., 0.},
        {0., 0., 0., 0., 0., 4.88336, 0., 5.22335, 1.98855, 0., 11.5939, 0., 11.0117, 0., 5.20044, 0., 0., 10.2545, 0., 9.59911},
        {27.5678, 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 5.15709, 0., 5.20044, 0., 0., 0., 0., 0., 0.},
        {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 4.31367, 15.067, 0., 0., 0., 0., 0., 0., 0., 0.},
        {0., 0., 0., 0., 0., 13.6207, 0., 0., 0., 17.8025, 0., 0., 0., 0., 0., 0., 0., 7.4694, 7.92899, 0.},
        {0., 0., 0., 0., 0., 6.89529, 0., 8.99876, 0., 0., 0., 0., 0., 10.2545, 0., 0., 7.4694, 0., 0., 0.},
        {3.17758, 0., 8.35618, 0., 0., 0., 0., 0., 0., 21.6259, 0., 0., 0., 0., 0., 0., 7.92899, 0., 0., 0.},
        {0., 0., 0., 4.48173, 0., 0., 0., 0., 0., 0., 0., 3.35598, 0., 9.59911, 0., 0., 0., 0., 0., 0.},
        };

        X = new int[]{-283, 11, 19, 16, 13, 13, 12, 13, 19, 10, 15, 20, 16, 20, 18, 11, 17, 11, 12, 17};
        start = 0;






        double profit = 0, best_profit = Math.pow(10, 30);
        List<Integer> reachable_nodes = new ArrayList<>();
        int next_node, amount, cur_node = start, min_wanted, possible_amount_add, last_index = 0;
        this.node_path = new ArrayList<>();
        this.visited_nodes = new ArrayList<>();
        this.built_path = new ArrayList<>();
        
        amount = X[start];
        X[start] = 0;
        this.node_path.add(start);
        this.visited_nodes.add(start);
        
        for(int i=0; i<vector.length; i++){

            min_wanted = this.getMinimumWanted();
            possible_amount_add = this.getPossibleAmountAdd();
            
            if(min_wanted == 0){
                this.node_path = this.node_path.subList(0, last_index+2);
                return best_profit;
            }
            
            if(Math.abs(amount + possible_amount_add) < min_wanted){
                this.node_path = this.node_path.subList(0, last_index+2);
                return best_profit;
            }
            
            next_node = (int) Math.round(vector[i]);
            reachable_nodes = this.getReachableNodes(cur_node);
            if(reachable_nodes.isEmpty()){
                this.node_path = this.node_path.subList(0, last_index+2);
                return best_profit;
            }
            
            next_node = reachable_nodes.get(next_node % reachable_nodes.size());
            this.visited_nodes.add(next_node);
            
            //path creation
            this.node_path.add(next_node);
            
            //create path
            if(this.pathCreate[cur_node][next_node] > 0){
                profit += this.pathCreate[cur_node][next_node];
                this.pathCreate[cur_node][next_node] = 0;
                this.pathCreate[next_node][cur_node] = 0;
                this.built_path.add(new Integer[]{cur_node, next_node});
            }
            
            //path cost
            profit += (this.pathCost[cur_node][next_node]*Math.abs(amount));
            
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
            
            if(profit <= best_profit){
                best_profit = profit;
                if(this.visited_nodes.size() < nodeCount){
                    best_profit = Double.MAX_VALUE;
                }
                last_index = i;
            }
           
        }
        
        this.node_path = this.node_path.subList(0, last_index+2);
        return best_profit;
        
    }
    
    private List<Integer> getReachableNodes(int curNode){
        
        List<Integer> list = new ArrayList<>();
        
        for(int i=0;i<nodeCount;i++){
            
            if(path[curNode][i] > -1 && !this.visited_nodes.contains(i)){
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
    
        double[] vector = new double[]{18.15230937727639, 14.159507772138284, 11.808717616052467, 1.286959081622224, 12.614404281250785, 6.3141550814170495, 12.769740908323929, 6.790651038286033, 9.236625325018759, 16.125090494306136, 11.66501989579939, 14.52266108467765, 17.87490370417573, 7.995073608146809, 18.83178354341432, 9.773662152705207, 16.079829738409835, 2.001430471042706, 2.6676532786101084, 12.311895838940824, 11.897711446320253, 3.0187174696506216, 11.424646369415438, 6.192181156093385, 5.2859992303732355, 3.767553469888564, 12.783953468627063, 8.440506290167514, 5.0832866768538345, 15.224162553111908, 9.388134447888973, 3.8540451894490277, 6.252550694502576, 6.9451268688740475, 9.256975151424783, 18.634102970716654, 11.821839124829353, 13.662050126113616, 13.386778426569135, 18.44653985073991};
        TestFunction tf = new Network2a();
        
        System.out.println(tf.fitness(vector));
        System.out.println(((Network2a)tf).getNode_path().toString());
        
    }
}
