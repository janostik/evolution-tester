package model.probabilistic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 5 normal generators with different mean values
 * 
 * @author wiki on 17/06/2016
 */
public class RankedNormalGenerator5systems extends RankedNormalGenerator{

    public RankedNormalGenerator5systems() {
    }

    public RankedNormalGenerator5systems(util.random.Random normal, double rank) {
        this.normal = normal;
        this.rank = rank;
    }

    @Override
    public List<RankedNormalGenerator5systems> getAllNormalGenerators() {

        List<RankedNormalGenerator5systems> list = new ArrayList<>();

        list.add(new RankedNormalGenerator5systems(new util.random.NormalRandom(0.1, 0.1), 0.2));
        list.add(new RankedNormalGenerator5systems(new util.random.NormalRandom(0.3, 0.1), 0.2));
        list.add(new RankedNormalGenerator5systems(new util.random.NormalRandom(0.5, 0.1), 0.2));
        list.add(new RankedNormalGenerator5systems(new util.random.NormalRandom(0.7, 0.1), 0.2));
        list.add(new RankedNormalGenerator5systems(new util.random.NormalRandom(0.9, 0.1), 0.2));

        return list;

    }

    @Override
    public List<RankedNormalGenerator5systems> getNormalGenerators(List<util.random.Random> normalList) {

        double r = 1.0 / (double) normalList.size();
        List<RankedNormalGenerator5systems> list = new ArrayList<>();

        normalList.stream().forEach((ch) -> {
            list.add(new RankedNormalGenerator5systems(ch, r));
        });

        return list;

    }

}
