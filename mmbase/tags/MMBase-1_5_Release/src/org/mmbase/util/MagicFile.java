/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.*;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.logging.*;

/**
 * Parse the UNIX magic file and determine types of files.
 * Implementation made on the basis of actual magic file and its manual.<br>
 *
 * TODO:<br>
 * - link the info with mimetypes<br>
 * - add test modifiers<br>
 * - add commandline switches for warning, error and debugging messages<br>
 *<br>
 * Ignored features of magic:<br>
 * - date types<br>
 * - indirect offsets (prefix of '&' in sublevel match or (address+bytes) where offset = value of address plus bytes<br>
 * - AND'ing of type<br>
 *<br>
 * BUGS:<br>
 * - test string isn't read when end of line is reached in absence of a message string<br>
 * <br>
 *
 * Tested:<br>
 * - .doc<br>
 * - .rtf<br>
 * - .pdf<br>
 * - .sh<br>
 * - .gz<br>
 * - .bz2<br>
 * - .html<br>
 * - .rpm<br>
 * - .wav<br>
 *<br>
 * Not supported by magic file:<br>
 * - StarOffice<br>
 *
 * @author cjr@dds.nl
 */
public class MagicFile  {
    // Location of magic file
    protected static String magicfile = "/opt2/mmbase/magic.reduced"; // Dropped those PHP core dumps etc from /usr/lib/magic
    protected static String MAGICXMLFILE  = "magic.xml"; // Name of the XML magic file - should reside in top config dir

    // logger
    private static Logger log = Logging.getLoggerInstance(MagicFile.class.getName());

    // No configuration below
    private static int BIG_ENDIAN = 0;
    private static int LITTLE_ENDIAN = 1;
    private static String[] label = new String[]{"big endian","little endian"};

    protected static int BUFSIZE = 4598; // Read a string of maximally this length from the file

    protected byte[] lithmus = new byte[BUFSIZE];
    protected String magicxml;
    protected Vector detectors;

    /**
     * Exception to be thrown when a feature from UNIX 'file'/magic is not supported
     */
    protected class NotSupported extends Exception {
        public NotSupported(String msg) {
            super(msg);
        }
    }


    protected class Detector {
        String rawinput; // Original input line
        int offset;
        String type;         // types: byte, short, long, string, date, beshort, belong, bedate, leshort, lelong, ledate
        String typeAND;   // Some types are defined as e.g. "belong&0x0000ff70", then typeAND=0x0000ff70 (NOT IMPLEMENTED!)
        String test;      // Test value
        char testComparator;  // What the test is like,
        String message;   // Designation for this type in 'magic' file
        String extension; // Default file extension for this type
        String mimetype;  // MimeType for this type

        String xString;
        int xInt;
        char xChar;

        Vector childList;

        boolean parsingFailure; // Set this if parsing of magic file fails
        boolean hasX;           // Is set when an 'x' value is matched

        protected int nextWhiteSpace(String s) {
            return nextWhiteSpace(s,0);
        }

        protected int nextWhiteSpace(String s, int startIndex) {
            for (int j=startIndex; j < s.length(); j++) {
                if (s.charAt(j) == ' ' || s.charAt(j) == '\t' || s.charAt(j) == '\n') {
                    return j;
                }
            }
            return s.length();
        }

        protected int nextNonWhiteSpace(String s, int startIndex) {
            for (int j=startIndex; j < s.length(); j++) {
                if (s.charAt(j) != ' ' && s.charAt(j) != '\t') {
                    return j;
                }
            }
            return -1;
        }

        /**
         * Separate command from offset
         * @exception Throws an exception when parsing failed
         */
        private int parseOffsetString(String s,int startIndex) throws Exception {
            try {
                int m = nextWhiteSpace(s,startIndex);

                // Bail out when encountering an indirect offset
                char c = s.charAt(startIndex);
                // '&': In sublevel we can start relatively to where the previous match ended
                // '(': Read value at first address, and add that at second to it
                if (c == '&') {
                    parsingFailure = true;
                    throw new NotSupported("parseOffsetString: >& offset feature not implemented\n(Tt is used only for HP Printer Job Language type)");
                } else if (c == '(') {
                    parsingFailure = true;
                    throw new NotSupported("parseOffsetString: indirect offsets not implemented");
                }
                offset = Integer.decode(s.substring(startIndex,m)).intValue();
                return nextNonWhiteSpace(s,m+1);
            } catch (NumberFormatException e) {
                // log.error("string->integer conversion failure for '"+s+"'");
                throw new Exception("parseOffetString: string->integer conversion failure for '"+s+"'");
            }
        }

        /**
         * Parse the type string from the magic file
         *
         *   -- nothing to be done: the found string is already atomic :-)
         */
        private int parseTypeString(String s, int startIndex) throws Exception {
            int m = nextWhiteSpace(s,startIndex);
            if (m <= startIndex) {
                throw new Exception("parseTypeString: failed to delimit type string");
            }
            int n = s.indexOf('&',startIndex);
            if (n > -1 && n < m-2) {
                type = s.substring(startIndex,n);
                typeAND = s.substring(n+1,m);
            } else {
                type = s.substring(startIndex,m);
                typeAND = "0";
            }
            return nextNonWhiteSpace(s,m+1);
        }

        /**
         * Parse the test string from the magic file
         *   -- determine: a.) the test comparator, and b.) the test value
         */
        private int parseTestString(String s, int startIndex) throws Exception {
            int start = 0;
            //int m = nextWhiteSpace(s,startIndex); // XXX need a better algorithm to account for '\' syntax
            // Can't use nextWhiteSpace here, we need harder parsing...
            boolean backslashmode = false;
            boolean octalmode = false;
            boolean hexmode = false;
            //int l = s.length();
            char c;
            StringBuffer numbuf = new StringBuffer();

            test = "";

            c = s.charAt(startIndex);
            switch (c) {
            case '=':
            case '>':
            case '<':
            case '&':
                //case '!': // Heck, what does this mean?
                //case '~': // Heck, what does this mean?
            case '^': testComparator = c; start = 1; break;
            default: testComparator = '='; break;
            }
            if (s.charAt(startIndex+start) == '~' || s.charAt(startIndex+start) == '!') {
                // XXX do nothing with these, but remove them to get rid of decode errors
                start++;
            }
            int i = startIndex+start;

            if (!type.equals("string")) {
                int m = nextWhiteSpace(s,i);
                String t = s.substring(i,m);
                if (t.equals("x")) {
                    test = "x";
                } else if (type.equals("beshort") || type.equals("leshort")) {
                    try {
                        test = "0x"+Integer.toHexString(Integer.decode(s.substring(i,m)).intValue());
                        //test.addElement(Integer.decode(s.substring(i,m)));
                    } catch (NumberFormatException e) {
                        throw new Exception("decode("+s.substring(i,m)+")");
                    }
                } else if (type.equals("belong") || type.equals("lelong")) {
                    // Values possibly too long for Integer, while Long type won't parse :-(
                    int endIndex = m;
                    try {
                        //test.addElement(Long.decode(s.substring(i,m)));
                        if (s.charAt(m-1) == 'L' || s.charAt(m-1) == 'l') {
                            endIndex = m-1;
                        }
                        test = "0x"+Long.toHexString(Long.decode(s.substring(i,endIndex)).longValue());
                    } catch (NumberFormatException e) {
                        log.error(e.getMessage());
                        log.error(Logging.stackTrace(e));
                        throw new Exception("parseLong("+s.substring(i,endIndex)+") ");
                    }
                } else if (type.equals("byte")) {
                    try {
                        test = "0x"+Integer.toHexString(Integer.decode(s.substring(i,m)).intValue());
                        //test.addElement(Integer.decode(s.substring(i,m)));
                    } catch (NumberFormatException e) {
                        throw new Exception("decode("+s.substring(i,m)+")");
                    }
                }
                i = m;
            } else {
                StringBuffer buf = new StringBuffer();

                int testIndex = 0;
                int m = s.length();
                int m1 = i;
                while (i<m) {
                    c = s.charAt(i);
                    if (backslashmode) {
                        switch (c) {
                        case 'n': backslashmode = false; buf.append('\n'); break;
                        case 'r': backslashmode = false; buf.append('\r'); break;
                        case 't': backslashmode = false; buf.append('\t'); break;
                        case '\\':
                            if (hexmode) {
                                try {
                                    //test.addElement(Integer.decode("0x"+numbuf.toString()));
                                    test = test + (char)Integer.decode("0x"+numbuf.toString()).intValue();
                                } catch (NumberFormatException e) {
                                    throw new Exception("decode(0x"+numbuf.toString()+") faalde");
                                }
                                hexmode = false;
                            } else if (octalmode) {
                                try {
                                    //test.addElement(Integer.decode("0"+numbuf.toString()));
                                    test = test + (char)Integer.decode("0"+numbuf.toString()).intValue();
                                } catch (NumberFormatException e) {
                                    throw new Exception("decode(0"+numbuf.toString()+") faalde");
                                }
                                octalmode = false;
                            } else {
                                backslashmode = false;
                                buf.append('\\');
                            }
                            break;
                        case 'x':
                            if (octalmode && numbuf.length()==3) {
                                try {
                                    //test.addElement(Integer.decode("0"+numbuf.toString()));
                                    test = test + (char)Integer.decode("0"+numbuf.toString()).intValue();
                                } catch (NumberFormatException e) {
                                    throw new Exception("decode(0"+numbuf.toString()+") faalde");
                                }
                                octalmode = false;
                                backslashmode = false;
                                buf = new StringBuffer();
                                buf.append('x');
                            } else {
                                hexmode = true;
                                numbuf = new StringBuffer();
                                if (buf.length()>0) {
                                    test = test + buf.toString();
                                    buf = new StringBuffer();
                                }
                            }
                            break;
                        case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
                            // We should be in octalmode or hexmode here!!
                            if (!octalmode && !hexmode) {
                                if (buf.length()>0) {
                                    //test.addElement(buf.toString());
                                    test = test + buf.toString();
                                    buf = new StringBuffer();
                                }
                                octalmode = true;
                                numbuf = new StringBuffer();
                            }
                            numbuf.append( c );
                            break;
                        case ' ':
                            if (octalmode) {
                                try {
                                    //test.addElement(Integer.decode("0"+numbuf.toString()));
                                    test = test + (char)Integer.decode("0"+numbuf.toString()).intValue();
                                } catch (NumberFormatException e) {
                                    throw new Exception("decode(0"+numbuf.toString()+") faalde");
                                }
                                octalmode = false;
                            } else if (hexmode) {
                                try {
                                    //test.addElement(Integer.decode("0x"+numbuf.toString()));
                                    test = test + (char)Integer.decode("0x"+numbuf.toString()).intValue();
                                } catch (NumberFormatException e) {
                                    throw new Exception("decode(0x"+numbuf.toString()+") faalde");
                                }
                                hexmode = false;
                            } else {
                                buf.append(' ');
                            }
                            backslashmode = false;
                            break;
                        default:
                            if (hexmode) {
                                if (c == 'a' || c == 'A' || c == 'b' || c == 'B' || c == 'c' || c == 'C' || c == 'd' || c == 'D' ||
                                    c == 'e' || c == 'E' || c == 'f' || c == 'F') {
                                    numbuf.append(c);
                                } else {
                                    try {
                                        //test.addElement(Integer.decode("0x"+numbuf.toString()));
                                        test = test + (char)Integer.decode("0x"+numbuf.toString()).intValue();
                                    } catch (NumberFormatException e) {
                                        throw new Exception("decode(0x"+numbuf.toString()+") faalde");
                                    }
                                    hexmode = false;
                                    backslashmode = false;
                                }
                            } else if (octalmode) {
                                try {
                                    //test.addElement(Integer.decode("0"+numbuf.toString()));
                                    test = test + (char)Integer.decode("0"+numbuf.toString()).intValue();
                                } catch (NumberFormatException e) {
                                    throw new Exception("decode(0"+numbuf.toString()+") faalde");
                                }
                                octalmode = false;
                                backslashmode = false;
                            } else {
                                backslashmode = false;
                                //tmp[testIndex++] = charToByte(c);
                                buf.append(c);
                            }
                        }
                    } else if (c == '\\') {
                        if (buf.length()>0) {
                            //test.addElement(buf.toString());
                            test = test + buf.toString();
                            buf = new StringBuffer();
                        }
                        backslashmode = true;
                    } else if (c == ' ' || c == '\t' || c == '\n' || i==m-1) {  // Don't forget to set values on end of string
                        if (buf.length() > 0) {
                            //test.addElement(buf.toString());
                            test = test + buf.toString();
                            buf = new StringBuffer();
                        }
                        if (numbuf.length() >0) {
                            if (octalmode) {
                                try {
                                    //test.addElement(Integer.decode("0"+numbuf.toString()));
                                    test = test + (char)Integer.decode("0"+numbuf.toString()).intValue();
                                } catch (NumberFormatException e) {
                                    throw new Exception("decode(0"+numbuf.toString()+") faalde");
                                }
                                octalmode = false;
                                backslashmode = false;
                            } else if (hexmode) {
                                try {
                                    //test.addElement(Integer.decode("0x"+numbuf.toString()));
                                    test = test + (char)Integer.decode("0x"+numbuf.toString()).intValue();
                                } catch (NumberFormatException e) {
                                    throw new Exception("decode(0x"+numbuf.toString()+") faalde");
                                }
                                hexmode = false;
                                backslashmode = false;
                            }
                        }
                        break;
                    } else {
                        buf.append(c);
                    }
                    i++;
                }
            }
            //log.debug("test size = "+test.size());
            //log.debug("test = "+vectorToString(test));
            return nextNonWhiteSpace(s,i+1);
        }



        /**
         * Parse the message string from the magic file
         *
         *   -- nothing to be done: the found string is already atomic :-)
         */
        private int parseMessageString(String s, int startIndex) throws Exception {
            if (false) throw new Exception("dummy exception to stop jikes from complaining");
            message = s.substring(startIndex);
            return s.length()-1;

        }


        /**
         * Add an embedded detector object that searches for more details after an initial match.
         */
        public void addChild(Detector detector, int level) {
            if (level == 1) {
                childList.addElement(detector);
            } else if (level > 1) {
                if (childList.size() == 0) {
                    log.debug("Hm. level = "+level+", but childList is empty");
                } else {
                    ((Detector)childList.elementAt(childList.size()-1)).addChild(detector,level-1);
                }
            }
        }

        protected void init() {
            childList = new Vector();
            extension = "";
            mimetype  = "application/octet-stream";
            message   = "Unknown";
            parsingFailure = false;
        }

        public Detector() {
            init();
        }
        public Detector(String line) {
            String offsetString, typeString, testString, messageString;
            init();
            rawinput = line;

            hasX = false;

            xInt = -99;
            xString = "default";
            xChar = 'x';

            // parse line
            log.debug("parse: "+line);
            int n;
            String level = "start";
            try {
                level = "parseOffsetString";
                n = parseOffsetString(line,0);
                level = "parseTypeString";
                n = parseTypeString(line,n);
                level = "parseTestString";
                n = parseTestString(line,n);
                // If there are multiple test level, an upper one doesn't have to have a message string
                if (n > 0) {
                    level = "parseMessageString";
                    n = parseMessageString(line,n);
                } else {
                    message = "";
                }
                level = "end";
            } catch (NotSupported e) {
                log.warn(e.getMessage());
            } catch (Exception e) {
                log.error("parse failure at "+level+": "+e.getMessage()+" for ["+line+"]");
                parsingFailure = true;
            }
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }
        public String getExtension() {
            return extension;
        }

        public void setMimeType(String mimetype) {
            this.mimetype = mimetype;
        }
        public String getMimeType() {
            if (mimetype.equals("???")) {
                return "application/octet-stream";
            } else {
                return mimetype;
            }
        }
        public void setDesignation(String designation) {
            this.message = designation;
        }
        public void setOffset(String offset) {
            this.offset = Integer.parseInt(offset);
        }
        public int getOffset() {
            return offset;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }
        public void setTest(String test) {
            this.test = test;
        }
        public String getTest() {
            return test;
        }
        public void setComparator(char comparator) {
            this.testComparator = comparator;
        }
        public char getComparator() {
            return testComparator;
        }



        /**
         * @return Whether detector matches the prefix/lithmus of the file
         */
        public boolean test(byte[] lithmus) {
            boolean hit;
            //log.debug("TESTING "+rawinput);
            if (type.equals("string")) {
                hit = testString(lithmus);
            } else if (type.equals("beshort")) {
                hit = testShort(lithmus,BIG_ENDIAN);
            } else if (type.equals("belong")) {
                hit = testLong(lithmus,BIG_ENDIAN);
            } else if (type.equals("leshort")) {
                hit = testShort(lithmus,LITTLE_ENDIAN);
            } else if (type.equals("lelong")) {
                hit = testLong(lithmus,LITTLE_ENDIAN);
            } else if (type.equals("byte")) {
                hit = testByte(lithmus);
            } else {
                // Date types are not supported
                hit = false;
            }
            if (hit) {
                int m = childList.size();
                Detector child;
                if (m > 0) {
                    for (int i=0;i<m;i++) {
                        child = (Detector)childList.elementAt(i);
                        if (child.test(lithmus)) {
                            String s = child.getDesignation();
                            if (s.startsWith("\\b")) {
                                s = s.substring(2);
                            }
                            this.message = this.message + " " + s;
                        }
                    }
                }
            }
            return hit;
        }

        /**
         * todo: I noticed there is also a %5.5s variation in magic...
         */
        public String getDesignation() {
            if (hasX) {
                int n;
                n = message.indexOf("%d");
                if (n >= 0) {
                    return message.substring(0,n)+ xInt + message.substring(n+2);
                }

                n = message.indexOf("%s");
                if (n >= 0) {
                    return message.substring(0,n)+ xString + message.substring(n+2);
                }

                n = message.indexOf("%c");
                if (n >= 0) {
                    return message.substring(0,n) + xChar + message.substring(n+2);
                }
            }
            return message;
        }

        /**
         * @return Whether parsing of magic line for this detector succeeded
         */
        public boolean valid() {
            return !parsingFailure;
        }

        /**
         * @return Conversion of 2 byte array to integer
         */
        private int byteArrayToInt(byte[] ar) {
            StringBuffer buf = new StringBuffer();
            for (int i=0; i < ar.length; i++) {
                buf.append( Integer.toHexString((int)ar[i]&0x000000ff) );
            }
            return Integer.decode("0x"+buf.toString()).intValue();
        }

        /**
         * @return Conversion of 4 byte array to long
         */
        private long byteArrayToLong(byte[] ar) {
            StringBuffer buf = new StringBuffer();
            for (int i=0; i < ar.length; i++) {
                buf.append( Integer.toHexString((int)ar[i]&0x000000ff) );
            }
            String s = buf.toString();
            return Long.decode( "0x"+buf.toString()).longValue();
        }

        /**
         * Test whether a string matches
         */
        protected boolean testString(byte[] lithmus) {
            String lithmusString = new String(lithmus);
            if (test.length() == 0) {
                log.warn("TEST STRING LENGTH ZERO FOR ["+rawinput+"]");
                return false;
            }
            String compare = lithmusString.substring(offset,offset+test.length());
            xString = compare;
            int n;
            log.debug("test string = '"+test+"' ("+message+")");
            n = compare.compareTo(test);
            switch (testComparator) {
            case '=': return n == 0;
            case '>':
                hasX = true;
                return n > 0;
            case '<':
                hasX = true;
                return n < 0;
            }
            return false;
        }

        /**
         * Test whether a short matches
         */
        protected boolean testShort(byte[] lithmus,int endian) {
            log.debug("testing "+label[endian]+" short for "+rawinput);
            int found = 0;
            if (endian == BIG_ENDIAN) {
                found = byteArrayToInt(new byte[]{lithmus[offset],lithmus[offset+1]});
            } else if (endian == LITTLE_ENDIAN) {
                found = byteArrayToInt(new byte[]{lithmus[offset+1],lithmus[offset]});
            }
            xInt = found;

            if (test.equals("x")) {
                hasX = true;
                return true;
            } else if (test.equals("")) {
                return false;
            } else {
                int v = Integer.decode(test).intValue();
                // Hm. How did that binary arithmatic go?
                log.debug("dumb string conversion: 0x"+Integer.toHexString((int)lithmus[offset]&0x000000ff)+Integer.toHexString((int)lithmus[offset+1]&0x000000ff));

                switch (testComparator) {
                case '=':
                    log.debug(Integer.toHexString(v) + " = " + Integer.toHexString(found));
                    return v == found;
                case '>':
                    hasX = true;
                    return found > v;
                case '<':
                    hasX = true;
                    return found < v;
                }
                return false;
            }
        }

        /**
         * Test whether a long matches
         */
        protected boolean testLong(byte[] lithmus, int endian) {
            log.debug("testing "+label[endian]+" long for "+rawinput);
            long found = 0;
            try {
                if (endian == BIG_ENDIAN) {
                    found = byteArrayToLong( new byte[]{
                        lithmus[offset],
                        lithmus[offset+1],
                        lithmus[offset+2],
                        lithmus[offset+3]
                    });
                } else if (endian == LITTLE_ENDIAN) {
                    found = byteArrayToLong( new byte[]{
                        lithmus[offset+3],
                        lithmus[offset+2],
                        lithmus[offset+1],
                        lithmus[offset]
                    });
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                if (!message.equals("")) {
                    log.error("Failed to test "+label[endian]+" long for "+message);
                } else {
                    log.error("Failed to test "+label[endian]+" long:");
                }
                log.error("Offset out of bounds: "+offset+" while max is "+BUFSIZE);
                return false;
            }
                xInt = (int)found; // If it really is a long, we wouldn't want to know about it

            if (test.equals("x")) {
                hasX = true;
                return true;
            } else if (test.equals("")) {
                return false;
            } else {
                long v = Long.decode(test).longValue();

                // Hm. How did that binary arithmatic go?

                switch (testComparator) {
                case '=':
                    log.debug("checking "+label[endian]+" long: "+Long.toHexString(v) + " = " + Long.toHexString(found));
                    return v == found;
                case '>':
                    hasX = true;
                    return found > v;
                case '<':
                    hasX = true;
                    return found < v;
                }

                return false;
            }
        }


        /**
         * Test whether a byte matches
         */
        protected boolean testByte(byte[] lithmus) {
            log.debug("testing byte for "+rawinput);
            if (test.equals("x")) {
                hasX = true;
                xInt = (int)lithmus[offset];
                xChar = (char)lithmus[offset];
                xString = "" + xChar;
                return true;
            } else if (test.equals("")) {
                return false;
            } else {
                byte b = (byte)Integer.decode(test).intValue();
                switch (testComparator) {
                case '=': return b == lithmus[offset];
                case '&':
                    // All bits in the test byte should be set in the found byte
                    //log.debug("byte test as string = '"+test+"'");
                    byte filter = (byte)(lithmus[offset] & b);
                    //log.debug("lithmus = "+lithmus[offset]+"; test = "+b+"; filter = "+filter);
                    return filter == b;
                default: return false;
                }
            }
        }

        /**
         * @return Original unprocessed input line
         */
        public String getRawInput() {
            return rawinput;
        }

        protected String xmlEntities(String s) {
            StringBuffer res = new StringBuffer();
            char c;
            String oct;
            for (int i=0;i<s.length();i++) {
                c = s.charAt(i);
                switch (c) {
                case '>': res.append("&gt;"); break;
                case '<': res.append("&lt;"); break;
                case '&': res.append("&amp;"); break;
                default:
                    // Convert all characters not in the allowed XML character set
                    int n = (int)c;
                    /* -- below is actual xml standard definition of allowed characters
                    if (n == 0x9 || n == 0xA || n == 0xD || (n >= 0x20 && n <= 0xD7FF) || (n >= 0xE000 && n <= 0xFFFD) ||
                        (n >= 0x10000 && n <= 0x10FFFF)) {
                    */
                    if (n==0x9 || n == 0xA || n == 0xD || (n >= 0x20 && n < 128)) {
                        res.append(c);
                    } else {
                        // octal representation of number; pad with zeros
                        oct = Integer.toOctalString(n);
                        res.append("\\");
                        for (int j=3;j>oct.length();j--) {
                            res.append("0");
                        };
                        res.append(oct);
                    }
                }
            }
            return res.toString();
        }

        /**
         * XML notatie:
         * <detector>
         *   <mimetype>foo/bar</mimetype>
         *   <extension>bar</extension>
         *   <designation>blablabla</designation>
         *   <test offset="bla" type="bla" comparator="=">test string</test>
         *   <childlist>
         *     <detector>etc</detector>
         *   </childlist>
         * </detector>
         *
         */
        public void toXML(FileWriter f) throws IOException {
            toXML(f,0);
        }

        /**
         * @param level Indicates depth of (child) element
         */
        public void toXML(FileWriter f, int level) throws IOException {
            StringBuffer s = new StringBuffer();
            String comparatorEntity;

            char[] pad;
            if (level>0) {
                pad = new char[level*4];
                for (int i=0; i<level*4;i++) {
                    pad[i] = ' ';
                }
            } else {
                pad = new char[]{};
            }

            if (testComparator == '>') {
                comparatorEntity = "&gt;";
            } else if (testComparator == '<') {
                comparatorEntity = "&lt;";
            } else if (testComparator == '&') {
                comparatorEntity = "&amp;";
            } else {
                comparatorEntity = ""+testComparator;
            }
            s.append(pad+"<detector>\n"+pad+"  <mimetype>???</mimetype>\n"+pad+"  <extension>???</extension>\n"+
                     pad+"  <designation>"+xmlEntities(message)+"</designation>\n"+
                     pad+"  <test offset=\""+offset+"\" type=\""+type+"\" comparator=\""+comparatorEntity+"\">"+xmlEntities(test)+"</test>\n");
            f.write(s.toString());
            if (childList.size() > 0) {
                f.write(pad+"  <childlist>\n");
                Enumeration enum = childList.elements();
                while (enum.hasMoreElements()) {
                    //s.append(((Detector)enum.nextElement()).toXML(level+1));
                    ((Detector)enum.nextElement()).toXML(f,level+1);
                }
                f.write(pad+"  </childlist>\n"); //s.append(pad).append("  </childlist>\n");
            }
            //s.append(pad).append("</detector>\n");
            f.write(pad+"</detector>\n");

        }

        /**
         * @return String representation of Detector object.
         */
        public String toString() {
            if (parsingFailure) {
                return "parse error";
            } else {
                StringBuffer res = new StringBuffer("["+offset+"] {"+type);
                if (typeAND != "0") {
                    res.append("["+typeAND+"]");
                }
                res.append("} "+testComparator+"("+test+") --> "+message);
                if (childList.size()>0) {
                    res.append("\n");
                    for (int i=0;i<childList.size();i++) {
                        res.append("> ").append(((Detector)childList.elementAt(i)).toString());
                    }
                }
                return res.toString();
            }
        }
    }

    protected class MagicXMLReader extends XMLBasicReader {
        protected int counter;
        public MagicXMLReader(String path) {
            super(path);
            counter = 0;
        }

        public String getVersion() {
            Element e = getElementByPath("magic.info.version");
            return getElementValue(e);
        }
        public String getAuthor() {
            Element e = getElementByPath("magic.info.author");
            return getElementValue(e);
        }
        public String getDescription() {
            Element e = getElementByPath("magic.info.description");
            return getElementValue(e);
        }
        public Vector getDetectors() {
            Vector v = new Vector();
            Element e = getElementByPath("magic.detectorlist");
            if (e==null) {
                log.fatal("BOOOOM!!");  // aargh!
                System.exit(0);
            }

            Enumeration enum = getChildElements(e);
            Element detectorElement;
            Element e1;
            Detector d;
            while (enum.hasMoreElements()) {
                counter++;
                d = getOneDetector((Element)enum.nextElement());
                v.addElement(d);
            }
            //log.error("Read "+counter+" toplevel detectors");
            return v;
        }

        public String html2text(String s) {
            return s;
        }

        /**
         * Replaces octal representations of bytes, written as \ddd to actual byte values.
         */
        private String convertOctals(String s) {
            int p = 0;
            int stoppedAt = 0;
            StringBuffer buf = new StringBuffer();
            char c;
            while (p < s.length()) {
                c = s.charAt(p);
                if (c == '\\') {
                    if (p>s.length()-4) {
                        // Can't be a full octal representation here, let's cut it off
                        break;
                    } else {
                        char c0;
                        boolean failed = false;
                        for (int p0=p+1; p0<p+4;p0++) {
                            c0 = s.charAt(p0);
                            if (!((int)c0>='0' && (int)c0<='9')) {
                                failed = true;
                            }
                        }
                        if (!failed) {
                            String substring = s.substring(p+1,p+4);
                            buf.append(s.substring(stoppedAt,p)).append((char)Integer.parseInt(s.substring(p+1,p+4),8));
                            stoppedAt = p+4;
                            p = p+4;
                        } else {
                            p++;
                        }
                    }
                } else {
                    p++;
                }
            }
            buf.append(s.substring(stoppedAt,p));
            return buf.toString();
        }

        public Detector getOneDetector(Element e) {
            Detector d = new Detector();
            Element e1;
            //detectorElement = (Element)enum.nextElement();
            e1 = getElementByPath(e,"detector.mimetype");
            d.setMimeType( getElementValue(e1) );

            e1 = getElementByPath(e,"detector.extension");
            d.setExtension( getElementValue(e1) );

            e1 = getElementByPath(e,"detector.designation");
            d.setDesignation( getElementValue(e1) );

            e1 = getElementByPath(e,"detector.test");
            d.setTest( convertOctals(getElementValue(e1)) );

            d.setOffset( getElementAttributeValue(e1,"offset"));
            d.setType( getElementAttributeValue(e1,"type"));
            String comparator = getElementAttributeValue(e1,"comparator");
            if (comparator.equals("&gt;")) {
                d.setComparator('>');
            } else if (comparator.equals("&lt;")) {
                d.setComparator('<');
            } else if (comparator.equals("&amp;")) {
                d.setComparator('&');
            } else if (comparator.length() == 1) {
                d.setComparator(comparator.charAt(0));
            } else {
                d.setComparator('=');
            }

            e1 = getElementByPath(e,"detector.childlist");
            if (e1 != null) {
                Enumeration enum = getChildElements(e1);
                Detector child;
                while (enum.hasMoreElements()) {
                    e1 = (Element)enum.nextElement();
                    child = getOneDetector(e1);
                    d.addChild(child,1); // Not sure if this is the right thing
                }
            }
            return d;
        }
    }

    /**
     * Constructor: reads detection data from magic file
     */
    public MagicFile() {
        String configpath = MMBaseContext.getConfigPath();
        magicxml = configpath+File.separator+MAGICXMLFILE;
        log.info("Magic XML file is: "+magicxml);
        try {
            readDetectionData();
        } catch (IOException e) {
            log.error(e.getMessage());
            log.error(Logging.stackTrace(e));
        }
    }

    private void readDetectionData() throws IOException {
        if (false) throw new IOException("This code here to satisfy jikes");
        File magic = new File(magicxml);
        if (!magic.exists()) {
            log.warn("magic file doesn't exist: "+magicxml);
        }
        MagicXMLReader reader = new MagicXMLReader(magicxml);
        detectors = reader.getDetectors();
    }
    /**
     * Read and parse the magic file
     */
    private void XXXreadDetectionData() throws IOException {
        detectors = new Vector();
        BufferedReader reader = new BufferedReader(new FileReader(magicfile));

        String line;
        char c;
        Detector detector;
        while (reader.ready()) {
            line = reader.readLine();
            if (line.length() != 0) {
                c = line.charAt(0);
                if (c != '#' && c != '\n' && c != '\r' && c != '\t' && c != ' ') {
                    // Determine the level as defined by number of prefixed '>'
                    int i=0;
                    while (line.length() > i && line.charAt(i) == '>') {
                        i++;
                    }
                    detector = new Detector(line.substring(i));
                    if (detector.valid()) {
                        if (i==0) {
                            detectors.addElement(detector);
                        } else {
                            ((Detector)detectors.elementAt(detectors.size()-1)).addChild(detector,i);
                        }
                    } else {
                        log.debug("Parsing failed for: '"+line+"'");
                    }
                }
            }
        }
    }

    /**
     * @param path Location of file to be checked
     * @return Type of the file as determined by the magic file
     */
    public String test(String path) {
        try {
            //log.debug("path = "+path);
            FileInputStream fir = new FileInputStream(path);
            if (fir == null) {
                log.error("fir = null");
                return "Error reading "+path+": fir = null";
            }
            int res = fir.read(lithmus,0,BUFSIZE);
            log.debug("read "+res+" bytes from "+path);
        } catch (IOException e) {
            return "Error reading "+path+": "+e.getMessage();
        }

        return test(lithmus);
    }

    public String test(byte[] lithmus) {
        Enumeration enum = detectors.elements();
        Detector detector;
        while (enum.hasMoreElements()) {
            detector = (Detector)enum.nextElement();
            //log.debug("DETECTOR["+detector.getRawInput()+"]");
            if (detector != null && detector.test(lithmus)) {
                //return detector.getDesignation();
                return detector.getMimeType();
            }
        }
        return "Failed to determine type";
    }

    /**
     * @return Enumeration of detectors
     */
    public Enumeration elements() {
        return detectors.elements();
    }

    public boolean toXML(String path) throws IOException {
        File f = new File(path);
        return toXML(f);
    }

    /**
     * Write the current datastructure to an XML file
     * XXX Ugly and hardcoded paths
     */
    public boolean toXML(File f) throws IOException {
        FileWriter writer = new FileWriter(f);

        writer.write("<!DOCTYPE magic PUBLIC \"// MMBase - Magic XML //\" \"http://www.mmbase.org/dtd/magic.dtd\">\n<magic>\n<info>\n<version>0.1</version>\n<author>cjr@dds.nl</author>\n<description>Conversion of the UNIX 'magic' file with added mime types and extensions.</description>\n</info>\n<detectorlist>\n");
        Enumeration enum = this.elements();
        while (enum.hasMoreElements()) {
            ((Detector)enum.nextElement()).toXML(writer);
        }
        writer.write("</detectorlist>\n</magic>\n");
        writer.close();
        return true;
    }

    public boolean fromXML(String path) {
        return false;
    }

    public static void main(String[] argv) {
        try {
            MagicFile df = new MagicFile();

            if (argv.length == 1) {
                if (argv[0].startsWith("--")) {
                    if (argv[0].equals("--xml")) {
                        df.toXML("/tmp/magic.xml");
                        log.info("Written XML version of magic to: /tmp/magic.xml");
                    }
                } else {
                    log.info(df.test(argv[0]));
                }
            } else {
                Enumeration enum = df.elements();
                Detector d;
                while (enum.hasMoreElements()) {
                    d = (Detector)enum.nextElement();
                    log.info(d.toString());
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            log.error(Logging.stackTrace(e));
        }
    }
}





