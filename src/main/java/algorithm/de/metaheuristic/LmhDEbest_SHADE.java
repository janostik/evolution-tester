package algorithm.de.metaheuristic;

import algorithm.de.DEbest;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.NetworkIndividual;
import model.tf.Cec2015;
import model.tf.SNLs_SHADE_tf;
import model.tf.TestFunction;
import util.RandomUtil;

/**
 *
 * Testing class uses DE best to find the best ids vector for SNLs_SHADE.
 * 
 * @author wiki on 03/05/2016
 */
public class LmhDEbest_SHADE {

    private int random_seed_count;
    
    private TestFunction tf_master;
    private TestFunction tf_slave;
    private int dimension_master;
    private int dimension_slave;
    private int max_fes_master;
    private int max_fes_slave;
    private int H_slave;
    private int NPinit_slave;
    private int NPfinal_slave;
    private int NP_master;
    private double F_master;
    private double CR_master;
    private util.random.Random rndGenerator_master;
    
    
    
    public void run() throws Exception{
        
        this.random_seed_count = 30;
        
        for(int funcIt = 1; funcIt < 16; funcIt++) {
        
            /**
             * Slave settings - SHADE
             */
            this.dimension_slave = 10;
            this.tf_slave = new Cec2015(this.dimension_slave, funcIt);
            this.NPinit_slave = 100;
            this.NPfinal_slave = 20;
            this.max_fes_slave = 100 * this.NPinit_slave;
            this.H_slave = 10;

            /**
             * Master settings - DE best
             */
            this.F_master = 0.5;
            this.CR_master = 0.8;
            this.NP_master = 10;
            this.dimension_master = this.NPinit_slave - this.NPfinal_slave;
            this.max_fes_master = 100 * this.NP_master;
            this.rndGenerator_master = new util.random.UniformRandom();

            /**
             * Other variables
             */
            long seed;
            DEbest master;
            List<List<NetworkIndividual>> stats = new ArrayList<>();

            //Cycle through seeds
            for(int seedIt = 0; seedIt < this.random_seed_count; seedIt++){

                seed = this.getRandomSeed();
                this.tf_master = new SNLs_SHADE_tf(seed, tf_slave, dimension_slave, max_fes_slave, H_slave, NPinit_slave, NPfinal_slave);

                master = new DEbest(dimension_master, NP_master, max_fes_master, tf_master, rndGenerator_master, F_master, CR_master);
                master.runAlgorithm();

                System.out.println("Seed " + (seedIt+1) + ": " + master.getBest().fitness);

                stats.add((List<NetworkIndividual>) ((SNLs_SHADE_tf) this.tf_master).getDeadList(master.getBest()));

            }

            List<Integer> fitness_order = new ArrayList<>();
            List<Integer> centrality_order = new ArrayList<>();

            for(List<NetworkIndividual> list : stats) {

                list.stream().map((ni) -> {
                    fitness_order.add(ni.position_fitness);
                    return ni;
                }).forEach((ni) -> {
                    centrality_order.add(ni.position_centrality);
                });

            }

            /**
             * Statistics printout
             */

            try {
                PrintWriter pw1 = new PrintWriter("C:\\Users\\wiki\\Documents\\CentralityStats\\CEC2015\\fitness_order_" + funcIt + ".txt");
                PrintWriter pw2 = new PrintWriter("C:\\Users\\wiki\\Documents\\CentralityStats\\CEC2015\\centrality_order_" + funcIt + ".txt");

                pw1.print("{");
                pw2.print("{");

                for(int i = 0; i < fitness_order.size(); i++){

                    pw1.print(fitness_order.get(i).intValue());
                    pw2.print(centrality_order.get(i).intValue());

                    if(i != fitness_order.size()-1){
                        pw1.print(",");
                        pw2.print(",");
                    }

                }

                pw1.print("}");
                pw2.print("}");

                pw1.close();
                pw2.close();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(LmhDEbest_SHADE.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }

        
    }
    
    /**
     * Gets random seed from range 0 to 100,000,000
     * 
     * @return 
     */
    private long getRandomSeed(){
        
        return RandomUtil.nextInt(100_000_000);
        
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        LmhDEbest_SHADE mh = new LmhDEbest_SHADE();
        mh.run();
        
    }
    
}
