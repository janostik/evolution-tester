package model.ap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import model.ap.objects.AP_Const_static;
import model.ap.objects.AP_Div;
import model.ap.objects.AP_Multiply;
import model.ap.objects.AP_Plus;
import model.ap.objects.AP_Sigmoid;
import model.ap.objects.AP_Sub;
import model.ap.objects.AP_ann_x1;
import model.ap.objects.AP_ann_x2;
import model.ap.objects.AP_ann_x3;
import model.ap.objects.AP_ann_x4;
import model.ap.objects.AP_object;

/**
 *
 * @author adam on 09/12/2015
 */
public class APANN {

    List<AP_object> GFSall;
    List<AP_object> GFS_0;
    List<AP_object> GFS_1;
    List<AP_object> GFS_2;
    int D;
    
    public String equation;

    public APANN() {

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
//        this.GFSall.add(new AP_Div());
//        this.GFSall.add(new AP_Mod());
//        this.GFSall.add(new AP_aTOb());
        
        this.GFSall.add(new AP_Sigmoid());
//        this.GFSall.add(new AP_Sin());
//        this.GFSall.add(new AP_Cos());
//        this.GFSall.add(new AP_Tan());
//        this.GFSall.add(new AP_Abs());
//        this.GFSall.add(new AP_Exp());
//        this.GFSall.add(new AP_Quad());
//        this.GFSall.add(new AP_Sqrt());
//        this.GFSall.add(new AP_Cube());
//        this.GFSall.add(new AP_Ln());

        this.GFSall.add(new AP_ann_x1());
        this.GFSall.add(new AP_ann_x2());
        this.GFSall.add(new AP_ann_x3());
        this.GFSall.add(new AP_ann_x4());
        this.GFSall.add(new AP_Const_static());
//        this.GFSall.add(new AP_One());
//        this.GFSall.add(new AP_Zero());
//        this.GFSall.add(new AP_MinusOne());
//        this.GFSall.add(new AP_Const());
//        this.GFSall.add(new AP_Pi());
//        this.GFSall.add(new AP_Euler());
 
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
     * 
     * @param vector
     * @return 
     */
    public Integer[] discretizeVector(double[] vector) {
        int dim = vector.length;
        Integer[] discrete = new Integer[dim];

        double min = -100, max = 100;
        
        double dindex, diff, delta;
        int  imax = this.GFSall.size() - 1, dis_index;
        diff = (max - min);
        delta = 1.0 /(double) this.GFSall.size();

        for (int i = 0; i < dim; i++) {
            dis_index = 0;
//            tmp = Math.round(vector[i]);
            dindex = (vector[i] - min) / diff;
            
            dindex -= delta;
            while(dindex > 0 && dis_index != imax){
                dindex -= delta;
                dis_index++;
            }
            
            discrete[i] = dis_index;
        }

        return discrete;
    }
    
    /**
     * Discrete Set Handling method
     *
     * @param vector
     * @param x
     * @return
     */
    public double dsh(double[] vector, double[] x) {

        int length_of_ind = vector.length - (int) Math.ceil(vector.length/3.0);
        this.D = length_of_ind;
        double[] half_vector = Arrays.copyOfRange(vector, 0, D);
        Integer[] vec_to_gfs = this.discretizeVector(half_vector);
        double[] other_half = Arrays.copyOfRange(vector, D, vector.length);
        int index_of_const = 0;

        Integer[] gfs_code = getGFScode(vec_to_gfs);
        Queue<Double> queue = new LinkedList<>();
        Queue<String> strQueue = new LinkedList<>();
        AP_object cur_object;
        List<Double> array;
        List<String> strArray;
//        String eq = "";
        double tmp_result;

        for (int i = gfs_code.length - 1; i > -1; i--) {

            switch (gfs_code[i]) {
                case -1:
                    cur_object = null;
                    break;
                case 0:
                    cur_object = this.GFS_0.get(vec_to_gfs[i] % this.GFS_0.size());
                    break;
                case 1:
                    cur_object = this.GFS_1.get(vec_to_gfs[i] % this.GFS_1.size());
                    break;
                case 2:
                    cur_object = this.GFS_2.get(vec_to_gfs[i] % this.GFS_2.size());
                    break;
                default:
                    cur_object = this.GFSall.get(vec_to_gfs[i] % this.GFSall.size());
                    break;
            }

            array = new ArrayList<>();
            strArray = new ArrayList<>();
            int xIndex = -1;

            if (cur_object != null) {

                switch (cur_object.argCount()) {
                    case 0:
                        
                        if(cur_object.getClass() == AP_Const_static.class){
                            array.add(other_half[i % other_half.length]);
                            strArray.add(String.valueOf(other_half[index_of_const % other_half.length]));
                            index_of_const++;
                        }
                        else{
                            
                            if(cur_object.getClass() == AP_ann_x1.class){
                                xIndex = 0;
                            }
                            else if(cur_object.getClass() == AP_ann_x2.class){
                                xIndex = 1;
                            }
                            else if(cur_object.getClass() == AP_ann_x3.class){
                                xIndex = 2;
                            }
                            else {
                                xIndex = 3;
                            }

                            array.add(x[xIndex]);
                            strArray.add(cur_object.toString());
                            
//                            if(cur_object.getClass() == AP_x.class){
//                                array.add(x[0]);
//                                strArray.add("x");
//                            }
//                            else {
//                                array.add(x[1]);
//                                strArray.add("y");
//                            }
                        }
                        tmp_result = cur_object.compute(array);

                        queue.add(tmp_result);
                        strQueue.add(cur_object.createEq(strArray));
                        break;
                    case 1:
                        array.add(queue.remove());
                        strArray.add(strQueue.remove());
                        tmp_result = cur_object.compute(array);

                        queue.add(tmp_result);
                        strQueue.add(cur_object.createEq(strArray));
                        break;
                    case 2:
                        array.add(queue.remove());
                        strArray.add(strQueue.remove());
                        array.add(queue.remove());
                        strArray.add(strQueue.remove());
                        tmp_result = cur_object.compute(array);

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

        APANN ap = new APANN();
        double[] vector = new double[]{-100,100,20,45,50,6,7,1,2,3,4};
        double x = 1, y = 2;

//        for (int i = 0; i < vector.length; i++) {
//            vector[i] = RandomUtil.nextInt(ap.getGFSsize());
//        }

        ap.discretizeVector(vector);

        System.out.println("Input: " + Arrays.toString(ap.discretizeVector(vector)));

//        System.out.println("GFS code: " + Arrays.toString(gfscode));

        System.out.println("Result: " + ap.dsh(vector, new double[]{x,y}));
        
        System.out.println("Equation: \n" + ap.getEquation());

    }

}
