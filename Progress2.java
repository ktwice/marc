/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package marc;

/**
 *
 * @author marina
 */
public class Progress2 {
    private final int step=10;
    private int max;
    private int offset;
    private int next;
    public Progress2(int max,String s) {
        this.max=max;
        System.out.print("\n"+s+"["+max+"]");
        offset=0;
        next=step;
    }
    public void inc(int i) {
        offset+=i;
        int mark = (int)(offset*100/max);
        if(mark<next) return;
        while(mark>=next) next+=step;
        System.out.print(""+(next-step)+"%");
        if(offset==max) System.out.println();
    }
    public void inc() {inc(1);}
}
