/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.mmbase.util.logging.*;

/**
 * You need only to implement transform(Reader, Writer) you have the simplest
 * kind of tranformer. The name becoming your class name.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 */

public abstract class AbstractCharTransformer implements CharTransformer {
    private static Logger log = Logging.getLoggerInstance(AbstractCharTransformer.class.getName());


    // javadoc inherited
    public abstract Writer transform(Reader r, Writer w);

    // javadoc inherited
    public Writer transformBack(Reader r, Writer w) {
        throw new UnsupportedOperationException("transformBack is not supported for this transformer");
    }
        
    // javadoc inherited
    public final Writer transformBack(Reader r) {
        return transformBack(r, new StringWriter());
    }

    // javadoc inherited
    public final Writer transform(Reader r) {
        return transform(r, new StringWriter());
    }

    // javadoc inherited
    public String transform(String r) {
        Writer sw = transform(new StringReader(r));
        return sw.toString();
    }

    // javadoc inherited
    public String transformBack(String r) {
        Writer sw = transformBack(new StringReader(r));
        return sw.toString();
    }


    /**
     * An implemention for tranform(Reader, Writer) based on transform(String).
     * Evil, evil, but convention sometimes. 
     * These functions can be used by extensions to implement transform and transformBack
     */
    protected Writer transformUtil(Reader r, Writer w)  {
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

    protected Writer transformBackUtil(Reader r, Writer w)  {
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
