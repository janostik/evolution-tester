package model.tf;

import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.Math.E;
import static java.lang.Math.PI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;
import model.Individual;
import util.IndividualUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 *
 * CEC 2019 benchmark - 100 digit
 * 
 * Manually coded functions
 * 
 * @author wiki on 06/06/2019
 */
public class Cec2019_100digit_manual implements TestFunction {
    
    private final int dim, func_num;
    
    private double[] M, OShift, y, z;
    
    public Cec2019_100digit_manual(int func_num) throws Exception {
        
        this.func_num = func_num;
        
        
        switch(func_num)
        {
            case 1:
                this.dim = 9;
                break;
            case 2:
                this.dim = 16;
                break;
            case 3:
                this.dim = 18;
                break;
            default:
                this.dim = 10;
                this.loadRotAndShift();
        }

        this.y = new double[this.dim];
        this.z = new double[this.dim];
        
    }
    
    private void loadRotAndShift() throws URISyntaxException, FileNotFoundException {
        
        /*Load Matrix M*****************************************************/
        URL resource = Cec2019_100digit_manual.class.getResource("/CEC2019/input_data/M_"+this.func_num+"_D" + this.dim + ".txt");
        File fpt = Paths.get(resource.toURI()).toFile();//* Load M data *
        Scanner input = new Scanner(fpt);
        input.useLocale(Locale.US);
        if (!fpt.exists())
        {
            System.out.println("\n Error: Cannot open input file for reading ");
        }

        this.M=new double[this.dim*this.dim]; 


        for (int i=0;i<this.dim*this.dim; i++)
        {
                this.M[i]=input.nextDouble();
        }

        input.close();
        
        /*Load shift_data***************************************************/

        resource = Cec2015.class.getResource("/CEC2019/input_data/shift_data_"+this.func_num+".txt");
        fpt = Paths.get(resource.toURI()).toFile();
        input = new Scanner(fpt);
        input.useLocale(Locale.US);
        if (!fpt.exists())
        {
                System.out.println("\n Error: Cannot open input file for reading ");
        }

        this.OShift=new double[this.dim];

        for (int i=0;i<this.dim;i++)
        {
                OShift[i]=input.nextDouble();
        }

        input.close();
        
    }
    
    void sr_func (double[] x, double[] sr_x, int nx, double[] Os, double[] Mr, double sh_rate, int s_flag, int r_flag)
    {
        int i,j;
        if (s_flag==1)
        {
            if (r_flag==1)
            {	
                    shiftfunc(x, y, nx, Os);
                    for (i=0; i<nx; i++)//shrink to the orginal search range
                    {
                            y[i]=y[i]*sh_rate;
                    }
                    rotatefunc(y, sr_x, nx, Mr);
            }
            else
            {
                    shiftfunc(x, sr_x, nx, Os);
                    for (i=0; i<nx; i++)//shrink to the orginal search range
                    {
                            sr_x[i]=sr_x[i]*sh_rate;
                    }
            }
        }
        else
        {	

            if (r_flag==1)
            {	
                    for (i=0; i<nx; i++)//shrink to the orginal search range
                    {
                            y[i]=x[i]*sh_rate;
                    }
                    rotatefunc(y, sr_x, nx, Mr);
            }
            else

            {
                    for (j=0; j<nx; j++)//shrink to the orginal search range
                    {
                            sr_x[j]=x[j]*sh_rate;
                    }
            }
        }
    }
    
    void shiftfunc (double[] x, double[] xshift, int nx,double[] Os)
    {
        int i;
        for (i=0; i<nx; i++)
        {
            xshift[i]=x[i]-Os[i];
        }
    }


    void rotatefunc (double[] x, double[] xrot, int nx,double[] Mr)
    {
        int i,j;
        for (i=0; i<nx; i++)
        {
            xrot[i]=0;
            for (j=0; j<nx; j++)
            {
                    xrot[i]=xrot[i]+x[j]*Mr[i*nx+j];
            }
        }
    }
    
    double ackley_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Ackley's  */
    {
        int i;
        double sum1, sum2;
        sum1 = 0.0;
        sum2 = 0.0;

        sr_func(x,z,nx,Os,Mr,1.0,s_flag,r_flag);/*shift and rotate*/ 		

        for (i=0; i<nx; i++)
        {
            sum1 += z[i]*z[i];
            sum2 += Math.cos(2.0*PI*z[i]);
        }
        sum1 = -0.2*Math.sqrt(sum1/nx);
        sum2 /= nx;
        f =  E - 20.0*Math.exp(sum1) - Math.exp(sum2) +20.0;

        return f;
    }
    
    double happycat_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) 
    /*HappyCat, probided by Hans-Georg Beyer (HGB)*/
    /*original global optimum: [-1,-1,...,-1]*/
    {
            int i;
            double alpha,r2,sum_z;
            alpha = 1.0/8.0;

            sr_func(x,z,nx,Os,Mr,5/100.0,s_flag,r_flag);/*shift and rotate*/ 

            r2 = 0.0;
            sum_z = 0.0;
            for (i=0;i<nx;i++)
            {
                    z[i] = z[i] - 1.0; //shift to orgin
                    r2 += z[i]*z[i];
                    sum_z += z[i];

            }
            f = Math.pow(Math.abs(r2-nx), 2*alpha) + (0.5*r2 + sum_z)/nx + 0.5;

            return f;
    }
    
    double escaffer6_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Expanded Scaffer°Įs F6  */
    {
        int i;
        double temp1, temp2;

        sr_func (x, z, nx, Os, Mr, 1.0, s_flag, r_flag); /* shift and rotate */


        f = 0.0;
        for (i=0; i<nx-1; i++)
        {
            temp1 = Math.sin(Math.sqrt(z[i]*z[i]+z[i+1]*z[i+1]));
                    temp1 =temp1*temp1;
            temp2 = 1.0 + 0.001*(z[i]*z[i]+z[i+1]*z[i+1]);
            f += 0.5 + (temp1-0.5)/(temp2*temp2);
        }
        temp1 = Math.sin(Math.sqrt(z[nx-1]*z[nx-1]+z[0]*z[0]));
            temp1 =temp1*temp1;
        temp2 = 1.0 + 0.001*(z[nx-1]*z[nx-1]+z[0]*z[0]);
        f += 0.5 + (temp1-0.5)/(temp2*temp2);

        return f;
    }
    
    double schwefel_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Schwefel's  */
    {
        int i;
            double tmp;

            sr_func(x,z,nx,Os,Mr,1000.0/100.0,s_flag,r_flag);/*shift and rotate*/ 


        f=0;
        for (i=0; i<nx; i++)
            {
            z[i] += 4.209687462275036e+002;
            if (z[i]>500)
                    {
                            f-=(500.0-(z[i]%500))*Math.sin(Math.pow(500.0-(z[i]%500),0.5));
                            tmp=(z[i]-500.0)/100;
                            f+= tmp*tmp/nx;
                    }
                    else if (z[i]<-500)
                    {
                            f-=(-500.0+(Math.abs(z[i])%500))*Math.sin(Math.pow(500.0-(Math.abs(z[i])%500),0.5));
                            tmp=(z[i]+500.0)/100;
                            f+= tmp*tmp/nx;
                    }
                    else
                            f-=z[i]*Math.sin(Math.pow(Math.abs(z[i]),0.5));
        }
        f=4.189828872724338e+002*nx+f;

        return f;
    }
    
    double weierstrass_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Weierstrass's  */
    {
        int i,j,k_max;
        double sum,sum2=0, a, b;

        sr_func(x,z,nx,Os,Mr,0.5/100.0,s_flag,r_flag);/*shift and rotate*/ 


        a = 0.5;
        b = 3.0;
        k_max = 20;
        f = 0.0;
        for (i=0; i<nx; i++)
        {
            sum = 0.0;
                    sum2 = 0.0;
            for (j=0; j<=k_max; j++)
            {
                sum += Math.pow(a,j)*Math.cos(2.0*PI*Math.pow(b,j)*(z[i]+0.5));
                            sum2 += Math.pow(a,j)*Math.cos(2.0*PI*Math.pow(b,j)*0.5);
            }
            f += sum;
        }
            f -= nx*sum2;

            return f;
    }
    
    double rastrigin_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Rastrigin's  */
    {
        int i;
            f=0.0;


            /*for (int j=0;j<nx;j++)
            {
                    System.out.println(Os[j]);
            }*/

            sr_func(x,z,nx,Os,Mr,5.12/100.0,s_flag,r_flag);/*shift and rotate*/ 

            for(i=0;i<nx;i++)
            {
                    f += (z[i]*z[i] - 10.0*Math.cos(2.0*PI*z[i]) + 10.0);
            }

        return f;
    }
    
    double griewank_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Griewank's  */
    {
        int i;
        double s, p;

        sr_func(x,z,nx,Os,Mr,600.0/100.0,s_flag,r_flag);/*shift and rotate*/ 

        s = 0.0;
        p = 1.0;
        for (i=0; i<nx; i++)
        {
            s += z[i]*z[i];
            p *= Math.cos(z[i]/Math.sqrt(1.0+i));
        }
        f = 1.0 + s/4000.0 - p;

        return f;
    }
    
    double schaffer_F7_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) {
        
        int i;
        double tmp;
        f = 0.0;
        sr_func(x, z, nx, Os, Mr, 1.0, s_flag, r_flag);
        for(i = 0; i<nx-1; i++) {
            
            z[i]=Math.pow(y[i]*y[i]+y[i+1]*y[i+1],0.5);
            tmp=Math.sin(50.0*Math.pow(z[i],0.2));
            f += Math.pow(z[i],0.5)+Math.pow(z[i],0.5)*tmp*tmp;
            
        }
        f = f*f/(nx-1)/(nx-1);
        
        return f;
    }
    
    double Lennard_Jones(double[] x, int D, double f) {

        f = 0;
        int i, j, k, a, b;
        double xd, yd, zd, ed, ud, sum = 0;


        double[] minima = new double[]{-1.,-3.,-6.,-9.103852,-12.712062,-16.505384,-19.821489,-24.113360,
                -28.422532,-32.765970,-37.967600,-44.326801,-47.845157,-52.322627,-56.815742,-61.317995,
                -66.530949,-72.659782,-77.1777043,-81.684571,-86.809782,-02.844472,-97.348815,-102.372663};

        k = D / 3;
        if (k < 2)  // default if k<2
        {
            k = 2;
            D = 6;
        }

        for (i = 0; i < k - 1; i++)
        {
            for (j = i + 1; j < k; j++)
            {
                a = 3 * i;
                b = 3 * j;
                xd = x[a] - x[b];
                yd = x[a + 1] - x[b + 1];
                zd = x[a + 2] - x[b + 2];
                ed = xd*xd + yd*yd + zd*zd;
                ud = ed*ed*ed;
                if (ed>0) sum += (1.0 / ud - 2.0) / ud;
            }
        }

        f += sum;
        f += 12.7120622568;

        return f;
    }

    double Hilbert(double[] x, int D, double f) {

        f = 0;
        int i, j, k, b;

        double sum = 0;

        double[][] hilbert, y;
        hilbert = new double[10][10];
        y = new double[10][10];

        b = (int)Math.sqrt((double)D);


        for(i = 0; i < b; i++)
        {
            for (j = 0; j < b; j++)
            {
                hilbert[i][j] = 1. / (double)(i + j + 1);
            }
        }

        for(j = 0; j < b; j++)
        {
            for (k = 0; k < b; k++)
            {
                y[j][k] = 0;
                for (i = 0; i < b; i++)
                {
                    y[j][k] += hilbert[j][i] * x[k + b * i];
                }
            }
        }


        for(i = 0; i < b; i++)
        {
            for (j = 0; j < b; j++)
            {
                if (i == j) sum += Math.abs(y[i][j] - 1);
                else sum += Math.abs(y[i][j]);
            }
        }

        f += sum;
        
        return f;
    }

    double Chebyshev(double[] x, int D, double f) {

        f = 0.0;
        int i, j;
        int sample;
        double a = 1., b = 1.2, px, y = -1, sum = 0;
        double dx = 0, dy;

        for(j = 0; j < D - 2; j++)
        {
            dx = 2.4 * b - a;
            a = b;
            b = dx;
        }

        sample = 32 * D;
        dy = 2. / (double)sample;

        for(i = 0; i <= sample; i++)
        {
            px = x[0];
            for (j = 1; j < D; j++)
            {
                px = y*px + x[j];
            }
            if (px < -1 || px > 1) sum += (1. - Math.abs(px))*(1. - Math.abs(px));
            y += dy;
        }

        for(i = -1; i <= 1; i += 2)
        {
            px = x[0];
            for (j = 1; j < D; j++)
            {
                px = 1.2*px + x[j];
            }

            if (px < dx) sum += px * px;
        }

        f += sum;

        return f;
    }

    
    @Override
    public double fitness(Individual individual) {
        return this.fitness(individual.vector);
    }
    
    @Override
    public double fitness(double[] vector) {
        
        double[] t = new double[this.dim];
        double out = 0;
        
        for (int j=0; j<this.dim; j++){
            t[j] = vector[j];
        }
        
        switch(this.func_num) {
            case 1:
                out=Chebyshev(t,this.dim,out);
                out+=1.0;
                break;
            case 2:
                out=Hilbert(t,this.dim,out);
                out+=1.0;
                break;
            case 3:
                out=Lennard_Jones(t,this.dim,out);
                out+=1.0;
                break;
            case 4:
                out=rastrigin_func(t,out,this.dim,OShift,M,1,1);
                out+=1.0;
                break;
            case 5:
                out=griewank_func(t,out,this.dim,OShift,M,1,1);
                out+=1.0;
                break;
            case 6:
                out=weierstrass_func(t,out,this.dim,OShift,M,1,1);
                out+=1.0;
                break;
            case 7:
                out=schwefel_func(t,out,this.dim,OShift,M,1,1);
                out+=1.0;
                break;
            case 8:
                out=escaffer6_func(t,out,this.dim,OShift,M,1,1);
                out+=1.0;
                break;
            case 9:
                out=happycat_func(t,out,this.dim,OShift,M,1,1);
                out+=1.0;
                break;
            case 10:
                out=ackley_func(t,out,this.dim,OShift,M,1,1);
                out+=1.0;
                break;
            default:
                System.err.println("There are only 10 fucntions in the benchmark [1-10].");
                break;
        }
        
        
        return out;
    }

    @Override
    public void constrain(Individual individual) {
        switch(this.dim)
        {
            case 1:
                IndividualUtil.randIfOutOfBounds(individual, -8192, 8192);
                break;
            case 2:
                IndividualUtil.randIfOutOfBounds(individual, -16384, 16384);
                break;
            case 3:
                IndividualUtil.randIfOutOfBounds(individual, -4, 4);
                break;
            default:
                IndividualUtil.randIfOutOfBounds(individual, -100, 100);
        }
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] vector = new double[dim];
        Random rnd = new UniformRandom();
        switch(this.dim)
        {
            case 1:
                for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-8192, 8192);
                break;
            case 2:
                for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-16384, 16384);
                break;
            case 3:
                for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-4, 4);
                break;
            default:
                for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-100, 100);
        }
        return vector;
    }

    @Override
    public double fixedAccLevel() {
        return 0;
    }

    @Override
    public double optimum() {
        return 1;
    }

    @Override
    public double max(int dim) {
        switch(this.dim)
        {
            case 1:
                return 8192;
            case 2:
                return 16384;
            case 3:
                return 4;
            default:
                return 100;
        }
    }

    @Override
    public double min(int dim) {
        switch(this.dim)
        {
            case 1:
                return -8192;
            case 2:
                return -16384;
            case 3:
                return -4;
            default:
                return -100;
        }
    }
        
    @Override
    public String name() {
        return "man_CEC2019-f" + this.func_num;
    }
    
    public static void main(String[] args) throws Exception {
        
        Cec2019_100digit_manual test;
        double res = 0;
        double[] arr;
        
        for(int j = 1; j < 11; j++) {
            
            test = new Cec2019_100digit_manual(j);
            
            for(int i = 0; i < 10; i++) {
                arr = new double[test.dim];
                for(int a = 0; a < test.dim; a++) {
//                    arr[a] = RandomUtil.nextDouble(test.min(a), test.max(a));
                    arr[a] = a;
                }
                res = test.fitness(arr);
            }

            System.out.println(res);
            
        }
  
    }

}
