/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package marc;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marina
 */
public class WSS {
    private List<String> pathList = new ArrayList<String>();
    private Map<Integer,Map<Character,String[]>> tagList =
            new TreeMap<Integer,Map<Character,String[]>>();

    static public void importDir(String dirName) {
        File dir = new File("c:\\irbis64\\datai\\"+dirName);
        FilenameFilter ff = new ExtensionFilter("ws");
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(dirName + "_dir.txt", "rw");
            try {
                raf.write("ws\twss\tf0\tf1\tf2\tf3\tf4\tf5\tf6\tf7\tf8\tf9\n".getBytes("windows-1251"));
                for (File file:dir.listFiles(ff))
                    importWs(file, raf);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(WSS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(WSS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(WSS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                raf.setLength(raf.getFilePointer());
                raf.close();
            } catch (IOException ex) {
                Logger.getLogger(WSS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WSS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public void importWs(File file, RandomAccessFile raf)
            throws UnsupportedEncodingException, FileNotFoundException, IOException {
        System.out.println(file.getName());
        FilenameFilter ff = new ExtensionFilter("wss");
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "cp1251"));
        try {
            int pcount = Integer.valueOf(reader.readLine());
            int i;
            String[] pages = new String[pcount];
            for (i=0; i<pcount; ++i)
                pages[i] = reader.readLine();
            int[] counts = new int[pcount];
            for (i=0; i<pcount; ++i)
                counts[i] = Integer.valueOf(reader.readLine());
            for (i=0; i<pcount; ++i){
                String s = file.getName() + "\t" + pages[i];
                for(int i1=0; i1<counts[i]; ++i1) {
                    String s5 = null;
                    String sline = s;
                    for(int i2=0; i2<10; ++i2) {
                        String s2 = reader.readLine();
                        sline += "\t" + s2;
                        if(i2==5) s5 = s2;
                    }
                    raf.write(sline.concat("\n").getBytes("windows-1251"));
                    if(s5!=null && ff.accept(null, s5))
                        importWss(file.getName(),file.getParentFile(),s5,raf);
                }
            }
        } finally {
            reader.close();
        }
    }
    static public void importWss(String ws, File dir, String fname, RandomAccessFile raf)
            throws UnsupportedEncodingException, FileNotFoundException, IOException {
        System.out.println(" "+fname);
        try{
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(dir.getPath()+"\\"+fname), "cp1251"));
        try {
            int pcount = Integer.valueOf(reader.readLine());
            String s = ws + "\t" + fname;
            for (int i=0; i<pcount; ++i){
                String sline = s + "\t" + reader.readLine().toLowerCase();
                for(int i2=1; i2<10; ++i2)
                    sline += "\t" + reader.readLine();
                raf.write(sline.concat("\n").getBytes("windows-1251"));
            }
        } finally {
            reader.close();
        }
        }catch(Exception e) {System.out.println(fname+" Exception="+e.toString());}
    }

    public void addPath(String path) { pathList.add(path); }

    private String wssFileName(int tag) {
        String s = String.valueOf(tag) + ".wss";
        if (pathList.size()==0) pathList.add("");
        for(String path:pathList) {
            String fname = path + s;
            File file = new File(fname);
            if(file.exists()) return fname;
        }
        return null;
    }

    private void wssRead(String fname, Map<Character,String[]> map)
            throws FileNotFoundException, IOException {
        BufferedReader reader = null;
        try {
            reader= new BufferedReader(new InputStreamReader(
                    new FileInputStream(fname), "cp1251"
                    ));
            int wcount = Integer.valueOf(reader.readLine());
            while ( --wcount >= 0){
                String[] sarray = new String[10];
                for(int i = 0; i<10; ++i)
                    sarray[i] = reader.readLine();
                if (sarray[0]==null) {
                    System.out.print(" ***** неожиданно закончился WSS="+fname);
                    break;
                }
                if(sarray[0].trim().length()>1) {
                    System.out.print(" ****** мусор в WSS="+fname);
                    break;
                }
                char ch = Character.toUpperCase(sarray[0].charAt(0));
//                map.put(ch=='*'?'\u0000':ch, sarray);
            }
        } finally {
            if (reader != null) reader.close();
        }

    }

    private Map<Character,String[]> getWs(String fname)
            throws FileNotFoundException, IOException {
        Map<Character,String[]> map = new TreeMap<Character,String[]>();
        BufferedReader reader = null;
        try {
            reader= new BufferedReader(new InputStreamReader(
                    new FileInputStream(fname), "cp1251"
                    ));
            int wcount = Integer.valueOf(reader.readLine());
            int[] wcounts = new int[wcount];
            for(int i=0; i<wcount; ++i){
                
            }
            while ( --wcount >= 0){
                String[] sarray = new String[10];
                for(int i = 0; i<10; ++i)
                    sarray[i] = reader.readLine();
                if (sarray[0]==null) {
                    System.out.print(" ***** неожиданно закончился WSS="+fname);
                    break;
                }
                if(sarray[0].trim().length()>1) {
                    System.out.print(" ****** мусор в WSS="+fname);
                    break;
                }
                char ch = Character.toUpperCase(sarray[0].charAt(0));
//                map.put(ch=='*'?'\u0000':ch, sarray);
            }
        } finally {
            if (reader != null) reader.close();
        }
        return map;
    }

    private Map<Character,String[]> getWss(String fname)
            throws FileNotFoundException, IOException {
        Map<Character,String[]> map = new TreeMap<Character,String[]>();
        BufferedReader reader = null;
        try {
            reader= new BufferedReader(new InputStreamReader(
                    new FileInputStream(fname), "cp1251"
                    ));
            int wcount = Integer.valueOf(reader.readLine());
            while ( --wcount >= 0){
                String[] sarray = new String[10];
                for(int i = 0; i<10; ++i)
                    sarray[i] = reader.readLine();
                if (sarray[0]==null) {
                    System.out.print(" ***** неожиданно закончился WSS="+fname);
                    break;
                }
                if(sarray[0].trim().length()>1) {
                    System.out.print(" ****** мусор в WSS="+fname);
                    break;
                }
                char ch = Character.toUpperCase(sarray[0].charAt(0));
//                map.put(ch=='*'?'\u0000':ch, sarray);
            }
        } finally {
            if (reader != null) reader.close();
        }
        return map;
    }

    private Map<Character,String[]> readMap(int tag)
            throws FileNotFoundException, IOException{
        Map<Character,String[]> map = new TreeMap<Character,String[]>();
        tagList.put(tag, map);
        String fname = wssFileName(tag);
        if (fname != null)
            wssRead(fname,map);
        return map;
    }

    public Map<Character,String[]> tagMap(int tag)
            throws FileNotFoundException, IOException {
        Map<Character,String[]> map = tagList.get(tag);
        if (map != null) return map;
        return readMap(tag);
    }

    public Map<Integer,Set<Character>> wssMap(Map<Integer,List<Field>> tags) {
        Map<Integer,Set<Character>> map = new TreeMap<Integer,Set<Character>>();
        for(Map.Entry<Integer,List<Field>> tag: tags.entrySet()){
            Set<Character> set = map.get(tag.getKey());
            if(set==null){
                set = new TreeSet<Character>();
                map.put(tag.getKey(), set);
            }
            for(Field field:tag.getValue())
                set.addAll(field.getSubs().keySet());
        }
        return map;
    }

    public static void wssMapAddAll(Map<Integer,Set<Character>> wssmap
            , Map<Integer,Set<Character>> wssmapAdd) {
        for(Map.Entry<Integer,Set<Character>> e: wssmapAdd.entrySet()){
            Set<Character> set = wssmap.get(e.getKey());
            if (set == null) {
                set = new TreeSet<Character>();
                wssmap.put(e.getKey(), set);
            }
            set.addAll(e.getValue());
        }
    }

    public void printTag(int tag, Set<Character> wssset)
            throws FileNotFoundException, IOException {
        Map<Character,String[]> map = tagMap(tag);
        for(Character ch : wssset) {
            String[] sa = map.get(ch);
            System.out.printf("\n%4d^%1s %s",tag,ch>0?ch:' ',sa==null?"":sa[1]);
        }
    }

    public void print(Map<Integer,Set<Character>> wssmap)
            throws FileNotFoundException, IOException {
        for(Map.Entry<Integer,Set<Character>> e:wssmap.entrySet())
            printTag(e.getKey(),e.getValue());
    }
}
