package model.probabilistic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 2 normal generators with different mean values
 * 
 * @author wiki on 17/06/2016
 */
public class RankedNormalGenerator2systems extends RankedNormalGenerator {
    
    public RankedNormalGenerator2systems() {
    }

    public RankedNormalGenerator2systems(util.random.Random normal, double rank) {
        this.normal = normal;
        this.rank = rank;
    }

    @Override
    public List<RankedNormalGenerator2systems> getAllNormalGenerators() {

        List<RankedNormalGenerator2systems> list = new ArrayList<>();

        list.add(new RankedNormalGenerator2systems(new util.random.NormalRandom(0.1, 0.1), 0.5));
        list.add(new RankedNormalGenerator2systems(new util.random.NormalRandom(0.3, 0.1), 0.5));


        return list;

    }

    @Override
    public List<RankedNormalGenerator2systems> getNormalGenerators(List<util.random.Random> normalList) {

        double r = 1.0 / (double) normalList.size();
        List<RankedNormalGenerator2systems> list = new ArrayList<>();

        normalList.stream().forEach((ch) -> {
            list.add(new RankedNormalGenerator2systems(ch, r));
        });

        return list;

    }

}
