/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package marc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author marina
 */
public class RDR {
    static BufferedOutputStream out = null;
    static final byte[] nlcr = {13,10};
    static final Charset charset = Charset.forName("windows-1251");
    static String[] texts;
    static Map<String,Integer> map;
    static Map<String,Map<Integer,List<Field>>> xn = null;
    static Map<String,Map<Integer,List<Field>>> xf = null;

    static public void setxn(Map<String,Map<Integer,List<Field>>> x) {xn = x;}
    static public void setxf(Map<String,Map<Integer,List<Field>>> x) {xf = x;}
/**
 * прочитать строку файла в массив текущей записи
 * @param in поток файла
 * @return строка файла
 * @throws IOException
 */
    private static String readLine(InputStream in) throws IOException {
        final int maxsize = 4096;
        final int nl = 13;
        int ch = in.read();
        if (ch<0) return null;
        int i = 0;
        byte[] bytes = new byte[maxsize];
        do {
            if (ch == nl) {
                in.read();
                break;
            }
            bytes[i++] = (byte)ch;
        } while((ch = in.read())>=0);
        return new String(bytes,0,i,charset);
    }
    public static void txt2irbis(String name, String dir)
            throws FileNotFoundException, IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(dir + name +".txt"));
        out = new BufferedOutputStream(new FileOutputStream(dir + name+"_irbis.txt"));
        map = new TreeMap<String,Integer>();
        int i=0; for(String s1:readLine(in).split("\t")) map.put(s1, i++);
        String line;
        int n=0;
        int[] nc = new int[] {0,0};
        while ((line = readLine(in)) != null){
            System.out.println(++n);
            texts = line.split("\t");
            String sf = text("Фамилия, Имя, Отчество");
            String sn = text("Личный номер");
            String sg = text("Группа");
            if(xn!=null && sn != null) {
                Map<Integer,List<Field>> tags = xn.get(sn);
                if(tags==null) tags = xf.get(sf);
                if(tags!=null) {
                    boolean modified = false;
                    Map<Character,String> subs = tags.get(90).get(0).getSubs();
                    if(!sg.isEmpty()) if(!subs.containsKey('e')) {
                        modified = true;
            System.out.println(sf +" Добавляем группу "+sg);
                        subs.put('e', sg);
                    }
                    if(!sn.isEmpty()) if(!subs.containsKey('j')) {
                        modified = true;
            System.out.println(sf +" Добавляем номер "+sn);
                        subs.put('j', sn);
                    }
                    Field.tagsOut(tags, out);
                    if(modified) ++nc[0];
                    continue;
                }
            }
            ++nc[1];
            System.out.println(sf +" Добавляем абитуриента ");
            String[] fio = sf.split(" ");
            fld(10,fio[0]);
            fld(11,fio[1]);
            fld(12,fio[2]);
            fld(13,sub(text("Адрес регистрации"),"b",""));
            fld(17,text("Телефон"));
            fld(21,text("Дата рождения"));
            fld(50,"магистрант");
            String subs = "";
            subs = sub(text("Формирующее подр."),"a",subs);
            subs = sub(sg,"e",subs);
            if(sn != null) subs = sub(sn,"j",subs);
            subs = sub(text("Форма освоения"),"o",subs);
            fld(90,subs);
            fld(920,"RDRU");
            out.write("*****".getBytes());
            out.write(nlcr);
        }
        in.close();
        out.close();
        System.out.println("Исправлено="+nc[0]);
        System.out.println("Добавлено="+nc[1]);
    }
/**
 * добавить значение подполя в строку поля
 * @param sub текст подполя
 * @param ch код подполя
 * @param subs строка поля
 * @return новая строка поля
 */
    static String sub(String sub, String ch, final String subs) {
        String s = sub.trim();
        if(s.isEmpty()) return subs;
        return subs+"^"+ch+s;
    }
/**
 * записать строку поля в выходной файл out
 * @param tag код поля
 * @param subs сформированая строка поля
 * @throws IOException
 */
    static void fld(int tag, String subs) throws IOException {
        String s = subs.trim();
        if(s.isEmpty()) return;
        out.write(("#"+tag+": ").getBytes());
        out.write(s.getBytes(charset));
        out.write(nlcr);
    }
/**
 * содержимое заданного столбика в текущей записи
 * @param s1 заголовок столбика
 * @return соответствующий элемент текущей записи
 */
    static String text(String s1) {
        Integer i = map.get(s1);
        if(i==null) {
            System.out.println("No Such Header <"+s1+">");
            return null;
        }
        if(i>=texts.length) return "";
        String s = texts[i].trim();
        if(!s.startsWith("\"")) return s;
        return s.substring(1,s.length()-1).replace((char)10,' ');
    }
}
