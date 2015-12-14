package model.ap;

import static java.lang.Double.NaN;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import model.ap.objects.AP_Abs;
import model.ap.objects.AP_Const;
import model.ap.objects.AP_Cos;
import model.ap.objects.AP_Cube;
import model.ap.objects.AP_Div;
import model.ap.objects.AP_Euler;
import model.ap.objects.AP_Exp;
import model.ap.objects.AP_Ln;
import model.ap.objects.AP_MinusOne;
import model.ap.objects.AP_Mod;
import model.ap.objects.AP_Multiply;
import model.ap.objects.AP_One;
import model.ap.objects.AP_Pi;
import model.ap.objects.AP_Plus;
import model.ap.objects.AP_Quad;
import model.ap.objects.AP_Sin;
import model.ap.objects.AP_Sqrt;
import model.ap.objects.AP_Sub;
import model.ap.objects.AP_Tan;
import model.ap.objects.AP_Zero;
import model.ap.objects.AP_aTOb;
import model.ap.objects.AP_object;
import model.ap.objects.AP_x;

/**
 *
 * @author adam on 09/12/2015
 */
public class AP {

    List<AP_object> GFSall;
    List<AP_object> GFS_0;
    List<AP_object> GFS_1;
    List<AP_object> GFS_2;
    int D;
    
    public String equation;

    public AP() {

        /**
         * There should be initialization of all GFS sets
         */
        this.createGFS();

    }

    /**
     * Creates GFSs
     */
    private void createGFS(){
        
        this.GFSall = new ArrayList<>();
        this.GFS_2 = new ArrayList<>();
        this.GFS_1 = new ArrayList<>();
        this.GFS_0 = new ArrayList<>();
        
        this.GFSall.add(new AP_Plus());
        this.GFSall.add(new AP_Sub());
        this.GFSall.add(new AP_Multiply());
        this.GFSall.add(new AP_Div());
        this.GFSall.add(new AP_Mod());
        this.GFSall.add(new AP_aTOb());
        
        this.GFSall.add(new AP_Sin());
        this.GFSall.add(new AP_Cos());
        this.GFSall.add(new AP_Tan());
        this.GFSall.add(new AP_Abs());
        this.GFSall.add(new AP_Exp());
        this.GFSall.add(new AP_Quad());
        this.GFSall.add(new AP_Sqrt());
        this.GFSall.add(new AP_Cube());
        this.GFSall.add(new AP_Ln());

        this.GFSall.add(new AP_x());
        this.GFSall.add(new AP_One());
        this.GFSall.add(new AP_Zero());
        this.GFSall.add(new AP_MinusOne());
        this.GFSall.add(new AP_Const());
        this.GFSall.add(new AP_Pi());
        this.GFSall.add(new AP_Euler());
 
        for(AP_object ob : this.GFSall){
            
            switch(ob.argCount()){
                case 2:
                    this.GFS_2.add(ob);
                    break;
                case 1:
                    this.GFS_2.add(ob);
                    this.GFS_1.add(ob);
                    break;
                case 0:
                    this.GFS_2.add(ob);
                    this.GFS_1.add(ob);
                    this.GFS_0.add(ob);
                    break;
                default:
                    break;
            }
            
        }
        
    }
    
    /**
     * Discrete Set Handling method
     *
     * @param vector
     * @param x
     * @return
     */
    public double dsh(Integer[] vector, double x) {

        this.D = vector.length;

        Integer[] gfs_code = getGFScode(vector);
        Queue<Double> queue = new LinkedList<>();
        Queue<String> strQueue = new LinkedList<>();
        AP_object cur_object;
        List<Double> array;
        List<String> strArray;
        StringBuilder eq = new StringBuilder("");
        double tmp_result;

        for (int i = gfs_code.length - 1; i > -1; i--) {

            switch (gfs_code[i]) {
                case -1:
                    cur_object = null;
                    break;
                case 0:
                    cur_object = this.GFS_0.get(vector[i] % this.GFS_0.size());
                    break;
                case 1:
                    cur_object = this.GFS_1.get(vector[i] % this.GFS_1.size());
                    break;
                case 2:
                    cur_object = this.GFS_2.get(vector[i] % this.GFS_2.size());
                    break;
                default:
                    cur_object = this.GFSall.get(vector[i] % this.GFSall.size());
                    break;
            }

            array = new ArrayList<>();
            strArray = new ArrayList<>();

            if (cur_object != null) {

                switch (cur_object.argCount()) {
                    case 0:
                        array.add(x);
                        strArray.add("x");
                        tmp_result = cur_object.compute(array);
//                        if(tmp_result == 0 || tmp_result == NaN){
//                            tmp_result = 1;
//                        }
                        queue.add(tmp_result);
                        strQueue.add(cur_object.createEq(strArray));
                        break;
                    case 1:
                        array.add(queue.remove());
                        strArray.add(strQueue.remove());
                        tmp_result = cur_object.compute(array);
//                        if(tmp_result == 0 || tmp_result == NaN){
//                            tmp_result = 1;
//                        }
                        queue.add(tmp_result);
                        strQueue.add(cur_object.createEq(strArray));
                        break;
                    case 2:
                        array.add(queue.remove());
                        strArray.add(strQueue.remove());
                        array.add(queue.remove());
                        strArray.add(strQueue.remove());
                        tmp_result = cur_object.compute(array);
//                        if(tmp_result == 0 || tmp_result == NaN){
//                            tmp_result = 1;
//                        }
                        queue.add(tmp_result); 
                        strQueue.add(cur_object.createEq(strArray));
                        break;
                    default:
                        array.add(queue.remove());
                        strArray.add(strQueue.remove());
                        array.add(queue.remove());
                        strArray.add(strQueue.remove());
                        array.add(queue.remove());
                        strArray.add(strQueue.remove());
                        tmp_result = cur_object.compute(array);
//                        if(tmp_result == 0 || tmp_result == NaN){
//                            tmp_result = 1;
//                        }
                        queue.add(tmp_result);
                        strQueue.add(cur_object.createEq(strArray));
                        break;
                }
                
//                eq.insert(0, cur_object.toString() + " ");
            }

        }
        
//        equation = eq.toString();
        equation = strQueue.remove();

        return queue.remove();

    }

    /**
     * Helper method which produces array of values. Those values are indexes of
     * GFS to look in. -1 is no GFS at all. 3 is GFSall
     *
     * @param vector
     * @return
     */
    public Integer[] getGFScode(Integer[] vector) {

        this.D = vector.length;

        int terminals_needed = 1;
        int to_end = D - 1;
        int cur_arg_count = 0;
        Integer[] gfs_code = new Integer[D];
        for (int i = 0; i < D; i++) {
            gfs_code[i] = -1;
        }

        for (int i = 0; i < vector.length; i++) {

            switch (to_end) {
                case 0:
                    cur_arg_count = this.GFS_0.get(vector[i] % this.GFS_0.size()).argCount();
                    gfs_code[i] = 0;
                    break;
                case 1:
                    cur_arg_count = this.GFS_1.get(vector[i] % this.GFS_1.size()).argCount();
                    gfs_code[i] = 1;
                    break;
                case 2:
                    cur_arg_count = this.GFS_2.get(vector[i] % this.GFS_2.size()).argCount();
                    gfs_code[i] = 2;
                    break;
                default:
                    cur_arg_count = this.GFSall.get(vector[i] % this.GFSall.size()).argCount();
                    gfs_code[i] = 3;
                    break;
            }

            terminals_needed += (cur_arg_count - 1);
            to_end -= cur_arg_count;

            if (terminals_needed == 0) {
                break;
            }

        }

        return gfs_code;

    }
    
    /**
     * 
     * @return 
     */
    public String getEquation(){
        return equation;
    }
    
    /**
     * 
     * @return 
     */
    public int getGFSsize(){
        return this.GFSall.size();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

//        AP ap = new AP();
//        Integer[] vector = new Integer[]{1,2,13,11,11,14,14,14,20,20,20,20,20,20,20,20,20,20,20,20};
//        double x = 1;
//
////        for (int i = 0; i < vector.length; i++) {
////            vector[i] = RandomUtil.nextInt(ap.getGFSsize());
////        }
//
//        Integer[] gfscode = ap.getGFScode(vector);
//
//        System.out.println("Input: " + Arrays.toString(vector));
//
//        System.out.println("GFS code: " + Arrays.toString(gfscode));
//
//        System.out.println("Result: " + ap.dsh(vector, x));
//        
//        System.out.println("Equation: \n" + ap.getEquation());

        AP_Ln log = new AP_Ln();
        List<Double> list = new ArrayList<>();
        list.add(-1.0);
        
        System.out.println(log.compute(list));

    }

}
