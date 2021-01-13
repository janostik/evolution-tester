package model.tf.nwf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.Individual;
import model.tf.TestFunction;
import util.IndividualUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 * Created by adam on 24/02/16.
 * 2 central warehouses
 * the demand may be only fully satisfied
 * All nodes can be visited multiple times and the path is not continuous
 */
public class Network3 implements TestFunction {

    private int nodeCount = 20;
    private int[][] path;
    private double[][] pathCreate;
    private double[][] pathCost;
    private int[][] pathLoad;
    
    private int[] X;
    private int[] nodeLoad;
    private List<Integer> start;
    private double cost = 100;
    private double load_cost = 0;
    private List<Integer[]> node_path;
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
        pathLoad = new int[][]{
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

        X = new int[]{11, 19, 16, 13, 13, 12, 13, 19, 10, 15, 20, 16, 20, 18, 11, 17, 11, 12, -133, -133};
        nodeLoad = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        double profit = 0;
        List<Integer> reachable_nodes = new ArrayList<>(), cur_amounts = new ArrayList<>();
        List<Integer[]> cur_path = new ArrayList<>();
        Integer[] path_point;
        int next_node = -1, amount = 0, cur_node = -1, last_index = 0, max_wanted, last_start = -1, cumulative_amount = 0;
        this.node_path = new ArrayList<>();
        this.built_path = new ArrayList<>();

        //cycle through features
        for(int i=0; i<vector.length; i++){
            
            start = this.getStarts(X);
            
            if(start == null || start.isEmpty()){
                break;
            }

            max_wanted = this.getMaximumWanted();
            
            if(max_wanted == 0 || max_wanted > this.getStartMaxAmount(start)) {
                break;
            }
            
            reachable_nodes = this.getReachableSellNodes(cur_node, amount);
            
            //select start case
            if(reachable_nodes == null || reachable_nodes.isEmpty()){
                
                //return unused
                if(last_start != -1){
                    X[last_start] += amount;
                }
                
                if(cur_amounts == null || cur_amounts.isEmpty()){
                    //nowhere to go
                }
                else {
                    //track back the path and amounts
                    cumulative_amount = 0;
                    
                    for(int p = cur_amounts.size()-1; p > -1; p--){
                        cumulative_amount += cur_amounts.get(p);
                        path_point = cur_path.get(p);
                        pathLoad[path_point[0]][path_point[1]] += cumulative_amount;
                    }
                    
                    cur_path = new ArrayList<>();
                    cur_amounts = new ArrayList<>();
                    
                }
                //start selection
                next_node = (int) Math.round(vector[i]);
                next_node = start.get(next_node % start.size());
                if(X[next_node] <= -max_wanted){
                    amount = -max_wanted; 
                }
                else{
                    amount = X[next_node];
                }
                X[next_node] -= amount;
                cur_node = next_node;
                last_start = cur_node;
                
            }
            //select sell node
            else{
                
                next_node = (int) Math.round(vector[i]);
                next_node = reachable_nodes.get(next_node % reachable_nodes.size());
                amount += X[next_node];
                nodeLoad[next_node] += X[next_node];
                cur_path.add(new Integer[]{cur_node, next_node});
                cur_amounts.add(X[next_node]);
                X[next_node] = 0;
                cur_node = next_node;
                
            }
            
            
            //nodeLoad vector is ready
            
        }
        
        //last path has ended
        //return unused
        if(last_start != -1){
            X[last_start] += amount;
        }
        
        if(cur_amounts == null || cur_amounts.isEmpty()){
            //nowhere to go
        }
        else {
            //track back the path and amounts
            cumulative_amount = 0;

            for(int p = cur_amounts.size()-1; p > -1; p--){
                cumulative_amount += cur_amounts.get(p);
                path_point = cur_path.get(p);
                pathLoad[path_point[0]][path_point[1]] += cumulative_amount;
            }

            cur_path = new ArrayList<>();
            cur_amounts = new ArrayList<>();

        }
        //pathLoad matrix is ready
        
        //count the profit
        for(int i = 0; i < nodeCount; i++){
            
            profit -= (nodeLoad[i]*cost);
            
            for(int j = 0; j < nodeCount; j++){
                profit += (pathLoad[i][j]*pathCost[i][j]);
                
                if(pathLoad[i][j] > 0){
                    
                    this.node_path.add(new Integer[]{i, j});
                    if(pathCreate[i][j] > 0){
                        profit += pathCreate[i][j];
                        this.built_path.add(new Integer[]{i, j});
                    }
                }
            } 
        }

        return profit;
        
    }
    
    private int getStartMaxAmount(List<Integer> s){
        
        if(s == null || s.isEmpty()){
            return 0;
        }
        
        int max = 0;
        
        for(Integer am : s){
            if(Math.abs(X[am]) > max){
                max = Math.abs(X[am]);
            }
        }
        
        return max;
        
    }
    
    private List<Integer> getStarts(int[] x){
        
        List<Integer> s = new ArrayList<>();
        for(int i = 0; i < x.length; i++) {
            if(x[i] < 0){
                s.add(i);
            }
        }
        
        return s;
        
    }
    
    private List<Integer> getReachableSellNodes(int curNode, int amount){
        
        List<Integer> list = new ArrayList<>();
        
        if(curNode == -1 || amount == 0){
            return list;
        }

        for(int i=0;i<nodeCount;i++){
            
            if(path[curNode][i] > -1 && X[i] <= Math.abs(amount) && X[i] >= 0){
                list.add(i);
            }
            
        }
        
        return list;
        
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
    
    private int getMaximumWanted(){
        
        int ret = Integer.MIN_VALUE;
        boolean empty = true;
        
        for(int i=0; i < X.length; i++){
            
            if(X[i] > 0 && X[i] > ret){
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

    public List<Integer[]> getNode_path() {
        return node_path;
    }

    public void setNode_path(List<Integer[]> node_path) {
        this.node_path = node_path;
    }

    public List<Integer[]> getBuilt_path() {
        return built_path;
    }

    public void setBuilt_path(List<Integer[]> built_path) {
        this.built_path = built_path;
    }

    public int[][] getPathLoad() {
        return pathLoad;
    }

    public void setPathLoad(int[][] pathLoad) {
        this.pathLoad = pathLoad;
    }

    public int[] getNodeLoad() {
        return nodeLoad;
    }

    public void setNodeLoad(int[] nodeLoad) {
        this.nodeLoad = nodeLoad;
    }

    public List<Integer> getStart() {
        return start;
    }

    public void setStart(List<Integer> start) {
        this.start = start;
    }
    
    public static void main(String[] args) throws Exception {
    
        double[] vector = new double[]{20.0, 5.742295694686435, 3.550907281722562, 17.87106177122086, 5.1430064971884235, 7.0200619695541935, 0.0, 13.316293443671984, 18.100779490047575, 7.189060480516068};
        TestFunction tf = new Network3();
        Integer[] pa;
        
        System.out.println("Profit: " + tf.fitness(vector));
        System.out.println("NodeLoad: " + Arrays.toString(((Network3)tf).getNodeLoad()));
        System.out.println("PathLoad: ");
        System.out.print("{");
        for(int a = 0; a < ((Network3)tf).getNodeCount(); a++){
            System.out.print("{");
            for(int b = 0; b < ((Network3)tf).getNodeCount(); b++){

                System.out.print(((Network3)tf).getPathLoad()[a][b]);

                if(b != ((Network3)tf).getNodeCount() - 1){
                    System.out.print(", ");
                }  

            }
            System.out.print("}");

            if(a != ((Network3)tf).getNodeCount() - 1){
                System.out.println(", ");
            } 
        }
        System.out.println("}");
        System.out.println("Path: ");
            System.out.print("{");
            for(int p = 0; p < ((Network3)tf).getNode_path().size(); p++) {
                pa = ((Network3)tf).getNode_path().get(p);
                System.out.print("{" + pa[0] + ", " + pa[1] + "}");
                
                if(p != ((Network3)tf).getNode_path().size()-1){
                    System.out.print(", ");
                }
                
            }
            System.out.println("}");
        
    }

    @Override
    public double[] optimumPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
