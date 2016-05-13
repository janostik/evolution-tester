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
 * Location of garbage facilitites in 2 regions.
 * 
 * @author wiki
 */
public class SpalovnyZlinJM implements TestFunction {

    double[][] adjM = new double[][]{{0,18.4,33.2,0,0,0,0,0,17.4,0,0,0,0,0,0,0,24.4,0,34.8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{18.4,0,45.2,0,0,0,0,0,29.4,0,0,0,0,0,0,0,32.8,0,45.,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{33.2,45.2,0,0,0,0,37.5,35.6,19.4,0,0,0,33.3,27.,20.3,9.5,0,0,32.8,0,25.1,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,22.3,30.2,0,0,0,22.3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,24.4,0,0,0,0,10.3,0,0,0,17.3,0,0,0,0,41.9,0,0,0,42.1,0,0,0,0,0,0},{0,0,0,22.3,0,0,43.,0,0,20.,0,0,0,0,44.8,0,0,26.3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,37.5,30.2,0,43.,0,0,0,39.4,23.6,0,22.5,0,47.2,36.1,0,0,0,0,15.,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,35.6,0,0,0,0,0,33.,0,0,11.6,21.,11.2,0,36.8,36.3,0,0,0,23.7,0,0,0,0,0,0,0,0,0,0,0,0,0},{17.4,29.4,19.4,0,0,0,0,33.,0,0,0,0,0,24.,0,0,11.4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,24.4,20.,39.4,0,0,0,53.2,0,0,0,30.6,0,0,21.9,0,0,52.6,0,0,45.1,0,51.4,0,31.8,46.5,0,0,0,0,0},{0,0,0,22.3,0,0,23.6,0,0,53.2,0,42.6,23.3,0,0,0,0,0,0,50.5,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,11.6,0,0,42.6,0,23.4,0,0,0,0,0,0,34.,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,33.3,0,0,0,22.5,21.,0,0,23.3,23.4,0,28.1,0,34.6,0,0,0,39.3,10.7,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,27.,0,0,0,0,11.2,24.,0,0,0,28.1,0,0,28.2,27.7,0,0,0,30.6,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,20.3,0,10.3,44.8,47.2,0,0,30.6,0,0,0,0,0,14.1,0,0,19.3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,9.5,0,0,0,36.1,36.8,0,0,0,0,34.6,28.2,14.1,0,0,0,31.8,0,26.9,0,0,0,0,0,0,0,0,0,0,0,0,0},{24.4,32.8,0,0,0,0,0,36.3,11.4,0,0,0,0,27.7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,26.3,0,0,0,21.9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,15.8,27.1,0,0,0,0,0},{34.8,45.,32.8,0,17.3,0,0,0,0,0,0,0,0,0,19.3,31.8,0,0,0,0,0,0,0,33.3,0,0,0,52.4,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,50.5,34.,39.3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,25.1,0,0,0,15.,23.7,0,52.6,0,0,10.7,30.6,0,26.9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,11.1,0,0,0,0,0,0,0,26.9,40.,32.1,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,11.1,0,16.5,0,17.6,0,0,0,58.3,0,31.7,0,17.3},{0,0,0,0,41.9,0,0,0,0,45.1,0,0,0,0,0,0,0,0,33.3,0,0,0,16.5,0,0,18.6,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,29.7,0,0,13.4,25.5,0,18.5,0,21.8},{0,0,0,0,0,0,0,0,0,51.4,0,0,0,0,0,0,0,0,0,0,0,0,17.6,18.6,29.7,0,0,19.7,32.,0,0,0,0,12.},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,14.8,0,27.5,0},{0,0,0,0,42.1,0,0,0,0,31.8,0,0,0,0,0,0,0,15.8,52.4,0,0,0,0,0,0,19.7,0,0,18.5,0,0,0,0,26.9},{0,0,0,0,0,0,0,0,0,46.5,0,0,0,0,0,0,0,27.1,0,0,0,0,0,0,13.4,32.,0,18.5,0,36.2,0,0,0,27.9},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,58.3,0,25.5,0,0,0,36.2,0,0,31.1,28.4,40.9},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,26.9,0,0,0,0,14.8,0,0,0,0,0,19.2,46.8},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,40.,31.7,0,18.5,0,0,0,0,31.1,0,0,18.7,14.3},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,32.1,0,0,0,0,27.5,0,0,28.4,19.2,18.7,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,17.3,0,21.8,12.,0,26.9,27.9,40.9,46.8,14.3,0,0}};
    double[] garbage_production = new double[]{14070.6,14343.6,119806.,19561.3,4147.45,18887.9,10201.5,8107.61,6250.03,16045.3,4836.13,6307.23,4056.93,6973.76,5520.2,19984.1,8029.2,9128.13,14495.3,24822.4,10501.,4842.76,7432.78,18157.3,5126.45,10345.5,10415.8,24179.2,15164.4,5119.98,12566.7,4706.77,14186.4,27447.6};
    double production_sum = 505767.09;
    int facility_count = 2;
    int[] number_of_cities = new int[]{34,34};
    double[] capacity_options = new double[]{100_000,200_000,300_000,400_000,500_000,600_000,700_000};
    
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
        }
        
        map.put("Facilities", facility_list);
        
        List<Double> facility_capacities = new ArrayList<>();
        
        //Capacities of facilities
        double capacities[] = new double[facility_count];
        double capacity_sum = 0;
        for(int i = facility_count; i < 2*facility_count; i++){
            capacities[i-facility_count] = capacity_options[((int) (vector[i] * capacity_options.length)) % capacity_options.length];
            facility_capacities.add(capacities[i-facility_count]);
            capacity_sum += capacities[i-facility_count];
        }
        
        map.put("Capacities", facility_capacities);
        
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
            map.put((i+1) + ". facility", facility_nodes.get(i));
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
        
        map.put("Used capacities", facility_capacities);
        
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
            
            map.put((i+1) + ". facility spanning tree", paths);
            
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
        return "Umisteni_Spaloven_Zlin_JM";
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        SpalovnyZlinJM sp = new SpalovnyZlinJM();
        double[] vector = new double[]{0.794518,0.0377281,0.41956,0.788069,0.80197,0.521636,0.33642,0.850001,0.695432,0.375628,0.957841,0.359868,0.0150438,0.958522,0.963681,0.225539,0.386704,0.961989,0.362569,0.837156,0.158392,0.941589,0.773301,0.808321,0.932717,0.543997,0.343726,0.464673,0.797852,0.481229,0.553551,0.944577,0.597523,0.395742,0.462471,0.322302,0.629435,0.725644};
        
        System.out.println(sp.fitness(vector));
        
    }
    
}
