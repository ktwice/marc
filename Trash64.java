package marc;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * сдерживатель роста mfn
 * @author k2
 */
public class Trash64 implements Closeable{
/**
 *
 */    
    private RandomAccessFile raf = null;
/**
 * значение v920 для корзины mfn
 */    
    static private final String v920trash = "TRASH64";
/**
 * tags, представляющий запись из корзины
 */    
    static final private Map<Integer,List<Field>> trashtags = new TreeMap<>();
    static { Field.tagsAdd(trashtags,920,Field.create(v920trash)); }
/**
 * зацепляем xrf
 */    
    private XRF64 xrf;
/**
 * карта индексов значений v920 в mfn2
 */    
    private Map<String,Integer> index920;
/**
 * сборник mfn для значения v920
 */    
    private int[][] mfn2;
/**
 * метка/ключ сбора массива was (было)
 */    
    private String syear4 = null;
/**
 * старые mfn для перебора слиянием
 */    
    private Map<String, Integer> was = null;
/**
 * обратный отсчет для was
 */    
    private int wasCounter = 0;
/**
 * корзина mfn
 */    
    private int[] trash = null;
/**
 * обратный отсчет для trash
 */    
    private int trashCounter = 0;
/**
 * mfn-сборник для перемещения в корзину
 */    
    private List<Integer> trash4 = new ArrayList<>();
/**
 * tags-сборник для новых записей
 */    
    private List<Map<Integer,List<Field>>> add4 = new ArrayList<>();
/**
 * итератор по was
 */    
    private Iterator<Entry<String,Integer>> iter = null;
/**
 * текущее значение итератора
 */    
    private Entry<String,Integer> entr = null;
    private Counter2 c2 = null;
    public Counter2 getCounter2() {return c2;}
    private boolean log(Map<Integer,List<Field>> tags)
            throws FileNotFoundException, IOException {
        final byte[] lfcr = new byte[] {13,10};
        final Charset charset = Charset.forName("windows-1251");
        final String fname = "Trash64.log";
        final boolean b = (raf==null);
        raf = new RandomAccessFile(fname,"rw");
        if(b)   raf.setLength(0);
        else    raf.seek(raf.length());
        for(String s:Field.txt2(tags)) {
            raf.write(s.getBytes(charset));
            raf.write(lfcr);
        }
        raf.close();
        return true;
    }
/**
 * перебирает все новые записи готовые для слияния 
 * @param tags 
 */    
    public void write(Map<Integer,List<Field>> tags) throws IOException {
//        if(log(tags)) return;
        c2.inc("write-next");
        if(entr != null) {
            String snew = tags.get(60).get(0).getSub('b');
            do {
                String swas = entr.getKey();
                int comp = snew.compareTo(swas);
                if(comp < 0) break;
                int mfn = entr.getValue();
                xrf.next(mfn);
                entr = (iter.hasNext()) ? iter.next() : null;
                --wasCounter;
                if(comp == 0){
                    Map<Integer,List<Field>> wastags = xrf.read(mfn);
                    if(!Field.tagsDeepEquals(tags, wastags)) {
                        xrf.write(tags, mfn);
                        c2.inc("not the same");
//                        System.out.println("not the same mfn="+mfn);
                    }else {
                        c2.inc("the same");
                    }
                    return;
                } 
                if(!add4.isEmpty())  {
                    xrf.write(add4.remove(0), mfn);
                    c2.inc("update other");
                } else {
                    trash4.add(mfn);
                    c2.max("trash4 max",trash4.size());
                }
            } while(entr != null);
        }
        //forAdd(tags);
        if(!trash4.isEmpty()) {
            int mfn = trash4.remove(0);
            xrf.next(mfn);
            xrf.write(tags, mfn);
            c2.inc("update trash4");
        } else if(trashCounter > 0) {
            int mfn = trash[--trashCounter];
            xrf.next(mfn);
            xrf.write(tags, mfn);
            c2.inc("update trash");
        } else if(wasCounter <= add4.size()) {
            xrf.write(tags);
            c2.inc("add");
        } else {
            add4.add(tags);
            c2.max("add4 max",add4.size());
        }
    }
    private void write(int mfn) throws IOException {
        xrf.next(mfn);
        if(add4.isEmpty()) {
            xrf.write(trashtags, mfn);
            c2.inc("close: update to trash");
        } else {
            xrf.write(add4.remove(0), mfn);
            c2.inc("close: update");
        }
    }
/**
 * доделывает trash4, add4 и was
 */    
    @Override
    public void close() throws IOException {
        while(entr != null) {
            int mfn = entr.getValue();
            entr = (iter.hasNext()) ? iter.next() : null;
//            --wassize;
            write(mfn);
        }
        for(int mfn:trash4) {
            write(mfn);
        }
        for(Map<Integer,List<Field>> tags:add4) {
            xrf.write(tags);
            c2.inc("close: add");
        }
        xrf.close();
        c2.print();
    }
/**
 * включает KO2 на затирание новыми записями 
 * (кроме тех, у кого четко указан другой год)
 * @param syear4 
 */    
    public void setYear(String syear4) {
        this.syear4 = syear4;
    }
/**
 * возвращает ключ совмещения, если указан наш год
 * null, если указан другой год
 * пустую строку во всех остальных случаях (их в корзину)
 * @param tags
 * @return ключ совмещения
 */    
    private String KO2key(Map<Integer,List<Field>> tags) {
        List<Field> fs = tags.get(60);
        if(fs==null) return "";
        Field f = fs.get(0);
        if(f==null) return "";
        String syear = f.getSub('a');
        if(syear==null) return "";
        if(syear.isEmpty()) return "";
        if(!syear.startsWith(syear4)) return null;
        String skey = f.getSub('b');
        return skey==null ? "" : skey.trim().toUpperCase();
    }
/**
 * массив mfn 
 * @param v920 значение v920
 * @return not null
 */
    public int[] get(String v920) {
        Integer index = index920.get(v920);
        if(index==null) return new int[0];
        return mfn2[index];
    }
/**
 * зачитать указанный mfn
 * @param mfn
 * @return tags указанного mfn
 * @throws IOException 
 */    
    public Map<Integer,List<Field>> read(int mfn) throws IOException {
        xrf.next(mfn);
        return xrf.read(mfn);
    }
/**
 * инициализируем отпечаток текущего состояния
 * @param name xrf-файл
 * @throws FileNotFoundException
 * @throws IOException 
 */    
    public void open(String name, String mode) throws FileNotFoundException, IOException {
        final int STEP = 4096;
        c2 = new Counter2();
        index920 = new TreeMap<>();
        mfn2 = null;
        int[] mfn1;
        int[] mfnsize1 = null;
        xrf = XRF64.open(name, mode);
        int nxtmfn = xrf.getMST().nxtmfn();
        int mfn=0;
        while(++mfn < nxtmfn) {
            if(xrf.next() > 0) continue;
            Map<Integer,List<Field>> tags = xrf.read(mfn);
            String v920 = Field.getTagsSub(tags, 920, '\u0000');
            if(v920==null) v920 = "";
            else v920 = v920.trim();
            if(syear4!=null) { // назначен год сбора KO2 для переписывания
                if(v920.equals("KO2")) { // их будем собирать в was
                    String skey = KO2key(tags);
                    if(skey!=null) {
                        if(was == null) was = new TreeMap<>();
                        if(was.containsKey(skey)) trash4.add(mfn);
                        else was.put(skey, mfn);
                    }
                }
            }
            int index2;
            Integer index = index920.get(v920);
            if(index==null) {
                if(mfn2 == null) {
                    index = 0;
                    mfn2 = new int[1][];
                    mfnsize1 = new int[1];
                } else {
                    index = mfn2.length;
                    mfn2 = Arrays.copyOf(mfn2, 1+index);
                    mfnsize1 = Arrays.copyOf(mfnsize1, 1+index);
                }
                index920.put(v920, index);
                mfn2[index] = mfn1 = new int[STEP];
                index2 = 0;
            } else {
                mfn1 = mfn2[index];
                index2 = mfnsize1[index];
                if(index2 == mfn1.length)
                    mfn2[index] = mfn1 = Arrays.copyOf(mfn1, index2+STEP);
            }
            mfnsize1[index]++;
            mfn1[index2] = mfn;
        }
        if(mfnsize1!=null) {
            for(int i=0; i<mfnsize1.length; i++) 
                mfn2[i] = Arrays.copyOf(mfn2[i], mfnsize1[i]);
            if(was != null) {
                wasCounter = was.size();
                c2.inc("was", wasCounter);
                iter = was.entrySet().iterator();
                if(iter.hasNext()) entr = iter.next();
            }
            Integer index = index920.get(v920trash);
            if(index != null) {
                trash = mfn2[index];
                trashCounter = trash.length;
                c2.inc("trash", trashCounter);
            }
        }
        if(mfn2==null)
            System.out.println("map920 is null.");
        else {
            System.out.println("map920:");
            for(Map.Entry<String,Integer> e:index920.entrySet()) {
                System.out.println(e.getKey()+" = " + mfn2[e.getValue()].length + ".");
            }
        }
   }
}
