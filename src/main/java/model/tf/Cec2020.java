package model.tf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Individual;
import util.IndividualUtil;
import util.random.Random;
import util.random.UniformRandom;

/**
 *
 * CEC 2020 becnhmark set
 * 
 * 10 functions
 * - 1 unimodal
 * - 3 basic multimodal
 * - 3 hybrid
 * - 3 composition
 * 
 * Search range - [-100, 100]^D
 * 
 * Supported dimensions - 5, 10, 15, 20
 * - F6, F7 only 10, 15, 20
 * 
 * MaxFES - 50 000, 1 000 000, 3 000 000, 10 000 000
 * 
 * 30 runs
 * 
 * @author adam on 06/01/2020
 */
public class Cec2020 extends CECbase/*implements TestFunction*/ {

    final double INF = 1.0e99;
    final double EPS = 1.0e-14;
    final double E  = 2.7182818284590452353602874713526625;
    final double PI = 3.1415926535897932384626433832795029;
    
    double[] OShift,M,y,z,x_bound;
    int ini_flag,n_flag,func_flag;
    int[] SS;
    
    int nx, func_num, func_real;
    
    public Cec2020(int nx, int func_num0) throws Exception {

        this.fn = func_num0-1;
        this.sensitivity = new HashMap<>();
        this.sensitivity.put(5, new int[][]{{6,5,5,5,5},{4,4,4,4,4},{4,4,4,4,4},{4,4,4,4,4},{4,4,4,4,4},{0,0,0,0,0},{0,0,0,0,0},{7,7,7,7,7},{8,8,8,8,8},{9,9,9,9,9}});
        this.sensitivity.put(10, new int[][]{{6,6,6,6,6,6,6,6,6,6},{4,4,4,4,4,4,4,4,4,4},{4,4,4,4,4,4,4,4,4,4},{4,4,4,4,4,4,4,4,4,4},{6,6,6,6,6,6,6,6,6,6},{13,13,13,13,13,13,13,13,13,13},{4,4,4,4,4,4,4,4,4,4},{7,7,7,7,7,7,7,7,7,7},{8,8,8,8,8,8,8,8,8,8},{9,9,9,9,9,9,9,9,9,9}});
        this.sensitivity.put(15, new int[][]{{6,6,6,6,6,6,6,6,6,6,6,6,6,6,6},{4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},{4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},{4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},{4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},{13,13,13,13,13,13,13,13,13,13,13,13,13,13,13},{4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},{10,10,10,10,10,10,10,10,10,10,10,10,10,10,10}});
        this.sensitivity.put(20, new int[][]{{6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6},{4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},{4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},{4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},{3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},{3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},{13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13},{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},{9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9}});
        
        this.func_real = func_num0;
        this.nx = nx;
        int[] Func_num = {1,2,3,7,4,16,6,22,24,25};
        this.func_num = Func_num[func_num0-1];
        int cf_num,i,j;
        cf_num=10;

        if (ini_flag==1) 
        {
            if ((n_flag!=nx)||(func_flag!=func_num)) /* check if nx or func_num are changed, reinitialization*/
            {
                    ini_flag=0;
            }
        }
        if (ini_flag==0) /* initiailization*/
        {

            /**
             * START
             */
            
            y=new double[nx];
            z=new double[nx];
            x_bound=new double[nx];
            for (i=0; i<nx; i++)
                    x_bound[i]=100.0;

            if (!(nx==2||nx==5||nx==10||nx==15||nx==20||nx==30||nx==50||nx==100))
            {
                    System.out.println("\nError: Test functions are only defined for D=2,5,10,15,20,30,50,100.");
            }

            if (nx==2&&(func_num==4||func_num==216||func_num==36))
            {
                    System.out.println("\nError: NOT defined for D=2.\n");
            }

            /*Load Matrix M*****************************************************/
            URL resource = Cec2020.class.getResource("/Cec2020/input_data/M_"+func_num+"_D" + nx + ".txt");
            File fpt = Paths.get(resource.toURI()).toFile();//* Load M data *
            Scanner input = new Scanner(fpt);
            input.useLocale(Locale.US);
            if (!fpt.exists())
            {
                System.out.println("\n Error: Cannot open input file for reading.");
            }

            if (func_num<20)
            {
                    M=new double[nx*nx]; 

                    for (i=0;i<nx*nx; i++)
                    {
                        M[i]=input.nextDouble();
                    }
            }
            else
            {
                    M=new double[cf_num*nx*nx]; 

                    for (i=0; i<cf_num*nx*nx; i++)
                    {
                        M[i]=input.nextDouble();
                    }

            }
            input.close();


            /*Load shift_data***************************************************/



            if (func_num<20)
            {
                    resource = Cec2020.class.getResource("/Cec2020/input_data/shift_data_"+func_num+".txt");
                    fpt = Paths.get(resource.toURI()).toFile();
                    input = new Scanner(fpt);
                    input.useLocale(Locale.US);
                    if (!fpt.exists())
                    {
                            System.out.println("\n Error: Cannot open input file for reading ");
                    }

                    OShift=new double[nx];
                    for(i=0;i<nx;i++)
                    {
                        OShift[i]=input.nextDouble();
                        if (OShift == null)
                        {
                                System.out.println("\nError: there is insufficient memory available!");
                        }
                    }
                    input.close();
            }
            else
            {

                    OShift=new double[nx*cf_num];

                    resource = Cec2020.class.getResource("/Cec2020/input_data/shift_data_"+func_num+".txt");
                    fpt = Paths.get(resource.toURI()).toFile();								
                    FileReader reader = new FileReader(fpt);
                    BufferedReader br = new BufferedReader(reader);
                    String[] s = new String[100];

                    for (i=0;i<cf_num;i++){
                            s[i] = br.readLine();
                            String[] array = s[i].split("\\s+");
                            double[] temp = new double[array.length-1];

                            for ( int k = 0; k < array.length-1; k++) {
                                temp[k]= Double.parseDouble(array[k+1]);

                            }

                            for (j=0;j<nx;j++){

                                    OShift[i*nx+j] = temp[j];

                            }

                    }

                    br.close();
                    reader.close();
                    input.close();


            }

            input.close();



            /*Load Shuffle_data*******************************************/

            if (func_num==4||func_num==6||(func_num>=11&&func_num<=20))
            {
                    resource = Cec2020.class.getResource("/Cec2020/input_data/shuffle_data_"+func_num+"_D"+nx+".txt");
                    fpt = Paths.get(resource.toURI()).toFile();
                    input = new Scanner(fpt);
                    input.useLocale(Locale.US);
                    if (!fpt.exists())
                    {
                        System.out.println("\n Error: Cannot open input file for reading ");
                    }

                    SS = new int[nx];

                    for(i=0;i<nx;i++)
                    {
                            SS[i] = input.nextInt();
                    }	
            }
            else if (func_num==29||func_num==30)
            {
                    resource = Cec2020.class.getResource("/Cec2020/input_data/shuffle_data_"+func_num+"_D"+nx+".txt");
                    fpt = Paths.get(resource.toURI()).toFile();
                    input = new Scanner(fpt);
                    input.useLocale(Locale.US);
                    if (!fpt.exists())
                    {
                        System.out.println("\n Error: Cannot open input file for reading ");
                    }

                    SS = new int[nx*cf_num];

                    for(i=0;i<nx*cf_num;i++)
                    {
                            SS[i] = input.nextInt();
                    }
            }
            input.close();

            n_flag=nx;
            func_flag=func_num;
            ini_flag=1;
            
            /**
             * END
             */
        }

        //Adding info about function limits
        this.limits = this.findLimits();
        
    }
    
    @Override
    public double fitness(Individual individual) {

        return this.fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {
       
        //1,2,3,7,4,16,6,22,24,25

        if((this.func_num == 6 && this.nx == 5)||(this.func_num == 16 && this.nx == 5)) {
            return this.optimum();
        }
        
        double[] t = new double[nx];
        double out = 0;
        
        for (int j=0; j<nx; j++){
            t[j] = vector[j];
        }

        switch(func_num)
        {
            case 1:	
                out=bent_cigar_func(t,out,nx,OShift,M,1,1);
                out+=100.0;
                break;
            case 2:	
                out=schwefel_func(t,out,nx,OShift,M,1,1);
                out+=1100.0;
                break;
            case 3:	
                out=bi_rastrigin_func(t,out,nx,OShift,M,1,1);
                out+=700.0;
                break;
            case 7:	
                out=griewank_func(t,out,nx,OShift,M,1,1);
                out+=1900.0;
                break;
            case 4:	
                out=hf01(t,out,nx,OShift,M,SS,1,1);
                out+=1700.0;
                break;
            case 16:	
                out=hf06(t,out,nx,OShift,M,SS,1,1);
                out+=1600.0;
                break;
            case 6:
                out=hf05(t,out,nx,OShift,M,SS,1,1);
                out+=2100.0;
                break;
            case 22:	
                out=cf02(t,out,nx,OShift,M,1);
                out+=2200.0;
                break;
            case 24:
                out=cf04(t,out,nx,OShift,M,1);
                out+=2400.0;
                break;
            case 25:	
                out=cf05(t,out,nx,OShift,M,1);
                out+=2500.0;
                break;

        default:
                System.out.println("\nError: There are only 30 test functions in this test suite!");
                out = 0.0;
                break;
        }
        
        if((out-this.optimum())<(1e-8)) {
            out = this.optimum();
        }
        
        return out;
        
    }

    @Override
    public void constrain(Individual individual) {
        IndividualUtil.randIfOutOfBounds(individual, -100, 100);
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] vector = new double[dim];
        Random rnd = new UniformRandom();
        for (int i = 0; i < dim; i++) vector[i] = rnd.nextDouble(-100, 100);
        return vector;
    }

    @Override
    public double fixedAccLevel() {
        return 0;
    }

    @Override
    public double[] optimumPosition() {
   
        return Arrays.copyOf(this.OShift,this.nx);
        
    }
    
    @Override
    public double optimum() {
        
        
        //1,2,3,7,4,16,6,22,24,25
        switch(this.func_num) {
            case 1:
                return 100;

            case 2:
                return 1100;

            case 3:
                return 700;

            case 7:
                return 1900;

            case 4:
                return 1700;

            case 16:
                return 1600;

            case 6:
                return 2100;

            case 22:
                return 2200;

            case 24:
                return 2400;

            case 25:
                return 2500;
                
            default:
                return 0;

        }
    }

    @Override
    public double max(int dim) {
        return 100;
    }

    @Override
    public double min(int dim) {
        return -100;
    }
    
    /**
     * 
     * FUNCTIONS
     * 
     */
    
    
    double ellips_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Ellipsoidal */
    {
        int i;
        f = 0.0;
        sr_func(x,z,nx,Os,Mr,1.0,s_flag,r_flag);/*shift and rotate*/

        for (i=0; i<nx; i++)
        {
            f += Math.pow(10.0,6.0*i/(nx-1))*z[i]*z[i];
        }
        return f;
    }

    double bent_cigar_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Bent_Cigar */
    {
            int i;
            sr_func(x,z,nx,Os,Mr,1.0,s_flag,r_flag);/*shift and rotate*/

            f = z[0]*z[0];
            for (i=1; i<nx; i++)
        {
            f += Math.pow(10.0,6.0)*z[i]*z[i];
        }
        return f;
    }

    double discus_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Discus */
    {
        int i;
        sr_func(x,z,nx,Os,Mr,1.0,s_flag,r_flag);/*shift and rotate*/

            f = Math.pow(10.0,6.0)*z[0]*z[0];
        for (i=1; i<nx; i++)
        {
            f += z[i]*z[i];
        }

        return f;
    }

    double rosenbrock_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Rosenbrock's */
    {
        int i;
            double tmp1,tmp2;
            f = 0.0;
        sr_func(x,z,nx,Os,Mr,2.048/100.0,s_flag,r_flag);/*shift and rotate*/
        z[0] +=1.0; //shift to origin
        for (i=0; i<nx-1; i++)
        {
                    z[i+1] += 1.0; //shift to orgin
            tmp1=z[i]*z[i]-z[i+1];
                    tmp2=z[i]-1.0;
            f += 100.0*tmp1*tmp1 +tmp2*tmp2;
        }


        return f;
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

    double rastrigin_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Rastrigin's  */
    {
        int i;
        f=0.0;

        sr_func(x,z,nx,Os,Mr,5.12/100.0,s_flag,r_flag);/*shift and rotate*/ 

        for(i=0;i<nx;i++)
        {
                f += (z[i]*z[i] - 10.0*Math.cos(2.0*PI*z[i]) + 10.0);
        }

        return f;
    }

    double grie_rosen_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag, int r_flag) /* Griewank-Rosenbrock */
    {
        int i;
        double temp,tmp1,tmp2;
        f=0.0;

        sr_func (x, z, nx, Os, Mr, 5.0/100.0, s_flag, r_flag); /* shift and rotate */

        z[0] += 1.0;//shift to orgin
        
        for (i=0; i<nx-1; i++)
        {
            z[i+1] += 1.0;//shift to orgin
            tmp1 = z[i]*z[i]-z[i+1];
            tmp2 = z[i]-1.0;
            temp = 100.0*tmp1*tmp1 + tmp2*tmp2;
             f += (temp*temp)/4000.0 - Math.cos(temp) + 1.0;
        }
        
        tmp1 = z[nx-1]*z[nx-1]-z[0];
        tmp2 = z[nx-1]-1.0;
        temp = 100.0*tmp1*tmp1 + tmp2*tmp2;;
        f += (temp*temp)/4000.0 - Math.cos(temp) + 1.0;
        
        return f;
    }

    
    double bi_rastrigin_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag, int r_flag) /* Lunacek Bi_rastrigin Function */
    {
        int i;
        double mu0=2.5,d=1.0,s,mu1,tmp,tmp1,tmp2;
        double[] tmpx = new double[nx];
        
        f=0.0;

        s=1.0-1.0/(2.0*Math.pow(nx+20.0,0.5)-8.2);
        mu1=-Math.pow((mu0*mu0-d)/s,0.5);

        if (s_flag==1)
                shiftfunc(x, y, nx, Os);
        else
        {
            for (i=0; i<nx; i++)//shrink to the orginal search range
            {
                    y[i] = x[i];
            }
        }
        
        for (i=0; i<nx; i++)//shrink to the orginal search range
        {
            y[i] *= 10.0/100.0;
        }

        for (i = 0; i < nx; i++)
        {
            tmpx[i]=2*y[i];
            if (Os[i] < 0.0)
                tmpx[i] *= -1.;
        }
        
        for (i=0; i<nx; i++)
        {
                z[i]=tmpx[i];
                tmpx[i] += mu0;
        }
        
        tmp1=0.0;tmp2=0.0;
        
        for (i=0; i<nx; i++)
        {
            tmp = tmpx[i]-mu0;
            tmp1 += tmp*tmp;
            tmp = tmpx[i]-mu1;
            tmp2 += tmp*tmp;
        }
        
        tmp2 *= s;
        tmp2 += d*nx;
        tmp=0.0;

        if (r_flag==1)
        {
            rotatefunc(z, y, nx, Mr);
            for (i=0; i<nx; i++)
            {
                    tmp+=Math.cos(2.0*PI*y[i]);
            }	
            if(tmp1<tmp2)
                f = tmp1;
            else
                f = tmp2;
            
            f += 10.0*(nx-tmp);
        }
        else
        {
            for (i=0; i<nx; i++)
            {
                tmp+=Math.cos(2.0*PI*z[i]);
            }	
            if(tmp1<tmp2)
                    f = tmp1;
            else
                    f = tmp2;
            
            f += 10.0*(nx-tmp);
        }

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

    double katsuura_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Katsuura  */
    {
        int i,j;
            double temp,tmp1,tmp2,tmp3;
            tmp3=Math.pow(1.0*nx,1.2);

            sr_func(x,z,nx,Os,Mr,5/100.0,s_flag,r_flag);/*shift and rotate*/ 


        f=1.0;
        for (i=0; i<nx; i++)
            {
                    temp=0.0;
                    for (j=1; j<=32; j++)
                    {
                            tmp1=Math.pow(2.0,j);
                            tmp2=tmp1*z[i];
                            temp += Math.abs(tmp2-Math.floor(tmp2+0.5))/tmp1;
                    }
                    f *= Math.pow(1.0+(i+1)*temp,10.0/tmp3);
        }
            tmp1=10.0/nx/nx;
        f=f*tmp1-tmp1;

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
            f = 0.0;
            for (i=0;i<nx;i++)
            {
                    z[i] = z[i] - 1.0; //shift to orgin
                    r2 += z[i]*z[i];
                    sum_z += z[i];

            }
            f = Math.pow(Math.abs(r2-nx), 2*alpha) + (0.5*r2 + sum_z)/nx + 0.5;

            return f;
    }

    double hgbat_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) 
    /*HGBat, provided by Hans-Georg Beyer (HGB)*/
    /*original global optimum: [-1,-1,...-1]*/
    {
            int i;
            double alpha,r2,sum_z;
            alpha=1.0/4.0;

            sr_func (x, z, nx, Os, Mr, 5.0/100.0, s_flag, r_flag); /* shift and rotate */

            r2 = 0.0;
            sum_z=0.0;
        for (i=0; i<nx; i++)
        {
                    z[i]=z[i]-1.0;//shift to orgin
            r2 += z[i]*z[i];
                    sum_z += z[i];
        }
        f=Math.pow(Math.abs(Math.pow(r2,2.0)-Math.pow(sum_z,2.0)),2*alpha) + (0.5*r2 + sum_z)/nx + 0.5;
        return f;

    }

    double escaffer6_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Expanded Scaffer??s F6  */
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

    double hf01 (double[] x, double f, int nx, double[] Os,double[] Mr,int[] S,int s_flag,int r_flag) /* Hybrid Function 1 */
    {
            int i,tmp,cf_num=3;
            double[] fit = new double[3];
            int[] G = new int[3];
            int[] G_nx = new int[3];
            double[] Gp = {0.3,0.3,0.4};

            tmp=0;
            for (i=1; i<cf_num; i++)
            {
                    G_nx[i] = (int)Math.ceil(Gp[i]*nx);
                    tmp += G_nx[i];
            }
            G_nx[0]=nx-tmp;
            G[0]=0;
            for (i=1; i<cf_num; i++)
            {
                    G[i] = G[i-1]+G_nx[i-1];
            }

            sr_func (x, z, nx, Os, Mr, 1.0, s_flag, r_flag); /* shift and rotate */

            for (i=0; i<nx; i++)
            {
                    y[i]=z[S[i]-1];
            }

            double[] ty,tO,tM;

            i=0;
             ty = new double[G_nx[i]];
             tO = new double[G_nx[i]];
             tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[ii];
                    tO[ii]=Os[ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=schwefel_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=1;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+ii];
                    tO[ii]=Os[G_nx[i-1]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=rastrigin_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=2;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-2]+G_nx[i-1]+ii];
                    tO[ii]=Os[G_nx[i-2]+G_nx[i-1]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=ellips_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            f=0.0;
            for(i=0;i<cf_num;i++)
            {
                    f += fit[i];
            }
            return f;
    }

    double hf02 (double[] x, double f, int nx, double[] Os,double[] Mr,int[] S,int s_flag,int r_flag) /* Hybrid Function 2 */
    {
            int i,tmp,cf_num=3;
            double[] fit = new double[3];
            int[] G = new int[3];
            int[] G_nx = new int[3];
            double[] Gp={0.3,0.3,0.4};

            tmp=0;
            for (i=0; i<cf_num-1; i++)
            {
                    G_nx[i] = (int)Math.ceil(Gp[i]*nx);
                    tmp += G_nx[i];
            }
            G_nx[cf_num-1]=nx-tmp;

            G[0]=0;
            for (i=1; i<cf_num; i++)
            {
                    G[i] = G[i-1]+G_nx[i-1];
            }

            sr_func (x, z, nx, Os, Mr, 1.0, s_flag, r_flag); /* shift and rotate */

            for (i=0; i<nx; i++)
            {
                    y[i]=z[S[i]-1];
            }



            double[] ty,tO,tM;

            i=0;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[ii];
                    tO[ii]=Os[ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=bent_cigar_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=1;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+ii];
                    tO[ii]=Os[G_nx[i-1]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=hgbat_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=2;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-2]+G_nx[i-1]+ii];
                    tO[ii]=Os[G_nx[i-1]+G_nx[i-2]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=rastrigin_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            f=0.0;
            for(i=0;i<cf_num;i++)
            {
                    f += fit[i];
            }
            return f;

    }

    double hf03 (double[] x, double f, int nx, double[] Os,double[] Mr,int[] S,int s_flag,int r_flag) /* Hybrid Function 3 */
    {
            int i,tmp,cf_num=4;
            double[] fit = new double[4];
            int[] G_nx = new int[4];
            int[] G = new int[4];
            double[] Gp={0.2,0.2,0.3,0.3};

            tmp=0;
            for (i=0; i<cf_num-1; i++)
            {
                    G_nx[i] = (int)Math.ceil(Gp[i]*nx);
                    tmp += G_nx[i];
            }
            G_nx[cf_num-1]=nx-tmp;

            G[0]=0;
            for (i=1; i<cf_num; i++)
            {
                    G[i] = G[i-1]+G_nx[i-1];
            }

            sr_func (x, z, nx, Os, Mr, 1.0, s_flag, r_flag); /* shift and rotate */

            for (i=0; i<nx; i++)
            {
                    y[i]=z[S[i]-1];
            }


            double[] ty,tO,tM;

            i=0;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[ii];
                    tO[ii]=Os[ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=griewank_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=1;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+ii];
                    tO[ii]=Os[G_nx[i-1]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=weierstrass_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=2;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+G_nx[i-2]+ii];
                    tO[ii]=Os[G_nx[i-1]+G_nx[i-2]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=rosenbrock_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=3;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+G_nx[i-2]+G_nx[i-3]+ii];
                    tO[ii]=Os[G_nx[i-1]+G_nx[i-2]+G_nx[i-3]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=escaffer6_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            f=0.0;
            for(i=0;i<cf_num;i++)
            {
                    f += fit[i];
            }
            return f;

    }

    double hf04 (double[] x, double f, int nx, double[] Os,double[] Mr,int[] S,int s_flag,int r_flag) /* Hybrid Function 4 */
    {
            int i,tmp,cf_num=4;
            double[] fit = new double[4];
            int[] G = new int[4];
            int[] G_nx = new int[4];
            double[] Gp={0.2,0.2,0.3,0.3};

            tmp=0;
            for (i=0; i<cf_num-1; i++)
            {
                    G_nx[i] = (int)Math.ceil(Gp[i]*nx);
                    tmp += G_nx[i];
            }
            G_nx[cf_num-1]=nx-tmp;

            G[0]=0;
            for (i=1; i<cf_num; i++)
            {
                    G[i] = G[i-1]+G_nx[i-1];
            }

            sr_func (x, z, nx, Os, Mr, 1.0, s_flag, r_flag); /* shift and rotate */

            for (i=0; i<nx; i++)
            {
                    y[i]=z[S[i]-1];
            }


            double[] ty,tO,tM;

            i=0;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[ii];
                    tO[ii]=Os[ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=hgbat_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=1;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+ii];
                    tO[ii]=Os[G_nx[i-1]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=discus_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=2;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+G_nx[i-2]+ii];
                    tO[ii]=Os[G_nx[i-1]+G_nx[i-2]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=grie_rosen_func(ty,fit[i],G_nx[i],tO,tM,0,0);
            i=3;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+G_nx[i-2]+G_nx[i-3]+ii];
                    tO[ii]=Os[G_nx[i-1]+G_nx[i-2]+G_nx[i-3]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=rastrigin_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            f=0.0;
            for(i=0;i<cf_num;i++)
            {
                    f += fit[i];
            }
            return f;

    }

    double hf05 (double[] x, double f, int nx, double[] Os,double[] Mr,int[] S,int s_flag,int r_flag) /* Hybrid Function 5 */
    {
            int i,tmp,cf_num=5;
            double[] fit = new double[5];
            int[] G = new int[5];
            int[] G_nx = new int[5];
            double[] Gp={0.1,0.2,0.2,0.2,0.3};

            if (nx==5) {
                G_nx[0] = 1;
                G_nx[1] = 1;
                G_nx[2] = 1;
                G_nx[3] = 1;
                G_nx[4] = 1;
            }
            else {
                tmp=0;
                for (i=1; i<cf_num; i++)
                {
                        G_nx[i] = (int)Math.ceil(Gp[i]*nx);
                        tmp += G_nx[i];
                }
                G_nx[0]=nx-tmp;
            }
            
            G[0]=0;
            for (i=1; i<cf_num; i++)
            {
                    G[i] = G[i-1]+G_nx[i-1];
            }


            sr_func (x, z, nx, Os, Mr, 1.0, s_flag, r_flag); /* shift and rotate */



            for (i=0; i<nx; i++)
            {
                    y[i]=z[S[i]-1];
            }

            double[] ty,tO,tM;

            i=0;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[ii];
                    tO[ii]=Os[ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=escaffer6_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=1;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+ii];
                    tO[ii]=Os[G_nx[i-1]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=hgbat_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=2;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+G_nx[i-2]+ii];
                    tO[ii]=Os[G_nx[i-1]+G_nx[i-2]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }

            fit[i]=rosenbrock_func(ty,fit[i],G_nx[i],tO,tM,0,0);
            i=3;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+G_nx[i-2]+G_nx[i-3]+ii];
                    tO[ii]=Os[G_nx[i-1]+G_nx[i-2]+G_nx[i-3]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=schwefel_func(ty,fit[i],G_nx[i],tO,tM,0,0);
            i=4;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+G_nx[i-2]+G_nx[i-3]+G_nx[i-4]+ii];
                    tO[ii]=Os[G_nx[i-1]+G_nx[i-2]+G_nx[i-3]+G_nx[i-4]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=ellips_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            //for(i=0;i<cf_num;i++){
            //	System.out.println("fithf05["+i+"]"+"="+fit[i]);
            //}

            f=0.0;
            for(i=0;i<cf_num;i++)
            {
                    f += fit[i];
            }
            return f;

    }

    double hf06(double[] x, double f, int nx, double[] Os,double[] Mr,int[] S,int s_flag,int r_flag) /* Hybrid Function 6 */
    {
            int i,tmp,cf_num=4;
            double[] fit = new double[4];
            int[] G = new int[4];
            int[] G_nx = new int[4];
            double[] Gp={0.2,0.2,0.3,0.3};

            if(nx==5) {
                G_nx[0] = 1;
                G_nx[1] = 1;
                G_nx[2] = 1;
                G_nx[3] = 2;
            }
            else {
                tmp=0;
                for (i=1; i<cf_num; i++)
                {
                    G_nx[i] = (int)Math.ceil(Gp[i]*nx);
                    tmp += G_nx[i];
                }
                G_nx[0]=nx-tmp;
            }

            G[0]=0;
            for (i=1; i<cf_num; i++)
            {
                G[i] = G[i-1]+G_nx[i-1];
            }

            sr_func (x, z, nx, Os, Mr, 1.0, s_flag, r_flag); /* shift and rotate */

            for (i=0; i<nx; i++)
            {
                    y[i]=z[S[i]-1];
            }

            double[] ty,tO,tM;

            i=0;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[ii];
                    tO[ii]=Os[ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=escaffer6_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=1;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+ii];
                    tO[ii]=Os[G_nx[i-1]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=hgbat_func(ty,fit[i],G_nx[i],tO,tM,0,0);

            i=2;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+G_nx[i-2]+ii];
                    tO[ii]=Os[G_nx[i-1]+G_nx[i-2]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=rosenbrock_func(ty,fit[i],G_nx[i],tO,tM,0,0);
            i=3;
            ty = new double[G_nx[i]];
            tO = new double[G_nx[i]];
            tM = new double[G_nx[i]];
            for(int ii=0;ii<G_nx[i];ii++)
            {
                    ty[ii]=y[G_nx[i-1]+G_nx[i-2]+G_nx[i-3]+ii];
                    tO[ii]=Os[G_nx[i-1]+G_nx[i-2]+G_nx[i-3]+ii];
                    tM[ii]=Mr[i*nx+ii];
            }
            fit[i]=schwefel_func(ty,fit[i],G_nx[i],tO,tM,0,0);


            //for(i=0;i<cf_num;i++){
            //	System.out.println("fithf06["+i+"]"+"="+fit[i]);
            //}

            f=0.0;
            for(i=0;i<cf_num;i++)
            {
                    f += fit[i];
            }
            return f;

    }

    double cf01 (double[] x, double f, int nx, double[] Os,double[] Mr,int r_flag) /* Composition Function 1 */
    {
            int i,j,cf_num=5;
            double[] fit = new double[5];// fit[5];
            double[] delta = {10, 20, 30, 40, 50};
            double[] bias = {0, 100, 200, 300, 400};

            double[] tOs = new double[nx];
            double[] tMr = new double[cf_num*nx*nx];

            i=0;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=rosenbrock_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=10000*fit[i]/1e+4;

            i=1;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=ellips_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=10000*fit[i]/1e+10;

            i=2;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=bent_cigar_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=10000*fit[i]/1e+30;

            i=3;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=discus_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=10000*fit[i]/1e+10;

            i=4;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=ellips_func(x,fit[i],nx,tOs,tMr,1,0);
            fit[i]=10000*fit[i]/1e+10;


            return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);

    }

    double cf02 (double[] x, double f, int nx, double[] Os,double[] Mr,int r_flag) /* Composition Function 2 */
    {
            int i,j,cf_num=3;
            double[] fit = new double[3];
            double[] delta = {10,20,30};
            double[] bias = {0, 100, 200};

            double[] tOs = new double[nx];
            double[] tMr = new double[cf_num*nx*nx];

            i=0;
            for(j=0;j<nx;j++){
                tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,0);

            i=1;
            for(j=0;j<nx;j++){
                tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=griewank_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=1000*fit[i]/(1e+2);

            i=2;
            for(j=0;j<nx;j++){
                tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);


            return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
    }

    double cf03 (double[] x, double f, int nx, double[] Os,double[]Mr,int r_flag) /* Composition Function 3 */
    {
            int i,j,cf_num=3;
            double[] fit=new double[3];
            double[] delta = {10,30,50};
            double[] bias = {0, 100, 200};

            double[] tOs = new double[nx];
            double[] tMr = new double[cf_num*nx*nx];


            i=0;
                    for(j=0;j<nx;j++){
                            tOs[j] = Os[i*nx+j];
                    }
                    for(j=0;j<nx*nx;j++){
                            tMr[j] = Mr[i*nx*nx+j];
                    }
                    fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
                    fit[i]=1000*fit[i]/4e+3;

            i=1;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=1000*fit[i]/1e+3;

            i=2;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=ellips_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=1000*fit[i]/1e+10;



            return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
    }

    double cf04 (double[] x, double f, int nx, double[] Os,double[] Mr,int r_flag) /* Composition Function 4 */
    {
            int i,j,cf_num=4;
            double[] fit=new double[4];
            double[] delta = {10,20,30,40};
            double[] bias = {0, 100, 200, 300};

            double[] tOs = new double[nx];
            double[] tMr = new double[cf_num*nx*nx];

            i=0;
            for(j=0;j<nx;j++){
                tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                tMr[j] = Mr[i*nx*nx+j];

            }
            fit[i]=ackley_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=1000*fit[i]/(1e+2);

            i=1;
            for(j=0;j<nx;j++){
                tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                tMr[j] = Mr[i*nx*nx+j];

            }
            fit[i]=ellips_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=10000*fit[i]/(1e+10);

            i=2;
            for(j=0;j<nx;j++){
                tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=griewank_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=1000*fit[i]/(1e+2);

            i=3;
            for(j=0;j<nx;j++){
                tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);

            return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
    }

    double cf05 (double[] x, double f, int nx, double[] Os,double[] Mr,int r_flag) /* Composition Function 5 */
    {
            int i,j,cf_num=5;
            double[] fit=new double[5];
            double[] delta = {10,20,30,40,50};
            double[] bias = {0, 100,200,300,400};

            double[] tOs = new double[nx];
            double[] tMr = new double[cf_num*nx*nx];

            i=0;
            for(j=0;j<nx;j++){
                tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=10000*fit[i]/(1e+3);
            i=1;
            for(j=0;j<nx;j++){
                tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=happycat_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=1000*fit[i]/(1e+3);
            i=2;
            for(j=0;j<nx;j++){
                tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=ackley_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=1000*fit[i]/(1e+2);
            i=3;
            for(j=0;j<nx;j++){
                tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=discus_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=10000*fit[i]/(1e+10);
            i=4;
            for(j=0;j<nx;j++){
                tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=rosenbrock_func(x,fit[i],nx,tOs,tMr,1,r_flag);

            return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
    }

    double cf06 (double[] x, double f, int nx, double[] Os,double[] Mr,int r_flag) /* Composition Function 6 */
    {
            int i,j,cf_num=5;
            double[] fit=new double[5];
            double[] delta = {10,20,30,40,50};
            double[] bias = {0, 100,200,300,400};

            double[] tOs = new double[nx];
            double[] tMr = new double[cf_num*nx*nx];

            i=0;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=grie_rosen_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=10000*fit[i]/4e+3;
            i=1;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=happycat_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=10000*fit[i]/1e+3;
            i=2;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=10000*fit[i]/4e+3;
            i=3;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=escaffer6_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=10000*fit[i]/2e+7;
            i=4;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<cf_num*nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            fit[i]=ellips_func(x,fit[i],nx,tOs,tMr,1,r_flag);
            fit[i]=10000*fit[i]/1e+10;

            return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
    }

    double cf07 (double[] x, double f, int nx, double[] Os,double[] Mr,int[] SS,int r_flag) /* Composition Function 7 */
    {
            int i,j,cf_num=3;
            double[] fit=new double[3];
            double[] delta = {10,30,50};
            double[] bias = {0, 100, 200};

            double[] tOs = new double[nx];
            double[] tMr = new double[cf_num*nx*nx];
            int[] tSS = new int[nx];


            i=0;
                    for(j=0;j<nx;j++){
                            tOs[j] = Os[i*nx+j];
                    }
                    for(j=0;j<nx*nx;j++){
                            tMr[j] = Mr[i*nx*nx+j];
                    }
                    for(j=0;j<nx;j++){
                            tSS[j] = SS[i*nx+j];
                    }
                    fit[i]=hf01(x,fit[i],nx,tOs,tMr,tSS,1,r_flag);


            i=1;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            for(j=0;j<nx;j++){
                    tSS[j] = SS[i*nx+j];
            }
            fit[i]=hf02(x,fit[i],nx,tOs,tMr,tSS,1,r_flag);

            i=2;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            for(j=0;j<nx;j++){
                    tSS[j] = SS[i*nx+j];
            }
            fit[i]=hf03(x,fit[i],nx,tOs,tMr,tSS,1,r_flag);




            return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
    }

    double cf08 (double[] x, double f, int nx, double[] Os,double[]Mr,int[] SS,int r_flag) /* Composition Function 8 */
    {
            int i,j,cf_num=3;
            double[] fit=new double[3];
            double[] delta = {10,30,50};
            double[] bias = {0, 100, 200};

            double[] tOs = new double[nx];
            double[] tMr = new double[cf_num*nx*nx];
            int[] tSS = new int[nx];


            i=0;
                    for(j=0;j<nx;j++){
                            tOs[j] = Os[i*nx+j];
                    }
                    for(j=0;j<nx*nx;j++){
                            tMr[j] = Mr[i*nx*nx+j];
                    }
                    for(j=0;j<nx;j++){
                            tSS[j] = SS[i*nx+j];
                    }
                    fit[i]=hf04(x,fit[i],nx,tOs,tMr,tSS,1,r_flag);


            i=1;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            for(j=0;j<nx;j++){
                    tSS[j] = SS[i*nx+j];
            }
            fit[i]=hf05(x,fit[i],nx,tOs,tMr,tSS,1,r_flag);

            i=2;
            for(j=0;j<nx;j++){
                    tOs[j] = Os[i*nx+j];
            }
            for(j=0;j<nx*nx;j++){
                    tMr[j] = Mr[i*nx*nx+j];
            }
            for(j=0;j<nx;j++){
                    tSS[j] = SS[i*nx+j];
            }
            fit[i]=hf06(x,fit[i],nx,tOs,tMr,tSS,1,r_flag);




            return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
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

    void asyfunc (double[] x, double[] xasy, int nx, double beta)
    {
            int i;
        for (i=0; i<nx; i++)
        {
                    if (x[i]>0)
            xasy[i]=Math.pow(x[i],1.0+beta*i/(nx-1)*Math.pow(x[i],0.5));
        }
    }


    void oszfunc (double[] x, double[] xosz, int nx)
    {
            int i,sx;
            double c1,c2,xx=0;
        for (i=0; i<nx; i++)
        {
                    if (i==0||i==nx-1)
            {
                            if (x[i]!=0)
                                    xx=Math.log(Math.abs(x[i]));//xx=log(fabs(x[i]));
                            if (x[i]>0)
                            {	
                                    c1=10;
                                    c2=7.9;
                            }
                            else
                            {
                                    c1=5.5;
                                    c2=3.1;
                            }	
                            if (x[i]>0)
                                    sx=1;
                            else if (x[i]==0)
                                    sx=0;
                            else
                                    sx=-1;
                            xosz[i]=sx*Math.exp(xx+0.049*(Math.sin(c1*xx)+Math.sin(c2*xx)));
                    }
                    else
                            xosz[i]=x[i];
        }
    }


    double cf_cal(double[] x, double f, int nx, double[] Os,double[] delta,double[] bias,double[] fit, int cf_num)
    {
            int i,j;

            double[] w;
            double w_max=0,w_sum=0;
            w=new double[cf_num];
            for (i=0; i<cf_num; i++)
            {
                    fit[i]+=bias[i];
                    w[i]=0;
                    for (j=0; j<nx; j++)
                    {
                            w[i]+=Math.pow(x[j]-Os[i*nx+j],2.0);
                    }
                    if (w[i]!=0)
                            w[i]=Math.pow(1.0/w[i],0.5)*Math.exp(-w[i]/2.0/nx/Math.pow(delta[i],2.0));
                    else
                            w[i]=INF;
                    if (w[i]>w_max)
                            w_max=w[i];
            }

            for (i=0; i<cf_num; i++)
            {
                    w_sum=w_sum+w[i];
            }
            if(w_max==0)
            {
                    for (i=0; i<cf_num; i++)
                            w[i]=1;
                    w_sum=cf_num;
            }
            f = 0.0;
        for (i=0; i<cf_num; i++)
        {
                    f=f+w[i]/w_sum*fit[i];
        }

        return f;

    }
        
    @Override
    public String name() {
        return "CEC2020-f" + this.func_real;
    }
    
    /**
     * 
     * @return 
     */
    private double[][] findLimits() {
        
        double[][] limits = new double[this.nx][2];
        
        double threshold = Math.pow(10,-15), sens = Math.pow(10, -8);
        double mid = 0, left, right, res, old;
        double[] active;

        for(int i = 0; i < this.nx; i++) {
            
//            System.out.println("Computing dim: " + (i+1));
            
            //Finds the lower optimum limit
            left = this.min(this.nx);
            right = this.optimumPosition()[i];
            old = 0.0;
            while(Math.abs(left-right) > threshold && Math.abs(left-right) != old) {
                
                old = Math.abs(left-right);
                
                mid = (left+right)/2.0;
                active = this.optimumPosition();
                active[i] = mid;
                res = this.fitness(active);
                if(Math.abs(res - this.optimum()) <= sens) {
                    right = mid;
                } else {
                    left = mid;
                }
                
            }
            limits[i][0] = mid;
//            System.out.println("low: " + mid);
            
            //Finds the upper optimum limit
            right = this.max(this.nx);
            left = this.optimumPosition()[i];
            old = 0.0;
            while(Math.abs(left-right) > threshold && Math.abs(left-right) != old) {
                
                old = Math.abs(left-right);
                
                mid = (left+right)/2.0;
                active = this.optimumPosition();
                active[i] = mid;
                res = this.fitness(active);
                if(Math.abs(res - this.optimum()) <= sens) {
                    left = mid;
                } else {
                    right = mid;
                }
                
            }
            limits[i][1] = mid;
//            System.out.println("upp: " + mid);
            
        }
        
        return limits;
    }
    
    public static void main(String[] args) {
        
        try {
            Cec2020 test = new Cec2020(5, 1);
            System.out.println(Arrays.toString(test.getLimits(1)));
            System.out.println(test.fitness(new double[]{-55.2763983184293,-70.42955938473036,-29.61018186131012,-58.32676328262075,22.089601808455804}));
//        try {
//            int[] dims = new int[]{5,10,15,20};
//            for(int dim = 0; dim < dims.length; dim++) {
//                int dd = dims[dim];
//                System.out.print("new int[][][]{{");
//                for(int funcN = 1; funcN < 11; funcN++) {
//                
//                    Cec2020 func = new Cec2020(dd, funcN);
//                    double[][] limits = func.findLimits();
//                    
//                    for(int lim = 0; lim < limits.length; lim++) {
//                        
//                        System.out.print("{" + limits[lim][0] + "," + limits[lim][1] + "}");
//                        
//                        if(lim != limits.length-1)
//                            System.out.print(",");
//                        
//                    }
//                    System.out.print("}");
//                    if(funcN != 10)
//                        System.out.print(",");
//                    
//                }
//                System.out.println("}");
//                System.out.println();
//                System.out.println();
//                System.out.println();
//            }

/*
-1.4386804758751207e+01
*/

//{-55.27638580971947,-70.4295334103686,-29.610181901045507,-58.326763221008555,22.08960180295481}
//            res = test.fitness(new double[]{-55.27638580971947,-70.4295334103686,-29.610181901045507,-58.326763221008555,22.08960180295481});
//            System.out.println(res);
//            
//            res = test.fitness(new double[]{-1.4386804758751207e+01,7.0924510683363536e+01,5.4035479973086844e+01,-5.0249947519157985e+01,5.9854126935756824e+01,5.5338090773363639e+01,6.9756035554109161e+01,-8.9322980162372332e+00,3.8997577178756508e+01,4.1910300289238947e+01,-2.5503293682589799e+01,3.8681264045965513e+01,1.4585345438954116e+01,5.2297602990780776e+00,-4.2492532913802442e+01,-3.1909567445474476e+01,2.4433991652184162e+01,-6.0037932192924401e+01});
//            System.out.println(res);
//            
//            res = test.fitness(new double[]{-1.4386804758751207e+01,7.092451068336353e+01,5.4035479973086844e+01,-5.0249947519157985e+01,5.9854126935756824e+01,5.5338090773363639e+01,6.9756035554109161e+01,-8.9322980162372332e+00,3.8997577178756508e+01,4.1910300289238947e+01,-2.5503293682589799e+01,3.8681264045965513e+01,1.4585345438954116e+01,5.2297602990780776e+00,-4.2492532913802442e+01,-3.1909567445474476e+01,2.4433991652184162e+01,-6.0037932192924401e+01});
//            System.out.println(res);
//            
//            res = test.fitness(new double[]{-1.4386804758751207e+01,7.09245106833635e+01,5.4035479973086844e+01,-5.0249947519157985e+01,5.9854126935756824e+01,5.5338090773363639e+01,6.9756035554109161e+01,-8.9322980162372332e+00,3.8997577178756508e+01,4.1910300289238947e+01,-2.5503293682589799e+01,3.8681264045965513e+01,1.4585345438954116e+01,5.2297602990780776e+00,-4.2492532913802442e+01,-3.1909567445474476e+01,2.4433991652184162e+01,-6.0037932192924401e+01});
//            System.out.println(res);
//            
//            res = test.fitness(new double[]{-1.4386804758751207e+01,7.0924510683363e+01,5.4035479973086844e+01,-5.0249947519157985e+01,5.9854126935756824e+01,5.5338090773363639e+01,6.9756035554109161e+01,-8.9322980162372332e+00,3.8997577178756508e+01,4.1910300289238947e+01,-2.5503293682589799e+01,3.8681264045965513e+01,1.4585345438954116e+01,5.2297602990780776e+00,-4.2492532913802442e+01,-3.1909567445474476e+01,2.4433991652184162e+01,-6.0037932192924401e+01});
//            System.out.println(res);
//            
//            res = test.fitness(new double[]{-1.4386804758751207e+01,7.092451068336e+01,5.4035479973086844e+01,-5.0249947519157985e+01,5.9854126935756824e+01,5.5338090773363639e+01,6.9756035554109161e+01,-8.9322980162372332e+00,3.8997577178756508e+01,4.1910300289238947e+01,-2.5503293682589799e+01,3.8681264045965513e+01,1.4585345438954116e+01,5.2297602990780776e+00,-4.2492532913802442e+01,-3.1909567445474476e+01,2.4433991652184162e+01,-6.0037932192924401e+01});
//            System.out.println(res);
//            
//            res = test.fitness(new double[]{-1.4386804758751207e+01,7.09245106833e+01,5.4035479973086844e+01,-5.0249947519157985e+01,5.9854126935756824e+01,5.5338090773363639e+01,6.9756035554109161e+01,-8.9322980162372332e+00,3.8997577178756508e+01,4.1910300289238947e+01,-2.5503293682589799e+01,3.8681264045965513e+01,1.4585345438954116e+01,5.2297602990780776e+00,-4.2492532913802442e+01,-3.1909567445474476e+01,2.4433991652184162e+01,-6.0037932192924401e+01});
//            System.out.println(res);
//            
//            res = test.fitness(new double[]{-1.4386804758751207e+01,7.0924510683e+01,5.4035479973086844e+01,-5.0249947519157985e+01,5.9854126935756824e+01,5.5338090773363639e+01,6.9756035554109161e+01,-8.9322980162372332e+00,3.8997577178756508e+01,4.1910300289238947e+01,-2.5503293682589799e+01,3.8681264045965513e+01,1.4585345438954116e+01,5.2297602990780776e+00,-4.2492532913802442e+01,-3.1909567445474476e+01,2.4433991652184162e+01,-6.0037932192924401e+01});
//            System.out.println(res);
//            
//            res = test.fitness(new double[]{-1.4386804758751207e+01,7.092451068e+01,5.4035479973086844e+01,-5.0249947519157985e+01,5.9854126935756824e+01,5.5338090773363639e+01,6.9756035554109161e+01,-8.9322980162372332e+00,3.8997577178756508e+01,4.1910300289238947e+01,-2.5503293682589799e+01,3.8681264045965513e+01,1.4585345438954116e+01,5.2297602990780776e+00,-4.2492532913802442e+01,-3.1909567445474476e+01,2.4433991652184162e+01,-6.0037932192924401e+01});
//            System.out.println(res);
//
//            res = test.fitness(new double[]{-1.4386804758751207e+01,7.09245106e+01,5.4035479973086844e+01,-5.0249947519157985e+01,5.9854126935756824e+01,5.5338090773363639e+01,6.9756035554109161e+01,-8.9322980162372332e+00,3.8997577178756508e+01,4.1910300289238947e+01,-2.5503293682589799e+01,3.8681264045965513e+01,1.4585345438954116e+01,5.2297602990780776e+00,-4.2492532913802442e+01,-3.1909567445474476e+01,2.4433991652184162e+01,-6.0037932192924401e+01});
//            System.out.println(res);
//            res = test.fitness(new double[]{-1.4386804758751207e+01,7.0924510e+01,5.4035479973086844e+01,-5.0249947519157985e+01,5.9854126935756824e+01,5.5338090773363639e+01,6.9756035554109161e+01,-8.9322980162372332e+00,3.8997577178756508e+01,4.1910300289238947e+01,-2.5503293682589799e+01,3.8681264045965513e+01,1.4585345438954116e+01,5.2297602990780776e+00,-4.2492532913802442e+01,-3.1909567445474476e+01,2.4433991652184162e+01,-6.0037932192924401e+01});
//            System.out.println(res);
//            res = test.fitness(new double[]{-1.4386804758751207e+01,7.092451e+01,5.4035479973086844e+01,-5.0249947519157985e+01,5.9854126935756824e+01,5.5338090773363639e+01,6.9756035554109161e+01,-8.9322980162372332e+00,3.8997577178756508e+01,4.1910300289238947e+01,-2.5503293682589799e+01,3.8681264045965513e+01,1.4585345438954116e+01,5.2297602990780776e+00,-4.2492532913802442e+01,-3.1909567445474476e+01,2.4433991652184162e+01,-6.0037932192924401e+01});
//            System.out.println(res);
//            res = test.fitness(new double[]{-1.4386804758751207e+01,7.09245e+01,5.4035479973086844e+01,-5.0249947519157985e+01,5.9854126935756824e+01,5.5338090773363639e+01,6.9756035554109161e+01,-8.9322980162372332e+00,3.8997577178756508e+01,4.1910300289238947e+01,-2.5503293682589799e+01,3.8681264045965513e+01,1.4585345438954116e+01,5.2297602990780776e+00,-4.2492532913802442e+01,-3.1909567445474476e+01,2.4433991652184162e+01,-6.0037932192924401e+01});
//            System.out.println(res);
//
//            
//            System.out.println("Check this out!");
//            res = test.fitness(test.optimumPosition());
//            System.out.println(res);
        } catch (Exception ex) {
            Logger.getLogger(Cec2020.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
