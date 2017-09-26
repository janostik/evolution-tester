package algorithm.de;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import model.Individual;
import model.net.BidirectionalEdge;
import model.net.Edge;
import model.net.Net;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.random.Random;

/**
 * 
 * SHADE algorithm with linear decrease of population size.
 * Individuals that are going to be killed are selected based on their centrality.
 * 
 * @author adam on 05/04/2016
 */
public class NLc_SHADE extends Lfv_SHADE {

    Net net = new Net();
    
    public NLc_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize) {
        super(D, MAXFES, f, H, NP, rndGenerator, minPopSize);
    }
    
    @Override
    public String getName() {
        return "NLc_SHADE";
    }
    
    @Override
    public Individual run() {

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
        this.M_CR = new double[this.H];

        for (int h = 0; h < this.H; h++) {
            this.M_F[h] = 0.5;
            this.M_CR[h] = 0.5;
        }

        /**
         * Generation iteration;
         */
        int r, Psize, pbestIndex;
        double Fg, CRg;
        List<Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        Individual trial;
        Individual x;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR1, meanS_CR2;
        int k = 0;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;
        Individual[] parentArray; //for edge creation
        Edge edge;

        while (true) {

            this.G++;
            this.S_F = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();

            newPop = new ArrayList<>();

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
                r = rndGenerator.nextInt(this.H);
                Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                while (Fg <= 0) {
                    Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                }
                if (Fg > 1) {
                    Fg = 1;
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
                parentArray = new Individual[4];
                parentArray[0] = x;
                parentArray[1] = this.getRandBestFromList(pBestArray, x.id);
                pbestIndex = this.getPbestIndex(parentArray[1]);
                pbest = parentArray[1].vector.clone();
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size(), pbestIndex);
                parentArray[2] = this.P.get(rIndexes[0]);
                pr1 = parentArray[2].vector.clone();
                if (rIndexes[1] > this.NP - 1) {
                    pr2 = this.Aext.get(rIndexes[1] - this.NP).vector.clone();
                    parentArray[3] = null;
                } else {
                    parentArray[3] = this.P.get(rIndexes[1]);
                    pr2 = parentArray[3].vector.clone();
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
//                id++;
//                trial = new Individual(String.valueOf(id), u, f.fitness(u));
                trial = new Individual(x.id, u, f.fitness(u));

                /**
                 * Trial is better
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                    this.S_F.add(Fg);
                    this.S_CR.add(CRg);
                    this.Aext.add(x);
                    wS.add(Math.abs(trial.fitness - x.fitness));
                    
                    for (int par = 1; par < parentArray.length; par++ ) {
                        if(parentArray[par] == null){
                            continue;
                        }
                        edge = new BidirectionalEdge(parentArray[par], trial);
                        edge.iter = G;
                        net.addEdge(edge);
                    }
                    
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
                for (Double num : wS) {
                    wSsum += num;
                }
                meanS_F1 = 0;
                meanS_F2 = 0;
                meanS_CR1 = 0;
                meanS_CR2 = 0;

                for (int s = 0; s < this.S_F.size(); s++) {
                    meanS_F1 += (wS.get(s) / wSsum) * this.S_F.get(s) * this.S_F.get(s);
                    meanS_F2 += (wS.get(s) / wSsum) * this.S_F.get(s);
                    meanS_CR1 += (wS.get(s) / wSsum) * this.S_CR.get(s) * this.S_CR.get(s);
                    meanS_CR2 += (wS.get(s) / wSsum) * this.S_CR.get(s);
                }

                this.M_F[k] = (meanS_F1 / meanS_F2);
                this.M_CR[k] = (meanS_CR1 / meanS_CR2);

                k++;
                if (k >= this.H) {
                    k = 0;
                }
            }
            
            /**
             * Resize of population and archives
             */
            this.P = new ArrayList<>();
            this.P.addAll(newPop);
            NP = (int) Math.round(this.maxPopSize - ((double) this.FES/(double) this.MAXFES)*(this.maxPopSize - this.minPopSize));
            
            /**
             * Print out of the nodes and their centrality
             */
//            System.out.println("-------------------");
//            net.getDegreeMap().entrySet().stream().forEach((entry) -> {
//                System.out.println("ID: " + entry.getKey().id + " - degree: " + entry.getValue() + " - fitness: " + entry.getKey().fitness);
//            });
//            System.out.println("-------------------");
            
            P = this.resizePop(P, NP);
            
            /**
             * Print out of the nodes and their centrality
             */
//            System.out.println("-------------------");
//            net.getDegreeMap().entrySet().stream().forEach((entry) -> {
//                System.out.println("ID: " + entry.getKey().id + " - degree: " + entry.getValue() + " - fitness: " + entry.getKey().fitness);
//            });
//            System.out.println("-------------------");

        }

        return this.best;

    }
    
    /**
     *
     * @param list
     * @param size
     * @return
     */
    @Override
    protected List<Individual> resizePop(List<Individual> list, int size) {

        if(size == list.size()){
            return list;
        }
        
        List<Individual> toRet = new ArrayList<>();
        List<Individual> tmp = new ArrayList<>();
        Net tmpNet = new Net(this.net);
        Individual tmpInd;
        int bestIt = 0;
        tmp.addAll(list);
        
        for(int i = 0; i < bestIt; i++) {
            tmpInd = this.getBestFromList(tmp);
            //add node to return array
            toRet.add(tmpInd);
            //remove it from temporary list of nodes that are left
            tmp.remove(tmpInd);
            //remove edges from temporary network
            tmpNet.removeEdgesForNode(tmpInd);
        }

        for (int i = bestIt; i < size; i++) {
            tmpInd = tmpNet.getNodeWithHighestDegree();
            if(tmpInd == null){
                //If there are no nodes left with the edges, select the best according to fitness value
                tmpInd = this.getBestFromList(tmp);
            }
            //add node to return array
            toRet.add(tmpInd);
            //remove it from temporary list of nodes that are left
            tmp.remove(tmpInd);
            //remove edges from temporary network
            tmpNet.removeBidirectionalEdgesForNode(tmpInd);
        }
        
        //remove edges for the nodes which did not survive to the next gen
        tmp.stream().forEach(this.net::removeBidirectionalEdgesForNode);

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
        int MAXFES = 100 * NP;
        int funcNumber = 14;
        TestFunction tf = new Schwefel();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        NLc_SHADE shade;

        int runs = 10;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new NLc_SHADE(dimension, MAXFES, tf, H, NP, generator, minNP);

            shade.run();

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
            
            System.out.println("-------------------");
            ((NLc_SHADE)shade).net.getDegreeMap().entrySet().stream().forEach((entry) -> {
                System.out.println("ID: " + entry.getKey().id + " - degree: " + entry.getValue() + " - fitness: " + entry.getKey().fitness);
            });
            System.out.println("-------------------");
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
