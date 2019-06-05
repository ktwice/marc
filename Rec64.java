package marc;

import java.nio.*;
import java.nio.charset.*;
import java.util.*;

/**
 * запись MST или XRF файла (набор 32-битных целых)
 * @author ktwice
 */
public class Rec64 implements LazyField {
    static final int XRF_LOW = 0;
    static final int XRF_HIGH = 1;
    static final int XRF_FLAGS = 2;
    static final int XRF_SIZEOF = 3*4;

    static final int MST_CTLMFN = 0;
    static final int MST_NXTMFN = 1;
    static final int MST_NXT_LOW = 2;
    static final int MST_NXT_HIGH = 3;
    static final int MST_MFTYPE = 4;
    static final int MST_RECCNT = 5;
    static final int MST_MFCXX1 = 6;
    static final int MST_MFCXX2 = 7;
    static final int MST_MFCXX3 = 8;
    static final int MST_SIZEOF = 9*4;

    static final int BIT_ALL_ZERO = 0;
    static final int BIT_LOG_DEL = 1;
    static final int BIT_PHYS_DEL  = 2;
    static final int BIT_ABSENT  = 4;
    static final int BIT_NOTACT_REC = 8;
    static final int BIT_LAST_REC = 32;
    static final int BIT_LOCK_REC  = 64;
//    static final int BIT_NEW_REC = ;

    static final int REC64_MFN = 0;
    static final int REC64_MFRL = 1;
    static final int REC64_MFB_LOW = 2;
    static final int REC64_MFB_HIGH = 3;
    static final int REC64_BASE = 4;
    static final int REC64_NVF = 5;
    static final int REC64_VERSION = 6;
    static final int REC64_STATUS = 7;
    static final int REC64_SIZEOF = 8;

    static final int LOOKUP_TAG = 0;
    static final int LOOKUP_POS = 1;
    static final int LOOKUP_LEN = 2;
    static final int LOOKUP_SIZEOF = 3;

    static final int XRF_FLAGS_NOTREAL_MASK = BIT_LOG_DEL+BIT_PHYS_DEL+BIT_ABSENT;

    static final Charset utf8 = Charset.forName("UTF-8");
/**
 * массив байт готовый к записи или только прочитанный
 */
    protected byte[] bs; 
/**
 * 32-битный взгляд на bs
 */    
    protected IntBuffer ib; 
/**
 * содержимое полей
 */    
//    protected List<byte[]> fs;
/**
 * конструктор из уже готового буфера,
 * @param bytes буфер, содержащий в себе 32-битные целые
 */
    Rec64(byte[] bs) { 
        this.bs = bs;
        ib = ByteBuffer.wrap(bs).asIntBuffer();
    }
/**
 * конструктор новенького еще пустого буфера
 * @param size размер буфера в байтах
 */
    Rec64(int size) {
        bs = new byte[size]; 
        ib = ByteBuffer.wrap(bs).asIntBuffer();
    }
/**
 * конструктор буфера из tags
 * @param tags источник
 */    
    Rec64(Map<Integer,List<Field>> tags) {
        List<byte[]> fs = new ArrayList<>();
        int bytesCount = setBytes(tags, fs);
        int fieldsCount = fs.size();
        int base = (REC64_SIZEOF + LOOKUP_SIZEOF * fieldsCount) * 4;
        bytesCount += base;
        bs = new byte[bytesCount];
        ib = ByteBuffer.wrap(bs).asIntBuffer();
        ib.put(REC64_BASE, base);
        ib.put(REC64_NVF, fieldsCount);
        ib.put(REC64_MFRL, bytesCount);
        ib.position(REC64_SIZEOF);
        int i = 0;
        int pos = 0;
        for(Map.Entry<Integer,List<Field>> e:tags.entrySet()) {
            int tag = e.getKey();
            int fcount = e.getValue().size();
            while( fcount-- > 0) {
                byte[] bytes = fs.get(i++);
                int len = bytes.length;
                System.arraycopy(bytes, 0, bs, base+pos, len);//bs fs pos length
                ib.put(tag);
                ib.put(pos);
                ib.put(len);
                pos += len;
            }
        }
    }
/**
 * конструктор буфера из tags
 * @param tags источник
 */    
    Rec64(Map<Integer,List<Field>> tags, boolean old) {
        List<byte[]> fs = new ArrayList<>();
        int bytesCount = setBytes(tags, fs);
        int fieldsCount = fs.size();
        int base = (REC64_SIZEOF + LOOKUP_SIZEOF * fieldsCount) * 4;
        bs = new byte[base];
        ib = ByteBuffer.wrap(bs).asIntBuffer();
        setLookup(tags, fs);
        ib.put(REC64_BASE, base);
        ib.put(REC64_NVF, fieldsCount);
        if(bytesCount%2 > 0) {
            ++bytesCount;
            fs.add(new byte[1]);
        }
        ib.put(REC64_MFRL, base + bytesCount);
    }
    public byte[] getBytes() { return bs; }
    public IntBuffer getIntBuffer() { return ib; }
/**
 * заполняет список bytes байт-массивами полей из tags
 * @param tags карта тэгов - источник
 * @return суммарный объем байт-массивов
 */
    private int setBytes(Map<Integer,List<Field>> tags, List<byte[]> fs) {
        int bytesCount = 0;
        for(List<Field> fields:tags.values())
            for(Field f:fields) {
                byte[] fbytes = f.get().getBytes(utf8);
                fs.add(fbytes);
                bytesCount += fbytes.length;
            }
        return bytesCount;
    }
/**
 * заполняет this справочником
 * @param tags карта тэгов - источник
 */
    private void setLookup(Map<Integer,List<Field>> tags, final List<byte[]> fs) {
        int i = 0;
        int pos = 0;
        ib.position(REC64_SIZEOF);
        for(Map.Entry<Integer,List<Field>> e:tags.entrySet()) {
            int tag = e.getKey();
            for(int fsize = e.getValue().size(); fsize>0; --fsize) {
                byte[] bytes = fs.get(i++);
                int len = bytes.length;
                System.arraycopy(bytes, 0, bs, pos, len);//bs fs pos length
                ib.put(tag);
                ib.put(pos);
                ib.put(len);
                pos += len;
            }
        }
    }
/**
 * собрать long из двух лежащих друг за другом int
 * сначала лежит low и сразу за ним high
 * @param lowpos смещение(позиция) int low
 * @return long готовый результат
 */    
    public long getLowHighLong(int lowpos) {
        int low = ib.get(lowpos);
        int high = ib.get(lowpos+1);
        final long pow32 = (long)256 * 256 * 256 * 256;
        return pow32*high + (low >= 0 ? low : pow32+low);
    }
/**
 * записать long в два лежащих друг за другом int
 * сначала лежит low и сразу за ним high
 * @param lowpos смещение(позиция) int low
 * @param result записываемый long
 */    
    public void putLowHighLong(int lowpos, long result) {
        ib.put(lowpos,(int)result);
        ib.put(lowpos+1,(int)(result >>> 32));
    }
/**
 * создать карту тегов
 * @return созанная карта тегов (тэг,список полей)
 */
    public Map<Integer,List<Field>> newTags() {
        int nvf = ib.get(REC64_NVF);
        int base = ib.get(REC64_BASE);
        if (base != (REC64_SIZEOF+LOOKUP_SIZEOF*nvf)*4) return null;
        Map<Integer,List<Field>> tags = new TreeMap<>();
        for(int index=0; index<nvf; index++) {
            int i = REC64_SIZEOF + index * LOOKUP_SIZEOF;
            int tag = ib.get(i + LOOKUP_TAG);
            List<Field> fields = tags.get(tag);
            if (fields == null) tags.put(tag, fields = new ArrayList<>());
            Field field = new Field();
            field.lazyInit(this, i);
            fields.add(field);
        }
        return tags;
    }
/**
 * реализация LazyField
 * @param i - метка поля
 * @return строковое представление поля
 */
    @Override
    public String getString(int i) {
        int pos = ib.get(REC64_BASE) + ib.get(i + LOOKUP_POS);
        int len = ib.get(i + LOOKUP_LEN);
        return new String(bs, pos, len, utf8);
    }
//------------------------------------------------------------------------------    
    public String xml() {
        return "<rec"+xmlAttr()+"/>";
    }
    public String xmlAttr() {
        return XLines.attr("mfn", ""+ib.get(REC64_MFN))
            + XLines.attr("mfrl", ""+ib.get(REC64_MFRL))
            + XLines.attr("nvf", ""+ib.get(REC64_NVF))
            + XLines.attr("version", ""+ib.get(REC64_VERSION))
            + XLines.attr("status", Field.bitString(ib.get(REC64_STATUS)))
            ;
    }
}
