/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;

import org.mmbase.util.logging.*;

/**
 * To escape from and to 'java' like unicode escaping. That is \\u<4 hex digits>.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7.2
 * @version $Id$
 */

public class UnicodeEscaper extends ReaderTransformer implements CharTransformer {
    private static final Logger log = Logging.getLoggerInstance(UnicodeEscaper.class);

    public Writer transform(Reader r, Writer w) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (c > 127) {
                    String hex = Integer.toHexString(c);
                    int i = hex.length();
                    w.write("\\u");
                    while (i < 4) {
                        w.write('0'); i++;
                    }
                    w.write(hex); 
                } else {
                    w.write(c);
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return w;
    }

    public Writer transformBack(Reader r, Writer w) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (c == '\\') {
                    c = r.read();
                    if (c == -1) { w.write('\\'); break; }
                    if (c == 'u') {
                        // read 4 hexadecimal digits.
                        StringBuffer hex = new StringBuffer(4);
                        while (c != -1 && hex.length() < 4) {
                            c = r.read();
                            hex.append((char) c);
                        }
                        w.write((char) Integer.parseInt(hex.toString(), 16));
                    } else {
                        w.write("\\" + (char) c);
                    }
                } else {
                    w.write((char) c);
                }
            }
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }

    public String toString() {
        return "UnicodeEscaper";
    }
}
