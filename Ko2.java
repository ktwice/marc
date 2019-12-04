package marc;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
//import marc.Ko2.Sem2.Count2;
//import marc.XLines;
/**
 *
 * @author ktwice
 */
public class Ko2 {
        Map<String,String> m4 = new TreeMap<>();
/**
 * Сбор информации о перспективности дисциплины ред,2019-12-03
 * @throws IOException 
 */    
    public void disc_rang3() throws IOException {
        Map<String,int[]> m3 = new TreeMap<>();
        Pattern p3a = Pattern.compile(" v3a=\"([^\"]+)\"");
        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("c:\\IRBIS64\\Datai\\KO\\rang3.mnu")
                ,"windows-1251"))
        ){
            for(Curr2 c2:this.currMap.values()) {
                Matcher m = p3a.matcher(c2.attr);
String n = m.find() ? m.group(1).length()==8 ? m.group(1).substring(3,5) : "ng" : "nf";
//                bw.write(c2.key+" "+c2.y+" "+ n);
//                bw.newLine();
                if(c2.courseDiscs == null) continue;
        Map<String,int[]> xm3 = new TreeMap<>();
                int[] i3 = {0,0,(Integer.parseInt(c2.y) > year4)?1:0}; // новый план
                for(int course=0; course<c2.courseDiscs.length; course++) {
                    if(i3[0] > 0)
                        if(i3[1] == 0)
                            i3[1] = 1; // будут изучать
                    if(c2.courseGroups == null) i3[0] = 0;
                    else if(c2.courseGroups.length <= course) i3[0] = 0;
                    else if(c2.courseGroups[course] == null) i3[0] = 0;
                    else i3[0] = 1; // изучают
                    if(c2.courseDiscs[course] == null) continue;
//                    bw.write("\t"+course);
//                    bw.newLine();
//                    if(IntStream.of(i3).sum() == 0) continue; //
                    for(Curr2.CurrDisc2 cd2:c2.courseDiscs[course].keySet()) {
                        if(cd2.d==null) continue; // дисциплина без имени
                        String n3 = n+" "+cd2.d;
//                        bw.write(n3);
//                        bw.newLine();
                        int[] x3 = xm3.get(n3);
                        if(x3 == null) xm3.put(n3, Arrays.copyOf(i3,3));
                        else for(int i=0;i<3;i++) if(x3[i]==0) x3[i] = i3[i];
                    }
                }
                for(Map.Entry<String,int[]> e:xm3.entrySet()) {
                    int[] x3 = m3.get(e.getKey());
                    if(x3 == null) m3.put(e.getKey(),e.getValue());
                    else for(int i=0;i<3;i++) x3[i] += e.getValue()[i];
                }
            }
            for(Map.Entry<String,int[]> e:m3.entrySet()) {
                int[] x3 = e.getValue();
                String s3 = ""+x3[0]+"-"+x3[1]+"-"+x3[2];
//                bw.write(s3+" "+e.getKey());
//                bw.newLine();
                String s4 = e.getKey().substring(3);
                String s2 = e.getKey().substring(0,2);
                String v4 = m4.get(s4);
                if(v4 != null) s2 = v4+" "+s2;
                m4.put(s4, s2+"="+s3);
            }
            for(Map.Entry<String,String> e:m4.entrySet()) {
                bw.write(e.getKey().replace(' ', '_'));
                bw.newLine();
                bw.write(e.getValue());
                bw.newLine();
            }
            bw.write("*****");
        }
//        if(m3 != null) System.exit(3);
    }
    
    static final Map<String,Set<String>> denys = new TreeMap<>();
    static final Map<String,Integer> syn2mfn = new TreeMap<>();
    static final Map<Integer,Set<String>> mfn2syn = new TreeMap<>();
    static Pattern pDoubleSpace = Pattern.compile("[\\s]+");
    private String normalizeText(String s) {
        return pDoubleSpace.matcher(s.trim()).replaceAll(" ");
    }
    static Pattern pNotAlphaNum = Pattern.compile("[^\\p{L}0-9]+");
    private String normalizeKey(String t) {
        return pNotAlphaNum.matcher(t).replaceAll("").toUpperCase();
    }
/**
 * возвращает объект соответствующий дисциплине учебного плана
 * @param text передается нормализованным и не проверяется на нул или пусто
 * @return нул или объект
 */            
    private Disc2 getDisc2(String text) {
        String key = normalizeKey(text);
        Disc2 disc2 = discMap.get(key); 
        if(disc2==null){
            Integer mfn = syn2mfn.get(key);
            if(mfn != null) 
                disc2 = discMap.get(key = "$YN"+mfn); 
        }
        return disc2;
    }
    private Trash2 trash2 = null;
    private Trash64 trash = null;
/**
 * перезапись картотеки книгообеспеченности ред.2019-12-03
 * @param tags
 * @throws IOException 
 */    
    private void write(Map<Integer,List<Field>> tags) throws IOException {
        if(trash!=null) trash.write(tags);
        else trash2.write(tags);
    }
    private Curr2 emptyCurr2;
    private Curr2 getEmptyCurr() {
        if(emptyCurr2 == null) {
            emptyCurr2 = new Curr2(0);
            emptyCurr2.attr = "";
            emptyCurr2.xmlid = 0;
        }
        return emptyCurr2;
    }
    private Disc2 emptyDisc2;
    private Disc2 getEmptyDisc(Curr2.CurrDisc2 cd2) {
        if(emptyDisc2 == null) 
            emptyDisc2 = new Disc2(cd2);
        return emptyDisc2;
    }
    //private static final boolean debugon=true;
/**
 * карта новизны учебной литературы по циклам дисциплин
 */    
    Map<String,String> newSynMap = new TreeMap<>();
/**
 * актуальные циклы дисциплин
 */    
    Set<String> newSynSet = new TreeSet<>();
/**
 * реальные результаты расчета
 */    
    private XLines koxml;
/**
 * ошибки заполнения 910 полей
 */    
    private List<String> mfnList = new ArrayList<>();
/**
 * все карточки книгообеспеченности
 */    
    private Map<String,Disc2> discMap=new TreeMap<>();
/**
 * все учебные планы
 */    
    private Map<String,Curr2> currMap = new TreeMap<>();
/**
 * таблица распределения изданий по БД и типу
 */    
    private Map<String,int[][]> bindexesMap = new TreeMap<>();
/**
 * deprecated
 */    
//    private List<Book2> fltbooks;
//    private Set<String>[] derrSet;
//    private Set<String>[] dwarnSet;
    private List<Book2>[] fondbooks;
//    private Set<String> fulldiscs = new TreeSet<>();
    /**
     * строка наименование учебного года расчета
     */
    private String syear;
    /**
     * календарный год начала учебного года
     */
    private int year4;
//    private XRF64 xrf;
//    private Map<String,Map<String,Integer>> xrfIndex;
/**
 * карта синонимов дисциплин
 */    
//    private Map<String,String> synMap;
    private Map<String,Group2> groupMap = new TreeMap<>(); // наверное не нужен
    private String[] cs={"ГСЭ","ЕН","ОПД","СД","ФТД"};
/**
 * максимально возможный семестр (оптимизация в массив)
 */    
    private static final int g3max = 16;
    private static final int[] iempty = new int[0];
    private static final String charsetName = "windows-1251";
    private static final Charset charset = Charset.forName(charsetName);
    /**
     * количество полугодий учебного года
     */
    private static final int y2count = 2;
    private static final int bmaxindex = 2;
    private static final Set<Integer> g3empty = new TreeSet<>();
    //private static Set<String> czs = new TreeSet<String>();
    static final Comparator<Field> A_ORDER = new Comparator<Field>() {
        @Override
        public int compare(Field f1, Field f2) {
            Map<Character,String> s1 = f1.getSubs();
            Map<Character,String> s2 = f2.getSubs();
            return s1.get('a').compareTo(s2.get('a'));
        }
    };
    static int compareString(String s1, String s2) {
        if(s1==null)
            return (s2==null) ? 0 : -1;
        return (s2==null) ? 1 : s1.compareTo(s2);
    }
    static int compareStrings(String[] ss1, String[] ss2) {
        if(ss1==null)
            return (ss2==null) ? 0 : -1;
        if(ss2==null) return 1;
        for(int index=0; index<ss1.length; ++index) {
            int i = compareString(ss1[index], ss2[index]);
            if(i!=0) return i;
        }
        return 0;
    }
    static final Comparator<Field> KO2_BOOK_ORDER = new Comparator<Field>() {
        @Override
        public int compare(Field f1, Field f2) {
            Map<Character,String> s1 = f1.getSubs();
            Map<Character,String> s2 = f2.getSubs();
            int i = s2.get('e').compareTo(s1.get('e')); // год издания
            if(i != 0) return i;
            i = s1.get('x').compareTo(s2.get('x')); // описание
            if(i != 0) return i;
            return s1.get('a').compareTo(s2.get('a')); // учебник (id)
        }
    };
    static final Comparator<Map.Entry<Book2,int[]>> KO2_BOOK2_ORDER
            = new Comparator<Map.Entry<Book2,int[]>>() {
        @Override
        public int compare(Map.Entry<Book2,int[]> e1, Map.Entry<Book2,int[]> e2) {
            TextBook t1 = e1.getKey().t;
            TextBook t2 = e2.getKey().t;
            int i;
            i = compareString(t2.getYear(), t1.getYear()); // год издания
            if(i != 0) return i;
            i = compareStrings(t1.getTexts(), t2.getTexts()); // описание
            if(i != 0) return i;
            i = compareString(t1.getBd(), t2.getBd()); // БД
            if(i != 0) return i;
            return t2.getMfn() - t1.getMfn(); // mfn
        }
    };
    static final Comparator<Field> KO2_61_ORDER = new Comparator<Field>() {
        @Override
        public int compare(Field f1, Field f2) {
            int i;
            i = f1.compareTo(f2,'1'); // год начала
            if(i != 0) return i;
            i = f1.compareTo(f2,'d'); // код плана
            if(i != 0) return i;
            i = f1.compareTo(f2,'a'); // группа
            return i;
        }
    };
    static final Comparator<Field> AD_ORDER = new Comparator<Field>() {
        @Override
        public int compare(Field f1, Field f2) {
            Map<Character,String> s1 = f1.getSubs();
            Map<Character,String> s2 = f2.getSubs();
            int i = s1.get('a').compareTo(s2.get('a'));
            if(i != 0) return i;
            return s1.get('d').compareTo(s2.get('d'));
        }
    };
    static final Comparator<Field> E_INT_ORDER = new Comparator<Field>() {
        @Override
        public int compare(Field f, Field f2) {
            return f.getInt('e')-f2.getInt('e');
        }
    };
    static final Comparator<Book2> BOOK_ORDER = new Comparator<Book2>() {
        @Override
        public int compare(Book2 b, Book2 b2) {
            int i = b.t.getBd().compareTo(b2.t.getBd());
            if(i!=0) return i;
            return b.t.getMfn()-b2.t.getMfn();
        }
    };
    static final Comparator<Group2.GroupCurr2> GROUP_ORDER = new Comparator<Group2.GroupCurr2>() {
        @Override
        public int compare(Group2.GroupCurr2 gc, Group2.GroupCurr2 gc2) {
            return gc.group2this().key.compareTo(gc2.group2this().key);
        }
    };
    static final Comparator<Curr2.CurrDisc2> ORDER_61 = new Comparator<Curr2.CurrDisc2>() {
        @Override
        public int compare(Curr2.CurrDisc2 cd, Curr2.CurrDisc2 cd2) {
            return cd.curr2this().spec.compareTo(cd2.curr2this().spec);
        }
    };
    static final Comparator<Curr2.CurrDisc2> ORDER_y = new Comparator<Curr2.CurrDisc2>() {
        @Override
        public int compare(Curr2.CurrDisc2 cd, Curr2.CurrDisc2 cd2) {
            return cd.y.compareTo(cd2.y);
        }
    };
/**
 * Карта ключей соотв. рабочему листу
 * @return
 */
    public static Map<String,String> getMapIndex() {
        Map<String,String> map = new TreeMap<>();
        map.put("GROUP", "021b");
        map.put("OKSO", "003b");
        return map;
    }
    static private String slim(String s) {
        return s.trim().replace("\u0020{2,}", "\u0020");
    }
/**
 * причесывание ко
 * @param ko причесываемое
 * @return красивое целое
 */        
    static private long ko100(double ko) {
        if(ko > 0) {
            ko = Math.ceil(ko); // маленькие подтягиваем до целого
            if(ko>100) // большие срезаем до двузначной мантиссы
                ko -= ko % Math.pow(10,Math.floor(Math.log10(ko))-1);
        }
        return (long)ko;
    }
/**
 * инициализация студенто-сборника-по-полугодиям если он был null
 * @return студенто-сборник-по-полугодиям
 */    
    static int[] y2new() {
        int[] y2 = new int[y2count];
        Arrays.fill(y2, -1);
        return y2;
    }
/**
 * правильно сделать добавку к сборнику
 * @param y2 сборник
 * @param inc добавка not null
 * @return null или заново инициализированный сборник
 */    
    static int[] y2inc(int[] y2, int[] inc) {
        int[] y2new = null;
        if(y2 == null) {
            y2 = y2new = y2new();
        }
        for(int y2index=0; y2index<y2count; y2index++) {
            int i = inc[y2index];
            if(i < 0) continue;
            if(y2[y2index] < 0)
                y2[y2index] = i;
            else y2[y2index] += i;
        }
        return y2new;
    }
/**
 * оформить массив списков книг в xml
 * @param x XLines для вставки сформированного содержимого
 * @param books массив списков книг
 */    
    static XLines xmlBooks(XLines x, List<Book2>[] books) {
        if(books != null) {
            final String[] narray = {"b","p"};
            int i=0;
            for(List<Book2> blist:books) {
                if(blist != null) {
                    String n = narray[i];
                    for(Book2 b:blist)
                        x.xml(n, b.t.getId());
                }
                ++i;
            }
        }
        return x;
    }
    static String koAttr(double[] dko) {
        long i;
        String s = "";
        if((i = Math.round(dko[0]/100)) != 0)
            s += XLines.attr("ko", ""+i);
        if((i = Math.round(dko[1]/100)) != 0)
            s += XLines.attr("km", ""+i);
        return s;
    }
    static String text691(List<String[][][]> i691, boolean bcurr) {
        int i[] = new int[2];
        if(bcurr) {
            i[0]=1;
            i[1]=2;
        } else {
            i[0]=0;
            i[1]=3;
        }
        String s = "";
        for(String[][][] s3:i691) {
            for(int index:i) {
                if(s3[index]!=null)
                    for(String[] s1:s3[index]) {
                        if(s1!=null)
                            for(String s0:s1){
                                if(s0!=null) s += (s.isEmpty())?s0:";"+s0;
                            }
                    }
            }
        }
        return s;
    }
    static void xbset(XLines x, Set<Book2> bset){
        if(bset.isEmpty()) return;
        Book2[] a = bset.toArray(new Book2[0]);
        Arrays.sort(a,BOOK_ORDER);
        for(Book2 b:a){
//            x.xml("b",XLines.xmlattr("y", b.year),b.getId());
            x.xml("b",b.t.getId());
        }
    };
/**
 * добавляет книгу в список со всеми проверками
 * @param books список книг[dopindex]
 * @param dopindex дополнительная учебная литература?
 * @param b добавляемый учебник
 * @return если не null - books пересоздан заново
 */        
    private static List<Book2>[] addBook2(List<Book2>[] books, int dopindex, Book2 b) {
        List<Book2>[] new2 = null;
        if(books==null)
            books = new2 = new List[2]; // books пересоздан
        else if(dopindex>0) { // это дополнительная
            List<Book2> books0 = books[0];
            if(books0 != null) // основные есть
                if(b == books0.get(books0.size()-1)) // наш учебник уже - основной
                    return null;
        }
        List<Book2> books4 = books[dopindex]; 
        if(books4 == null)
            books[dopindex] = (books4 = new ArrayList<>());
        books4.add(b);
        return new2;
    }
    private static Map<Character,Set<String>> map691 = new TreeMap<>();
//    private static List<String> p691err = new ArrayList<>();
//    private Map<String,List<Group2.GroupCurr2>> currGroupMap = new TreeMap<>();;
    static void kaf_add(Map<String,Map<Integer,Set<String>>> dkaf,
        String ds, int fld, String kaf) {
        Map<Integer,Set<String>> dd = dkaf.get(ds);
        if(dd==null) dkaf.put(ds, dd = new TreeMap<>());
        Set<String> ddfld = dd.get(fld);
        if(ddfld==null) dd.put(fld, ddfld = new TreeSet<>());
        ddfld.add(kaf);
    }
    static Set<String> kaf_find(Map<String,Map<Integer,Set<String>>> dkaf,
        String ds, int fld) {
        Map<Integer,Set<String>> dd = dkaf.get(ds);
        if(dd==null) return null;
        return dd.get(fld);
    }
    static Set<String> kaf_agg(Map<String,Map<Integer,Set<String>>> dkaf,
        String ds) {
        if(!dkaf.containsKey(ds)) return null;
        Set<String> agg = new TreeSet<>();
        for(Set<String> fkaf:dkaf.get(ds).values())
            for(String kaf: fkaf)
                agg.add(kaf);
        return agg;
    }
/**
 * проходит, если фильтр=null или является элементом фильтра
 * @param s проверяемое
 * @param fs фильтры
 * @return проходит?
 */
    private static boolean check691unit(String s,String[] fs) {
        if(fs==null) return true;
        if(s==null) return false;
        for(String f:fs) {
            if(f.startsWith("*")){
                if(f.endsWith("*")){
                    if(s.contains(f.subSequence(1, f.length()-1)))
                        return true;
                }
                else if(s.endsWith(f.substring(1)))
                    return true;
            } else if(f.endsWith("*")){
                if(s.startsWith(f.substring(0, f.length()-1)))
                    return true;
            } else if(s.equals(f))
                return true;
        }
        return false;
    }
/**
 * проходит, если фильтр=null или является элементом фильтра
 * @param s проверяемый массив семестров
 * @param plots фильтры на семестры (диапазоны задаются через тире)
 * @return массив подходящих семестров или null, если их нет
 */
    static private int[] check691unit(int[] s,Plots plots) {
        if(plots == null) return s;
        for(int i=0;i<s.length;i++) {
            if(plots.check(s[i])) continue;
            int[] checked = Arrays.copyOfRange(s, 0, s.length-1);
            int checkedcount = i;
            for(int i2=i+1;i<s.length;i2++){
                if(plots.check(s[i2]))
                    checked[checkedcount++] = s[i2];
            }
            if(checkedcount>0)
                return Arrays.copyOf(checked, checkedcount);
            return null;
        }
        return s;
    }
/**
 * проходит если запись фильтров = null или проходят все поля
 * @param ss запись (не null)
 * @param fss запись фильтров
 * @return проходит?
 */
    private static boolean check691(String ss[],String[][] fss) {
        if(fss==null) return true;
        for(int i=0; i<ss.length; ++i)
            if(!check691unit(ss[i],fss[i]))
                return false;
        return true;
    }
    private static String d3(double[] a) {
        long[] a3 = new long[]
            {Math.round(a[0] * 100 / a[3])
            ,Math.round(a[1])
            ,Math.round(a[2] * 100 / a[3])};
        String s = ""+a3[0];
        //if(a3[1]>0) s += a3[1];
        s += "-"+a3[2];
        return "<b>"+s;
    }
    private static String m3(double[] a3) {
        String s = "" + Math.round(a3[0]);
        s += "-" + Math.round(a3[2]);
        return s;
    }
    public static String[] splitTrim(String s) {
        if(s==null) return null;
        String[] ss=s.trim().toUpperCase().replace(';', ',').replace(' ', ',').split(",");
        int sslength = ss.length;
        for(int i=0; i<sslength; ++i){
            String st = ss[i].trim();
            if(st.length()==ss[i].length()) continue;
            if(st.isEmpty()) {
                int newlength = i;
                for(int j=i+1; j<sslength; ++j) {
                    st = ss[j].trim();
                    if(st.isEmpty()) continue;
                    ss[newlength] = st;
                    ++newlength;
                }
                if(newlength==0) return null;
                return Arrays.copyOf(ss, newlength);
            }
            ss[i] = st;
        }
        return ss;
    }
/**
 *
 * @param subs 691 подполе без наименования дисциплины
 * @return рваный массив группировки ограничений или null если их нет
 */
    public static String[][][] prepare691(Map<Character,String> subs) {
        final String s691 = "fbkshncvxyao"; // 691 подполя
        final int[] index1 ={0,1,1,1,2,2,2,2,2,2,3,3}; // маска первого индекса
        final int[] size2 ={1,3,6,2}; // размерность второго индекса
        final int[] shift2 ={0,1,1+3,1+3+6}; // сдвиг второго индекса относительно позиции в строке
        String[][][] a = null;
        for(Map.Entry<Character,String> e:subs.entrySet()) {
            char k = e.getKey();
            int index = s691.indexOf(k);
            if(index<0) {
                System.out.println("неизвестное подполе 691="+(k=='\u0000'?"null":""+k));
                continue;
            }
            String[] ss = splitTrim(e.getValue());
            if(ss==null) continue;
            for(String ss6:ss) {
                String fars=Words.farCheck(ss6);
                if(fars!=null)
                    System.out.println(" иностранная буква в фильтре 691: "+fars);
            }
            int aindex = index1[index];
            if(a==null) a = new String[4][][];
            if(a[aindex]==null) a[aindex] = new String[size2[aindex]][];
            a[aindex][index-shift2[aindex]] = ss;

            Set<String> set691 = map691.get(k);
            if(set691==null) map691.put(k, set691 = new TreeSet<>());
            for(String s:ss)
                set691.add(s);
        }
        return a;
    }
/**
 * строка содержит список тире-диапазонов через запятую или косую
 * @param s
 * @return набор целых чисел
 */
    public static int[] readSemesters(String s) {
        if(s==null) return null;
//        if(s.equals("5/ 7-9"))
//            System.out.println();
        Set<Integer> set = new TreeSet<>();
        try {
            for(String s1:s.replace("/", ",").split(",")){
                int i = s1.indexOf('-');
                if(i<0) set.add(Integer.parseInt(s1.trim()));
                else {
                    int i2 = Integer.parseInt(s1.substring(i+1).trim());
                    int i1 = Integer.parseInt(s1.substring(0, i).trim());
                    if(i2<i1 || i2>i1+100) return null;
                    for(; i1<=i2; i1++)
                        set.add(i1);
                }
            }
        }
        catch (Exception e) {return null;}
        if(set.isEmpty()) return null;
        int[] a = new int[set.size()];
        int ind=0;
        for(Integer i:set)
            a[ind++] = i;
        Arrays.sort(a);
        if(a[0]<=0) return null;
        return a;
    }
/**
 * строка содержит либо проценты либо отношение, если через косую черту
 * @param s
 * @return возвращаются всегда целые проценты
 */
    public static float readPercentage(String s) {
        int i = s.indexOf('/');
        if(i<0) return readInt(s);
        try {return (float)readInt(s.substring(0,i)) * 100 / readInt(s.substring(i+1));}
        catch(Exception e) {return 0;}
    }
/**
 * возвращает изображаемое целое или ноль
 * @param s
 * @return
 */
    public static int readInt(String s) {
        try { return Integer.parseInt(s.trim());}
        catch (Exception e) {
            System.out.println(" не могу извлечь число из " + s);
            return 0;
        }
    }
/**
 * перебор БД с учебниками
 * @param names имена БД через дефис-тире-минус
 * @throws IOException 
 */    
    public void checkBooks(String names) throws IOException {
        if(names==null || names.isEmpty())
            names="BOOK-UMLI-INOS-ABS";
        else names=names.toUpperCase();
        String[] anames = names.split("-");
        for(String s:anames) {
            if("UMLI".equals(s.trim()))
                TextBook.setUmli(true);
        }
        for(String s:anames) {
            readBooks(s.trim());
        }
        indexHtml();
        writeflt();
    }
/**
 * mfnerr.txt ред,2019-12-03
 * @throws FileNotFoundException
 * @throws IOException 
 */
    void writeflt() throws FileNotFoundException, IOException {
        FileOutputStream f = new FileOutputStream(syear+"\\mfnerr.txt");
        f.write("Ошибки в записях каталога:\r\n".getBytes(charset));
        for(String smfn:mfnList)
            f.write(smfn.concat("\r\n").getBytes(charset));
        f.write("\r\nДисциплины в записях каталога, которых нет в учебных планах:\r\n".getBytes(charset));
        for(String d:denys.keySet()) {
            String names = " ("+String.join(",",denys.get(d))+")";
            f.write(d.getBytes(charset));
            f.write(names.concat("\r\n").getBytes(charset));
        }
        f.close();
    }

    private void readDiscs(Map<Integer, List<Field>> tags) {
//        int tag=7-1;
        for(int tag=7;tag<=11;++tag) {
            List<Field> fs = tags.get(tag);
            if(fs==null) continue;
            for(Field f:fs) {
                String s = f.getSubs().get('c');
//                if(s!=null) fulldiscs.add(s.trim().toUpperCase());
            }
        }
    }
/**
 * перезапись карточек книгообеспеченности ред.2019-12-03
 * @throws IOException 
 */
    void writeDiscs() throws IOException {
        if(emptyDisc2 != null)
            write(emptyDisc2.xmlTags2());
        for(Disc2 d2:discMap.values()) {
//            if(d2.cdList==null) {
//                d2.xml2();
//                continue;
//            }
//            if(!d2.set59.isEmpty()) {
//                String s59 = d2.set59.iterator().next();
//                for(String s:d2.set59)
//                    write(d2.tags59(s59,s));
//            }
            Map<Integer, List<Field>> tags = d2.xmlTags();
            write(tags);
            if(!tags.containsKey(59)) continue;
            Field f60 = tags.get(60).get(0);
            List<String> ss59 = new ArrayList<>();
            for(Field f:tags.get(59))
                ss59.add(f.getSub('\0'));
            for(String s59:ss59) {
                String s60b = f60.getSub('b');
                f60.put('b', s59);
                for(Field f:tags.get(59)) {
                    if(f.getSub('\0').equals(s59)) {
                        f.put('\0', s60b);
                        break;
                    }
                }
                write(tags);
            }
        }
        koxml.close("y");
    }
//    private String getSyn1(String sdisc) {
//        String key = sdisc.toUpperCase();
//        String syn1 = synMap.get(key);
//        if(syn1==null) return key;
//        return syn1;
//    }
/**
 * читает контингент группы, соответствующий расчетному учебному году
 * @param tags
 * syear расчетный учебный год
 * @return контингент группы в расчетном учебном году
 */
    public int readGroupCountingent(Map<Integer,List<Field>> tags) {
        List<Field> fs = tags.get(22);
        if(fs==null) return 0;
        String syear4 = syear.substring(0, 4);
        int i = fs.size();
        while(--i >= 0) {
            Map<Character, String> subs = fs.get(i).getSubs();
            if(subs.get('a').substring(0, 4).compareTo(syear4) <= 0)
                return readInt(subs.get('b'));
        }
        return 0;
    }
    public String readGroupCountingent2(Map<Integer,List<Field>> tags) {
        List<Field> fs = tags.get(22);
        if(fs==null) return "";
        String syear4 = syear.substring(0, 4);
        int i = fs.size();
        while(--i >= 0) {
            Map<Character, String> subs = fs.get(i).getSubs();
            if(subs.get('a').substring(0, 4).compareTo(syear4) <= 0)
                return subs.get('b');
        }
        return "";
    }
/**
 * создает индексы по всем рабочим листам
 * @param xrf
 * @return
 * @throws FileNotFoundException
 * @throws IOException
 */
    private Map<String,Map<String,Integer>> buildIndex(XRF64 xrf, Map<String,String> mapIndex)
            throws IOException {
        Map<String,Map<String,Integer>> map = new TreeMap<>();
        String syear4 = syear.substring(0, 4);
        int mfn=0;
        int nxtmfn = xrf.getMST().nxtmfn();
        while(++mfn < nxtmfn) {
            if(xrf.next() > 0) continue;
            Map<Integer,List<Field>> tags = xrf.read(mfn);
            String s = Field.getTagsSub(tags, 920, '\u0000');
            if(s==null || s.isEmpty()) continue;
            String s4 = mapIndex.get(s);
            if(s4==null) {
                if("KO2".equals(s)) {
                    s4 = Field.getTagsSub(tags, 60, 'a');
                    if(!s4.startsWith(syear4)) continue;
                    s4 += " "+Field.getTagsSubUpper(tags, 60, 'b');
                }
                else {
                    s4 = Field.getTagsSub(tags,"903");
                    if("CURR".equals(s)) readDiscs(tags);
                }
            }
            else s4 = Field.getTagsSub(tags,s4);
            if(s4==null) s4 = "MFN"+mfn;
            Map<String,Integer> mapMFN = map.get(s);
            if(mapMFN==null) 
                map.put(s,(mapMFN = new TreeMap<>()));
            else if(mapMFN.containsKey(s4)){
                System.out.println("*** дубль ключа "+s+"="+s4);
                 s4 = "DBLMFN"+mfn;
            }
            mapMFN.put(s4,mfn);
        }
        for(String s1:map.keySet())
            System.out.println(s1+"="+map.get(s1).size());
        return map;
    }
    public void init(int year4) {
        this.year4 = year4;
        syear = year4+"-"+((year4+1)%100);
//        koxmlcindex = 1;
        koxml = new XLines("ko", syear);
        koxml.xml1("y","y",syear);
//        year2 = year4*2+1;
        System.out.println("Расчет книгообеспеченности на "+syear+" учебный год.");
    }
/**
 * Чтение всех записей из БД Книгообеспеченность ред,2019-12-03
 * @param year4 учебный год (первый)
 * @return Trash2 wrapper for rewriting file-records
 * @throws IOException 
 */    
    public Closeable readKo(int year4) throws IOException {
        String name = "ko";
        this.year4 = year4;
        syear = year4+"-"+((year4+1)%100);
//        koxmlcindex = 1;
        koxml = new XLines(name, syear);
        koxml.xml1("y","y",syear);
//        year2 = year4*2+1;
        System.out.println("Расчет книгообеспеченности на "+syear+" учебный год.");
//        xrf = new XRF64();
//        xrf.open("C:\\IRBIS64\\DATAI\\"+name+"\\"+name, "r");
        System.out.println("");
//        xrfIndex = buildIndex(xrf,getMapIndex());
//        trash = new Trash64();
        trash2 = new Trash2();
//        trash.setYear(syear);
//        trash.open("C:\\IRBIS64\\DATAI\\"+name+"\\"+name,"rw");
        trash2.open("C:\\IRBIS64\\DATAI\\"+name+"\\"+name,new String[] {"KO2","SUBJ"});
        readSyns();
        readCurrs();
        readGroups();
//        xrf.close();
//        xrf=null;
        System.out.println();
        System.out.println(""+groupMap.size()+" активных учебных групп");
        System.out.println(""+currMap.size()+" активных учебных планов");
        System.out.println(""+discMap.size()+" активных дисциплин");
        for(String cd:newSynSet){
            koxml.newline().xml("y",XLines.attr("cd", cd),newSynMap.get(cd));
        }
        koxml.add(getEmptyCurr().xml());
        for(Curr2 curr2:currMap.values()){
            koxml.add(curr2.xml());
        }
//        groupsHtml();
//        derrSet = new Set[bmaxindex];
        fondbooks = new List[bmaxindex];
        for(int bindex=0; bindex<bmaxindex; ++bindex) {
//            derrSet[bindex] = new TreeSet<>();
            fondbooks[bindex] = new ArrayList<>();
        }
        return trash2;
    }
/**
 * Чтение учебных групп из БД Книгообеспеченность ред.2019-12-03
 * @throws IOException 
 */
    public void readGroups() throws IOException {
        int[] n = {0,0,0,0,0,0,0};
//        Progress2 p2 = new Progress2(n.length, "groups");
        Set<String> s0 = new HashSet<>();
        for(int mfn:trash2.get920("GROUP")) {
            n[1]++;
//            p2.inc();
            Map<Integer,List<Field>> tags = trash2.read(mfn);
            Group2 group = new Group2();
            group.read(tags);
            boolean bcount = false;
            for(Field f22:tags.get(22)){
                String s = f22.getSubs().get('b');
                int count = readInt(s);
                if(count<=0){
                    System.out.println("*** в группе "+group.key+" неверное количество студентов="+s);
                    continue;
                }
                s = f22.getSubs().get('c');
                Curr2 curr2 = (s==null) ? null : currMap.get(s = s.trim());
                if(curr2 == null){ // пусто или нет такого
                    s0.add(s);
                    n[3]+=count;
                    System.out.println("*** в группе "+group.key+" у "+count+" студентов неверно назначен план="+s);
//                    continue;
                }
                bcount=true;
                n[4]+=count;
                n[6]++;
                group.newGroupCurr2(count, curr2, f22.getSubs());
            }
            if(bcount) {
                n[5]++;
                groupMap.put(group.key, group);
            }
        }
        if(!s0.isEmpty()) {
            String sv = "";
            for(String s:s0)
                sv += " " + s;
            emptyCurr2.ver = sv;
        }
        System.out.println(""+n[1]+" групп прочитано");
        if(n[3]>0) System.out.println(""+n[2]+" групп отброшено");
        System.out.println(""+n[3]+" студентов отброшено из-за несоответствия плана");
        System.out.println(""+n[4]+" студентов зафиксировано для расчета");
        System.out.println(""+n[5]+" групп ("+n[6]+" подгрупп) зафиксировано для расчета");
        for(Disc2 d:discMap.values()){
            if(d.key.isEmpty()) continue;
            d.sortcd();
        }
    }
    private List<String> bindexesTable(){
        List<String> ss = new ArrayList<>();
        ss.add("<table border=1 cellspacing=0 cellpadding=2>");
        ss.add("<tr> <th>БД</th> <th>Всего</th>");
//        ss.add("<th colspan="+TextBook.textbooks.length+">Учебников</th></tr><tr>");
        List<String> ss1 = new ArrayList<>();
        int[][] i2 = new int[bmaxindex][2];
        for(Map.Entry<String,int[][]> e: bindexesMap.entrySet()) {
            ss1.add("</tr><tr><td>"+e.getKey()+"</td>");
            int[] ie = e.getValue()[bmaxindex];
            ss1.add("<td>"+ie[1]+"экз. "+ie[0]+"назв.</td>");
            for(int bindex=0; bindex<bmaxindex; bindex++){
                ie = e.getValue()[bindex];
                ss1.add("<td>"+ie[1]+"экз. "+ie[0]+"назв.</td>");
                i2[bindex][0] += ie[0];
                i2[bindex][1] += ie[1];
            }
            ss1.add("</tr>");
        }
        for(int bindex=0; bindex<bmaxindex; bindex++){
            ss.add("<th>"+TextBook.textbooks[bindex]);
            ss.add("<br/>"+i2[bindex][1]+" экз. "+i2[bindex][0]+"назв.</th>");
        }
        ss.addAll(ss1);
        ss.add("</table>");
        return ss;
    }
    private static final Map<Character,String> test691 = new TreeMap<>();
    static {
        test691.put('f', "семестр");
        test691.put('b', "d:v7d");
        test691.put('k', "d:uk");
        test691.put('s', "d:cd");
        test691.put('h', "c:v6c");
        test691.put('n', "c:v2a");
        test691.put('c', "c:v3a");
        test691.put('v', "c:v5a");
        test691.put('x', "c:v4a");
        test691.put('y', "c:v5b");
        test691.put('a', "g:v20f");
        test691.put('o', "g:v20k");
    }
/**
 * перебор учебников из БД ред.2019-12-03
 * @param name имя БД
 * @throws IOException 
 */
    public void readBooks(String name) throws IOException {
        System.out.println("readBooks("+name+") started. "+(new Date()));
        XRF64 xrf;
        try {
            xrf = XRF64.open("C:\\IRBIS64\\DATAI\\"+name+"\\"+name, "r");
        } catch (Exception e) {
            System.out.println("********* Exception="+e.getMessage());
            return;
        }
        Counter2 n = new Counter2();
        int[][] bindexes = new int[bmaxindex+1][3];
        bindexesMap.put(name, bindexes);
        int mfn=0;
        int nxtmfn = xrf.getMST().nxtmfn();
mfn:    while(++mfn < nxtmfn) {
            if(xrf.next() > 0) continue;
            n.inc("записей"); // real record
            Map<Integer,List<Field>> tags = xrf.read(mfn);
            if(!tags.containsKey(691)) continue;
            n.inc("содержат 691"); // 691 record
            if(tags.containsKey(907)) 
                for(Field f907:tags.get(907)) { 
                    if(!"ОБРНЗ".equals(f907.getSubsUpper('c'))) continue;
System.out.println("учебник "+name+"-"+mfn+" отброшен ОБРНЗ");
                    n.inc("учебник.отпал.907-ОБРНЗ");
                    continue mfn;
                }
            TextBook t = new TextBook(name,mfn);
            bindexes[bmaxindex][0]++;
            String s910 = t.read910(tags.get(910));
            if(s910!=null) {
System.out.println("учебник "+name+"-"+mfn+" отброшен " + s910);
                n.inc("учебник.отпал.910-подсчет-экземпляров");
                mfnList.add(name+"="+mfn+" " + s910);
                bindexes[bmaxindex][2]++;
                continue;
            }
            bindexes[bmaxindex][1] += t.getCount();
            if(!t.read(tags)) {
System.out.println("учебник "+name+"-"+mfn+" отброшен без ссылки или экземпляров");
                n.inc("учебник.отпал.ничего-нет");
                continue;
            } // не прошел в список литературы
            bindexes[t.getBIndex()][0]++;
            bindexes[t.getBIndex()][1] += t.getCount();
            Book2 book = new Book2(t);
            fondbooks[t.getBIndex()].add(book); // все учебники библиотеки
Map<Disc2,List<String[][][]>>[] maps =  new Map[] {new HashMap<>(), new HashMap<>()};
//Set<Disc2>[] sets = new Set[] {new HashSet<>(), new HashSet<>()};
            int read691 = 0; // анализируем 691 ред.2019-12-03
            for(Field f:tags.get(691)) {
                Map<Character,String> subs = f.getSubs();
                String sdisc = subs.remove('d'); // строка дисциплины из учебника
                if(sdisc==null) {
                    mfnList.add(name+"-"+mfn+". нет 691^d");
                    continue; // нет дисциплины
                }
                sdisc = normalizeText(sdisc);
                if(sdisc.isEmpty()) {
                    mfnList.add(name+"-"+mfn+". пустой 691^d");
                    continue; //пустая дисциплина
                }
                String fars=Words.farCheck(sdisc);
                if(fars!=null)
                    System.out.println(""+mfn+" иностранная буква: "+fars);
                String subg = subs.remove('g');
                subg = (subg == null) ? "осн" : subg.toLowerCase();
                int dopindex = subg.equals("осн")? 0 : subg.equals("доп")? 1 : -1;
                if(dopindex < 0) {
                    mfnList.add(name+"-"+mfn+" отброшено v691 c ^g=" + subg);
                    continue;
                }
                Disc2 d2 = getDisc2(sdisc);
                if(d2==null) { // такой дисциплины нет в активных планах
                    Set<String> denyset = denys.get(sdisc);
                    if(denyset == null)
                        denys.put(sdisc, denyset = new TreeSet<>());
                    denyset.add(name);
                    continue;
                }
                ++read691;
                subs.remove('z'); // удаляем комментарий. его не сверяем
                book.read691d(subs,maps[dopindex],mfn,d2,sdisc);
            }
            n.inc(read691==0 ? "691.отпал.планы" : "691.вышел на фильтры");
            Set<Disc2> d2set = new HashSet<>();
            for(int dopindex=0; dopindex<2; dopindex++){
                if(maps[dopindex] == null) continue;
                for(Map.Entry<Disc2,List<String[][][]>> e691:maps[dopindex].entrySet()) {
                    Disc2 disc2 = e691.getKey(); // учебник подходит этой дисциплине
                    d2set.add(disc2);
                    List<String[][][]> d691 = e691.getValue(); // все фильтры групп у книги по этой дисциплине
                    book.linkDisc2(dopindex, disc2, d691); // привязка книг и спроса друг к другу
                }
            }
XLines kxml = new XLines();
            if(!d2set.isEmpty()) {
                if(book.y2counts != null) {
                    for(int y2index=0; y2index<y2count; y2index++) {
                        int count = book.y2counts[y2index];
                        if(count <= 0) continue;
                        kxml.xml("k", "y2", ""+(y2index+1), ""+count);
                    }
                }
                for(Disc2 d2:d2set)
                    kxml.xml("d", "D"+d2.xmlid);
            }
//            for(int dopindex=0; dopindex<2; dopindex++)
//                for(Disc2 d2:sets[dopindex]) {
//                    d2.addBook(dopindex, book);
//                    kxml.xml("d", "X"+d2.xmlid);
//                }
            if(kxml.isEmpty()) continue;
            t.koxml(koxml,kxml);
            koxml.write();
            n.inc("691.прошло.экземпляров",t.getCount());
            n.inc("691.прошло.наименований");
        }
        xrf.close();
        n.inc("<v691>",map691.size());
        for(Map.Entry<Character,Set<String>> e:map691.entrySet()){
//            String s = null;
//            for(String s691:e.getValue())
//                s = s==null ? s691 : s + " " +s691;
            koxml.newline().xml("v691"
                    ,XLines.attr("bd", name)
                    +XLines.attr("sub", ""+e.getKey())
                    +XLines.attr("test", test691.get(e.getKey()))
                    ,String.join(" ",e.getValue()));
        }
        koxml.write();
        map691.clear();
        n.print();
//        System.out.println(""+n[0]+" записей перебрано");
//        System.out.println(""+n[1]+" учебников найдено");
//        System.out.println(""+n[3]+" учебников с " + n[5] + " экземплярами");
//System.out.println(""+n[3]+" учебников по активным дисциплинам");
//System.out.println(""+n[4]+" 691-х полей повторяются:");
    }
    public void scanBooks(String name) throws IOException {
        System.out.println("readBooks("+name+") started. "+(new Date()));
        XRF64 xrf;
        try {
            xrf = XRF64.open("C:\\IRBIS64\\DATAI\\"+name+"\\"+name, "r");
        } catch (Exception e) {
            System.out.println("********* Exception="+e.getMessage());
            return;
        }
        //Progress2 p2 = new Progress2(bxrf.maxMFN(), name);
        int[] n = new int[8];
//        int[][] bindexes = new int[bmaxindex+1][3];
//        bindexesMap.put(name, bindexes);
        //int bindex = name.equalsIgnoreCase("umli")?1:0;
//        Map<Disc2,List<String[][][]>> map691 = new HashMap<Disc2,List<String[][][]>>();
        int mfn=0;
        int nxtmfn = xrf.getMST().nxtmfn();
        while(++mfn < nxtmfn) {
            //p2.inc();
            ++n[0];
            if(xrf.next() > 0) continue;
            Map<Integer,List<Field>> tags = xrf.read(mfn);
//            if(mfn==1452)
//                System.out.println(mfn);
            TextBook t = new TextBook(name,mfn);
            if(!tags.containsKey(910)) continue;
            String s910 = t.read910(tags.get(910));
            if(s910!=null) {
                mfnList.add(name+"="+mfn+" " + s910);
                continue;
            }
//            bindexes[bmaxindex][0]++;
            if(t.getCount()<=0) {
                //mfnList.add(name+"="+mfn+" без экземпляров.");
//                bindexes[bmaxindex][2]++;
                continue;
            } // нечего выдавать студентам
            n[1]+=t.getCount();
//            bindexes[bmaxindex][1] += book.count;
            List<Field> fields691 = tags.get(691);
            if(fields691==null) continue; // это не учебник
//            book.bindex = bindex;
//            bindexes[book.bindex][0]++;
//            bindexes[book.bindex][1] += book.count;
//            book.read(tags);
//            fondbooks[book.bindex].add(book); // все учебники библиотеки
            ++n[2];
//            n[6] += book.count;
//            for(Field f:fields691)
//                book.read691d(f,map691,mfn);
//                    //++n[4];
//            if(map691.isEmpty()) continue; // нет активных дисциплин
//            book.add691(map691); // привязка книг и спроса друг к другу
//            map691.clear();
            n[6] += t.getCount();
//            ++n[3];
        }
        xrf.close();
        System.out.println("\n"+n[0]+" книг перебрано c " + n[1] + " экземплярами");
        //System.out.println(""+n[1]+" учебников найдено");
        System.out.println(""+n[2]+" учебников с " + n[6] + " экземплярами");
        //System.out.println(""+n[3]+" учебников по активным дисциплинам");
        //System.out.println(""+n[4]+" 691-х полей повторяются:");
    }
    public boolean checkNewSyn(List<Field> fields69) {
        if(fields69.isEmpty()) return false;
        Field f = fields69.get(0);
        String s = f.getSubsUpper('\u0000');
        if(s == null) return false;
        if(!s.startsWith("?"))
            return false;
        int year4s = Integer.valueOf(syear.substring(0, 4));
        newSynMap = new TreeMap<>();
        for(int i=1; i<fields69.size(); i++){
            String sf = fields69.get(i).getSubsUpper('\u0000');
            int ieq = sf.indexOf('=');
            if(ieq<1)
                System.out.println("непонятный цикл в синониме новизны: "+sf);
            try {
                String sy = ""+(year4s - Integer.parseInt(sf.substring(ieq+1)));
                newSynMap.put(sf.substring(0, ieq), sy);
            } catch(Exception e) {
                System.out.println("непонятное число в синониме новизны: "+sf);
            }
        }
        return true;
    }
/**
 * Чтение синонимов из БД Книгообеспеченность ред.2019-12-03
 * @throws IOException 
 */
    public void readSyns() throws IOException {
//        synMap = new TreeMap<>();
//        if(!xrfIndex.containsKey("SYN")) {
//            System.out.println("*** нет ни одного синонима");
//            return;
//        }
        for(int mfn:trash2.get920("SYN")) {
            List<Field> fields69 = trash2.read(mfn).get(69);
            if(fields69==null) continue;
            if(checkNewSyn(fields69)) continue;
            syn2(fields69,mfn);
//            Set<String> syn1set = new TreeSet<>();
//            String syn1 = null;
//            for(Field f:fields69) {
//                String s = f.getSubs().get('\u0000');
//                if(s==null) continue;
//                s = Words.normalizeSpaces(s).toUpperCase();
//                if(s.isEmpty()) { // пустые пропускаем
//                } else if(syn1==null) { // первый синоним
//                    String s1 = synMap.get(s); // не перенаправлен ли?
//                    syn1 = (s1==null) ? s : s1;
//                    syn1set.add(syn1);
//                } else if(s.equals(syn1)) { // записан сам в себя
//                } else if(syn1set.remove(s)) { // перекладываем вложеный синоним
//                    for(Map.Entry<String,String> e:synMap.entrySet())
//                        if(e.getValue().equals(s))
//                            e.setValue(syn1);
//                } else if(synMap.containsKey(s)) { // уже привязан
//                    System.out.println("*** отброшен синоним "+s);
//                    System.out.println("\t из первого синонима "+syn1);
//                    System.out.println("\t т.к. он уже привязан к "+synMap.get(s));
//                } else synMap.put(s, syn1); // регистрируем
////                String syn1s = synMap.get(s);
////                if(syn1 == null) { // регистрируем первый синоним
////                    if(syn1s != null) {
////                        syn1 = syn1s;
////                        System.out.println("вложение первого синонима="+s);
////                    }
////                    else {
////                        syn1 = s;
////                        if(synMap.containsValue(s))
////                            System.out.println("найден дубль первого синонима="+s);
////                    }
////                } // далее проверки непервого синонима
////                else if(syn1s != null)
////                    System.out.println("*** отброшен дубль непервого синонима="+s);
////                else if(s.equals(syn1))
////                    System.out.println("*** отброшен синоним самого себя="+s);
////                else {
////                    synMap.put(s,syn1);
////                    if(!synMap.containsValue(s)) continue;
////                    System.out.println("довложение первого синонима="+s);
////                    for(Map.Entry<String,String> e:synMap.entrySet())
////                        if(e.getValue().equals(s))
////                            e.setValue(syn1);
////                }
//            }
        }
        if(newSynMap == null) {
            System.out.println("*** не нашел синонима, начинающегося со знака вопроса");
        } else {
            System.out.println("? новизна по циклам дисциплин:");
            for(Map.Entry<String,String> e:newSynMap.entrySet())
                System.out.println(e.getKey()+"\t"+e.getValue());
        }
    }
/**
 * Чтение учебных планов из БД Книгообеспеченность ред.2019-12-03
 * @throws IOException 
 */
    public void readCurrs() throws IOException {
        for(int mfn:trash2.get920("CURR")) {
            Curr2 curr = new Curr2(mfn);
            curr.read(trash2.read(mfn));
            currMap.put(curr.key, curr);
        }
    }
    private Map<String,Map<String,Map<String,List<Book2>>>> xb() {
        System.out.println("xb() started. "+(new Date()));
        Map<String,Map<String,Map<String,List<Book2>>>> m = new TreeMap<>();
        for(int bindex=0; bindex<fondbooks.length; ++bindex) {
            String sb = TextBook.textbooks[bindex];
            Map<String,Map<String,List<Book2>>> m1 = null;
            for(Book2 b:fondbooks[bindex]) {
                if(m1 == null)
                    m.put(sb, m1 = new TreeMap<>());
                String s = ""+b.t.getYear();
                Map<String,List<Book2>> m2 = m1.get(s);
                if(m2 == null) m1.put(s, m2 = new TreeMap<>());
                s = b.t.getRecommend();
                if(s==null) s="";
                List<Book2> l3 = m2.get(s);
                if(l3 == null) m2.put(s, l3 = new ArrayList<>());
                l3.add(b);
            }
        }
        return m;
    }
    private void booksHtml() throws FileNotFoundException, IOException {
        Map<String,Map<String,Map<String,List<Book2>>>> m = xb();
        List<String> html = newHtml("Учебный фонд");
        html.add("Структура фонда учебной литературы. "+new Date());
        html.add("<br>Нарастающим итогом по году издания");
        html.add("<br>Развернуто по грифу = экземпляров / названий");
        html.add("<br>% = доля экземпляров с грифом");
        html.add("<br>Итого по типу учебной литературы");
        html.add("<br><table><tr valign=\"top\">");
        for(int bindex=0; bindex<bmaxindex; ++bindex) {
            String sb = TextBook.textbooks[bindex];
            Map<String,Map<String,List<Book2>>> m1 = m.get(sb);
            if(m1==null) continue;
            html.add("<td>");
            html.addAll(printBooks(sb,m1));
        }
        html.add("</table>");
        endHtml(html);
        FileOutputStream f = new FileOutputStream(syear+"\\books.htm");
        for(String line:html)
            f.write(line.getBytes(charset));
        f.close();
    }
    private List<String> printBooks(String tt, Map<String,Map<String,List<Book2>>> x){
        Set<String> set = new TreeSet<>();
        for(Map<String,List<Book2>> m:x.values())
            set.addAll(m.keySet());
        List<String> ss = new ArrayList<>();
        ss.add("<table border=1 cellspacing=0>");
        ss.add("<tr><td>г.и.");
        for(String s:set) ss.add("<td>"+(s==null ? "" :s));
        ss.add("<td>%");
        ss.add("<td>"+tt);
        int[][] ints2 = new int[set.size()][3];
        String[] years  = x.keySet().toArray(new String[0]);
        for(int i=years.length-1;i>=0;--i){
            String skey = years[i];
            ss.add("<tr><td>"+skey);
            int iint=0;
            for(String s:set) {
                int[] ints = ints2[iint++];
                List<Book2> bs = x.get(skey).get(s);
                if(bs!=null && !bs.isEmpty()) {
                    for(Book2 b:bs) {
                        ints[0]+=b.t.getCount();
                        ints[1]++;
                        if(!s.isEmpty())ints[2] += b.t.getCount();
                    }
                    ss.add("<td>"+ints[0]+" / "+ints[1]);
                }
                else ss.add("<td>&nbsp");
            }
            int[] ints3 = new int[3];
            for(int[] ints:ints2)
                for(int i2=0; i2<ints.length; ++i2)
                    ints3[i2]+=ints[i2];
            if(ints3[2]==0) ss.add("<td>&nbsp");
            else ss.add("<td>"+(int)Math.round(ints3[2]*(float)100/ints3[0]));
            ss.add("<td>"+ints3[0]+" / "+ints3[1]);
        }
        ss.add("</table>");
        return ss;
    }
    private List<String> newHtml(String title) {
        List<String> html = new ArrayList<>();
        html.add("<html><head>");
        html.add("<title>"+title+"</title>");
        html.add("<meta http-equiv=\"Content-Language\" content=\"ru\">");
        html.add("<meta http-equiv=\"Content-Type\" content=\"text/html; charset="+charsetName+"\">");
        html.add("</head><body>");
        return html;
    }
    private void endHtml(List<String> html) {html.add("</body></html>");}
/**
 * ko.html ред.2019-12-03
 * @throws FileNotFoundException
 * @throws IOException 
 */
    private void indexHtml() throws FileNotFoundException, IOException {
        List<String> html = newHtml("Расчет КО "+syear);
        html.add("Расчет книгообеспеченности");
        html.add(" на "+syear+" уч.год "+new Date()+"<br>");
//        html.add("<br><a href=\"sems.html\">Подготовка Таблицы № 3</a>");
        html.add("<br><a href=\"jconsole.txt\">консоль запуска программы расчета</a>");
        html.add("<br><a href=\"mfnerr.txt\">"+mfnList.size()+" учебников c ошибками оформления</a>");
        html.add("</br>");
        html.addAll(bindexesTable());
        endHtml(html);
        try (
            FileOutputStream f = new FileOutputStream(syear+"\\ko.html");
        ) {
            for(String line:html)
                f.write(line.getBytes(charset));
        }
    }

    private void syn2(List<Field> fields69, int mfn) {
        Set<String> keys = new TreeSet<>();
        Set<Integer> mfns = new TreeSet<>();
        for(Field f:fields69) {
            String s = f.getSubs().get('\u0000');
            if(s==null) continue;
            s = normalizeKey(normalizeText(s));
            if(s.isEmpty()) continue;
            Integer mfn2 = syn2mfn.get(s); // в каких синонимах еще встречается
            if(mfn2==null) {
                keys.add(s);
            } // уникальные складываем сразу
            else {
                mfns.add(mfn2);
            } // дубли пока откладываем
        }
        for(Integer mfn2:mfns) {
            System.out.println("syn "+mfn2+" was eaten by "+mfn);
            for(String s:mfn2syn.remove(mfn2)) {
                keys.add(s);
            } // списки синонимов-дублей добавляем полностью
        }
        if(keys.isEmpty()) return;
        mfn2syn.put(mfn,keys);
        for(String key:keys) {
            syn2mfn.put(key, mfn);
        }
    }
    class Book2 {
/**
 * библиографическая запись учебника
 */
        private TextBook t;
/**
 * ленивый счетчик спроса по полугодиям (в % т.е. в 100 раз больше)
 * (БЕЗ учета спроса как дополнительной литературы)
 * -1 не подходит ни одной строке плана
 *  0 не подходит ни одной группе
 * >0 скольким студентам подходит * 100
 */        
        private int[] y2counts = null;
//        private float[] countingents = new float[2 * y2count];
        private Book2(TextBook t) {this.t = t;}
/**
 * deprecated
 */        
//        private double getKO(int sem2index) {return t.getCount()*100/countingents[sem2index];}
/**
 * ленивая книгообеспеченность (-1 если спрос остался нетронутым)
 * @param y2index
 * @return -1,0 или книгообеспеченность в %
 * желательно округлять до большего процента Math.ceil()
 */        
        private double ko(int y2index) {
            if(y2counts == null) return -1;
            int y2count = y2counts[y2index];
            if(y2count <= 0) return y2count;
            return (double)10000 * t.getCount() / y2counts[y2index];
        }
/**
 * построить выходное поле с учебником
 * @param demand спрос по полугодиям
 * @return готовое поле
 */        
        private Field newField(int[] demand) {
            Field f = t.newField();
            if(demand != null)
                for(int y2index=0; y2index<y2count; y2index++)
                    if(demand[y2index] >= 0)
                        f.put((char)('l'+y2index), ""+(int)Math.ceil(0.01*demand[y2index]));
            if(y2counts != null) 
                for(int y2index=0; y2index<y2count; y2index++)
                    if(y2counts[y2index] >= 0)
                        f.put((char)('i'+y2index), ""+(int)Math.ceil(0.01*y2counts[y2index]));
            return f;
        }
        private Field newField(double[] m, double m0) {
            Field f = new Field();
            m[0] += m0;
            Map<Character,String> subs = f.getSubs();
            subs.put('a', t.getId());
            subs.put('b', ""+t.getCount());
            subs.put('e', t.getYear());
            if(t.getRecommend()!=null) {
                m[2] += m0;
                subs.put('f', t.getRecommend());
            }
            if(t.getText()!=null) subs.put('x', t.getText());
            //if(title!=null) subs.put('y', title);
            if(t.getHttp()!=null) {
                m[1]++;
                subs.put('z', t.getHttp());
            }
            return f;
        }
/**
 * включить 691 поле в карту фильтров по дисциплинам
 * @param f повторение 691 поля
 * @param map заполняем дисциплины фильтрами или ставим им null что фильтров нет
 * @param mfn для вывода диагностики
 */
        private int read691d(Map<Character,String> subs
                , Map<Disc2,List<String[][][]>> m
                , int mfn
                , Disc2 d2
                , String sdisc) {
            List<String[][][]> list = m.get(d2);
            if(m.containsKey(d2) && list==null) // фильтры были сброшены
                return 0;
            String[][][] s3 = prepare691(subs);
            if(s3==null){ // фильтры надо сбрасывать
                m.put(d2, null); // сброс всех фильтров
                return -1;
            }
            if(m.containsKey(d2)) { // будем добавлять в список
                for(String[][][] s3item:list) {
                    if(!equals691(s3,s3item)) continue;
                    System.out.println("***mfn="+mfn+". Дубль 691^d="+sdisc);
                    return 0; // такой фильтр уже в списке
                }
            } else { // списка еще нет - надо создавать
                m.put(d2, list = new ArrayList<>());
            }
            list.add(s3);
            return 1;
        }
/**
 * проверка непустого фильтра на дубль
 * @param s3 новый фильтр
 * @param s3item сравнить с другим непустым фильтром
 * @return совпадает?
 */
        private boolean equals691(String[][][] s3, String[][][] s3item) {
            for(int i1=0; i1<s3.length; ++i1) {
                String[][] s2 = s3[i1];
                String[][] s2item = s3item[i1];
                if(s2==null) {
                    if(s2item==null) continue;
                    return false;
                }
                else if(s2item==null) return false;
                for(int i2=0; i2<s2.length; ++i2)
                    if(!Arrays.equals(s2[i2], s2item[i2]))
                        return false;
            }
            return true;
        }
/**
 * раздача учебника подходящим семестро-строко-плано-группам
 * @param dopindex 0-как основной учебник 1-как дополнительный учебник
 * @param disc2 дисциплина
 * @param d691 массив из v691 по этой дисциплине
 */
        private void linkDisc2(int dopindex, Disc2 disc2, List<String[][][]> d691) {
//            if(t.getMfn()==21208) 
//                System.out.println("mfn="+t.getMfn());
            if(d691==null) {// подходит по всем планам, их семестрам и группам
                disc2.addBook(dopindex, this); // учебник подходит всем по этой дисциплине
                y2inc(dopindex,disc2);
                return;
            }     
            int hits = 0; // счетчик попаданий учебника
            Set<Integer>[] g3s = null; // метки[cd.d2index] семестро-групп
bycd:       for(Curr2.CurrDisc2 cd:disc2.cdList) { //по каждой строке-плана этой дисциплины
                int[] i691 = null; // гребенка
                int index691=-1;
                for(String[][][] s3:d691) {
                    ++index691;
                    if(!check691(cd.curr2this().s691,s3[2])) continue;
                    if(!check691(cd.s691,s3[1])) continue;
                    if(s3[0]==null && s3[3]==null) {
                        y2inc(dopindex, cd); // учебник подходит всем семестрам
                        continue bycd;
                    }
                    if(i691==null) i691 = new int[d691.size()];
                    i691[index691] = 1;
                }
                if(i691==null) { // гребенка пуста
                    if(g3s==null)
                        g3s = new Set[disc2.cdList.size()]; // not disc2
                    g3s[cd.d2index] = g3empty; // not cd
                    continue;
                }
                boolean semfull = true;
                Set<Integer> g3indexes = new TreeSet<>(); // будем искать
forsem:         for(int sem:cd.sems) {
                    List<Group2.GroupCurr2> gcs = cd.getSemGroupCurrs(sem);
                    int groupsleft = -1; // ни семестра ни групп
                    String ssem = null; // строковое представление семестра
                    index691 = 0; // перебирать гребенку с самого начала
                    for(String[][][] s3:d691) { //прогоняем все фильтры
                        if(i691[index691++]==0) continue; //по гребенке дисциплины
                        if(s3[0]!=null) { //задан фильтр семестра
                            if(ssem==null) ssem=""+sem; 
                            if(!check691unit(ssem, s3[0][0])) continue;
                        }
                        if(groupsleft<0) { // семестр еще не зафиксирован?
                            y2inc(dopindex, cd, sem);
                            if(gcs==null) { //групп нет на этом курсе вообще
                                g3indexes.add(sem); 
                                continue forsem;
                            }
                            groupsleft=gcs.size(); //фиксируем семестр как подходящий
                        }
                        for(Group2.GroupCurr2 gc:gcs) {
                            if(!check691(gc.group2this().s691, s3[3])) continue;
                            if(g3indexes.add(sem + g3max*(gc.xmlid+1))) { 
                                y2inc(dopindex, cd, sem, gc);
                                if(--groupsleft==0) { // семестр полный получился
                                    continue forsem;
                                }
                            }
                        }
                    }
                    semfull = false; // семестр неполный получился
                    if(groupsleft<0) continue; // семестр не прошел
                    if(groupsleft<gcs.size()) continue; // есть подходящие группы
                    g3indexes.add(sem); 
                }
                if(semfull) continue; // полный не надо записывать в g3
                if(g3s==null)
                    g3s = new Set[disc2.cdList.size()]; // not disc2
                g3s[cd.d2index] = g3indexes; // not cd
            }
            if(g3s==null) {// никто на фильтрах не пострадал
                disc2.addBook(dopindex, this);
                return;
            }
            for(Curr2.CurrDisc2 cd:disc2.cdList) {
                Set<Integer> g3keys = g3s[cd.d2index];
                if(g3keys==null) {
                    ++hits;
                    cd.addBook(dopindex, this);
                } else if(!g3keys.isEmpty()) {
                    ++hits;
                    cd.addBook3(dopindex,g3keys,this);
                }
            }
            if(hits==0) // учебник так никуда и не попал
                disc2.addBook0(dopindex, this);
        }
/**
 * складываем группу в книгу 
 * @param dopindex
 * @param sem
 * @param gc
 * @param cd 
 */
        private void y2inc(int dopindex, Curr2.CurrDisc2 cd, int sem, Group2.GroupCurr2 gc) {
            if(dopindex!=0) return; // только для основной литературы
            if(!t.checkNew(cd.y)) return; // только для подходящего года
            if(y2counts==null)
                y2counts = y2new();
            int y2index = (sem-1) % y2count;
            if(y2counts[y2index] < 0)
                y2counts[y2index] = gc.count * cd.percentage;
            else y2counts[y2index] += gc.count * cd.percentage;
        }
/**
 * складываем всю дисциплину в книгу
 * @param dopindex
 * @param d2 
 */
        private void y2inc(int dopindex, Disc2 d2) {
            if(dopindex!=0) return; // только для основной литературы
            int i = Arrays.binarySearch(d2.ysorted, t.getYear());
            if(i < 0) {
                if(++i == 0) return;
                i = -i - 1;
            }
            int[] y2new = Ko2.y2inc(y2counts, d2.y2sorted[i]);
            if(y2new != null) y2counts = y2new;
        }
/**
 * складываем семестр без групп в книгу
 * @param dopindex
 * @param sem
 * @param cd 
 */        
        private void y2inc(int dopindex, Curr2.CurrDisc2 cd, int sem) {
            if(dopindex!=0) return; // только для основной литературы
            if(!t.checkNew(cd.y)) return; // только для подходящего года
            if(y2counts==null)
                y2counts = y2new();
            int y2index = (sem-1) % y2count;
            if(y2counts[y2index] < 0)
                y2counts[y2index] = 0;
        }
/**
 * складываем все семестр-группы в книгу
 * @param dopindex
 * @param sem
 * @param cd 
 */        
        private void y2inc(int dopindex, Curr2.CurrDisc2 cd) {
            if(dopindex!=0) return; // только для основной литературы
            if(!t.checkNew(cd.y)) return; // только для подходящего года
            int[] y2new = Ko2.y2inc(y2counts, cd.y2Counts);
            if(y2new == null) return;
            y2counts = y2new;
        }

    }
    class Disc2 {
/**
 * список задетых синонимов
 */        
        private Set<String> set59 = new TreeSet<>();
/**
 * массив ленивых списков учебников уроння дисциплины
 */
        private List<Book2>[] books;
        private List<Book2>[] books0;
/**
 * id
 */        
        private int xmlid;
//        private Map<Book2,Integer>[] bookmaps = new Map[2];
/**
 * первый синоним дисциплины в верхнем регистре
 */        
        private String key;
//        private int gcounter = 0;
/**
 * сборник контингента дисциплинны умноженного на проценты по полугодиям
        private int[] y2Counts = new int[y2count];
 */        
/**
 * список строк учебных планов с этой дисциплиной
 */        
        final private List<Curr2.CurrDisc2> cdList = new ArrayList<>();
/**
 * добавить книгу в список подходящих всем по дисциплине
 * и сделать корректировку спроса в ней
 * @param dopindex 0-основная 1-дополнительная
 * @param b учебник
 */        
        private void addBook(int dopindex, Book2 b) {
            List<Book2>[] new2 = addBook2(books, dopindex, b);
            if(new2 != null) books = new2;
        }
/**
 * добавить книгу в список никому не подходящих по дисциплине
 * @param dopindex 0-основная 1-дополнительная
 * @param b учебник
 */        
        private void addBook0(int dopindex, Book2 b) {
            List<Book2>[] new2 = addBook2(books0, dopindex, b);
            if(new2 != null) books0 = new2;
        }
        String[] ysorted;
        int[][] y2sorted;
        private Disc2(String key) {
            this.key = key;
            xmlid = discMap.size()+1;
        }
        private Disc2(Curr2.CurrDisc2 cd2) {
            this.key = "";
//            xmlid = 0;
//            cdList = new ArrayList<>();
            cdList.add(cd2);
        }
//        private Field newField(String s60b) {
//            Field f = new Field();
//            f.put('a', syear);
//            f.put('b', s60b);
//            return f;
//        }
        private Field newField(String sem2text, double dcountingent, int ng) {
            Field f = Field.create('b', key);
            f.put('a', sem2text);
            f.put('c', ""+(int)Math.round(dcountingent)+"/"+ng);
            return f;
        }
        private Field newField(String sem2text, double dcountingent, int ng, float[] kos) {
            Field f = newField(sem2text,dcountingent,ng);
            char ch='i';
            for(int bindex=0; bindex<kos.length; ++bindex)
                f.put(ch++, ""+(int)Math.round(kos[bindex]/dcountingent));
            return f;
        }
/**
 * генерируем спрос по дисциплине
 * из спросов на строки планов
 */        
        private void sortcd() {
            Map<String,int[]> ymap = new TreeMap<>();
            for(Curr2.CurrDisc2 cd:cdList) {
                int[] y2disc = ymap.get(cd.y);
                if(y2disc==null)
                    ymap.put(cd.y, y2disc = y2new());
                y2inc(y2disc, cd.y2Counts);
            }
            ysorted = new String[ymap.size()]; // год цикла дисциплин
            y2sorted = new int[ysorted.length][]; // посеместрные контингенты
            int i=0;
            for(Map.Entry<String,int[]> e:ymap.entrySet()){
                ysorted[i] = e.getKey();
                y2sorted[i] = e.getValue();
                if(i > 0)
                    y2inc(y2sorted[i], y2sorted[i-1]);
                ++i;
            }
        }
/**
 * содержание выходного xml для дисциплины
 * @return XLines
 */
        private void xml2() throws IOException {
            XLines x = koxml;
            x.xml1("d",XLines.attr("xml:id", "X"+xmlid));
            if(!key.isEmpty()) x.xml("syn", key);
            xmlBooks(x.newline(),books);
            x.xml2("d");
            x.write(); 
        }
        private Map<Integer, List<Field>> tags59(final String s59, final String s)
                throws IOException {
            Map<Integer, List<Field>> tags = new TreeMap<>(); 
            Field.tagsAdd(tags, 920, Field.create("SUBJ"));
            Field.tagsAdd(tags, 903, Field.create(s));
            Field.tagsAdd(tags, 59, Field.create(s59));
            return tags;
        }
        private Map<Integer, List<Field>> xmlTags() throws IOException {
//            String[] a59 = set59.toArray(new String[0]);
//            Arrays.sort(a59);
            XLines x = koxml;
            x.xml1("d",XLines.attr("xml:id", "D"+xmlid));
//            x.xml("syn", a59[0]);
            for(String s59:set59)
                x.newline().xml("syn", s59);
            xmlBooks(x.newline(),books);
            Map<Book2, int[]>[] bmaps = new Map[bmaxindex+1]; // учебники
            List<Field> fs = new ArrayList<>();
            long[] k = new long[y2count+bmaxindex+1]; // сборник студентов по полугодиям
            long[][] bmin2 = new long[bmaxindex][];
            for(int bindex=0; bindex<bmaxindex; bindex++){
                k[y2count+bindex] = -1; // инициализируем минимумы ко по виду литературы
                bmin2[bindex] = new long[] {Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE};
            }
            if(cdList!=null)
                for(Curr2.CurrDisc2 cd:cdList) 
                    cd.xml61(x, bmaps, fs, k, bmin2);
            if(books0!=null) {
                x.newline().xml1("b0");
                xmlBooks(x,books0);
                x.xml2("b0");
                int dopindex = 0;
                for(List<Book2> bs:books0) {
                    if(bs != null) {
                        int bindex0 = (dopindex==0) ? 0 : bmaxindex;
                        for(Book2 book2:bs) {
                            int bindex = bindex0 + book2.t.getBIndex();
                            if(bindex>bmaxindex) {
                                System.out.println(" методичка в доплитературе! "+book2.t.getId());
                                bindex = bmaxindex;
                            }
                            Map<Book2, int[]> bmap = bmaps[bindex];
                            if(bmap == null) bmaps[bindex] = bmap = new HashMap<>();
                            bmap.put(book2, null);
                        }
                    }
                    ++dopindex;
                }
            }
            x.xml2("d");
            x.write(); 
            for(Field f:fs)
                f.getSubs().remove('g'); //too many bytes !
            Collections.sort(fs, KO2_61_ORDER);
            Map<Integer, List<Field>> tags = new TreeMap<>(); 
            tags.put(61, fs); // строки-планов и группы
            int tag = 61;
            for(Map<Book2, int[]> bmap:bmaps) {
                ++tag;
                if(bmap==null) continue;
                if(bmap.isEmpty()) continue;
                Map.Entry<Book2, int[]>[] es = bmap.entrySet().toArray(new Map.Entry[0]);
                Arrays.sort(es, KO2_BOOK2_ORDER);
                tags.put(tag, fs = new ArrayList<>());
                int n[] = new int[2]; //{экз, эи}
                String y0 = null;
                Field f = null;
                for(Map.Entry<Book2, int[]> e:es) {
                    Book2 book2 = e.getKey();
                    TextBook t = book2.t;
                    String y1 = t.getYear();
                    if(f != null && !y1.equals(y0)) {
                        f.put('p', "" + n[0] + ((n[1] == 0) ? "" : "+"+n[1]));
                    }
                    y0 = y1;
                    f = book2.newField(e.getValue());
                    fs.add(f);
                    int tcount = t.getCount();
                    n[0] += tcount;
                    if(t.getHttp()!=null) n[1]++;
                }
                f.put('p', "" + n[0] + ((n[1] == 0) ? "" : "+"+n[1]));
            }
            Iterator<String> i59 = set59.iterator();
            String s60b = i59.hasNext() ? i59.next() : key;
            if(i59.hasNext()) {
                fs = new ArrayList<>();
                do {
                    fs.add(Field.create(i59.next()));
                } while(i59.hasNext());
                tags.put(59, fs); // синонимы
            }
            Field f = new Field();
            f.put('a', syear);
            f.put('b', s60b);
            f.put('z', m4.get(s60b));
            for(int y2index=0; y2index<y2count; y2index++)
                if(k[y2index]>0)
                    f.put((char)('c'+y2index), ""+(long)Math.ceil(0.01*k[y2index]));
            for(int bindex=0; bindex<bmaxindex; bindex++){
                long[] bmin = bmin2[bindex];
                String s;
                if(bmin[0]==Long.MAX_VALUE)
                    s = "0";
                else {
                    s = "" + bmin[0];
                    if(bmin[1]>0) s += "+" + bmin[1];
                    if(bmin[2]<Long.MAX_VALUE) s += "=" + bmin[2];
                }
                f.put((char)('i'+bindex), s);
                if(bmin[3]<Long.MAX_VALUE) f.put((char)('f'+bindex), ""+bmin[3]);
            }
            Field.tagsAdd(tags, 60, f);
            Field.tagsAdd(tags, 920, Field.create("KO2"));
            return tags;
        }
/**
 * содержание выходного xml для дисциплины
 * @return XLines
 */
        private Map<Integer, List<Field>> xmlTags2() throws IOException {
            XLines x = koxml;
            x.xml1("d",XLines.attr("xml:id", "D"+xmlid));
            x.xml("syn", key);
            for(String s59:set59)
                x.newline().xml("syn", s59);
            List<Field> fs = new ArrayList<>();
            long[] k = new long[y2count+bmaxindex+1]; // сборник студентов по полугодиям
            for(Curr2.CurrDisc2 cd:cdList) 
                cd.xml61(x, fs, k);
            x.xml2("d");
            x.println();
            x.write(); 
// далее работаем только с tags
            Collections.sort(fs, KO2_61_ORDER);
            Map<Integer, List<Field>> tags = new TreeMap<>(); 
            tags.put(61, fs); // строки-планов и группы
            Field f = new Field();
            f.put('a', syear);
            f.put('b', key);
            Field.tagsAdd(tags, 60, f);
            Field.tagsAdd(tags, 920, Field.create("KO2"));
//            System.out.println();
//            Field.tagsOut(tags, System.out);
            return tags;
        }
    }
    class Group2 {
/**
 * количество лет обучения нруппы (максимальный номер курса)
 */        
        private int years;
/**
 * год поступления (четырехзначный)
 */        
        private int year0;
/**
 * курс обучения (=1 в год поступления)
 */        
        private int course;
//        private String f;
/**
 * наименование группы
 */
        private String key;
        private String[] s691;
        private String attr691;
        private Map<Character,String> sub21;
//        private List<GroupCurr2> gcs = new ArrayList<GroupCurr2>();
//        private String curr2s;
//        private Group2(String key) {this.key = key;}
        class GroupCurr2 {
            private Map<Character,String> sub22;
            private int xmlid;
            private Curr2 curr2;
            private int count;
            private Group2 group2this() {return Group2.this;}
            private String attr() {
                String attr = XLines.attr("xml:id","G"+curr2.xmlid+"-"+xmlid);
                attr += XLines.attr("n", ""+count);
                attr += XLines.attr("y", ""+year0);
                attr += attr691;
//                attr += XLines.attr("v20f", s691[0]);
//                attr += XLines.attr("v20k", s691[1]);
                attr += XLines.attr("v21f", sub21.get('f'));
                attr += XLines.attr("v21i", sub22.get('i'));
                attr += XLines.attr("v21j", sub21.get('j'));
                attr += XLines.attr("v21l", sub21.get('l'));
                attr += XLines.attr("v21m", sub21.get('m'));
                attr += XLines.attr("v21n", sub21.get('n'));
                attr += XLines.attr("v22x", sub22.get('x'));
                attr += XLines.attr("v22y", sub22.get('y'));
                attr += XLines.attr("v22z", sub22.get('z'));
                return attr;
            }
            private Field newField(Curr2.CurrDisc2 cd) {
                Group2 g = Group2.this;
                Field f = cd.newField(g.course);
                Map<Character,String> fsubs = f.getSubs();
                fsubs.put('a', g.key);
                fsubs.put('b', ""+count);
                fsubs.put('x', ""+years);
                return f;
            }
        }
/**
 * регистрация в группе правильной строки с количеством студентов и планом
 * @param count количество студентов
 * @param curr2 их учебный план
 */        
        private void newGroupCurr2(int count, Curr2 curr2, Map<Character,String> z) {
            if(curr2 == null) curr2 = getEmptyCurr(); 
            GroupCurr2 gc = new GroupCurr2();
            gc.count = count;
            gc.curr2 = curr2;
            gc.sub22 = z;
            Map<Curr2.CurrDisc2,int[]> cdmap = curr2.addGroup(gc);
            for(Map.Entry<Curr2.CurrDisc2,int[]> e:cdmap.entrySet()) {
                Curr2.CurrDisc2 cd = e.getKey();
                int[] y2s = e.getValue();
                if(y2s == null) return; // отсекаем пустую дисциплину
                cd.addGroupCounts(count, y2s);
            }
        }
/**
 * зачитать число лет обучения
 * @param tags отсюда
 * @return округлено в большую сторону
 */        
        private int read21m(Map<Integer,List<Field>> tags) {
            String s = Field.getTagsSub(tags,21,'m');
            if(s == null) return 0;
            s = s.split(" ",2)[0];
            s = s.replace(',', '.');
            try {
                return (int)Math.ceil(Double.parseDouble(s));
            }
            catch(Exception e) {
                return 0;
            }
        }
/**
 * зачитать группу
 * @param tags отсюда
 * @return null если все в порядке или объяснение почему не взяли
 * @throws IOException 
 */        
        private void read(Map<Integer,List<Field>> tags) throws IOException {
            Field f21 = tags.get(21).get(0);
            sub21 = f21.getSubs();
            key = Field.getTagsSub(tags,21,'b'); 
            year0 = readInt(Field.getTagsSub(tags,21,'a')); // год поступления
            if(year0 > year4) System.out.println(key+" еще не учатся");
            course = year4 - year0 + 1; // курс обучения
            years = read21m(tags); // лет обучения
            if(years > 0 && course > years)  System.out.println(key+" уже не учатся");
//            sem0 = y2count * year0;
            s691 = new String[] 
                {Field.getTagsSubUpper(tags,20,'f')
                ,Field.getTagsSubUpper(tags,20,'k')
                };
            for(String s:s691) {
                String fars = Words.farCheck(s);
                if(fars!=null) System.out.println(key+" иностранная буква: "+fars);
            }
            attr691 = XLines.attr("v20f",Field.getTagsSub(tags, 20, 'f'));
            attr691 += XLines.attr("v20k",Field.getTagsSub(tags, 20, 'k'));
        }
    }
/**
 * класс распределения студентов по дисциплинам в группе выбора
 */    
    class Choice2 {
        private String key;
        private int n=0;
        private float percentage0 = 100;
        /**
         * список семестров для группы выбора
         */
        private int[] sems=null;
        private List<Curr2.CurrDisc2> list0 = new ArrayList<>();
        private Choice2(String key) {this.key = key;}
        private String add(Curr2.CurrDisc2 cd) {
            ++n;
            if(sems==null) sems = cd.sems;
            else if(sems.length != cd.sems.length)
                System.out.println(cd.text()+" не совпадает количество семестров по группе выбора="+key);
            if(cd.percentage==0) list0.add(cd);
            else {
                percentage0 -= cd.percentage;
                if(percentage0<0)
                    return cd.text()+" превышено 100% по группе выбора="+key;
            }
            return null;
        }
        private String set() {
            int n0 = list0.size();
            if(n0==0 && percentage0>0)
                return(" остались лишние проценты по группе выбора="+key);
            if(n==1)
                System.out.println(list0.get(0).text()+" ТОЛЬКО ОДНА альтернатива по группе выбора="+key);
            for(Curr2.CurrDisc2 cd:list0)
                cd.percentage = Math.round(percentage0/n0);
            return null;
        }
    }
    class Curr2 {
        private String y;
/**
 * порядковый номер учебного плана для xml
 */
        private int mfn;
        private int xmlid;
        private String txt() {return "CURR="+key;}
        private String text(int tag) {return "CURR="+key+"/"+cs[tag-7];}
/**
 * ленивые списки групп по курсам
 * индекс = [курс]
 */        
        private List<Group2.GroupCurr2>[] courseGroups;
/**
 * ленивые карты дисциплина = семестры[полугодия] 
 * индекс = [курс]
 */        
        private Map<CurrDisc2,int[]>[] courseDiscs;
/**
 * ленивые подитоги контингента плана по курсам
 * индекс = [курс]
 */        
//        private int[] courseCounts;
/**
 * максимальный курс дисциплин плана
 */        
        private int maxCourse;

        private CurrDisc2 emptyCurrDisc2;
        private CurrDisc2 getEmptyCurrDisc() {
            if(emptyCurrDisc2 == null) {
                emptyCurrDisc2 = new CurrDisc2(0);
                getEmptyDisc(emptyCurrDisc2);
            }
            return emptyCurrDisc2;
        }
/**
 * класс строки дисциплины учебного плана
 */        
        class CurrDisc2 {
/**
 * список подходящих книг для всех семестров и групп
 * [dopindex]
 */            
            private List<Book2>[] books;
/**
 * списки подходящих книг для отдельных семестров и групп
 * ключ = sem + 16 * (gc.xmlid + 1)
 * индекс = [dopindex]
 */            
            private Map<Integer, List<Book2>[]> g3;
/**
 * ленивый спрос этой строчки учебного плана 
 * в % - т.е. в 100 раз больше
 * если -1 значит остался нетронутым
 * [y2index]
 */            
            private int[] y2Counts = y2new();
/**
 * зафиксировать факт изучения этими студентами этой дисциплины 
 * @param gccount количество студентов
 * @param y2s массив по полугодиям семестров изучения этой дисциплины
 * если значение ноль, значит в этом полугодии дисциплина не изучается
 */            
            private void addGroupCounts(int gccount, int[] y2s) {
                int c100 = gccount * percentage;
                for(int y2index=0; y2index<y2count; y2index++) {
                    if(y2s[y2index] == 0) continue;
                    y2Counts[y2index] += c100;
                }
            }
            private Map<CurrDisc2,int[]> emptycdmap(int course) {
                Map<CurrDisc2,int[]> cdmap = new HashMap<>();
                cdmap.put(this, null);
                if(sems==null)
                    sems = new int[] {course};
                else {
                    int i = Arrays.binarySearch(sems, course);
                    if(i < 0) {
                        sems = Arrays.copyOf(sems, sems.length+1);
                        for(int j=-i; j<sems.length; j++)
                            sems[j] = sems[j-1];
                        sems[-i-1] = course;
                    }
                }
                return cdmap;
            }
/**
 * список всех групп изучающих эту дисциплину в заданном семестре
 * @param sem заданный семестр
 * @return список всех изучающих групп
 */            
            private List<Group2.GroupCurr2> getSemGroupCurrs(int sem) {
                Curr2 curr2 = Curr2.this;
                int course = curr2.sem2course(sem);
                List<Group2.GroupCurr2>[] gcsarray = curr2.courseGroups;
                if(gcsarray!=null && gcsarray.length>course)
                    return gcsarray[course];
                return null;
            }
/**
 * добавить книгу в список подходящих всем семестрам
 * @param dopindex как основная=0 или как дополнительная=1
 * @param b учебник
 */            
            private void addBook(int dopindex, Book2 b) {
                List<Book2>[] new2 = addBook2(books, dopindex, b);
                if(new2 != null) books = new2;
            }
/**
 * индекс повторения поля цикла дисциплины в записи учебного плана источника
 */
            private int fno;
/**
 * индекс реальной дисциплины начиная с 1
 */            
            private int xmlid;
/**
 * индекс строки плана в Disc2.cdList
 */            
            private int d2index;
/**
 * для xml
 */
            private String attr;
/**
 * реальный текст источника
 */            
            private String d;
            private Map<Character,String> subs;
/**
 * для фильтров книг
 */            
            private String[] s691;
/**
 * место назначения
 */
            private Disc2 d2;
/**
 * процент студентов для дисциплин по выбору
 */            
            private int percentage;
/**
 * год новизны литературы
 */            
            private String y;
/**
 * список семестров изучения дисциплины
 */
            private int[] sems;
/**
 * индекс массива циклов дисциплин +7
 */            
            private int tag;
/**
 * номер поля - источника
 */            
            private int tag7;
/**
 * уровень предмета
 */            
            private String level;
            private String a;
/**
 * кафедра читающая
 */            
            private String reading;
/**
 * индикатор строки предмета в источнике
 */            
            private String indicator;
            private CurrDisc2(int tag) {
                this.tag7 = tag;
            }
            private Curr2 curr2this() {return Curr2.this;}
            private String txt() {return "v"+tag7+"["+fno+"]";}
            private String text() {return Curr2.this.txt()+"/"+txt();}
/**
 * суммировать книгообеспеченность с учетом года новизны
 * @param books массив списков книг
 * @param sem семестр
 * @param d куда суммировать
 */    
            private void koBooks(List<Book2>[] books, int sem, double[] d) {
                if(books == null) return;
                List<Book2> blist = books[0]; // по основной уч. литературе
                if(blist == null) return;
                int y2index = (sem-1) % y2count; // вычисляем полугодие
                for(Book2 b:blist) {
                    if(b.y2counts == null) continue;
                    int y2count = b.y2counts[y2index]; // общий спрос полугодия
                    if(y2count <= 0) continue;
                    if(!b.t.checkNew(y)) continue; // новизна достаточна?
// считаем отдельно по основной учебной литературе и отдельно по учебно-методической                    
                    d[b.t.getBIndex()] += (double)(b.t.getCount() * 100 * 100 ) / y2count;
                }
            }
            private Field newField(int course) {
                Field f = Curr2.this.newField();
                f.put('c', ""+course);
                if(tag>0) {
                    f.put('e', level);
                    if(percentage!=100)
                        f.put('f', ""+percentage);
                    f.put('h', cs[tag-7]);
                    f.put('s', reading);
                    f.put('y', y);
                }
                return f;
            }
/**
 * составить матрицу фильтров прошедших проверку по дисциплине-строке
 * @param list691 набор полей 691 по этой дисциплине-строке учПлана
 * @return int[] 1=прошел проверку или null если никто или int[0] если все
 */
            private int[] trim691new3(final List<String[][][]> list691) {
                int[] i691 = null;
                int index691=-1;
                for(String[][][] s3:list691) {
                    ++index691;
                    if(!check691(Curr2.this.s691,s3[2])) continue;
                    if(!check691(s691,s3[1])) continue;
                    if(s3[0]==null && s3[3]==null) return iempty;
                    if(i691==null)
                        i691 = new int[list691.size()];
                    i691[index691] = 1;
                }
                return i691;
            }
/**
 * составить матрицу фильтров прошедших проверку вместе с семестрами
 * @param list691 набор полей 691 по этой дисциплине-строке учПлана
 * @return int[как в list691][] семестры, прошедшие проверку или null если никто
 */
            private int[][] trim691new(final List<String[][][]> list691) {
                int[][] i691 = null;
                int index691=-1;
                for(String[][][] s3:list691) {
                    ++index691;
                    if(!check691(Curr2.this.s691,s3[2])) continue;
                    if(!check691(s691,s3[1])) continue;
                    int[] checked = (s3[0]==null) ? sems : check691unit(sems, new Plots(s3[0][0]));
                    if(checked == null) continue;
                    if(i691==null)
                        i691 = new int[list691.size()][];
                    i691[index691] = checked;
                }
                return i691;
            }
            private String read(Field f, Map<String,Choice2> mapChoice) {
                subs = f.getSubs();
                String s = subs.get('c');
                if(s == null) return "нет дисциплины";
                d = normalizeText(s);
                if(d.isEmpty()) return "пустая дисциплина";
                s = Words.farCheck(d);
                if(s!=null) {
                    System.out.println(Curr2.this.key+" иностранная буква: "+s);
                }
                s = subs.get('b');
                if(s==null) return "пустые семестры по дисциплине="+d;
                sems = readSemesters(s);
                if(sems==null) return "неверные семестры="+s+" по дисциплине="+d;
//                if(sems[sems.length-1] >= sems[0]+sems.length)
//                    System.out.println("пропущенные семестры="+s+" по дисциплине="+d);
                reading = subs.get('d');
// цикл дисциплин по новому
                if(!subs.containsKey('z')) { // если цикл явно не задан то по старому
                    indicator = null;
                    tag = tag7;
                }
                else {
                    indicator = subs.get('z').trim().toUpperCase();
                    String cz;
                    int ipoint = indicator.indexOf('.');
                    if(ipoint<0) { // точек нет - все - в цикл
                         cz = indicator;
                         level = "";
                    }else{ // до точки - цикл, а после - уровень компонента
                         cz = indicator.substring(0,ipoint);
                         level = indicator.substring(ipoint+1);
                    }
                    level = level.isEmpty() ? null : level.toUpperCase();
                    
                    tag = -1;
                    for(int i=0;i<cs.length;i++) // проверяем все найденные циклы
                        if(cs[i].equals(cz)) {
                            tag = i+7; // цикл уже был зафиксирован
                            break;
                        }
                    if(tag<0){
                        tag = cs.length + 7; // добавляем новый цикл
                        cs = Arrays.copyOf(cs,cs.length+1);
                        cs[cs.length-1] = cz;
                    }
                }
                s = addChoice(mapChoice);
                if(s!=null) return s;
                String cd = cs[tag-7];
                y = newSynMap.get(cd);
                if(y==null) {
                    y = "*";
                    newSynMap.put(cd, y);
                }
                newSynSet.add(cd);
//                attr += XLines.attr("y", y);
                attr = XLines.attr("cd", cd);
                attr += XLines.attr("uk", level);
                attr += XLines.attr("v7a", f.getSub('a'));
                attr += XLines.attr("v7d", f.getSub('d'));
                attr += XLines.attr("v7b", f.getSub('b'));
                s691 = new String[] {f.getSubsUpper('d'),level,cd};
            for(String s6:s691) {
                if(s6==null) continue;
                String fars = Words.farCheck(s6);
                if(fars!=null) System.out.println(Curr2.this.key+" иностранная буква: "+fars);
            }
                for(int sem : sems) {
                    int y2index = (sem-1) % y2count;
                    if(y2Counts[y2index] < 0) y2Counts[y2index] = 0;
                    int course = sem2course(sem); // курс обучения
                    if(courseDiscs==null)
                        courseDiscs = new Map[course+1];
                    else if(courseDiscs.length <= course)
                        courseDiscs = Arrays.copyOf(courseDiscs, course+1);
                    Map<CurrDisc2,int[]> cdmap = courseDiscs[course];
                    if(cdmap==null) 
                        courseDiscs[course] = (cdmap = new HashMap<>());
                    int[] y2sems = cdmap.get(this);
                    if(y2sems == null)
                        cdmap.put(this, y2sems = new int[y2count]);
                    y2sems[(sem-1)%y2count] = sem;
                }
                d2 = realDisc2(d);
                d2index = d2.cdList.size();
                d2.cdList.add(this);
                d2.set59.add(d);
                return null;
            }
/**
 * возвращает объект соответствующий дисциплине учебного плана
 * @param text передается yjhvfлизованым и не проверяется на нул и пусто
 * @return готовый объект
 */            
            private Disc2 realDisc2(String text) {
                String key = normalizeKey(text);
                Disc2 disc2 = discMap.get(key); 
                if(disc2==null){
                    Integer mfn = syn2mfn.get(key);
                    if(mfn != null) 
                        disc2 = discMap.get(key = "$YN"+mfn); 
                }
                if(disc2==null)
                    discMap.put(key, disc2 = new Disc2(key));
                
//                String syn1 = synMap.get(key);    // первый синоним
//                String key1 = (syn1==null) ? key : syn1; // ключ объекта
//                Disc2 disc2 = discMap.get(key1); 
//                if(disc2==null) {
//                    discMap.put(key1, disc2 = new Disc2(key1));
////                    int far=Words.farChar(s);
////                    if(far>0) System.out.println("иностранная буква="+far+" "+s.substring(0, far)+" "+s.substring(far));
//                }
//                if(disc2.cdList==null) disc2.cdList=new ArrayList<>();
//                d2index = disc2.cdList.size();
//                disc2.cdList.add(this);
//                if(syn1 != null)
//                    disc2.set59.add(key);
                return disc2;
            }
            private String addChoice(Map<String,Choice2> mapChoice) {
                String f = subs.get('f');
                if(f!=null && !f.trim().isEmpty()) { // непустой процент
                    percentage = Math.round(readPercentage(f));
                    if(percentage<=0) return "Процент/доля не положительно ="+f;
                    if(percentage>100) return "Процент/доля превысил 100% ="+f;
                }
                a = subs.get('a');
                if(a!=null){ // анализируем/корректируем текст
                    a = a.trim().toUpperCase();
                    if(a.isEmpty()) a=null;
                    else if(indicator == null) { // по старому
                        if(!Character.isDigit(a.charAt(0))) { // есть уровень компонента
                            level = a.substring(0, 1); // уровень компонента
                            if(a.length()==1) a=null;
                        }
                    }
                }

                if(a!=null){ // это - 100% метка группы выбора
                    a = cs[tag-7]+"."+level+"."+a;
                    Choice2 choice = mapChoice.get(a);
                    if(choice==null) mapChoice.put(a,choice = new Choice2(a));
                    choice.add(this);
                } else if(percentage==0) percentage=100;
                else System.out.println(cs[tag-7]+"."+fno+" изучает НЕ ВСЯ группа");
                return null;
            }
/**
 * добавляет книгу семестро-группам из g3keys
 * @param dopindex дополнительная учебная литература?
 * @param g3keys список ключей набранных семестро-групп
 * @param b добавляемая книга
 */
            private void addBook3(int dopindex, Set<Integer> g3keys, Book2 b) {
                if(g3==null) g3 = new TreeMap<>();
                for(int g3key:g3keys) {
                    List<Book2>[] g3books = g3.get(g3key);
                    if(g3books == null) 
                        g3.put(g3key, g3books = new List[2]);
                    if(g3books[dopindex] == null)
                        g3books[dopindex] = new ArrayList<>();
                    g3books[dopindex].add(b);
                }
            }
/**
 * сформировать k-элементы для строки плана
 * @return XLines
 */
            private XLines xmlDeprecated(XLines x) {
                String cdxmlid = "D" + Curr2.this.xmlid + "-" + xmlid;
                String cdattr = XLines.attr("d", cdxmlid);
                List<Group2.GroupCurr2>[] gcsarray = Curr2.this.courseGroups;
                for(int sem:sems) {
                    int y2index = (sem-1) % y2count;
                    int course = sem2course(sem);
                    String kattr = "";
                    kattr += XLines.attr("s", ""+sem);
                    kattr += XLines.attr("y2", ""+(y2index+1));
                    kattr += XLines.attr("c", ""+course);
                    kattr += cdattr;
                    List<Book2>[] g3books = g3==null ? null : g3.get(sem);
                    List<Group2.GroupCurr2> gcs = null;
                    if(gcsarray!=null && gcsarray.length>course)
                        gcs = gcsarray[course];
                    if(gcs==null) { // групп нет в этом семестре
                        x.newline();
                        if(books==null && g3books==null)
                            x.xml("k", kattr, null);
                        else {
                            x.xml1("k", kattr);
                            xmlBooks(x,books);
                            xmlBooks(x,g3books);
                            x.xml2("k");
                        }
                    } else {
                        if(g3books != null){
                            x.newline().xml1("k0", kattr);
                            xmlBooks(x,g3books);
                            x.xml2("k0");
                        }
                        for(Group2.GroupCurr2 gc:gcs) {
                            g3books = g3==null ? null : g3.get(sem + g3max * (gc.xmlid+1));
                            String gcxmlid = "G"+gc.curr2.xmlid+"-"+gc.xmlid;
                            String gcattr = XLines.attr("g", gcxmlid);
                            double[] dko = new double[2];
                            koBooks(d2.books, sem, dko);
                            x.newline();
                            if(books==null && g3books==null)
                                x.xml("k", kattr + gcattr + koAttr(dko), null);
                            else {
                                koBooks(books, sem, dko);
                                koBooks(g3books, sem, dko);
                                x.xml1("k", kattr + gcattr + koAttr(dko));
                                xmlBooks(x,books);
                                xmlBooks(x,g3books);
                                x.xml2("k");
                            }
                        }
                    }
                }
                return x;
            }
/**
 * сформировать k-элементы для строки плана
 * @return XLines
 */
            private XLines xml61(XLines x
/**
 *  [y2index]=контингент //по {62=осн,63=м/у,64=доп}
 */                    
                    , Map<Book2, int[]>[] bmaps
                    , List<Field> fs
                    , long[] k
                    , long[][] bmin2
                    ) {
/**
 *  [y2index][bindex][]={экз,эи,ко}
 */                    
                Map<Group2.GroupCurr2, double[][][]> gcmap = new HashMap<>();
/**
 *  [y2index][bindex][]={экз,эи,ко}
 */                    
                Map<Integer, double[][][]> cmap = new TreeMap<>();
                String cdxmlid = Curr2.this.xmlid + "-";
                String cdattr = XLines.attr("d", "D" + cdxmlid + xmlid);
                for(int sem:sems) {
                    String kattr = XLines.attr("s", "" + sem);
                    int y2index = (sem-1) % y2count;
                    kattr += XLines.attr("y2", "" + (y2index + 1));
                    int course = Curr2.this.sem2course(sem);
                    kattr += XLines.attr("c", "" + course);
                    kattr += cdattr;
                    List<Book2>[] g3books = g3==null ? null : g3.get(sem);
                    List<Group2.GroupCurr2> gcs = getSemGroupCurrs(sem);
                    if(gcs==null) { // групп нет в этом семестре
                        x.newline();
                        if(books==null && g3books==null)
                            x.xml("k", kattr, null);
                        else {
                            x.xml1("k", kattr);
                            xmlBooks(x,books);
                            xmlBooks(x,g3books);
                            x.xml2("k");
                        }
// проверяем по курсу складываем все книги по экземплярам  
                        double[][][] v = cmap.get(course);
                        if(v == null) cmap.put(course, v = new double[y2count][][]);
                        checkTags(0, bmaps, v, y2index, d2.books, books, g3books);
                        
                    } else {
                        if(g3books != null){
                            x.newline().xml1("k", kattr);
                            xmlBooks(x,g3books);
                            x.xml2("k");
// проверяем по курсу складываем только g3books книги по экземплярам                        
                            double[][][] v = cmap.get(course);
                            if(v == null) cmap.put(course, v = new double[y2count][][]);
                            checkTags(0, bmaps, v, y2index, g3books);
                        }
                        for(Group2.GroupCurr2 gc:gcs) {
                            g3books = g3==null ? null : g3.get(sem + g3max * (gc.xmlid+1));
                            String gcattr = XLines.attr("g", "G" + cdxmlid + gc.xmlid);
                            double[] dko = new double[2];
                            koBooks(d2.books, sem, dko);
                            x.newline();
                            if(books==null && g3books==null)
                                x.xml("k", kattr + gcattr + koAttr(dko), null);
                            else {
                                koBooks(books, sem, dko);
                                koBooks(g3books, sem, dko);
                                x.xml1("k", kattr + gcattr + koAttr(dko));
                                xmlBooks(x,books);
                                xmlBooks(x,g3books);
                                x.xml2("k");
                            }
// проверяем по группе складываем все книги по ко
                            int scount = Math.round(gc.count * percentage);
                            k[y2index] += scount; // собираем здесь студентов по полугодиям
                            double[][][] v = gcmap.get(gc);
                            if(v == null) gcmap.put(gc, v = new double[y2count][][]);
                            checkTags(scount, bmaps, v, y2index, d2.books, books, g3books);
                        }
                    }
                }
// cmap и gcmap сформированы - заталкиваем их в tags 
// [y2index][bindex][] = {экз,эи,ко} 
                
                for(Map.Entry<Integer, double[][][]> e:cmap.entrySet()) {
                    Field f = newField(e.getKey());
                    fs.add(f);
                    setv3(f, e.getValue(), null, bmin2);
                }
                for(Map.Entry<Group2.GroupCurr2, double[][][]> e:gcmap.entrySet()) {
                    Field f = e.getKey().newField(this);
                    fs.add(f);
                    setv3(f, e.getValue(), k, bmin2);
                }
                return x;
            }
/**
 * сформировать k-элементы для строки плана
 * @return XLines
 */
            private XLines xml61(XLines x
                    , List<Field> fs
                    , long[] k
                    ) {
                String cdxmlid = Curr2.this.xmlid + "-";
                String cdattr = XLines.attr("d", "D" + cdxmlid + xmlid);
                for(int course:sems) {
                    String kattr = XLines.attr("c", "" + course);
                    kattr += cdattr;
                    List<Group2.GroupCurr2> gcs = Curr2.this.courseGroups[course];
                    for(Group2.GroupCurr2 gc:gcs) {
                        fs.add(gc.newField(this));
                        String gcattr = XLines.attr("g", "G" + cdxmlid + gc.xmlid);
                        x.newline().xml("k", kattr + gcattr, null);
                        k[0] += gc.count; // собираем здесь студентов по полугодиям
                    }
                }
                return x;
            }
/**
 * заполнить поле 61 и хранитель минимумов ко 
 * @param f заполняемое поле 61
 * @param v3 источник экземпляров и ко
 * @param k хранитель минимумов ко
 */            
            private void setv3(Field f
                    , double[][][] v3
                    , long[] k
                    , long[][] bmin2
                    ){
                char[] i3 = new char[] {'i','o'};
                char[] l3 = new char[] {'l','u'};
//                if(v3==null) return;
                for(int y2index=0; y2index<y2count; y2index++) {
                    double[][] v2 = v3[y2index];
                    if(v2==null) continue; // в этом семестре не изучается
                    for(int bindex=0; bindex<bmaxindex; bindex++) {
                        double[] v = v2[bindex];
                        long b[] = new long[3];
                        long[] bmin = bmin2[bindex];
                        String s;
                        if(v==null) {
                            s = "0";
                        } else {
                            b[0] = Math.round(v[0]);
                            b[1] = Math.round(v[1]);
                            s = "" + b[0];
                            if(b[1]>0) s += "+" + b[1];
                            if(bmin[3] > b[1]) bmin[3] = b[1];
                        }
                        f.put((char)(i3[bindex]+y2index), s);
                        if(k==null) { // если без группы (студентов)
                            b[2] = Long.MAX_VALUE;
                        } else if(v!=null) {
                                b[2] = ko100(v[2]);
    //                                int koindex = y2count+bindex;
    //                                if(k[koindex] < 0 || ko < k[koindex]) k[koindex] = ko;
                                s = "" + b[2];
                                f.put((char)(l3[bindex]+y2index), s);
                            }
                        if(b[2]>bmin[2])continue;
                        if(b[2]<bmin[2]){
                            bmin[0] = b[0];
                            bmin[1] = b[1];
                            bmin[2] = b[2];
                            continue;
                        }
                        if(b[0]>bmin[0])continue;
                        if(b[0]<bmin[0]){
                            bmin[0] = b[0];
                            bmin[1] = b[1];
//                            bmin[2] = b[2];
                            continue;
                        }
                        if(b[1]<bmin[1]) bmin[1] = b[1];
                    }
                }
            }
/**
 * заполнить v3 подходящими экземплярами из books
 * заполнить bmaps подходящими учебниками books, нарастив их спрос
 * @param scount сколько студентов составляют спрос
 * @param bmaps подходящие учебники[] = раскладываем спрос по полугодиям
 * @param v3 [y2index][bindex][] = {экз,эи,ко} сборник экземпляров
 * @param y2index полугодие
 * @param books входной список подходящих учебников[dopindex]
 */
            private void checkTags(int scount
                    , Map<Book2, int[]>[] bmaps
                    , double[][][] v3
                    , int y2index
                    , List<Book2>[]... books) {
                double[][] v2 = v3[y2index];
                if(v2==null) v3[y2index] = v2 = new double[bmaxindex+1][];
                for(List<Book2>[] bs:books) {
                    if(bs == null) continue;
                    for(int dopindex=0; dopindex<2; ++dopindex) {
                        if(bs[dopindex] == null) continue;
                        for(Book2 b:bs[dopindex]) {
                            TextBook t = b.t;
                            int bindex = dopindex==0 ? t.getBIndex() : bmaxindex;
                            Map<Book2, int[]> bmap = bmaps[bindex];
                            if(bmap == null) bmaps[bindex] = bmap = new HashMap<>();
// сами учебники не отбрасываем никогда
                            if(dopindex==0 && !t.checkNew(y)) {
                                if(!bmap.containsKey(b)) bmap.put(b, null);
                                continue;
                            } 
// а спрос учебникам фиксируем только с учетом новизны
                            int[] v = bmap.get(b);
                            if(v == null) bmap.put(b, v = y2new());
                            if(v[y2index] < 0) v[y2index] = scount; // убираем сторожок
                            else v[y2index] += scount; // спрос добавляем в книгу
// и предложение курсам/группам фиксируем тоже с учетом новизны                            
                            double[] v1 = v2[bindex];
                            if(v1==null) v2[bindex] = v1 = new double[] {0,0,-1};
                            v1[0] += t.getCount();
                            if(t.getHttp() != null) v1[1]++;
                            if(v1[2] < 0) v1[2] = b.ko(y2index);
                            else v1[2] += b.ko(y2index);
                        }
                    }    
                }
            }
            
        }
/**
 * наименование учебного плана (его идентификатор)
 */
        private String key;
/**
 * код квалификации, он же определяет специальность для подсчета ЭИ
 */
        private String kk;
/**
 * {КафВып, Напр, Спец, ВидОбуч, Специализация, Квалификация}
 */
        private String[] s691;
/**
 * специальность учебного плана для расчета норматива ЭИ
 */
        private String spec;
/**
 * версия учебного плана
 */
        private String ver;
/**
 * комментарий учебного плана
 */
        private String w;
/**
 * складываем все, что надо из Ирбиса в аттрибуты
 */
        private String attr;
/**
 * это смещение курсов допускается только = 4.
 */
        private int course0 = 0;
/**
 * список реальных строк плана
 * индекс=[cd.id]
 */        
        private List<CurrDisc2> currDiscList=new ArrayList<>();
/**
 * список реальных групп плана
 * индекс=[gc.xmlid]
 */        
        private List<Group2.GroupCurr2> groupCurrList=new ArrayList<>();
/**
 * сборка всех студентов
 * @param gc 
 */        
        private Map<Curr2.CurrDisc2,int[]> addGroup(Group2.GroupCurr2 gc) {
            gc.xmlid = groupCurrList.size();
            groupCurrList.add(gc);
            Group2 g = gc.group2this();
            int course = g.course;
            if(courseGroups == null) {
                courseGroups = new List[course+1];
            } else if(courseGroups.length <= course) {
                courseGroups = Arrays.copyOf(courseGroups, course+1);
            }
            List<Group2.GroupCurr2> gcs = courseGroups[course];
            if(gcs==null) courseGroups[course] = (gcs=new ArrayList<>());
            gcs.add(gc);
            if(courseDiscs==null)
                courseDiscs = new Map[course+1];
            if(courseDiscs.length<=course) 
                courseDiscs = Arrays.copyOf(courseDiscs, course+1);
            Map<Curr2.CurrDisc2,int[]> cdmap = courseDiscs[course];
            if(cdmap==null) { // на пустую дисциплину
                courseDiscs[course] = cdmap = getEmptyCurrDisc().emptycdmap(course);
            }
            return cdmap;
        }
        private Curr2(int mfn) {
//            this.key = key;
            this.mfn = mfn;
            this.xmlid = currMap.size()+1;
        }
        private int sem2course(int sem) { return (sem-1)/y2count+1-course0; }
        private boolean read(Map<Integer,List<Field>> tags) {
            key = Field.getTagsSub(tags,"903");
            y = Field.getTagsSub(tags, 6, 'f'); // год начала
            w = Field.getTagsSubUpper(tags, 6, 'e'); // комментарий
            kk = Field.getTagsSub(tags, 5, 'c'); // код квалификации
            s691 = new String[]
            {Field.getTagsSubUpper(tags, 6, 'c') // 1=КафедраВыпускающая
            ,Field.getTagsSubUpper(tags, 2, 'a') // 2=Направление
            ,Field.getTagsSubUpper(tags, 3, 'a') // 3=Специальность
            ,Field.getTagsSubUpper(tags, 5, 'a') // 4=ВидОбучения
            ,Field.getTagsSubUpper(tags, 4, 'a') // 6=Специализация
            ,Field.getTagsSubUpper(tags, 5, 'b') // 7=Квалификация
            };
            for(String s:s691) {
                if(s==null) continue;
                String fars = Words.farCheck(s);
                if(fars!=null) System.out.println(key+" иностранная буква: "+fars);
            }
            ver = Field.getTagsSub(tags, 6, 'z');
            spec = (kk==null) ? "" : kk.substring(0, 2)
                    +"."+Field.getTagsSub(tags, 3, 'a')
                    +" "+Field.getTagsSub(tags, 3, 'z')
                    +": "+Field.getTagsSub(tags, 3, 'b')
                    ;
            int semmin = -1;
            int semmax = -1;
            for(int tag=7; tag<12; tag++) {
                List<Field> fields = tags.get(tag);
                if(fields==null) continue;
                Map<String,Choice2> mapChoice = new TreeMap<>();
                for(Field f:fields) {
                    CurrDisc2 cd = new CurrDisc2(tag);
                    String e = cd.read(f,mapChoice);
                    if(e!=null) {
                        System.out.println(key + "**** дисциплина отброшена:"+e);
                        continue;
                    }
                    int sem = cd.sems[cd.sems.length-1];
                    if(sem>semmax) semmax = sem;
                    sem = cd.sems[0];
                    if(semmin<0 || sem<semmin) semmin = sem;
                    cd.xmlid = currDiscList.size();
                    currDiscList.add(cd);
                }
                for(Choice2 choice:mapChoice.values()) {
                    String s = choice.set();
                    if(s!=null) System.out.println(text(tag)+s);
                }
            }
            if(currDiscList.isEmpty()){
                System.out.println(key+"**** вообще нет дисциплин с семестрами ****");
//                return false;
            } else if(semmin != 1){
                if(semmin==9 && y2count==2) {
                    System.out.println(key+"назначено смещение курсов (semmin="+semmin+")");
                    course0 = 4;
                } else {
                    System.out.println(key+"не назначено смещение курсов (semmin="+semmin+")");
                }
            }
            maxCourse = sem2course(semmax);
            attr = XLines.attr("id", key);
            for(String s4:new String[] {"2a","2b","3a","3b","4a","4b","5a","5b","5c","6a","6b","6c","6d"})
                attr += XLines.attr("v"+s4,Field.getTagsSub(tags, s4));
            attr += XLines.attr("c0",""+course0);
            return true;
        }
        private Field newField() {
            Field f = new Field();
            f.put('d', key);
            f.put('t', kk);
            if(s691!=null) f.put('g', s691[0]);
            f.put('z', ver);
            f.put('1', y);
            //f.put('x', ""+maxCourse);
            return f;
        }
        private XLines xml() {
            XLines x = new XLines();
            x.xml1("c", attr);
            for(CurrDisc2 cd:currDiscList) {
                String dattr = XLines.attr("xml:id","D"+xmlid+"-"+cd.xmlid);
                dattr += cd.attr;
                if(cd.percentage != 100)
                    dattr += XLines.attr("v7f", ""+cd.percentage);
                x.newline().xml("d", dattr, cd.d);
            }
            if(courseGroups!=null)
                for(List<Group2.GroupCurr2> gcs:courseGroups) {
                    if(gcs==null) continue;
                    for(Group2.GroupCurr2 gc:gcs) {
                        x.newline().xml("g", gc.attr(), gc.group2this().key);
                    }
                }
            x.xml2("c");
            return x;
        }
    }
}
