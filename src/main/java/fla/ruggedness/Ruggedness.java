package fla.ruggedness;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Individual;
import model.fla.ModifiedPRW;
import model.fla.PRW;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 *
 * @author adam on 20/01/2016
 */
public class Ruggedness {

    private TestFunction ffunction;
    private int dimension;
    private int walkSteps;
    private double stepBoundary;
    private Individual best;
    private PRW prw;

    public Ruggedness() {
    }

    public Ruggedness(TestFunction ffunction, int dimension, int walkSteps, double stepBoundary, PRW prw) {
        this.ffunction = ffunction;
        this.dimension = dimension;
        this.walkSteps = walkSteps;
        this.stepBoundary = stepBoundary;
        this.prw = prw; 
    }

    /**
     * 
     * @return 
     */
    private double estimateSingleRuggedness(int[] startZone){
        
        /**
         * First determine stability measure
         */
        Random rnd = new Random();
//        int[] startZone = new int[this.dimension];
//        for(int i=0; i<this.dimension; i++){
//            startZone[i] = rnd.nextInt(2);
//        }
//        double changeProbability = 0.05;
//        PRWm prwm = new PRWm(this.dimension, this.walkSteps, this.stepBoundary, startZone, this.ffunction, changeProbability);
//        prwm.walk();
        this.prw.setStartZone(startZone);
        this.prw.walk();
        
        this.best = new Individual("0", new double[dimension], Double.MAX_VALUE);
        List<Individual> walk = prw.getWalkIndividuals();
        Individual bestInd = prw.getBest();
        if(bestInd.fitness < this.best.fitness){
            this.best = bestInd;
        }
        
//        for(Individual ind : walk){
//            System.out.println(ind);
//        }
        
//        System.out.println("=============================\nBEST: " + bestInd);
        
//        double e = this.countStabilityMeasureV2(walk);
        double e = this.countStabilityMeasure(walk);
        
//        System.out.println("=============================\nE: " + e);
 
        double[] eTable = new double[]{0, e/128, e/64, e/32, e/16, e/8, e/4, e/2, e};
        double[] entropyTable = new double[9];
        
        /**
         * Code to time series, group counts, count entropy
         */
        int[] timeSerie;
        int[] groupCounts;
        double entropy;
        double maxEntropy = 0;
        
        for(int i=0; i<eTable.length; i++){
            
//            System.out.println("======================");
            
            timeSerie = new int[walk.size()-1];
            timeSerie = this.codeToTimeSeries(walk, eTable[i]);
            
//            System.out.println("TIME SERIE:\n" + Arrays.toString(timeSerie));
            
            groupCounts = new int[6];            
            groupCounts = this.getGroupCounts(timeSerie);
            
//            System.out.println("GROUP COUNTS:\n" + Arrays.toString(groupCounts));
            
            entropy = this.countEntropy(groupCounts, timeSerie.length-1);
            entropyTable[i] = entropy;
            if(entropy > maxEntropy){
                maxEntropy = entropy;
            }

        }
        
//        System.out.println("ENTROPY TABLE:\n" + Arrays.toString(entropyTable));
        
        /**
         * Max of entropyTable
         */
        
        
        return maxEntropy; 
    }
    
    /**
     * 
     * @return 
     */
    private double estimateDimRuggedness(){
        
        int[] startZone;
        int which_dim;
        String binary;
        char[] ch_array;
        
        this.best = new Individual("0", new double[dimension], Double.MAX_VALUE);
        
        double sum = 0;
        
        for(int i=0; i<this.dimension; i++){
            
            startZone = new int[dimension];
            for(int k = 0; k < dimension; k++){
                startZone[k] = 0;
            }
            
            which_dim = i * ((int)(Math.pow(2, dimension)/dimension));
            binary = Integer.toBinaryString(which_dim);
            ch_array = binary.toCharArray();
            for(int j = (ch_array.length - 1); j > -1; j--){
                if(ch_array[j] == '1'){
                    startZone[dimension - (ch_array.length - j - 1) - 1] = 1;
                }
            }
            
            sum += this.estimateSingleRuggedness(startZone);
            
        }
        
        return sum/(double) this.dimension;
        
    }
    
    /**
     * 
     * @param runCount
     * @return 
     */
    public double[] estimateRuggedness(int runCount){
    
        double sum = 0;
        double[] result = new double[runCount];
        
        for(int i=0; i<runCount; i++){
            result[i] = this.estimateDimRuggedness();
        }
        
        return result;
        
    }
    
    /**
     * 
     * @param groupCounts
     * @param doubles
     * @return 
     */
    private double countEntropy(int[] groupCounts, int doubles){
        
        double entropy = 0;
        double entropyPart;
        double probability;
        
        for(int i=0; i<groupCounts.length; i++){
            
            if(groupCounts[i] > 0){
                probability = groupCounts[i]/(double) doubles;
                entropyPart = (probability * (Math.log(probability)/Math.log(6)));
                entropy += entropyPart;
            }
            
        }
        
        return -entropy;
        
    }
    
    /**
     * 
     * @param timeSerie
     * @return 
     */
    private int[] getGroupCounts(int[] timeSerie){
        
        int[] groupCounts = new int[]{0,0,0,0,0,0};
        
        for(int i=0; i<timeSerie.length-1; i++){
            
            if(timeSerie[i]==0 && timeSerie[i+1]==1){
                groupCounts[0]++;
            }
            else if(timeSerie[i]==0 && timeSerie[i+1]==-1){
                groupCounts[1]++;
            }
            else if(timeSerie[i]==1 && timeSerie[i+1]==0){
                groupCounts[2]++;
            }
            else if(timeSerie[i]==1 && timeSerie[i+1]==-1){
                groupCounts[3]++;
            }
            else if(timeSerie[i]==-1 && timeSerie[i+1]==0){
                groupCounts[4]++;
            }
            else {
                groupCounts[5]++;
            }
            
        }
        
        return groupCounts;
        
    }
    
    /**
     * 
     * @param indList
     * @param e
     * @return 
     */
    private int[] codeToTimeSeries(List<Individual> indList, double e){
        
        int[] timeSerie = new int[indList.size()-1];
        
        for(int i=0; i<indList.size()-1; i++){
            
            if((indList.get(i+1).fitness - indList.get(i).fitness) < -e){
                timeSerie[i] = -1;
            }
            if(Math.abs(indList.get(i+1).fitness - indList.get(i).fitness) <= e){
                timeSerie[i] = 0;
            }
            if((indList.get(i+1).fitness - indList.get(i).fitness) > e){
                timeSerie[i] = 1;
            }
        }
        
        return timeSerie;
        
    }
    
    /**
     * 
     * @param indList
     * @return 
     */
    private double countStabilityMeasure(List<Individual> indList){
        
        double e = Double.MIN_VALUE;
        double dif;
        
        for(int i=0; i<indList.size()-1; i++){
            
            dif = Math.abs(indList.get(i).fitness - indList.get(i+1).fitness);
            
            if(dif > e){
                e = dif;
            }
            
        }
        
        return e;
        
    }
    
    /**
     * 
     * @param indList
     * @return 
     */
    private double countStabilityMeasureV2(List<Individual> indList){

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (Individual indList1 : indList) {
            if (indList1.fitness < min) {
                min = indList1.fitness;
            }
            if (indList1.fitness > max) {
                max = indList1.fitness;
            }
        }
        
        return Math.abs(max-min);
        
    }
    
    public void mainForCEC2016() throws Exception {
        
        
        this.dimension = 10;
        this.walkSteps = dimension * 1000;
        this.stepBoundary = 0.1;
        int runs = 30;
        double changeProbability = 0.05;
        double[] result;
        PrintWriter single_rug;
        PrintWriter result_table;
        
        result_table = new PrintWriter("C:\\Users\\wiki\\Documents\\Ruggedness-CEC2016/results_rug.txt");
        result_table.print("{");
        
        result_table.print("{function,PRW mean,PRW std,MPRW mean,MPRW std},");
        
        for(int func_num = 1; func_num < 16; func_num++){
            
            this.ffunction = new Cec2015(dimension, func_num);
            /**
             * Classic PRW
             */
            this.prw = new PRW(dimension, walkSteps, stepBoundary, new int[dimension], ffunction);
            result = this.estimateRuggedness(runs);
            single_rug = new PrintWriter("C:\\Users\\wiki\\Documents\\Ruggedness-CEC2016\\classic/" + ffunction.name() + "_rug.txt");
            single_rug.print("{");
            
            for(int i = 0; i < result.length; i++){
                
                single_rug.print(String.format(Locale.US, "%.10f", result[i]));
                
                if(i != (result.length - 1)) {
                    single_rug.print(",");
                }
                
            }
            
            single_rug.print("}");
            single_rug.close();
            
            result_table.print("{");
            result_table.print(ffunction.name());
            result_table.print(",");
            result_table.print(String.format(Locale.US, "%.10f", new Mean().evaluate(result)));
            result_table.print(",");
            result_table.print(String.format(Locale.US, "%.10f", new StandardDeviation().evaluate(result)));
            
            System.out.println(ffunction.name());
            System.out.println(new Date());
            System.out.println("PRW Ruggedness:\n" + String.format(Locale.US, "%.10f", new Mean().evaluate(result)) + " +- " + String.format(Locale.US, "%.10f", new StandardDeviation().evaluate(result)));

            
            /**
             * Modified PRW
             */
            this.prw = new ModifiedPRW(dimension, walkSteps, stepBoundary, new int[dimension], ffunction, changeProbability);
            result = this.estimateRuggedness(runs);
            single_rug = new PrintWriter("C:\\Users\\wiki\\Documents\\Ruggedness-CEC2016\\modified/" + ffunction.name() + "_rug.txt");
            single_rug.print("{");
            
            for(int i = 0; i < result.length; i++){
                
                single_rug.print(String.format(Locale.US, "%.10f", result[i]));
                
                if(i != (result.length - 1)) {
                    single_rug.print(",");
                }
                
            }
            
            single_rug.print("}");
            single_rug.close();
            
            result_table.print(",");
            result_table.print(String.format(Locale.US, "%.10f", new Mean().evaluate(result)));
            result_table.print(",");
            result_table.print(String.format(Locale.US, "%.10f", new StandardDeviation().evaluate(result)));
            result_table.print("}");
            
            System.out.println(new Date());
            System.out.println("Modified PRW Ruggedness:\n" + String.format(Locale.US, "%.10f", new Mean().evaluate(result)) + " +- " + String.format(Locale.US, "%.10f", new StandardDeviation().evaluate(result)));
            System.out.println("=====================");
            
            if(func_num != 15){
                result_table.print(",");
            }
            
        }
        
        result_table.print("}");
        result_table.close();
        
    }
    
    /**
     * 
     * @param ff
     * @param walkSteps
     * @param stepBoundary
     * @param walkCount
     * @return 
     */
    public double[] printOutRuggedness(TestFunction ff, int walkSteps, double stepBoundary, int walkCount){
        
        
        double[] ruggedness = new double[5];
        this.ffunction = ff;
        this.walkSteps = walkSteps;
        this.stepBoundary = stepBoundary;
        double[] ruggednessTable;
        List<Individual> bestArray = new ArrayList<>();
        
//        this.dimension = 2;
//        ruggednessTable = this.estimateRuggedness(walkCount);
//        ruggedness[0] = this.mean(ruggednessTable);
//        bestArray.add(this.best);
        
        this.dimension = 10;
//        this.ffunction.init(dimension);
        ruggednessTable = this.estimateRuggedness(walkCount);
        ruggedness[1] = this.mean(ruggednessTable);
        bestArray.add(this.best);
        
//        this.dimension = 30;
//        ruggednessTable = this.estimateRuggedness(walkCount);
//        ruggedness[2] = this.mean(ruggednessTable);
//        bestArray.add(this.best);
//        
//        this.dimension = 50;
//        ruggednessTable = this.estimateRuggedness(walkCount);
//        ruggedness[3] = this.mean(ruggednessTable);
//        bestArray.add(this.best);
//        
//        this.dimension = 100;
//        ruggednessTable = this.estimateRuggedness(walkCount);
//        ruggedness[4] = this.mean(ruggednessTable);
//        bestArray.add(this.best);
        
        PrintWriter writer;
        try {
            writer = new PrintWriter(this.ffunction.name()+"-ruggedness.txt", "UTF-8");

            writer.println(Arrays.toString(ruggedness));
            
            System.out.println(this.ffunction.name() + " Ruggedness: " + Arrays.toString(ruggedness));
            for(Individual ind : bestArray){
                System.out.println(this.ffunction.name() + " " + ind.vector.length + "D best: " + ind);
                writer.println(this.ffunction.name() + " " + ind.vector.length + "D best: " + ind);
            }
        
            writer.close();
        
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Ruggedness.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ruggedness;
        
    }
    
    /**
     * 
     * @param array
     * @return 
     */
    private double mean(double[] array){
        
        double sum = 0;
        
        for(int i=0; i<array.length; i++){
            sum += array[i];
        }
        
        return sum/(double)array.length;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        Ruggedness re;

        re = new Ruggedness();
        re.mainForCEC2016();

        
    }

    
}
