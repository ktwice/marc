package marc;

import java.io.*;
import java.util.*;
import static marc.Rec64.XRF_FLAGS;
//import static java.lang.Math.*;

/**
 * XRF - файл c таблицей указателей на записи в MST-файле.
 * extends буфер xrf-записи
 * <br/>
 * номер строки таблицы = mfn
 * <br/>
 * статические методы open и create создают объект
 * метод close грамотно закрывает объект
 * <br/>
 * next() читает в буфер следующий элемент xrf файла 
 * <br/>
 * next(int) читает в буфер заданный элемент xrf файла
 * <br/>
 * write(без mfn) пишет в буфер новый элемент
 * <br/>
 * write(с mfn) берет данные из буфера и сверяет mfn
 * <br/>
 * кроме next и write(без mfn), все методы работают с текущим буфером
 * и не выполняют переходов к другому элементу
 * <br/>
 * методы write* если изменяют буфер, то сразу это фиксируют в файле
 * 
 * @author ktwice
 */
public class XRF64 extends Rec64 implements Closeable{
    private XRF64(MST64 mst, RandomAccessFile raf) {
        super(Rec64.XRF_SIZEOF);
        this.mst = mst;
        this.raf = raf;
    }
/**
 * доступ к связанному *.mst файлу
 */    
    private MST64 mst;
/**
 * *.xrf файл
 */    
    private RandomAccessFile raf;
/**
 * открыть *.xrf файл
 * @param name - имя файла без расширения
 * @param mode - r или rw
 * @return управляющая запись связаного MST-файла
 * @throws java.io.FileNotFoundException
 * @throws java.io.IOException
 */
    static public XRF64 open(final String name, final String mode)
            throws FileNotFoundException, IOException {
        RandomAccessFile raf = new RandomAccessFile(name+".XRF", mode);
        MST64 mst = MST64.open(name, mode);
        return new XRF64(mst, raf);
    }
/**
 * пересозть *.xrf файл на запись
 * @param name - имя файла без расширения
 * @return управляющая запись связаного MST-файла
 * @throws java.io.FileNotFoundException
 * @throws java.io.IOException
 */
    static public XRF64 create(final String name)
            throws FileNotFoundException, IOException {
        String mode = "rw";
        RandomAccessFile raf = new RandomAccessFile(name+".XRF", mode);
        raf.setLength(0);
        MST64 mst = MST64.create(name);
        return new XRF64(mst, raf);
    }
/**
 * грамотное завершение работы с объектом
 * @throws IOException 
 */    
    @Override
    public void close() throws IOException {
        mst.close();
        raf.close();
    }
/**
 * получить доступ к MST64
 * @return
 */    
    public MST64 getMST() { return mst; }
/**
 * из текущей записи достать позицию в *.mst файле
 * @return 
 */    
    public long getPos() {return getLowHighLong(Rec64.XRF_LOW);}
/**
 * чтение xrf
 * @return установленые нереальные биты
 * @throws IOException 
 */    
    public int next() throws IOException {
        raf.readFully(bs);
        return ib.get(Rec64.XRF_FLAGS) & Rec64.XRF_FLAGS_NOTREAL_MASK;
    }
/**
 * чтение xrf с позиционированием
 * @param mfn позиция
 * @return установленые нереальные биты
 * @throws IOException если там не тот mfn
 */    
    public int next(int mfn) throws IOException {
        raf.seek((mfn-1) * Rec64.XRF_SIZEOF);
        raf.readFully(bs);
        return ib.get(Rec64.XRF_FLAGS) & Rec64.XRF_FLAGS_NOTREAL_MASK;
    }
/**
 * чтение mst соответствующего буферу (последним next)
 * @param mfn ожидаемый (и обязательный)
 * @return запись из mst полностью
 * @throws IOException если там не тот mfn
 */    
    public Map<Integer,List<Field>> read(int mfn) throws IOException {
        long pos = getLowHighLong(Rec64.XRF_LOW);
        byte[] bytes = mst.read(pos, mfn);
        Rec64 rec64 = new Rec64(bytes);
        return rec64.newTags();
    }
/**
 * добавить новую запись в mst
 * @param rec64 содержание новой записи
 * @throws java.io.IOException
 */
    public int write(Rec64 rec64) throws IOException {
        long pos = mst.write(rec64); // добавляем в конец mst
        int mfn = rec64.ib.get(Rec64.REC64_MFN); // было выставлено при добавлении в mst
        putLowHighLong(Rec64.XRF_LOW, pos);
        ib.put(Rec64.XRF_FLAGS, Rec64.BIT_NOTACT_REC);
        raf.seek((mfn-1) * Rec64.XRF_SIZEOF);
        raf.write(bs);
        return mfn;
    }
/**
 * добавить новую версию записи в mst
 * @param rec64 содержание новой записи
 * @return mfn старой версии
 * @throws java.io.IOException
 */
    public void write(Rec64 rec64, int mfn) throws IOException {
        long pos = getLowHighLong(Rec64.XRF_LOW);
        long nxt = mst.write(pos, mfn, rec64);
        putLowHighLong(Rec64.XRF_LOW, nxt);
        ib.put(Rec64.XRF_FLAGS, Rec64.BIT_NOTACT_REC);
        raf.seek((mfn-1) * Rec64.XRF_SIZEOF);
        raf.write(bs);
    }
/**
 * добавить новую запись
 * @param tags содержание новой записи
 * @throws java.io.IOException
 */
    public int write(Map<Integer,List<Field>> tags) throws IOException {
        return write(new Rec64(tags));
    }
/**
 * добавить новую версию записи
 * @param tags содержание новой записи
 * @return mfn старой версии
 * @throws java.io.IOException
 */
    public void write(Map<Integer,List<Field>> tags, int mfn) throws IOException {
        write(new Rec64(tags), mfn);
    }
/**
 * согласованая установка флагов
 * @param mfn куда
 * @param status какие флаги выставить
 * @throws IOException 
 */    
    public void write(int status, int mfn) throws IOException {
        long pos = getLowHighLong(Rec64.XRF_LOW);
        mst.write(pos, mfn, status + Rec64.BIT_NOTACT_REC + Rec64.BIT_LAST_REC);
        ib.put(Rec64.XRF_FLAGS, status + Rec64.BIT_NOTACT_REC);
        raf.seek((mfn-1) * Rec64.XRF_SIZEOF);
        raf.write(bs);
    }
    @Override
    public String xml() {
        return "<xrf"+xmlAttr()+"/>";
    }
    @Override
    public String xmlAttr() {
        return XLines.attr("flags", Field.bitString(ib.get(XRF_FLAGS)));
    }
//------------------------------------------------------------------------------    
    public void printHistory(int mfn) throws IOException {
        System.out.println();
        System.out.println("<mfn xml:id=\""+mfn+"\"/> "+xml());
        for(Rec64 rec64:mst.readHistory(getLowHighLong(Rec64.XRF_LOW), mfn)){
            System.out.print(rec64.xml());
            List<Field> fields = rec64.newTags().get(907);
            if(fields!=null) {
                int n = fields.size();
                System.out.print(" v907#"+n+"="+fields.get(n-1).get());
            }
            System.out.println();
        }
    }
    public void printHistory2(int mfn) throws IOException {
        System.out.println();
        //System.out.print("mfn="+mfn);
        for(Rec64 rec64:mst.readHistory(getLowHighLong(Rec64.XRF_LOW), mfn)) {
            //rec64.printRec64();
            System.out.println(rec64.xml());
            Field.print2(rec64.newTags());
        }
    }

}
