/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;

/**
 * Transformations related to escaping in XML.
 *
 * @author Michiel Meeuwissen
 */

public class Xml extends AbstractTransformer implements CharTransformer {
    
    private final static int ESCAPE           = 1;     
    private final static int ESCAPE_ATTRIBUTE = 2;
    private final static int ESCAPE_ATTRIBUTE_DOUBLE = 3;
    private final static int ESCAPE_ATTRIBUTE_SINGLE = 4;
    private final static int ESCAPE_ATTRIBUTE_HTML = 5;

    /**
     * Used when registering this class as a possible Transformer
     */

    public HashMap transformers() {
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
     * Attributes of XML tags cannot contain quotes.
     *
     * @author Michiel Meeuwissen
     * @version 2001-09-14
     */
    public static String XMLAttributeEscape(String att, char quot) {
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

    	    } else {
    	    	sb.append(c);
    	    }
	}
	return sb.toString();
    }
    public static String XMLAttributeEscape(String att) {
        StringBuffer sb = new StringBuffer();
	char[] data = att.toCharArray();
	char c;
	for (int i =0 ; i < data.length; i++){
	    c = data[i];
            if (c == '"') {
                sb.append("&quot;");
            } else if (c == '\'')  {
                sb.append("&apos;");
            } else {
    	    	sb.append(c);
    	    }
	}
	return sb.toString();        
    }
   
    /**
     * Utility class for escaping and unescaping
     * (XML)data 
     * @author Kees Jongenburger
     * @version 23-01-2001
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
    	StringBuffer sb = new StringBuffer();
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
	return sb.toString();
    }

    private static String removeNewlines(String incoming) {
    	String ret = incoming.replace('\n', ' ');
    	return ret.replace('\r', ' ');	
    }

    /**
     * Utility class for escaping and unescaping
     * (XML)data 
     * @author Kees Jongenburger
     * @version 23-01-2001
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
//		System.out.println(entity);
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

    public Writer transform(Reader r) {
        throw new UnsupportedOperationException("transform(Reader) is not yet supported");
    }
    public Writer transformBack(Reader r) {
        throw new UnsupportedOperationException("transformBack(Reader) is not yet supported");
    } 

    public String transform(String r) {
        switch(to){
        case ESCAPE:           return XMLEscape(r);
        case ESCAPE_ATTRIBUTE: return XMLAttributeEscape(r);
        case ESCAPE_ATTRIBUTE_DOUBLE: return XMLAttributeEscape(r, '"');
        case ESCAPE_ATTRIBUTE_SINGLE: return XMLAttributeEscape(r, '\'');
        case ESCAPE_ATTRIBUTE_HTML: return removeNewlines(XMLAttributeEscape(r));
        default: throw new UnsupportedOperationException("Cannot transform");
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
        default: throw new UnsupportedOperationException("Cannot transform");
        }
    } 

}
