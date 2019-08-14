package model.tf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 *
 * Testing cutting stock 1D
 * Solver
 * 
 * Initial solution - longest first + shortest first
 * 
 * User defined number of random solutions is generated next to obtain the best result.
 * 
 * @author wikki on 13/08/2019
 */
public class CuttingStock1D_solver_clean{

    /**
     * Wiki example
     */
    protected int[][] base_order = {{1380,22},{1520,25},{1560,12},{1710,14},{1820,18},{1880,18},{1930,20},{2000,10},{2050,12},{2100,14},{2140,16},{2150,18},{2200,20}};
    protected int[] base_stock = {5600};
    protected int cut_through = 0;
    
    protected int[][] order;
    protected int[] stock;
    
    protected double waste;
    protected HashMap<Integer, Integer> stock_used;
    protected HashMap<List, int[]> patterns;
    protected double rodsUs;

    protected int dim;

    public CuttingStock1D_solver_clean() {
    }

    public CuttingStock1D_solver_clean(int[][] order, int[] stock, int cut_through) {
        this.base_order = order;
        this.base_stock = stock;
        this.cut_through = cut_through;
        
    }
   
    /**
     * Initial solution with max lenght first.
     * 
     * @return fitness
     */
    protected double getMaxSolve() {
        
        /**
         * Initial solve - select longest possible option first
         */
        this.order = new int[this.base_order.length][];
        for (int i = 0; i < this.base_order.length; i++) {
            this.order[i] = Arrays.copyOf(this.base_order[i], this.base_order[i].length);
        }
        this.stock = this.base_stock.clone();
        
        this.stock_used = new HashMap<>();
        for(int i = 0; i < stock.length; i++) {
            this.stock_used.put(i, 0);
        }

        this.waste = 0;
        
        this.patterns = new HashMap<>();
        
        double rodsU = 0;
        int remaining_length = 0;
        int rod_index = 0;
        int index;
        List<Integer> pattern = new ArrayList<>();
        List<Integer> choices;
        int[] single_used_rods;
        int[] real_used_rods;
        
        while(true) {

            choices = this.getPossibleChoicesNumber(remaining_length, pattern);
            
            /**
             * Nothing can be added to current pattern
             */
            if(choices.isEmpty()) {
                
                if(!pattern.isEmpty()) {
                    single_used_rods = this.countUsedRods(pattern, rod_index);
                    
                    real_used_rods = single_used_rods;
            
                    this.updateRemainingOrder(pattern, real_used_rods[1]);
                    
                    this.stock_used.put(real_used_rods[0], this.stock_used.getOrDefault(real_used_rods[0], 0) + real_used_rods[1]);
                    this.patterns.put(pattern,real_used_rods);

                    rodsU += real_used_rods[1];
                    this.waste += remaining_length * real_used_rods[1];

                    pattern = new ArrayList<>();
                    remaining_length = 0;
                    
                }
                else {
                    rod_index = 0;
                    remaining_length = this.stock[rod_index];
                }
                
                /**
                 * Not the last piece
                 */
                if(checkRemainingOrder()) {
                    this.waste += remaining_length;
                    break; 
                }
                
            }
            else {
                index = this.getLongestChoiceNumber(choices);
                remaining_length -= (this.order[index][0] + this.cut_through);
                pattern.add(index);
            }

        }

        this.rodsUs = rodsU;

        return rodsU + this.countLeftToCut() + this.waste + this.patterns.size();
        
    }
    
    /**
     * Initial solution with min length first.
     * 
     * @return fitness
     */
    protected double getMinSolve() {
        
        /**
         * Initial solve - select longest possible option first
         */
        this.order = new int[this.base_order.length][];
        for (int i = 0; i < this.base_order.length; i++) {
            this.order[i] = Arrays.copyOf(this.base_order[i], this.base_order[i].length);
        }
        this.stock = this.base_stock.clone();
        
        this.stock_used = new HashMap<>();
        for(int i = 0; i < stock.length; i++) {
            this.stock_used.put(i, 0);
        }

        this.waste = 0;
        
        this.patterns = new HashMap<>();
        
        double rodsU = 0;
        int remaining_length = 0;
        int rod_index = 0;
        int index;
        List<Integer> pattern = new ArrayList<>();
        List<Integer> choices;
        int[] single_used_rods;
        int[] real_used_rods;
        
        while(true) {

            choices = this.getPossibleChoicesNumber(remaining_length, pattern);
            
            /**
             * Nothing can be added to current pattern
             */
            if(choices.isEmpty()) {
                
                if(!pattern.isEmpty()) {
                    single_used_rods = this.countUsedRods(pattern, rod_index);
                    
                    real_used_rods = single_used_rods;
            
                    this.updateRemainingOrder(pattern, real_used_rods[1]);
                    
                    this.stock_used.put(real_used_rods[0], this.stock_used.getOrDefault(real_used_rods[0], 0) + real_used_rods[1]);
                    this.patterns.put(pattern,real_used_rods);

                    rodsU += real_used_rods[1];
                    this.waste += remaining_length * real_used_rods[1];

                    pattern = new ArrayList<>();
                    remaining_length = 0;
                    
                }
                else {
                    rod_index = 0;
                    remaining_length = this.stock[rod_index];
                }
                
                /**
                 * Not the last piece
                 */
                if(checkRemainingOrder()) {
                    this.waste += remaining_length;
                    break; 
                }
                
            }
            else {
                index = this.getShortestChoiceNumber(choices);
                remaining_length -= (this.order[index][0] + this.cut_through);
                pattern.add(index);
            }

        }

        this.rodsUs = rodsU;

        return rodsU + this.countLeftToCut() + this.waste + this.patterns.size();
        
    }
    
    /**
     * 
     * Gets initial solution
     * Tries both max solve and min solve and returns better one
     * When equal, returns max first solution
     * 
     * @return 
     */
    protected double initSolve() {
        
        int pieces = 0;
        double avg_length = 0;
        int avg_pattern_length, pattern_count;
        for(int i = 0; i < this.base_order.length; i++) {
            
            pieces += this.base_order[i][1];
            avg_length += (this.base_order[i][0] * this.base_order[i][1]);
            
        }
        avg_length = avg_length / pieces;
        avg_pattern_length = (this.base_stock[0] / (int) (avg_length));
        pattern_count = pieces / avg_pattern_length;
        
        this.dim = (int)((pattern_count * avg_pattern_length)*1.5) + pattern_count;
        
        if(this.getMaxSolve() > this.getMinSolve()) {
            return this.getMinSolve();
        }
        else {
            return this.getMaxSolve();
        }

    }
    
    /**
     * Main function for getting the result.
     * 
     * @param tries - number of generated random solutions
     * @return fitness
     */
    public double solve(int tries) {
        
        /**
         * Initial solution
         */
        double ret_value = this.initSolve(); 
        
        /**
         * Added random solutions
         */
        double vector[], best[] = null;
        double value;
        boolean change = false;
        for(int i = 0; i < tries; i++) {
            
            vector = this.generateTrial(this.dim);
            value = this.fitness(vector);
            if(value <= ret_value) {
                best = vector.clone();
                ret_value = value;
                change = true;
            }
            
        }
      
        /**
         * To generate text solution.
         */
        if(change) {
            System.out.println(this.getNiceVector(best));
            this.fitness(best);
        }
        else {
            this.initSolve();
        }
        
        return ret_value;
    }
    
    /**
     * Aux method for vector printout with 4 decimal digits precision
     * 
     * @param vector
     * @return 
     */
    protected String getNiceVector(double[] vector) {
        
        String out = "";
        out += "{";
        
        for(int i = 0; i < vector.length; i++) {
            out += String.format(Locale.US, "%.4f", vector[i]);
            if(i != vector.length-1)
                out +=", ";
        }
        
        out += "}";
        
        return out;
    }

    /**
     * Evaluates a vector solution.
     * 
     * @param vector
     * @return fitness
     */
    public double fitness(double[] vector) {

        this.order = new int[this.base_order.length][];
        
        for (int i = 0; i < this.base_order.length; i++) {
            this.order[i] = Arrays.copyOf(this.base_order[i], this.base_order[i].length);
        }
        this.stock = this.base_stock.clone();
        
        this.stock_used = new HashMap<>();
        for(int i = 0; i < stock.length; i++) {
            this.stock_used.put(i, 0);
        }

        this.waste = 0;
        
        this.patterns = new HashMap<>();
        
        double rodsU = 0;
        int remaining_length = 0;
        int rod_index = 0;
        
        int index;
        List<Integer> pattern = new ArrayList<>();
        List<Integer> choices;
        int[] single_used_rods;
        int[] real_used_rods;
        int i = 0;
        
        while(i < vector.length) {

            choices = this.getPossibleChoicesNumber(remaining_length, pattern);
            
            /**
             * Nothing can be added to current pattern
             */
            if(choices.isEmpty()) {
                
                if(!pattern.isEmpty()) {
                    single_used_rods = this.countUsedRods(pattern, rod_index);
                    
                    real_used_rods = single_used_rods;
            
                    this.updateRemainingOrder(pattern, real_used_rods[1]);
                    
                    this.stock_used.put(real_used_rods[0], this.stock_used.getOrDefault(real_used_rods[0], 0) + real_used_rods[1]);
                    this.patterns.put(pattern,real_used_rods);

                    rodsU += real_used_rods[1];
                    this.waste += remaining_length * real_used_rods[1];

                    pattern = new ArrayList<>();
                    remaining_length = 0;
                    
                }
                else {
                    rod_index = this.getIndex(vector[i], stock.length);
                    i++;
                    remaining_length = this.stock[rod_index];
                }
                
                /**
                 * Not the last piece
                 */
                if(checkRemainingOrder()) {
                    this.waste += remaining_length;
//                    System.out.println("Dim: " + i);
                    break; 
                }
                
            }
            else {
                index = choices.get(this.getIndex(vector[i], choices.size()));
                i++;
                remaining_length -= (this.order[index][0] + this.cut_through);
                pattern.add(index);
            }

        }

        /**
         * Dimension was too low to generate enough patterns to fulfill th order
         */
        if(!checkRemainingOrder()) {
            System.err.println("Low dimension");
        }
        
        this.rodsUs = rodsU;

        return rodsU + this.countLeftToCut() + this.waste + this.patterns.size();
    }
    
    /**
     * Evaluates the length of all uncutted pieces.
     * 
     * @return 
     */
    protected double countLeftToCut(){
        
        double length = 0;
        
        for(int i = 0; i < this.order.length; i++) {
            length += this.order[i][0]*this.order[i][1];
        }
        
        return length;
        
    }
    
    /**
     * Generates a String printout of a last evaluated solution
     * 
     * @return String
     */
    public String writeDownPatterns() {
        
        int index = 0;
        String result = "";
        
        double sum = 0;
        for(int i = 0; i < this.base_order.length; i++) {
            sum += (this.base_order[i][0]*this.base_order[i][1]);
        }
        
        result += "Length Sum: " + sum + " mm\n";
        result += "Solution Length: " + (this.rodsUs * this.stock[0]) + " mm\n";
        result += "Cut Waste: " + ((this.rodsUs * this.stock[0]) - sum - this.waste) + " mm (" + String.format(Locale.US, "%.3f",(((this.rodsUs * this.stock[0]) - sum - this.waste)/(this.rodsUs * this.stock[0]))*100) + "%)\n";
        result += "Waste: " + this.waste + " mm (" + String.format(Locale.US, "%.3f",(this.waste/(this.rodsUs * this.stock[0]))*100) + "%)\n===================\n";
        
        for(List<Integer> pattern : this.patterns.keySet()) {
            
            result += index + ". pattern: ";
            for(int p : pattern) {
                result += this.base_order[p][0] + " ";
            }
            
            result += " - ";

            result += this.stock[this.patterns.get(pattern)[0]] + " " + this.patterns.get(pattern)[1] + "x ";

            result += "\n";
            
            index++;
            
        }
        
        result += "===================\n";
        result += "Rods: ";
        
        for(Integer stock : this.stock_used.keySet()) {
            
            result += this.base_stock[stock] + " - " + this.stock_used.get(stock) + "x \n";
            
        }

        return result;
        
    }
    
    /**
     * Check if the order is completed
     * 
     * @return True if completed
     */
    protected boolean checkRemainingOrder() {
        
        for (int[] order1 : this.order) {
            if (order1[1] > 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Counts how many rods can be cut with predefined pattern.
     * Depends on the state of the order.
     * 
     * @param pattern Cut pattern
     * @param rod_index Which source rod to use
     * @return Array with rod index and its count
     */
    protected int[] countUsedRods(List<Integer> pattern, int rod_index) {
        
        if(pattern.isEmpty()) {
            return new int[]{0,0};
        }
        
        int rods_useded = 0;
        int[] rods_ret;
        int pattern_count;
        
        for(Integer order_index : pattern) {
            
            pattern_count = 0;
            
            for(Integer order_index2 : pattern) {
                if(order_index2 == order_index) {
                    pattern_count += 1;
                }
            }
          
            if(rods_useded == 0 || this.order[order_index][1]/pattern_count <= rods_useded) { 
                rods_useded = this.order[order_index][1]/pattern_count;
            }
            
        }
        
        
        
        rods_ret = new int[]{rod_index, rods_useded};
        
        return rods_ret;
    }
    
    /**
     * Removes currently cutted pieces from order.
     * Updates this.order
     * 
     * @param pattern Cut pattern
     * @param rods_used How many rods will be cut
     */
    protected void updateRemainingOrder(List<Integer> pattern, int rods_used) {
        
        /**
         * Updates remaining order
         */
        for(Integer order_index : pattern) {           
            this.order[order_index][1] -= rods_used;

        }
        
    }
    
    /**
     * Returns an index of a rod, that will be cut.
     * 
     * @param remaining_length
     * @param index
     * @return 
     */
    protected int getIndexOfOrder(int remaining_length, int index) {
        
        int ret_index = index;
        
        for(int i = 0; i < this.order.length; i++) {
            if(this.order[i][0] <= remaining_length && this.order[i][1] > 0) {
                if(ret_index == 0) {
                    return i;
                }
                else {
                    ret_index--;
                }
            }
        }
        
        return -1;
    }

    /**
     * Returns shortest piece left to cut in this.order
     * 
     * @param choices
     * @return index of a piece
     */
    protected int getShortestChoiceNumber(List<Integer> choices) {
        
        if(choices == null || choices.isEmpty()) {
            return -1;
        }
        
        Integer max_length = null;
        int index = -1;
        for(Integer i : choices) {
            if(max_length == null || this.order[i][0] < max_length) {
                max_length = this.order[i][0];
                index = i;
            }
        }
        
        return index;
    }
    
    /**
     * Returns longest piece left to cut in this.order
     * 
     * @param choices
     * @return index of a piece
     */
    protected int getLongestChoiceNumber(List<Integer> choices) {
        
        if(choices == null || choices.isEmpty()) {
            return -1;
        }
        
        Integer max_length = null;
        int index = -1;
        for(Integer i : choices) {
            if(max_length == null || this.order[i][0] > max_length) {
                max_length = this.order[i][0];
                index = i;
            }
        }
        
        return index;
    }
    
    /**
     * Returns possible choices of pieces to cut with current remaining length and this.order status
     * 
     * @param remaining_length
     * @param pattern
     * @return 
     */
    protected List<Integer> getPossibleChoicesNumber(int remaining_length, List<Integer> pattern) {
        
        List<Integer> possibilities = new ArrayList<>();
        
        if(remaining_length == 0) {
            return possibilities;
        }
        
        int count;
        
        for(int i = 0; i < this.order.length; i++) {
            if (this.order[i][0] <= remaining_length && this.order[i][1] > 0) { 
                
                if(pattern.contains(i)) {
                    count = 0;
                    for(int c : pattern) {
                        if(c == i) {
                            count++;
                        }
                    }
                    if(this.order[i][1] > count) {
                        possibilities.add(i);
                    }
                }
                else {
                    possibilities.add(i);
                }
            }
        }
        
        return possibilities;
        
    }
    
    /**
     * From 0-1 to 0-max_bound
     * 
     * @param input
     * @param max_bound
     * @return 
     */
    protected int getIndex(double input, int max_bound) {
        
        if(max_bound == 1) {
            return 0;
        }
        
        double decision_point = 1./max_bound;
        double rest = input;
        int index = 0;
        
        while(rest > decision_point) {
            rest -= decision_point;
            index++;
        }
        
        return index;
    }

    /**
     * Returns random vector of length dim
     * 
     * @param dim
     * @return 
     */
    public double[] generateTrial(int dim) {
        
        double[] trial = new double[dim];
        java.util.Random rnd = new java.util.Random();
        
        for(int i = 0; i < dim; i++) {
            trial[i] = rnd.nextDouble();
        }
        
        return trial;
    }
    
    /**
     * 
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        /**
         * Delka = 5840
         * Prorez = 4
         */
        int[][] order;
        int[] stock = new int[]{5840};
        int cut_through = 4;
        CuttingStock1D_solver_clean cut;
        double[] trial;

        /**
         * Trubka
         */
        order = new int[][]{{1040,3},{940,12},{840,1},{740,5}};
        cut = new CuttingStock1D_solver_clean(order, stock, cut_through);
        trial = new double[]{0.6196509442317167, 0.2816832165632539, 0.24560344439628023, 0.4007850181651942, 0.05963679804418354, 0.43325474983774104, 0.693760468220283, 0.015327262336560787, 0.7102653658073036, 0.1163838285770955, 0.4279359264614391, 0.9313718863471665, 0.11716541037495054, 0.9939361834946511, 0.2346098837338545, 0.25376698001764375, 0.640114715416252, 0.004283397395251409, 0.8008465387591777, 0.306143143401273, 0.7158382303908976, 0.40574108233227535, 0.3301087079590106, 0.46399530210736384, 0.20837720293111284, 0.19810656250363, 0.1270062966829144, 0.19703202322509672, 0.4576559101928619, 0.2401732875793544, 0.96131219998811, 0.8589089799193628, 0.10659835410870455, 0.2033900371972477, 0.7569302894943356, 0.47437693401317316, 0.038124531289013386, 0.3543707796234209, 0.16810182915957117, 0.017039055874162323, 0.7855820486760803, 0.04275049501648687, 0.9029884483304403, 0.4453257468427454, 0.5936457041951867, 0.6859217719329934, 0.2338709549581558, 0.0699603366076954, 0.9014438911669516, 0.9884752084643368, 0.9935552855530073, 0.6631018048168517, 0.3956475380414288, 0.11170654713166761, 0.4991779284347715, 0.08751570177096979, 0.9384836584005717, 0.5173418267344131, 0.2674552645821313, 0.12353814100870009, 0.6141124436284466, 0.24967152067409526, 0.08952249383912525, 0.6022616417699191, 0.8565327516565063, 0.49157320606768307, 0.01885946470296862, 0.9152859948452264, 0.12315833314937885, 0.3324416040666137, 0.09240217754655144, 0.464541227704689, 0.860432898248394, 0.8198339104356912, 0.15279059486811142, 0.6401037500953404, 0.5713290533607068, 0.5316772562249686, 0.9688046398298962, 0.469590824801025, 0.6393026638494738, 0.10217437812459906, 0.9613918645225378, 0.169632868388494, 0.18925075697875826, 0.3620824364729258, 0.23429222207236433, 0.4480866110319801, 0.6732476253922137, 0.012829140949833895, 0.28111626628465736, 0.555066770065725, 0.057310175745491465, 0.04396952739924609, 0.6948352386669168, 0.7615374729929285, 0.4551707137407519, 0.4247752183355329, 0.44099281414110136, 0.15611910962893893};
        
        
        
        System.out.println("==========================\nTrubka\n==========================\n");
        
        cut.fitness(trial);
        System.out.println(cut.writeDownPatterns());
        System.out.println("START: "  + new Date());
        cut.solve(100000);
        System.out.println("END: "  + new Date());
        
        System.out.println(cut.writeDownPatterns());
        
        /**
         * VP S
         */
        order = new int[][]{{3665,2},{2660,4},{2650,12},{2625,4},{2615,4},{2425,2},{2405,10},{2395,6},{2385,8},{2295,16},{2290,4},{2045,4},{1925,2},{1680,2},{765,2},{595,2},{565,2}};
        cut = new CuttingStock1D_solver_clean(order, stock, cut_through);
        trial = new double[]{0.2800, 0.6015, 0.2035, 0.3000, 0.9148, 0.3479, 0.3792, 0.1744, 0.7637, 0.6583, 0.1554, 0.8429, 0.4405, 0.4124, 0.3902, 0.2598, 0.7698, 0.7916, 0.5559, 0.3330, 0.0614, 0.3785, 0.8229, 0.8332, 0.6218, 0.4804, 0.5364, 0.9536, 0.7246, 0.5556, 0.9326, 0.5347, 0.9305, 0.3664, 0.0660, 0.2828, 0.4716, 0.6236, 0.6982, 0.5387, 0.3976, 0.4407, 0.3297, 0.2779, 0.3183, 0.8755, 0.6508, 0.0649, 0.8709, 0.1978, 0.4338, 0.6224, 0.4910, 0.2958, 0.2889, 0.5273, 0.6719, 0.8079, 0.7712, 0.2993, 0.6618, 0.1512, 0.0377, 0.8097, 0.4631, 0.6073, 0.6263, 0.5035, 0.0785, 0.0190, 0.9592, 0.4055, 0.1759, 0.4586, 0.8539, 0.2558, 0.7333, 0.4290, 0.6699, 0.4106, 0.7693, 0.7811, 0.7129, 0.3342, 0.9647, 0.5448, 0.1737, 0.6074, 0.8363, 0.3021, 0.1874, 0.3829, 0.1379, 0.5608, 0.5660, 0.1278, 0.3313, 0.8299, 0.3764, 0.3138, 0.2972, 0.7389, 0.8178, 0.4345, 0.9343, 0.5997, 0.7850, 0.3645, 0.9357, 0.2633, 0.0945, 0.6076, 0.8239, 0.9925, 0.5992, 0.4239, 0.2760, 0.3367, 0.3189, 0.1765, 0.8778, 0.6783, 0.2752, 0.4908, 0.7078, 0.1956, 0.9331, 0.9827, 0.5705, 0.4844, 0.0264, 0.3774, 0.2754, 0.9689, 0.0685, 0.8154, 0.0869, 0.5389, 0.0398, 0.0223, 0.3606, 0.5569, 0.5787, 0.8043, 0.4232, 0.4912, 0.5035, 0.0242, 0.9555, 0.9558, 0.6449, 0.8111, 0.4924, 0.8594, 0.7777, 0.2621, 0.1686, 0.7035, 0.9079, 0.3059, 0.5055, 0.8059, 0.2909, 0.7135, 0.8612, 0.3725, 0.3368, 0.4683, 0.1476, 0.8918, 0.9939, 0.8343, 0.5551, 0.2857, 0.2886, 0.4998, 0.3181, 0.6110, 0.4530, 0.1142, 0.0098, 0.8817, 0.4679, 0.3871, 0.4542, 0.7543, 0.9149, 0.6699, 0.1113, 0.4035, 0.7423, 0.6047, 0.7088, 0.9053, 0.9247, 0.9908, 0.9672, 0.8383, 0.0217, 0.4936};
        
        System.out.println("==========================\nVP S\n==========================\n");
        
        cut.fitness(trial);
        System.out.println(cut.writeDownPatterns());
        System.out.println("START: "  + new Date());
        cut.solve(100000);
        System.out.println("END: "  + new Date());
        
        System.out.println(cut.writeDownPatterns());
        
        /**
         * VP O
         */
        order = new int[][]{{2455,8},{1875,4},{1605,2},{1565,2},{1525,4},{1515,10},{1475,2},{1275,6},{915,4}};
        cut = new CuttingStock1D_solver_clean(order, stock, cut_through);
        trial = new double[]{0.5965, 0.3300, 0.3268, 0.0238, 0.7898, 0.6122, 0.8046, 0.5348, 0.0269, 0.6047, 0.2614, 0.5030, 0.3295, 0.8104, 0.5816, 0.5940, 0.3949, 0.4867, 0.2609, 0.8531, 0.1803, 0.2054, 0.5164, 0.0675, 0.6450, 0.3661, 0.1634, 0.8780, 0.4526, 0.0483, 0.8107, 0.5615, 0.1542, 0.4249, 0.3029, 0.5656, 0.5550, 0.8976, 0.0324, 0.8430, 0.1698, 0.4320, 0.3654, 0.7250, 0.2255, 0.6623, 0.9764, 0.4089, 0.8992, 0.0494, 0.2114, 0.5169, 0.2001, 0.7667, 0.4048, 0.8115, 0.7313, 0.3467, 0.0588, 0.3209, 0.1029, 0.5159, 0.6003, 0.1674, 0.4273, 0.7540, 0.4683, 0.5032, 0.9798, 0.6901, 0.9355, 0.5627, 0.3717, 0.9404, 0.9278, 0.2628, 0.8117, 0.2815, 0.9178, 0.5261, 0.6258, 0.7042, 0.4190, 0.7969, 0.7234, 0.2802, 0.9939, 0.1106, 0.4369, 0.0255, 0.7008, 0.1494, 0.4878, 0.6532, 0.3305, 0.8019, 0.3588, 0.2631, 0.4583, 0.2874};
        
        System.out.println("==========================\nVP O\n==========================\n");
        
        cut.fitness(trial);
        System.out.println(cut.writeDownPatterns());
        System.out.println("START: "  + new Date());
        cut.solve(100000);
        System.out.println("END: "  + new Date());
        
        System.out.println(cut.writeDownPatterns());
        
        /**
         * SP Z70
         */
        order = new int[][]{{3894,3},{3889,2},{3714,2},{2990,1},{1900,1},{1845,1},{1835,1},{1794,6},{1789,9},{1784,1},{1730,1},{1725,1},{1680,1},{1485,1},{1470,1},{1465,1},{1455,3},{1394,1},{1389,1},{1315,2},{1255,1},{1220,1},{1215,1},{894,12},{889,7},{870,1},{850,1},{840,5},{710,1},{669,6},{575,1}};
        cut = new CuttingStock1D_solver_clean(order, stock, cut_through);
        trial = new double[]{0.6659, 0.1081, 0.8915, 0.9773, 0.4852, 0.3104, 0.4474, 0.0631, 0.8088, 0.4608, 0.2113, 0.7952, 0.3671, 0.5263, 0.4111, 0.3254, 0.6535, 0.3918, 0.7030, 0.3393, 0.5736, 0.7691, 0.7131, 0.7790, 0.8828, 0.6992, 0.2539, 0.3049, 0.7444, 0.2437, 0.5456, 0.9838, 0.5547, 0.8747, 0.3489, 0.9950, 0.7754, 0.0210, 0.7793, 0.9832, 0.8893, 0.2036, 0.7109, 0.0558, 0.7534, 0.2035, 0.1041, 0.3202, 0.5622, 0.7458, 0.2934, 0.2894, 0.3518, 0.2530, 0.2573, 0.4442, 0.4785, 0.3250, 0.1598, 0.7344, 0.7872, 0.7367, 0.9999, 0.8259, 0.2477, 0.9072, 0.0344, 0.4559, 0.6824, 0.9949, 0.7515, 0.3456, 0.8346, 0.9956, 0.5608, 0.7370, 0.4346, 0.4836, 0.0431, 0.7723, 0.1072, 0.9223, 0.5520, 0.6118, 0.9366, 0.7963, 0.1991, 0.3999, 0.2543, 0.1640, 0.7689, 0.9331, 0.7472, 0.6196, 0.9075, 0.1555, 0.2974, 0.0626, 0.0100, 0.4147, 0.0610, 0.1250, 0.5890, 0.7487, 0.1663, 0.5778, 0.5504, 0.3401, 0.0724, 0.5527, 0.7257, 0.6757, 0.0642, 0.9516, 0.2120, 0.8795, 0.5836, 0.9890, 0.5584, 0.1469, 0.0928, 0.4138, 0.6032, 0.2063, 0.9533, 0.5939, 0.8138, 0.2521, 0.3615, 0.3065, 0.3110, 0.8541, 0.7338, 0.7803, 0.3702, 0.9324, 0.0521, 0.2567, 0.0812, 0.0059, 0.5947, 0.1004, 0.0245, 0.5724, 0.1625, 0.2776, 0.2732, 0.6571, 0.3797, 0.8424, 0.7674, 0.2813, 0.9023, 0.9241, 0.1220, 0.8430, 0.3958, 0.5914, 0.2295, 0.1780, 0.9616, 0.8854, 0.5954, 0.3738, 0.2809, 0.2594, 0.2076, 0.2475, 0.1272, 0.6403, 0.8791, 0.7659, 0.0488, 0.3364, 0.4304, 0.5842, 0.9682, 0.0555, 0.7767, 0.3139, 0.1583, 0.3686, 0.9761, 0.0887, 0.5629, 0.0570, 0.5581, 0.4921, 0.7045, 0.9161, 0.9990, 0.9353, 0.4917, 0.7504, 0.0435, 0.1286, 0.0800, 0.4950, 0.6781, 0.3758};
        
        System.out.println("==========================\nSP Z70\n==========================\n");
        
        cut.fitness(trial);
        System.out.println(cut.writeDownPatterns());
        System.out.println("START: "  + new Date());
        cut.solve(100000);
        System.out.println("END: "  + new Date());
        
        System.out.println(cut.writeDownPatterns());
        
        /**
         * SP Z90
         */
        order = new int[][]{{2555,1},{2540,1},{2415,1},{1570,1},{1506,1},{1438,1},{1434,1},{1424,1},{1213,1},{1095,1},{1090,1},{1051,1},{1042,1},{1040,1},{1030,2},{975,1},{961,1},{960,1},{955,2},{947,1},{938,1},{934,1},{914,1},{908,1},{881,1},{840,1},{835,1},{825,1},{818,1},{815,1},{760,2},{732,1},{694,1},{676,1},{668,1}};
        cut = new CuttingStock1D_solver_clean(order, stock, cut_through);
        trial = new double[]{0.6242, 0.3391, 0.2907, 0.3996, 0.7651, 0.9409, 0.4987, 0.1975, 0.5313, 0.4142, 0.1191, 0.1931, 0.1565, 0.5492, 0.8464, 0.2065, 0.4553, 0.7408, 0.4428, 0.8269, 0.0623, 0.5602, 0.9605, 0.7328, 0.9151, 0.0792, 0.6974, 0.0255, 0.9694, 0.9402, 0.9558, 0.4866, 0.4908, 0.5066, 0.5481, 0.1465, 0.9125, 0.2918, 0.6517, 0.0597, 0.1695, 0.2671, 0.0530, 0.7226, 0.6245, 0.5794, 0.0412, 0.1430, 0.1852, 0.6696, 0.2121, 0.1682, 0.9812, 0.7785, 0.2260, 0.3182, 0.0137, 0.3493, 0.1255, 0.0311, 0.5236, 0.8913, 0.1654, 0.9264, 0.4312, 0.4596, 0.2319, 0.4179, 0.0933, 0.0185, 0.8354, 0.7692, 0.7980, 0.6106, 0.3510, 0.4710, 0.8669, 0.4311, 0.6478, 0.6447, 0.9772, 0.6206, 0.5030, 0.0269, 0.3237, 0.8313, 0.2250, 0.9730, 0.5480, 0.3972, 0.7262, 0.8353, 0.9324, 0.2445, 0.8793, 0.0152, 0.2243, 0.5138, 0.3672, 0.9666, 0.5918, 0.2354, 0.3943, 0.4395, 0.6388, 0.2990, 0.5650, 0.2874, 0.1102, 0.3868, 0.9549, 0.2389, 0.9020, 0.5045, 0.9021, 0.1330, 0.2599, 0.2353, 0.3255, 0.2526, 0.5973, 0.8677, 0.4457, 0.5556, 0.1903, 0.1031, 0.6221, 0.7984, 0.1219, 0.1360, 0.6513, 0.7771, 0.2358, 0.5187, 0.2772, 0.1658, 0.5334, 0.3694, 0.8504, 0.7257, 0.4042, 0.1676, 0.0454, 0.9777, 0.3122, 0.5868, 0.2972, 0.7844, 0.0787, 0.5637, 0.1454, 0.6714, 0.7007, 0.0731, 0.9946, 0.1315, 0.7210, 0.8842, 0.8885, 0.6049, 0.5288, 0.9140, 0.7741, 0.3010, 0.6094, 0.6571, 0.3901, 0.5417, 0.6855, 0.5736, 0.3500, 0.8422, 0.8966, 0.2463, 0.2113, 0.5176, 0.6475, 0.5821, 0.3235, 0.2077, 0.5244, 0.3215, 0.0293, 0.3439, 0.7249, 0.6502, 0.3566, 0.2480, 0.7773, 0.7576, 0.6841, 0.2510, 0.9335, 0.7759, 0.9901, 0.9423, 0.5139, 0.6534, 0.1984, 0.4260};
       
        System.out.println("==========================\nSP Z90\n==========================\n");
        
        cut.fitness(trial);
        System.out.println(cut.writeDownPatterns());
        System.out.println("START: "  + new Date());
        cut.solve(100000);
        System.out.println("END: "  + new Date());
        
        System.out.println(cut.writeDownPatterns());
        
        /**
         * VP Z
         */
        order = new int[][]{{2325,78},{1845,4},{1840,4},{1700,12},{1495,6},{1260,2}};
        cut = new CuttingStock1D_solver_clean(order, stock, cut_through);
        trial = new double[]{0.1442, 0.2502, 0.1395, 0.8114, 0.2165, 0.0678, 0.5652, 0.2920, 0.1055, 0.8503, 0.1022, 0.0533, 0.1136, 0.6072, 0.2910, 0.6537, 0.5930, 0.2309, 0.9390, 0.5438, 0.9051, 0.2803, 0.7566, 0.8047, 0.4171, 0.0070, 0.1522, 0.8826, 0.0878, 0.1851, 0.2343, 0.2746, 0.0056, 0.2256, 0.6455, 0.4274, 0.8401, 0.0004, 0.7176, 0.9455, 0.5406, 0.7774, 0.5750, 0.5754, 0.7752, 0.6810, 0.2393, 0.3317, 0.5406, 0.1263, 0.4890, 0.5590, 0.0943, 0.7537, 0.2168, 0.6898, 0.8594, 0.4857, 0.2920, 0.9476, 0.2373, 0.4686, 0.4818, 0.2721, 0.3249, 0.8207, 0.8188, 0.2960, 0.3778, 0.3094, 0.9064, 0.2930, 0.4898, 0.3046, 0.0520, 0.6318, 0.3605, 0.6159, 0.7845, 0.4225, 0.2181, 0.1096, 0.2708, 0.7217, 0.9289, 0.3247, 0.3804, 0.8738, 0.0784, 0.6591, 0.6629, 0.3102, 0.3409, 0.0789, 0.3020, 0.4024, 0.7680, 0.7223, 0.1880, 0.7700};
        
        System.out.println("==========================\nVP Z\n==========================\n");
        
        cut.fitness(trial);
        System.out.println(cut.writeDownPatterns());
        System.out.println("START: "  + new Date());
        cut.solve(100000);
        System.out.println("END: "  + new Date());
        
        System.out.println(cut.writeDownPatterns());
        
        cut = new CuttingStock1D_solver_clean();
        
        trial = new double[]{0.6944, 0.8901, 0.5148, 0.8928, 0.8229, 0.9669, 0.3438, 0.8817, 0.4254, 0.0302, 0.8022, 0.9515, 0.1403, 0.1063, 0.9405, 0.9908, 0.7822, 0.4441, 0.9858, 0.7561, 0.3312, 0.0342, 0.9098, 0.9726, 0.5038, 0.8789, 0.5566, 0.6088, 0.2530, 0.3528, 0.7686, 0.9592, 0.5536, 0.4277, 0.3375, 0.8787, 0.0787, 0.6928, 0.1811, 0.7727, 0.1467, 0.0350, 0.4882, 0.5261, 0.1478, 0.7258, 0.2475, 0.1575, 0.0642, 0.6052, 0.5639, 0.3936, 0.7276, 0.6075, 0.5162, 0.5853, 0.0433, 0.7091, 0.3485, 0.0173, 0.2006, 0.4169, 0.9132, 0.6084, 0.1864, 0.9210, 0.6342, 0.8578, 0.4766, 0.3362, 0.2776, 0.1810, 0.3879, 0.6468, 0.1953, 0.9143, 0.5381, 0.2112, 0.1723, 0.5168, 0.4958, 0.0380, 0.0633, 0.6254, 0.1440, 0.1780, 0.1899, 0.1265, 0.5280, 0.5578, 0.4266, 0.4297, 0.9900, 0.1022, 0.9211, 0.9728, 0.0374, 0.5007, 0.7570, 0.8867};
        
        System.out.println("==========================\nWikipedia\n==========================\n");
        
        cut.fitness(trial);
        System.out.println(cut.writeDownPatterns());
        cut.solve(100000);

        
        System.out.println(cut.writeDownPatterns());

        
    }
    
}
