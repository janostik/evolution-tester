package model.probabilistic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 3 normal generators with different mean values
 * 
 * @author wiki on 17/06/2016
 */
public class RankedNormalGenerator3systems extends RankedNormalGenerator {

    public RankedNormalGenerator3systems() {
    }

    public RankedNormalGenerator3systems(util.random.Random normal, double rank) {
        this.normal = normal;
        this.rank = rank;
    }

    @Override
    public List<RankedNormalGenerator3systems> getAllNormalGenerators() {

        List<RankedNormalGenerator3systems> list = new ArrayList<>();

        list.add(new RankedNormalGenerator3systems(new util.random.NormalRandom(0.1, 0.1), 0.34));
        list.add(new RankedNormalGenerator3systems(new util.random.NormalRandom(0.3, 0.1), 0.33));
        list.add(new RankedNormalGenerator3systems(new util.random.NormalRandom(0.5, 0.1), 0.33));


        return list;

    }

    @Override
    public List<RankedNormalGenerator3systems> getNormalGenerators(List<util.random.Random> normalList) {

        double r = 1.0 / (double) normalList.size();
        List<RankedNormalGenerator3systems> list = new ArrayList<>();

        normalList.stream().forEach((ch) -> {
            list.add(new RankedNormalGenerator3systems(ch, r));
        });

        return list;

    }

}
