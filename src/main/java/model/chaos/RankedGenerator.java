package model.chaos;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wiki on 08/12/2015
 */
public class RankedGenerator {

    public util.random.Random chaos;
    public double rank;

    public RankedGenerator() {
    }

    public RankedGenerator(util.random.Random chaos, double rank) {
        this.chaos = chaos;
        this.rank = rank;
    }

    public static List<RankedGenerator> getCompetitiveSystems() {
        
        List<RankedGenerator> list = new ArrayList<>();

        list.add(new RankedGenerator(new util.random.UniformRandom(), 0.5));
        list.add(new RankedGenerator(new util.random.TinkerbellRandom(), 0.5));

        return list;
        
    }
    
    public static List<RankedGenerator> getAllChaosGeneratorsV1() {

        List<RankedGenerator> list = new ArrayList<>();

        list.add(new RankedGenerator(new util.random.BurgersRandom(), 0.2));
        list.add(new RankedGenerator(new util.random.DelayedLogisticRandom(), 0.2));
        list.add(new RankedGenerator(new util.random.DissipativeRandom(), 0.2));
        list.add(new RankedGenerator(new util.random.LoziRandom(), 0.2));
        list.add(new RankedGenerator(new util.random.TinkerbellRandom(), 0.2));

        return list;

    }
    
    public static List<RankedGenerator> getAllChaosGeneratorsV2() {

        List<RankedGenerator> list = new ArrayList<>();

        list.add(new RankedGenerator(new util.random.UniformRandom(), 0.2));
        list.add(new RankedGenerator(new util.random.DelayedLogisticRandom(), 0.2));
        list.add(new RankedGenerator(new util.random.DissipativeRandom(), 0.2));
        list.add(new RankedGenerator(new util.random.ArnoldCatRandom(), 0.2));
        list.add(new RankedGenerator(new util.random.TinkerbellRandom(), 0.2));

        return list;

    }
    
    public static List<RankedGenerator> getAllChaosGeneratorsV3() {

        List<RankedGenerator> list = new ArrayList<>();

        list.add(new RankedGenerator(new util.random.UniformRandom(), 0.2));
        list.add(new RankedGenerator(new util.random.DelayedLogisticRandom(), 0.2));
        list.add(new RankedGenerator(new util.random.DissipativeRandom(), 0.2));
        list.add(new RankedGenerator(new util.random.LoziRandom(), 0.2));
        list.add(new RankedGenerator(new util.random.ArnoldCatRandom(), 0.2));

        return list;

    }
    
    public static List<RankedGenerator> getAllChaosGeneratorsV4() {

        List<RankedGenerator> list = new ArrayList<>();

        list.add(new RankedGenerator(new util.random.UniformRandom(), 1./3.));
        list.add(new RankedGenerator(new util.random.DissipativeRandom(), 1./3.));
        list.add(new RankedGenerator(new util.random.ArnoldCatRandom(), 1./3.));

        return list;

    }

    public static List<RankedGenerator> getChaosGenerators(List<util.random.Random> chaosList) {

        double r = 1.0 / (double) chaosList.size();
        List<RankedGenerator> list = new ArrayList<>();

        chaosList.stream().forEach((ch) -> {
            list.add(new RankedGenerator(ch, r));
        });

        return list;

    }

}
