package model;

import java.util.Arrays;

/**
 * Created by jakub on 27/10/15.
 */
public class Individual {

    public double[] vector;
    public double fitness;
    private int id;

    public Individual(int id, double[] vector, double fitness) {
        this.id = id;
        this.vector = vector;
        this.fitness = fitness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Individual that = (Individual) o;

        if (id != that.id) return false;
        if (Double.compare(that.fitness, fitness) != 0) return false;
        return Arrays.equals(vector, that.vector);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (vector != null ? Arrays.hashCode(vector) : 0);
        temp = Double.doubleToLongBits(fitness);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Individual{" +
                "id=" + id +
                ", vector=" + Arrays.toString(vector) +
                ", fitness=" + fitness +
                '}';
    }
}
