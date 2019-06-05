package marc;

import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

/**
 * измерение мелодичности
 * @author ktwice
 */
public class Melody {
    static final int maxgroup = 2;  // предел мелодичности
    static final String salower = "аеёиоуыэюя"; // гласные
    static final String sblower = "бвгджзйклмнпрстфхцчшщъь"; // согласные
    static final String sa = salower+salower.toUpperCase(); // все гласные
    static final String sb = sblower+sblower.toUpperCase(); // все согласные
    static final String[] s2 = {sa,sb};
    static final Pattern pa = Pattern.compile("["+sa+"]"); // гласная?
    static final Pattern pb = Pattern.compile("["+sb+"]"); // согласная?
    static final Pattern p = Pattern.compile("["+sa+sb+"]"); // буква?
    static final Pattern pna = Pattern.compile("[^"+sa+"]"); // не гласная?
    static final Pattern pnb = Pattern.compile("[^"+sb+"]"); // не согласная?
    static final Pattern pn = Pattern.compile("[^"+sa+sb+"]"); // не буква?
    static final Pattern[] p2 = {pa,pb};
    static final Pattern[] pn2 = {pna,pnb};
/**
 * проверяемые charset-ы
 */    
    private Charset[] charsets;
/**
 * имена проверяемых charset-ов
 */    
    private String[] charsetNames;
/**
 * накопистельная мелодичность по charset-ам
 */    
    private int[] sumResults;
/**
 * накопительное количество проверенных байт
 */    
    private int sumBytes;
    public int[] getSumResults() {return sumResults;}
    public int getSumBytes() {return sumBytes;}
/**
 * конструктор на русский
 */
    public Melody() {
        charsetNames = "utf-8,windows-1251,cp866,koi8-r".split(",");
        charsets = new Charset[charsetNames.length];
        sumResults = new int[charsetNames.length];
        int i=0;
        for(String s:charsetNames)
            charsets[i++] = Charset.forName(s);
    }
/**
 * сброс всех баллов
 */    
    public void reset() {
        sumBytes = 0;
        Arrays.fill(sumResults, 0);
    }
/**
 * подсчет мелодичности и начисление баллов
 * @param bytes буфер
 * @param off начало проверки
 * @param len длина проверки
 * @return мелодичность в процентах по charset-ам или null, если ascii only
 */    
    public int[] test(byte[] bytes, int off, int len) {
        sumBytes += len;
        int[] r = null;
        int i=0;
        int max = 0;
cs:     for(Charset charset:charsets) {
            String s = new String(bytes, off, len, charset);
            int res = test(s);
            if(res > 0) {
                res = res * 100 / s.length();
//                System.out.println(charset.name()+"="+res+" "+s);
                if(max < res) max = res;
                if(r == null) r = new int[charsets.length];
                r[i] = res;
                sumResults[i++]++; // балл за ненулевую мелодичность
            } else if(i++==0) {
                for(int j=0; j<len; j++)
                    if(bytes[off+j] < 0) 
                        continue cs;
                return null;
            }
        }
        if(r != null) {
            for(int j=0; j<r.length; j++)
                if(r[j] == max)
                    sumResults[j]++; // и балл за максимальную мелодичность
        }
//        System.out.println();
        return r;
    }
/**
 * charset с максимальными баллами
 * @return 
 */    
    public Charset best() {
        int ibest = -1;
        int max = 0;
        for(int i=0; i<sumResults.length; i++) {
            int isum = sumResults[i];
            if(isum == max) {
                ibest = -1;
            } else if(isum > max) {
                max = isum;
                ibest = i;
            }
        }
        Charset cset = ibest<0 ? null : charsets[ibest];
        System.out.println("best="+ (cset==null ? "null" : cset.name()));
        return cset;
    }
/**
 * проверка на выдающийся результат
 * @param r мелодичность по charset-ам
 * @return charset выдающегося результата или null
 */    
    public Charset check(int[] r) {
        int index = 0;
        int maxindex = 0;
        int sum = 0;
        for(int res:r) {
            sum += res;
            if(res > r[maxindex])
                maxindex = index;
            index++;
        }
        int rmax = r[maxindex];
        return (rmax+rmax > sum) ? charsets[maxindex] : null;
    }        
    
    public Map<String,Entry> entries = new TreeMap<String,Entry>();
    public Melody(String charset) {
        if (charset.isEmpty()) charset = "utf-8,windows-1251,cp866,koi8-r";
        for(String s:charset.split(","))
            add(Charset.forName(s));
    }

    
    public static char mixed(String s) {
        int mix=-1;
        for(char ch:s.toCharArray()) {
            int mix2 = ruslat(ch);
            if(mix2 >= 0) {
                if(mix < 0)
                    mix = mix2;
                else if(mix != mix2)
                    return ch;
                }
            }
        return '\u0000';
    }

    public static int ruslat(char ch) {
        if(ch<='z') {
            if(ch<'A') return -1;
            if(ch<='Z') return 1;
            if(ch>='a') return 1;
            return -1;
        }
        if(ch<'Ё') return -1;
        if(ch>'ё') return -1;
        if(ch<'А') return ch=='Ё' ? 0 : -1;
        if(ch>'я') return ch=='ё' ? 0 : -1;
        return 0;
    }

    public static String ruslatTest() {
        char ch;
        if(ruslat(ch='A'-1)==-1)
        if(ruslat(++ch)==1)
        if(ruslat(ch='Z')==1)
        if(ruslat(++ch)==-1)
        if(ruslat(ch='a'-1)==-1)
        if(ruslat(++ch)==1)
        if(ruslat(ch='z')==1)
        if(ruslat(++ch)==-1)
        if(ruslat(ch='Ё'-1)==-1)
        if(ruslat(++ch)==0)
        if(ruslat(++ch)==-1)
        if(ruslat(ch='А'-1)==-1)
        if(ruslat(++ch)==0)
        if(ruslat(ch='я')==0)
        if(ruslat(++ch)==-1)
        if(ruslat(ch='ё'-1)==-1)
        if(ruslat(++ch)==0)
        if(ruslat(++ch)==-1)
            return "Ok";
        return "" + (int)ch + "=<" + ch + "> failed.";
    }

    public static String mixedTest() {
        String s = ruslatTest();
        if(!s.equals("Ok")) return s;
        if(mixed(s="йq")=='q')
        if(mixed(s="й q")=='q')
        if(mixed(s=" йq")=='q')
        if(mixed(s=" й q")=='q')
        if(mixed(s="qй")=='й')
        if(mixed(s="q й")=='й')
        if(mixed(s=" qй")=='й')
        if(mixed(s=" q й")=='й')
        if(mixed(s="")=='\u0000')
        if(mixed(s=" ")=='\u0000')
        if(mixed(s="  ")=='\u0000')
        if(mixed(s="  йй ")=='\u0000')
        if(mixed(s="  qq ")=='\u0000')
        if(mixed(s="  йй й")=='\u0000')
        if(mixed(s="  qq q")=='\u0000')
        if(mixed(s="qq q")=='\u0000')
        if(mixed(s="йй й")=='\u0000')
        if(mixed(s="q q")=='\u0000')
        if(mixed(s="й й")=='\u0000')
            return "Ok";
        return "<" + s +"> failed.";
    }

    public class Entry {
        private int melody = 0;
        private Charset charset;
        public Entry(Charset charset) {this.charset = charset;}
        public int getMelody() {return melody;}
        public Charset getCharset() {return charset;}
        public void addMelody(String s) {melody+=test(s);}
    }

/**
 * регистрация участника соревнований
 * @param charset
 */
    public void add(Charset charset) {
        entries.put(charset.name(), new Entry(charset));
    }

/**
 * @param nearestMaxPercent предел серьезности ближайшего соперника
 * @return чемпион с заданным отрывом
 */
    public Charset bestCharset(int nearestMaxPercent) {
        Entry e1 = null;
        int i2 = 0;
        int i1 = -1;
        for(Entry e:entries.values()) {
            int i = e.getMelody();
            if (i>i1) {
                i2 = i1;
                i1 = i;
                e1 = e;
            }
            else if (i>i2) i2 = i;
        }
        System.out.print("\nbestCharset="+e1.getCharset().name()+"("+i1+","+i2+")");
        return i2*100<i1*(nearestMaxPercent) ? e1.getCharset() : null;
    }

/**
 * @return рекорд мелодичности
 */
    public int maxMelody() {
        int i = -1;
        for(Entry e:entries.values()) {
            int ie = e.getMelody();
            if (ie>i) i = ie;
        }
        return i;
    }

/**
 * @param s измеряемый текст
 * @return мелодичность текста
 */
    private static int test(String s) {
      int r = 0; // букв даже не было
      Matcher[] mn2 = null; // не буква {гласная,согласная}
      Matcher mn = null; // не буква
      Matcher m = p.matcher(s); // буква
      int inext =0; // последняя найденная не буква
      while(m.find(inext)) { // ищем букву следующего слова
        int wstart = m.start();  // первая буква слова
        int i2 = s2[0].indexOf(s.charAt(wstart))<0 ? 1 : 0; // 0 - гласная
        int istart = wstart; // начало группы
        if (mn2==null) mn2 = new Matcher[] {pna.matcher(s), pnb.matcher(s)};
        while(true){ // начиная со следующей ищем
            if (!mn2[i2].find(istart+1)) { // ищем конец группы
                inext = s.length(); // группа продолжалась до конца строки
                if (inext-istart<=maxgroup) // группа не слишком длинная
                    if(inext-wstart>maxgroup) // слово не слишком короткое
                         r += inext-wstart; // добрать последнее слово
                return r;
            }
            inext = mn2[i2].start();  // следующее начало
            if (inext-istart>maxgroup) { // слишком длинная группа
                if(mn==null) mn = pn.matcher(s);
                if (mn.find(inext)) { // конец слова
                    inext = mn.start()+1; // начало следующего слова
                    break;// уходим на следующее слово
                }
                return r;
            }
            if (s2[1-i2].indexOf(s.charAt(inext))<0) { // это не буква ?
                if(inext-wstart>maxgroup)
                    r += (inext-wstart); // отличное было слово
                ++inext; // сдвигаем на возможно букву
                break; // уходим на следующее слово
            }
            i2 = 1-i2; // отличная была группа
            istart = inext; // новое начало группы
        }
      }
      return r;
    }

}
