/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package marc;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * helper для XRF64 ReadOnly
 * @author k2
 */
public class Reader64 implements Closeable{
/**
 * ядро оболочки
 */    
    private XRF64 xrf;
/**
 * запомнили верхнюю границу в статическом конструкторе open
 */    
    private int nxtmfn;
/**
 * храним текущее положение (последний вызов next)
 */    
    private int mfn = 0;
/**
 * скрываем простой конструктор
 */    
    private Reader64() {}
/**
 * статический конструктор
 * @param name имя базы
 * @return наш helper
 * @throws FileNotFoundException
 * @throws IOException 
 */    
    static public Reader64 open(String name) throws FileNotFoundException, IOException {
        XRF64 xrf = XRF64.open("c:\\IRBIS64\\Datai\\"+name+"\\"+name, "r");
        Reader64 r64 = new Reader64();
        r64.xrf = xrf;
        r64.nxtmfn = xrf.getMST().nxtmfn();
        return r64;
    }
    @Override
    public void close() throws IOException {
        xrf.close();
    }
/**
 * позиционирование по xrf файлу
 * @param mfn какая запись интересует (должно быть больше нуля и меньше nxtmfn)
 * @return нереальные ключи записи, если они есть или просто ноль.
 * -1 обозначает что задан неверный номер позиции
 * @throws IOException 
 */    
    public int next(int mfn) throws IOException {
        if(mfn <= 0) return -1;
        if(mfn >= nxtmfn) return -1;
        this.mfn = mfn;
        return xrf.next(mfn);
    }
/**
 * позиционирование по xrf файлу на следующий mfn
 * @return нереальные ключи записи, если они есть или просто ноль.
 * -1 обозначает что задан неверный номер позиции 
 * @throws IOException 
 */    
    public int next() throws IOException {
        return next(mfn+1);
    }
/**
 * чтение содержания mst файла,
 * соответствующего текущей позиции в xrf файле
 * @return tags
 * @throws IOException 
 */    
    public Map<Integer,List<Field>> read() throws IOException {
        return xrf.read(mfn);
    }
/**
 * номер текущей позиции
 * @return mfn
 */    
    public int getMfn() {return mfn;}
/**
 * номер первой несуществующей позиции
 * @return mfn
 */    
    public int getNxtMfn() {return nxtmfn;}
}
