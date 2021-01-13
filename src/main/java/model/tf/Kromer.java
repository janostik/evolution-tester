package model.tf;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Individual;
import util.ArrayUtils;
import util.IndividualUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 * Created by wiki on 29/01/20.
 */
public class Kromer implements TestFunction {

    private int[] L;
    private int[][] c;
    private int dim;
    
    public Kromer(String path) throws FileNotFoundException {

        URL resource = Cec2015.class.getResource(path);
        File fpt = null;
        try {
            fpt = Paths.get(resource.toURI()).toFile();
        } catch (URISyntaxException ex) {
            Logger.getLogger(Kromer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Scanner scan = new Scanner(fpt);
        
        this.dim = scan.nextInt();
        
//        System.out.println("DIM: " + this.dim);
        
        this.L = new int[this.dim];
        
//        System.out.println("L: ");
        
        for(int i = 0; i < this.dim; i++) {
            this.L[i] = scan.nextInt();
//            System.out.print(this.L[i] + " ");
        }
//        System.out.println();
        
//        System.out.println("c: ");
        
        this.c = new int[this.dim][this.dim];
        
        for(int i = 0; i < this.dim; i++) {
            for(int j = 0; j < this.dim; j++) {
                this.c[i][j] = scan.nextInt();
//                System.out.print(this.c[i][j] + " ");
            }
//            System.out.println();
        }
        
        scan.close();
        
    }

    private double srflp_d(int q, int r, int[] perm) {
        
        double length = this.L[perm[q]]/2.0 + this.L[perm[r]]/2.0;
        for(int i = q+1; i < r; i++) {
            length = length + this.L[perm[i]];
        }
        
        return length;
    }
    
    private double srflp_permutation(int[] perm) {
        
        double fit = 0;
        
        for(int q = 0; q < this.dim; q++) {
            for(int r = q+1; r < this.dim; r++) {
                fit = fit + this.c[perm[q]][perm[r]] * srflp_d(q, r, perm);
            }
        }
        
        return fit;
    }
    
    private int[] getPermutation(double[] vector) {
        
        //random key encoding permutation
        return ArrayUtils.argsort(vector);
        
    }
    
    @Override
    public double fitness(Individual individual) {
        return fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {

        return this.srflp_permutation(this.getPermutation(vector));
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
        return 10E-8;
    }

    @Override
    public double optimum() {
        return 0.0;
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
        return "Kromer";
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        
        Kromer kr = new Kromer("/Kromer-data/srflp/Anjos/AnKeVa_2005_60dept_set1.txt");
        
        double[] vector = kr.generateTrial(60);
        
        System.out.println("Vector: " + Arrays.toString(vector));
        System.out.println("Permutation: " + Arrays.toString(kr.getPermutation(vector)));
        System.out.println("Fitness: " + kr.fitness(vector));

    }

    @Override
    public double[] optimumPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
