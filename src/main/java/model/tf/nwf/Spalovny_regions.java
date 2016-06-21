
package model.tf.nwf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Individual;
import model.tf.TestFunction;
import util.Dijsktra;
import util.Dijsktra.Vertex;
import util.IndividualUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 *
 * This class represents the basic solution of incinerator facility locations
 * Each region, only 1 incinerator
 * 
 * @author wiki on 18/05/2016
 */
public class Spalovny_regions implements TestFunction {

    double[][] adjM = new double[][]{{0, 11},{11 ,0}};
    double[] garbage_production = new double[]{421456.20,4618.37};
    double production_sum = 10_000;
    int facility_count = 1;
    int[] number_of_cities = new int[]{2};
    double[] capacity_options = new double[]{50_000, 100_000, 200_000, 300_000, 400_000, 500_000, 600_000, 750_000, 1_000_000, 1_500_000};
    
    public Map<String, List> getOutput(double[] vector) {
        
        Map<String, List> map = new HashMap<>();
        List<Integer> facility_list = new ArrayList<>();
        
        double fitness = 0;
        
        //Locations of facilities
        int[] locations = new int[facility_count];
        int sum = 0;
        List<List<Integer>> facility_nodes = new ArrayList<>();
        for(int i = 0; i < facility_count; i++) {
            facility_nodes.add(new ArrayList<>());
            locations[i] = (((int) (vector[i] * number_of_cities[i])) % number_of_cities[i]) + sum;
            facility_list.add(locations[i]);
            sum += number_of_cities[i];
        }
        
        map.put("facilities", facility_list);
        
        List<Double> facility_capacities = new ArrayList<>();
        
        //Capacities of facilities
        double capacities[] = new double[facility_count];
        double capacity_sum = 0;
        for(int i = facility_count; i < 2*facility_count; i++){
            capacities[i-facility_count] = capacity_options[((int) (vector[i] * capacity_options.length)) % capacity_options.length];
            facility_capacities.add(capacities[i-facility_count]);
            capacity_sum += capacities[i-facility_count];
        }
        
        map.put("capacities", facility_capacities);
        
        //Capacity is not enough
        if(capacity_sum < production_sum){
            return null;
        }
        
        //Where to go from nodes
        int facility_index;
        for(int i = 2*facility_count; i < vector.length; i++) {
            facility_index = ((int) (vector[i] * facility_count)) % facility_count;
            facility_nodes.get(facility_index).add(i - 2*facility_count);
        }
        
        for(int i = 0; i < facility_nodes.size(); i++) {
            map.put("facility[[" + (i+1) + "]]", facility_nodes.get(i));
        }
        
        facility_capacities = new ArrayList<>();
        //Chceck capacity of single facilities and if not over, add remaining capacity to fitness
        double garbage_sum;
        for(int i = 0; i < facility_count; i++) {
            garbage_sum = 0;
            for(Integer node_index : facility_nodes.get(i)){
                garbage_sum += garbage_production[node_index];
            }
            facility_capacities.add(garbage_sum);
            if(garbage_sum > capacities[i]){
                return null;
            }
            else {
                fitness += capacities[i] - garbage_sum;
            }
        }
        
        map.put("usedCapacities", facility_capacities);
        
        //Compute the distances and update the fitness
        List<List<Vertex>> paths;
        Dijsktra.Vertex[] vertices;
        double path_length;
        for(int i = 0; i < facility_count; i++) {
            
            paths = new ArrayList<>();
            
            vertices = Dijsktra.getShortestPathsFromAdjacencyMatrix(adjM, locations[i]);
            for(Integer node_index : facility_nodes.get(i)) {
                path_length = vertices[node_index].minDistance;
                if(path_length == Double.POSITIVE_INFINITY){
                    return null;
                }
                fitness += (path_length * garbage_production[node_index]);
                paths.add(Dijsktra.getShortestPathTo(vertices[node_index]));
            }
            
            map.put("span[[" + (i+1) + "]]", paths);
            
        }
        
        return map;
    }
    
    @Override
    public double fitness(double[] vector) {
        
        double fitness = 0;
        
        //Locations of facilities
        int[] locations = new int[facility_count];
        int sum = 0;
        List<List<Integer>> facility_nodes = new ArrayList<>();
        for(int i = 0; i < facility_count; i++) {
            facility_nodes.add(new ArrayList<>());
            locations[i] = (((int) (vector[i] * number_of_cities[i])) % number_of_cities[i]) + sum;
            sum += number_of_cities[i];
        }
        
        //Capacities of facilities
        double capacities[] = new double[facility_count];
        double capacity_sum = 0;
        for(int i = facility_count; i < 2*facility_count; i++){
            capacities[i-facility_count] = capacity_options[((int) (vector[i] * capacity_options.length)) % capacity_options.length];
            capacity_sum += capacities[i-facility_count];
        }
        
        //Capacity is not enough
        if(capacity_sum < production_sum){
            return Math.pow(10, 30);
        }
        
        //Where to go from nodes
        int facility_index;
        for(int i = 2*facility_count; i < vector.length; i++) {
            facility_index = ((int) (vector[i] * facility_count)) % facility_count;
            facility_nodes.get(facility_index).add(i - 2*facility_count);
        }
        
        //Chceck capacity of single facilities and if not over, add remaining capacity to fitness
        double garbage_sum;
        for(int i = 0; i < facility_count; i++) {
            garbage_sum = 0;
            for(Integer node_index : facility_nodes.get(i)){
                garbage_sum += garbage_production[node_index];
            }
            if(garbage_sum > capacities[i]){
                return Math.pow(10, 30);
            }
            else {
                fitness += capacities[i] - garbage_sum;
            }
        }
        
        //Compute the distances and update the fitness
        Vertex[] vertices;
        double pathe_length;
        for(int i = 0; i < facility_count; i++) {
            
            vertices = Dijsktra.getShortestPathsFromAdjacencyMatrix(adjM, locations[i]);
            for(Integer node_index : facility_nodes.get(i)) {
                pathe_length = vertices[node_index].minDistance;
                if(pathe_length == Double.POSITIVE_INFINITY){
                    return Math.pow(10, 30);
                }
                fitness += (pathe_length * garbage_production[node_index]);
            }
            
        }
        
        return fitness;
    }

    @Override
    public double fitness(Individual individual) {
        
        
        return fitness(individual.vector);
    }

    @Override
    public void constrain(Individual individual) {
        IndividualUtil.randIfOutOfBounds(individual, 0, 1);
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] vector = new double[dim];
        Random rnd = new UniformRandom();
        for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(0, 1);
        return vector;
    }

    @Override
    public double fixedAccLevel() {
        return 10^-7;
    }

    @Override
    public double optimum() {
        return 0;
    }

    @Override
    public double max(int dim) {
        return 1;
    }

    @Override
    public double min(int dim) {
        return 0;
    }

    @Override
    public String name() {
        return "Spalovny_regions";
    }
    
}
