/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import org.mmbase.util.transformers.*;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
/**
 *
 * Class to convert from/to a string (byte[]) from/to a encoded string (byte[])
 *
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
 *
 *  A list of supported encodings can be gotten by java
 *  org.mmbase.util.Encode, and you add your own encodings by calling
 *  the static function 'register' of this class.
 *
 *
 *  Usage:
 *  <pre>
 *  Encode encoder = new Encode("ESCAPE_XML");
 *  System.out.println(  encoder.decode( encoder.encode("& \" < >") )  );
 *  </pre>
 **/
public class Encode {  

    private Transformer trans; // the instance of the object doing the actual work.

    private  static HashMap encodings;                   // string -> Config, all encoding are registered in this.
    private  static HashSet registered = new HashSet();  // in this is remembered which classes were registered, to avoid registering them more than once.

    static {
        encodings = new HashMap();
        
        // a few Encoding are avaible by default:
        try {
            register("org.mmbase.util.transformers.Base64");
            register("org.mmbase.util.transformers.Xml");
            register("org.mmbase.util.transformers.Url");
            register("org.mmbase.util.transformers.Sql");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println(e.toString());
        }
    }

    /**
     * Created a encode instance of a certain type of encoding. It
     * will instantiate the right class, and configure it. This
     * instantion will be used when you call 'encode' or 'decode'
     * members later.
     *
     * @param	encoding a string that describes which encoding should be used.
     */
    public Encode(String encoding) {
        if (encodings.containsKey(encoding.toUpperCase())) { // it must be known.
            Config e  = (Config)encodings.get(encoding.toUpperCase()); // get the info.
            try {
                trans = (Transformer) e.clazz.newInstance(); 
            } catch (InstantiationException ex) {
                throw new IllegalArgumentException("encoding: '" + encoding + "' could not be instantiated");
            } catch (IllegalAccessException ex) {
            }
            trans.configure(e.config);
        } else {
            throw new IllegalArgumentException("encoding: '" + encoding + "' unknown");
        }
        
    }


    /**
     * Add new transformation types. Feed it with a class name (which
     * must implement Transformer)
     *
     * @param String a class name.
     */

    public static void register(String clazz) {
        if (! registered.contains(clazz)) { // if already registered, do nothing.
            try {
                Class atrans = Class.forName(clazz);
                Class trans  = Class.forName("org.mmbase.util.transformers.Transformer");

                if(trans.isAssignableFrom(atrans)) { // make sure it is of the right type.
                    // Instantiate it, just once, to call the method 'transformers'
                    // In this way we find out what this class can do.
                    Object transformer = atrans.newInstance();
                    java.lang.reflect.Method transformers = atrans.getMethod("transformers", new Class [] {});
                    HashMap newencodings = (HashMap) transformers.invoke(transformer, new Object[] {});
                    encodings.putAll(newencodings); // add them all to our encodings.

                    // TODO, perhaps there should be a check here, to make sure that no two classes use the
                    // same string to identify a transformation.

                } else {
                    throw new IllegalArgumentException("The class " + clazz + " does not implement " + trans.getName()); 
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e.toString());            
            } catch (Exception e) { // yeah, yeah, it can throw a lot more. 
                // TODO perhaps make better distinction between exceptions...
                throw new IllegalArgumentException(e.toString());            
            }
            registered.add(clazz);
        }
    }

    /**
     *	This function will encode a given string to it's encoded
     *	variant. It is static, it will make a temporary instance of
     *	the Transformer class. If you need to encode alot, it is
     *	better to use new Encode first.
     *
     *
     *	@param	encoding    a string that describes which encoding should be used.
     *	@param	toEncode    a string which is the value which should be encoded.
     *                      This can also be a byte[].
     *
     *	@return     	    a string which is the encoded representation of toEncode 
     *	    	    	    with the given encoding
     **/
    public static String encode(String encoding, String toEncode) {
        Encode e = new Encode(encoding);
        return e.encode(toEncode);
    }

    
    public static String encode(String encoding, byte[] bytes) {
        Encode e = new Encode(encoding);
        return e.encode(bytes);
    }
   
    /**
     *	This function will decode a given string to it's decoded variant. 
     *  @see #encode
     *	@param	decoding    a string that describes which decoding should be used.
     *	@param	toDecode    a string which is the value which should be encoded.
     *	@return     	    a string which is the encoded representation of toEncode 
     *	    	    	    with the given encoding
     **/
    
    public static String decode(String encoding, String toDecode) {
        Encode e = new Encode(encoding);
        return e.decode(toDecode); 
    }
    public static byte[] decodeBytes(String encoding, String toDecode) {
        Encode e = new Encode(encoding);
        return e.decodeBytes(toDecode); 
    }
    

    /**
     *	This function will encode a given string to it's encoded variant. 
     *	@param	toEncode    A string which is the value which should be encoded. 
                            If the transformer does transform bytes, then first getBytes is done on the String.
     *
     *	@return     	    a string which is the encoded representation of toEncode 
     *	    	    	    with the given encoding
     **/
    public String encode(String toEncode) {
        if (isByteToCharEncoder()) {
            return ((ByteToCharTransformer)trans).transform(toEncode.getBytes());
        } else {
            return ((CharTransformer)trans).transform(toEncode);
        }
    }
    /**
     * Encodes a byte array. 
     *
     * @return a string;;    
     */
    public String encode(byte[] bytes) {
    	return ((ByteToCharTransformer)trans).transform(bytes);
    }

    /**
     *	This function will decode a given string to it's decoded variant
     *	@param	toDecode    a string which is the value which should be encoded.
     *	@return     	    a string which is the encoded representation of toEncode 
     *	    	    	    with the given encoding
     **/
    public String decode(String toDecode) {
        if (isByteToCharEncoder()) {
            return new String(((ByteToCharTransformer)trans).transformBack(toDecode));
        } else {
            return ((CharTransformer)trans).transformBack(toDecode); 
        }
    }
    public byte[] decodeBytes(String toDecode) {
        if (isByteToCharEncoder()) {
            return ((ByteToCharTransformer)trans).transformBack(toDecode);
        } else {
            return ((CharTransformer)trans).transformBack(toDecode).getBytes(); 
        }
    }
    /**
     * All the currently known encodings.
     *
     * @return Set of Strings.
     */

    public static Set possibleEncodings() {
        return encodings.keySet();
    }
    /**
     * Checks if a certain string represents a known transformation.
     *
     */
    public static boolean isEncoding(String e) {
        return encodings.containsKey(e.toUpperCase());
    }
    /**
     * Checks if the transformation is between two Strings.
     */
    public boolean isCharEncoder() {
        return trans instanceof org.mmbase.util.transformers.CharTransformer;
    }
    /**
     * Checks if the transformations makes from byte[] String.
     */
    public boolean isByteToCharEncoder() {
        return trans instanceof org.mmbase.util.transformers.ByteToCharTransformer;
    }

    /**
     * Invocation of the class from the commandline for testing.
     *
     * @author Michiel Meeuwissen
     */
    public static void  main(String[] argv) {        
        String coding = null;
        boolean decode = false;
        String string = null;

        {   // read arguments.
            int cur = 0;
            while (cur < argv.length) {
                if ("-decode".equals(argv[cur])) {
                    decode = true;
                } else if ("-encode".equals(argv[cur])) {                    
                } else if ("-class".equals(argv[cur])) {
                    register(argv[++cur]);
                } else {
                    if (coding == null) {
                        coding = argv[cur];
                        if (! isEncoding(coding)) {
                            throw new RuntimeException (coding + " is not a  known coding");
                        }
                    } else if (argv[cur].charAt(0) == '-') {
                        throw new RuntimeException ("unknown option " + argv[cur]);
                    } else {
                        if (string == null) string = "";
                        string += " " + argv[cur];
                    }
                }
                cur++;                                            
            }            
        }

        if (coding == null) { // supply help
            System.out.println("org.mmbase.util.Encode main is for testing purposes only\n");
            System.out.println("   use: java -Dmmbase.config=... org.mmbase.util.Encode [-class <classname> [-class ..]] [-encode|-decode] <coding> [string]\n\n");
            System.out.println("On default it encodes and gets the string from STDIN\n\n"); 
            System.out.println("possible decoding are");
            Vector v = new Vector(possibleEncodings());
            java.util.Collections.sort(v);
            Iterator i = v.iterator();
            while (i.hasNext()) {
                String enc = (String)i.next();
                System.out.println(enc + "   " + ((Config)encodings.get(enc)).info);
            }
        } else {

            if (string == null) {
                //  put STDIN in the string.
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
            }

            // do the job:
            if (decode) {              
                System.out.println(new String(decodeBytes(coding, string)));
                // decode bytes, then also byte decoding go ok... (I think).
            } else {
                System.out.println(encode(coding, string));
            }                                
        }        
    }
}
