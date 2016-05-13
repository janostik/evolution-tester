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
 * Location of garbage facilitites in 3 regions.
 * 
 * Only 2 facilities - random positions
 * 
 * @author wiki
 */
public class Spalovny3kraje implements TestFunction {

    double[][] adjM = new double[][]{{0,18.40,33.20,0,0,0,0,0,17.40,0,0,0,0,0,0,0,24.40,0,34.80,0,0,0,0,41.90,0,0,0,0,48.50,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{18.40,0,45.20,0,0,0,0,0,29.40,0,0,0,0,0,0,0,32.80,0,45.00,0,0,0,0,29.30,0,0,48.80,0,40.50,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{33.20,45.20,0,0,0,0,37.50,35.60,19.40,0,0,0,33.30,27.00,20.30,9.50,0,0,32.80,0,25.10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,22.30,30.20,0,0,0,22.30,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,24.40,0,0,0,0,10.30,0,0,0,17.30,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,41.90,0,0,0,42.10,0,0,0,0,0,0},{0,0,0,22.30,0,0,43.00,0,0,20.00,0,0,0,0,44.80,0,0,26.30,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,37.50,30.20,0,43.00,0,0,0,39.40,23.60,0,22.50,0,47.20,36.10,0,0,0,0,15.00,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,35.60,0,0,0,0,0,33.00,0,0,11.60,21.00,11.20,0,36.80,36.30,0,0,0,23.70,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{17.40,29.40,19.40,0,0,0,0,33.00,0,0,0,0,0,24.00,0,0,11.40,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,24.40,20.00,39.40,0,0,0,53.20,0,0,0,30.60,0,0,21.90,0,0,52.60,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,45.10,0,51.40,0,31.80,46.50,0,0,0,0,0},{0,0,0,22.30,0,0,23.60,0,0,53.20,0,42.60,23.30,0,0,0,0,0,0,50.50,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,11.60,0,0,42.60,0,23.40,0,0,0,0,0,0,34.00,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,33.30,0,0,0,22.50,21.00,0,0,23.30,23.40,0,28.10,0,34.60,0,0,0,39.30,10.70,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,27.00,0,0,0,0,11.20,24.00,0,0,0,28.10,0,0,28.20,27.70,0,0,0,30.60,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,20.30,0,10.30,44.80,47.20,0,0,30.60,0,0,0,0,0,14.10,0,0,19.30,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,9.50,0,0,0,36.10,36.80,0,0,0,0,34.60,28.20,14.10,0,0,0,31.80,0,26.90,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{24.40,32.80,0,0,0,0,0,36.30,11.40,0,0,0,0,27.70,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,26.30,0,0,0,21.90,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,80.20,0,0,0,0,0,0,0,0,0,0,0,15.80,27.10,0,0,0,0,0},{34.80,45.00,32.80,0,17.30,0,0,0,0,0,0,0,0,0,19.30,31.80,0,0,0,0,0,0,0,0,0,0,0,0,25.80,49.60,0,0,0,0,0,0,33.30,0,0,0,52.40,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,50.50,34.00,39.30,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,25.10,0,0,0,15.00,23.70,0,52.60,0,0,10.70,30.60,0,26.90,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,12.20,0,0,42.60,0,0,0,0,0,0,19.50,0,0,0,0,0,0,0,0,24.30,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,47.60,0,57.40,0,0,0,0,0,0,0,0,0,0,0,0,0},{41.90,29.30,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,23.90,35.20,29.90,23.00,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,12.20,0,0,0,0,0,29.20,0,14.80,0,0,0,0,20.70,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,23.90,0,0,17.80,19.60,28.90,0,19.70,0,9.60,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,48.80,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,35.20,0,17.80,0,34.00,0,0,0,0,18.00,14.30,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,42.60,0,29.90,29.20,19.60,34.00,0,20.50,23.30,17.90,0,25.70,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{48.50,40.50,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,80.20,25.80,0,0,0,0,23.00,0,28.90,0,20.50,0,27.40,0,0,0,0,0,0,32.00,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,49.60,0,0,0,0,0,14.80,0,0,23.30,27.40,0,0,0,0,0,19.70,20.80,22.40,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,19.70,0,17.90,0,0,0,0,15.60,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,47.60,0,0,0,0,0,0,0,0,0,29.10,15.40,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9.60,18.00,25.70,0,0,15.60,29.10,0,24.90,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,57.40,0,0,0,14.30,0,0,0,0,15.40,24.90,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,19.50,0,0,20.70,0,0,0,0,19.70,0,0,0,0,0,11.10,0,0,0,0,0,0,0,26.90,40.00,32.10,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,20.80,0,0,0,0,11.10,0,16.50,0,17.60,0,0,0,58.30,0,31.70,0,17.30},{0,0,0,0,41.90,0,0,0,0,45.10,0,0,0,0,0,0,0,0,33.30,0,0,0,0,0,0,0,0,0,32.00,22.40,0,0,0,0,0,16.50,0,0,18.60,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,29.70,0,0,13.40,25.50,0,18.50,0,21.80},{0,0,0,0,0,0,0,0,0,51.40,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,17.60,18.60,29.70,0,0,19.70,32.00,0,0,0,0,12.00},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,14.80,0,27.50,0},{0,0,0,0,42.10,0,0,0,0,31.80,0,0,0,0,0,0,0,15.80,52.40,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,19.70,0,0,18.50,0,0,0,0,26.90},{0,0,0,0,0,0,0,0,0,46.50,0,0,0,0,0,0,0,27.10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,13.40,32.00,0,18.50,0,36.20,0,0,0,27.90},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,58.30,0,25.50,0,0,0,36.20,0,0,31.10,28.40,40.90},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,24.30,0,0,0,0,0,0,0,0,0,0,0,0,26.90,0,0,0,0,14.80,0,0,0,0,0,19.20,46.80},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,40.00,31.70,0,18.50,0,0,0,0,31.10,0,0,18.70,14.30},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,32.10,0,0,0,0,27.50,0,0,28.40,19.20,18.70,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,17.30,0,21.80,12.00,0,26.90,27.90,40.90,46.80,14.30,0,0}};
    double[] garbage_production = new double[]{14070.61,14343.59,119805.75,19561.32,4147.45,18887.91,10201.53,8107.61,6250.03,16045.27,4836.13,6307.23,4056.93,6973.76,5520.20,19984.11,8029.20,9128.13,14495.34,24822.35,10501.02,10695.23,9435.95,2755.95,5084.76,7126.47,6041.74,50918.85,29322.16,26006.27,8264.66,23361.72,7493.42,10543.17,4842.76,7432.78,18157.25,5126.45,10345.50,10415.80,24179.22,15164.39,5119.98,12566.70,4706.77,14186.38,27447.64};
    double production_sum = 702817.4400;
    int facility_count = 2;
    int[] number_of_cities = new int[]{47, 47};
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
        
        Spalovny3kraje sp = new Spalovny3kraje();
        double[] vector = new double[]{0.6022221310054365, 0.04835235318142894, 0.5886418500290493, 0.3395585995357688, 0.6051062024026252, 0.18608106278509623, 0.7662308367803593, 0.8883942964565209, 0.24389946442094124, 0.47319722283934573, 0.7856971887497768, 0.7807164179239763, 0.9865948354454808, 0.4575191005980104, 0.6863698425789508, 0.6124404231342073, 0.8782834620278555, 0.9104086570369905, 0.6867881237761224, 0.874940221564706, 0.7686868786188944, 0.4303523609529298, 0.2678461870438984, 0.13721790254993244, 0.7545353262767387, 0.03975861363003273, 0.21162662651457337, 0.31588787056058115, 0.14119946918452042, 0.018618899993290316, 0.06333750993138235, 0.0415102328005567, 0.03609736466274781, 0.15674086439286394, 0.13629747579465407, 0.2393543534373827, 0.10533626665823884, 0.16794831190818216, 0.2715252282645999, 0.14498465661881918, 0.11474785558739226, 0.11642687387719391, 0.015190966679944268, 0.3139736474943954, 0.3862643392460068, 0.22833116925566233, 0.3465200234298986, 0.23116867889483877, 0.1743769818722519, 0.18562234715293605, 0.0963419748496468};
        
        System.out.println(sp.fitness(vector));
        
        Map<String, List> map = sp.getOutput(vector);
            
        System.out.println("=================================");
        String line;

        for(Map.Entry<String,List> entry : map.entrySet()){
            line = "";
            System.out.println(entry.getKey());
            line += "{";
//                System.out.print("{");
            for(int pup = 0; pup < entry.getValue().size(); pup++){
//                    System.out.print(entry.getValue().get(pup));
                line += entry.getValue().get(pup);
                if(pup != entry.getValue().size()-1){
//                       System.out.print(","); 
                   line += ",";
                }
            }
//                System.out.println("}");
            line += "}";
            line = line.replace("[", "{");
            line = line.replace("]", "}");
            System.out.println(line);

        }

        System.out.println("=================================");
        
    }
    
}
