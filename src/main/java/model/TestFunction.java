package model;

/**
 * Basic interface for test functions.
 * TODO: Can be changed if needed.
 * Created by jakub on 27/10/15.
 */
public interface TestFunction {

    double fitness(Individual individual);

    void constrain(Individual individual);

    double[] generateTrial(int dim);

    double fixedAccLevel();

    double optimum();
}
