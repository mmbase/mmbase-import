/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Class to convert from/to a string from/to a encoded string.
 * @author Eduard Witteveen
 * @author Michiel Meeuwissen
 *
 *  Supported encodings are at this moment:
 *  <UL>
 *  <LI>BASE64</LI>
 *  <LI>ESCAPE_XML</LI>
 *  <LI>ESCAPE_HTML</LI>
 *  <LI>ESCAPE_HTML_ATTRIBUTE</LI>
 *  <LI>ESCAPE_WML</LI>
 *  <LI>ESCAPE_WML_ATTRIBUTE</LI>
 *  <LI>ESCAPE_URL</LI>
 *  <LI>ESCAPE_URL_PARAM</LI>
 *  <LI>ESCAPE_SINGLE_QUOTE</LI> 
 *  </UL>
 *  Usage:
 *  <pre>
 *  Encode encoder = new Encode("ESCAPE_XML");
 *  System.out.println(  encoder.decode( encoder.encode("& \" < >") )  );
 *  </pre>
 **/
public class Encode {  
    private EncodingType encoding;

    /**
     *	Created a encode instance of a certain type of encoding
     *	@param	encoding a string that describes which encoding should be used.
     **/
    public Encode(String encoding) {
    	this.encoding = EncodingType.getEncoding(encoding);
    }

    /**
     *	This function will encode a given string to it's encoded variant
     *	@param	encoding    a string that describes which encoding should be used.
     *	@param	toEncode    a string which is the value which should be encoded.
     *	@return     	    a string which is the encoded representation of toEncode 
     *	    	    	    with the given encoding
     **/
    public static String encode(String encoding, String toEncode) {
        return encode(EncodingType.getEncoding(encoding), toEncode);     	
    }

    /**
     *	This function will decode a given string to it's decoded variant
     *	@param	decoding    a string that describes which decoding should be used.
     *	@param	toDecode    a string which is the value which should be encoded.
     *	@return     	    a string which is the encoded representation of toEncode 
     *	    	    	    with the given encoding
     **/
    public static String decode(String encoding, String toDecode) {
        return decode(EncodingType.getEncoding(encoding), toDecode); 
    }

    /**
     *	This function will encode a given string to it's encoded variant
     *	@param	toEncode    a string which is the value which should be encoded.
     *	@return     	    a string which is the encoded representation of toEncode 
     *	    	    	    with the given encoding
     **/
    public String encode(String toEncode) {
    	return encode(encoding, toEncode);
    }

    /**
     *	This function will decode a given string to it's decoded variant
     *	@param	toDecode    a string which is the value which should be encoded.
     *	@return     	    a string which is the encoded representation of toEncode 
     *	    	    	    with the given encoding
     **/
    public String decode(String toDecode) {
    	return decode(encoding, toDecode); 
    }
        
    private static String encode(EncodingType encoding, String toEncode) {
    	switch ( encoding.value() ) {
        case EncodingType.BASE_64_INT :
            BASE64Encoder enc = new BASE64Encoder();
            return enc.encodeBuffer(toEncode.getBytes()); 
            
        case EncodingType.ESCAPE_XML_INT :
            return XMLEscape(toEncode);
            
        case EncodingType.ESCAPE_HTML_INT	 :
        case EncodingType.ESCAPE_WML_INT	 :	    
            return XMLEscape(toEncode);
            
        case EncodingType.ESCAPE_HTML_ATTRIBUTE_INT :
        case EncodingType.ESCAPE_WML_ATTRIBUTE_INT :	    
            return removeNewlines(XMLEscape(toEncode));
            
        case EncodingType.ESCAPE_URL_INT :
            return URLEscape.escapeurl(toEncode);
            
        case EncodingType.ESCAPE_URL_PARAM_INT :
            return URLParamEscape.escapeurl(toEncode);	    
            
        case EncodingType.ESCAPE_SINGLE_QUOTE_INT :
            return Escape.singlequote(toEncode);
                
        case EncodingType.RICH_TEXT_INT :
	    	return richToXML(toEncode);
		
        default:
            throw new IllegalStateException("EncodingType not found, encoding was : " + encoding + "(" +encoding.value()+ ")");
	}
    }

    private static String decode(EncodingType encoding, String toDecode) {
    	try {
            switch ( encoding.value() ) {
            case EncodingType.BASE_64_INT :
                BASE64Decoder dec = new BASE64Decoder();
                return new String(dec.decodeBuffer(toDecode)); 
		
            case EncodingType.ESCAPE_XML_INT :
                return XMLUnescape(toDecode);
		
            case EncodingType.ESCAPE_HTML_INT :
                return XMLUnescape(toDecode);
                
            case EncodingType.ESCAPE_HTML_ATTRIBUTE_INT :
                return XMLUnescape(toDecode);
                
            case EncodingType.ESCAPE_URL_INT :
                return URLEscape.unescapeurl(toDecode);
		
            case EncodingType.ESCAPE_URL_PARAM_INT :
                return URLParamEscape.unescapeurl(toDecode);
		
            case EncodingType.ESCAPE_SINGLE_QUOTE_INT :
                throw new IllegalArgumentException("not needed to revert this at anytime it tinks");
		
            case EncodingType.RICH_TEXT_INT :
	    	return XMLToRich(toDecode);
                
            default:
                throw new IllegalStateException("EncodingType not found, encoding was : " + encoding + "(" +encoding.value()+ ")");
	    }
	}    
	catch(Exception e) {
	    throw new IllegalArgumentException("the entered string to decode properly was wrong: " + e);
	}
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
		System.out.println(entity);
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

    /**
     * Defines a kind of 'rich' text format. This is a way to easily
     * type structured text in XML.  The XML tags which can be
     * produced by this are all HTML as well.
     *
     * This is a generalisation of the MMBase html() functions which
     * do similar duties, but hopefully this one is better, and more
     * powerfull too.
     *
     * The following things are recognized:
     * <ul>
     *  <li> Firstly, XMLEscape is called.</li>
     *  <li> A line starting with an asterix (*) will start an unnumberd
     *       list. The first new line not starting with a space or an other
     *       asterix will end the list </li>
     *  <li> Underscores are translated to the emphasize HTML-tag</li> 
     *  <li> You can create a header tag by by starting a line with a dollar sign + number</li> 
     *  <li> A paragraph can be begun (and ended) with an empty line.</li> 
     * </ul>
     *
     * Test with commandline: java org.mmbase.util.Encode RICH_TEXT (reads from STDIN)
     *
     * @param data text to convert
     * @return the converted text
     *
     * @author Michiel Meeuwissen */

    public static String richToXML(String data) {
        String data2 = XMLEscape(data);      
        StringObject obj = new StringObject(data2);
        obj.replace("\r","");      // drop newlines

        // handle lists
        // make <ul> possible (not yet nested), with *-'s on the first char of line.
        int inList = 0; // if we want nesting possible, then an integer (rather then boolean) will be handy
        int pos = obj.indexOf("\n*", 0); // search the first
        while (pos != -1) { 
            if (inList == 0) { // not yet in list
                inList++;        // now we are
                obj.delete(pos, 2); // delete \n*
                obj.insert(pos, "\r<ul>\r<li>"); // insert 10 chars.
                pos += 10;        
            } else {             // already in list
                if (obj.charAt(pos + 1) != '*') { // end of list
                    obj.delete(pos, 1); // delete \n
                    obj.insert(pos, "</li>\r</ul>\n");
                    pos += 12;
                    inList--;
                } else {                      // not yet end
                    obj.delete(pos, 2); // delete \n*
                    obj.insert(pos, "</li>\r<li>");
                    pos += 10;
                }
            }
            if (inList > 0) { // search for new line
                pos = obj.indexOf("\n", pos);
                if (pos + 1 == obj.length()) { obj.delete(pos, 1); obj.insert(pos, "\r</li>"); break; }
                while (obj.charAt(pos + 1) == ' ') { // if next line starts with space, this new line does not count. This makes it possible to have some formatting in a <li>
                    pos = obj.indexOf("\n", pos + 1);
                }
                while (obj.charAt(pos + 1) == '\n') {
                    pos++; // can also be more than one new line             
                }                    
                
            } else {             // search for next list
                pos = obj.indexOf("\n*", pos);
            }
        }
        // make sure that the list is closed:
        while (inList > 0) {
            obj.insert(obj.length(), "</ul>\r"); inList--;
        }            
        
        // to do _-escaping
        obj.replace("__", "&123;"); // search _ entity...
        
        // Emphasizing. This is perhaps also asking for trouble, because
        // people will try to use it like <font> or other evil
        // things. But basicly emphasizion is content, isn't it?
        boolean emph = false;
        pos = obj.indexOf("_", 0);
        while (pos != -1) {
            obj.delete(pos, 1);
            if (! emph) {
                obj.insert(pos, "<em>");
                pos += 3;
                emph = true;
            } else {
                obj.insert(pos, "</em>");
                pos += 4;
                emph = false;
            }
            pos = obj.indexOf("_", pos);
        }
        
        if (emph) {
            obj.insert(obj.length(), "</em>\r");
        }
       
        obj.replace("&123;", "_");
        
        // handle headers
        boolean inHeader = false;
        char    level = '3';
        pos = obj.indexOf("\n$", 0);
        while (pos != -1) {
            obj.delete(pos, 2); // remove \n$
            obj.insert(pos, "\r\r<h"); // use \n\n because paragraph must beforehand.
            pos += 4;
            level = obj.charAt(pos);
            pos += 1;
            obj.insert(pos, ">");
            pos+=1;
            // search end of header;
            pos = obj.indexOf("\n", pos);
            if (pos == -1) break ; // not found.
            obj.insert(pos, "</h" + level + ">\r");
            pos += 6;
            pos = obj.indexOf("\n$", 0);                                  
        }
       
        // TODO:
        // aslkdjf aslkdjf asdlf
        // 
        // $ajslkdjfasld kfjaslkdf
        // asfdlkjasd 
        // goes wrong
       
       
        // handle paragraphs:
        boolean inParagraph = false;
        pos = obj.indexOf("\n\n", 0); // one or more empty lines.
        while (pos != -1) {
            while (obj.length() > pos && obj.charAt(pos) == '\n') obj.delete(pos, 1);
            if (pos == obj.length()) break;
            // if 
            obj.insert(pos, "\r<p>");
            if (inParagraph) {
                obj.insert(pos, "</p>\r");                
                pos +=5;
            }
            pos += 3;
            inParagraph = true;
            pos = obj.indexOf("\n\n", pos);
        }
        if (inParagraph) {
            // read whole text, but stil in paragraph
            // if text ends with newline, take it away, because it then means </p> rather then <br />
            if (obj.charAt(obj.length() -1) == '\n') obj.delete(obj.length()-1, 1);
            obj.insert(obj.length(), "</p>\r");
        }

        // handle newlines:
        obj.replace("\n", "<br />\r");

        // we used \r for non significant newlines:
        obj.replace("\r", "\n");
        
        return obj.toString();
    }

    /**
     * Inverse of richToXML (untested).
     */
    public static String XMLToRich(String data) {
        StringObject obj = new StringObject(data);
        //obj.replace("\n", " ");
        obj.replace("<br />", "\n");
        obj.replace("<p>", "\n\n");
        obj.replace("</p>", "\n");
        obj.replace("<ul>", "\n");
        obj.replace("</ul>", "\n");
        obj.replace("</li>", "");
        obj.replace("<li>", "\n*");
        obj.replace("<em>", "_");
        obj.replace("</em>", "_");
        obj.replace("<h1>", "\n$1");
        obj.replace("<h2>", "\n$2");
        obj.replace("<h3>", "\n$3");
        obj.replace("</h1>", "\n");
        obj.replace("</h2>", "\n");
        obj.replace("</h3>", "\n");        
        return XMLUnescape(obj.toString());
    }

    /**
     * Invocation of the class from the commandline for testing.
     *
     * @author Michiel Meeuwissen
     */
    public static void main(String[] argv) {        
        if (argv.length == 0) { // supply help
            System.out.println("org.mmbase.util.Encode main is for testing purposes only\n");
            System.out.println("   use: java org.mmbase.util.Encode [encode|decode] <coding> [string]\n\n");
            System.out.println("On default it encodes and gets the string from STDIN\n\n"); 
        } else {
            String coding = null;
            boolean decode = false;
            String string;

            {   // read arguments.
                int next = 0;
                if ("decode".equals(argv[next])) {
                    decode = true;
                    next++;
                } else if ("encode".equals(argv[next])) {
                    next++;
                }             
                coding = argv[next++];
                EncodingType.getEncoding(coding); // throws exception if not exist.

                boolean stdin = argv.length <= next;
            
                if (stdin) { //  put STDIN in the string.
                    string = "";
                    try {
                        java.io.BufferedReader stdinReader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
                        String line = stdinReader.readLine();
                        while (line != null) {
                            string += line + "\n";
                            line = stdinReader.readLine();
                        }
                        System.out.println("----------------");
                    } catch (java.io.IOException e) {
                        System.err.println(e.toString());
                    }                
                } else {
                    string = argv[next++];
                }
            }
            // do the job:
            if (decode) {              
                System.out.println(decode(coding, string));
            } else {
                System.out.println(encode(coding, string));
            }                                
        }
    }
}

// not private cause there is a: 
// *** Error: An instance of "org/mmbase/util/Encode.this" is not accessible here because it would have to cross a static region in the intervening type "org/mmbase/util/Encode$EncodingType".
// errror
// container class for our type of encoding...
class EncodingType {
    public final static int BASE_64_INT = 100;
    public final static int ESCAPE_XML_INT = 102;        
    public final static int ESCAPE_HTML_INT = 103;        
    public final static int ESCAPE_HTML_ATTRIBUTE_INT = 104;        		
    public final static int ESCAPE_WML_INT = 105;        
    public final static int ESCAPE_WML_ATTRIBUTE_INT = 106;        			
    public final static int ESCAPE_URL_INT = 107;	
    public final static int ESCAPE_URL_PARAM_INT = 108;    
    public final static int ESCAPE_SINGLE_QUOTE_INT = 109; 
    public final static int RICH_TEXT_INT = 110; 
	
    public final static EncodingType BASE64 = new EncodingType("BASE64", BASE_64_INT);
    public final static EncodingType ESCAPE_XML = new EncodingType("ESCAPE_XML", ESCAPE_XML_INT);
    public final static EncodingType ESCAPE_HTML = new EncodingType("ESCAPE_HTML", ESCAPE_HTML_INT);    	
    public final static EncodingType ESCAPE_HTML_ATTRIBUTE = new EncodingType("ESCAPE_HTML_ATTRIBUTE", ESCAPE_HTML_ATTRIBUTE_INT);    		
    public final static EncodingType ESCAPE_WML = new EncodingType("ESCAPE_WML", ESCAPE_WML_INT);    	
    public final static EncodingType ESCAPE_WML_ATTRIBUTE = new EncodingType("ESCAPE_WML_ATTRIBUTE", ESCAPE_WML_ATTRIBUTE_INT);    		
    public final static EncodingType ESCAPE_URL = new EncodingType("ESCAPE_URL", ESCAPE_URL_INT);
    public final static EncodingType ESCAPE_URL_PARAM = new EncodingType("ESCAPE_URL_PARAM", ESCAPE_URL_PARAM_INT);        
    public final static EncodingType ESCAPE_SINGLE_QUOTE = new EncodingType("ESCAPE_SINGLE_QUOTE", ESCAPE_SINGLE_QUOTE_INT);	
    public final static EncodingType RICH_TEXT = new EncodingType("RICH_TEXT", RICH_TEXT_INT);	
    
    public  boolean equals(String other) { return name.equals(other); }
    public  String toString() { return name; }
    public  int value() { return value; }    
    public static EncodingType getEncoding(String encoding) {
        // try all encodings we know....	
        // we know that encoding never == null so do functions on encoding..
        if( BASE64.equals(encoding) ) return BASE64;
        if( ESCAPE_XML.equals(encoding) ) return ESCAPE_XML;	    
        if( ESCAPE_HTML.equals(encoding) ) return ESCAPE_HTML;	        	    
        if( ESCAPE_HTML_ATTRIBUTE.equals(encoding) ) return ESCAPE_HTML_ATTRIBUTE;
        if( ESCAPE_WML.equals(encoding) ) return ESCAPE_WML;	        	    
        if( ESCAPE_WML_ATTRIBUTE.equals(encoding) ) return ESCAPE_WML_ATTRIBUTE;
        if( ESCAPE_URL.equals(encoding) ) return ESCAPE_URL;	
        if( ESCAPE_URL_PARAM.equals(encoding) ) return ESCAPE_URL_PARAM;		
        if( ESCAPE_SINGLE_QUOTE.equals(encoding) ) return ESCAPE_SINGLE_QUOTE;		
        if( RICH_TEXT.equals(encoding) ) return RICH_TEXT;	     
        throw new IllegalArgumentException("encoding: '" +encoding+" unknown");
    }
	
    private EncodingType(String name, int value) { this.name = name; this.value = value;}	
    private int value;
    private String name;
}
