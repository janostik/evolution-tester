/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm.de;

import algorithm.Algorithm;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import model.Individual;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import util.RandomUtil;

/**
 *
 * @author adam
 */
public class EigSpsLShaDE implements Algorithm{

    
    /**
     * Extended Indiivudal for the purposes of LShade algortihm
     */
    public class LShadeIndividual extends Individual {

        public LShadeIndividual(String id, double[] vector, double fitness, int q) {
            super(id, vector, fitness);
            this.q = q;
        }
        
        private int q;

        public int getQ() {
            return q;
        }

        public void setQ(int q) {
            this.q = q;
        }
 
    }

    private int D;
    private int G;
    private int NP;
    private int NPinit;
    private int NPmin;
    private List<LShadeIndividual> Aext;
    private List<LShadeIndividual> Asps;
    private List<LShadeIndividual> P;
    private int FES;
    private int MAXFES;
    private RealMatrix C;
    private RealMatrix B;
    private RealMatrix Bt;
    private TestFunction f;
    private LShadeIndividual best;
    private List<LShadeIndividual> bestHistory;
    private double[] M_F;
    private double[] M_ER;
    private double[] M_CR;
    private double Finit;
    private double ERinit;
    private double CRinit;
    private List<Double> S_F;
    private List<Double> S_ER;
    private List<Double> S_CR;
    private double w_F;
    private double w_ER;
    private double w_CR;
    private double CR_min;
    private double CR_max;
    private int Q;
    private double alfa;
    private int H;
    private double w_ext;
    private double p;

    public EigSpsLShaDE(int D, int NPinit, int NPmin, int MAXFES, TestFunction f, double Finit, double ERinit, double CRinit, double w_F, double w_ER, double w_CR, double CR_min, double CR_max, int Q, double alfa, int H, double w_ext, double p) {
        this.D = D;
        this.NPinit = NPinit;
        this.NPmin = NPmin;
        this.MAXFES = MAXFES;
        this.f = f;
        this.Finit = Finit;
        this.ERinit = ERinit;
        this.CRinit = CRinit;
        this.w_F = w_F;
        this.w_ER = w_ER;
        this.w_CR = w_CR;
        this.CR_min = CR_min;
        this.CR_max = CR_max;
        this.Q = Q;
        this.alfa = alfa;
        this.H = H;
        this.w_ext = w_ext;
        this.p = p;
    }
    
    
    
    @Override
    public Individual run() {
        
        /**
         * Initialization
         */
        this.G = 0;
        this.NP = NPinit;
        this.Aext = new ArrayList<>();
        this.best = null;
        this.bestHistory = new ArrayList<>();
        
        /**
         * Initial population
         */
        int EXTsize;
        int id = 0;
        double[] features;
        this.P = new ArrayList<>();
        LShadeIndividual ind;
        
        for(int i=0; i<this.NP; i++){
            id = i;
            features = this.f.generateTrial(this.D).clone();
            ind = new LShadeIndividual(String.valueOf(id), features, this.f.fitness(features),0);
            this.isBest(ind);
            this.P.add(ind);
            this.FES++;
            this.writeHistory();
        }
        
        /**
         * Initial A SPS
         */
        this.Asps = new ArrayList<>();
        this.Asps.addAll(this.P);
        
        /**
         * Covariance matrix
         */
        double[][] genMatrix = new double[this.NP][this.D];
        for(int i=0; i<this.P.size(); i++){
            genMatrix[i] = this.P.get(i).vector;
        }
        Covariance cov = new Covariance(genMatrix);
        this.C = cov.getCovarianceMatrix();
        EigenDecomposition ed = new EigenDecomposition(this.C);
        this.B = ed.getV();
        this.Bt = ed.getVT();
        
        this.M_F = new double[this.H];
        this.M_ER = new double[this.H];
        this.M_CR = new double[this.H];
        
        for(int h=0; h< this.H;h++){
            this.M_F[h] = this.Finit;
            this.M_ER[h] = this.ERinit;
            this.M_CR[h] = this.CRinit;
        }
        
        /**
         * Generation iteration;
         */
        CauchyDistribution cauchy;
        int r, Psize;
        double Fg, ERg, CRg, jrand, tmp;
        List<LShadeIndividual> pA, newPop, pBestArray;
        double[] v, pbest, pr1, pr2, pActive, u, xover, xoverP, xoverV;
        int[] rIndexes;
        LShadeIndividual trial, x;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_ER, meanS_CR;
        int k = 0;

        
        while(true){
            
            this.G++;
            this.S_F = new ArrayList<>();
            this.S_ER = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();
            EXTsize = (int) (this.w_ext * this.NP);
            if(EXTsize < 1){
                EXTsize = 1;
            }
            Psize = (int) (this.p * this.NP);
            if(Psize < 1){
                Psize = 1;
            }
            
            newPop = new ArrayList<>(); 
            
            for(int i=0; i<this.NP; i++){

                x = this.P.get(i);
                r = RandomUtil.nextInt(this.H);
                Fg = RandomUtil.cauchy(this.M_F[r], this.w_F);
                while(Fg <= 0){
                    Fg = RandomUtil.cauchy(this.M_F[r], this.w_F);
                }
                if(Fg > 1){
                    Fg = 1;
                }
                ERg = RandomUtil.normal(this.M_ER[r], this.w_ER);
                if(ERg > 1){
                    ERg = 1;
                }
                if(ERg < 0){
                    ERg = 0;
                }
                
                CRg = RandomUtil.normal(this.M_CR[r], this.w_CR);
                if(CRg > this.CR_max){
                    CRg = this.CR_max;
                }
                if(CRg < this.CR_min){
                    CRg = this.CR_min;
                }
                
                /**
                 * Base parent selection
                 */
                if(this.P.get(i).getQ() <= this.Q){
                    pA = new ArrayList<>();
                    pA.addAll(this.P);  
                } else {
                    pA = new ArrayList<>();
                    pA.addAll(this.Asps);
                }
                
                pBestArray = new ArrayList<>();
                pBestArray.addAll(this.P);
                pBestArray.addAll(this.Asps);
                pBestArray = this.resize(pBestArray, Psize);
                
                /**
                 * Base equation
                 */
                v = new double[this.D];
                pActive = pA.get(i).vector.clone();
                pbest = this.getBestFromList(pBestArray).vector.clone();
                rIndexes = this.genRandIndexes(i, this.NP, pA.size()+this.Aext.size());
                pr1 = pA.get(rIndexes[0]).vector.clone();
                if(rIndexes[1] > pA.size()-1){
                    pr2 = this.Aext.get(rIndexes[1]-pA.size()).vector.clone();
                } else {
                    pr2 = pA.get(rIndexes[1]).vector.clone();
                }
                
                for(int j = 0; j < this.D; j++){
                    
                    v[j] = pActive[j] + Fg*(pbest[j] - pActive[j]) + Fg*(pr1[j] - pr2[j]);
                            
                }
                
                /**
                 * Crossover
                 */
                u = new double[this.D];
                jrand = RandomUtil.nextInt(this.D);
                
                if(RandomUtil.nextDouble() <= ERg){
                    
                    xoverP = this.Bt.operate(pActive);
                    xoverV = this.Bt.operate(v);
                    xover = new double[this.D];
                    
                    for(int j=0; j<this.D; j++){
                        if(RandomUtil.nextDouble() <= CRg || j == jrand){
                            xover[j] = xoverV[j];
                        } else {
                            xover[j] = xoverP[j];
                        }
                    }
                    
                    u = this.B.operate(xover);
                    
                } else {
                    
                    for(int j=0; j<this.D; j++){
                        if(RandomUtil.nextDouble() <= CRg || j == jrand){
                            u[j] = v[j];
                        } else {
                            u[j] = pActive[j];
                        }
                    }
                    
                }
                
                /**
                 * Constrain check
                 */
                for(int d=0;d<this.D;d++){
                    if(u[d] < this.f.min(this.D)){
                        u[d] = (this.f.min(this.D) + pActive[d])/2.0;
                    }
                    else if(u[d] > this.f.max(this.D)){
                        u[d] = (this.f.max(this.D) + pActive[d])/2.0;
                    }
                }
                
                /**
                 * Trial ready
                 */
                id++;
                trial = new LShadeIndividual(String.valueOf(id), u, f.fitness(u), 0);

                /**
                 * Trial is better
                 */
                if(trial.fitness < x.fitness){
                    newPop.add(trial);
                    this.S_F.add(Fg);
                    this.S_ER.add(ERg);
                    this.S_CR.add(CRg);
                    this.Aext.add(x);
                    this.Asps.add(trial);
                    wS.add(Math.abs(trial.fitness - x.fitness));
                } else {
                    x.setQ(x.q+1);
                    newPop.add(x);
                }
                
                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if(this.FES >= this.MAXFES){
                    break;
                }
                
            }
            
            if(this.FES >= this.MAXFES){
                break;
            }
            
            this.C = this.C.scalarMultiply(1-this.alfa);
            this.NP = (int) Math.round(this.NPinit - (this.FES/(double) this.MAXFES)*(this.NPinit - this.NPmin));
            wSsum = 0;
            for(Double num : wS){
                wSsum += num;
            }
            meanS_F1 = 0;
            meanS_F2 = 0;
            meanS_ER = 0;
            meanS_CR = 0;
            
            for(int s=0; s < this.S_F.size(); s++){
                meanS_F1 += (wS.get(s)/wSsum) * this.S_F.get(s) *this.S_F.get(s);
                meanS_F2 += (wS.get(s)/wSsum) * this.S_F.get(s);
                meanS_ER += (wS.get(s)/wSsum) * this.S_ER.get(s);
                meanS_CR += (wS.get(s)/wSsum) * this.S_CR.get(s);
            }

            this.M_F[k] = (meanS_F1/meanS_F2);
            this.M_ER[k] = meanS_ER/(double) this.S_ER.size();
            this.M_CR[k] = meanS_CR/(double) this.S_CR.size();

            k++;
            if(k >= this.H){
                k = 0;
            }
            
            /**
             * Resize of population and archives
             */
            this.P = new ArrayList<>();
            this.P = this.resize(newPop, this.NP);
            this.Asps = this.resize(this.Asps, this.NP);
            this.Aext = this.resizeAext(this.Aext, EXTsize);

        }
        
        
        return this.best;
        
    }

    @Override
    public List<? extends Individual> getPopulation() {
        return this.P;
    }

    @Override
    public TestFunction getTestFunction() {
        return this.f;
    }

    @Override
    public String getName() {
        return "EIG-SPS-LShaDE";
    }
    
    /**
     * 
     * @param list
     * @param size
     * @return 
     */
    private List<LShadeIndividual> resizeAext(List<LShadeIndividual> list, int size){
        
        List<LShadeIndividual> toRet = new ArrayList<>();
        toRet.addAll(list);
        int index;
        
        while(toRet.size()>size){
            
            index = RandomUtil.nextInt(toRet.size());
            toRet.remove(index);
            
        }
        
        return toRet;
        
    }
    
    /**
     * 
     * @param list
     * @param size
     * @return 
     */
    private List<LShadeIndividual> resize(List<LShadeIndividual> list, int size){
        
        List<LShadeIndividual> toRet = new ArrayList<>();
        List<LShadeIndividual> tmp = new ArrayList<>();
        tmp.addAll(list);
        int bestIndex;
        
        for(int i=0; i<size; i++){
            bestIndex = this.getIndexOfBestFromList(tmp);
            toRet.add(tmp.get(bestIndex));
            tmp.remove(bestIndex);
        }
        
        return toRet;
        
    }
    
    /**
     * 
     * @param list
     * @return 
     */
    private int getIndexOfBestFromList(List<LShadeIndividual> list){
        
        LShadeIndividual b = null;
        int i=0;
        int index=-1;
        
        for(LShadeIndividual ind : list){
            
            if(b == null){
                b = ind;
                index = i;
            }
            else if(ind.fitness < b.fitness){
                b = ind;
                index = i;
            }
            i++;
        }
        
        return index;
        
    }
    
    /**
     * 
     * @param list
     * @return 
     */
    private LShadeIndividual getBestFromList(List<LShadeIndividual> list){
        
        LShadeIndividual b = null;
        
        for(LShadeIndividual ind : list){
            
            if(b == null){
                b = ind;
            }
            else if(ind.fitness < b.fitness){
                b = ind;
            }
            
        }
        
        return b;
        
    }
    
    /**
     * 
     * @param max1
     * @param max2
     * @return 
     */
    private int[] genRandIndexes(int index, int max1, int max2){
        
        int a, b;
        
        a = RandomUtil.nextInt(max1);
        b = RandomUtil.nextInt(max2);
        
        while(a == b || a == index || b == index){
            a = RandomUtil.nextInt(max1);
            b = RandomUtil.nextInt(max2);
        }
        
        return new int[]{a,b};
    }
    
    /**
     * 
     */
    private void writeHistory(){
        this.bestHistory.add(this.best);
    }
    
    /**
     * 
     * @param ind
     * @return 
     */
    private boolean isBest(LShadeIndividual ind){
        
        if(this.best == null || ind.fitness < this.best.fitness){
            this.best = ind;
            return true;
        }
        
        return false;
        
    }

    public int getD() {
        return D;
    }

    public void setD(int D) {
        this.D = D;
    }

    public int getG() {
        return G;
    }

    public void setG(int G) {
        this.G = G;
    }

    public int getNP() {
        return NP;
    }

    public void setNP(int NP) {
        this.NP = NP;
    }

    public int getNPinit() {
        return NPinit;
    }

    public void setNPinit(int NPinit) {
        this.NPinit = NPinit;
    }

    public int getNPmin() {
        return NPmin;
    }

    public void setNPmin(int NPmin) {
        this.NPmin = NPmin;
    }

    public List<LShadeIndividual> getAext() {
        return Aext;
    }

    public void setAext(List<LShadeIndividual> Aext) {
        this.Aext = Aext;
    }

    public List<LShadeIndividual> getAsps() {
        return Asps;
    }

    public void setAsps(List<LShadeIndividual> Asps) {
        this.Asps = Asps;
    }

    public List<LShadeIndividual> getP() {
        return P;
    }

    public void setP(List<LShadeIndividual> P) {
        this.P = P;
    }

    public int getFES() {
        return FES;
    }

    public void setFES(int FES) {
        this.FES = FES;
    }

    public int getMAXFES() {
        return MAXFES;
    }

    public void setMAXFES(int MAXFES) {
        this.MAXFES = MAXFES;
    }

    public RealMatrix getC() {
        return C;
    }

    public void setC(RealMatrix C) {
        this.C = C;
    }

    public RealMatrix getB() {
        return B;
    }

    public void setB(RealMatrix B) {
        this.B = B;
    }

    public RealMatrix getBt() {
        return Bt;
    }

    public void setBt(RealMatrix Bt) {
        this.Bt = Bt;
    }

    public TestFunction getF() {
        return f;
    }

    public void setF(TestFunction f) {
        this.f = f;
    }

    public LShadeIndividual getBest() {
        return best;
    }

    public void setBest(LShadeIndividual best) {
        this.best = best;
    }

    public List<LShadeIndividual> getBestHistory() {
        return bestHistory;
    }

    public void setBestHistory(List<LShadeIndividual> bestHistory) {
        this.bestHistory = bestHistory;
    }

    public double[] getM_F() {
        return M_F;
    }

    public void setM_F(double[] M_F) {
        this.M_F = M_F;
    }

    public double[] getM_ER() {
        return M_ER;
    }

    public void setM_ER(double[] M_ER) {
        this.M_ER = M_ER;
    }

    public double[] getM_CR() {
        return M_CR;
    }

    public void setM_CR(double[] M_CR) {
        this.M_CR = M_CR;
    }

    public double getFinit() {
        return Finit;
    }

    public void setFinit(double Finit) {
        this.Finit = Finit;
    }

    public double getERinit() {
        return ERinit;
    }

    public void setERinit(double ERinit) {
        this.ERinit = ERinit;
    }

    public double getCRinit() {
        return CRinit;
    }

    public void setCRinit(double CRinit) {
        this.CRinit = CRinit;
    }

    public List<Double> getS_F() {
        return S_F;
    }

    public void setS_F(List<Double> S_F) {
        this.S_F = S_F;
    }

    public List<Double> getS_ER() {
        return S_ER;
    }

    public void setS_ER(List<Double> S_ER) {
        this.S_ER = S_ER;
    }

    public List<Double> getS_CR() {
        return S_CR;
    }

    public void setS_CR(List<Double> S_CR) {
        this.S_CR = S_CR;
    }

    public double getW_F() {
        return w_F;
    }

    public void setW_F(double w_F) {
        this.w_F = w_F;
    }

    public double getW_ER() {
        return w_ER;
    }

    public void setW_ER(double w_ER) {
        this.w_ER = w_ER;
    }

    public double getW_CR() {
        return w_CR;
    }

    public void setW_CR(double w_CR) {
        this.w_CR = w_CR;
    }

    public double getCR_min() {
        return CR_min;
    }

    public void setCR_min(double CR_min) {
        this.CR_min = CR_min;
    }

    public double getCR_max() {
        return CR_max;
    }

    public void setCR_max(double CR_max) {
        this.CR_max = CR_max;
    }

    public int getQ() {
        return Q;
    }

    public void setQ(int Q) {
        this.Q = Q;
    }

    public double getAlfa() {
        return alfa;
    }

    public void setAlfa(double alfa) {
        this.alfa = alfa;
    }

    public int getH() {
        return H;
    }

    public void setH(int H) {
        this.H = H;
    }

    public double getW_ext() {
        return w_ext;
    }

    public void setW_ext(double w_ext) {
        this.w_ext = w_ext;
    }

    public double getp() {
        return p;
    }

    public void setp(double p) {
        this.p = p;
    }
    
    
    
    public static void main(String[] args) throws Exception {
    
        int dimension = 10;
        int NPinit = 113;
        int NPmin = 72;
        int MAXFES = 10000*dimension;
        int funcNumber = 10;
        TestFunction tf = new Cec2015(dimension, funcNumber);
        double Finit = 0.3709;
        double ERinit = 0.1387;
        double CRinit = 0.9553;
        double w_F = 0.8553;
        double w_ER = 0.0325;
        double w_CR = 0.2581;
        double CR_min = 0.9450;
        double CR_max = 0.9899;
        int Q = 31;
        double alfa=0.1957;
        int H = 730;
        double w_ext = 1.4141;
        double p = 0.4105;
        
        EigSpsLShaDE lshade;
        
        int runs = 10;
        double[] bestArray = new double[runs];
        
        for(int k = 0; k<runs;k++){
        
            lshade = new EigSpsLShaDE(dimension, NPinit, NPmin, MAXFES, tf, Finit, ERinit, CRinit, w_F, w_ER, w_CR, CR_min, CR_max, Q, alfa, H, w_ext, p);

            lshade.run();

            PrintWriter writer;

            try {
                writer = new PrintWriter("CEC2015-1-lshade"+k+".txt", "UTF-8");

                writer.print("{");

                for(int i=0; i<lshade.getBestHistory().size(); i++){

                    writer.print(String.format(Locale.US, "%.10f",lshade.getBestHistory().get(i).fitness));

                    if(i != lshade.getBestHistory().size()-1){
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(EigSpsLShaDE.class.getName()).log(Level.SEVERE, null, ex);
            }

            bestArray[k] = lshade.getBest().fitness;
            System.out.println(lshade.getBest().fitness);
        }
        
        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        System.out.println("Solution error: " + (new Mean().evaluate(bestArray)-(funcNumber*100)));
        
        
    }
    
}
