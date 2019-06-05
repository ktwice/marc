/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package marc;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * чтение txt файлов
 * @author ktwice
 */
public class TXT64 implements Closeable{
    private RandomAccessFile raf;
    private Charset charset;
    final private int bsize = 4096;
    private byte[] b = new byte[bsize];
    private int boff = b.length;
    private String filename;
    private String name;

    static private TXT64 create(String name, Charset charset, String opt)
            throws FileNotFoundException, IOException {
        String filename;
        if(name.indexOf('.') < 0) filename = name.concat(".TXT");
        else filename = name;
        RandomAccessFile raf = new RandomAccessFile(filename, opt);
        TXT64 txt = new TXT64();
        txt.charset = charset;
        txt.raf = raf;
        txt.filename = filename;
        return txt;
    }
    static public TXT64 open(String name, Charset charset)
            throws FileNotFoundException, IOException {
        return create(name,charset,"r");
    }
    static public TXT64 open(String name)
            throws FileNotFoundException, IOException {
        return open(name, Charset.forName("windows-1251"));
    }
    static public TXT64 rewrite(String name, Charset charset)
            throws FileNotFoundException, IOException {
        return create(name,charset,"rw");
    }
    static public TXT64 rewrite(String name)
            throws FileNotFoundException, IOException {
        return rewrite(name, Charset.forName("windows-1251"));
    }
    public String getName() {
        return name;
    }
    public String getFileName() {
        return filename;
    }
    @Override
    public void close() throws IOException {
        raf.close();
    }
    protected String readLine() throws IOException {
        final byte stopbyte = 10;
        String line = "";
        while(true) {
            for(int i=boff; i<b.length; i++) {
                if(b[i] != stopbyte) continue;
                line += new String(b, boff, i-boff, charset);
                boff = i + 1;
                return line;
            }
            if(b.length < bsize) break;
            line += new String(b, boff, bsize-boff, charset);
            int readed = raf.read(b);
            if(readed < b.length) b = Arrays.copyOf(b, readed);
            boff = 0;
        }
        return line.isEmpty() ? null : line;
    }
    public Map<Integer,List<Field>> read() throws IOException {
        String line;
        Map<Integer,List<Field>> tags = new TreeMap<>();
        while((line = readLine()) != null) {
            char ch = line.charAt(0);
            if(ch=='*') return tags;
            if(ch!='#') throw new IOException("Первый символ в txt - не #");
            int i = line.indexOf(':');
            if(i < 0) throw new IOException("В строке нет двоеточия ':'");
            int tag = Integer.parseInt(line.substring(1, i));
            String text = line.substring(i+1).trim();
            Field f = new Field();
            f.setString(text);
            Field.tagsAdd(tags, tag, f);
        }
        return tags.isEmpty() ? null : tags;
    }
    public void write(Map<Integer,List<Field>> tags)
            throws IOException {
//        final Charset outcharset = Charset.forName("windows-1251");
        final byte[] nlcr = {13,10};
        final byte[] eor = "*****".getBytes();
        for(Map.Entry<Integer,List<Field>> e:tags.entrySet()) {
            int tag = e.getKey();
            for(Field f:e.getValue()) {
                raf.write(("#"+tag+": ").getBytes());
                raf.write(f.get().getBytes(charset));
                raf.write(nlcr);
            }
        }
        raf.write(eor);
        raf.write(nlcr);
    }
}
