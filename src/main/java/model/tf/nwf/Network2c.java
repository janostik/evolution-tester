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
 * Do not need to sell everything from storages
 * Multiple storages
 * Visited nodes have to be fully satisfied
 */
public class Network2c implements TestFunction {

    private int nodeCount = 20;
    private int[][] path;
    private double[][] pathCreate;
    private double[][] pathCost;
    
    private int[] X;
    private int[] start;
    private double cost = 100;
    private double load_cost = 0;
    private List<Integer> node_path;
    private List<Integer> visited_nodes;
    private List<Integer[]> built_path;
    private List<Integer> actualAmount;
    
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

        X = new int[]{11, 19, 16, 13, 13, 12, 13, 19, 10, 15, 20, 16, 20, 18, 11, 17, 11, 12, -133, -133};
//        start = new int[]{18, 19};
        start = new int[]{19, 18};

        this.actualAmount = new ArrayList<>();
        int storageIterator = 0;

        double profit = 0, best_profit = Math.pow(10, 30);
        List<Integer> reachable_nodes = new ArrayList<>();
        int next_node, amount, cur_node = start[storageIterator], min_wanted, possible_amount_add, last_index = 0, tmpAmount;
        this.node_path = new ArrayList<>();
        this.visited_nodes = new ArrayList<>();
        this.built_path = new ArrayList<>();
        
        /**
         * If there is nothing at the starting position to take.
         */
        if(X[cur_node] >= 0){
            return Double.MAX_VALUE;
        }
        
        //Amount is the current load
        amount = this.getAmountFromFeature(vector[storageIterator], this.X[cur_node]);
        //Increment to use next feature next time when visiting storage
        storageIterator++;
        //Subtract the number of pieces taken from start
        X[cur_node] = X[cur_node] + amount;
        //Write the starting node into the path and to visited nodes
        this.node_path.add(cur_node);
        this.visited_nodes.add(cur_node);
        
        //Start from the index after the number of starting positions - n
        //First n features of an individual are used to evolve the amount to pick up at these nodes
        for(int i=start.length; i<vector.length; i++){

            min_wanted = this.getMinimumWanted(); //Minimal demand in nodes - positive value
            possible_amount_add = this.getPossibleAmountAdd(this.visited_nodes); //Maximum amount that can be added from other sources than the starting one
            //The value of possible_amount_add is negative
            
            //No demand left in the network
            if(min_wanted == 0){
                this.node_path = this.node_path.subList(0, last_index + 1);
                return best_profit;
            }
            
            //Minimum demand is higher than the maximal possible load (current amount plus possible amount addition from reachable storages)
            if(Math.abs(amount + possible_amount_add) < min_wanted){
                this.node_path = this.node_path.subList(0, last_index+1);
                return best_profit;
            }
            
            //Rounding of the feature for next node jump
            next_node = (int) Math.round(vector[i]);
            //Getting the reachable nodes from network
            reachable_nodes = this.getReachableNodes(cur_node);
            
            //Nowhere to go from current node
            if(reachable_nodes.isEmpty()){
                this.node_path = this.node_path.subList(0, last_index+1);
                return best_profit;
            }
            
            //Get the actual index of the next node to visit and add it to visited nodes
            next_node = reachable_nodes.get(next_node % reachable_nodes.size());
            this.visited_nodes.add(next_node);
            
            //Next node is added to the path
            this.node_path.add(next_node);
            
            //This part lowers the profit
            //The change is caused by the price of building the non-existing path between two nodes
            if(this.pathCreate[cur_node][next_node] > 0){
                profit += this.pathCreate[cur_node][next_node];
                //Matrix of created paths has to be updated
                this.pathCreate[cur_node][next_node] = 0;
                this.pathCreate[next_node][cur_node] = 0;
                //The array of built paths is updated with currently built path
                this.built_path.add(new Integer[]{cur_node, next_node});
            }
            
            //All paths have their costs which are multiplied by the number of items that are transported
            profit += (this.pathCost[cur_node][next_node]*Math.abs(amount));
            
            //When the next node is reached the amount of currently loaded has to change
            //If next node is storage the amount will decrease because we have negative amount
            //Otherwise it will increase by the amount which will be sold in the next node
            
            //Next node is a storage point
            if(this.X[next_node] < 0){
                
                //The amount of items which will be picked up at the storage point
                tmpAmount = this.getAmountFromFeature(vector[storageIterator], this.X[next_node]);
                //Current load change
                amount += tmpAmount;
                //Increment to use next feature next time when visiting storage
                storageIterator++;
                //Subtract the number of pieces taken from storage
                this.X[next_node] = this.X[next_node] + tmpAmount;
                
            }
            //Next node is a selling place
            else {
                //Condition which decides whether the node can be fully satisfied
                if(amount + this.X[next_node] <= 0 ){
                    //Load update
                    amount += this.X[next_node];
                    //Profit update
                    profit -= (this.X[next_node] * this.cost);
                    //Demand of the node update
                    this.X[next_node] = 0;
                }
            }
            
            //Actual move to the next node
            cur_node = next_node;
            
            //Remember the best profit of the whole path through the network
            if(profit <= best_profit){
                best_profit = profit;
                //Where does the most profitable path ends
                last_index = i - start.length + 1;
            }
           
        }
        
        //When all features of the individual are used return the best profit
        this.node_path = this.node_path.subList(0, last_index+1);
        return best_profit;
        
    }
    
    /**
     * Uses feature of the individual to select the amount
     * which will be extracted from the node - storage.
     * 
     * @param feature
     * @param maxAmount
     * @return 
     */
    public int getAmountFromFeature(double feature, int maxAmount){
        
        double normalizedFeature = (feature - this.min(0)) / (this.max(0) - this.min(0));
        int actAmount = ((int) Math.round(normalizedFeature * (Math.abs(maxAmount)+1))) % (Math.abs(maxAmount)+1);
        
        this.actualAmount.add(actAmount);
        
        return -actAmount;
    }
    
    /**
     * Function returns reachable nodes from current node.
     * Only the nodes that has not been visited yet.
     * 
     * @param curNode
     * @return 
     */
    private List<Integer> getReachableNodes(int curNode){
        
        List<Integer> list = new ArrayList<>();
        
        for(int i=0;i<nodeCount;i++){
            
            if(path[curNode][i] > -1 && !this.visited_nodes.contains(i)){
                list.add(i);
            }
            
        }
        
        return list;
        
    }
    
    /**
     * Functionn returns the sum of all non-visited storages.
     * 
     * @return 
     */
    private int getPossibleAmountAdd(List<Integer> visitedNodes){
        
        int ret = 0;
        
        for(int i=0; i<X.length; i++){
        
            //Sum of the non visited storages
            if(X[i] < 0 && !visitedNodes.contains(i)){
                ret += X[i];
            }
            
        }
        
        return ret;
        
    }
    
    /**
     * 
     * @return 
     */
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
        return 0;
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
        return "Network2b";
    }

    public List<Integer> getVisited_nodes() {
        return visited_nodes;
    }

    public void setVisited_nodes(List<Integer> visited_nodes) {
        this.visited_nodes = visited_nodes;
    }

    public List<Integer> getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(List<Integer> actualAmount) {
        this.actualAmount = actualAmount;
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

    public int[] getStart() {
        return start;
    }

    public void setStart(int[] start) {
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
        TestFunction tf = new Network2c();
        
        System.out.println(tf.fitness(vector));
        System.out.println(((Network2c)tf).getNode_path().toString());
        
    }

    @Override
    public double[] optimumPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
