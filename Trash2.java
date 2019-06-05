package marc;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * оболочка для xrf файла для компактной перезаписи с проверкой изменений
 * @author k2
 */
public class Trash2 implements Closeable{
/**
 * black label   
 */    
    static private final String v920trash = "TRASH64";
/**
 * зацепляем xrf
 */    
    private XRF64 xrf;
/**
 * общий счетчик для update4
 */    
    private int updates = 0;
/**
 * пул перезаписи. карты(v920 и ключ перезаписи) mfn 
 */    
    private final Map<String, Map<String,Integer>> update4 = new TreeMap<>();
/**
 * mfn записей v920=TRASH64
 */    
    private final List<Integer> trash64 = new ArrayList<>();
/**
 * mfn оставшихся записей по v920
 */
    private final Map<String, List<Integer>> rest920 = new TreeMap<>();
    public List<Integer> get920(String v920) { return rest920.get(v920); }
/**
 * mfn записей на удаление
 */    
    private final List<Integer> trash4 = new ArrayList<>();
/**
 * содержимое записей для отложенной записи
 */    
    private final List<Map<Integer,List<Field>>> write4 = new ArrayList<>();
/**
 * счетчик перезаписи
 */    
    private Counter2 c2;
/**
 * расчет ключа перезаписи
 * @param v920 тип записи (содержимое поля v920) 
 * @param tags содержимое записи
 * @return ключ перезаписи
 */    
    private static String key920(String v920, Map<Integer,List<Field>> tags) {
        String s;
        switch(v920){
            case "KO2":
                s = Field.getTagsSub(tags, 60, 'b');
                break;
            default:
                s = Field.getTagsSub(tags, 903, '\u0000');
        }
        return (s==null)?"":s;
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
 * открыть xrf и подготовить к перезаписи
 * @param name имя xrf файла
 * @param a920 массив v920 для полной перезаписи (все как бы удаляем)
 * @return ссылка Closeable на себя
 * @throws FileNotFoundException
 * @throws IOException 
 */    
    public Closeable open(String name, String[] a920)
            throws FileNotFoundException, IOException {
        Arrays.sort(a920);
        c2 = new Counter2();
        xrf = XRF64.open(name, "rw");
        final int nxtmfn = xrf.getMST().nxtmfn();
        int mfn=0;
        while(++mfn < nxtmfn) {
            if(xrf.next() > 0) continue; // пропускаем удаленные
            Map<Integer,List<Field>> tags = xrf.read(mfn);
            String v920 = Field.getTagsSub(tags, 920, '\u0000');
            v920 = (v920==null) ? "" : v920.trim();
            c2.inc(v920);
            if(v920.equals(v920trash)) {
                trash64.add(mfn); 
                continue;
            }
            Map<String,Integer> m920 = update4.get(v920);
            if(m920 == null) {
                if(Arrays.binarySearch(a920, v920) < 0) {
                    List<Integer> list920 = rest920.get(v920);
                    if(list920==null) rest920.put(v920, list920 = new ArrayList<>());
                    list920.add(mfn);
                    continue;
                }
                update4.put(v920, m920 = new TreeMap<>());
            }
            String k920 = key920(v920,tags);
//            System.out.println(v920+" = " + k920 + ".");
            if(m920.containsKey(k920)) { 
                System.out.println("неожиданный дубль ключа" + v920+" = " + k920 + ".");
                trash4.add(mfn);
                c2.inc(v920+" trash4");
                continue;
            }
            ++updates;
            m920.put(k920,mfn);
        }
        return this;
   }
/**
 * перезаписать на старое место с проверкой изменений
 * @param tags
 * @return получилось?
 * @throws IOException 
 */    
    private boolean write920(Map<Integer,List<Field>> tags) throws IOException {
        if(updates == 0) return false;
        String v920 = Field.getTagsSub(tags, 920, '\u0000');
        v920 = (v920==null) ? "" : v920.trim();
        Map<String,Integer> m920 = update4.get(v920);
        if(m920 == null) return false;
        String k920 = key920(v920,tags);
        Integer mfn = m920.remove(k920);
        if(mfn == null) return false;
        if(m920.isEmpty()) update4.remove(v920);
        --updates;
        xrf.next(mfn);
        if(Field.tagsDeepEquals(tags, xrf.read(mfn))) {
            c2.inc(v920+" the same");
            return true;
        }
        c2.inc(v920+" updated");
        xrf.write(tags, mfn);
        return true;
    }
   @Override
    public void close() throws IOException {
        for(Map<String,Integer> m920:update4.values()) // оставшиеся непереписанными
            trash4.addAll(m920.values()); // отправляем в списки на удаление
        for(Map<Integer,List<Field>> tags:write4) // отложенные
            write2(tags); // компактно добавляем
        if(!trash4.isEmpty()) { 
            Map<Integer,List<Field>> trashtags = new TreeMap<>();
            Field.tagsAdd(trashtags,920,Field.create(v920trash)); 
            for(int mfn:trash4) {// списки на удаление
                c2.inc("trashed");
                xrf.next(mfn);
                xrf.write(trashtags, mfn); // записываем черной меткой
            }
        }
        xrf.close();
        c2.print();
    }
/**
 * компактно добавить запись
 * @param tags
 * @throws IOException 
 */    
    private void write2(Map<Integer,List<Field>> tags) throws IOException {
        if(!trash4.isEmpty()) {
            c2.inc("rewrited"); // перезаписать уже ненужную запись
            int mfn = trash4.remove(0);
            xrf.next(mfn);
            xrf.write(tags, mfn);
        } else if(!trash64.isEmpty()) {
            c2.inc("restored"); // перезаписать вместо TRASH64
            int mfn = trash64.remove(0);
            xrf.next(mfn);
            xrf.write(tags, mfn);
        } else {
            c2.inc("new added"); // добавить запись в конец файла
            xrf.write(tags);
        }
    }
/**
 * добавить запись
 * @param tags
 * @throws IOException 
 */    
    public void write(Map<Integer,List<Field>> tags) throws IOException {
        if(!write920(tags)) // попытаться пристроить на старое место в пул перезаписи
            write4.add(tags); // если не получилось, тогда в кэш
        while(updates < write4.size()) // в кэше не держим больше чем в пуле перезаписи
            write2(write4.remove(0)); // его из кэша сбросить
    }
}
