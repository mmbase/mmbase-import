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

    /**
     * Used when registering this class as a possible Transformer
     */

    public HashMap transformers() {
        HashMap h = new HashMap();
        h.put("escape_xml".toUpperCase(),  new Config(Xml.class, ESCAPE));
        h.put("escape_html".toUpperCase(), new Config(Xml.class, ESCAPE));
        h.put("escape_wml".toUpperCase(),  new Config(Xml.class, ESCAPE));
        h.put("escape_xml_attribute".toUpperCase(), new Config(Xml.class, ESCAPE_ATTRIBUTE, "Escaping in attributes only involves quotes. This is for double quotes."));
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
        return XMLAttributeEscape(att, '"');
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
        default: return null;
        }
    }
    public String transformBack(String r) {
        switch(to){
        case ESCAPE:           return XMLUnescape(r);
        case ESCAPE_ATTRIBUTE: return r;
        default: return null;
        }
    } 

}
