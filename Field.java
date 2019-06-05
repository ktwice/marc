package marc;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.*;

/**
 * карта подполей, имеющей строковое представление
 * за пустыми подполями надо следить самому
 * подполе без буквы имеет ключ = '\u0000'
 * @author marina
 */
public class Field {
//    final static private int wsize = 24;
    final static private int wsize = 32;
    static Words words;
    static Writer wr = null;
    final static private Pattern subfieldPattern = Pattern.compile("\\^.");
    final static private Pattern spacePattern = Pattern.compile("[ ]");
    final static private Pattern wordPattern = Pattern.compile("[^ ]");
    final static private Pattern pslim1 = Pattern.compile(" [\\Q ,;:.)]}\\E]");
    final static private Pattern pslim2 = Pattern.compile("[\\Q{[(\\E] ");
    public static Field creatext(String s) {
        Field f = new Field();
        f.setString(s);
        return f;
    }
    public static Field create(String s) {
        Field f = new Field();
        f.getSubs().put('\u0000', s);
        return f;
    }
    public static Field create(char ch, String s) {
        Field f = new Field();
        f.getSubs().put(ch, s);
        return f;
    }
    public static Field create(Map<Character, String> subs) {
        Field f = new Field();
        f.setSubs(subs);
        return f;
    }
    public static void tagsAdd(Map<Integer, List<Field>> tags,
            int fld, Field f) {
        List<Field> fs = tags.get(fld);
        if(fs==null) tags.put(fld, fs = new ArrayList<Field>());
        fs.add(f);
    }
    public static String ymd8(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        if(year<=0) year = c.get(Calendar.YEAR);
        else if(year < 100) year += 2000;
        else if(year<1000 && year>9999) return "yyyy****";
        if(month<=0) month = c.get(Calendar.MONTH)+1;
        else if(month>12) return "****mm**";
        if(day<=0) day = c.get(Calendar.DAY_OF_MONTH);
        c.setLenient(false);
        try {c.set(year,month-1,day);}
        catch (IllegalArgumentException iae) {return "******dd";}
        String s = ""+year;
        if(month<10) s += "0";
        s += month;
        if(day<10) s += "0";
        s += day;
        return s;
    }
    public static void tagsAdd907(Map<Integer, List<Field>> tags,
            String s, String d8) {
        List<Field> fs = tags.get(907);
        //String d8 = ymd8(0,0,0);
        if(fs==null)
            tags.put(907, fs = new ArrayList<Field>());
        else fs.clear();
        Field f = Field.create('a',d8).put('b', s);
        fs.add(f);
    }
    public Field put(char ch, String s) {
        if( s== null) getSubs().remove(ch);
        else getSubs().put(ch, s);
        return this;
    }
    public int compareTo(Field f2, char ch) {
        String s = getSubs().get(ch);
        String s2 = f2.getSubs().get(ch);
        if(s == null) {
            if(s2 == null) return 0;
            return -1;
        } else if(s2 == null) 
            return 1;
        return s.compareTo(s2);
    }
//    public class Comparator implements java.util.Comparator<Field> {
//        private char[] s;
//        private int[] m;
//        private char[] d;
//        public Comparator(String s, String m, String d) {
//            this.s = s.toCharArray();
//            this.m = new int[this.s.length];
//            this.d = new char[this.s.length];
//            for(int i=0; i<this.s.length; ++i){
//                this.m[i] = (m==null || i>=m.length() || m.charAt(i)!='-')? +1: -1;
//                this.d[i] = (d==null || i>=d.length() || d.charAt(i)!='s')? 0: 's';
//            }
//        }
//        @Override
//        public int compare(Field f1, Field f2) {
//            int i=-1;
//            Map<Character,String> subs1 = f1.getSubs();
//            Map<Character,String> subs2 = f2.getSubs();
//            for(char ch:s){
//                ++i;
//                String s1 = subs1.get(ch);
//                String s2 = subs2.get(ch);
//                if(s1==null) {
//                    if(s2==null) continue;
//                    return -1*m[i];
//                }
//                if(s2==null) return m[i];
//                if(d[i]==0) {
//                    int ri = Integer.parseInt(s1)-Integer.parseInt(s2);
//                    if(ri==0) continue;
//                    return ri*m[i];
//                }
//                int rs = s1.compareTo(s2);
//                if(rs==0) continue;
//                return rs*m[i];
//            }
//            return 0;
//        }
//    }

    private LazyField lazy = null;
    private int lazyIndex;
    private Map<Character,String> subs = null;
    public static void fssOut(List<List<String>> fss) {
        for(List<String> fs:fss) {
            System.out.println();
            for(String f:fs) {
                System.out.println(f);
            }
        }
        System.out.println();
    }
/**
 * строка без крайних и двойных пробелов 
 * @param s
 * @return исправленная строка или null
 */    
    public static String normalized(String s) {
        String s2 = s.trim();
        while(s2.contains("  "))
            s2 = s2.replace("  ", " ");
        char ch = Melody.mixed(s2);
        if(ch != '\u0000') 
            System.out.println("mixed("+s2+")="+(int)ch+"<"+ch+">");
        if(s2==s) return null;
        System.out.println("normalized("+s+")="+s2+";");
        return s2;
    }
/**
 * нормализует значение каждого подполя и пустые убирает
 * @return количество оставшихся подполей или -1 если ничего не изменилось
 */    
    public int normalize() {
        Iterator<Map.Entry<Character,String>> eit = getSubs().entrySet().iterator();
        while(eit.hasNext()) { // внешний цикл поиска ненормализованых значений
            Map.Entry<Character,String> e = eit.next();
            String s = normalized(e.getValue());
            if(s==null) continue;
            for(;;) { // цикл нормализации
                if(s.isEmpty()) eit.remove();
                else e.setValue(s);
                do { // внутренний цикл поиска ненормализованых значений
                    if(!eit.hasNext()) return subs.size();
                    e = eit.next();
                    s = normalized(e.getValue());
                } while (s==null);
            }
        }
        return -1;
    }
/**
 * нормализует все поля списка, пустые и полные дубли полей удаляет
 * @param fs нормализуемый список полей
 * @return список был откорректирован?
 */
    public static boolean normalizeFields(List<Field> fs) {
        boolean b = false;
        Iterator<Field> it = fs.iterator();
        while(it.hasNext()) {
            Field f = it.next();
            int i = f.normalize();
            if(f.deepEqualsField(fs)!=null) i=0;
            else if(i<0) continue;
            if(!b) b = true;
            if(i==0) it.remove();
        }
        return b;
    }
/**
 * ищет в списке первое полностью совпадающее поле, пока не встретит самого себя
 * @param fs проверяемый список
 * @return полностью совпадающее поле из списка или null
 */
    public Field deepEqualsField(List<Field> fs) {
        for(Field f:fs) {
            if(f==this) break;
            if(deepEquals(f)>=0) continue;
            System.out.println("deepEquals="+get()+";");
            return f;
        }
        return null;
    }
/**
 * нормализует все
 * @param tags цель нормализации
 * @return был откорректирован?
 */
    public static boolean normalizeTags(Map<Integer,List<Field>> tags) {
        boolean b = false;
        Iterator<List<Field>> it = tags.values().iterator();
        while(it.hasNext()) {
            List<Field> fs = it.next();
            if(!normalizeFields(fs)) continue;
            if(!b) b = true;
            if(fs.size()==0) it.remove();
        }
        return b;
    }
/**
 * сверка на полное соответствие содержания
 * @param f сравниваемое поле
 * @return -1 если совпадают или размер или код подполя
 */
    public int deepEquals(Field f) {
        int i = getSubs().size();
        Map<Character,String> fsubs = f.getSubs();
        if(i != fsubs.size()) return 1; // размер не такой
        Iterator<Map.Entry<Character,String>> it = subs.entrySet().iterator();
        for(Map.Entry<Character,String> fe:fsubs.entrySet()) {
            Map.Entry<Character,String> e = it.next();
            char ch = e.getKey().charValue();
            if(ch != fe.getKey().charValue()) return ch; // код не такой
            if(!e.getValue().equals(fe.getValue())) return ch; // текст не тот
        }
        return -1; // все совпало
    }
/**
 * сверка на полное соответствие содержания записей
 * @param tags запись
 * @param tags2 вторая запись
 * @return совпадают ли
 */
    public static boolean tagsDeepEquals(Map<Integer,List<Field>> tags
            , Map<Integer,List<Field>> tags2) {
        if(tags.size()!=tags2.size()) return false;
        Iterator<Map.Entry<Integer,List<Field>>> it = tags.entrySet().iterator();
        for(Map.Entry<Integer,List<Field>> e2:tags2.entrySet()) {
            Map.Entry<Integer,List<Field>> e = it.next();
            if(e.getKey().intValue()!=e2.getKey().intValue()) return false;
            if(e.getValue().size()!=e2.getValue().size()) return false;
        }
        Iterator<List<Field>> itv = tags.values().iterator();
        for(List<Field> fs2:tags2.values()) {
            Iterator<Field> itf = itv.next().iterator();
            for(Field f2:fs2)
                if(itf.next().deepEquals(f2) >= 0) return false;
        }
        return true;
    }
    public static String preText(String pre, String text){
        if(text==null || text.isEmpty()) return "";
        return pre+text;
    }
    public static String textEnds(String text, String ends){
        if(text==null || text.isEmpty()) return "";
        if(text.endsWith(ends)) return text;
        return text+ends;
    }
    public static String linkText(String s, String pre, String text, String post) {
        if(text==null || text.isEmpty()) return s;
        if(!text.endsWith(post)) text += post;
        if(s==null || s.isEmpty()) return text;
        return s + pre + text;
    }
    public static String linkText(String s, String pre, String text) {
        if(text==null || text.isEmpty()) return s;
        if(s==null || s.isEmpty()) return text;
        return s + pre + text;
    }
    public static String linkText(String s, String text) {
        if(text==null || text.isEmpty()) return s;
        if(s==null || s.isEmpty()) return text;
        return s + text;
    }
    public static String tagsText(Map<Integer,List<Field>> tags) {
        if(tagsText(tags,920).startsWith("SPEC")) return tagsTextSPEC(tags);
        String s = "";
        s = linkText(s, "", tagsText(tags,700), ".");
        s = linkText(s, " ", tagsText(tags,200), ".");
        s = linkText(s, " - ", tagsText(tags,205), ".");
        s = linkText(s, " - ", tagsText(tags,210), ".");
        s = linkText(s, " - ", tagsText(tags,215), ".");
        return s;
    }
    public static String tagsTextSPEC(Map<Integer,List<Field>> tags) {
        String s = "";
        s = linkText(s, "", tagsText(tags,461), ".");
        s = linkText(s, " ", tagsText(tags,200), ".");
        s = linkText(s, " - ", tagsText(tags,205), ".");
        s = linkText(s, " - ", tags210text461(tags), ".");
        s = linkText(s, " - ", tagsText(tags,215), ".");
        return s;
    }
    public static String[] tagsTexts(Map<Integer,List<Field>> tags) {
        if(tagsText(tags,920).startsWith("SPEC")) return tagsTextsSPEC(tags);
        String s2 = textEnds(tagsText(tags,200), ".");
        s2 = linkText(s2, " - ", tagsText(tags,205), ".");
        return new String[]
        {textEnds(textEnds(tagsText(tags,700), ".")," ")
        ,s2
        ,preText(" - ", textEnds(tagsText(tags,210), "."))
        ,preText(" - ", textEnds(tagsText(tags,215), "."))
        };
    }
    public static String[] tagsTextsSPEC(Map<Integer,List<Field>> tags) {
        String s1 = "";
//        if(tags.containsKey(961)){
//            Map<Character,String> subs = tags.get(961).get(0).getSubs();
//            if(!subs.containsKey('4') && subs.containsKey('a') && subs.containsKey('b')){
//                s1 = subs.get('a')+", "+subs.get('b');
//            }
//        }
        String s2="";
        if(tags.containsKey(461)){
            Map<Character,String> subs = tags.get(461).get(0).getSubs();
            if(s1.isEmpty()) {
                s1 = subs.get('x');
            }
            s2 = tagsText900(tags,subs.get('c'));
            s2 = linkText(s2, " : ", subs.get('e'));
            if(!subs.containsKey('x'))
                s2 = linkText(s2, " / ", subs.get('f'));
            s2 = textEnds(s2,".");
            s2 = linkText(s2, " ", tagsText(tags,200-900), ".");
            s2 = linkText(s2, " - ", tagsText(tags,205), ".");
        }
        return new String[]
        {textEnds(textEnds(s1, ".")," ")
        ,s2
        ,preText(" - ", textEnds(tags210text461(tags), "."))
        ,preText(" - ", textEnds(tagsText(tags,215), "."))
        };
    }
    private static String tagsText900(Map<Integer,List<Field>> tags, String s) {
        if(s==null || s.isEmpty()) return s;
        List<Field> fs = tags.get(900);
        if(fs==null || fs.isEmpty()) return s;
        String v900t = fs.get(0).getSubs().get('t');
        if(v900t==null || v900t.isEmpty()) return s;
        if(v900t.equals("a")) return s + " [Текст]";
        if(v900t.equals("l")) return s + " [Электронный ресурс]";
        return s;
    }
    public static String tagsText(Map<Integer,List<Field>> tags, int n) {
        List<Field> fs = tags.get((n==200-900) ? 200 : n);
        if(fs==null || fs.isEmpty()) return "";
        Field f = fs.get(0);
        Map<Character,String> subs = f.getSubs();
        String s = "";
        switch(n) {
            case 920:
                if(!subs.containsKey('\u0000')) break;
                s = subs.get('\u0000');
                break;
            case 700:
                if(!subs.containsKey('a')) break;
                s = subs.get('a');
                if(subs.containsKey('b')) s = linkText(s, ", ", subs.get('b'));
                else s = linkText(s, ", ", subs.get('g'));
                break;
            case 461:
                s = linkText(s, "", subs.get('x'),".");
                s = linkText(s, " ", tagsText900(tags,subs.get('c')));
                s = linkText(s, " : ", subs.get('e'));
                break;
            case 200:
                if(!subs.containsKey('a')) {
                    if(subs.containsKey('v')) return tagsText900(tags,subs.get('v'));
                    break;
                }
                String s1 = linkText("", subs.get('f'));
                s1 = linkText(s1, " ; ", subs.get('g'));
                s = linkText(tagsText900(tags,subs.get('a')), " : ", subs.get('e'));
                if(subs.containsKey('v')) s = linkText(subs.get('v'), " : ", s);
                s = linkText(s, " / ", s1);
                break;
            case 200-900:
                if(!subs.containsKey('a')) {
                    if(subs.containsKey('v')) return subs.get('v');
                    break;
                }
                String s11 = linkText("", subs.get('f'));
                s11 = linkText(s11, " ; ", subs.get('g'));
                s = linkText(subs.get('a'), " : ", subs.get('e'));
                if(subs.containsKey('v')) s = linkText(subs.get('v'), " : ", s);
                s = linkText(s, " / ", s11);
                break;
            case 205:
                s = linkText("",subs.get('a'));
                break;
            case 210:
                s = subs210text(subs);
                break;
            case 215:
                if(!subs.containsKey('a')) break;
                s = subs.get('a');
                if(!Character.isDigit(s.charAt(0))) break;
                int i = 0;
                while(++i<s.length() && Character.isDigit(s.charAt(i)));
                s = s.substring(0, i) + " с.";
                break;
        }
        return s;
    }
    public static String subs210text(Map<Character,String> subs210) {
        String s = "";
        s = linkText(s, subs210.get('a'));
        s = linkText(s, " : ", subs210.get('c'));
        s = linkText(s, ", ", subs210.get('d'));
        return s;
    }
    public static String tags210text461(Map<Integer,List<Field>> tags) {
        List<Field> fs461 = tags.get(461);
        if(fs461==null || fs461.isEmpty()) return tagsText(tags,210);
        Map<Character,String> subs461 = fs461.get(0).getSubs();
        List<Field> fs = tags.get(210);
        Map<Character,String> subs = new TreeMap<Character,String>();
        if(fs!=null && !fs.isEmpty()) subs.putAll(fs.get(0).getSubs());
        if(!subs.containsKey('a')) subs.put('a', subs461.get('d'));
        if(!subs.containsKey('c')) subs.put('c', subs461.get('g'));
        if(!subs.containsKey('d')) {
            String year = subs461.get('h');
            if(year!=null && !year.trim().isEmpty()) subs.put('d', year.trim());
        }
        return subs210text(subs);
    }
    public static String tagsDeep(Map<Integer,List<Field>> tags
            , Map<Integer,List<Field>> tags2) {
        if(tags.size()!=tags2.size()) return "полей"; // размер разный
        Iterator<Map.Entry<Integer,List<Field>>> it = tags.entrySet().iterator();
        for(Map.Entry<Integer,List<Field>> e2:tags2.entrySet()) {
            Map.Entry<Integer,List<Field>> e = it.next();
            if(e.getKey().intValue()!=e2.getKey().intValue()) return "поле "+e.getKey(); //код поля разный
            if(e.getValue().size()!=e2.getValue().size()) return "подполей "+e.getKey(); //размер поля разный
        }
        Iterator<List<Field>> itv = tags.values().iterator();
        for(List<Field> fs2:tags2.values()) {
            Iterator<Field> itf = itv.next().iterator();
            for(Field f2:fs2) {
                int i = itf.next().deepEquals(f2);
                if(i<0) continue;
                return ""+i+(i>32 ? "(^"+(char)i+")" : "")+" "+f2.get(); //содержание поля разное
            }
        }
        return null;
    }
    public static void setWriter(Writer w) { wr = w; }
    /**
     * Набор функций для работы с такой формой организации информации как
     * List<List<String>> fields это запись состоящая из набора полей
     * List<String> field каждое поле состоит из набора подполей
     * каждое подполе представляет строку конкатенации
     * метки подполя (первый символ = charAt(0))
     * и его значения (остальные символы = substring(1))
     * первые два элемента(подполя) - обязательны всегда
     * - метка поля (состоит из трёх цифр)
     * - индикатор поля (может быть и пустым)
     *
     * функция getFieldsItem ищет первое попавшееся поле с заданной меткой.
     * если такого поля нет, оно будет добавлено с индикатором из двух пробелов
     *
     * @param fields запись
     * @param tag3 метка поля искомого элемента
     * @return List<String> field
     */
    public static List<String> getFieldsItem(List<List<String>> fields, String tag3) {
        for(List<String> f:fields)
            if (f.get(0).endsWith(tag3))
                return f;
        List<String> field = new ArrayList<String>();
        fields.add(field);
        field.add("\u0000"+tag3);
        field.add("\u001e");
        return field;
    }

/**
 * ищет поле с заданной меткой и добавляет к нему новое подполе
 * @param fields запись
 * @param tagSub4 первые три цифры - метка поля, последняя буква - метка подполя
 *          если последней буквы нет - значение подполя присваивается индикатору
 * @param s значение подполя (null игнорируется и ничего не происходит)
 */
    public static void addFieldsSub(List<List<String>> fields, String tagSub4, Object s) {
        if (s==null) return;
        List<String> field = getFieldsItem(fields,tagSub4.substring(0,3));
        if(tagSub4.length()>3)
            field.add(tagSub4.substring(3,4)+(String)s);
        else field.set(1,"\u001e"+(String)s);
    }

    /**
     * возвращает индекс поля с заданной меткой или -1 если такого нет
     * @param fields запись
     * @param tag3 искомая метка поля
     * @return индекс или -1
     */
    public static int getFieldsTagIndex(List<List<String>> fields, String tag3){
        for(int i=0; i<fields.size(); i++)
            if (fields.get(i).get(0).endsWith(tag3))
                return i;
        return -1;
    }

    /**
     * возвращает индекс подполя или -1, если такого подполя нет
     * @param field поле записи
     * @param sub метка подполя
     * @return индекс подполя или -1
     */
    public static int getFieldSubIndex(List<String> field, char sub){
        for(int i=0; i<field.size(); i++)
            if (field.get(i).charAt(0)==sub)
                return i;
        return -1;
    }

    public void lazyInit(LazyField lazy, int lazyIndex) {
        this.lazy = lazy;
        this.lazyIndex = lazyIndex;
    }
/**
 * карта подполей с проверкой на ленивую инициализацию
 * @return карта поля(0 или буква-подполя,подполе)
 */
    public Map<Character,String> getSubs() {
        if(subs==null) {
            if(lazy==null) subs = new TreeMap<>();
            else subs = fieldMap(lazy.getString(lazyIndex));
        }
        return subs;
    }
    public Map<Character,String> getSubsWas() {
        if(subs==null) setString(lazy==null ? "" : lazy.getString(lazyIndex));
        return subs;
    }
    public String getSub(char ch) {
        return getSubs().get(ch);
    }

    public String[] getSubsValues(String keys) {
        int i=-1;
        getSubs();
        String[] ss = new String[keys.length()];
        for(char ch:keys.toCharArray()) {
            ++i;
            String s = subs.get(ch);
            if(s == null) continue;
            s = s.trim();
            if(s.isEmpty()) continue;
            ss[i] = s;
        }
        return ss;
    }

    static public void xmlTags(XLines xlines,
            Map<Integer,List<Field>> tags) {
        for(Map.Entry<Integer,List<Field>> efs:tags.entrySet()) {
            xmlFields(xlines,efs.getKey(),efs.getValue());
        }
    }
    static public void xmlTags(XLines xlines,
            Map<Integer,List<Field>> tags,
            Plots plots,
            Plots noplots) {
        for(Map.Entry<Integer,List<Field>> efs:tags.entrySet()) {
            int tag = efs.getKey();
            if(!plots.check(tag)) continue;
            if(noplots.check(tag)) continue;
            xmlFields(xlines,tag,efs.getValue());
        }
    }
    static public void xmlFields(XLines xlines, int tag, List<Field> fields) {
        String fa = Integer.toString(tag);
        for(Field f:fields) {
            xlines.newline().xml1("f", "f", fa);
            for(Map.Entry<Character,String> esub: f.getSubs().entrySet()) {
                char ch = esub.getKey();
                if(ch=='\u0000') xlines.xml("s", esub.getValue());
                else xlines.xml("s", "s", "" + ch, esub.getValue());
            }
            xlines.xml2("f");
        }
    }

/**
 * строит строку представления согласно карте подполей
 * @return строка представления или null
 *//*
    public String get2() {// заменено
        char[] cs = {'^','\0'};
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Character,String> e: getSubs().entrySet()) {
            cs[1] = e.getKey();
            sb.append(cs);
            sb.append(e.getValue());
        }
        if (sb.length()==0) return null;
        if (sb.charAt(1)=='\0') return sb.substring(2);
        return sb.toString();
    }*/
    public String get() {
        Iterator<Map.Entry<Character,String>> it = getSubs().entrySet().iterator();
        if(!it.hasNext()) return "";
        StringBuilder sb;
        Map.Entry<Character,String> e = it.next();
        char ch = e.getKey();
        if(ch==0) sb = new StringBuilder(e.getValue());
        else{
            sb = new StringBuilder();
            sb.append('^');
            sb.append(ch);
            sb.append(e.getValue());
        }
        while(it.hasNext()){
            e = it.next();
            sb.append('^');
            sb.append(e.getKey());
            sb.append(e.getValue());
        }
        return sb.toString();
    }

    public int getInt(char ch) {
        try {return Integer.parseInt(getSubs().get(ch));}
        catch(Exception e) {return 0;}
    }

/**
 * построить из строки представления карту подполей
 * @param field - представление поля
 * @return карта подполей (0 или буква-подполя,подполе)
 * возвращает остатки
 */
    public void setString(final CharSequence input) {
        subs = fieldMap(input);
    }
    
    static public Map<Character,String> fieldMap(final CharSequence input) {
        Matcher m = subfieldPattern.matcher(input);
        Map<Character,String> fmap = new TreeMap<>();
        char ch = '\u0000';
        if (!m.find()) 
            fmap.put(ch, input.toString());
        else {
            int index = 0;
            do {
                int mstart = m.start();
                if(index>0 || mstart>0)
                    fmap.put(ch, input.subSequence(index, mstart).toString());
                index = m.end();
                ch = Character.toLowerCase(input.charAt(mstart+1));
            } while(m.find());
            fmap.put(ch, input.subSequence(index, input.length()).toString());
        }
        return fmap;
    }
/**
 * построить из строки представления карту подполей
 * @param field - представление поля
 * @return карта подполей (0 или буква-подполя,подполе)
 * возвращает остатки
 */
    public StringBuilder setMRCString(final String str) {
        StringBuilder sbr = null;
        StringBuilder sb = new StringBuilder(str);
        int len = sb.length();
        while (--len>=0 && sb.charAt(len)=='^') sb.setLength(len);
        String s;
        if (subs==null) subs = new TreeMap<Character,String>();
        else subs.clear();
        char ch = '\0';
        int pos = 0; // subfield-char
        int pos2; // next ^-char
        while((pos2 = sb.indexOf("^",pos))>=0) {
            s = sb.substring(pos, pos2); //.trim();
            if (!s.isEmpty()) {
                if (subs.containsKey(ch)) {
                    if (sbr == null) sbr = new StringBuilder();
                    sbr.append('^');
                    sbr.append(ch);
                    sbr.append(s);
                }
                else subs.put(ch, s);
            }
            pos = pos2+1;
            ch = Character.toLowerCase(sb.charAt(pos++));
        }
        s = sb.substring(pos); //.trim();
        if (!s.isEmpty()) {
            if (subs.containsKey(ch)) {
                if (sbr == null) sbr = new StringBuilder();
                sbr.append('^');
                sbr.append(ch);
                sbr.append(s);
            }
            else subs.put(ch, s);
        }
        return sbr;
    }

    public static String getTagsSub(Map<Integer,List<Field>> tags, String tagSub4) {
        int last = tagSub4.length()-1;
        char ch = tagSub4.charAt(last);
        if(Character.isDigit(ch))
            return getTagsSub(tags,Integer.parseInt(tagSub4),'\u0000');
        return getTagsSub(tags,Integer.parseInt(tagSub4.substring(0,last)),ch);
    }

    public static String[] getTagsSubs(Map<Integer,List<Field>> tags, String[] tagSubs4) {
        int i = 0;
        String[] ss = new String[tagSubs4.length];
        for(String s:tagSubs4)
            ss[i++] = getTagsSub(tags,s);
        return ss;
    }
/**
 * Первый обрубленый непустой
 * @param tags
 * @param tag
 * @param sub
 * @return
 */
    public static String getTagsSub(Map<Integer,List<Field>> tags, int tag, char sub) {
        List<Field> fields = tags.get(tag);
        if (fields == null) return null;
        String s = fields.get(0).getSubs().get(sub);
        if(s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
    public static String getTagsSubLast(Map<Integer,List<Field>> tags, int tag, char sub) {
        List<Field> fields = tags.get(tag);
        if (fields == null) return null;
        String s = fields.get(fields.size()-1).getSubs().get(sub);
        if(s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
    public String getSubsUpper(char sub) {
        String s = getSubs().get(sub);
        if(s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s.toUpperCase();
    }
    public static String getTagsSubUpper(Map<Integer,List<Field>> tags, int tag, char sub) {
        List<Field> fields = tags.get(tag);
        if (fields == null) return null;
        return fields.get(0).getSubsUpper(sub);
    }

    public final void setSub(char ch, String s) {
        if (s != null && !s.isEmpty()) getSubs().put(ch, s);
    }

    @Override public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Field)) return false;
        Field f = (Field)o;
        return this.get().equals(f.get());
    }

/**
 * клонировать карту подполей
 * @param subs - карта подполей, которую надо клонировать
 * @return клон исходной карты подполей
 */
    public void setSubs(final Map<Character,String> from) {
        if (subs==null) subs = new TreeMap<Character,String>();
        else subs.clear();
        for(Map.Entry<Character,String> e: from.entrySet())
            subs.put(e.getKey(), e.getValue());
    }

/**
 * вывод на печать всего тега
 * @param e - Map.Entry тега
 * @param i - свободный номер списка полей
 * @return количество напечатаных полей тега (строк)
 */
    static public int printTag(int tag, List<Field> fields, int i) {
        int n = 0;
        for(Field field:fields) {
            String s = field.get();
            if(s==null) continue;
            System.out.printf("%2d.%4d ",++n+i,tag);
            System.out.println(s);
        }
        return n;
    }

/**
 * вывод на печать карты тегов
 * @param tags - карта тегов
 */
    static public void print(Map<Integer,List<Field>> tags) {
        int i = 0;
        for(Map.Entry<Integer,List<Field>> e:tags.entrySet())
            i += printTag(e.getKey(),e.getValue(),i);
    }
/**
 * вывод на печать карты тегов
 * @param tags - карта тегов
 */
    static public void print2(Map<Integer,List<Field>> tags) {
        for(String line:txt2(tags))
            System.out.println(line);
    }
/**
 * вывод на печать карты тегов
 * @param tags - карта тегов
 */
    static public List<String> txt2(Map<Integer,List<Field>> tags) {
        List<String> lines = new ArrayList<String>();
        for(Map.Entry<Integer,List<Field>> e:tags.entrySet()) {
            String tag = "#"+e.getKey()+": ";
            for(Field field:e.getValue())
                lines.add(tag+field.get());
        }
        lines.add("*****");
        return lines;
    }

/**
 * вывод на печать всего тега
 * @param e - Map.Entry тега
 * @param i - свободный номер списка полей
 * @return количество напечатаных полей тега (строк)
 */

    static private void impSub(String sub, String line, List<String> list) {
        int n = 0;
        List<String> ws = words.cut(sub);
        for(int i=0; i<ws.size(); i+=2)
            list.add(String.format("%s%d;%s;[%s];",line,n++,ws.get(i),ws.get(i+1)));
    }

    static private void impSub24(String sub, String line, List<String> list) {
        int n = 0;
        int ispace = 0;
        int iword;
        Matcher mw = wordPattern.matcher(sub);
        Matcher ms = spacePattern.matcher(sub);
        boolean eof = false;
        do {
            if (!mw.find(ispace))
                iword = sub.length();
            else iword = mw.start();
            int istep = ispace;
            if(!ms.find(iword)){
                eof = true;
                ispace = sub.length();
            }
            else ispace = ms.start();
            list.add(String.format("%s%d\t%d\t%s",line,n++,iword-istep,
                sub.substring(iword, iword+wsize<ispace?iword+wsize:ispace)));
            while (iword+wsize<ispace) {
                iword += wsize;
                list.add(String.format("%s%d\t%d\t%s",line,n++,0,
                    sub.substring(iword, iword+wsize<ispace?iword+wsize:ispace)));
            }
        } while (!eof);
        //i,jстаток sub
    }
static private long fno;
/**
 * вывод на печать карты тегов
 * @param tags - карта тегов
 */
    static public String imp2Heads() {
        fno = 0;
        return "fno\tsub\tfld\tmst\tmfn\ttxt";
    }
    static public List<String> imp2Tags(String mst, int mfn, Map<Integer
            ,List<Field>> tags) {
        final Character chzero = '~';
        String smfn = "\t"+mst+"\t"+mfn+"\t";
        List<String> list = new ArrayList<String>();
        for(Map.Entry<Integer,List<Field>> fe:tags.entrySet()) {
            String line = "\t" + fe.getKey() + smfn;
            for(Field field:fe.getValue()) {
                String sfno = "\n" + (++fno) + "\t";
                for(Map.Entry<Character,String> se:field.getSubs().entrySet()) {
                    Character ch = se.getKey();
                    if(ch==chzero)
                        System.out.println("mfn="+mfn+" fld="+fe.getKey()+" "+chzero+"="+se.getValue());
                    String sub = ((ch.charValue() == 0) ? chzero : ch).toString();
                    String txt = se.getValue();
                    list.add(sfno+sub+line+txt);
                }
            }
        }
        return list;
    }
    static public String impHeads() {
        return "mfn\tfld\tfno\tsub\twno\tspaces\tword"+wsize;
    }
    static public List<String> impTags(Map<Integer,List<Field>> tags) {
        List<String> list = new ArrayList<String>();
        for(Map.Entry<Integer,List<Field>> e:tags.entrySet())
            impTag(e.getValue(),String.format("%03d\t",e.getKey()),list);
        return list;
    }
    static public void impTag(List<Field> fields, String line, List<String> list) {
        int n = 0;
        for(Field field:fields)
            impField(field.getSubs(),line.concat(String.valueOf(n++)).concat("\t"),list);
    }
    static private void impField(Map<Character,String> subs, String line, List<String> list) {
        for(Map.Entry<Character,String> e:subs.entrySet()) {
            Character ch = e.getKey();
            String s = (ch.charValue() == 0) ? "+" : ch.toString();
            impSub24(e.getValue(),line.concat(s).concat("\t"),list);
        }
    }
    static public List<String> impFields(List<List<String>> fields, String marker) {
        int fno = 0;
        List<String> list = new ArrayList<String>();
        if(marker != null)
            impSub24(marker,"000\t" + fno++ + "\t+\t",list);
        for(List<String> field:fields){
            String s = field.get(0).substring(1) + "\t" + fno++ + "\t";
            String s1 = field.get(1).substring(1);
            if(s.length()>0) impSub24(s1,s+"+\t",list);
            ListIterator<String> iterator =  field.listIterator(2);
            while(iterator.hasNext()) {
                String s2 = iterator.next();
                impSub24(s2.substring(1),s+s2.substring(0, 1)+"\t",list);
            }
        }
        return list;
    }

    static public int tagsFieldCount(Map<Integer,List<Field>> tags) {
        int i = 0;
        for(List<Field> e:tags.values())
            i += e.size();
        return i;
    }

/**
 * удаляет все пустые представления полей и пустые тэги из карты тегов
 * @param tags - карта тэгов
 * @return оставшееся количество полей в карте тегов
 */
    static public int packTags(Map<Integer,List<Field>> tags) {
        int len = 0;
        Iterator<Map.Entry<Integer,List<Field>>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer,List<Field>> e = it.next();
            List<Field> fields = e.getValue();
            int i = fields.size();
            while(i > 0)
                if(fields.get(--i).trim() < 0)
                    fields.remove(i);
            if (fields.isEmpty())
                it.remove();
        }
        return len;
    }

    static public int slimTags(Map<Integer,List<Field>> tags) throws IOException {
        int len = 0;
        Iterator<Map.Entry<Integer,List<Field>>> it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer,List<Field>> e = it.next();
            List<Field> fields = e.getValue();
            int i = fields.size();
            while(i > 0)
                if(fields.get(--i).slim() < 0)
                    fields.remove(i);
            if (fields.isEmpty())
                it.remove();
        }
        return len;
    }

    static public String ex10Tags(Map<Integer,List<Field>> tags) {
        if(!tags.containsKey(10)) return null;
        Field f10 = tags.get(10).get(0);
        Map<Character,String> subs = f10.getSubs();
        String s = subs.get('\u0000');
        int i = s.indexOf(' ');
        if (i<0) return null;
        Field f11;
        if(tags.containsKey(11))
            f11 = tags.get(11).get(0);
        else {
            String s10 = s.substring(0, i);
            subs.put('\u0000', s10);
            f11 = new Field();
            f11.setString(s.substring(i+1));
            tags.put(11, new ArrayList<Field>());
            tags.get(11).add(f11);
            s = s+"="+s10;
        }
        subs = f11.getSubs();
        String s11 = subs.get('\u0000');
        i = s11.indexOf(' ');
        if (i<0) return s;
        if(tags.containsKey(12)) return s;
        String s11new = s11.substring(0, i);
        subs.put('\u0000', s11new);
        Field f12 = new Field();
        String s12 = s11.substring(i+1);
        f12.setString(s12);
        tags.put(12, new ArrayList<Field>());
        tags.get(12).add(f12);
        return s+";"+s11new+";"+s12+".";
    }

    static public int mrcPackTags(Map<Integer,List<Field>> tags) {
        int len = 0;
        for(Map.Entry<Integer,List<Field>> e:tags.entrySet()) {
            List<Field> fields = e.getValue();
            int i = fields.size();
            while(i > 0)
                if(fields.get(--i).mrcPack() < 0)
                    fields.remove(i);
            if (fields.isEmpty())
                tags.remove(e.getKey());
        }
        return len;
    }

/**
 * произвести усечение крайних пробелов полей и пустые выбросить.
 * @return количество исправлений или -1 если полей вообще не осталось
 */
    public int trim() {
        int packCount=0;
        Map<Character,String> map = getSubs();
        Iterator<Map.Entry<Character,String>> it = map.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Character,String> e = it.next();
            String value = e.getValue();
            String packed = value.trim();
            if (packed.isEmpty()) {
                it.remove();
                ++packCount;
            }
            else if (packed!=value) {
                e.setValue(packed);
                ++packCount;
            }
        }
        return map.isEmpty() ? -1 : packCount;
    }

    public static String slimString(String s) throws IOException {
        String s2 = s.trim();
        Matcher m = pslim1.matcher(s2);
        while (m.find()) {
            int i = m.start();
            String s21 = s2.substring(i+1);
            s2 = s2.substring(0,i).concat(s21);
            m = pslim1.matcher(s2);
        }
        m = pslim2.matcher(s2);
        while (m.find()) {
            int i = m.start()+1;
            String s22 = s2.substring(i+1);
            s2 = s2.substring(0,i).concat(s22);
            m = pslim2.matcher(s2);
        }
        if(s2!=s && wr!=null)
            wr.write("slim("+s+")="+s2+"<end>\n");
        return s2;
    }

    public int slim() throws IOException {
        int packCount=0;
        Map<Character,String> map = getSubs();
        Iterator<Map.Entry<Character,String>> it = map.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Character,String> e = it.next();
            String value = e.getValue();
            String packed = slimString(value);
            if (packed.isEmpty()) {
                it.remove();
                ++packCount;
            }
            else if (packed!=value) {
                e.setValue(packed);
                ++packCount;
            }
        }
        return map.isEmpty() ? -1 : packCount;
    }

/**
 * произвести усечение крайних пробелов полей с кодами и пустые выбросить.
 * @return количество исправлений или -1 если ничего больше не осталось
 */
    public int mrcPack() {
        int packCount=0;
        Map<Character,String> map = getSubs();
        Iterator<Map.Entry<Character,String>> it = map.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Character,String> e = it.next();
            String value = e.getValue();
            if (e.getKey() != '\u0000') {
                String packed = value.trim();
                if (packed.isEmpty()) {
                    it.remove();
                    ++packCount;
                }
                else if (packed!=value) {
                    e.setValue(packed);
                    ++packCount;
                }
            }
        }
        return map.isEmpty() ? -1 : packCount;
    }

    public String getPack() {
        StringBuilder sb = new StringBuilder();
        Map<Character,String> map = getSubs();
        Iterator<Map.Entry<Character,String>> it = map.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Character,String> e = it.next();
            String value = e.getValue();
            String packed = value.trim();
            if (packed.isEmpty())
                it.remove();
            else {
                sb.append('^');
                sb.append(e.getKey());
                sb.append(packed);
                if (packed!=value)
                    e.setValue(packed);
            }
        }
        return sb.charAt(1)=='\u0000' ? sb.substring(2) : sb.toString();
    }

    public String getMRCPack() {
        StringBuilder sb = new StringBuilder();
        Map<Character,String> map = getSubs();
        Iterator<Map.Entry<Character,String>> it = map.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Character,String> e = it.next();
            String value = e.getValue();
            char c = e.getKey();
            if (c=='\u0000')
                sb.append(value);
            else {
                String packed = value.trim();
                if (packed.isEmpty())
                    it.remove();
                else {
                    sb.append('^');
                    sb.append(c);
                    sb.append(packed);
                    if (packed!=value)
                        e.setValue(packed);
                }
            }
        }
        return sb.toString();
    }

/**
 * строка представления битовой маски
 * @param x битовая маска
 * @return строка суммы степеней двойки
 */
    static public String bitString(int x) {
        String s = "";
        int n = 2;
        int m;
        while(x > 0) {
            m = x % n;
            if(m > 0) {
                s = "+" + String.valueOf(m) + s;
                x -= m;
            }
            n *= 2;
        }
        return s.isEmpty() ? "+0" : s ;
    }

    static public int bytes2int(byte[] bytes, int index, int len) {
        int i = bytes[index] - '0';
        while(--len>0)
            i = i * 10 + (bytes[++index] - '0');
        return i;
    }

    static void SetWords(Words newWords) { words = newWords; }

    static List<Field> getTagsFields(Map<Integer,List<Field>> tags, int tag) {
        List<Field> fields = tags.get(tag);
        if(fields==null) tags.put(tag, fields = new ArrayList<Field>());
        return fields;
    }
    static void tagsOut(Map<Integer,List<Field>> tags, java.io.OutputStream out)
            throws IOException {
        final Charset outcharset = Charset.forName("windows-1251");
        final byte[] nlcr = {13,10};
        final byte[] eor = "*****".getBytes();
        for(Map.Entry<Integer,List<Field>> e:tags.entrySet()) {
            int tag = e.getKey();
            for(Field f:e.getValue()) {
                out.write(("#"+tag+": ").getBytes());
                out.write(f.get().getBytes(outcharset));
                out.write(nlcr);
            }
        }
        out.write(eor);
        out.write(nlcr);
    }
}
