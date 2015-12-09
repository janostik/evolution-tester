package model.ap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import model.ap.objects.AP_Const;
import model.ap.objects.AP_Cos;
import model.ap.objects.AP_Plus;
import model.ap.objects.AP_Sin;
import model.ap.objects.AP_Sub;
import model.ap.objects.AP_object;
import model.ap.objects.AP_t;
import util.RandomUtil;

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

    public AP() {

        /**
         * There should be initialization of all GFS sets
         */
        this.GFSall = new ArrayList<>();
        this.GFSall.add(new AP_Plus());
        this.GFSall.add(new AP_Sub());
        this.GFSall.add(new AP_Sin());
        this.GFSall.add(new AP_Cos());
        this.GFSall.add(new AP_t());
        this.GFSall.add(new AP_Const());

        this.GFS_2 = new ArrayList<>();
        this.GFS_2.add(new AP_Plus());
        this.GFS_2.add(new AP_Sub());
        this.GFS_2.add(new AP_Sin());
        this.GFS_2.add(new AP_Cos());
        this.GFS_2.add(new AP_t());
        this.GFS_2.add(new AP_Const());

        this.GFS_1 = new ArrayList<>();
        this.GFS_1.add(new AP_Sin());
        this.GFS_1.add(new AP_Cos());
        this.GFS_1.add(new AP_t());
        this.GFS_1.add(new AP_Const());

        this.GFS_0 = new ArrayList<>();
        this.GFS_0.add(new AP_t());
        this.GFS_0.add(new AP_Const());

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
        AP_object cur_object;
        List<Double> array;

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

            if (cur_object != null) {

                switch (cur_object.argCount()) {
                    case 0:
                        array.add(x);
                        queue.add(cur_object.compute(array));
                        break;
                    case 1:
                        array.add(queue.remove());
                        queue.add(cur_object.compute(array));
                        break;
                    case 2:
                        array.add(queue.remove());
                        array.add(queue.remove());
                        queue.add(cur_object.compute(array));
                        break;
                    default:
                        array.add(queue.remove());
                        array.add(queue.remove());
                        array.add(queue.remove());
                        queue.add(cur_object.compute(array));
                        break;
                }
            }

        }

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
    
    public String getEquation(Integer[] vector){
        
        this.D = vector.length;

        Integer[] gfs_code = getGFScode(vector);
        AP_object cur_object;
        String out = "";

        for (int i = 0; i < gfs_code.length; i++) {

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

            if (cur_object != null) {
                out += cur_object.toString() + "\n";
            }

        }

        return out;
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        AP ap = new AP();
        Integer[] vector = new Integer[4];
        double x = 1;

        for (int i = 0; i < vector.length; i++) {
            vector[i] = RandomUtil.nextInt(6);
        }

        Integer[] gfscode = ap.getGFScode(vector);

        System.out.println("Input: " + Arrays.toString(vector));

        System.out.println("GFS code: " + Arrays.toString(gfscode));

        System.out.println("Result: " + ap.dsh(vector, x));
        
        System.out.println("Equation: \n" + ap.getEquation(vector));

    }

}
