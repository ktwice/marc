/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package marc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author ktwice
 */
public class Disc {
    private Map<String,List<Integer>> map = new TreeMap<>();
    private XRF64 xrf;
    private String[] keys;

    public void open(String name)
            throws FileNotFoundException, IOException {
        xrf = XRF64.open(name + "vuz\\vuz", "r");
        MST64 mst = xrf.getMST();
        int nxtmfn = mst.nxtmfn();
        int mfn = 0;
        while (++mfn < nxtmfn) {
            xrf.next();
            int xrfFlags = xrf.getIntBuffer().get(Rec64.XRF_FLAGS);
            if (xrfFlags != 0 && xrfFlags != Rec64.BIT_NOTACT_REC) continue;
            byte[] bytes = mst.read(xrf.getPos(),mfn);
            Map<Integer,List<Field>> tags = new Rec64(bytes).newTags();
            List<Field> fields = tags.get(920);
            if (fields == null) continue;
            if (!fields.get(0).get().equals("DISC")) continue;
            fields = tags.get(3);
            if (fields == null) {
                System.out.print("DISC="+mfn+" нет поля 3");
                continue;
            }
            String s = fields.get(0).getSubs().get('A');
            if (s == null) {
                System.out.print("DISC="+mfn+" нет подполя 3^D");
                continue;
            }
            s = s.trim().toUpperCase();
            List<Integer> mfnList = map.get(s);
            if (mfnList == null) {
                mfnList = new ArrayList<>();
                map.put(s, mfnList);
            }
            mfnList.add(mfn);
        }
        keys = map.keySet().toArray(new String[0]);
    }

    public void print() {
        System.out.print("\n Disc.map.size= "+map.size());
        for (String s: map.keySet())
            System.out.print("\n Disc="+s);
    }

    public void close() throws IOException { xrf.close(); }

    public String getSimilar(String s) {
        int i = Arrays.binarySearch(keys, s);
        if (i>=0)return null;
        i = -i-1; // insertionpoint
        float w1 = 0;
        float w2 = 0;
        if (i<keys.length) w1 = theSameWordsStarts(s,keys[i]);
        if (i>0) w2 = theSameWordsStarts(s,keys[i-1]);
        if (w1>w2) return keys[i];
        if (w2>0) return keys[i-1];
        return null;
    }

    private float theSameWordsStarts(String s1, String s2) {
        int min = Math.min(s1.length(), s2.length());
        int wcount = 0;
        char ch;
        for(int i=0; i<min; ++i) {
            ch = s1.charAt(i);
            if(ch != s2.charAt(i)) return wcount;
            if(ch == ' ') ++wcount;
        }
        if (s1.length()>min) return wcount + s1.charAt(min)==' '?0.5f:0.0f;
        if (s2.length()>min) return wcount + s2.charAt(min)==' '?0.5f:0.0f;
        return wcount+1;
    }

    private int theSameStarts(String s1, String s2) {
        int max = Math.max(s1.length(), s2.length());
        for(int i=0; i<max; ++i)
            if(s1.charAt(i) != s2.charAt(i))
                return i;
        return max;
    }

    public List<Integer> getMfnList(String v691d) {
        return map.get(v691d);
    }

    public void add691mfn(List<Field> fields691, int mfn)
            throws IOException {
        xrf.next(mfn);
        Map<Integer,List<Field>> tags = xrf.read(mfn);
        List<Field> fields83 = tags.get(83);
        if (fields83 == null) return;
        String s3 = Field.getTagsSub(tags, 3, '0');
        String v691d = Field.getTagsSub(tags, 3, 'A');
        String s4 = Field.getTagsSub(tags, 4, '\u0000');
        String s5 = Field.getTagsSub(tags, 5, '\u0000');
        String s6 = Field.getTagsSub(tags, 6, '\u0000');
        for(Field field83:fields83) {
            Field newfield = new Field();
            newfield.setString("");
            fields691.add(newfield);
            newfield.setSub('A',field83.getSubs().get('A'));
            newfield.setSub('B',s5);
            newfield.setSub('C',field83.getSubs().get('C'));
            newfield.setSub('D',v691d);
            newfield.setSub('F',field83.getSubs().get('F'));
            newfield.setSub('I',s3);
            newfield.setSub('H',field83.getSubs().get('H'));
            newfield.setSub('N',field83.getSubs().get('N'));
            newfield.setSub('S',s4);
            newfield.setSub('K',s6);
            newfield.setSub('V',field83.getSubs().get('V'));
            newfield.setSub('O',field83.getSubs().get('O'));
            newfield.setSub('W',field83.getSubs().get('W'));
        }
    }

}
