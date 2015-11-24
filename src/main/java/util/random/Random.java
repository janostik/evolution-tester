package util.random;

/**
 *
 * @author adam on 24/11/2015
 */
public interface Random {

    Double nextDouble();

    int nextInt(int bound);

    default double nextDouble(double min, double max) {
        return nextDouble() * (max - min) + min;
    }

}
