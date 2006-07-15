/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.config;

import java.util.*;
import java.io.*;


import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.*;

/**
 * @author Case Roole, cjr@dds.nl
 *
 * $Id: AbstractReport.java,v 1.9 2003-07-07 13:33:25 keesj Exp $
 *
 *
 */
public abstract class AbstractReport implements ReportInterface {
    protected String mode, encoding;
    protected Hashtable specialChars;
    protected String configpath;

   
    //protected String classname = getClass().getName();
    //protected boolean debug = false;

    // --- public methods ------------------------------------------
    public void init(String mode, String encoding) {
        this.mode = mode;
        this.encoding = encoding;
        if (encoding.equalsIgnoreCase("HTML")) {
            specialChars = getHTMLChars();
        } else if (encoding.equalsIgnoreCase("TEXT")) {
            specialChars = getTEXTChars();
        } else {
            specialChars = getTEXTChars();
        }
        configpath = getMMBaseConfigPath();
    }

    public String report() {
        return "";
    }

    // --- protected utility methods --------------------------------
    /**
     * Replace a substring
     *
     * @param s String which is to be modified
     * @param sub Substring to be replaced (once!)
     * @param rep Replacement string
     *
     * @return <code>s</code> with substring <code>sub</code> replaced with <code>rep</code>
     */
    protected String stringReplace(String s, String sub, String rep) {
        String res;
        int n = s.indexOf(sub);
        if (n >= 0) {
            return s.substring(0, n) + rep + s.substring(n + sub.length());
        } else {
            return s;
        }
    }

    /**
     * Retrieve all xml files in a directory
     *
     * @param path Directory path
     * @return String array containing the names of the xml files in the directory, without the extension
     *
     */
    protected Vector listDirectory(String path) throws IOException {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            throw new IOException("Path " + path + " is not a directory.\n");
        } else {
            String[] dirlist = dir.list(new XMLFilenameFilter());
            Vector v = new Vector();
            for (int i = 0; i < dirlist.length; i++) {
                v.addElement(dirlist[i].substring(0, dirlist[i].length() - 4));
            }
            return v;
        }
    }



    /**
     * @return String with '<' and '>' converted to respectively &lt; and &gt;
     * @duplicate Can be found for example in Config module too.
     */
    protected String htmlEntities(String s) {
        StringBuffer res = new StringBuffer();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
                case '>' :
                    res.append("&gt;");
                    break;
                case '<' :
                    res.append("&lt;");
                    break;
                default :
                    res.append(c);
            }
        }
        return res.toString();
    }

    protected String xmlErrorMessage(String path, XMLParseResult pr) {
        String eol = (String)specialChars.get("eol");
        LineNumberReader f = null;
        try {
            f = new LineNumberReader(new FileReader(path));
        } catch (IOException canthappen) {}

        String res = "";
        List fatalList = pr.getFatalList();
        for (int j = 0; j < fatalList.size(); j++) {
            ErrorStruct fatalerror = (ErrorStruct)fatalList.get(j);
            int lineno = fatalerror.getLineNumber();
            int col = fatalerror.getColumnNumber();
            String msg = fatalerror.getMessage();

            if (f != null) {
                try {
                    int i = f.getLineNumber();
                    while (f.ready() && i < lineno - 1) {
                        f.readLine();
                        i++;
                    }
                    String line = f.readLine();
                    if (line != null) {
                        if (encoding.equalsIgnoreCase("html")) {
                            line = htmlEntities(line);
                        }
                        res = res + "*** line " + lineno + ": column " + col + ": " + msg + eol;
                        res = res + "*** " + line + eol;
                    }
                } catch (IOException e) {
                    res = res + "*** IOException reading line " + lineno + " in " + path + ":" + eol + e.getMessage();
                }
            }
        }
        return res;
    }

    // --- private methods -------------------------------------------

    /**
     * @return Hashtable with some special characters represented for HTML
     */
    private Hashtable getHTMLChars() {
        Hashtable h = new Hashtable();
        h.put("amp", "&amp;amp");
        h.put("eol", "<br />\n");
        return h;
    }

    /**
     * @return Hashtable with some special characters represented for TEXT
     */
    private Hashtable getTEXTChars() {
        Hashtable h = new Hashtable();
        h.put("amp", "&amp;");
        h.put("eol", "\n");
        return h;
    }

    private String getMMBaseConfigPath() {
        return MMBaseContext.getConfigPath();
    }
}
