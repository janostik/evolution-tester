package util.random;

/**
 *
 * @author adam on 25/11/2015
 */
public class Tinkerbell implements Random {

    final private static model.chaos.Tinkerbell rnd = new model.chaos.Tinkerbell();

    @Override
    public Double nextDouble() {
        return rnd.getRndReal();
    }

    @Override
    public int nextInt(int bound) {
        return rnd.getRndInt(bound);
    }

}
