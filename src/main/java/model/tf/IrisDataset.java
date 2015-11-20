package model.tf;

import java.util.ArrayList;
import java.util.HashMap;
import model.Individual;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import util.RandomUtil;

/**
 *
 * Iris dataset, only in 12D
 * 
 * @author adam on 19/11/2015
 */
public class IrisDataset implements TestFunction {
    
    public static final int EUCLIDIAN_DISTANCE = 1;
    public static final int CHEBYSHEV_DISTANCE = 2;
    
    public static final double[] min = new double[]{4.3,2.0,1.0,0.1};
    public static final double[] max = new double[]{7.9,4.4,6.9,2.5};
    
    public static boolean normalized;

    @Override
    public double fitness(Individual individual) {
        return this.CFEiris(individual.vector, EUCLIDIAN_DISTANCE);
    }

    @Override
    public double fitness(double[] vector) {
        return this.CFEiris(vector, EUCLIDIAN_DISTANCE);
    }

    @Override
    public void constrain(Individual individual) {
        
        for(int i = 0; i< individual.vector.length; i++){
            if(individual.vector[i] > this.max(i)){
                individual.vector[i] = this.max(i);
            }
            if(individual.vector[i] < this.min(i)){
                individual.vector[i] = this.min(i);
            }
        }
        
    }

    @Override
    public double[] generateTrial(int dim) {
        
        double[] vector = new double[dim];
        
        for(int i = 0; i < dim; i++){
            vector[i] = RandomUtil.nextDouble(this.min(i), this.max(i));
        }
        
        return vector;
        
    }

    @Override
    public double fixedAccLevel() {
        return 0;
    }

    @Override
    public double optimum() {
        return 0;
    }

    @Override
    public double max(int dim) {
        
        if(normalized){
            return 1;
        } else {
            return max[dim % 4];
        }
        
    }

    @Override
    public double min(int dim) {
        
        if(normalized){
            return 0;
        } else {
            return min[dim % 4];
        }
        
    }

    @Override
    public String name() {
        return "iris";
    }
    
    public class IrisCentroid {
    
        private double sepal_length;
        private double sepal_width;
        private double petal_length;
        private double petal_width;
        private String iris_class;

        public IrisCentroid() {
        }

        public double getSepal_length() {
            return sepal_length;
        }

        public void setSepal_length(double sepal_length) {
            this.sepal_length = sepal_length;
        }

        public double getSepal_width() {
            return sepal_width;
        }

        public void setSepal_width(double sepal_width) {
            this.sepal_width = sepal_width;
        }

        public double getPetal_length() {
            return petal_length;
        }

        public void setPetal_length(double petal_length) {
            this.petal_length = petal_length;
        }

        public double getPetal_width() {
            return petal_width;
        }

        public void setPetal_width(double petal_width) {
            this.petal_width = petal_width;
        }

        public String getIris_class() {
            return iris_class;
        }

        public void setIris_class(String iris_class) {
            this.iris_class = iris_class;
        }

        @Override
        public String toString() {
            return "IrisCentroid{" + "sepal_length=" + sepal_length + ", sepal_width=" + sepal_width + ", petal_length=" + petal_length + ", petal_width=" + petal_width + ", iris_class=" + iris_class + '}';
        }

    }
    
    public class IrisObject {
        
        private double sepal_length;
        private double sepal_width;
        private double petal_length;
        private double petal_width;
        private String iris_class;

        public IrisObject(double sepal_length, double sepal_width, double petal_length, double petal_width, String iris_class) {

            if(normalized){
                this.sepal_length = (sepal_length - IrisDataset.min[0]) / (IrisDataset.max[0] - IrisDataset.min[0]);
                this.sepal_width = (sepal_width - IrisDataset.min[1]) / (IrisDataset.max[1] - IrisDataset.min[1]);
                this.petal_length = (petal_length - IrisDataset.min[2]) / (IrisDataset.max[2] - IrisDataset.min[2]);
                this.petal_width = (petal_width - IrisDataset.min[3]) / (IrisDataset.max[3] - IrisDataset.min[3]);
            } else {
                this.sepal_length = sepal_length;
                this.sepal_width = sepal_width;
                this.petal_length = petal_length;
                this.petal_width = petal_width;
            }
         
            this.iris_class = iris_class;
        }

        public double getSepal_length() {
            return sepal_length;
        }

        public void setSepal_length(double sepal_length) {
            this.sepal_length = sepal_length;
        }

        public double getSepal_width() {
            return sepal_width;
        }

        public void setSepal_width(double sepal_width) {
            this.sepal_width = sepal_width;
        }

        public double getPetal_length() {
            return petal_length;
        }

        public void setPetal_length(double petal_length) {
            this.petal_length = petal_length;
        }

        public double getPetal_width() {
            return petal_width;
        }

        public void setPetal_width(double petal_width) {
            this.petal_width = petal_width;
        }

        public String getIris_class() {
            return iris_class;
        }

        public void setIris_class(String iris_class) {
            this.iris_class = iris_class;
        }

        @Override
        public String toString() {
            return "IrisObject{" + "sepal_length=" + sepal_length + ", sepal_width=" + sepal_width + ", petal_length=" + petal_length + ", petal_width=" + petal_width + ", iris_class=" + iris_class + '}';
        }

    }
    
    private ArrayList<IrisObject> iris_array = new ArrayList<>();
    private IrisCentroid centroid_setosa;
    private IrisCentroid centroid_versicolor;
    private IrisCentroid centroid_virginica;

    public IrisDataset(boolean normalized) {
        
        this.normalized = normalized;
        
        this.iris_array.add(new IrisObject(5.1,3.5,1.4,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.9,3.0,1.4,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.7,3.2,1.3,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.6,3.1,1.5,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.0,3.6,1.4,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.4,3.9,1.7,0.4,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.6,3.4,1.4,0.3,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.0,3.4,1.5,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.4,2.9,1.4,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.9,3.1,1.5,0.1,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.4,3.7,1.5,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.8,3.4,1.6,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.8,3.0,1.4,0.1,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.3,3.0,1.1,0.1,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.8,4.0,1.2,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.7,4.4,1.5,0.4,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.4,3.9,1.3,0.4,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.1,3.5,1.4,0.3,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.7,3.8,1.7,0.3,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.1,3.8,1.5,0.3,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.4,3.4,1.7,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.1,3.7,1.5,0.4,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.6,3.6,1.0,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.1,3.3,1.7,0.5,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.8,3.4,1.9,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.0,3.0,1.6,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.0,3.4,1.6,0.4,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.2,3.5,1.5,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.2,3.4,1.4,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.7,3.2,1.6,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.8,3.1,1.6,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.4,3.4,1.5,0.4,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.2,4.1,1.5,0.1,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.5,4.2,1.4,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.9,3.1,1.5,0.1,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.0,3.2,1.2,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.5,3.5,1.3,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.9,3.1,1.5,0.1,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.4,3.0,1.3,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.1,3.4,1.5,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.0,3.5,1.3,0.3,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.5,2.3,1.3,0.3,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.4,3.2,1.3,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.0,3.5,1.6,0.6,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.1,3.8,1.9,0.4,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.8,3.0,1.4,0.3,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.1,3.8,1.6,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(4.6,3.2,1.4,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.3,3.7,1.5,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(5.0,3.3,1.4,0.2,"Iris-setosa"));
        this.iris_array.add(new IrisObject(7.0,3.2,4.7,1.4,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.4,3.2,4.5,1.5,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.9,3.1,4.9,1.5,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.5,2.3,4.0,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.5,2.8,4.6,1.5,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.7,2.8,4.5,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.3,3.3,4.7,1.6,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(4.9,2.4,3.3,1.0,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.6,2.9,4.6,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.2,2.7,3.9,1.4,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.0,2.0,3.5,1.0,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.9,3.0,4.2,1.5,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.0,2.2,4.0,1.0,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.1,2.9,4.7,1.4,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.6,2.9,3.6,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.7,3.1,4.4,1.4,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.6,3.0,4.5,1.5,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.8,2.7,4.1,1.0,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.2,2.2,4.5,1.5,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.6,2.5,3.9,1.1,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.9,3.2,4.8,1.8,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.1,2.8,4.0,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.3,2.5,4.9,1.5,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.1,2.8,4.7,1.2,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.4,2.9,4.3,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.6,3.0,4.4,1.4,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.8,2.8,4.8,1.4,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.7,3.0,5.0,1.7,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.0,2.9,4.5,1.5,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.7,2.6,3.5,1.0,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.5,2.4,3.8,1.1,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.5,2.4,3.7,1.0,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.8,2.7,3.9,1.2,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.0,2.7,5.1,1.6,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.4,3.0,4.5,1.5,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.0,3.4,4.5,1.6,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.7,3.1,4.7,1.5,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.3,2.3,4.4,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.6,3.0,4.1,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.5,2.5,4.0,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.5,2.6,4.4,1.2,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.1,3.0,4.6,1.4,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.8,2.6,4.0,1.2,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.0,2.3,3.3,1.0,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.6,2.7,4.2,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.7,3.0,4.2,1.2,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.7,2.9,4.2,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.2,2.9,4.3,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.1,2.5,3.0,1.1,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(5.7,2.8,4.1,1.3,"Iris-versicolor"));
        this.iris_array.add(new IrisObject(6.3,3.3,6.0,2.5,"Iris-virginica"));
        this.iris_array.add(new IrisObject(5.8,2.7,5.1,1.9,"Iris-virginica"));
        this.iris_array.add(new IrisObject(7.1,3.0,5.9,2.1,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.3,2.9,5.6,1.8,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.5,3.0,5.8,2.2,"Iris-virginica"));
        this.iris_array.add(new IrisObject(7.6,3.0,6.6,2.1,"Iris-virginica"));
        this.iris_array.add(new IrisObject(4.9,2.5,4.5,1.7,"Iris-virginica"));
        this.iris_array.add(new IrisObject(7.3,2.9,6.3,1.8,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.7,2.5,5.8,1.8,"Iris-virginica"));
        this.iris_array.add(new IrisObject(7.2,3.6,6.1,2.5,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.5,3.2,5.1,2.0,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.4,2.7,5.3,1.9,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.8,3.0,5.5,2.1,"Iris-virginica"));
        this.iris_array.add(new IrisObject(5.7,2.5,5.0,2.0,"Iris-virginica"));
        this.iris_array.add(new IrisObject(5.8,2.8,5.1,2.4,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.4,3.2,5.3,2.3,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.5,3.0,5.5,1.8,"Iris-virginica"));
        this.iris_array.add(new IrisObject(7.7,3.8,6.7,2.2,"Iris-virginica"));
        this.iris_array.add(new IrisObject(7.7,2.6,6.9,2.3,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.0,2.2,5.0,1.5,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.9,3.2,5.7,2.3,"Iris-virginica"));
        this.iris_array.add(new IrisObject(5.6,2.8,4.9,2.0,"Iris-virginica"));
        this.iris_array.add(new IrisObject(7.7,2.8,6.7,2.0,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.3,2.7,4.9,1.8,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.7,3.3,5.7,2.1,"Iris-virginica"));
        this.iris_array.add(new IrisObject(7.2,3.2,6.0,1.8,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.2,2.8,4.8,1.8,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.1,3.0,4.9,1.8,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.4,2.8,5.6,2.1,"Iris-virginica"));
        this.iris_array.add(new IrisObject(7.2,3.0,5.8,1.6,"Iris-virginica"));
        this.iris_array.add(new IrisObject(7.4,2.8,6.1,1.9,"Iris-virginica"));
        this.iris_array.add(new IrisObject(7.9,3.8,6.4,2.0,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.4,2.8,5.6,2.2,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.3,2.8,5.1,1.5,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.1,2.6,5.6,1.4,"Iris-virginica"));
        this.iris_array.add(new IrisObject(7.7,3.0,6.1,2.3,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.3,3.4,5.6,2.4,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.4,3.1,5.5,1.8,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.0,3.0,4.8,1.8,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.9,3.1,5.4,2.1,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.7,3.1,5.6,2.4,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.9,3.1,5.1,2.3,"Iris-virginica"));
        this.iris_array.add(new IrisObject(5.8,2.7,5.1,1.9,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.8,3.2,5.9,2.3,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.7,3.3,5.7,2.5,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.7,3.0,5.2,2.3,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.3,2.5,5.0,1.9,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.5,3.0,5.2,2.0,"Iris-virginica"));
        this.iris_array.add(new IrisObject(6.2,3.4,5.4,2.3,"Iris-virginica"));
        this.iris_array.add(new IrisObject(5.9,3.0,5.1,1.8,"Iris-virginica"));
        
        this.countCentroids();
    }
    
    private void countCentroids(){
        
        this.centroid_setosa = new IrisCentroid();
        this.centroid_setosa.setIris_class("Iris-setosa");
        
        this.centroid_versicolor = new IrisCentroid();
        this.centroid_versicolor.setIris_class("Iris-versicolor");
        
        this.centroid_virginica = new IrisCentroid();
        this.centroid_virginica.setIris_class("Iris-virginica");
        
        double setosa_pl = 0;
        double setosa_pw = 0;
        double setosa_sl = 0;
        double setosa_sw = 0;
        double setosa_count = 0;
        
        double versicolor_pl = 0;
        double versicolor_pw = 0;
        double versicolor_sl = 0;
        double versicolor_sw = 0;
        double versicolor_count = 0;
        
        double virginica_pl = 0;
        double virginica_pw = 0;
        double virginica_sl = 0;
        double virginica_sw = 0;
        double virginica_count = 0;
        
        for(IrisObject iris : this.iris_array){
            
            switch(iris.getIris_class()){
                
                case "Iris-setosa":
                    setosa_pl += iris.getPetal_length();
                    setosa_pw += iris.getPetal_width();
                    setosa_sl += iris.getSepal_length();
                    setosa_sw += iris.getSepal_width();
                    setosa_count += 1;
                    break;
                case "Iris-versicolor":
                    versicolor_pl += iris.getPetal_length();
                    versicolor_pw += iris.getPetal_width();
                    versicolor_sl += iris.getSepal_length();
                    versicolor_sw += iris.getSepal_width();
                    versicolor_count += 1;
                    break;
                case "Iris-virginica":
                    virginica_pl += iris.getPetal_length();
                    virginica_pw += iris.getPetal_width();
                    virginica_sl += iris.getSepal_length();
                    virginica_sw += iris.getSepal_width();
                    virginica_count += 1;
                    break;
                
            }
            
        }
        
        this.centroid_setosa.setPetal_length(setosa_pl/setosa_count);
        this.centroid_setosa.setPetal_width(setosa_pw/setosa_count);
        this.centroid_setosa.setSepal_length(setosa_sl/setosa_count);
        this.centroid_setosa.setSepal_width(setosa_sw/setosa_count);
        
        this.centroid_versicolor.setPetal_length(versicolor_pl/versicolor_count);
        this.centroid_versicolor.setPetal_width(versicolor_pw/versicolor_count);
        this.centroid_versicolor.setSepal_length(versicolor_sl/versicolor_count);
        this.centroid_versicolor.setSepal_width(versicolor_sw/versicolor_count);
        
        this.centroid_virginica.setPetal_length(virginica_pl/virginica_count);
        this.centroid_virginica.setPetal_width(virginica_pw/virginica_count);
        this.centroid_virginica.setSepal_length(virginica_sl/virginica_count);
        this.centroid_virginica.setSepal_width(virginica_sw/virginica_count);
        
    }
    
    /**
     * Nice printout of classification results.
     * 
     * @param ind
     * @return 
     */
    public double getAccuracyE(Individual ind){
        
        double[] coords = ind.vector.clone();
        double[] firstCoords = new double[4];
        double[] secondCoords = new double[4];
        double[] thirdCoords = new double[4];
        
        firstCoords[0] = coords[0];
        firstCoords[1] = coords[1];
        firstCoords[2] = coords[2];
        firstCoords[3] = coords[3];
        secondCoords[0] = coords[4];
        secondCoords[1] = coords[5];
        secondCoords[2] = coords[6];
        secondCoords[3] = coords[7];
        thirdCoords[0] = coords[8];
        thirdCoords[1] = coords[9];
        thirdCoords[2] = coords[10];
        thirdCoords[3] = coords[11];
        
        String firstClass = this.countCentroidDistance(firstCoords);
        String secondClass = this.countCentroidDistance(secondCoords);
        String thirdClass = this.countCentroidDistance(thirdCoords);
        
//        System.out.println("========================================");
//        System.out.println("Centroid classes:");
//        System.out.println("1 - " + firstClass);
//        System.out.println("2 - " + secondClass);
//        System.out.println("3 - " + thirdClass);
//        System.out.println("========================================");

        String detectedClass;
        HashMap<String, Double> mapResult;
        double irisCentroidDistance;
        int counter = 1;
        boolean classification;
        int goodClassifiactions = 0;
        int badClassifiactions = 0;
        int allClassifiactions = 0;
        ArrayList<IrisObject> wronglyClassfied = new ArrayList<>();
        
        for(IrisObject iris : this.iris_array){
            
            detectedClass = "no class";
            allClassifiactions++;
            classification = false;
            irisCentroidDistance = Double.MAX_VALUE;
            
//            System.out.println("========================================");
            
            /**
             * Classifictaion + error counting
             */
            mapResult = this.countEuclidianDistance(iris, coords);
            
            if(mapResult.containsKey("first")){
                detectedClass = firstClass;
                irisCentroidDistance = mapResult.get("first");
                if(iris.getIris_class().equals(firstClass)){
                    classification = true;
                    goodClassifiactions++;
                }
                else{
                    badClassifiactions++;
                    wronglyClassfied.add(iris);
                }
            }
            
            if(mapResult.containsKey("second")){
                detectedClass = secondClass;
                irisCentroidDistance = mapResult.get("second");
                if(iris.getIris_class().equals(secondClass)){
                    classification = true;
                    goodClassifiactions++;
                }
                else{
                    badClassifiactions++;
                    wronglyClassfied.add(iris);
                }
            }
            
            if(mapResult.containsKey("third")){
                detectedClass = thirdClass;
                irisCentroidDistance = mapResult.get("third");
                if(iris.getIris_class().equals(thirdClass)){
                    classification = true;
                    goodClassifiactions++;
                }
                else{
                    badClassifiactions++;
                    wronglyClassfied.add(iris);
                }
            }
            
//            System.out.println(counter + " - OC: " + iris.getIris_class() + " DC: " + detectedClass + " CR: " + classification + " CD: " + irisCentroidDistance);
            counter++;
        }
        
//        System.out.println("========================================");
//        
//        System.out.println("Good classifications: " + goodClassifiactions + "/" + allClassifiactions + " = " + (goodClassifiactions/(double) allClassifiactions)*100.0 + "%");
//        System.out.println("Wrong classifications: " + badClassifiactions + "/" + allClassifiactions + " = " + (badClassifiactions/(double) allClassifiactions)*100.0 + "%");
//        
//        System.out.println("========================================");
//        
//        System.out.println("Wrongly classified irises:");
//        for(IrisObject iris : wronglyClassfied){
//            System.out.println(iris);
//        }
        
        return (goodClassifiactions/(double) allClassifiactions);
    }
    
    /**
     * Nice printout of classification results.
     * 
     * @param ind
     * @return 
     */
    public double getAccuracyCH(Individual ind){
        
        double[] coords = ind.vector.clone();
        double[] firstCoords = new double[4];
        double[] secondCoords = new double[4];
        double[] thirdCoords = new double[4];
        
        firstCoords[0] = coords[0];
        firstCoords[1] = coords[1];
        firstCoords[2] = coords[2];
        firstCoords[3] = coords[3];
        secondCoords[0] = coords[4];
        secondCoords[1] = coords[5];
        secondCoords[2] = coords[6];
        secondCoords[3] = coords[7];
        thirdCoords[0] = coords[8];
        thirdCoords[1] = coords[9];
        thirdCoords[2] = coords[10];
        thirdCoords[3] = coords[11];
        
        String firstClass = this.countCentroidDistanceCH(firstCoords);
        String secondClass = this.countCentroidDistanceCH(secondCoords);
        String thirdClass = this.countCentroidDistanceCH(thirdCoords);
        
//        System.out.println("========================================");
//        System.out.println("Centroid classes:");
//        System.out.println("1 - " + firstClass);
//        System.out.println("2 - " + secondClass);
//        System.out.println("3 - " + thirdClass);
//        System.out.println("========================================");

        String detectedClass;
        HashMap<String, Double> mapResult;
        double irisCentroidDistance;
        int counter = 1;
        boolean classification;
        int goodClassifiactions = 0;
        int badClassifiactions = 0;
        int allClassifiactions = 0;
        ArrayList<IrisObject> wronglyClassfied = new ArrayList<>();
        
        for(IrisObject iris : this.iris_array){
            
            detectedClass = "no class";
            allClassifiactions++;
            classification = false;
            irisCentroidDistance = Double.MAX_VALUE;
            
//            System.out.println("========================================");
            
            /**
             * Classifictaion + error counting
             */
            mapResult = this.countChebyshevDistance(iris, coords);
            
            if(mapResult.containsKey("first")){
                detectedClass = firstClass;
                irisCentroidDistance = mapResult.get("first");
                if(iris.getIris_class().equals(firstClass)){
                    classification = true;
                    goodClassifiactions++;
                }
                else{
                    badClassifiactions++;
                    wronglyClassfied.add(iris);
                }
            }
            
            if(mapResult.containsKey("second")){
                detectedClass = secondClass;
                irisCentroidDistance = mapResult.get("second");
                if(iris.getIris_class().equals(secondClass)){
                    classification = true;
                    goodClassifiactions++;
                }
                else{
                    badClassifiactions++;
                    wronglyClassfied.add(iris);
                }
            }
            
            if(mapResult.containsKey("third")){
                detectedClass = thirdClass;
                irisCentroidDistance = mapResult.get("third");
                if(iris.getIris_class().equals(thirdClass)){
                    classification = true;
                    goodClassifiactions++;
                }
                else{
                    badClassifiactions++;
                    wronglyClassfied.add(iris);
                }
            }
            
//            System.out.println(counter + " - OC: " + iris.getIris_class() + " DC: " + detectedClass + " CR: " + classification + " CD: " + irisCentroidDistance);
            counter++;
        }
        
        System.out.println("========================================");
        
        System.out.println("Good classifications: " + goodClassifiactions + "/" + allClassifiactions + " = " + (goodClassifiactions/(double) allClassifiactions)*100.0 + "%");
        System.out.println("Wrong classifications: " + badClassifiactions + "/" + allClassifiactions + " = " + (badClassifiactions/(double) allClassifiactions)*100.0 + "%");
        
        System.out.println("========================================");
        
//        System.out.println("Wrongly classified irises:");
//        for(IrisObject iris : wronglyClassfied){
//            System.out.println(iris);
//        }
        
        return (goodClassifiactions/(double) allClassifiactions);
    }
    
    public double CFEiris(double[] coords, int distanceMeasure){
        
        double first_set = 0;
        double first_count = 0;
        double second_set = 0;
        double second_count = 0;
        double third_set = 0;
        double third_count = 0;
        
        HashMap<String, Double> mapResult = new HashMap<>();
        
        for(IrisObject singleIris : this.iris_array){
            
            switch(distanceMeasure){
                case EUCLIDIAN_DISTANCE:
                    mapResult = this.countEuclidianDistance(singleIris, coords);
                    break;
                case CHEBYSHEV_DISTANCE:
                    mapResult = this.countChebyshevDistance(singleIris, coords);
                    break;
            }
            
            if(mapResult.containsKey("first")){
                first_set += mapResult.get("first");
                first_count += 1;
            }
            
            if(mapResult.containsKey("second")){
                second_set += mapResult.get("second");
                second_count += 1;
            }
            
            if(mapResult.containsKey("third")){
                third_set += mapResult.get("third");
                third_count += 1;
            }
            
        }
        
        double toRet = 1;
        if(first_count != 0){
            toRet += (first_set);
        }
        else{
            toRet += Double.MAX_VALUE;
        }
        if(second_count != 0){
            toRet += (second_set);
        }
        else{
            toRet += Double.MAX_VALUE;
        }
        if(third_count != 0){
            toRet += (third_set);
        }
        else{
            toRet += Double.MAX_VALUE;
        }
        
        return 1 - (1.0 / toRet);
    }
    
    private String countCentroidDistance(double[] coords){
        
        double setosa_distance = Math.pow((this.centroid_setosa.getSepal_length() - coords[0]),2) + Math.pow((this.centroid_setosa.getSepal_width() - coords[1]),2) + Math.pow((this.centroid_setosa.getPetal_length() - coords[2]),2) + Math.pow((this.centroid_setosa.getPetal_width() - coords[3]),2);
        double versicolor_distance = Math.pow((this.centroid_versicolor.getSepal_length() - coords[0]),2) + Math.pow((this.centroid_versicolor.getSepal_width() - coords[1]),2) + Math.pow((this.centroid_versicolor.getPetal_length() - coords[2]),2) + Math.pow((this.centroid_versicolor.getPetal_width() - coords[3]),2);
        double virginica_distance = Math.pow((this.centroid_virginica.getSepal_length() - coords[0]),2) + Math.pow((this.centroid_virginica.getSepal_width() - coords[1]),2) + Math.pow((this.centroid_virginica.getPetal_length() - coords[2]),2) + Math.pow((this.centroid_virginica.getPetal_width() - coords[3]),2);
        
        double min = setosa_distance;
        String cl = "Iris-setosa";
        if(versicolor_distance <= min){
            min = versicolor_distance;
            cl = "Iris-versicolor";
        }
        if(virginica_distance <= min){
            min = virginica_distance;
            cl = "Iris-virginica";
        }
        
        return cl;
    }
    
    private String countCentroidDistanceCH(double[] coords){
        
        double tmp;
        double setosa_distance = 0;
        tmp = Math.abs(this.centroid_setosa.getSepal_length() - coords[0]);
        if(tmp >= setosa_distance){
            setosa_distance = tmp;
        }
        tmp = Math.abs(this.centroid_setosa.getSepal_width() - coords[1]);
        if(tmp >= setosa_distance){
            setosa_distance = tmp;
        }
        tmp = Math.abs(this.centroid_setosa.getPetal_length() - coords[2]);
        if(tmp >= setosa_distance){
            setosa_distance = tmp;
        }
        tmp = Math.abs(this.centroid_setosa.getPetal_width() - coords[3]);
        if(tmp >= setosa_distance){
            setosa_distance = tmp;
        }
        
        
        double versicolor_distance = 0;
        tmp = Math.abs(this.centroid_versicolor.getSepal_length() - coords[0]);
        if(tmp >= versicolor_distance){
            versicolor_distance = tmp;
        }
        tmp = Math.abs(this.centroid_versicolor.getSepal_width() - coords[1]);
        if(tmp >= versicolor_distance){
            versicolor_distance = tmp;
        }
        tmp = Math.abs(this.centroid_versicolor.getPetal_length() - coords[2]);
        if(tmp >= versicolor_distance){
            versicolor_distance = tmp;
        }
        tmp = Math.abs(this.centroid_versicolor.getPetal_width() - coords[3]);
        if(tmp >= versicolor_distance){
            versicolor_distance = tmp;
        }
        
        double virginica_distance = 0;
        tmp = Math.abs(this.centroid_virginica.getSepal_length() - coords[0]);
        if(tmp >= virginica_distance){
            virginica_distance = tmp;
        }
        tmp = Math.abs(this.centroid_virginica.getSepal_width() - coords[1]);
        if(tmp >= virginica_distance){
            virginica_distance = tmp;
        }
        tmp = Math.abs(this.centroid_virginica.getPetal_length() - coords[2]);
        if(tmp >= virginica_distance){
            virginica_distance = tmp;
        }
        tmp = Math.abs(this.centroid_virginica.getPetal_width() - coords[3]);
        if(tmp >= virginica_distance){
            virginica_distance = tmp;
        }
                
        double min = setosa_distance;
        String cl = "Iris-setosa";
        if(versicolor_distance <= min){
            min = versicolor_distance;
            cl = "Iris-versicolor";
        }
        if(virginica_distance <= min){
            min = virginica_distance;
            cl = "Iris-virginica";
        }
        
        return cl;
    }
    
    /**
     * 
     * @param iris
     * @param coords
     * @return 
     */
    private HashMap<String, Double> countChebyshevDistance(IrisObject iris, double[] coords){
        
        HashMap<String, Double> mapToRet = new HashMap<>();
        
//        double first_distance = Math.sqrt(Math.pow((iris.getSepal_length() - coords[0]),2) + Math.pow((iris.getSepal_width() - coords[1]),2) + Math.pow((iris.getPetal_length() - coords[2]),2) + Math.pow((iris.getPetal_width() - coords[3]),2));
//        double second_distance = Math.sqrt(Math.pow((iris.getSepal_length() - coords[4]),2) + Math.pow((iris.getSepal_width() - coords[5]),2) + Math.pow((iris.getPetal_length() - coords[6]),2) + Math.pow((iris.getPetal_width() - coords[7]),2));
//        double third_distance = Math.sqrt(Math.pow((iris.getSepal_length() - coords[8]),2) + Math.pow((iris.getSepal_width() - coords[9]),2) + Math.pow((iris.getPetal_length() - coords[10]),2) + Math.pow((iris.getPetal_width() - coords[11]),2));

        double max1 = Math.abs(iris.getSepal_length() - coords[0]);
        if(Math.abs(iris.getSepal_width() - coords[1]) >= max1){
            max1 = Math.abs(iris.getSepal_width() - coords[1]);
        }
        if(Math.abs(iris.getPetal_length() - coords[2]) >= max1){
            max1 = Math.abs(iris.getPetal_length() - coords[2]);
        }
        if(Math.abs(iris.getPetal_width() - coords[3]) >= max1){
            max1 = Math.abs(iris.getPetal_width() - coords[3]);
        }
        
        double max2 = Math.abs(iris.getSepal_length() - coords[4]);
        if(Math.abs(iris.getSepal_width() - coords[5]) >= max2){
            max2 = Math.abs(iris.getSepal_width() - coords[5]);
        }
        if(Math.abs(iris.getPetal_length() - coords[6]) >= max2){
            max2 = Math.abs(iris.getPetal_length() - coords[6]);
        }
        if(Math.abs(iris.getPetal_width() - coords[7]) >= max2){
            max2 = Math.abs(iris.getPetal_width() - coords[7]);
        }
        
        double max3 = Math.abs(iris.getSepal_length() - coords[8]);
        if(Math.abs(iris.getSepal_width() - coords[9]) >= max3){
            max3 = Math.abs(iris.getSepal_width() - coords[9]);
        }
        if(Math.abs(iris.getPetal_length() - coords[10]) >= max3){
            max3 = Math.abs(iris.getPetal_length() - coords[10]);
        }
        if(Math.abs(iris.getPetal_width() - coords[11]) >= max3){
            max3 = Math.abs(iris.getPetal_width() - coords[11]);
        }
        
        double first_distance = max1;
        double second_distance = max2;
        double third_distance = max3;
        
        double min = first_distance;
        String cl = "first";
        if(second_distance <= min){
            min = second_distance;
            cl = "second";
        }
        if(third_distance <= min){
            min = third_distance;
            cl = "third";
        }
        
        mapToRet.put(cl, min);
        
        return mapToRet;
        
    }
    
    /**
     * 
     * @param iris
     * @param coords
     * @return 
     */
    private HashMap<String, Double> countEuclidianDistance(IrisObject iris, double[] coords){
        
        HashMap<String, Double> mapToRet = new HashMap<>();

        double first_distance = Math.pow((iris.getSepal_length() - coords[0]),2) + Math.pow((iris.getSepal_width() - coords[1]),2) + Math.pow((iris.getPetal_length() - coords[2]),2) + Math.pow((iris.getPetal_width() - coords[3]),2);
        double second_distance = Math.pow((iris.getSepal_length() - coords[4]),2) + Math.pow((iris.getSepal_width() - coords[5]),2) + Math.pow((iris.getPetal_length() - coords[6]),2) + Math.pow((iris.getPetal_width() - coords[7]),2);
        double third_distance = Math.pow((iris.getSepal_length() - coords[8]),2) + Math.pow((iris.getSepal_width() - coords[9]),2) + Math.pow((iris.getPetal_length() - coords[10]),2) + Math.pow((iris.getPetal_width() - coords[11]),2);

        double min = first_distance;
        String cl = "first";
        if(second_distance <= min){
            min = second_distance;
            cl = "second";
        }
        if(third_distance <= min){
            min = third_distance;
            cl = "third";
        }
        
        mapToRet.put(cl, min);
        
        return mapToRet;
        
    }
    
    public ArrayList<DoublePoint> getDoublePointsDataset(){
        
        ArrayList<DoublePoint> points = new ArrayList<>();
        
        for (IrisObject singleIris : this.iris_array) {
            double[] point = new double[4];
            point[0] = singleIris.getSepal_length();
            point[1] = singleIris.getSepal_width();
            point[2] = singleIris.getPetal_length();
            point[3] = singleIris.getPetal_width();
            points.add(new DoublePoint(point));
        }
        
        return points;
        
    }

    public ArrayList<IrisObject> getIris_array() {
        return iris_array;
    }

    public void setIris_array(ArrayList<IrisObject> iris_array) {
        this.iris_array = iris_array;
    }

    public IrisCentroid getCentroid_setosa() {
        return centroid_setosa;
    }

    public void setCentroid_setosa(IrisCentroid centroid_setosa) {
        this.centroid_setosa = centroid_setosa;
    }

    public IrisCentroid getCentroid_versicolor() {
        return centroid_versicolor;
    }

    public void setCentroid_versicolor(IrisCentroid centroid_versicolor) {
        this.centroid_versicolor = centroid_versicolor;
    }

    public IrisCentroid getCentroid_virginica() {
        return centroid_virginica;
    }

    public void setCentroid_virginica(IrisCentroid centroid_virginica) {
        this.centroid_virginica = centroid_virginica;
    }

    @Override
    public String toString() {
        return "IrisDataset{" + "centroid_setosa=" + centroid_setosa + ", centroid_versicolor=" + centroid_versicolor + ", centroid_virginica=" + centroid_virginica + '}';
    }
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
    
        IrisDataset ids = new IrisDataset(true);
        
        System.out.println(ids);
        
//        double[] test = {0.594079, 0.799372, 0.973118, 0.730321, 0.515067, 0.992759, 0.853417, 0.465985, 0.892907, 0.758765, 0.362434, 0.537252};
//        
//        System.out.println(ids.CFEiris(test, EUCLIDIAN_DISTANCE));
//        
//        double[] min = new double[12];
//        double[] max = new double[12];
//        for(int i = 0; i < 12; i++){
//            min[i] = 0;
//            max[i] = 1;
//        }
//        
//        Individual ind = new Individual(12, min, max);
//        ind.setFeatures(new double[]{0.19611111111111118, 0.5908333333333332, 0.07864406779661014,0.05999999999999999,0.6355555555555555, 0.4058333333333334, 0.7715254237288134,0.8024999999999998,0.4544444444444445, 0.3208333333333332, 0.5525423728813559, 0.5108333333333333});
//        
//        System.out.println(ids.CFEiris(ind.getFeatures(), EUCLIDIAN_DISTANCE));
//        System.out.println(ids.getAccuracyE(ind));
//        
//        System.out.println("{" + ids.getCentroid_setosa().getSepal_length() + ", " + ids.getCentroid_setosa().getSepal_width() + ", " +ids.getCentroid_setosa().getPetal_length() + ", " +ids.getCentroid_setosa().getPetal_width() + "}");
//        System.out.println("{" + ids.getCentroid_virginica().getSepal_length() + ", " + ids.getCentroid_virginica().getSepal_width() + ", " +ids.getCentroid_virginica().getPetal_length() + ", " +ids.getCentroid_virginica().getPetal_width() + "}");
//        System.out.println("{" + ids.getCentroid_versicolor().getSepal_length() + ", " + ids.getCentroid_versicolor().getSepal_width() + ", " +ids.getCentroid_versicolor().getPetal_length() + ", " +ids.getCentroid_versicolor().getPetal_width() + "}");
//        
//        PrintWriter writer;
//        
//        try {
//            writer = new PrintWriter("IrisData.txt", "UTF-8");
//        
//        for(IrisObject iris : ids.getIris_array()){
//            
//            writer.println("{" + iris.getSepal_length() + ", " + iris.getSepal_width() + ", " + iris.getPetal_length() + ", " + iris.getPetal_width() + "},");
//            
//        }
//        
//        writer.close();
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(IrisDataset.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(IrisDataset.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }
    
}
