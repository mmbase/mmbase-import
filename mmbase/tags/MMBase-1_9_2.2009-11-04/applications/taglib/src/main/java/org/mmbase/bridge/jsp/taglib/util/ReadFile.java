package org.mmbase.bridge.jsp.taglib.util;
import java.io.*;
import org.w3c.dom.*;
import org.mmbase.util.Casting;

/**
 * This file is found in the public domain, on http://www.dpawson.co.uk/xsl/sect2/N4760.html
 * It is used to be able to include the codesamples in the taglib documentation.
 * XSLT does not provide a way to easily include textfiles into your final document,
 * so unfortunately we need this class to be able to do that.
 * @author Johannes Verelst 
 */
public class ReadFile {
    public static Node readExample(String actualFileName) {
        return Casting.toXML("<example><![CDATA[" + contents(actualFileName) + "]]></example>").getDocumentElement();
    }

    public static String contents(String actualFileName) {
        FileReader in = null;
        try {
            in = new FileReader(actualFileName);
        } catch (FileNotFoundException e) {
            return "File '" + actualFileName + "' not found.\n";
        }
        StringWriter out = new StringWriter();

        char[] buffer = new char[4096];
        int numchars;
        try {
            while((numchars = in.read(buffer)) != -1) {
                out.write(buffer, 0, numchars);
            }
            out.close();
        } catch (IOException e) {
            return "IO Error reading file '" + actualFileName + "'.\n";
        }
        String returnContents = out.toString();
        return returnContents.trim();
    }
}
