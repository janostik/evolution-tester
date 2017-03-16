package algorithm.de;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import model.Individual;
import model.net.BidirectionalEdge;
import model.net.Edge;
import model.net.Net;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.OtherDistributionsUtil;
import util.random.Random;

/**
 *
 * Pbest array is replaced by Cbest array, which accomodates best individuals in terms of centrality degree
 * New net had to be added, so the Cbest array is selected from the net from previous generation
 * 
 * @author wiki on 02/03/2017
 */
public class NCB_SHADE extends N_SHADE {

    Net new_net = new Net();
    
    public NCB_SHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int runNo, int funcNo, String path) {
        super(D, MAXFES, f, H, NP, rndGenerator, runNo, funcNo, path);
    }

    @Override
    public String getName() {
        return "NCB_SHADE";
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
        
        PrintWriter pw = null;
        Individual indi;
        
        try {
            /**
             * PRINT OUT THE RANKINGS
             */
            pw = new PrintWriter(path + "rankings_" + this.funcNo + "_" + this.runNo + ".txt");

            pw.print("{{");
            
            for(int idd = 0; idd < this.P.size(); idd++){

                indi = this.P.get(idd);
                
                pw.print("{" + indi.id + ", " + this.getPositionFitness(indi) + ", " + this.getPositionCentrality(indi) + ", " + String.format(Locale.US, "%.10f", indi.fitness) + "}");
                if(idd != this.P.size()-1) {
                    pw.print(",");
                }

            }
            
            pw.print("}");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(N_SHADE.class.getName()).log(Level.SEVERE, null, ex);
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
        Individual x, pbestInd;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR;
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
                    wS.add(x.fitness - trial.fitness);
                    
                    for (int par = 1; par < parentArray.length; par++ ) {
                        if(parentArray[par] == null){
                            continue;
                        }
                        edge = new BidirectionalEdge(parentArray[par], trial);
                        edge.iter = G;
                        new_net.addEdge(edge);
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

                this.Aext = this.resizeAext(this.Aext, this.Asize);
                
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
            
            pw.print(",{");
            for(int idd = 0; idd < this.P.size(); idd++){
                
                indi = this.P.get(idd);
                
                pw.print("{" + indi.id + ", " + this.getPositionFitness(indi) + ", " + this.getPositionCentrality(indi) + ", " + String.format(Locale.US, "%.10f", indi.fitness) + "}");
                if(idd != this.P.size()-1) {
                    pw.print(",");
                }

            }
            pw.print("}");
            
            net = new Net();

            new_net.getEdges().stream().forEach((e) -> {
                net.addEdge(e);
            });
            
            new_net = new Net();

        }

        pw.print("}");
        pw.close();
        
        return this.best;

    }
    
    /**
     *
     * @param list
     * @return
     */
    @Override
    protected int getIndexOfBestFromList(List<Individual> list) {

        Individual b = null;
        int i = 0;
        int index = -1;

        double ind_degree;
        double b_degree = -1;
        
        for (Individual ind : list) {

            ind_degree = this.net.getDegreeMap().get(ind) == null ? 0 : this.net.getDegreeMap().get(ind);

            if (b == null) {
                b = ind;
                b_degree = this.net.getDegreeMap().get(b) == null ? 0 : this.net.getDegreeMap().get(b);
                index = i;
            } else if (ind_degree > b_degree) {
                b = ind;
                b_degree = this.net.getDegreeMap().get(b) == null ? 0 : this.net.getDegreeMap().get(b);
                index = i;
            }
            i++;
        }

        return index;

    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        int NP = 100;
        int MAXFES = dimension * 10_000;
        int funcNumber = 3;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        int H = 10;
        util.random.Random generator = new util.random.UniformRandom();
        String path = "E:\\results\\CEC2015-NCB_SHADE-10\\";

        NCB_SHADE shade;

        int runs = 10;
        double[] bestArray = new double[runs];

        for (int k = 0; k < runs; k++) {

            //(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int runNo, int funcNo, String path)
            shade = new NCB_SHADE(dimension, MAXFES, tf, H, NP, generator, k, funcNumber, path);

            shade.run();

            bestArray[k] = shade.getBest().fitness - tf.optimum();
            System.out.println(shade.getBest().fitness - tf.optimum());
            System.out.println(Arrays.toString(shade.getBest().vector));
//            
//            System.out.println("-------------------");
//            ((NLfv_SHADE)shade).net.getDegreeMap().entrySet().stream().forEach((entry) -> {
//                System.out.println("ID: " + entry.getKey().id + " - degree: " + entry.getValue() + " - fitness: " + entry.getKey().fitness);
//            });
//            System.out.println("-------------------");
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
