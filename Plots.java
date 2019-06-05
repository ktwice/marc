/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package marc;

import java.util.ArrayList;
import java.util.List;

/**
 * фильтр на неотрицательные числа
 * @author k2
 */
public class Plots {
    List<int[]> list = new ArrayList<int[]>();
    public Plots() {}
    public boolean isEmpty() {return list.isEmpty();}
/**
 * конструктор
 * @param texts фильтр как набор диапазонов(с тире) через запятую(плюс или точку с запятой)
 *
 */
    public Plots(String texts) {
        if(texts == null) return;
        if(texts.isEmpty()) return;
        for(String text:texts.split(","))
            add(text);
    }

    public Plots(String[] texts) {
        if(texts == null) return;
        for(String text:texts)
            add(text);
    }

    private void add(String text) {
        String[] plot = text.split("-");
        System.out.println(text+" plot.length="+plot.length);
        if(plot.length == 0) {
            list.add(new int[] {-1,-1});
        }
        else if(plot.length == 1) {
            int i = text2int(plot[0]);
            list.add(new int[] {i,i});
        }
        else
            list.add(new int[]{text2int(plot[0]), text2int(plot[1])});
    }

    static private int text2int(String limit) {
        if(limit.isEmpty()) return -1;
        return Integer.parseInt(limit);
    }
/**
 * проверка на соответствие фильтру
 * @param i проверяемое неотрицательное целое
 * @return подходит?
 */
    public boolean check(int i) {
        for(int[] plot:list)
            if(i >= plot[0] && (plot[1] < 0 || i <= plot[1]))
                return true;
        return false;
    }

}
