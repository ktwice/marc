/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marc;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Closeable File as sequence of byte-blocks with block-length in first 5 digit-bytes
 * @author k2
 */
public class MrcInputStream implements Closeable {
    private InputStream fin;
    private final int head_size = 5;
    private final byte[] head = new byte[head_size];
    private int len;

    static public String defaultExt(String fname, String ext) {
        if(fname.indexOf('.',fname.lastIndexOf(java.io.File.separatorChar))<0)
            return fname+".mrc";
        return fname;
    }
/**
 * Builder/Constructor
 * @param fname File name (default file-ext=.mrc)
 * @return File-linked object
 * @throws FileNotFoundException
 * @throws IOException 
 */    
    static public MrcInputStream build2(String fname)
            throws FileNotFoundException, IOException {
        MrcInputStream m = new MrcInputStream();
        m.fin = new FileInputStream(defaultExt(fname,".mrc"));
        return m;
    }
    
    @Override
    public void close() throws IOException {
        if(fin == null) return;
        fin.close(); 
        fin = null;
    }
/**
 * Prepare for read the record
 * @return length of record or minus
 * @throws IOException 
 */
    public int next() throws IOException {
        if(len<0) return (len = -3); // minus-blocked
        if(len>0) fin.skip(len-head_size);
        if(fin.read(head)<head.length) return (len = -1); // eof
        try {
            return (len = Integer.valueOf(new String(head)));
        }catch(Exception e){
            System.out.println("MrcInputStream.next() error "+e.getMessage());
            return (len = -2);
        }
    }
    
    public int next(int count) throws IOException, Exception {
        if(count<1)
            throw new Exception("MrcInputStream.next(int) error argument less then 1");
        while(--count>0) {
            int n = next();
            if(n < 0) return n;
        }
        return next();
    }
/**
 * Read record
 * @return record wholly
 * @throws IOException
 * @throws Exception 
 */
    public byte[] read() throws IOException, Exception {
        if(len<head_size)
            throw new Exception("MrcInputStream.read() error len<head_size");
        byte[] bytes = Arrays.copyOf(head,len);
        if(fin.read(bytes,head_size,len-head_size)<len-head_size)
            throw new Exception("MrcInputStream.read() error no so many bytes="+(len-head_size));
        len=0; // set record-border marker
        return bytes;
    }
}
