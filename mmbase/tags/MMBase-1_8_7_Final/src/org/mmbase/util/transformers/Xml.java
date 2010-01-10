/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.HashMap;
import java.util.Map;

/**
 * Transformations related to escaping in XML.
 * @author Michiel Meeuwissen
 * @author Kees Jongenburger
 * @version $Id$
 */

public class Xml extends ConfigurableStringTransformer implements CharTransformer {

    public final static int ESCAPE           = 1;
    public final static int ESCAPE_ATTRIBUTE = 2;
    public final static int ESCAPE_ATTRIBUTE_DOUBLE = 3;
    public final static int ESCAPE_ATTRIBUTE_SINGLE = 4;
    public final static int ESCAPE_ATTRIBUTE_HTML = 5;



    public Xml() {
        super();
    }
    public Xml(int c) {
        super(c);
    }

    //public final static int BODYTAG = 20;

    /**
     * Used when registering this class as a possible Transformer
     */

    public Map transformers() {
        HashMap h = new HashMap();
        h.put("escape_xml".toUpperCase(),  new Config(Xml.class, ESCAPE, "Escapes >, < & and \""));
        h.put("escape_html".toUpperCase(), new Config(Xml.class, ESCAPE, "Like ESCAPE_XML now."));
        h.put("escape_wml".toUpperCase(),  new Config(Xml.class, ESCAPE, "Like ESCAPE_XML now."));
        h.put("escape_xml_attribute".toUpperCase(), new Config(Xml.class, ESCAPE_ATTRIBUTE, "Escaping in attributes only involves quotes. This simply escapes both types (which is little too much)."));
        h.put("escape_xml_attribute_double".toUpperCase(), new Config(Xml.class, ESCAPE_ATTRIBUTE_DOUBLE, "Escaping in attributes only involves quotes. This is for double quotes."));
        h.put("escape_xml_attribute_single".toUpperCase(), new Config(Xml.class, ESCAPE_ATTRIBUTE_SINGLE, "Escaping in attributes only involves quotes. This is for single quotes."));
        h.put("escape_html_attribute".toUpperCase(), new Config(Xml.class, ESCAPE_ATTRIBUTE_HTML, "This escapes all quotes, and also newlines. Handly in some html tags."));
        return h;
    }



    /**
     * Attributes of XML tags cannot contain quotes, and also &amp; must be escaped
     * @param att String representing the attribute
     * @param quot Which quote (either ' or ")
     */
    public static String XMLAttributeEscape(String att, char quot) {
        if (att == null) return "";
        StringBuffer sb = new StringBuffer();
        char[] data = att.toCharArray();
        char c;
        for (int i =0 ; i < data.length; i++){
            c = data[i];
            if (c == quot){
                if (quot == '"') {
                    sb.append("&quot;");
                } else {
                    sb.append("&apos;");
                }

            } else if (c == '&') {
                sb.append("&amp;");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    /**
     * Attributes of XML tags cannot contain quotes, and also &amp; must be escaped
     * @param att String representing the attribute
     */
    public static String XMLAttributeEscape(String att) {
        if (att == null) return "";
        StringBuffer sb = new StringBuffer();
        char[] data = att.toCharArray();
        char c;
        for (int i =0 ; i < data.length; i++){
            c = data[i];
            if (c == '"') {
                sb.append("&quot;");
            } else if (c == '\'')  {
                sb.append("&apos;");
            } else if (c == '&') {
                sb.append("&amp;");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Utility class for escaping and unescaping
     * (XML)data
     * @param xml the xml to encode
     * @return the encoded xml data
     * <UL>
     * <LI>& is replaced by &amp;amp;</LI>
     * <LI>" is replaced by &amp;quot;</LI>
     * <LI>&lt; is replaced by &amp;lt;</LI>
     * <LI>&gt; is replaced by &amp;gt;</LI>
     * </UL>
     **/
    public static String XMLEscape(String xml){
        if (xml == null) return "";
        StringBuffer sb = new StringBuffer();
        XMLEscape(xml, sb);
        return sb.toString();
    }

    /**
     * @since MMBase-1.8
     */
    public static void XMLEscape(String xml, StringBuffer sb) {
        char[] data = xml.toCharArray();
        char c;
        for (int i =0 ; i < data.length; i++){
            c = data[i];
            if (c =='&'){
                sb.append("&amp;");
            }
            else if (c =='<'){
                sb.append("&lt;");
            }
            else if (c =='>'){
                sb.append("&gt;");
            }
            else if (c =='"'){
                sb.append("&quot;");
            }
            else {
                sb.append(c);
            }
        }
    }

    private static String removeNewlines(String incoming) {
        String ret = incoming.replace('\n', ' ');
        return ret.replace('\r', ' ');
    }

    /**
     * Utility class for escaping and unescaping
     * (XML)data
     * @param data the data to decode to (html/xml) where
     * <UL>
     * <LI>& was replaced by &amp;amp;</LI>
     * <LI>" was replaced by &amp;quot;</LI>
     * <LI>&lt; was replaced by &amp;lt;</LI>
     * <LI>&gt; was replaced by &amp;gt;</LI>
     * </UL>
     * @return the decoded xml data
     **/
    public static String XMLUnescape(String data){
        if (data == null) return "";
        StringBuffer sb = new StringBuffer();
        int i;
        for (i =0; i < data.length();i++){
            char c = data.charAt(i);
            if (c == '&'){
                int end = data.indexOf(';',i+1);
                //if we found no amperstand then we are done
                if (end == -1){
                    sb.append(c);
                    continue;
                }
                String entity = data.substring(i+1,end);
                i+= entity.length()  + 1;
                if (entity.equals("amp")){
                    sb.append('&');
                }
                else if (entity.equals("lt")){
                    sb.append('<');
                }
                else if (entity.equals("gt")){
                    sb.append('>');
                }
                else if (entity.equals("quot")){
                    sb.append('"');
                }
                else if (entity.equals("apos")){
                    sb.append('\'');
                }
                else {
                    sb.append("&" + entity + ";");
                }
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    public String transform(String r) {
        switch(to){
        case ESCAPE:           return XMLEscape(r);
        case ESCAPE_ATTRIBUTE: return XMLAttributeEscape(r);
        case ESCAPE_ATTRIBUTE_DOUBLE: return XMLAttributeEscape(r, '"');
        case ESCAPE_ATTRIBUTE_SINGLE: return XMLAttributeEscape(r, '\'');
        case ESCAPE_ATTRIBUTE_HTML: return removeNewlines(XMLAttributeEscape(r));
        default: throw new UnknownCodingException(getClass(), "transform", to);
        }
    }
    public String transformBack(String r) {
        // the attribute unescape will do a little to much, I think.
        switch(to){
        case ESCAPE:
        case ESCAPE_ATTRIBUTE:
        case ESCAPE_ATTRIBUTE_DOUBLE:
        case ESCAPE_ATTRIBUTE_SINGLE: return XMLUnescape(r);
        case ESCAPE_ATTRIBUTE_HTML:
            // we can only try, the removing of newlines cannot be undone.
            return XMLUnescape(r);
        default: throw new UnknownCodingException(getClass(), "transformBack",  to);
        }
    }
    public String getEncoding() {
        switch(to){
        case ESCAPE:                    return "ESCAPE_XML";
        case ESCAPE_ATTRIBUTE:          return "ESCAPE_XML_ATTRIBUTE";
        case ESCAPE_ATTRIBUTE_DOUBLE:   return "ESCAPE_XML_ATTRIBUTE_DOUBLE";
        case ESCAPE_ATTRIBUTE_SINGLE:   return "ESCAPE_XML_ATTRIBUTE_SINGLE";
        case ESCAPE_ATTRIBUTE_HTML:     return "ESCAPE_HTML_ATTRIBUTE";
        default: throw new UnknownCodingException(getClass(), "getEncoding",  to);
        }
    }
}
