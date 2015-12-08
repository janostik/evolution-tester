package model.chaos;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wiki on 08/12/2015
 */
public class RankedChaosGenerator {

    public util.random.Random chaos;
    public double rank;

    public RankedChaosGenerator() {
    }

    public RankedChaosGenerator(util.random.Random chaos, double rank) {
        this.chaos = chaos;
        this.rank = rank;
    }

    public static List<RankedChaosGenerator> getAllChaosGenerators() {

        List<RankedChaosGenerator> list = new ArrayList<>();

        list.add(new RankedChaosGenerator(new util.random.BurgersRandom(), 0.2));
        list.add(new RankedChaosGenerator(new util.random.DelayedLogisticRandom(), 0.2));
        list.add(new RankedChaosGenerator(new util.random.DissipativeRandom(), 0.2));
        list.add(new RankedChaosGenerator(new util.random.LoziRandom(), 0.2));
        list.add(new RankedChaosGenerator(new util.random.TinkerbellRandom(), 0.2));

        return list;

    }

    public static List<RankedChaosGenerator> getChaosGenerators(List<util.random.Random> chaosList) {

        double r = 1.0 / (double) chaosList.size();
        List<RankedChaosGenerator> list = new ArrayList<>();

        chaosList.stream().forEach((ch) -> {
            list.add(new RankedChaosGenerator(ch, r));
        });

        return list;

    }

}
