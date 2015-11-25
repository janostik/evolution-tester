package util.random;

/**
 *
 * @author adam on 25/11/2015
 */
public class LoziRandom implements Random {

    final private static model.chaos.Lozi rnd = new model.chaos.Lozi();

    @Override
    public Double nextDouble() {
        return rnd.getRndReal();
    }

    @Override
    public int nextInt(int bound) {
        return rnd.getRndInt(bound);
    }

}
