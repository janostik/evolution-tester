package algorithm.de;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Ackley;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.distance.EuclideanDistance;
import util.random.Random;

/**
 *
 * L-SHADE switching between two F memories, one for exploitation (objective function value based) and one for exploration (distance based)
 * 
 * @author adam on 26/06/2017
 */
public class SwEEL_SHADE extends SHADE {

    protected final int minPopSize;
    protected final int maxPopSize;
    double[] M_dF;
    List<Double> S_dF;
    
    public SwEEL_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize) {
        super(D, MAXFES, f, H, NP, rndGenerator);
        this.minPopSize = minPopSize;
        this.maxPopSize = NP;
    }
 
    @Override
    public String getName() {
        return "SwEEL_SHADE";
    }
    
    @Override
    public Individual runAlgorithm() {

        /**
         * Initialization
         */
        this.G = 0;
        this.Aext = new ArrayList<>();
        this.best = null;
        this.bestHistory = new ArrayList<>();

        /**
         * Initial population
         */
        initializePopulation();

        this.M_F = new double[this.H];
        this.M_dF = new double[this.H];
        this.M_CR = new double[this.H];

        for (int h = 0; h < this.H; h++) {
            this.M_F[h] = 0.5;
            this.M_dF[h] = 0.5;
            this.M_CR[h] = 0.5;
        }

        /**
         * Generation iteration;
         */
        boolean exploration = false; //false - exploitation, true - exploration
        boolean improvement;
        int r, Psize, pbestIndex;
        double Fg, CRg;
        List<Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial;
        Individual x, pbestInd;
        List<Double> wS, dwS;
        double wSsum, dwSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2, meanS_dF1, meanS_dF2;
        int k = 0;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;

        EuclideanDistance euclid = new EuclideanDistance();
        
        while (true) {

            improvement = false;
            
            this.G++;
            this.S_F = new ArrayList<>();
            this.S_dF = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();
            dwS = new ArrayList<>();

            newPop = new ArrayList<>();

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
                r = rndGenerator.nextInt(this.H);
                
                if(exploration) {
                    
                    Fg = OtherDistributionsUtil.cauchy(this.M_dF[r], 0.1);
                    while (Fg <= 0) {
                        Fg = OtherDistributionsUtil.cauchy(this.M_dF[r], 0.1);
                    }
                    if (Fg > 2) {
                        Fg = 2;
                    }
                    
                } else {
                
                    Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                    while (Fg <= 0) {
                        Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                    }
                    if (Fg > 1) {
                        Fg = 1;
                    }
                    
                }

                CRg = OtherDistributionsUtil.normal(this.M_CR[r], 0.1);
                if (CRg > 1) {
                    CRg = 1;
                }
                if (CRg < 0) {
                    CRg = 0;
                }

                Psize = (int) (rndGenerator.nextDouble(pmin, 0.2) * this.NP);
                if (Psize < 2) {
                    Psize = 2;
                }

                pBestArray = new ArrayList<>();
                pBestArray.addAll(this.P);
                pBestArray = this.resize(pBestArray, Psize);

                /**
                 * Parent selection
                 */
                pbestInd = this.getRandBestFromList(pBestArray, x.id);
                pbestIndex = this.getPbestIndex(pbestInd);
                pbest = pbestInd.vector.clone();
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size(), pbestIndex);
                pr1 = this.P.get(rIndexes[0]).vector.clone();
                if (rIndexes[1] > this.NP - 1) {
                    pr2 = this.Aext.get(rIndexes[1] - this.NP).vector.clone();
                } else {
                    pr2 = this.P.get(rIndexes[1]).vector.clone();
                }
                parents = new ArrayList<>();
                parents.add(x.vector);
                parents.add(pbest);
                parents.add(pr1);
                parents.add(pr2);
                
                /**
                 * Mutation
                 */               
                v = mutation(parents, Fg);

                /**
                 * Crossover
                 */
                u = crossover(CRg, v, x.vector);

                /**
                 * Constrain check
                 */
                u = constrainCheck(u, x.vector);

                /**
                 * Trial ready
                 */
                id++;
                trial = new Individual(String.valueOf(id), u, f.fitness(u));

                /**
                 * Trial is better
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                    this.S_F.add(Fg);
                    this.S_dF.add(Fg);
                    this.S_CR.add(CRg);
                    this.Aext.add(x);
                    wS.add(x.fitness - trial.fitness);
                    dwS.add(euclid.getDistance(x.vector, trial.vector));
                    
                    improvement = true;
                    
                } else {
                    newPop.add(x);
                }

                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (this.FES >= this.MAXFES) {
                    break;
                }

                this.Aext = this.resizeAext(this.Aext, this.NP);
                
            }

            if (this.FES >= this.MAXFES) {
                break;
            }

            /**
             * Memories update
             */
            if (this.S_F.size() > 0) {
                wSsum = 0;
                dwSsum = 0;
                for (Double num : wS) {
                    wSsum += num;
                }
                for (Double num : dwS) {
                    dwSsum += num;
                }
                meanS_F1 = 0;
                meanS_F2 = 0;
                meanS_dF1 = 0;
                meanS_dF2 = 0;
                meanS_CR1 = 0;
                meanS_CR2 = 0;

                for (int s = 0; s < this.S_F.size(); s++) {
                    meanS_F1 += (wS.get(s) / wSsum) * this.S_F.get(s) * this.S_F.get(s);
                    meanS_F2 += (wS.get(s) / wSsum) * this.S_F.get(s);
                    meanS_dF1 += (dwS.get(s) / dwSsum) * this.S_dF.get(s) * this.S_dF.get(s);
                    meanS_dF2 += (dwS.get(s) / dwSsum) * this.S_dF.get(s);
                    meanS_CR1 += (wS.get(s) / wSsum) * this.S_CR.get(s) * this.S_CR.get(s);
                    meanS_CR2 += (wS.get(s) / wSsum) * this.S_CR.get(s);
                }

                this.M_F[k] = (meanS_F1 / meanS_F2);
                this.M_dF[k] = (meanS_dF1 / meanS_dF2);
                this.M_CR[k] = (meanS_CR1 / meanS_CR2);

                k++;
                if (k >= this.H) {
                    k = 0;
                }
            }
            
            if(!improvement) {
                exploration = !exploration;
            }
            
            /**
             * Resize of population and archives
             */
            this.P = new ArrayList<>();
            this.P.addAll(newPop);
            NP = (int) Math.round(this.maxPopSize - ((double) this.FES/(double) this.MAXFES)*(this.maxPopSize - this.minPopSize));
            P = this.resizePop(P, NP);

        }

        return this.best;

    }
    
    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<Individual> resizePop(List<Individual> list, int size) {

        if(size == list.size()){
            return list;
        }
        
        List<Individual> toRet = new ArrayList<>();
        List<Individual> tmp = new ArrayList<>();
        tmp.addAll(list);
        Individual bestInd;

        for (int i = 0; i < size; i++) {
            bestInd = this.getBestFromList(tmp);
            toRet.add(bestInd);
            tmp.remove(bestInd);
        }

        return toRet;

    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        int NP = 100;
        int minNP = 20;
        int MAXFES = 10000 * dimension;
        int funcNumber = 3;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 10;
        long seed = 10304050L;
        util.random.Random generator = new util.random.UniformRandom();

        SwEEL_SHADE shade;

        int runs = 3;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new SwEEL_SHADE(dimension, MAXFES, tf, H, NP, generator, minNP);

            shade.runAlgorithm();

//            PrintWriter writer;
//
//            try {
//                writer = new PrintWriter("CEC2015-" + funcNumber + "-shade" + k + ".txt", "UTF-8");
//
//                writer.print("{");
//
//                for (int i = 0; i < shade.getBestHistory().size(); i++) {
//
//                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(i).fitness));
//
//                    if (i != shade.getBestHistory().size() - 1) {
//                        writer.print(",");
//                    }
//
//                }
//
//                writer.print("}");
//
//                writer.close();
//
//            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
//                Logger.getLogger(ShaDE.class.getName()).log(Level.SEVERE, null, ex);
//            }

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println(shade.getBest().fitness - tf.optimum());
            System.out.println(Arrays.toString(shade.getBest().vector));
            
//            for(Individual ind : shade.P){
//                System.out.println("ID: " + ind.id + " - fitness: " + ind.fitness);
//            }
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");

    }
    
}
