package marc;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

/**
 *
 * @author ktwice
 */
public class XLines {
    private static final Charset xmlCharset = Charset.forName("utf-8");
    private static final byte[] xmlNewLine = new byte[] {0x0A};
    private static final String smargin = "                ";

    private List<StringBuilder> lines = new ArrayList<StringBuilder>();
    private int margin = 0;
    private FileOutputStream xmlOutputStream = null;
    private boolean nl = true;

    public XLines() {}
    public XLines(String xml1) {
        xml1(xml1);
    }
    public XLines(String filename, String dir) {
        try {
            open(filename, dir);
        } catch (FileNotFoundException ex) {
            xmlOutputStream = null;
//            Logger.getLogger(XLines.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            xmlOutputStream = null;
//            Logger.getLogger(XLines.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //public XLines(int mmax) {smargin = smargin16.substring(0,2+mmax);}
     final public void open(String filename, String dir) throws FileNotFoundException, IOException {
        if(dir==null) dir = "";
        else if(!dir.endsWith("\\")) dir += "\\";
        xmlOutputStream = new FileOutputStream(dir+filename+".xml");
        xmlOutputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes(xmlCharset));
        try {
            FileInputStream fis = new FileInputStream(filename+".dtd");
            try {
                byte[] bytes = new byte[4096];
                xmlOutputStream.write(xmlNewLine);
                int len = fis.read(bytes);
                int off = 0;
                while(bytes[off]!='<') ++off;
                xmlOutputStream.write(bytes, off, len-off);
                System.out.println(filename+".dtd injected.");
            }
            finally {
                fis.close();
            }
        }
        catch (Exception e) {}
        newline();
    }
    public void write() throws IOException {
        if(xmlOutputStream == null) return;
        for(StringBuilder line:lines) {
            xmlOutputStream.write(xmlNewLine);
            xmlOutputStream.write(line.toString().getBytes(xmlCharset));
        }
        lines.clear();
        nl = true;
    }
    public void println() throws IOException {
        for(StringBuilder line:lines) {
            System.out.println();
            System.out.print(line.toString());
        }
    }
    public void close() throws IOException {
        if(xmlOutputStream == null) return;
        write();
        System.out.println(xmlOutputStream.getChannel().position()/1024/1024+" Мбайт в xml-файле.");
        xmlOutputStream.close();
        xmlOutputStream = null;
    }
    public void close(String xml2) throws IOException {
        xml2(xml2);
        close();
    }
    public boolean isEmpty() {return lines.isEmpty();}
    public XLines newline() {
        nl = true;
        return this;
    }
    private void add(String... xml) {
        StringBuilder sb;
        if(nl) {
            sb = new StringBuilder().append(smargin.substring(0, margin));
            lines.add(sb);
            nl = false;
        }
        else sb = lines.get(lines.size()-1);
        for(String s:xml) sb.append(s);
        /*int m = xml.indexOf('/');
        if(m<0) margin++;
        else if(m==1) --margin;*/
    }
    public XLines add(XLines xlines) {
        if(xlines!=null) {
            String sm = smargin.substring(0, margin);
            for(StringBuilder line:xlines.lines)
                lines.add(line.insert(0,sm));
            margin += xlines.margin;
        }
        return this;
    }
    public void reset() {
        nl = true;
        lines.clear();
        margin = 0;
    }
    static public String attr(String aname, String atext) {
        if(atext==null) return "";
        return(new StringBuilder()
                .append(" ")
                .append(aname)
                .append("=\"")
                .append(text2xml(atext))
                .append("\"")
                .toString()
            );
    }
    static public String text2xml(String text) {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            //.replace("'", "&apos;")
            //.replace("?", "&quot;")
            ;
    }
    static public String xml2text(String xml) {
        return xml
            //.replace("&quot;", "?")
            //.replace("&apos;", "'")
            .replace("&gt;", ">")
            .replace("&lt;", "<")
            .replace("&amp;", "&");
    }
    final public void xml1(String tag) {
        add("<",tag,">");
        ++margin;
    }
    public void xml1(String tag, String attr) {
        add("<",tag,attr,">");
        ++margin;
    }
    public void xml1(String tag, String aname, String atext) {
        add("<",tag,attr(aname, atext),">");
        ++margin;
    }
    public void xml2(String tag) {
        --margin;
        add("</",tag,">");
    }
    public void xml(String tag) {
        add("<",tag,"/>");
    }
    public void xml(String tag, String text) {
        if(text == null || text.isEmpty()) return;
        add("<",tag,">",text2xml(text),"</",tag,">");
    }
    public void xmlEmptyText(String tag, String text) {
        String s = (text == null) ? "" : text2xml(text);
        add("<",tag,">", s, "</",tag,">");
    }
    public void xml(String tag, String attr, String text) {
        if(text == null || text.isEmpty())  add("<",tag,attr,"/>");
        else add("<",tag,attr,">",text2xml(text),"</",tag,">");
    }
    public void xml(String tag, String aname, String atext, String text) {
        xml(tag, attr(aname, atext), text);
    }
}
