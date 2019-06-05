/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package marc;

import java.io.*;

/**
 *
 * @author marina
 */
public class ExtensionFilter implements FilenameFilter{
    private String ext;
    public ExtensionFilter(String ext) {this.ext = ext;}
    public boolean accept(File dir, String name) {
        int dotoffset = name.length() - ext.length() - 1;
        if(dotoffset < 0) return false;
        if(name.charAt(dotoffset) != '.') return false;
        return name.substring(dotoffset+1).equalsIgnoreCase(ext);
    }

}
