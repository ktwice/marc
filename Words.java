/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package marc;

import java.util.*;
import java.util.regex.*;

/**
 *
 * @author marina
 */
public class Words {
    private int wsize;
    private int nsize;
    private int pos;
    private String s;
    static Pattern asciiPattern = Pattern.compile("[\\u0000-\\u007F]");
    static Pattern digitPattern = Pattern.compile("[^0-9]");
    static Pattern letterPattern = Pattern.compile("[^a-zA-Z]");
    static Pattern noPattern = Pattern.compile("[0-9a-zA-Z[^\\u0000-\\u007F]]");
    static Pattern pDoubleSpaces = Pattern.compile(" [ ]+");
    static Pattern pAddSpaceBefore = Pattern.compile("[ ]*([\\(])");
    static Pattern pAddSpaceAfter = Pattern.compile("([\\)\\;\\.\\,])[ ]*");
    static Pattern pDelSpaceBefore = Pattern.compile(" ([\\/\\-\\)\\;\\.\\,])");
    static Pattern pDelSpaceAfter = Pattern.compile("([\\/\\-\\(]) ");

    public Words(int wsize, int nsize) {
        this.wsize = wsize;
        this.nsize = nsize;
    }
/**
 * Нормализовать пробелы возле пунктуации
 * @param s вход - результат normalizeSpaces()
 * @return выход - нормализованы пробелы рядом с пунктуацией
 */
    static public String normalizeSpaces(String s) {
        s = pDoubleSpaces.matcher(s).replaceAll(" "); // убрать двойные пробелы
//System.out.println("\t>>>"+s+"<<< pDoubleSpaces");
        s = pAddSpaceBefore.matcher(s).replaceAll(" $1"); // добавить пробел перед
//System.out.println("\t>>>"+s+"<<< pAddSpaceBefore");
        s = pAddSpaceAfter.matcher(s).replaceAll("$1 "); // добавить пробел после
//System.out.println("\t>>>"+s+"<<< pAddSpaceAfter");
        s = s.trim(); 
//System.out.println("\t>>>"+s+"<<< trim()");
        s = pDelSpaceBefore.matcher(s).replaceAll("$1"); // убрать пробел перед
//System.out.println("\t>>>"+s+"<<< pDelSpaceBefore");
        s = pDelSpaceAfter.matcher(s).replaceAll("$1"); // убрать пробел после
//System.out.println("\t>>>"+s+"<<< pDelSpaceAfter");
        return s;            
    }
    static int near = Math.max('z'-'A','я'-'А');
/**
 * функция ищет иностранные буквы или слова
 * @param s текст
 * @return 0, если ничего нет
 * -позиция для иностранного слова
 * +позиция для иностранной буквы
 */    
    static public int farChar(String s) {
        int offset = 0; //перебираем все позиции
        int word = 0; // фиксируем позицию, где слово на другом языке
        char first = 0; // первая буква строки
        char firstword = 0; // первая буква слова
        for(char ch:s.toCharArray()) { 
            ++offset;
            if(Character.isLetter(ch)) {
                if(firstword==0) { // начались буквы
                    firstword = ch; // запоминаем
                    if(first==0) // первая буква
                        first = ch; // запоминаем
                    else if(word==0){ // это следующее слово?
                        int delta = first>ch ? first-ch : ch-first; // иностранное слово?
                        if(delta>near) {
                        if(ch=='ё' || ch=='Ё') {
                            ch = 'е';    
                            delta = first>ch ? first-ch : ch-first; // иностранная буква?
                            if(delta>near) word=offset;
                        } else if(first=='ё' || first=='Ё') {
                            firstword = 'е';    
                            delta = first>ch ? first-ch : ch-first; // иностранная буква?
                            if(delta>near) word=offset;
                        } else word=offset; // запоминаем иностранное слово
                        }
                    }
                } else { // это следующая буква
                    int delta = firstword>ch ? firstword-ch : ch-firstword; // иностранная буква?
                    if(delta>near) {
                        if(ch=='ё' || ch=='Ё') {
                            ch = 'е';    
                            delta = firstword>ch ? firstword-ch : ch-firstword; // иностранная буква?
                            if(delta>near) return offset;
                        } else if(firstword=='ё' || firstword=='Ё') {
                            firstword = 'е';    
                            delta = firstword>ch ? firstword-ch : ch-firstword; // иностранная буква?
                            if(delta>near) return offset;
                        } else return offset; // возвращаем иностранную букву
                    } 
                }
            } else { // не буква
                if(firstword!=0) // это первая не буква?
                    firstword=0; // закончились буквы
            }
        }
        return -word; // возвращаем иностранное слово
    }
    static String farCheck(String s) {
        int far = farChar(s);
        if(far<=0) return null;
        return s.substring(0,far-1)+"^"+s.substring(far-1);
    }
    public void reset(String s) {
        pos = 0;
        this.s = s;
    }

    public boolean cutWords(List<String> list) {
        int pos0 = pos;
        if(pos0>=s.length()) return false;
        char c = s.charAt(pos0);
        Matcher m;
        if (c >= '\u007F')
            m = asciiPattern.matcher(s);
        else if (c >='0' && c<='9')
            m = digitPattern.matcher(s);
        else if ((c >='a' && c<='z') || (c>='A' && c<='Z'))
            m = letterPattern.matcher(s);
        else {
            list.add("");
            return true;
        }
        pos = m.find(pos0+1) ? m.start() : s.length();
        while (pos-pos0 > wsize) {
            list.add(s.substring(pos0, (pos0+=wsize)));
            list.add("");
        }
        list.add(s.substring(pos0,pos));
        return true;
    }

    public void cutNoWords(List<String> list) {
        Matcher m = noPattern.matcher(s);
        int pos0 = pos;
        if (pos0>=s.length()) {
            list.add("");
            return;
        }
        pos = m.find(pos0) ? m.start() : s.length();
        while (pos-pos0 > nsize) {
            list.add(s.substring(pos0, (pos0+=nsize)));
            list.add("");
        }
        list.add(s.substring(pos0,pos));
    }

    public List<String> cut(String s) {
        List<String> list = new ArrayList<String>();
        reset(s);
        while (cutWords(list))
            cutNoWords(list);
        return list;
    }

}
