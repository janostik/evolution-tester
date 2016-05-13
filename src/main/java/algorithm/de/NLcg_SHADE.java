package algorithm.de;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import model.Individual;
import model.net.BidirectionalEdge;
import model.net.Edge;
import model.tf.Schwefel;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.random.Random;

/**
 *
 * SHADE algorithm with linear decrease of population.
 * The individual to be killed is selected based on its centrality order position.
 * The centrality order position is taken from the gaussian distribution with mean 0.48 and std 0.082.
 * 
 * @author adam on 04/05/2016
 */
public class NLcg_SHADE extends NLc_SHADE {

    public NLcg_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize) {
        super(D, MAXFES, f, H, NP, rndGenerator, minPopSize);
    }
    
    @Override
    public String getName() {
        return "NLcg_SHADE";
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
        double wSsum, meanS_F1, meanS_F2, meanS_CR;
        int k = 0;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;
        Individual[] parentArray; //for edge creation
        Edge edge;

        while (true) {

            this.G++;
            
//            if(G == 3 || G == 100) {
//                try {
//                    this.printOutNetwork(G);
//                } catch (FileNotFoundException ex) {
//                    Logger.getLogger(NLcg_SHADE.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
            
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
                parentArray[1] = this.getRandBestFromList(pBestArray);
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
                meanS_CR = 0;

                for (int s = 0; s < this.S_F.size(); s++) {
                    meanS_F1 += (wS.get(s) / wSsum) * this.S_F.get(s) * this.S_F.get(s);
                    meanS_F2 += (wS.get(s) / wSsum) * this.S_F.get(s);
                    meanS_CR += (wS.get(s) / wSsum) * this.S_CR.get(s);
                }

                this.M_F[k] = (meanS_F1 / meanS_F2);
                this.M_CR[k] = meanS_CR;

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
    
    public void printOutNetwork(int G) throws FileNotFoundException {
        
        String path = "C:\\Users\\wiki\\Dropbox\\PhD\\Clanky\\INCOS2016 - notyet\\data\\networks\\" + f.name() + "_" + G + ".csv";
        
        PrintWriter pw = new PrintWriter(path);

        pw.println("source,target,iter,directed");

        for(Edge edge : net.getEdges()){

            pw.println(edge.getSource().id + "," + edge.getTarget().id + "," + edge.iter + ",FALSE");

        }

        pw.close();
        
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
        Individual tmpInd;
        toRet.addAll(list);
        int diff = list.size() - size;

        for (int i = 0; i < diff; i++) {
            
            tmpInd = this.getIndividualToRemove(toRet);
            toRet.remove(tmpInd);
            this.net.removeBidirectionalEdgesForNode(tmpInd);
            
        }

        return toRet;

    }
    
    /**
     * 
     * Gets the individual which should be removed based on the gaussian distribution.
     * 
     * @param list
     * @return 
     */
    protected Individual getIndividualToRemove(List<Individual> list) {
        
        double gaus_value = OtherDistributionsUtil.normal(0.48, 0.082);
        if(gaus_value > 1){
            gaus_value = 1;
        }
        else if(gaus_value < 0) {
            gaus_value = 0;
        }
        int rank = ((int) (gaus_value * list.size())) % list.size();
        
        for(Individual ind : list){
            if(this.getPositionCentrality(ind) == rank) {
                return ind;
            }
        }
        
        return list.get(0);
    }
    
    /**
     * 
     * @param ind
     * @return 
     */
    protected int getPositionCentrality(Individual ind) {
        
        Map<Individual, Integer> degMap = this.net.getDegreeMap();
        int position = 0;
        
        int ind_degree = this.net.getDegreeMap().get(ind) == null ? 0 : this.net.getDegreeMap().get(ind);
        
        position = degMap.entrySet().stream().filter((entry) -> (entry.getValue() < ind_degree)).map((_item) -> 1).reduce(position, Integer::sum);
        
        return position;
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int dimension = 10;
        int NP = 100;
        int minNP = 20;
        int MAXFES = 100 * NP;
        int funcNumber = 14;
        TestFunction tf = new Schwefel();
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();

        NLcg_SHADE shade;

        int runs = 1;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            shade = new NLcg_SHADE(dimension, MAXFES, tf, H, NP, generator, minNP);

            shade.run();
            
//            try {
//                shade.printOutNetwork(200);
//            } catch (FileNotFoundException ex) {
//                Logger.getLogger(NLcg_SHADE.class.getName()).log(Level.SEVERE, null, ex);
//            }

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
            ((NLcg_SHADE)shade).net.getDegreeMap().entrySet().stream().forEach((entry) -> {
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
