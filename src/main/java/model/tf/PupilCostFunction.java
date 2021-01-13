package model.tf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import model.Individual;

import util.IndividualUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 * Created by jakub on 27/10/15.
 */
public class PupilCostFunction implements TestFunction {

    public double[][] valueArray;
    public int rows = 185;
    public int columns = 339;
    double optimum;
    String path;
    
    public PupilCostFunction(String path) {
        
        this.path = path;
        optimum = 1;
        
        String csvFile = path;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        valueArray = new double[rows][columns];
        int i = 0;

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                
                // use comma as separator
                String[] lineValues = line.split(cvsSplitBy);
                
                for(int j = 0; j < lineValues.length; j++) {
                    valueArray[i][j] = - Double.parseDouble(lineValues[j]);
                    if(valueArray[i][j] < optimum) {
                        optimum = valueArray[i][j];
                    }
                }
                i++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }

    @Override
    public double fitness(Individual individual) {
        return fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {
        
        if(vector.length != 2){
            return 1;
        }
        
        int row, column;
        
        row = ((int) Math.round(vector[0]*(rows))) % rows;
        column = ((int) Math.round(vector[1]*(columns))) % columns;
        
        return valueArray[row][column];
    }

    @Override
    public void constrain(Individual individual) {
        IndividualUtil.clipInBounds(individual, 0, 1);
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
        return 10E-2;
    }

    @Override
    public double optimum() {
        return optimum;
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
        return "Pupil: " + this.path;
    }
    
    public int[] getCoords(double[] vector) {
        
        if(vector.length != 2){
            return new int[]{};
        }
        
        int row, column;
        
        row = ((int) Math.round(vector[0]*(rows))) % rows;
        column = ((int) Math.round(vector[1]*(columns))) % columns;
        
        return new int[]{row, column};
        
    }

    @Override
    public double[] optimumPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
