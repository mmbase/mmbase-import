/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;



import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.mmbase.util.logging.*;

/**
 * Class for reading and parsing the contents of a CVS (comma value seperated) file.
 *
 * @deprecated not used. maybe move to 'tools' application
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id$
 */
public class CVSReader {

    // logger
    private static Logger log = Logging.getLoggerInstance(CVSReader.class.getName());

    /**
     * The CVS file to read.
     */
    String filename;
    /**
     * The CVS file header, which contains the column names.
     * The header is represented by a <code>Hashtable</code> of name-values
     * where name is a column name and value the index of that column.
     */
    protected Hashtable name2pos;
    /**
     * The content of the CVS file body (the records or rows).
     * Each entry in <code>rows</code> represents a line or record in the CVS body.
     * Each line is represented by a <code>Vector</code> of values. The position of those
     * values matches with teh columns from the header.
     */
    protected Vector rows=new Vector();

    /**
     * Constructor for the CVS Reader.
     * @param filename The CVS file to read
     */
    public CVSReader(String filename) {
        readCVS(filename);
    }

    /**
     * Reads the contents of a CVS file and extracts the header and body content.
     * The body content of the CVS file is stored in the {@link #name2pos} field,
     * the body content in the {@link #rows} field.
     */
    public void readCVS(String filename) {
        String body=loadFile(filename);
        StringTokenizer tok=new StringTokenizer(body,"\n\r");
        if (tok.hasMoreTokens()) name2pos=decodeHeader(tok.nextToken());
        rows=decodeBody(tok);
    }

    /**
     * Parses the body text of a CVS file.
     * Each row (line of text) in the body is a record whose fields are represented by a list of
     * komma-separated, possibly quoted, elements.
     * This routime converted the line into a <code>Vector</code> consisting of these elements.
     * @param tok A tokenenized list of strings (lines) that make up the body text.
     * @return a <code>Vector</code> containing, for each line in the CVS body, a list of elements.
     */
    Vector decodeBody(StringTokenizer mtok) {
        Vector results=new Vector();

        while (mtok.hasMoreTokens()) {
            String line=mtok.nextToken();
            Vector results2=new Vector();
            StringTokenizer tok=new StringTokenizer(line,",\"\n\r",true);
            String prebar=",";
            while (tok.hasMoreTokens()) {
                String bar=tok.nextToken();
                if (bar.equals("\"")) {
                    String part=tok.nextToken();
                    String part2="";
                    while (!part.equals("\"")) {
                        part2+=part;
                        part=tok.nextToken();
                    }
                    results2.addElement(part2);
                } else {
                    if (bar.equals(",")) {
                        if (prebar.equals(",") || !tok.hasMoreTokens()) {
                            results2.addElement("");
                        }
                        if (!tok.hasMoreTokens()) {
                            results2.addElement("");
                        }
                    } else {
                        results2.addElement(bar);
                    }
                }
                prebar=bar;
            }
            results.addElement(results2);
        }
        return results;
    }

    /**
     * Converts a CVS Header line into a hashtable of header elements.
     * @param line the headerline to parse (should exists of elements seperated by commas)
     * @return a <code>Hashtable</code> containing the header values with their
     *         postition in the header
     */
    Hashtable decodeHeader(String line) {
        int i=0;
        Hashtable results=new Hashtable();
        // XXX parsing on /n/r is not needed as a line cannot exist of multiple lines...
        StringTokenizer tok=new StringTokenizer(line,",\n\r");
        while (tok.hasMoreTokens()) {
            String part=tok.nextToken();
            part = Strip.DoubleQuote(part,Strip.BOTH);
            results.put(part,new Integer(i++));
        }
        return results;
    }

    /**
     * Reads the content of a file.
     * @param filename path and name of the file to read
     * @return the content of the file as a string
     */
    public String loadFile(String filename) {
        try {
            File sfile = new File(filename);
            FileInputStream scan =new FileInputStream(sfile);
            int filesize = (int)sfile.length();
            byte[] buffer=new byte[filesize];
            int len=scan.read(buffer,0,filesize);
            if (len!=-1) {
                // XXX: ideally, we should use the preferred encoding,
                // but this class cannot access MMBase
                return new String(buffer);
            }
            scan.close();
        } catch(Exception e) {
            log.error(e);
            log.error(Logging.stackTrace(e));
        }
        return null;
    }

    /**
     * Returns the element at the given row and column.
     * @param row the element row
     * @param col the element column
     * @return the element as a String.
     */
    public String getElement(int row,int col) {
        Vector rw=(Vector)rows.elementAt(row);
        String value=(String)rw.elementAt(col);
        return value;
    }


    /**
     * Returns the element at the given row and with the given column name.
     * @param row the element row
     * @param colname the element columnname
     * @return the element as a String.
     */
    public String getElement(int row,String colname) {
        Integer ii=(Integer)name2pos.get(colname);
        if (ii!=null) {
            int i=ii.intValue();
            Vector rw=(Vector)rows.elementAt(row);
            String value=(String)rw.elementAt(i);
            return value;
        }
        return null;
    }

    /**
     * Returns the number of rows in the CVS body.
     */
    public int size() {
        return rows.size();
    }
}
