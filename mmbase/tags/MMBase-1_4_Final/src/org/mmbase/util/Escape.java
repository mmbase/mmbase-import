/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 * Class for escaping single quotes in a string, so that they can be safely
 * included in a SQL statement.
 */
public class Escape {

    /**
     * Escapes single quotes in a string.
     * Escaping is done by doubling any quotes encountered.
     * Strings that are rendered in such way can more easily be included
     * in a SQL query.
     * @param str the string to escape
     * @return the escaped string
     */
    static public String singlequote(String str) {
        String line=null,obj;
        int idx;
        if (str!=null) {
            /* Single ' protection */
            line=new String("");
            obj=new String(str);
            while((idx=obj.indexOf('\''))!=-1) {
                line+=obj.substring(0,idx)+"''";
                obj=obj.substring(idx+1);
            }
            line=line+obj;
        }
        return line;
    }
}
