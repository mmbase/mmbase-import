/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;
import java.util.*;

import org.mmbase.util.logging.*;

/**
 * Replaces certain 'forbidden' words by something more decent. Of course, censoring is evil, but
 * sometimes it can be amusing too. This is only an example implementation.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 * @version $Id: Censor.java,v 1.3 2003-05-12 22:39:26 michiel Exp $
 */

public class Censor extends ReaderTransformer implements CharTransformer {
    private static Logger log = Logging.getLoggerInstance(Censor.class);

    protected static Map forbidden;
    
    static {
        forbidden = new HashMap();
        forbidden.put("MMBASE", "MMBase"); // catch all occurences of MMbase and so on
        forbidden.put("MICROSOFT", "Micro$oft");
        forbidden.put("FUCK", "****");
    }
    
    /**
     * Writes a word to a Writer, perhaps after replacing it (*censored*).
     *
     * @return true if a replacement occured
     */
    protected boolean censor(String word, Writer w) throws java.io.IOException {
        String replaced = (String) forbidden.get(word.toUpperCase());
        if (replaced == null) {
            w.write(word);
            return false;
        } else {
            w.write(replaced);
            return true;
        }
    }

    public Writer transform(Reader r, Writer w) {
        int replaced = 0;
        StringBuffer word = new StringBuffer();  // current word
        try {
            log.trace("Starting censor");
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (! Character.isLetter((char) c)) {
                    if (censor(word.toString(), w)) replaced++;
                    word.setLength(0);
                    w.write(c);
                } else {       
                    word.append((char) c);
                }
            }
            // write last word
            if (censor(word.toString(), w)) replaced++;
            log.debug("Finished censor. Replaced " + replaced + " words");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }


    public String toString() {
        return "CENSOR";
    }
}
