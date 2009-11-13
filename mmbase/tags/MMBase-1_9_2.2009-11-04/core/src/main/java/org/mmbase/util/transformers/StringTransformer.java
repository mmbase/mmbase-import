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
 * You need only to implement transform(String) you have the simplest
 * kind of tranformer (which is not 'streamable'). The name becoming your class name.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 */

public abstract class StringTransformer implements CharTransformer {
    private static final Logger log = Logging.getLoggerInstance(StringTransformer.class);

    // javadoc inherited
    public abstract String transform(String r);
        
    // javadoc inherited
    public final Writer transformBack(Reader r) {
        return transformBack(r, new StringWriter());
    }

    // javadoc inherited
    public final Writer transform(Reader r) {
        return transform(r, new StringWriter());
    }

    // javadoc inherited
    public String transformBack(String r) {
        throw new UnsupportedOperationException("transformBack is not supported for this transformer");
    }

    /**
     * An implemention for tranform(Reader, Writer) based on transform(String).
     * These functions can be used by extensions to implement transform and transformBack
     */
    public Writer transform(Reader r, Writer w)  {
        try {
            StringWriter sw = new StringWriter();
            while (true) {
                int c = r.read();
                if (c == -1) break;
                sw.write(c);
            }
            String result = transform(sw.toString());
            w.write(result);
        } catch (java.io.IOException e) {
            log.error(e.toString());
            log.debug(Logging.stackTrace(e));
        }
        return w;
    }

    public Writer transformBack(Reader r, Writer w)  {
        try {
            StringWriter sw = new StringWriter();
            while (true) {
                int c = r.read();
                if (c == -1) break;
                sw.write(c);
            }
            String result = transformBack(sw.toString());
            w.write(result);
        } catch (java.io.IOException e) {
            log.error(e.toString());
            log.debug(Logging.stackTrace(e));
        }
        return w;
    }    
}
