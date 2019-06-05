package marc;

import java.io.*;
import java.nio.*;
import java.util.*;
import static marc.Rec64.MST_NXTMFN;

/**
 * MST-файл записей
 * @author ktwice
 */
public class MST64 extends Rec64 implements Closeable{
/**
 * размер предварительно считываемого головного участка
 * минимально возможный с mfrl и mfn
 */    
    static final int HEAD_SIZEOF = 4 * (1+Rec64.REC64_MFRL);
/**
 * буфер для считывания головного участка
 */    
    private Rec64 rec64head = new Rec64(HEAD_SIZEOF);
    private RandomAccessFile raf;
    private String fname;
    private MST64(RandomAccessFile raf) {
        super(Rec64.MST_SIZEOF);
        this.raf = raf;
    }
/**
 * достает nxtmfn из буфера управляющей записи
 * @return mfn для добавления новой записи
 */
    public int nxtmfn() { return ib.get(Rec64.MST_NXTMFN); }
/**
 * статический конструктор на существующий файл
 * @param name имя файла без расширения
 * @param mode r или rw
 * @return готовый объект
 */
    static public MST64 open(final String name, final String mode)
            throws FileNotFoundException, IOException {
        RandomAccessFile raf = new RandomAccessFile(name+".MST", mode);
        System.out.println();
        System.out.println(name+".MST opened.");
        MST64 mst = new MST64(raf);
        mst.fname = name;
        raf.readFully(mst.bs);
        System.out.println(mst.xml());
        return mst;
    }
/**
 * статический конструктор на вновь создаваемый файл
 * @param name имя файла без расширения
 * @return готовый объект
 */    
    static public MST64 create(final String name)
            throws IOException {
        String mode = "rw";
        RandomAccessFile raf = new RandomAccessFile(name+".MST", mode);
        raf.setLength(0);
        raf.seek(0);
        System.out.printf("\n%s.MST created!", name);
        MST64 mst = new MST64(raf);
        mst.fname = name;
        mst.ib.put(Rec64.MST_NXTMFN, 1);
        mst.ib.put(Rec64.MST_NXT_LOW, Rec64.MST_SIZEOF);
        raf.write(mst.bs);
        return mst;
    }
/**
 * грамотное завершение работы с объектом
 * @throws IOException 
 */    
    @Override
    public void close() throws IOException {
        raf.close(); 
    }
/**
 * прочитать следующий заголовок
 * @return mfn из прочитанного заголовка
 * @throws java.io.IOException
 */
    public int next() throws IOException {
        raf.readFully(rec64head.bs); 
        return rec64head.ib.get(Rec64.REC64_MFN);
    }
/**
 * прочитать заголовок записи по смещению
 * @param pos - смещение нужной записи
 * @return mfn из прочитанного заголовка
 * @throws java.io.IOException
 */
    public int next(long pos) throws IOException {
        raf.seek(pos);
        raf.readFully(rec64head.bs); 
        return rec64head.ib.get(Rec64.REC64_MFN);
    }
/**
 * пропустить весь остаток записи без ее полного считывания
 * должна следовать за readHead
 * @throws java.io.IOException
 */
    public void skip() throws IOException {
        int mfrl = rec64head.ib.get(Rec64.REC64_MFRL);
        raf.skipBytes(mfrl - HEAD_SIZEOF);
    }
/**
 * дочитать остаток записи полностью
 * должна следовать за readHead
 * @return весь массив байтов полной записи
 * @throws java.io.IOException
 */
    public byte[] read() throws IOException {
        int mfrl = rec64head.ib.get(Rec64.REC64_MFRL);
        byte[] bytes = Arrays.copyOf(rec64head.bs, mfrl);
        raf.readFully(bytes, HEAD_SIZEOF, mfrl - HEAD_SIZEOF);
        return bytes;
    }
/**
 * прочитать запись полностью
 * @return весь массив байтов полной записи
 * @param pos позиция записи
 * @param mfn ожидаемый
 * @throws java.io.IOException если mfn не тот
 */
    public byte[] read(long pos, int mfn) throws IOException {
        raf.seek(pos);
        raf.readFully(rec64head.bs); 
        if(mfn != rec64head.ib.get(Rec64.REC64_MFN))
            throw new IOException("MST mismatch XRF.mfn="+mfn);
        int mfrl = rec64head.ib.get(Rec64.REC64_MFRL);
        byte[] bytes = Arrays.copyOf(rec64head.bs, mfrl);
        raf.readFully(bytes, HEAD_SIZEOF, mfrl - HEAD_SIZEOF);
        return bytes;
    }
/**
 * добавить новую запись и скорректировать управляющую запись
 * @param rec64 новая запись
 * @param mfn новый mfn
 * @return позиция добавленой записи
 */
    private long add2(int mfn, Rec64 rec64) throws IOException {
        IntBuffer ib64 = rec64.getIntBuffer();
        int mfrl = ib64.get(Rec64.REC64_MFRL);
// чтение буфера управляющей записи        
        long nxt = getLowHighLong(Rec64.MST_NXT_LOW);
// запись в буфер управляющей записи
        putLowHighLong(Rec64.MST_NXT_LOW, nxt + mfrl);
// дооформление буфера новой записи        
        ib64.put(Rec64.REC64_MFN, mfn);
        ib64.put(Rec64.REC64_STATUS, Rec64.BIT_LAST_REC + Rec64.BIT_NOTACT_REC);
// фиксируем новую запись
        raf.seek(nxt);
        raf.write(rec64.bs);
//        if(rec64.fs != null)
//            for(byte[] f: rec64.fs)
//                raf.write(f);
// фиксируем управляющую запись        
        raf.seek(0);
        raf.write(bs);
        return nxt;
    }
/**
 * добавить новую запись и скорректировать управляющую запись
 * @param rec64 новая запись
 * @return позиция добавленой записи
 */
    public long write(Rec64 rec64) throws IOException {
//        IntBuffer ib64 = rec64.getIntBuffer();
        rec64.ib.put(Rec64.REC64_VERSION, 1);
// чтение буфера управляющей записи        
        int mfn = ib.get(Rec64.MST_NXTMFN);
// запись в буфер управляющей записи
        ib.put(Rec64.MST_NXTMFN, mfn + 1);
        return add2(mfn, rec64);
    }
/**
 * добавить новую версию записи и скорректировать управляющую запись
 * @param rec64 новая версия записи
 * @param mfn старый номер
 * @param pos старая позиция
 * @return новая позиция
 */
    public long write(long pos, int mfn, Rec64 rec64) throws IOException {
// читаем старую запись - все до (но не включая) статуса!        
        Rec64 prev64 = new Rec64(4 * Rec64.REC64_STATUS);
        raf.seek(pos);
        raf.readFully(prev64.bs); 
        if(mfn != prev64.ib.get(Rec64.REC64_MFN))
            throw new IOException("MST mismatch XRF.mfn=" + mfn);
//        long mfb = prev64head.getLowHighLong(Rec64.REC64_MFB_LOW);
// переписываем статус!        
        raf.writeInt(Rec64.BIT_NOTACT_REC); 
//        int version = raf.readInt(); // как раз после статуса стоит версия
// дооформление буфера новой записи        
        int version = prev64.ib.get(Rec64.REC64_VERSION); 
        rec64.ib.put(Rec64.REC64_VERSION, version+1);
        rec64.putLowHighLong(Rec64.REC64_MFB_LOW, pos);
        return add2(mfn, rec64);
    }
/**
 * установить новый флаг статуса
 * @param mfn номер
 * @param pos позиция
 * @param status новый флаг
 */
    public void write(long pos, int mfn, int status) throws IOException {
// читаем старую запись - все до статуса!        
        Rec64 prev64head = new Rec64(4 * Rec64.REC64_STATUS);
        raf.seek(pos);
        raf.readFully(prev64head.bs); 
        if(mfn != prev64head.ib.get(Rec64.REC64_MFN))
            throw new IOException("MST mismatch XRF.mfn=" + mfn);
// переписываем статус!        
        raf.writeInt(status); 
    }
/**
 * строит список всех версий записи
 * @param pos
 * @param mfn
 * @return список версий начиная с текущей и до самой первой
 * @throws IOException 
 */
    public List<Rec64> readHistory(long pos, int mfn) throws IOException {
        List<Rec64> h = new ArrayList<>();
        while(true) {
            Rec64 rec64 = new Rec64(read(pos, mfn));
            h.add(rec64);
            pos = rec64.getLowHighLong(Rec64.REC64_MFB_LOW);
            if(pos==0) break;
            int version = rec64.ib.get(Rec64.REC64_VERSION);
            if(version==1) break;
        }
        return h;
    }
    @Override
    public String xml() {
        return "<mst"+xmlAttr()+"/>";
    }
    @Override
    public String xmlAttr() {
        return XLines.attr("nxtmfn", ""+ib.get(MST_NXTMFN))
            +XLines.attr("nxt", ""+getLowHighLong(MST_NXT_LOW))
            +XLines.attr("mfcxx3", Field.bitString(ib.get(MST_MFCXX3)))
            ;    
    }
}
