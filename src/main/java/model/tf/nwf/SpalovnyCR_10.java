package model.tf.nwf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.tf.Cec2015;
import util.Dijsktra;

/**
 *
 * Location of garbage facilitites in all regions of Czech republic.
 * 
 * 10 facilities - random positions
 * prize for incineration is not static - -x/3 000 000 + 1
 * 
 * @author wiki on 29/06/2016
 */
public class SpalovnyCR_10 extends Spalovny_random {

    double[] rnd_shift = new double[]{0.428879, 0.966152, 0.577329, 0.00255957, 0.975009, 0.200876, 0.508707, 0.695565, 0.092039, 0.473545};

    public SpalovnyCR_10() {
        
        super.adjM = new double[206][206];
        super.garbage_production = new double[]{421456.20,4618.37,40328.62,10267.64,6054.39,12617.38,5237.12,4892.46,11682.76,8108.45,7039.16,12327.33,26770.87,5010.74,8555.13,4609.10,5111.39,3988.39,14070.61,14343.59,119805.75,19561.32,4147.45,18887.91,10201.53,8107.61,6250.03,16045.27,4836.13,6307.23,4056.93,6973.76,5520.20,19984.11,8029.20,9128.13,14495.34,24822.35,10501.02,8511.10,16835.81,25171.38,3399.31,9079.82,7454.33,21032.18,4472.56,7215.61,6201.28,6119.69,51330.69,4445.92,15210.71,6734.93,15135.70,2793.67,3637.41,4976.93,9159.88,21280.52,9544.94,24765.51,8079.11,14040.02,5842.87,50333.33,9278.74,5987.36,6045.85,8719.04,3054.00,6051.64,11312.84,9453.26,8867.39,5051.98,32099.15,9986.36,27737.28,11916.22,5641.20,21873.89,16721.48,5193.34,12028.95,12322.73,5130.85,34858.49,12588.02,82707.62,1259.73,14886.78,7083.38,10695.23,9435.95,2755.95,5084.76,7126.47,6041.74,50918.85,29322.16,26006.27,8264.66,23361.72,7493.42,10543.17,5522.79,5866.35,4096.67,23156.53,2795.28,5124.32,6425.70,7346.90,35345.02,4735.35,8529.37,7744.97,8204.27,8258.16,8342.36,3092.94,9674.67,4317.68,4435.00,15640.78,8605.63,2680.56,13789.02,60292.82,6006.31,17997.80,6725.98,5591.85,7701.58,11773.66,21185.92,17639.47,41796.62,7931.37,44712.79,10010.61,7328.53,11285.26,34601.82,34060.59,12471.84,20311.27,8569.94,17996.19,36791.19,4792.82,14507.71,16659.05,12347.36,23618.15,18237.74,24668.06,7006.08,12956.23,10422.35,4519.41,5779.48,22885.43,22103.19,14204.32,23938.29,13171.29,13841.54,9880.92,21436.48,6069.16,7595.05,10984.90,35240.52,29269.43,7066.70,9149.82,4840.89,17531.07,5802.00,5711.93,25640.05,5779.25,2753.40,4690.38,3137.53,13686.97,5994.54,3524.22,19105.03,10862.11,12096.55,4842.76,7432.78,18157.25,5126.45,10345.50,10415.80,24179.22,15164.39,5119.98,12566.70,4706.77,14186.38,27447.64};
        super.production_sum = 3.24217685000*Math.pow(10,6);
        super.facility_count = 10;
        super.number_of_cities = new int[]{206, 206, 206, 206, 206, 206, 206, 206, 206, 206};
        super.capacity_options = new double[]{50_000, 100_000, 200_000, 300_000, 400_000, 500_000, 600_000, 750_000, 1_000_000, 1_500_000};
        
        String location = "/adjM_spalovnyCR.csv";
        URL resource = Cec2015.class.getResource(location);
        File fpt = null;
        try {
            fpt = Paths.get(resource.toURI()).toFile();
        } catch (URISyntaxException ex) {
            Logger.getLogger(SpalovnyCR_10.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";
        int rowIt = 0;

	try {

		br = new BufferedReader(new FileReader(fpt));
		while ((line = br.readLine()) != null) {

		        // use comma as separator
			String[] row = line.split(cvsSplitBy);
                        
                        for(int columnIt = 0; columnIt < row.length; columnIt++) {
                            this.adjM[rowIt][columnIt] = Double.parseDouble(row[columnIt]);
                        }
                        rowIt++;

		}

	} catch (FileNotFoundException e) {
            Logger.getLogger(SpalovnyCR_10.class.getName()).log(Level.SEVERE, null, e);
	} catch (IOException e) {
            Logger.getLogger(SpalovnyCR_10.class.getName()).log(Level.SEVERE, null, e);
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
                            Logger.getLogger(SpalovnyCR_10.class.getName()).log(Level.SEVERE, null, e);
			}
		}
	}
        
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
        
        //Check whether the incinerators are in different regions
        Set region_indexes = new HashSet<>();
        int city_index;
        int[] region_city_count = new int[]{1,17,21,7,15,10,22,13,15,15,26,16,15,13};
        for(int i = 0; i < locations.length; i++) {
            city_index = locations[i];
            for(int j = 0; j < region_city_count.length; j++) {
                city_index -= region_city_count[j];
                if(city_index < 0){
                    region_indexes.add(j);
                    break;
                }
            }
        }
        if(region_indexes.size() != this.facility_count){
            return Math.pow(10, 30);
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
                fitness += ((-garbage_sum/3000000.0) + 0.5 + rnd_shift[i]);
            }
        }
        
        //Compute the distances and update the fitness
        Dijsktra.Vertex[] vertices;
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
    public String name() {
        return "Umisteni_Spaloven_CR_10";
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        SpalovnyCR_10 sp = new SpalovnyCR_10();
        double[] vector = new double[206+2*10];
        
        System.out.println(sp.fitness(vector));
        
        Map<String, List> map = sp.getOutput(vector);
            
        System.out.println("=================================");
        String line;

        if(map == null) {
            return;
        }
        
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
