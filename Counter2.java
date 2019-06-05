/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package marc;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author k2
 */
public class Counter2 {
    private Map<String,Integer> ts = new TreeMap<>();
    public void inc(String t) {
        inc(t,1);
    }
    public void inc(String t, int sum) {
        Integer i = ts.get(t);
        if(i==null) {
            ts.put(t, sum);
        } else ts.put(t,i+sum);
    }
    public int get(String t) {
        Integer i = ts.get(t);
        if(i==null) return 0;
        return ts.get(t);
    }
    public void set(String t, int value) {
        ts.put(t,value);
    }
    public void max(String t, int value) {
        Integer i = ts.get(t);
        if(i==null || i<value) ts.put(t,value);
    }
    public void print() {
        System.out.println();
        System.out.println("Counter2:");
        for(Map.Entry<String,Integer> e:ts.entrySet())
            System.out.println(e.getKey()+" = "+e.getValue());
        System.out.println();
    }
}
