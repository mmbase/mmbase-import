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
 *  Class to convert from/to a string from/to a encoded string.
 * @author Eduard Witteveen
 * @version 10-05-2001
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
		
	    default:
	    	throw new IllegalStateException("EncodingType not found, encodeing was : " + encoding + "(" +encoding.value()+ ")");
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
    		    throw new IllegalArgumentException("not needed to revert this at anytime it tinkk");
		
    	    	default:
    		    throw new IllegalStateException("EncodingType not found, encodeing was : " + encoding + "(" +encoding.value()+ ")");
	    }
	}    
	catch(Exception e) {
	    throw new IllegalArgumentException("the entered string to decode propely was wrong: " + e);
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
	
        public final static EncodingType BASE64 = new EncodingType("BASE64", BASE_64_INT);
        public final static EncodingType ESCAPE_XML = new EncodingType("ESCAPE_XML", ESCAPE_XML_INT);
        public final static EncodingType ESCAPE_HTML = new EncodingType("ESCAPE_HTML", ESCAPE_HTML_INT);    	
        public final static EncodingType ESCAPE_HTML_ATTRIBUTE = new EncodingType("ESCAPE_HTML_ATTRIBUTE", ESCAPE_HTML_ATTRIBUTE_INT);    		
        public final static EncodingType ESCAPE_WML = new EncodingType("ESCAPE_WML", ESCAPE_WML_INT);    	
        public final static EncodingType ESCAPE_WML_ATTRIBUTE = new EncodingType("ESCAPE_WML_ATTRIBUTE", ESCAPE_WML_ATTRIBUTE_INT);    		
        public final static EncodingType ESCAPE_URL = new EncodingType("ESCAPE_URL", ESCAPE_URL_INT);
        public final static EncodingType ESCAPE_URL_PARAM = new EncodingType("ESCAPE_URL_PARAM", ESCAPE_URL_PARAM_INT);    
        public final static EncodingType ESCAPE_SINGLE_QUOTE = new EncodingType("ESCAPE_SINGLE_QUOTE", ESCAPE_SINGLE_QUOTE_INT);	
    
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
	    throw new IllegalArgumentException("encoding: '" +encoding+" unknown");
    	}
	
        private EncodingType(String name, int value) { this.name = name; this.value = value;}	
        private int value;
        private String name;
    }
