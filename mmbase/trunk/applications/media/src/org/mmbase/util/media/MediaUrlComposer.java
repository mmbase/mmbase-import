/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.builders.media.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.XMLBasicReader;
import org.mmbase.util.FileWatcher;

import java.util.*;
import java.io.File;

import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;

/**
 * The MediaUrlComposer creates additional information in the url. i.e. an url like
 * rpst://streams.omroep.nl/music/jingle.ra, can be expanded to
 * rpst://streams.omroep.nl/music/jungle.ra?start=234&stop=2422&author=Phil%20%collins.
 *
 * This class will contain some basic functionality to create the URI, this class can
 * be extended to create..........................
 *
 * This first attempt will be an implementation for the VPRO, lateron i will make it
 * more generic.
 *
 * @author Rob Vermeulen (VPRO)
 *
 */
public class MediaUrlComposer {
    
    private static Logger log = Logging.getLoggerInstance(MediaSourceFilter.class.getName());
    
    
    /**
     * construct the MediaProviderFilter
     */
    public MediaUrlComposer(MediaSource ms) {
        
    }
    
    /**
     * Replaces all plus characters to procent 20
     * @param s String in which chars will be replaced.
     * @return replaced String
     */
    public static String plusToProcent20(String s) {
        String result = "";
        for(int i=0; i<s.length(); i++) {
            if (s.charAt(i) != '+') {
                result += s.charAt(i);
            } else {
                result += "%20";
            }
        }
        return result;
    }
    
    /**
     * Removes RealPlayer incompatible characters from the string.
     * '#' characters are replaced by space characters.
     * Characters that are allowed are every letter or digit and ' ', '.', '-' and '_' chars.
     * @param s the String that needs to be fixed.
     * @return a realPlayer compatible String.
     */
    public static String makeRealCompatible(String s) {
        if (s != null) {
            char[] sArray = s.replace('#',' ').toCharArray();
            char[] dArray = new char[sArray.length];
            
            int j = 0;
            for (int i=0;i<sArray.length;i++) {
                if (Character.isLetterOrDigit(sArray[i]) ||(sArray[i]==' ')||(sArray[i]=='.')||(sArray[i]=='-')||(sArray[i]=='_')) {
                    dArray[j] = sArray[i];
                    j++;
                }
            }
            //Only use the characters until the first character with value=0. This is from index 0 to j-1.
            return (new String(dArray)).substring(0,j);
        }
        return null;
        
    }
}
