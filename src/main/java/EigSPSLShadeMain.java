
import algorithm.de.EigSpsLShaDE;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.DoubleStream;
import model.tf.Cec2015;
import model.tf.TestFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author adam on 18/11/2015
 */
public class EigSPSLShadeMain {

    public static List<List<Object>> setUpForCec2015(int dimension) throws Exception{
        
        List<List<Object>> ret = new ArrayList<>();
        List<Object> settings;
        int funcNumber;

        /**
         * f1
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 1;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(459); //NPinit
        settings.add(12); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.4972); //Finit
        settings.add(0.9497); //ERinit
        settings.add(0.2892); //CRinit
        settings.add(0.1315); //w_F
        settings.add(0.9854); //w_ER
        settings.add(0.3349); //w_CR
        settings.add(0.6011); //CR_min
        settings.add(1.1673); //CR_max
        settings.add(64); //Q
        settings.add(0.6288); //alfa
        settings.add(422); //H
        settings.add(1.9805); //w_ext
        settings.add(0.0956); //p
        ret.add(settings);
        
        /**
         * f2
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 2;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(387); //NPinit
        settings.add(6); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.3116); //Finit
        settings.add(0.8019); //ERinit
        settings.add(0.2240); //CRinit
        settings.add(0.5178); //w_F
        settings.add(0.7163); //w_ER
        settings.add(0.2290); //w_CR
        settings.add(0.9567); //CR_min
        settings.add(1.1568); //CR_max
        settings.add(920); //Q
        settings.add(0.2551); //alfa
        settings.add(695); //H
        settings.add(2.0466); //w_ext
        settings.add(0.3274); //p
        ret.add(settings);
        
        /**
         * f3
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 3;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(164); //NPinit
        settings.add(9); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.4085); //Finit
        settings.add(0.3172); //ERinit
        settings.add(0.1980); //CRinit
        settings.add(0.4899); //w_F
        settings.add(0.1717); //w_ER
        settings.add(0.9933); //w_CR
        settings.add(0.0100); //CR_min
        settings.add(0.9348); //CR_max
        settings.add(151); //Q
        settings.add(0.1720); //alfa
        settings.add(431); //H
        settings.add(1.7238); //w_ext
        settings.add(0.2230); //p
        ret.add(settings);
        
        /**
         * f4
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 4;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(484); //NPinit
        settings.add(151); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.2915); //Finit
        settings.add(0.6455); //ERinit
        settings.add(0.3603); //CRinit
        settings.add(0.0312); //w_F
        settings.add(0.3067); //w_ER
        settings.add(0.2979); //w_CR
        settings.add(0.5660); //CR_min
        settings.add(0.9555); //CR_max
        settings.add(13); //Q
        settings.add(0.2371); //alfa
        settings.add(61); //H
        settings.add(1.5365); //w_ext
        settings.add(0.1907); //p
        ret.add(settings);
        
        /**
         * f5
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 5;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(92); //NPinit
        settings.add(9); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.3045); //Finit
        settings.add(0.6024); //ERinit
        settings.add(0.8335); //CRinit
        settings.add(0.9708); //w_F
        settings.add(0.7593); //w_ER
        settings.add(0.0422); //w_CR
        settings.add(0.0867); //CR_min
        settings.add(0.3422); //CR_max
        settings.add(433); //Q
        settings.add(0.8216); //alfa
        settings.add(335); //H
        settings.add(2.4490); //w_ext
        settings.add(0.1739); //p
        ret.add(settings);
        
        /**
         * f6
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 6;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(78); //NPinit
        settings.add(4); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.2307); //Finit
        settings.add(0.9822); //ERinit
        settings.add(0.6595); //CRinit
        settings.add(0.7779); //w_F
        settings.add(0.1636); //w_ER
        settings.add(0.8292); //w_CR
        settings.add(0.0029); //CR_min
        settings.add(0.3792); //CR_max
        settings.add(634); //Q
        settings.add(0.1777); //alfa
        settings.add(13); //H
        settings.add(1.6688); //w_ext
        settings.add(0.3519); //p
        ret.add(settings);
        
        /**
         * f7
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 7;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(54); //NPinit
        settings.add(13); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.1648); //Finit
        settings.add(0.3041); //ERinit
        settings.add(0.3418); //CRinit
        settings.add(0.4402); //w_F
        settings.add(0.1873); //w_ER
        settings.add(0.7929); //w_CR
        settings.add(0.1518); //CR_min
        settings.add(0.3229); //CR_max
        settings.add(231); //Q
        settings.add(0.3434); //alfa
        settings.add(956); //H
        settings.add(1.4542); //w_ext
        settings.add(0.0447); //p
        ret.add(settings);
        
        /**
         * f8
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 8;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(52); //NPinit
        settings.add(4); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.4923); //Finit
        settings.add(0.9161); //ERinit
        settings.add(0.1788); //CRinit
        settings.add(0.8679); //w_F
        settings.add(0.1949); //w_ER
        settings.add(0.3458); //w_CR
        settings.add(0.1026); //CR_min
        settings.add(0.2619); //CR_max
        settings.add(301); //Q
        settings.add(0.7266); //alfa
        settings.add(186); //H
        settings.add(1.8254); //w_ext
        settings.add(0.0752); //p
        ret.add(settings);

        /**
         * f9
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 9;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(158); //NPinit
        settings.add(84); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.3559); //Finit
        settings.add(0.7073); //ERinit
        settings.add(0.7627); //CRinit
        settings.add(0.0250); //w_F
        settings.add(0.8203); //w_ER
        settings.add(0.1223); //w_CR
        settings.add(0.4171); //CR_min
        settings.add(1.3401); //CR_max
        settings.add(428); //Q
        settings.add(0.6565); //alfa
        settings.add(55); //H
        settings.add(2.5645); //w_ext
        settings.add(0.0213); //p
        ret.add(settings);
        
        /**
         * f10
         * dimension, NPinit, NPmin, MAXFES, tf, Finit, ERinit, CRinit, w_F, w_ER, w_CR, CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 10;
        settings = new ArrayList<>();
        settings.add(dimension);
        settings.add(113);
        settings.add(72);
        settings.add(10000*dimension);
        settings.add(new Cec2015(dimension, funcNumber));
        settings.add(0.3709);
        settings.add(0.1387);
        settings.add(0.9553);
        settings.add(0.8553);
        settings.add(0.0325);
        settings.add(0.2581);
        settings.add(0.9450);
        settings.add(0.9899);
        settings.add(31);
        settings.add(0.1957);
        settings.add(730);
        settings.add(1.4141);
        settings.add(0.4105);
        ret.add(settings);
        
        /**
         * f11
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 11;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(349); //NPinit
        settings.add(80); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.1738); //Finit
        settings.add(0.3240); //ERinit
        settings.add(0.5553); //CRinit
        settings.add(0.0422); //w_F
        settings.add(0.8030); //w_ER
        settings.add(0.3177); //w_CR
        settings.add(0.4862); //CR_min
        settings.add(1.2866); //CR_max
        settings.add(41); //Q
        settings.add(0.6467); //alfa
        settings.add(60); //H
        settings.add(1.0355); //w_ext
        settings.add(0.0118); //p
        ret.add(settings);
        
        /**
         * f12
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 12;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(227); //NPinit
        settings.add(68); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.7078); //Finit
        settings.add(0.9078); //ERinit
        settings.add(0.7912); //CRinit
        settings.add(0.1757); //w_F
        settings.add(0.9077); //w_ER
        settings.add(0.2641); //w_CR
        settings.add(0.0269); //CR_min
        settings.add(0.8352); //CR_max
        settings.add(9); //Q
        settings.add(0.3753); //alfa
        settings.add(296); //H
        settings.add(1.1197); //w_ext
        settings.add(0.0530); //p
        ret.add(settings);
        
        /**
         * f13
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 13;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(76); //NPinit
        settings.add(31); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.5417); //Finit
        settings.add(0.2411); //ERinit
        settings.add(0.8090); //CRinit
        settings.add(0.5201); //w_F
        settings.add(0.0811); //w_ER
        settings.add(0.3488); //w_CR
        settings.add(0.0811); //CR_min
        settings.add(0.1447); //CR_max
        settings.add(115); //Q
        settings.add(0.8429); //alfa
        settings.add(405); //H
        settings.add(6.0485); //w_ext
        settings.add(0.0399); //p
        ret.add(settings);
        
        /**
         * f14
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 14;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(57); //NPinit
        settings.add(6); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.1492); //Finit
        settings.add(0.5587); //ERinit
        settings.add(0.9853); //CRinit
        settings.add(0.5410); //w_F
        settings.add(0.6262); //w_ER
        settings.add(0.7864); //w_CR
        settings.add(0.0087); //CR_min
        settings.add(0.352); //CR_max
        settings.add(748); //Q
        settings.add(0.3513); //alfa
        settings.add(112); //H
        settings.add(2.5510); //w_ext
        settings.add(0.9973); //p
        ret.add(settings);
        
        /**
         * f15
         * dimension, NPinit, NPmin, MAXFES, tf, 
         * Finit, ERinit, CRinit, w_F, w_ER, w_CR, 
         * CR_min, CR_max, Q, alfa, H, w_ext, p
         */
        funcNumber = 15;
        settings = new ArrayList<>();
        settings.add(dimension); //dim
        settings.add(148); //NPinit
        settings.add(147); //NPmin
        settings.add(10000*dimension); //MAXFES
        settings.add(new Cec2015(dimension, funcNumber)); //TF
        settings.add(0.3365); //Finit
        settings.add(0.0670); //ERinit
        settings.add(0.8308); //CRinit
        settings.add(0.0679); //w_F
        settings.add(0.4346); //w_ER
        settings.add(0.0037); //w_CR
        settings.add(0.3976); //CR_min
        settings.add(1.1649); //CR_max
        settings.add(41); //Q
        settings.add(0.1143); //alfa
        settings.add(139); //H
        settings.add(4.1768); //w_ext
        settings.add(0.2089); //p
        ret.add(settings);
        
        return ret;
        
    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        
        int dimension = 10;
        TestFunction tf;

        EigSpsLShaDE shade;

        int runs = 51;
        List<List<Object>> list = setUpForCec2015(dimension);
        
        double[] bestArray;
        PrintWriter writer, sol_writer,res_writer;
        double best,worst,median,mean,std;
        String home_dir = "C:\\Users\\wiki\\CEC2015-LShade/";

        res_writer = new PrintWriter(home_dir + "results.txt", "UTF-8");
        
        res_writer.print("{");
        
        for (int funcNumber = 1; funcNumber < 16; funcNumber++){

            tf = new Cec2015(dimension, funcNumber);
            bestArray = new double[runs];
            
            for (int k = 0; k < runs; k++) {

                shade = new EigSpsLShaDE(list.get(funcNumber-1));
                shade.run();

                writer = new PrintWriter(home_dir + funcNumber + "-" + k + ".txt", "UTF-8");

                writer.print("{");

                for (int i = 0; i < shade.getBestHistory().size(); i++) {

                    writer.print(String.format(Locale.US, "%.10f", shade.getBestHistory().get(i).fitness));

                    if (i != shade.getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

                bestArray[k] = shade.getBest().fitness - tf.optimum();

            }
            
            best = DoubleStream.of(bestArray).min().getAsDouble();
            worst = DoubleStream.of(bestArray).max().getAsDouble();
            median = new Median().evaluate(bestArray);
            mean = new Mean().evaluate(bestArray);
            std = new StandardDeviation().evaluate(bestArray);

            sol_writer = new PrintWriter(home_dir + "results_" + funcNumber + ".txt", "UTF-8");
            
            sol_writer.print("{");
            sol_writer.print(funcNumber);
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.10f", best));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.10f", worst));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.10f", median));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.10f", mean));
            sol_writer.print(",");
            sol_writer.print(String.format(Locale.US, "%.10f", std));
            sol_writer.print("}");
            
            sol_writer.close();

            System.out.println("CEC2015 - f" + funcNumber);
            System.out.println("=================================");
            System.out.println("Best: " + best);
            System.out.println("Worst: " + worst);
            System.out.println("Median: " + median);
            System.out.println("Mean: " + mean);
            System.out.println("Std: " + std);
            System.out.println("=================================");
            
            res_writer.print("{");
            res_writer.print(funcNumber);
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", best));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", worst));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", median));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", mean));
            res_writer.print(",");
            res_writer.print(String.format(Locale.US, "%.10f", std));
            res_writer.print("}");
        
            if(funcNumber < 15){
               res_writer.print(",");
            }
            
        }

        res_writer.print("}");
        
        res_writer.close();
        
    }
    
}
