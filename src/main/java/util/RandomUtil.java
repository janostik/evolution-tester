package util;

import java.util.Random;

/**
 * Should contain static access to basic Random functions.
 * TODO: In the future change to interface so the basic random and chaos functions are interchangable.
 * Created by jakub on 27/10/15.
 */
public class RandomUtil {

    private static Random rnd = new Random();

    /*
    Class not to be instantiated
     */
    private RandomUtil() {
    }

    public static Double nextDouble() {
        return rnd.nextDouble();
    }

    public static int nextInt(int bound) {
        return rnd.nextInt(bound);
    }

    public static double nextDouble(double min, double max) {
        return nextDouble() * (max - min) + min;
    }

}
