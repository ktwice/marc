package marc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author k2
 */
public class TextBook {
/**
 * если выставить в true будет разделять по упрощенному принципу:
 * из БД UMLI или нет.
 */    
    private static boolean umli = false;
    public static final String[] textbooks={"ОСН","М/У"};
/**
 * добавочные значения 900-х подполей,
 * интерпретируемые как гриф учебной литературы
 */    
    private static final String[] recommends={"_МО","МО","УМО"};// SORTED!!!
    public static void setUmli(boolean umli2) {umli = umli2;}
/**
 * длина массива textbooks={"ОСН","М/У"...
 */    
    public static final int bmaxindex = textbooks.length;
    /**
     * 0-основная.дополнительная    1-учебно-методическая
     */
    private int bindex; 
    /**
     * Номер записи источника
     */
    private int mfn;
    /**
     * Источник
     */
    private String bd;
    /**
     * год издания
     */
    private String year;
    /**
     * дата создания записи
     */
    private String v932;
    /**
     * Гриф учебной литературы
     */
    private String recommend;
    /**
     * количество экземпляров
     */
    private int count=0;
    private int minus=0;
    /**
     * массив составляющих для списка литературы
     * 0 - автор + пробел
     * 1 - название
     * 2 - пробел-тире-пробел + издательство
     * 3 - пробел-тире-пробел + и т.д.
     */
    private String[] texts;
    /**
     * последняя ссылка
     */
    private String http;
    public int getBIndex() {return bindex;}
    public int getBIndex(boolean bdop) {return bindex+(bdop?bmaxindex:0);}
    public int getCount() {return count;}
    public int getMfn() {return mfn;}
    public String getBd() {return bd;}
    public String getYear() {return year;}
    public String getRecommend() {return recommend;}
    public String getHttp() {return http;}
    public TextBook(String bd, int mfn) {
        this.bd=bd;
        this.mfn=mfn;
    }
    public boolean checkNew(String y){
        return year.compareTo(y) >= 0;
    }
    public Field newField() {
        Field f = new Field();
        Map<Character,String> subs = f.getSubs();
        subs.put('a', getId());
        subs.put('b', ""+count);
        if(minus > 0) subs.put('c', ""+minus);
        subs.put('e', year);
        if(recommend != null) subs.put('f', recommend);
        subs.put('x', getText());
        if(http != null) subs.put('z', http);
        return f; 
    }
    public String getId() {
        return bd+"-"+mfn;
    }
    @Override
    public String toString() {
        return getId()+" "+getText();
    }
    public String getText() {
        String s = "";
        for(String t:texts)
            s += t;
        return s;
    }
    public String[] getTexts() {
        return texts;
    }
    public String read910(List<Field> list910) {
        int n = 0;
        int m = 0;
        if(list910 != null)
        for(Field f:list910) {
            String s = f.getSubs().get('a');
            if(s==null || s.isEmpty()) return "нет типа литературы";
            int ch = s.charAt(0);
            switch(ch){
                case '1':
                    ++m;
                case '0':
                    ++n;
                    break;
                case 'U': case 'u':
                    s = f.getSubs().get('1');
                    if(s!=null)
                        try {
                            int n910 = Integer.parseInt(s.trim());
                            if(n910<0)return "минус в количестве зкземпляров v910^1="+s;
                            n += n910;
                        }
                        catch(Exception e) {
                            return "мусор в количестве зкземпляров v910^1="+s;
                        }
                    s = f.getSubs().get('2');
                    if(s!=null)
                        try {
                            int m910 = Integer.parseInt(s.trim());
                            if(m910<0)return "минус в количестве зкземпляров v910^2="+s;
                            m += m910;
                        }
                        catch(Exception e) {
                            return "мусор в количестве зкземпляров v910^2="+s;
                        }
                    break;
            }
        }
        this.count = n;
        this.minus = m;
        if(m>n) System.out.println("*** выдано больше возможного "+this.getId());
        return null;
    }
    public String[] readTexts(Map<Integer,List<Field>> tags) {
        texts = Field.tagsTexts(tags);
        return texts;
    }
    public boolean read(Map<Integer,List<Field>> tags){
        readTexts(tags);
        bindex = umli ? ("UMLI".equals(bd) ? 1 : 0) : tagsBindex(tags);
        http = Field.getTagsSub(tags, 951, 'i');
        v932 = Field.getTagsSub(tags, 932, '\0');
        year = Field.getTagsSub(tags, 210, 'd');
        if(year==null)
            year = Field.getTagsSub(tags, 461, 'h');
        if(year==null) {
            year = "";
            System.out.println("*** mfn="+mfn+" без года издания");
        }else if(year.startsWith("[") && year.endsWith("]"))
            year = year.substring(1, year.length()-1);
        List<Field> fs = tags.get(900);
        if(fs == null) 
            System.out.println("*** mfn="+mfn+" без 900 поля");
        else {
recom:  for(Field f:fs) {
            Map<Character,String> subs = f.getSubs();
            for(String rec:subs.values())
                if(Arrays.binarySearch(recommends, rec) >= 0 || rec.startsWith("jj")){
                    recommend = rec;
                    break recom;
                }
        }
        }
        return (count>0 || http!=null); // в список литературы если есть экземпляры или ссылка
    }
    public void xmlb(XLines xlines) {
        xlines.xml("bt",textbooks[bindex]);
        xlines.xml("by",year);
        xlines.xml("bc",""+count);
        if(http!=null) xlines.xml("bh");
        if(recommend!=null) xlines.xml("br");
    }
    private int tagsBindex(Map<Integer,List<Field>> tags) {
        if(tags.containsKey(900))
        for(Field f:tags.get(900)){
            for(String s:f.getSubs().values())
                    if(s.trim().toLowerCase().startsWith("j4"))
                        return 1;
        }
        return 0;
    }
    public void koxml(XLines koxml, XLines x) {
        String attr="";
        attr += XLines.attr("xml:id", getId());
        attr += XLines.attr("n", ""+count);
        if(minus > 0) attr += XLines.attr("mn", ""+minus);
        if(v932 != null) attr += XLines.attr("v932", v932);
        attr += XLines.attr("y", year);
        koxml.xml1("b", attr);
        if(bindex == 1) koxml.xml("m");
        koxml.xml("r", recommend);
        koxml.xml("h", http);
        if(texts!=null)
            for(String text:texts)
                koxml.newline().xml("t", "", text);
        if(x != null) {
            if(!x.isEmpty()) {
                koxml.add(x);
                x.reset();
            }
        }
        koxml.xml2("b");

    }
}
