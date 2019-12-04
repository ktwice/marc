package marc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author k2
 */
public class Marc {
    static Charset charset = Charset.forName("windows-1251");
    public static void main(String[] args)
            throws FileNotFoundException, IOException, Exception {
//        testXRF(args[0]);
    for(String s:System.getProperties().stringPropertyNames())
        System.out.println("Properties."+s+"="+System.getProperties().getProperty(s));
        try {
        System.out.println("started. "+(new Date()));
        Map<String,String> map = new TreeMap<>();
        map.put("format", args.length>0 ? args[0] : "");
        map.put("name", args.length>1 ? args[1] : "");
        map.put("tag", args.length>2 ? args[2] : "");
        map.put("regex", args.length>3 ? args[3] : "");
        String name = map.get("name");
        String s = map.get("format").toLowerCase();
            //        System.out.println("tag="+map.get("tag"));
            //        System.out.println("regex="+map.get("regex"));
            //        if (s.equals("mrc")) testMRC(name,"cp866",map);
            //        if (s.equals("mrc")) testMRC(name,"windows-1251",map);
            //        if (s.equals("mrc")) importMRC(name,"windows-1251",map);
            switch (s) {
                case "test":
                    String[] ss = new String[] {
                        "rere куку",
                        "куку папа",
                        "hi mama",
                        "хеллоу, ghbdtn",
                        "КАБiNET",
                        ""
                    };
                    for(String s1:ss)
                        System.out.println("Words.farChar("+s1+")="+Words.farChar(s1));
//                    s = "^ann^bxxx";
//                    System.out.println(">>"+s+"<<");
//                    for(Map.Entry<Character, String> e:Field.fieldMap(s).entrySet())
//                        System.out.println(">>> ch("+e.getKey()+")="+e.getValue()+"<<<");
    /* 
                    String st = " h fhfhf  - lf/ lf;(mmmm).  . kk ( kk )    ;  kk  , tt .";
                    String sr = st;
                    sr = sr.replaceAll("[ ]+", " "); // убрать двойные пробелы
                    for(char c:"(".toCharArray()) // добавить пробел перед
                        sr = sr.replaceAll("[ ]*\\"+c, " "+c);
                    for(char c:");.,".toCharArray()) // добавить пробел после
                        sr = sr.replaceAll("\\"+c+"[ ]*", c+" ");
                    for(char c:"/-);.,".toCharArray()) // удалить пробел перед
                        sr = sr.replaceAll("[ ]+\\"+c, ""+c);
                    for(char c:"/-(".toCharArray()) // удалить пробел после
                        sr = sr.replaceAll("\\"+c+"[ ]+", ""+c);
System.out.println(">>>"+st+"<<<");
System.out.println(">>>"+Words.normalizeSpaces(sr).toUpperCase()+"<<<");
System.out.println(">>>"+sr.trim()+"<<<");
//System.out.println(st.trim().replaceAll("[ ]+", " ").replaceAll("[ ]+([ ]+", " (").replaceAll("[ ]+)[ ]+", ") "));
     break;
                    String line;
                    try (TXT64 txt = TXT64.open("ko.txt",Charset.forName("windows-1251"))) {
                        while((line=txt.readLine()) != null) {
                            System.out.println(line.trim());
                        }
                    }
                    long a = 5;
                    long b = 4;
                    System.out.println(""+a+" / "+b+" = "+(a / b));
                    System.out.println(""+a+" % "+b+" = "+(a % b));
                    a = -a;
                    System.out.println(""+a+" / "+b+" = "+(a / b));
                    System.out.println(""+a+" % "+b+" = "+(a % b));
                    a = -a;
                    b = -b;
                    System.out.println(""+a+" / "+b+" = "+(a / b));
                    System.out.println(""+a+" % "+b+" = "+(a % b));
                    a = -a;
                    System.out.println(""+a+" / "+b+" = "+(a / b));
                    System.out.println(""+a+" % "+b+" = "+(a % b));
                    Rec64 rec64 = new Rec64(8);
                    long[] longs = {Long.MIN_VALUE,Integer.MAX_VALUE,0,Integer.MIN_VALUE,Long.MAX_VALUE};
                    long[] dlongs = {-2,-1,0,1,2};
                    for(long i:longs) {
                        for(long d:dlongs) {
                            long l=i+d;
                            rec64.putLowHighLong(0, l);
                            long l2 = rec64.getLowHighLong(0);
                            System.out.println(""+(l==l2)+" l="+l+" l2="+l2);
                        }
                    } */
                    break;
                case "mrcimport":
                    importMRC(name,"utf-8",map);
                    break;
                case "plus":
                    plus(args);
                    break;
                case "farcheck":
                    farcheck(name);
                    break;
                case "txt2xrf":
                    txt2xrf(name);
                    break;
                case "xml2xrf":
                    xml2xrf(name, map.get("tag"));
                    break;
                case "xml2txt":
                    xml2txt(name);
                    break;
                case "rdr40":
                    rdr40(name);
                    break;
                case "xrf2rdr":
                    xrf2rdr(name);
                    break;
                case "mst2xml":
                    mst2xml(name);
                    break;
                case "xrf2t":
                    xrf2t(name);
                    break;
                case "xrf2xml":
                    xrf2xml(name,map.get("tag"),map.get("regex"));
                    break;
                case "ko":
                    ko(name,map.get("tag"));
                    break;
                case "ko2scan":
                    ko2scan(name,map.get("tag"));
                    break;
                case "group2vuz":
                    group2vuz(!"fix".equals(name));
                    break;
                case "ini2txt":
                    ini2txt(name,map.get("tag"));
                    break;
                case "txt2ini":
                    txt2ini(name,map.get("tag"));
                    break;
                case "mrcprint":
                    printMRC(name,map.get("tag"));
                    break;
                case "mrcwrite":
                    writeMRC(item(args,1),item(args,2),item(args,3),item(args,4));
                    break;
                case "mrcexport":
                    exportMRC(name,map);
                    break;
                case "slim":
                    slimMRC(name,"utf-8",map);
                    break;
                case "xrf2txt":
                    xrf2txt(name,map.get("tag"));
                    break;
                case "xrf2print":
                    xrf2print(name);
                    break;
                case "xrf2history":
                    xrf2history(name,map.get("tag"));
                    break;
                case "xrf2":
                    xrf2(name);
                    break;
                case "xrf2norma":
                    xrf2norma(name,!"fix".equals(map.get("tag")));
                    break;
                case "xrf2undelete":
                    xrf2undelete(name,!"fix".equals(map.get("tag")));
                    break;
                case "xrf2imp":
                    String[] names = Arrays.copyOfRange(args, 1, args.length);
                    xrf2imp(names);
                    break;
                case "disc":
                    testDisc(name,map);
                    break;
                case "testxrf":
                    testXRF(name,map);
                    break;
                case "wss":
                    importWss(name,map);
                    break;
                case "mrc":
                    mrc2txt(name,"");
                    break;
                case "xrf700":
                    xrf700(name);
                    break;
                case "xrf910":
                    xrf910(name);
                    break;
                case "mrc2xml":
                    mrc2xml(name,map.get("tag"));
                    break;
                case "mrc2mrc":
                    mrc2mrc(name,map.get("tag"));
                    break;
                case "mrcpiece":
                    mrcPiece(name,map.get("tag"));
                    break;
                case "xml2mrc":
                    xml2mrc(name,map.get("tag"));
                    break;
                case "dir":
                    WSS.importDir(name);
                    break;
                case "map920":
                    map920(name);
                    break;
                case "mixedtest":
                    System.out.println("mixedTest: "+Melody.mixedTest());
                    break;
                case "euler":
                    System.out.println("euler="+euler(1000)+".");
                    break;
                case "renum":
                    renum(name);
                    break;
                case "peri":
                    peri();
                    break;
            }
        System.out.println("completed. "+(new Date()));
        } catch(Exception e) {
            try (PrintWriter pw = new PrintWriter("Marc.err")) {
                e.printStackTrace(pw);
            }
            throw e;
        }
    }
    public static void renum(String name) throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        Map<Character,Integer> counters = new TreeMap<>();
        try (
                XRF64 xrf = XRF64.open(dirname+name, "rw");
                ) {
            Charset charset = Charset.forName("windows-1251");
            int mfn=0;
            int nxtmfn = xrf.getMST().nxtmfn();
            while(++mfn < nxtmfn) {
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                String v200a = Field.getTagsSub(tags, 200, 'a');
System.out.println("mfn="+mfn+" v200a="+v200a);
                if(v200a==null) continue;
                char ch = Character.toUpperCase(v200a.charAt(0));
                String v903 = Field.getTagsSub(tags, 903, '\u0000');
                Integer i = counters.get(ch);
                if(i==null) i = new Integer(0);
                counters.put(ch, ++i);
                String new903 = ""+ch+i;
                if(new903.equals(v903)) continue;
System.out.println(v903+"->"+new903);
                tags.get(903).get(0).put('\u0000', new903);
                xrf.write(tags, mfn);
            }
        }
    }
    public static void peri() throws FileNotFoundException, IOException {
        final int tag = 909;
            Map<Integer,List<Field>> ftags = new TreeMap<>();
            Field.tagsAdd(ftags, 903, Field.create("*"));
            Field.tagsAdd(ftags, 910, Field.creatext("^a0^b1^c20140128"));
            Field.tagsAdd(ftags, 920, Field.create("NJ"));
            Field.tagsAdd(ftags, 933, Field.create("*"));
            Field.tagsAdd(ftags, 934, Field.create("*"));
            Field.tagsAdd(ftags, 936, Field.create("*"));
            Counter2 c2 = new Counter2();
        Set<String> set = new TreeSet<>();
        String name = "peri";
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        try (
                XRF64 xrf = XRF64.open(dirname+name, "r");
                TXT64 txt = TXT64.rewrite("nj");
                ) {
            int mfn=0;
            int nxtmfn = xrf.getMST().nxtmfn();
            while(++mfn < nxtmfn) {
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                if(!tags.containsKey(tag)) {
                    System.out.println("mfn="+mfn+" v"+tag+" not exist");
                    continue;
                }
                c2.inc("журналов");
                String j = tags.get(903).get(0).getSub('\u0000');
                ftags.get(933).get(0).setSub('\u0000', j);
                for(Field f:tags.get(tag)) {
                    String s = f.getSub('h');
                    if(s==null) {
                        System.out.println("mfn="+mfn+" v"+tag+"^h not exist");
                        continue;
                    }
                    c2.inc("годов журналов");
                    String y = f.getSub('q');
                    String m = f.getSub('d');
                    ftags.get(934).get(0).setSub('\u0000', y);
                    ftags.get(910).get(0).setSub('d', m);
                    System.out.println("mfn="+mfn+" v"+tag+"^h= "+s);
                    List<String> ns = peri3(s);
                    for(String sn:ns){
                        c2.inc("генерировано записей");
                        System.out.println("\t"+sn);
                        ftags.get(903).get(0).setSub('\u0000', j+"/"+y+"/"+sn);
                        ftags.get(936).get(0).setSub('\u0000', sn);
                        String[] sns = sn.split("/");
                        c2.inc("генерировано номеров",sns.length);
                        if(sns.length==1) {
                            ftags.remove(931);
                        } else{
                            List<Field> fs = ftags.get(931);
                            if(fs==null) ftags.put(931, fs=new ArrayList<>());
                            else fs.clear();
                            for(String snn:sns) {
                                fs.add(Field.create(j+"/"+y+"/"+snn));
                            }
                        }
                        txt.write(ftags);
                        c2.inc("добавлено записей");
                    }
//                    txt.write(ftags);
                    set.addAll(ns);
                }
            }
        }
        for(String s:set)
            System.out.println(""+s);
        c2.print();
    }
    private static String[] peri2(String s){
        s = s.trim();
        int i = 0;
        while(i != s.length()){
            i = s.length();
            s = s.replace("  ", " ");
            s = s.replace(" -", "-");
            s = s.replace("- ", "-");
        }
        return s.split(" ");
    }
    private static List<String> peri3(String s){
        String[] s2 = peri2(s);
        List<String> list = new ArrayList<>();
        for(String s1:s2) {
//            char c = s1.charAt(0);
//                if(!Character.isDigit(c)){
//                    try {
//                        list.set(list.size()-1, list.get(list.size()-1)+" "+s1);
//                    }
//                    catch(Exception e) {
//                        System.out.println("**********error= "+s1);
//                    }
//                    continue;
//                }
                int i = s1.indexOf('-');
                if(i<0) {
                    list.add(s1);
                    continue;
                }
                try {
                    int n1 = Integer.parseInt(s1.substring(0, i));
                    int n2 = Integer.parseInt(s1.substring(i+1));
                    if(n2<=n1){
                        System.out.println("**********error= "+s1);
                    }
                    for(int n=n1; n<=n2; n++)
                        list.add(""+n);
                }
                catch(Exception e) {
                    list.add(s1);
                }
        }
        return list;
    }
    public static String item(String[] a, int index) {
        return a.length>index ? a[index] : "";
    }
    public static String[] get700(String s) {
        if(s==null) return null;
        String st = s.trim();
        int i = st.indexOf(' ');
        if(i<0) return null;
        String s1 = st.substring(0,i);
        String s2 = st.substring(i+1);
        int i2 = s2.indexOf('.');
        if(i2>0) return new String[] {s1,s2,null};
        String s3 = s2.substring(0,1)+".";
        i2 = s2.indexOf(' ');
        if(i2>0 && i2+1!=s2.length()) s3 += " "+s2.substring(i2+1, i2+2)+".";
        return new String[] {s1,s3,s2};
    }
    public static void xrf700(String name)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
//        int mfn;
        int[] t = new int[] {700,701,702,961};
        int mfnReaded = 0;
        int fieldUpdated = 0;
        int mfnUpdated = 0;
        try (
                XRF64 xrf = XRF64.open(dirname+name, "rw");
                FileOutputStream fos = new FileOutputStream(name+"_700.txt")
                ) {
            Charset charset = Charset.forName("windows-1251");
            int mfn=0;
            int nxtmfn = xrf.getMST().nxtmfn();
            while(++mfn < nxtmfn) {
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                ++mfnReaded;
    //            tags = xrf.readUp().newTags();
                int i=0;
                for(int tag:t)
                    if(tags.containsKey(tag))
                    for(Field f:tags.get(tag)){
                        Map<Character,String> subs = f.getSubs();
                        String s = subs.get('a');
                        String[] s3 = get700(s);
                        if(s3==null) continue;
                        subs.put('a', s3[0]);
                        subs.put('b', s3[1]);
                        if(s3[2]!=null) subs.put('g', s3[2]);
                        ++i;
                        fos.write("mfn=".concat(""+mfn+" ").concat(s).concat("\r\n").getBytes(charset));
                        for(String s1:s3)
                            if(s1!=null) fos.write(" ".concat(s1).concat("\r\n").getBytes(charset));
                    }
                if(i==0) continue;
                fieldUpdated += i;
                mfnUpdated++;
                xrf.write(tags, mfn);
            }
            fos.write("mfnReaded=".concat(""+mfnReaded).concat("\r\n").getBytes(charset));
            fos.write("fieldUpdated=".concat(""+fieldUpdated).concat("\r\n").getBytes(charset));
            fos.write("mfnUpdated=".concat(""+mfnUpdated).concat("\r\n").getBytes(charset));
        }
        System.out.print("\n mfnReaded=" + mfnReaded);
        System.out.print("\n fieldUpdated=" + fieldUpdated);
        System.out.print("\n mfnUpdated=" + mfnUpdated);
    }
    public static void xrf910(String name)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
//        int mfn;
        int[] t = new int[] {910,940};
        int mfnReaded = 0;
        int fieldUpdated = 0;
        int mfnUpdated = 0;
        try (XRF64 xrf = XRF64.open(dirname+name, "rw"); 
                FileOutputStream fos = new FileOutputStream(name+"_910.txt")
                ) {
            Charset charset = Charset.forName("windows-1251");
            int mfn=0;
            int nxtmfn = xrf.getMST().nxtmfn();
            while(++mfn < nxtmfn) {
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                ++mfnReaded;
    //            tags = xrf.readUp().newTags();
                int i=0;
                for(int tag:t)
                    if(tags.containsKey(tag)){
                        Iterator<Field> it=tags.get(tag).iterator();
                        while(it.hasNext()){
                            Field f=it.next();
                    //for(Field f:tags.get(tag)){
                        Map<Character,String> subs = f.getSubs();
                        String s = subs.get('a');
                        if(s==null)continue;
                        if(!s.equals("6"))continue;
                        s = subs.get('v');
                        if(s==null)continue;
                        if(s.contains("2010"))continue;
                        s = subs.get('b');
                        if(s==null)continue;
                        if(s.contains("б/уч"))continue;
                        fos.write("mfn=".concat(""+mfn+" "+tag+"=").concat(f.get()).concat("\r\n").getBytes(charset));
                        ++i;
                        it.remove();
                    }}
                if(i==0) continue;
                fieldUpdated += i;
                mfnUpdated++;
                xrf.write(tags, mfn);
            }
            fos.write("mfnReaded=".concat(""+mfnReaded).concat("\r\n").getBytes(charset));
            fos.write("fieldUpdated=".concat(""+fieldUpdated).concat("\r\n").getBytes(charset));
            fos.write("mfnUpdated=".concat(""+mfnUpdated).concat("\r\n").getBytes(charset));
        }
        System.out.print("\n mfnReaded=" + mfnReaded);
        System.out.print("\n fieldUpdated=" + fieldUpdated);
        System.out.print("\n mfnUpdated=" + mfnUpdated);
    }
    public static void rdr40(String full)
            throws FileNotFoundException, IOException {
        String name = "rdr\\rdr";
        String dirname = "c:\\irbis64\\datai\\";
        int mfnReaded = 0;
        Map<Integer,List<Field>> tags;
        XRF64 xrf;
        try (FileOutputStream fos = new FileOutputStream("rdr40.txt")) {
            Map<String,Map<String,Map<String,List<String>>>> map =
                    new TreeMap<>();
            xrf = XRF64.open(dirname+name, "r");
            int mfn=0;
            int nxtmfn = xrf.getMST().nxtmfn();
            while(++mfn < nxtmfn) {
                ++mfnReaded;
                int i=0;
                if(xrf.next() > 0) continue;
                tags = xrf.read(mfn);
                if(!tags.containsKey(40)) continue;
                String sout=null;
                for(Field f:tags.get(40)) {
                    Map<Character,String> subs = f.getSubs();
                    if(!subs.containsKey('g')) continue;
                    if(!subs.get('f').startsWith("*")) continue;
                    if(sout==null) sout=tags.get(24).get(0).get()+" "+tags.get(10).get(0).get();
                    String db = subs.get('g');
                    String v903 = subs.get('a');
                    String v910 = "b="+subs.get('b')+";"
                            + "h="+subs.get('h')+";"
                            + "d="+subs.get('k')+";"
                            ;
                    String book = subs.get('d')+" "+subs.get('v');
                    sout += "\t" +db+" "+v903+" "+v910+" "+book+"\r\n";
                    rdr40add(map,db,v903,v910,book);
                    i++;
                }
                if(sout!=null) if(!full.isEmpty())fw(fos,sout);
            }
            for(String db:map.keySet()){
                mfnReaded=0;
                //System.out.println(db);
                Map<String, Map<String, List<String>>> db_item = map.get(db);
                xrf.close();
                xrf = XRF64.open(dirname+db+"\\"+db, "r");
                mfn = 0;
                nxtmfn = xrf.getMST().nxtmfn();
                while(++mfn < nxtmfn) {
                    ++mfnReaded;
                    if(xrf.next() > 0) continue;
                    tags = xrf.read(mfn);
                    if(!tags.containsKey(910)) continue;
                    String v903 = tags.get(903).get(0).get();
                    Map<String, List<String>> v903_item = db_item.get(v903);
                    String sout=null;
                    int ibook = 0;
                    for(Field f:tags.get(910)) {
                        long i = book40(f);
                        if(i==0) continue;
                        ibook += i;
                        if(sout==null) sout = db+" "+v903;
                        String b910 = book910(f);
                        sout+="\t"+i+"экз. "+b910;
                        if(v903_item==null)
                            sout+=" ***нет читателей";
                        else {
                            List<String> v910_item = v903_item.get(b910);
                            if(v910_item==null)
                                sout +=" ***нет читателей этих экземпляров";
                            else {
                                int irdr = v910_item.size();
                                ibook -= irdr;
                                if(irdr!=i)
                                    sout +=" ***"+irdr+" читателей этих экземпляров";
                                v903_item.remove(b910);
                                }
                            }
                        sout += "\r\n";
                        }
                    if(sout==null) continue;
                    if(full.isEmpty()) // задана краткая форма вывода
                        if(ibook==0) continue;
                    fw(fos,sout);
                    }
                    for(Map.Entry<String, Map<String, List<String>>> e:db_item.entrySet()) {
                        for(Map.Entry<String, List<String>> e1:e.getValue().entrySet()) {
                            for(String book:e1.getValue()) {
        fwln(fos,"***нет отметок в "+db+" "+e.getKey()+" "+e1.getKey()+" "+book);
                            }
                        }
                    }
                }
        }
        xrf.close();
//        System.out.print("\n mfnReaded=" + mfnReaded);
  //      System.out.print("\n fieldUpdated=" + fieldUpdated);
    //    System.out.print("\n mfnUpdated=" + mfnUpdated);
    }
    public static String fwln(FileOutputStream fos, String s)throws IOException {
        return fw(fos,s+"\r\n");
    }
    public static String fw(FileOutputStream fos, String s)throws IOException {
        fos.write(s.getBytes(charset));
        return s;
    }
    public static long book40(Field f) {
        Map<Character,String> subs = f.getSubs();
        String suba = subs.get('a');
        if("U".equals(suba)) {
            String sub2 = subs.get('2');
            if(sub2==null) return 0;
            return Long.parseLong(sub2);
        }
        if("1".equals(suba)) return 1;
        return 0;
    }
    public static String book910(Field f) {
        Map<Character,String> subs = f.getSubs();
        return "b="+subs.get('b')+";"
               + "h="+subs.get('h')+";"
               + "d="+subs.get('d')+";"
               ;
    }
/**
 * Расчет книгообеспеченности ред 2019-12-03
 * @param name строка года расчета
 * @param names список баз для поиска литературы через дефис (минус)
 * @throws IOException 
 */
    public static void ko(String name, String names) throws IOException {
        Ko2 k2 = new Ko2();
        try(Closeable ca = k2.readKo(Integer.parseInt(name))) {
            k2.disc_rang3();
            k2.checkBooks(names);
            k2.writeDiscs();
        }
    }
    public static void ko2scan(String name, String names) throws IOException {
        Ko2 k2 = new Ko2();
        //int year = Integer.parseInt(name);
        //k2.readGroups("vuz",year);
        //k2.bsInits();
        //if(names==null || names.isEmpty())
          //  names="book-umli-inos-abs";
        System.out.println("name="+name);
        for(String s:names.split("-"))
            k2.scanBooks(s);
//        for(String smfn:k2.mfnList)
//            System.out.println(smfn);

        //k2.writeflt();
        //k2.rewrite("vuz");
    }

    public static void testWords(String name, Map<String,String> map)
            throws FileNotFoundException, IOException {
        Words words = new Words(24,4);
        String txt = "      GhtgfhfbzFyfnjvbxtcrfzReleaseTwentyTwo900000-1";
        for(String s:words.cut(txt)) System.out.println(s+";");
    }

    public static void importWss(String name, Map<String,String> map)
            throws FileNotFoundException, IOException {
        try (Writer wr = new FileWriter(name+".txt")) {
            wr.write("mfn\tfld\tfno\tsub\twno\tspaces\tword");
        }
    }

    public static void exportMRC(String name, Map<String,String> map)
            throws FileNotFoundException, IOException {
        MRC mrc = new MRC();
        mrc.open(name, "r");
        mrc.export(1, 1);
        mrc.close();
    }

    public static void import2(String dname, String charsetName, Charset charsetout)
            throws FileNotFoundException, IOException {
        int n = 0;
        File dir = new File(dname);
        if (!dir.isDirectory()) return;
        File[] files = dir.listFiles();
        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return (int)(((File)o1).lastModified() - ((File)o2).lastModified());
            }
        };
        Arrays.sort(files, comparator);
        RandomAccessFile rafExport = new RandomAccessFile(dname+".mrc","rw");
        try {
            for(File f:files)
                n += import3(f.getName(),charsetName,rafExport,charsetout);
        }
        finally {
            rafExport.setLength(n);
            rafExport.close();
        }
        System.out.print("\nBytes exported="+n);
    }

    public static int import3(String fname, String charsetName
            , RandomAccessFile raf, Charset charsetout)
            throws FileNotFoundException, IOException {
        Melody melody = null;
        Charset charset = null;
        if (charsetName.isEmpty())
            melody = new Melody("");
        else charset = Charset.forName(charsetName);
        MRC mrc = new MRC();
        MRCRec rec = new MRCRec(charset);
        int n = 0;
        mrc.open(fname, "r");
        while(mrc.next() > 0) {
            byte[] bytes = mrc.read();
            if (charset!=null)
                rec.wrap(bytes,charset);
            else{
                charset = rec.wrap(bytes,melody);
                if(charset==null && n>10)
                    charset = rec.getCharset();
            }
            MRCRec.write(rec.getFields(),raf,charsetout);
            }
        return 0;
    }

    public static void printMRC(String name, String charsetName)
            throws FileNotFoundException, IOException {
        MRCRec rec;
        rec =  new MRCRec(charsetName.isEmpty() ? null : Charset.forName(charsetName));
        MRC mrc = new MRC();
        int n = 0;
        mrc.open(name, "r");
        int mfn;
        while((mfn=mrc.next()) > 0) {
            byte[] bytes = mrc.read();
            if (charset!=null)
                rec.wrap(bytes,charset);
            else{
                charset = rec.wrap(bytes,new Melody(""));
                if(charset==null && n>10)
                    charset = rec.getCharset();
            }
            for(String s:rec.sortedTexts("\n "+(++n)+". "))
                System.out.println(s);
        }
        mrc.close();
    }

    public static void writeMRC(String name, String charsetName
            , String charsetoutName, String countString)
            throws FileNotFoundException, IOException {
        int count = countString.isEmpty() ? 0 : Integer.valueOf(countString);
        Charset charsetout = null;
        if (!charsetoutName.isEmpty())
             charsetout = Charset.forName(charsetoutName);
        MRC mrc;
        try (RandomAccessFile raf = new RandomAccessFile("write.mrc","rw")) {
            Melody melody = null;
            Charset charset = null;
            if (charsetName.isEmpty())
                melody = new Melody("");
            else charset = Charset.forName(charsetName);
            mrc = new MRC();
            MRCRec rec = new MRCRec(charset);
            int n = 0;
            mrc.open(name, "r");
            while(mrc.next() > 0) {
                if(count>0 && n>=count) break;
                ++n;
                byte[] bytes = mrc.read();
                if (charset!=null)
                    rec.wrap(bytes,charset);
                else{
                    charset = rec.wrap(bytes,melody);
                    if(charset==null && n>10+count)
                        charset = rec.getCharset();
                }
                MRCRec.write(rec.getFields(), raf
                    , charsetout==null ? rec.getCharset() : charsetout);
    //            System.in.read();
            }
            System.out.println("writeMRC()="+n);
            raf.setLength(raf.getFilePointer());
        }
        mrc.close();
    }

    public static void importMRC(String name, String charset, Map<String,String> map)
            throws FileNotFoundException, IOException {
        MRC mrc = new MRC();
        mrc.open(name, "r");
        MRCRec rec;
        rec = new MRCRec(Charset.forName(charset));
        Map<Integer,List<Field>> tags;
        Writer wr = new FileWriter(name+"-slim.log");
        wr.write(name+"\n");
        Field.setWriter(wr);
        XRF64 xrf = XRF64.create(name);
        String s;
        int n = 0;
        while(mrc.next() > 0) {
            ++n;
            rec.wrap(mrc.read());
//            System.out.print("\nМаркер=["+rec.getMarker()+"]");
            tags = rec.newMRCTags();
            tags.remove(0);
            Field.slimTags(tags);
            if (tags.containsKey(10))
                s = tags.get(10).get(0).get();
            else s = "нет поля 10";
            System.out.print("\n"+n+" "+s);
            if (name.startsWith("rdr")) {
                s = Field.ex10Tags(tags);
                if(s!=null) {
                    wr.write(s+"\n");
    //                System.out.print("\n"+s);
    //                Field.print(tags);
    //                System.in.read();
                }
            }
            xrf.write(tags);
        }
        mrc.close();
        wr.close();
        xrf.close();
    }

    public static void slimMRC(String name, String charset, Map<String,String> map)
            throws FileNotFoundException, IOException {
        MRC mrc = new MRC();
        mrc.open(name, "r");
        MRCRec rec = new MRCRec(Charset.forName(charset));
        XRF64 xrf = XRF64.create(name);
        Map<Integer,List<Field>> tags;
        Writer wr = new FileWriter(name+"slim.log");
        wr.write("slim-log.\n");
        Field.setWriter(wr);
        int mfn = 0;
        while(mrc.next() > 0) {
            rec.wrap(mrc.read());
//            System.out.print("\nМаркер=["+rec.getMarker()+"]");
            tags = rec.newMRCTags();
            Field.slimTags(tags);
            String s10 = Field.ex10Tags(tags);
            if(s10!=null)
                wr.write(s10+"\n");
//
            xrf.write(tags);
//            Field.print(tags);
//            System.in.read();
        }
        xrf.close();
        wr.close();
        mrc.close();
        System.out.println("mrc Records readed = "+mfn);
    }

    public static void mrc2xml(String name, String sno)
            throws FileNotFoundException, IOException, Exception {
        MrcInputStream mrc = MrcInputStream.build2(name);
        MrcBuff rec = new MrcBuff();
        XLines x = new XLines();
        int no = (sno==null || sno.isEmpty())?0:Integer.valueOf(sno);
        x.open(name+((no>0)?"."+no:""),null);
        x.xml1("mrc");
        rec.mrcLeader(x);
        int i=0;
        while(mrc.next() > 0) {
            ++i;
//            if(i==23630)
//                System.out.println(""+'\10'+i);
            if(no > 0) {
                if(i < no) continue;
                if(i > no) break;
            }
            rec.xml(x,rec.wrap(mrc.read()),i);
//            x.fss()
//            Field.fssOut(rec.fss());
        }
        System.out.println(""+i+" mrc records read");
        x.xml2("mrc");
        x.close();
        mrc.close();
//        System.out.println("mrc Records readed = "+i);
    }

    public static void mrc2xml_(String name, String sno)
            throws FileNotFoundException, IOException {
        MRC mrc = new MRC();
        mrc.open(name, "r");
        MRCRec rec = new MRCRec(null);
        XLines x = new XLines();
        int no = (sno==null || sno.isEmpty())?0:Integer.parseInt(sno);
        x.open(name+((no>0)?"."+no:""),null);
        x.xml1("mrc");
        rec.mrcLeader(x);
        int i=0;
        while(mrc.next() > 0) {
            ++i;
//            if(i==23630)
//                System.out.println(""+'\10'+i);
            if(no > 0) {
                if(i > no) break;
                if(i < no) {
                    mrc.skip();
                    continue;
                }
            }
            rec.xml(x,rec.wrap(mrc.read()),i);
//            Field.fssOut(rec.fss());
        }
        System.out.println(""+i+" mrc records read");
        x.xml2("mrc");
        x.close();
        mrc.close();
//        System.out.println("mrc Records readed = "+i);
    }
    public static void mrc2mrc(String name, String charsetName2)
            throws FileNotFoundException, IOException {
        MRC mrc = new MRC();
        mrc.open(name, "r");
        MRCRec rec = new MRCRec(null);
        Charset charset2 = Charset.forName(charsetName2==null || charsetName2.isEmpty() ? "utf-8" : charsetName2);
        String name2 = name+"."+charset2.name();
        RandomAccessFile raf = new RandomAccessFile(name2,"rw");
        System.out.println("File "+name2+" created.");
        while(mrc.next() > 0) {
            rec.wrap(mrc.read());
            for(byte[] bytes:MRCRec.bss(rec.getMarker(), rec.fss(), charset2))
                raf.write(bytes);
//            Field.fssOut(rec.fss());
        }
        raf.setLength(raf.getFilePointer());
        raf.close();
        mrc.close();
//        System.out.println("mrc Records readed = "+i);
    }
    public static void mrcPiece(String name, String sno)
            throws FileNotFoundException, IOException {
        MRC mrc = new MRC();
        mrc.open(name, "r");
        int no = (sno==null || sno.isEmpty())?0:Integer.parseInt(sno);
        String name2 = name+"."+no;
        RandomAccessFile raf = new RandomAccessFile(name2,"rw");
        System.out.println("File "+name2+" created.");
        int i = 0;
        while(mrc.next() > 0) {
            ++i;
            if(i < no) {
                mrc.skip();
                continue;
            }
            if(i == no) 
                raf.write(mrc.read());
            break;
//            Field.fssOut(rec.fss());
        }
        raf.setLength(raf.getFilePointer());
        raf.close();
        mrc.close();
//        System.out.println("mrc Records readed = "+i);
    }
    public static void xml2mrc(String xmlname, String sno)
            throws FileNotFoundException, IOException, XMLStreamException, Exception {
        int no = (sno==null || sno.isEmpty())?0:Integer.parseInt(sno);
        String name2 = xmlname+(no>0?sno:"")+".mrc";
        Charset charset2 = Charset.forName("utf-8");
        try (   XML64 x = XML64.open(xmlname); 
                FileOutputStream fout = new FileOutputStream(name2);
            ) {
//            System.out.println("xml element = "+x.getr().getLocalName());
            System.out.println("File "+name2+" created.");
            int i = 0;
            while(x.nextChild("r")) {
                ++i;
                if(no>0) {
                    if(sno.equals(x.getr().getAttributeValue("","r"))) {
                        continue;
                    }
                }
                String leader = x.getr().getAttributeValue("","l");
                List<List<String>> fss = x.readfss();
//                if(i==1)Field.fssOut(fss);
                for(byte[] bytes:MrcBuff.fssBytes(leader, fss, charset2)){
                    fout.write(bytes);
                }
//                Field.fssOut(rec.fss());
            }
            System.out.println("r-elements readed = "+i);
        }
    }
    public static void xml2mrc_(String xmlname, String sno)
            throws FileNotFoundException, IOException, XMLStreamException {
        int no = (sno==null || sno.isEmpty())?0:Integer.parseInt(sno);
        String name2 = xmlname+(no>0?sno:"")+".mrc";
        Charset charset2 = Charset.forName("utf-8");
        try (   XML64 x = XML64.open(xmlname); 
                RandomAccessFile raf = new RandomAccessFile(name2,"rw");
            ) {
//            System.out.println("xml element = "+x.getr().getLocalName());
            raf.setLength(0);
            System.out.println("File "+name2+" truncated.");
            int i = 0;
            while(x.nextChild("r")) {
                ++i;
                if(no>0) {
                    if(sno.equals(x.getr().getAttributeValue("","r"))) {
                        continue;
                    }
                }
                String leader = x.getr().getAttributeValue("","l");
                List<List<String>> fss = x.readfss();
                Field.fssOut(fss);
                for(byte[] bytes:MRCRec.bss(leader, fss, charset2)){
                    raf.write(bytes);
                }
//                Field.fssOut(rec.fss());
            }
            System.out.println("r-elements readed = "+i);
        }
    }

    public static void testXRF(String name,Map<String,String> map)
            throws FileNotFoundException, IOException {
        WSS wss = new WSS();
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
//        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        wss.addPath(dirname);
        Map<Integer,Set<Character>> wssmap = null;
        Map<Integer,List<Field>> tags;
        Map<Integer,List<Field>> tags1 = null;
        String regex = map.get("regex");
        char c = tagCutChar(map);
        String tag = map.get("tag");
        int countScaned = 0;
        int countReaded = 0;
        int countExecuted = 0;
        boolean mfnregex = tag.equalsIgnoreCase("mfn");
        try (XRF64 xrf = XRF64.open(dirname+name, "r")) {
            int nxtmfn = xrf.getMST().nxtmfn();
            int mfn=0;
            while(++mfn < nxtmfn) {
                ++countScaned;
                if (mfnregex && mfn<Integer.valueOf(regex))
                    continue;
                ++countReaded;
                if(xrf.next() > 0) continue;
                tags = xrf.read(mfn);
                if (!mfnregex)
                    if(!tag.isEmpty())
                        if (!matches(tags.get(Integer.valueOf(tag)), c, regex))
                            continue;
                ++countExecuted;
                if (wssmap==null) {
                    wssmap = wss.wssMap(tags);
                    tags1 = tags;
                }
                else {
                    WSS.wssMapAddAll(wssmap, wss.wssMap(tags));
                    if(Field.tagsFieldCount(tags) > Field.tagsFieldCount(tags1))
                        tags1 = tags;
                }
            }
            System.out.print("\n countScaned=" + countScaned);
            System.out.print("\n countReaded=" + countReaded);
            System.out.print("\n countExecuted=" + countExecuted);
            if (wssmap!=null) {
                Field.print(tags1);
                wss.print(wssmap);
            }
        }
    }

    public static String tagsFldSub(Map<Integer,List<Field>> tags, int fld, char sub) {
        List<Field> fields = tags.get(fld);
        if (fields == null) return null;
        if (fields.isEmpty()) return null;
        String s = fields.get(0).getSubs().get(sub);
//        if(s.equals("УМО")) s = "РК";
        return s;
    }
    public static void addMapOut(Map<String,List<String>> mapOut, String s1, String s2) {
        List<String> lines = mapOut.get(s1);
        if (lines==null) mapOut.put(s1, (lines = new ArrayList<>()));
        lines.add(s2);
    }
    public static void xrf2(String name)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        Map<String,List<String>> mapOut = new TreeMap<>();
        int countReaded = 0;
        int i83 = 0;
        int n;
        int n7;
        try (XRF64 xrf = XRF64.open(dirname+name, "rw")) {
            int nxtmfn = xrf.getMST().nxtmfn();
            int mfn = 0;
            while(++mfn < nxtmfn) {
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                String s2 = tagsFldSub(tags,920,'\0');
                if (!s2.equals("DISC")) continue;
                if(!tags.containsKey(83)) continue;
                ++countReaded;
                s2 = tagsFldSub(tags,4,'\0') + "\t";
                s2 = s2 + tagsFldSub(tags,6,'\0') + "\t";
                s2 = s2 + tagsFldSub(tags,3,'a') + "\t";
                s2 = s2 + tagsFldSub(tags,5,'\0') + "\t";
                for(Field field:tags.get(83)) {
                    Map<Character,String> subs = field.getSubs();
                    String s1 = subs.get('o')+"\t";
                    s1 = s1 + subs.get('a')+"\t";
                    s1 = s1 + subs.get('h')+"\t";
                    s1 = s1 + subs.get('c')+"\t";
                    s1 = s1 + subs.get('n')+"\t";
                    s1 = s1 + subs.get('v');
                    addMapOut(mapOut,s1,s2+subs.get('f'));
                    ++i83;
                }
            }
            System.out.print("\n countReaded=" + countReaded);
            System.out.print("\n i83=" + i83);
            Field f;
            n = 0;
            n7 = 0;
            for(Map.Entry<String,List<String>> e:mapOut.entrySet()) {
                int i = 0;
                String[] ss = e.getKey().split("\t");
                Map<Integer,List<Field>> tags2 = new TreeMap<>();
                f = new Field();
                f.setSub('a', ss[i++]);
                f.setSub('b', ss[i++]);
                f.setSub('c', ss[i++]);
                tags2.put(6, new ArrayList<>(Collections.singletonList(f)));
                f = new Field();
                f.setSub('a', ss[i++]);
                tags2.put(3, new ArrayList<>(Collections.singletonList(f)));
                f = new Field();
                f.setSub('a', ss[i++]);
                tags2.put(2, new ArrayList<>(Collections.singletonList(f)));
                f = new Field();
                f.setSub('a', ss[i++]);
                tags2.put(5, new ArrayList<>(Collections.singletonList(f)));
                f = new Field();
                f.setSub('\00', "CURR");
                tags2.put(920, new ArrayList<>(Collections.singletonList(f)));
                f = new Field();
                f.setSub('\00', "" + ++n);
                tags2.put(903, new ArrayList<>(Collections.singletonList(f)));
    //            tags2.put(7, new ArrayList<Field>());
                List<String> list =  e.getValue();
    //            Collections.sort(list);
                for(String s3:list){
                    String[] ss2 = s3.split("\t");
                    i = 0;
                    f = new Field();
                    String s7 = ss2[i++];
                    int f7 = 12;
                    switch (s7) {
                        case "ГСЭ":
                            f7 = 7;
                            break;
                        case "ЕН":
                            f7 = 8;
                            break;
                        case "ОПД":
                            f7 = 9;
                            break;
                        case "СД":
                            f7 = 10;
                            break;
                        case "ФТД":
                            f7 = 11;
                            break;
                        default:
                            System.out.print("\n**** s7="+s7+" CURR="+n);
                            break;
                    }
    //                f.setSub('a', s7);
                    if(ss2[i++].startsWith("Ф")) f.setSub('a', "Ф");
                    f.setSub('c', ss2[i++]);
                    f.setSub('d', ss2[i++]);
                    f.setSub('b', ss2[i++]);
                    if(!tags2.containsKey(f7))tags2.put(f7, new ArrayList<Field>());
                    tags2.get(f7).add(f);
                    ++n7;
                }
    //            Field.print(tags2);
                xrf.write(tags2);
            }
        }
        System.out.print("\n CURR added=" + n);
        System.out.print("\n n7=" + n7);
    }
    public static void xrf2curs(String name)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        Map<String,List<String>> mapOut = new TreeMap<>();
        int countReaded = 0;
        int i83 = 0;
        int n;
        int n7;
        try (XRF64 xrf = XRF64.open(dirname+name, "rw")) {
            int nxtmfn = xrf.getMST().nxtmfn();
            int mfn = 0;
            while(++mfn < nxtmfn) {
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                String s2 = tagsFldSub(tags,920,'\0');
                if (!s2.equals("DISC")) continue;
                if(!tags.containsKey(83)) continue;
                ++countReaded;
                s2 = tagsFldSub(tags,4,'\0') + "\t";
                s2 = s2 + tagsFldSub(tags,6,'\0') + "\t";
                s2 = s2 + tagsFldSub(tags,3,'a') + "\t";
                s2 = s2 + tagsFldSub(tags,5,'\0') + "\t";
                for(Field field:tags.get(83)) {
                    Map<Character,String> subs = field.getSubs();
                    String s1 = subs.get('o')+"\t";
                    s1 = s1 + subs.get('a')+"\t";
                    s1 = s1 + subs.get('h')+"\t";
                    s1 = s1 + subs.get('c')+"\t";
                    s1 = s1 + subs.get('n')+"\t";
                    s1 = s1 + subs.get('v');
                    addMapOut(mapOut,s1,s2+subs.get('f'));
                    ++i83;
                }
            }
            System.out.print("\n countReaded=" + countReaded);
            System.out.print("\n i83=" + i83);
            Field f;
            n = 0;
            n7 = 0;
            for(Map.Entry<String,List<String>> e:mapOut.entrySet()) {
                int i = 0;
                String[] ss = e.getKey().split("\t");
                Map<Integer,List<Field>> tags2 = new TreeMap<>();
                f = new Field();
                f.setSub('a', ss[i++]);
                f.setSub('b', ss[i++]);
                f.setSub('c', ss[i++]);
                tags2.put(6, new ArrayList<>(Collections.singletonList(f)));
                f = new Field();
                f.setSub('a', ss[i++]);
                tags2.put(3, new ArrayList<>(Collections.singletonList(f)));
                f = new Field();
                f.setSub('c', ss[i++]);
                tags2.put(2, new ArrayList<>(Collections.singletonList(f)));
                f = new Field();
                f.setSub('a', ss[i++]);
                tags2.put(5, new ArrayList<>(Collections.singletonList(f)));
                f = new Field();
                f.setSub('\00', "CURR");
                tags2.put(920, new ArrayList<>(Collections.singletonList(f)));
                f = new Field();
                f.setSub('\00', "" + ++n);
                tags2.put(903, new ArrayList<>(Collections.singletonList(f)));
    //            tags2.put(7, new ArrayList<Field>());
                List<String> list =  e.getValue();
    //            Collections.sort(list);
                for(String s3:list){
                    String[] ss2 = s3.split("\t");
                    i = 0;
                    f = new Field();
                    String s7 = ss2[i++];
                    int f7 = 12;
                    switch (s7) {
                        case "ГСЭ":
                            f7 = 7;
                            break;
                        case "ЕН":
                            f7 = 8;
                            break;
                        case "ОПД":
                            f7 = 9;
                            break;
                        case "СД":
                            f7 = 10;
                            break;
                        case "ФТД":
                            f7 = 11;
                            break;
                        default:
                            System.out.print("\n**** s7="+s7+" CURR="+n);
                            break;
                    }
    //                f.setSub('a', s7);
                    if(ss2[i++].startsWith("Ф")) f.setSub('a', "Ф");
                    f.setSub('c', ss2[i++]);
                    f.setSub('d', ss2[i++]);
                    f.setSub('b', ss2[i++]);
                    if(!tags2.containsKey(f7))tags2.put(f7, new ArrayList<Field>());
                    tags2.get(f7).add(f);
                    ++n7;
                }
    //            Field.print(tags2);
                xrf.write(tags2);
            }
        }
        System.out.print("\n CURR added=" + n);
        System.out.print("\n n7=" + n7);
    }
    public static void xrf2txt(String name,String start)
            throws FileNotFoundException, IOException {
        start = start.toUpperCase();
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        int countReaded = 0;
        int countWrited = 0;
        XRF64 xrf;
        try (FileOutputStream fos = new FileOutputStream(name+(start.isEmpty() ? "" : "."+start)+".txt")) {
            xrf = XRF64.open(dirname+name, "r");
            int nxtmfn = xrf.getMST().nxtmfn();
            Progress2 p2 = new Progress2(nxtmfn-1,name);
            int mfn = 0;
            while(++mfn < nxtmfn) {
                p2.inc();
                ++countReaded;
                //String smfn = "\n" + mfn + "\t";
                //for(String s:Field.impTags(xrf.readUp().newTags()))
                  //  wr.write(smfn + s);
                if(xrf.next() > 0) continue;
                countWrited++;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                if(!start.isEmpty())
                    if(!start.equals(Field.getTagsSubUpper(tags,920,'\u0000')))
                            continue;
                for(String line:Field.txt2(tags)) {
                    fos.write(line.getBytes());
                    fos.write("\r\n".getBytes());
                }
            }
        }
        xrf.close();
        System.out.println();
        System.out.println("" + countWrited + " writes to " + name + ".txt");
    }
    public static void xrf2print(String name)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        int countReaded = 0;
        try (XRF64 xrf = XRF64.open(dirname+name, "r")) {
            int nxtmfn = xrf.getMST().nxtmfn();
            int mfn = 0;
            while(++mfn < nxtmfn) {
                ++countReaded;
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                System.out.println();
                System.out.println("mfn="+mfn);
                Field.print(tags);
            }
        }
        System.out.println();
        System.out.println("" + countReaded + " printed.");
    }
    public static void xrf2rdr(String s1)
            throws FileNotFoundException, IOException {
        String name = "rdrab";
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        int countReaded = 0;
        Map<String,Map<Integer,List<Field>>> xn = new TreeMap<>();
        Map<String,Map<Integer,List<Field>>> xf = new TreeMap<>();
        try (XRF64 xrf = XRF64.open(dirname+name, "r")) {
            int nxtmfn = xrf.getMST().nxtmfn();
            Progress2 p2 = new Progress2(nxtmfn-1,name);
            int mfn = 0;
            while(++mfn < nxtmfn) {
                p2.inc();
                ++countReaded;
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                String sn = Field.getTagsSub(tags,90,'j');
                if(sn==null) {
                    String sf = Field.getTagsSub(tags,10,'\u0000');
                    sf += " " + Field.getTagsSub(tags,11,'\u0000');
                    sf += " " + Field.getTagsSub(tags,12,'\u0000');
                    xf.put(sf,tags);
                }
                else {
                    xn.put(sn, tags);
                }
            }
        }
        System.out.println();
        System.out.println("" + countReaded + " rdr-ed.");
        System.out.println("xn.size()=" + xn.size());
        System.out.println("xf.size()=" + xf.size());
        RDR.setxn(xn);
        RDR.setxf(xf);
        RDR.txt2irbis(s1, "c:\\Projects\\rdrab\\");
    }
    public static void xrf2history(String name, String smfn)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        int countReaded = 0;
        try (XRF64 xrf = XRF64.open(dirname+name, "r")) {
            if(smfn.isEmpty()) {
                int nxtmfn = xrf.getMST().nxtmfn();
                int mfn=0;
                while(++mfn < nxtmfn) {
                    ++countReaded;
                    xrf.next();
                    xrf.printHistory(mfn);
                    //xrf.printHistory2(mfn);
                }
            } else {
                ++countReaded;
                int mfn = Integer.parseInt(smfn) ;
                xrf.next(mfn);
                xrf.printHistory(mfn);
                xrf.printHistory2(mfn);
            }
        }
        System.out.println();
        System.out.println("" + countReaded + " histored.");
    }
    public static void mst2xml(String name)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        int countReaded = 0;
        XLines xml = new XLines();
        xml.open(name, null);
        try (XRF64 xrf = XRF64.open(dirname+name, "r")) {
            MST64 mst = xrf.getMST();
            int nxtmfn = mst.nxtmfn();
            xml.newline().xml1("mst", mst.xmlAttr());
            Progress2 p2 = new Progress2(nxtmfn-1,name);
            int mfn = 0;
            while(++mfn < nxtmfn) {
                p2.inc();
                ++countReaded;
                xrf.next();
                for(Rec64 rec64:mst.readHistory(xrf.getPos(), mfn)){
                    xml.newline().xml1("rec", rec64.xmlAttr());
                    Field.xmlTags(xml, rec64.newTags());
                    xml.xml2("rec");
                    xml.write();
                }
            }
            xml.close("mst");
        }
        System.out.println();
        System.out.println("" + countReaded + " xmled.");
    }
    public static void xrf2xml(String name, String splots, String snoplots)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        int countReaded = 0;
        try (XRF64 xrf = XRF64.open(dirname+name, "r")) {
            XLines xml = new XLines();
            xml.open(name, null);
            xml.xml1("xrf");
            Plots plots=null,noplots=null;
            if(!splots.isEmpty()) {
                splots = splots.replace("+", ",");
                plots = new Plots(splots);
                xml.xml("plots", splots);
                if(!snoplots.isEmpty())
                    snoplots = snoplots.replace("+", ",");
                noplots = new Plots(snoplots);
                xml.xml("noplots", snoplots);
            }
            XLines xlines = new XLines();
            int mfn=0;
            int nxtmfn = xrf.getMST().nxtmfn();
            Progress2 p2 = new Progress2(nxtmfn-1,name);
            while(++mfn < nxtmfn) {
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                p2.inc();
                ++countReaded;

                xlines.xml1("m", "m", ""+mfn);
                if(plots==null)
                    Field.xmlTags(xlines, tags);
                else
                    Field.xmlTags(xlines, tags, plots, noplots);
                xlines.xml2("m");

                //bytesReaded += xrf.mfn2xml(mfn,xlines);
                xml.add(xlines);
                xlines.reset();
                xml.write();
            }
            xml.close("xrf");
            System.out.println();
            System.out.println("" + countReaded + " records.");
            System.out.println("" + xrf.getMST().getReaded() + " bytes.");
            System.out.println("That's all.");
        }
//        System.out.println("" + bytesReaded + " bytes digged.");
    }
    public static void xrf2t(String name)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        String id = name.trim().toUpperCase();
        XLines xml = new XLines();
        xml.open(id+"-T", null);
        xml.xml1("xrf2t");
        XLines xlines = new XLines();
        int countReaded = 0;
        int countWrited = 0;
        try (XRF64 xrf = XRF64.open(dirname+name, "r")) {
            int nxtmfn = xrf.getMST().nxtmfn();
            Progress2 p2 = new Progress2(nxtmfn-1,name);
            int mfn = 0;
            while(++mfn < nxtmfn) {
                p2.inc();
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                ++countReaded;
                TextBook t= new TextBook(id,mfn);
                t.read910(tags.get(910));
                if(!t.read(tags)) continue;
                t.koxml(xlines, null);
                xml.add(xlines);
                xlines.reset();
                xml.write();
                ++countWrited;
            }
            xml.close("xrf2t");
        }
        System.out.println();
        System.out.println("" + countReaded + " readed from "+dirname);
        System.out.println("" + countWrited + " writed to "+id+"-T.xml");
    }
    public static void farcheck(String name)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        String id = name.trim().toUpperCase();
        int countReaded = 0;
        int countWrited = 0;
        try (XRF64 xrf = XRF64.open(dirname+name, "r")) {
            int nxtmfn = xrf.getMST().nxtmfn();
            Progress2 p2 = new Progress2(nxtmfn-1,name);
            int mfn = 0;
            while(++mfn < nxtmfn) {
                p2.inc();
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                ++countReaded;
                for(Map.Entry<Integer,List<Field>> e:tags.entrySet())
                    for(Field f:e.getValue())
                        for(Map.Entry<Character,String> es:f.getSubs().entrySet()) {
                            String v = es.getValue();
                            int far = Words.farChar(v);
                            if(far<=0) continue;
                            ++countWrited;
                            int i = Math.abs(far)-1;
                            System.out.println(""+far+" "+v.substring(0,i)+"^"+v.substring(i));
                        }
            }
        }
        System.out.println();
        System.out.println("" + countReaded + " readed from "+dirname);
        System.out.println("" + countWrited + " writed");
    }
    public static void xrf2norma(String name, boolean readOnly)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
//        int mfn;
        int readed = 0;
        int normalized = 0;
        int updated = 0;
        try (XRF64 xrf = XRF64.open(dirname+name, readOnly?"r":"rw")) {
            int mfn=0;
            int nxtmfn = xrf.getMST().nxtmfn();
            while(++mfn < nxtmfn) {
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                ++readed;
                if(Field.normalizeTags(tags)) {
                    System.out.println("\t"+mfn);
                    ++normalized;
                    if(readOnly) continue;
                    xrf.write(tags, mfn);
                    ++updated;
                }
            }
        }
        System.out.println("readed=" + readed);
        System.out.println("normalized=" + normalized);
        System.out.println("updated=" + updated);
    }
    public static void xrf2undelete(String name, boolean readOnly)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\"+name+"\\";
        int readed = 0;
        try (XRF64 xrf = XRF64.open(dirname+name, readOnly?"r":"rw")) {
            int nxtmfn = xrf.getMST().nxtmfn();
            int mfn = 0;
            while(++mfn < nxtmfn) {
                ++readed;
                if(!readOnly){
                    if(xrf.next() == 0) continue;
                    System.out.println("undeleted mfn="+mfn);
                    xrf.write(0, mfn);
                }
            }
        }
        System.out.println("undeleted=" + readed);
    }
    public static void xrf2imp(String[] names)
            throws FileNotFoundException, IOException {
        try (Writer wr = new FileWriter("imp.txt")) {
            wr.write(Field.imp2Heads());
            for(String name:names) {
                String dirname = "c:\\irbis64\\datai\\"+name+"\\";
                int countReaded = 0;
                try (XRF64 xrf = XRF64.open(dirname+name, "r")) {
                    int nxtmfn = xrf.getMST().nxtmfn();
                    Progress2 p2 = new Progress2(nxtmfn-1,name);
                    int mfn = 0;
                    while(++mfn < nxtmfn) {
                        p2.inc();
                        if(xrf.next() > 0) continue;
                        Map<Integer,List<Field>> tags = xrf.read(mfn);
                        ++countReaded;
                        for(String s:Field.imp2Tags(name, mfn, tags))
                            wr.write(s);
                    }
                }
                System.out.println(countReaded + "записей импортировано");
            }
        }
    }
    public static void mrc2txt(String name, String charsetName)
            throws FileNotFoundException, IOException {
        int n;
        try (Writer wr = new FileWriter(name+".txt")) {
//            wr.write(Field.impHeads());
            Melody melody = null;
            Charset charset = null;
            if (charsetName.isEmpty())
                melody = new Melody("");
            else charset = Charset.forName(charsetName);
            MRC mrc = new MRC();
            MRCRec rec = new MRCRec(charset);
            n = 0;
            mrc.open(name, "r");
//            Progress2 p2 = new Progress2((int)mrc.getRAF().length(), "mrc="+name);
            while(mrc.next() > 0) {
                String smfn = "\n" + ++n + "\t";
                byte[] bytes = mrc.read();
                if (charset!=null)
                    rec.wrap(bytes,charset);
                else{
                    charset = rec.wrap(bytes,melody);
                    if(charset==null && n>10)
                        charset = rec.getCharset();
                }
//                p2.inc();
//rec.fss()
                for(String s:Field.impFields(MRCRec.sortedFields(rec.getFields()),rec.getMarker()))
                    wr.write(smfn + s);
    //            System.in.read();
            }
            mrc.close();
        }
        System.out.print("\n countReaded=" + n);
    }

    private static String readLine(InputStream in) throws IOException {
        final int nl = 13;
        final Charset charset = Charset.forName("windows-1251");
        int ch = in.read();
        if (ch<0) return null;
        int i = 0;
        byte[] bytes = new byte[1024];
        do {
            if (ch == nl) {
                in.read();
                break;
            }
            bytes[i++] = (byte)ch;
        } while((ch = in.read())>=0);
        return new String(bytes,0,i,charset);
    }

    public static void ini2txt(String name, String charsetName)
            throws FileNotFoundException, IOException {
        final byte[] nlcr = {13,10};
        final Charset charset = Charset.forName("windows-1251");
        String sh = "Pref\tName\tDictionType\tMenu\tHint";
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(sh.split("\t")));
        String line = "C:\\IRBIS64\\Datai\\"+name+"\\" + name +".ini";
        Map<String,Map<String,String>> map;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(line))) {
            map = new TreeMap<>();
            while ((line = readLine(in)) != null)
                if (line.startsWith("Item")){
                    int pos = line.indexOf('=');
                    String iname = line.substring(4, pos).trim();
                    for(String s2:list)
                        if (iname.startsWith(s2)) {
                            String inum = iname.substring(s2.length());
                            String ival = line.substring(pos+1);
                            if (!map.containsKey(inum))
                                map.put(inum, new TreeMap<String,String>());
                            map.get(inum).put(s2, ival);
                            break;
                        }

                }
        }
        System.out.print("\n countReaded=" + map.size());
        line = name+"_ini.txt";
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(line))) {
            out.write(sh.getBytes(charset));
            out.write(nlcr);
            for(Map.Entry<String,Map<String,String>> e:map.entrySet()){
                line = "";
                for(String s3:list){
                    if (!line.isEmpty())
                        line += "\t";
                    if (e.getValue().containsKey(s3))
                        line += e.getValue().get(s3);
                }
                out.write(line.getBytes(charset));
                out.write(nlcr);
            }
        }
    }

    public static void txt2ini(String name, String charsetName)
            throws FileNotFoundException, IOException {
        final byte[] nlcr = {13,10};
        final Charset charset = Charset.forName("windows-1251");
        String line = name +".txt";
        List<String> lines = new ArrayList<>();
        int n;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(line))) {
            line = readLine(in);
            String[] names = line.split("\t");
            n = 0;
            while ((line = readLine(in)) != null)
                if(!line.trim().isEmpty()){
                    String[] values = line.split("\t");
                    for(int i=0; i<values.length; i++)
                        if (i<names.length)
                            if(!values[i].isEmpty())
                                lines.add("Item"+names[i]+n+"="+values[i]);
                    n++;
                }
        }
        System.out.print("\n countReaded=" + n);
        line = name+".ini";
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(line))) {
            line = "ItemNumb="+n;
            out.write(line.getBytes(charset));
            out.write(nlcr);
            for(String outline:lines){
                out.write(outline.getBytes(charset));
                out.write(nlcr);
            }
        }
    }

    public static String ym2sem(int year, int month) {
        int y2 = (month>6) ? (year+1)%100 : (year--)%100;
        String s=""+year+"-"+y2;
        if(month>=0) s += ((month>6 || month<2) ? "/1" : "/2");
        return s;
    }
/**
 * создает индексы по рабочему листу GROUP
 * @param xrf
 * @return map
 * @throws IOException
 */
    public static Map<String,Integer> getGroupIndex(XRF64 xrf)
            throws IOException {
        Map<String,Integer> map = new TreeMap<>();
        int mfn = 0;
        int nxtmfn = xrf.getMST().nxtmfn();
        while(++mfn < nxtmfn) {
            if(xrf.next() > 0) continue;
            Map<Integer,List<Field>> tags = xrf.read(mfn);
            String s = Field.getTagsSub(tags, 920, '\u0000');
            if("GROUP".equals(s)) {
                map.put(Field.getTagsSubUpper(tags, 21, 'b'),mfn);
            }
        }
        System.out.println(""+map.size()+" групп проиндексировано");
        return map;
    }
    public static Map<String,Character> getSubMap() {
        Map<String,Character> m = new HashMap<>();
        m.put("Название",'b');
        m.put("Активных студентов",'c');
        m.put("Контракт",'e');
        m.put("Формирующее подр.",'f');
        m.put("Территориальное подр.",'g');
        m.put("Выпускающее подр.",'h');
        m.put("Направление подготовки (специальность)",'i');
        m.put("Квалификация",'j');
        m.put("Форма освоения",'k');
        m.put("Условие освоения",'l');
        m.put("Срок освоения",'m');
        return m;
    }
    public static Map<Character, String> groupSubs(String[] names
            , String[] values, Map<String, Character> subMap){
        Map<Character,String> subs = new TreeMap<>();
        for(int i=0; i<values.length; i++) {
            String s = values[i].trim();
            if(s.isEmpty()) continue;
            if(i>=names.length)
                System.out.println("*** безымянный["+i+"]="+s+";");
            Character c = subMap.get(names[i]);
            if(c==null) continue;
            subs.put(c, s);
            }
        String gname = subs.get('b');
        System.out.println("прочитана группа "+gname+".");
        String[] gnames = gname.split("-");
        int index = Character.isDigit(gnames[1].charAt(0)) ? 1 : 2;
        int y = 2000 + Integer.parseInt(gnames[index]);
        subs.put('a', ""+y);
        return subs;
    }
    public static boolean checkGroupTags21(Map<Integer,List<Field>> tags,
            String sem, Map<Character,String> subs) throws Exception {
        boolean b21 = true;
        boolean b22 = checkGroupTags22(tags,sem,subs.remove('c'));
        Field f = tags.get(21).get(0);
        Map<Character,String> subs21 = f.getSubs();
        for(Map.Entry<Character,String> e:subs.entrySet()) {
            char ch = e.getKey();
            String s = e.getValue();
            if(s.equals(subs21.get(ch))) continue;
            if(b21) {
                b21 = false;
                System.out.println("v22.old="+f.get());
            }
            subs21.put(ch,s);
        }
        if(b21) return b22;
        System.out.println("v22.new="+f.get());
        return false;
    }
    public static boolean checkGroupTags22(Map<Integer,List<Field>> tags,
            String sem, String cnt) throws Exception {
        if(cnt==null || cnt.isEmpty()) cnt = "0";
        List<Field> fs = tags.get(22);
        if(fs!=null) {
            Field f = fs.get(fs.size()-1);
            if(cnt.equals(f.getSubs().get('b')))
                return true;
            int comp = sem.compareTo(f.getSubs().get('a'));
            if(comp<0) throw new Exception("v22^a="+f.getSubs().get('a')+" is too large then sem="+sem);
            if(comp==0) {
                f.put('b', cnt);
                System.out.println("v22^b modified. "+cnt);
                return false;
            }
        }
        Field.tagsAdd(tags, 22, Field.create('a', sem).put('b', cnt));
        System.out.println("v22 added. "+sem+"="+cnt);
        return false;
    }
    public static Map<Integer,List<Field>> newGroupTags(String sem, Map<Character,String> subs) {
        Map<Integer,List<Field>> tags = new TreeMap<>();
        Field.tagsAdd(tags, 21, Field.create(subs));
        Field.tagsAdd(tags, 22, Field.create('a',sem).put('b', subs.get('c')));
        Field.tagsAdd(tags, 920, Field.create("GROUP"));
        return tags;
    }
    public static void group2vuz(boolean readOnly)
            throws FileNotFoundException, IOException, Exception {
        final String filename = "group2vuz.csv";
        final String name = "VUZ";
        final int year = Calendar.getInstance().get(Calendar.YEAR);
        final int month = Calendar.getInstance().get(Calendar.MONTH);
        final String sem = ym2sem(year,month);
        final String sdate = Field.ymd8(0, 0, 0);
        int[] n;
        try (XRF64 xrf = XRF64.open("C:\\IRBIS64\\DATAI\\"+name+"\\"+name, readOnly ? "r" : "rw")) {
            System.out.println("");
            Map<String,Integer> groupIndex = getGroupIndex(xrf);
            Map<String,Character> subMap = getSubMap();
            String line;
            String[] names = null;
            n = new int[] {groupIndex.size(),0,0,0,0,0};
            List<Map<Character,String>> addSubs;
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename))) {
                addSubs = new ArrayList<>();
                while ((line = readLine(in)) != null)
                    if(!line.trim().isEmpty()){
                        String[] values = line.split(";");
                        if(values.length==0) continue;
                        if("№".equals(values[0])) {
                            names = new String[values.length];
                            for(int i=0; i<values.length; ++i) {
                                names[i]=values[i].trim();
                                System.out.println("names["+i+"]="+names[i]+";");
                                }
                            continue;
                        }
                        n[1]++;
                        Map<Character,String> subs = groupSubs(names,values,subMap);
                        String group = subs.get('b');
                        Integer mfn = groupIndex.remove(group.toUpperCase());
                        if(mfn==null) {
                            if(Integer.parseInt(subs.get('c'))>0)
                                addSubs.add(subs);
                            continue;
                        }
                        if(xrf.next(mfn) > 0) continue;
                        Map<Integer,List<Field>> tags = xrf.read(mfn);
                        //boolean b = subs.equals(tags.get(21).get(0).getSubs());
                        if(!checkGroupTags21(tags,sem,subs)) {
                            n[2]++;
                            //System.out.println("group="+group+" updated.");
                            if(!readOnly) {
                                Field.tagsAdd907(tags, "импорт-исправлено", sdate);
                                //Field.print(tags); System.in.read();
                                xrf.write(tags, mfn);
                            }
                        }
                    }
            }
            for(int mfn:groupIndex.values()) {
                xrf.next(mfn);
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                if(!checkGroupTags22(tags,sem,"0")) {
                    n[4]++;
                    System.out.println("\tfor "+Field.getTagsSub(tags, 21, 'b'));
                    if(!readOnly) {
                        Field.tagsAdd907(tags, "импорт-занулено", sdate);
                        //Field.print(tags); System.in.read();
                        xrf.write(tags, mfn);
                    }
                }
            }
            for(Map<Character,String> subs:addSubs) {
                n[5]++;
                System.out.println(""+subs.get('b')+" added.");
                if(!readOnly) {
                    Map<Integer,List<Field>> tags = newGroupTags(sem,subs);
                    Field.tagsAdd907(tags, "импорт-создано", sdate);
                    xrf.write(tags);
                    }
                }
        }
        String ro = readOnly ? "-бы" : "";
        System.out.println("");
        System.out.println("семестр="+sem);
        System.out.println("импорт="+sdate);
        System.out.println("найдено="+n[0]);
        System.out.println("прочитано="+n[1]);
        System.out.println("исправлено"+ro+"="+n[2]);
        System.out.println("занулено"+ro+"="+n[4]);
        System.out.println("создано"+ro+"="+n[5]);
    }

    public static void testDisc(String name,Map<String,String> map)
            throws FileNotFoundException, IOException {
        String dirname = "c:\\irbis64\\datai\\";
        int inone = 0;
        int countScaned = 0;
        int countReaded = 0;
        int countExecuted = 0;
        try (XRF64 xrf = XRF64.open(dirname+name+"\\"+name, "rw")) {
            Disc disc = new Disc();
            disc.open(dirname);
            disc.print();
            int mfn=0;
            int nxtmfn = xrf.getMST().nxtmfn();
            Set<String> set691 = new TreeSet<>();
            Set<String> set691none = new TreeSet<>();
            List<Integer> discList = new ArrayList<>();
            
            mfn:
            while(++mfn < nxtmfn) {
                ++countScaned; // всего просканировано
                if(xrf.next() > 0) continue;
                Map<Integer,List<Field>> tags = xrf.read(mfn);
                List<Field> fields = tags.get(691);
                if (fields == null) continue; // нет 691 поля
                ++countReaded; // всего с 691-м полем
                set691.clear();
                for(Field field:fields) {
                    String s = field.getSubs().get('D');
                    if (s == null || s.trim().isEmpty()) {
                        ++inone;
                        System.out.print("\n mfn="+mfn+". 691="+field.get());
                        continue mfn; // попался пустой 691^D
                    }
                    set691.add(s.trim().toUpperCase());
                }
    //            if (set691.isEmpty()) continue; // нет подполей D
                discList.clear();
                for(String s:set691) {
                    List<Integer> mfnList = disc.getMfnList(s);
                    if (mfnList == null) {
                        set691none.add(s);
                        ++inone;
                        System.out.print("\n mfn="+mfn+". 691^D="+s);
                        continue mfn; // попался неверный 691^D
                    }
                    discList.addAll(mfnList);
                }
                List<Field> new691 = new ArrayList<>();
                for(Integer disc_mfn:discList)
                    disc.add691mfn(new691,disc_mfn);
                if (new691.isEmpty()) continue;
                if (listFieldEquals(new691,fields)) continue;
                ++countExecuted;
                //Field.print(tags);
                tags.put(691, new691);
                System.out.print("\nперезаписываем mfn="+mfn);
                Field.packTags(tags);
                //Field.print(tags);
                xrf.write(tags, mfn);
    //            break;
    //            System.in.read();
            }
    //        if (tags!=null) {
    //          List<Field> fields = tags.get(903);
    //          String s = fields.get(0).getSubs().get('\0');
    //          s = String.valueOf(Integer.valueOf(s)+1);
    //          fields.get(0).getSubs().put('\0',s);
    //          rec64 = xrf.add(tags);
    //          System.out.printf("mfn=%d added.", rec64.getInt(REC64_MFN));
    //        }
            System.out.print("\n countScaned=" + countScaned);
            System.out.print("\n countReaded=" + countReaded);
            System.out.print("\n inone=" + inone);
            System.out.print("\n countExecuted=" + countExecuted);
            for(String snone: set691none) {
                System.out.print("\n 691none=" + snone);
                /* String sSimilar = disc.getSimilar(snone);
                if (sSimilar != null)
                    System.out.print("\n       похоже на " + sSimilar);*/
            }
        }
    }

    private static boolean listFieldEquals(List<Field> f1, List<Field> f2) {
        if (f1.size()!= f2.size()) return false;
        for(int i=0; i<f1.size(); ++i)
            if (!f1.get(i).equals(f2.get(i)))
                return false;
        return true;
    }

    private static boolean matches(List<Field> fields, char c, String regex) {
        if (fields == null) return false;
        if (regex.isEmpty()) return true;
        String s;
        for(Field field:fields) {
            if(c==' ') s = field.get();
            else s = field.getSubs().get(c);
            if(s.matches(regex)) return true;
        }
        return false;
    }

    private static char tagCutChar(Map<String,String> map) {
        String tag = map.get("tag");
        char c = ' ';
        int pos;
        if ((pos = tag.indexOf('^')) >= 0) {
            if (pos == tag.length()-1) c = '\u0000';
            else if (pos == tag.length()-2) c = tag.charAt(tag.length()-1);
            map.put("tag",tag.substring(0, pos));
        }
        return c;
    }

    private static void rdr40add(
            Map<String, Map<String, Map<String, List<String>>>> map
            , String db
            , String v903
            , String v910
            , String book) {
        Map<String, Map<String, List<String>>> db_item = map.get(db);
        if (db_item==null)
            map.put(db, db_item =new TreeMap<>());
        Map<String, List<String>> v903_item = db_item.get(v903);
        if (v903_item==null)
            db_item.put(v903, v903_item =new TreeMap<>());
        List<String> v910_item = v903_item.get(v910);
        if (v910_item==null)
            v903_item.put(v910, v910_item =new ArrayList<>());
        v910_item.add(book);
    }

    private static void map920(String name) throws FileNotFoundException, IOException {
        Trash64 trash = new Trash64();
        trash.open("c:\\irbis64\\datai\\"+name+"\\"+name,"r");
    }

    private static void txt2xrf(String name) throws FileNotFoundException, IOException {
        try (TXT64 txt = TXT64.open(name); 
                XRF64 xrf = XRF64.create(name)) {
            Map<Integer,List<Field>> tags;
            while((tags = txt.read()) != null)
                xrf.write(tags);
        }
    }

    private static void xml2xrf(String xmlname, String xrfname) throws FileNotFoundException, IOException, XMLStreamException {
        try (XML64 xml = XML64.open(xmlname); 
                XRF64 xrf = XRF64.open(xrfname,"rw")) {
            Map<Integer,List<Field>> tags;
            while((tags = xml.read()) != null)
                xrf.write(tags);
        }
    }
 
    private static void xml2txt(String name) throws FileNotFoundException, IOException, XMLStreamException {
        try (XML64 xml = XML64.open(name); 
                TXT64 txt = TXT64.rewrite(name+".txt")) {
            Map<Integer,List<Field>> tags;
            while((tags = xml.read()) != null)
                txt.write(tags);
        }
    }
 
    private static int euler(int ibelow) {
        int n=0;
        for(int i=1; i<ibelow; i++) {
            n += euler35(i);
        }
        return n;
    }
    private static int euler35(int n) {
        if(n%3 == 0 || n%5 == 0) return n;
        return 0;
    }

    private static void plus(String[] args) throws IOException {
        String name = args[0];
        Map<Integer,List<Field>> tags;
        try (XRF64 plus = XRF64.create(name)) {
            name = args[1];
            name = "c:\\irbis64\\datai\\"+name+"\\"+name;
            int countReaded = 0;
            try (XRF64 xrf = XRF64.open(name, "r")) {
                int nxtmfn = xrf.getMST().nxtmfn();
                int mfn = 0;
                while(++mfn < nxtmfn) {
                    if(xrf.next() > 0) continue;
                    ++countReaded;
                    tags = xrf.read(mfn);
                    plus.write(tags);
                }
                System.out.println(name + " plus " + countReaded);
            }
            for(int i=2; i<args.length; i++) {
                name=args[i];
                try (TXT64 txt = TXT64.open(name)) {
                    while((tags = txt.read()) != null) {
                        ++countReaded;
                        plus.write(tags);
                    }
                    System.out.println(name + " plus " + countReaded);
                }
            }
        }
    }
}
