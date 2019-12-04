package marc;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * чтение xml файлов записей
 * @author ktwice
 */
public class XML64 implements Closeable{
XMLStreamReader r;

    public XMLStreamReader getr() {return r;}

    static public XML64 open(String name)
            throws XMLStreamException, FileNotFoundException {
        XMLInputFactory f = XMLInputFactory.newInstance();
        XMLStreamReader r = f.createXMLStreamReader(new FileInputStream(name));
        try {
            XML64 x = new XML64();
            x.r = r;
//            while(r.hasNext()) {
//                int i = r.next();
//                if(i == XMLStreamConstants.START_DOCUMENT) break;
//            }
//            System.out.println("XMLStreamConstants.START_DOCUMENT:");
//            System.out.println("XMLStreamReader.getVersion()="
//                    +r.getVersion());
//            System.out.println("XMLStreamReader.getEncoding()="
//                    +r.getEncoding());
//            System.out.println("XMLStreamReader.getCharacterEncodingScheme()="
//                    +r.getCharacterEncodingScheme());
            while(r.hasNext()) {
                int i = r.next();
                if(i != XMLStreamConstants.START_ELEMENT) continue;
                System.out.println("START_ELEMENT="+r.getLocalName());
                System.out.println("XMLStreamReader.getVersion()="
                        +r.getVersion());
                System.out.println("XMLStreamReader.getEncoding()="
                        +r.getEncoding());
                System.out.println("XMLStreamReader.getCharacterEncodingScheme()="
                        +r.getCharacterEncodingScheme());
                return x;
            }
        } catch (XMLStreamException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Override
    public void close(){
        try {
            r.close();
        } catch (XMLStreamException ex) {
            System.out.println(ex.getMessage());
        }
    }
/**
    @param name element-name
    @return false if no such childs
    @throws XMLStreamException 
    */
    public boolean nextChild(String name) throws XMLStreamException {
        while(r.hasNext()) {
            int i = r.next();
            if(i == XMLStreamConstants.END_ELEMENT) return false;
            if(i != XMLStreamConstants.START_ELEMENT) continue;
            if(r.getLocalName().equals(name)) return true;
            int level = 0;
            while(r.hasNext()) {
                i = r.next();
                if(i == XMLStreamConstants.END_ELEMENT) {
                    if(level-- == 0) break;
                } else if(i == XMLStreamConstants.START_ELEMENT)
                    ++level;
            }
        }
        return false;
    }
/**
    reads all s to new Field
     * @return new Field
     * @throws javax.xml.stream.XMLStreamException 
    */    
    private Field newField() throws XMLStreamException {
        Field f = new Field();
        Map<Character,String> subs = f.getSubs();
        while(nextChild("s")) {
            char ch = r.getAttributeCount()==0 ? 0 : r.getAttributeValue(0).charAt(0);
            String s = r.getElementText();
            subs.put(ch,s);
        }
        return f;
    }
/**
    reads all f to new tags
    @return
    @throws XMLStreamException 
    */    
    private Map<Integer,List<Field>> newTags() throws XMLStreamException {
        Map<Integer,List<Field>> tags = new TreeMap<>();
        while(nextChild("f")) {
            int tag = Integer.parseInt(r.getAttributeValue(0));
            Field.tagsAdd(tags, tag, newField());
        }
        return tags;
    }
    
/**
    reads all f-elements
    @return fss
    @throws XMLStreamException 
    */    
    public List<List<String>> readfss() throws XMLStreamException {
        List<List<String>> fss = new ArrayList<>();
        while(nextChild("f")) {
            fss.add(readfs(r.getAttributeValue("","f")));
        }
        return fss;
    }
/**
    reads all s-elements
     * @return fs
     * @throws javax.xml.stream.XMLStreamException 
    */    
    private List<String> readfs(String tag3) throws XMLStreamException {
        List<String> fs = new ArrayList<>();
        fs.add(tag3);
        if(!nextChild("s")) {
            fs.add("");
            return fs;
        }
        if(r.getAttributeCount()>0) {
            fs.add("");
            fs.add(r.getAttributeValue(0).substring(0,1) + r.getElementText());
        } else {
            fs.add(r.getElementText());
        }
        while(nextChild("s")) {
            fs.add(r.getAttributeValue(0).substring(0,1) + r.getElementText());
        }
        return fs;
    }
    
    public Map<Integer,List<Field>> read() throws XMLStreamException {
        if(nextChild("m")) return newTags();
        return null;
    }
}
