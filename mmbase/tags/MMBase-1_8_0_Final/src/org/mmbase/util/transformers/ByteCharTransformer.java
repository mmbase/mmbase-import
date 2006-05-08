/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;

import org.mmbase.util.logging.*;

/**
 * A CharTransformer which wraps a ByteToCharTransformer.
 * 
 * It uses the <em>UTF-8</em> bytes (on default).
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: ByteCharTransformer.java,v 1.2 2005-07-09 11:14:38 nklasens Exp $
 */

public class ByteCharTransformer extends ReaderTransformer implements CharTransformer {
    private static Logger log = Logging.getLoggerInstance(ByteCharTransformer.class);

    private ByteToCharTransformer byteToChars;
    private String encoding = "UTF-8";
    
    public ByteCharTransformer(ByteToCharTransformer b) {
        byteToChars = b;
    }
    public ByteCharTransformer(ByteToCharTransformer b, String enc) {
        this(b);
        encoding = enc;
    }


    // javadoc inherited
    public Writer transform(Reader reader, Writer writer) {
        try {
            while (true) {
                int c = reader.read();
                if (c == -1) break;
                String s = "" + (char) c;
                writer.write(byteToChars.transform(s.getBytes(encoding)));
            }
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return writer;
    }

    public String toString() {
        return "CHAR "  + byteToChars ;
    }

    
}
