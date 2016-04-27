package util.random;

import org.apache.commons.math3.random.AbstractRandomGenerator;

/**
 *
 * @author adam on 24/11/2015
 */
public class UniformRandomSeed extends AbstractRandomGenerator implements Random  {

    private java.util.Random rnd;

    public UniformRandomSeed(long seed) {
        rnd = new java.util.Random(seed);
    }

    @Override
    public double nextDouble() {
        return rnd.nextDouble();
    }

    @Override
    public int nextInt(int bound) {
        return rnd.nextInt(bound);
    }
    
    @Override
    public String toString() {
        return "UniformSeed";
    }

    @Override
    public void setSeed(long l) {
        rnd = new java.util.Random(l);
    }

}
