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
 * Trims leading and trailing white space.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 * @version $Id$
 */

public class Trimmer extends ReaderTransformer implements CharTransformer {
    private static final Logger log = Logging.getLoggerInstance(Trimmer.class);


    public String transform(String s) {
        return s.trim();
    }

    public Writer transform(Reader r, Writer w) {
        StringBuffer word = new StringBuffer();  // current word
        StringBuffer space = new StringBuffer();
        try {
            log.trace("Starting trim");
            int c = r.read();

            while (c != -1 && Character.isWhitespace((char) c)) {
                c = r.read();
            }
            boolean inWord = true;
            while (true) {           
                if (Character.isWhitespace((char) c)) {
                    if (inWord) {
                        w.write(word.toString());
                        word.setLength(0);
                        inWord = false;
                    }
                    space.append((char) c);
                } else {
                    if (! inWord) {
                        w.write(space.toString());
                        space.setLength(0);
                        inWord = true;
                    }
                    word.append((char) c);
                }
                c = r.read();
                if (c == -1) break;
            }
            // write last word, but not last spaces
            if (inWord) {
                w.write(word.toString());
            }
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }


    public String toString() {
        return "TRIMMER";
    }
}
