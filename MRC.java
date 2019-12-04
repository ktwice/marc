/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package marc;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

/**
 *
 * @author marina
 */
public class MRC {
//    private int mfn;
    private RandomAccessFile raf;
//    private Charset charset;
    private final int head_size = 5;
    private final byte[] head = new byte[head_size];
    private int len;
    private long readed;

    public long getReaded() {return readed;}
    public RandomAccessFile getRAF() {return raf;}

    public int export(int start, int count) throws FileNotFoundException, IOException {
        int iCount = 0;
        int bCount = 0;
        RandomAccessFile rafExport = new RandomAccessFile("export.mrc","rw");
        try {
            while (start-->0) {
                if (next()<0) return 0;
                skip();
            }
            while (count!=0) {
                if(next()<0) return iCount;
                byte[] bytes = read();
                bCount += bytes.length;
                iCount++;
                rafExport.write(bytes);
                if (count>0) --count;
            }
            rafExport.setLength(bCount);
        }
        finally {
            rafExport.close();
        }
        System.out.print("\nBytes exported="+bCount);
        System.out.print("\nRecords exported="+iCount);
        return iCount;
    }
    public void open(String name, String mode)
            throws FileNotFoundException, IOException {
        if (name.indexOf('.')<0) name = name.concat(".MRC");
        raf = new RandomAccessFile(name,mode);
        readed = 0;
    }
    public void close() throws IOException { raf.close(); }

    public int next() throws IOException {
        try {
            raf.readFully(head);
            String s = new String(head);
            try { len = Integer.valueOf(s);
            }catch(Exception e){
            System.out.println("MRC.next() error "+e.getMessage()+". offset="+raf.getFilePointer()+". len="+s);
            return -2;
            }
            return len;
        }
        catch (EOFException e){
            return -1;
        }
    }

    public byte[] read() throws IOException {
        byte[] bytes = Arrays.copyOf(head,len);
        raf.readFully(bytes,head_size,len-head_size);
        readed += len;
        return bytes;
    }

    public void skip() throws IOException { raf.skipBytes(len-head_size); }

//    public Charset getCharset() { return charset;}

}
