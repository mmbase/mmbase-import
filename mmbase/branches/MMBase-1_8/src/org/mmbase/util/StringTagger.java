/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

/**
 * StringTagger, Creates a object with tags and fields from a String.
 * Its ideal for name-value pairs and name-value pairs with multivalues.
 * It also provides support for quoted values, and recognizes values that are 'function' calls with
 * their own parameter list (allowing to ignore any tokens within these lists when parsing).
 *
 * @application SCAN
 * @code-conventions Some methods (Values, Value etc) have wrong names (and are duplicating Map methods btw)
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id$
 */
public class StringTagger implements Map {

    /**
     * The name-value pairs where the value is a single string
     */
    private Hashtable tokens;
    /**
     * The name-value pairs where the value is a list of strings
     */
    private Map multitokens;
    /**
     * Token used to separate tags (default a space).
     */
    private char tagStart;
    /**
     * Token used to separate the tag name from its value (default '=').
     */
    private char tagSeparator;
    /**
     * Token used to separate multiple values within a tag (default ',').
     */
    private char fieldSeparator;
    /**
     * Token used to indicate quoted values (default '\"').
     */
    private char quote;
    /**
     * Token used to indicate the start of a function parameter list (default '(').
     */
    private char functionOpen;
    /**
     * Token used to indicate the end of a function parameter list (default ')').
     */
    private char functionClose;

    /**
     * The line that was parsed.
     */
    private String startline = "";

    /**
     * Creates a StringTag for the given line.
     * Example : StringTagger("cmd=lookup names='Daniel Ockeloen, Rico Jansen'",' ','=',','\'','('.')')
     * @param line : to be tagged line
     * @param tagStart : Seperator for the Tags
     * @param tagSeparator : Seperator inside the Tag (between name and value)
     * @param fieldSeparator : Seperator inside the value
     * @param quote : Char used if a quoted value
     * @param functionOpen char used to open a function parameter list
     * @param functionClose char used to close a function parameter list
     */
    public StringTagger(String line, char tagStart, char tagSeparator,char fieldSeparator, char quote,
                                     char functionOpen, char functionClose) {
        this.tagStart     = tagStart;
        this.startline    = line;
        this.tagSeparator = tagSeparator;
        this.fieldSeparator = fieldSeparator;
        this.quote = quote;
        this.functionOpen = functionOpen;
        this.functionClose = functionClose;
        tokens      = new Hashtable(); //needing elements(), keys()
        multitokens = new HashMap();
        createTagger(line);
    }

    /**
     * Creates a StringTag for the given line.
     * Uses default characters for the function parameter list tokens.
     * Example : StringTagger("cmd=lookup names='Daniel Ockeloen, Rico Jansen'",' ','=',','\'')
     * @param line : to be tagged line
     * @param tagStart : Seperator for the Tags
     * @param tagSeparator : Seperator inside the Tag (between name and value)
     * @param fieldSeparator : Seperator inside the value
     * @param quote : Char used if a quoted value
     */
    public StringTagger(String line, char tagStart, char tagSeparator,char fieldSeparator, char quote) {
        this(line, tagStart, tagSeparator,fieldSeparator, quote,'(',')');
    }

    /**
     * Creates a StringTag for the given line.
     * Uses default characters for all tokens.
     * @param line : to be tagged line
     */
    public StringTagger(String line) {
        this(line,' ','=',',','"','(',')');
    }

    /**
     * Parses the given line, and stores all value-pairs found in the
     * tokens and multitokens fields.
     * @param line : to be tagged line (why is this a parameter when it can eb retrieved from startline?)
     * @since MMBase-1.7
     */
    protected void createTagger(String line) {
        StringTokenizer tok2 = new StringTokenizer(line+tagStart,""+tagSeparator+tagStart,true);
        String part,tag,prevtok,tok;
        boolean isTag,isPart,isQuoted;

        isTag = true;
        isPart = false;
        isQuoted = false;
        prevtok = "";
        tag = part = ""; // should be StringBuffer
//        log.debug("Tagger -> |"+tagStart+"|"+tagSeparator+"|"+quote+"|");
        while(tok2.hasMoreTokens()) {
            tok = tok2.nextToken();
//            log.debug("tagger tok ("+isTag+","+isPart+","+isQuoted+") |"+tok+"|"+prevtok+"|");
            if (tok.equals(""+tagSeparator)) {
                if (isTag) {
                    tag = prevtok;
                    isTag = false;
                } else {
                    if (!isQuoted) {
                        splitTag(tag+tagSeparator+part);
                        isTag = true;
                        isPart = false;
                        part = "";
                    } else {
                        part += tok;
                    }
                }
            } else if (tok.equals(""+tagStart)) {
                if (isPart) {
                    if (isQuoted) {
                        part += tok;
                    } else {
                        if (!prevtok.equals("" + tagStart)) {
                            splitTag(tag + tagSeparator + part);
                            isTag = true;
                            isPart = false;
                            part = "";
                        }
                    }
                    prevtok = tok;
                }
            } else {
                if (!isTag) isPart = true;
//                log.debug("isTag "+isTag+" "+isPart);
                if (isPart) {
                    if (isQuoted) {
                        // Check end quote
                        if (tok.charAt(tok.length() - 1) == quote) {
                            isQuoted = false;
                        }
                        part += tok;
                    } else {
                        if (tok.charAt(0) == quote && !(tok.charAt(tok.length() - 1) == quote)) {
                            isQuoted = true;
                        }
                        part += tok;
                    }
                }
//                log.debug("isTag "+isTag+" "+isPart+" "+isQuoted);
                prevtok = tok;
            }
        }
    }

    /**
     * Handles and splits a tag in its component parts, and store the elemements in
     * the tokens and multitokens fields.
     * @param tag the string containing the tag
     * @since MMBase-1.7
     */
    protected void splitTag(String tag) {
        int    tagPos = tag.indexOf(tagSeparator);
        String name   = tag.substring(0,tagPos);
        String result = tag.substring(tagPos+1);
//        log.debug("SplitTag |"+name+"|"+result+"|");

        if (result.length()>1 && result.charAt(0) == quote && result.charAt(result.length() - 1) == quote) {
            result = result.substring(1, result.length() - 1);
        }
        tokens.put(name, result);

        StringTokenizer toks = new StringTokenizer(result, "" + fieldSeparator + functionOpen + functionClose, true);
        // If quoted, strip the " " from beginning and end ?
        Vector multi = new Vector();
        if(toks.hasMoreTokens()) {
            String tokvalue="";
            int nesting = 0;
            while (toks.hasMoreTokens()) {
                String tok = toks.nextToken();
                if (tok.equals("" + fieldSeparator)) {
                    if (nesting == 0) {
                        multi.add(tokvalue);
                        tokvalue = "";
                    } else {
                        tokvalue += tok;
                    }
                } else if (tok.equals("" + functionOpen)) {
                    nesting++;
                    tokvalue += tok;
                } else if (tok.equals("" + functionClose)) {
                    nesting--;
                    tokvalue += tok;
                } else {
                    tokvalue += tok;
                }
            }
            multi.add(tokvalue);
        }
        multitokens.put(name, multi);
    }


    // Map interface methods

    /**
     * Clears all data
     */
    public void clear() {
        tokens.clear();
        multitokens.clear();
        startline="";
    }

    /**
     * Checks whether a key exits.
     */
    public boolean containsKey (Object ob) {
        return tokens.containsKey(ob);
    }

    /**
     * Checks whether a value exits.
     */
    public boolean containsValue (Object ob) {
        return tokens.containsValue(ob);
    }

    /**
     *  returns all values
     */
    public Set entrySet() {
        return tokens.entrySet();
    }

    /**
     * Returns whether two objects are the same
     * @param ob the key of the value to retrieve
     */
    public boolean equals(Object ob) {
        return (ob instanceof Map) && (ob.hashCode() == this.hashCode());
    }

    /**
     * Returns the value of a key as an Object.
     * The value returned is a single, unseparated, string.<br />
     * Use {@link #Values} to get a list of multi-values as a <code>Vector</code>.<br />
     * Use {@link #Value} to get the first value as a String
     * @param ob the key of the value to retrieve
     */
    public Object get(Object ob) {
        return tokens.get(ob);
    }

    /**
     *  Hashcode for sorting and comparing
     */
    public int hashCode() {
        return multitokens.hashCode();
    }

    /**
     * Checks whether the tagger is empty
     */
    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    /**
     * Returns a Set of the name keys.
     */
    public Set keySet() {
        return tokens.keySet();
    }

    /**
     *  sets a value (for the Map interface).
     */
    public Object put(Object key, Object value) {
        Object res = tokens.get(key);
        setValue((String)key, (String)value);
        return res;
    }

    /**
     *  Manually sets a set of values (for the Map interface).
     */
    public void putAll(Map map) {
        throw new UnsupportedOperationException();
    }

    /**
     *  remove a value (for the Map interface).
     */
    public Object remove(Object key) {
        Object res = tokens.get(key);
        tokens.remove(key);
        multitokens.remove(key);
        return res;
    }

    /**
     *  sets a value (for the Map interface).
     */
    public int size() {
        return tokens.size();
    }

    /**
     *  returns all values
     */
    public Collection values() {
        return tokens.values();
    }

    // Custom methods

    /**
     * Returns a Enumeration of the name keys.
     */
    public Enumeration keys() {
        return tokens.keys();
    }

    /**
     * toString
     */
    public String toString() {
        StringBuffer content = new StringBuffer("[");
        for (Enumeration e = keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            content.append('<').append(key);
            content.append('=').append(Values(key));
            content.append('>');
        }
        content.append(']');
        return content.toString();
    }

    /**
     * Returns a Enumeration of the values as String.
     * The values returned are all single, unsepartated, strings.
     * Use {@link #multiElements} to get a list of multi-values.
     */
    public Enumeration elements() {
        return tokens.elements();
    }

    /**
     * Returns a Enumeration of the values as Vectors that contain
     * the seperated values.
     * Use {@link #elements} to get a list of single, unseparated, values.
     */
    public Enumeration multiElements(String token) {
        Vector tmp = (Vector) multitokens.get(token);
        if (tmp != null) {
            return tmp.elements();
        } else {
            return null;
        }
    }

    /**
     * Returns the values as a Vector that contains the separated values.
     * <br />
     * Use {@link #get} to get the list of values as a <code>String</code><br />
     * Use {@link #Value} to get the first value as a String
     * @param token the key of the value to retrieve
     */
    public Vector Values(String token) {
        Vector tmp = (Vector) multitokens.get(token);
        return tmp;
    }

    /**
     * Returns the original parsed line
     * @param token unused
     */
    public String ValuesString(String token) {
        return startline;
    }

    /**
     * Returns the first value as a <code>String</code>.
     * In case of a single value, it returns that value. In case of multiple values,
     * it returns the
     * Use {@link #get} to get the list of values as a <code>String</code><br />
     * Use {@link #Values} to get a list of multi-values as a <code>Vector</code>.<br />
     * @param token the key of the value to retrieve
     */
    public String Value(String token) {
        String val;
        Vector tmp=(Vector) multitokens.get(token);
        if (tmp!=null && tmp.size()>0) {
            val=(String) tmp.elementAt(0);
            if (val != null) {
                val = Strip.DoubleQuote(val,Strip.BOTH); // added stripping daniel
                return val;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     *  Manually sets a single value.
     */
    public void setValue(String token,String val) {
        Vector newval = new Vector();
        newval.addElement(val);
        tokens.put(token,newval);
        multitokens.put(token,newval);
    }

    /**
     *  Manually sets a multi-value value.
     */
    public void setValues(String token,Vector values) {
        tokens.put(token,values.toString());
        multitokens.put(token,values);
    }

    /**
     *  For testing
     */
    public static void main(String args[]) {
        StringTagger tag = new StringTagger(args[0]);
    }

}
