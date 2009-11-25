/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;

import org.mmbase.util.IOUtil;
import org.mmbase.util.logging.*;

/**
 * You need only to implement transform(byte[]) you have the simplest
 * kind of transformer (which is not 'streamable'). The name becoming your class name.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 */

public abstract class ByteArrayToCharTransformer implements ByteToCharTransformer {
    private static Logger log = Logging.getLoggerInstance(ByteArrayToCharTransformer.class);

    // javadoc inherited
    public abstract String transform(byte[] r);

    // javadoc inherited
    public final OutputStream transformBack(Reader r) {
        return transformBack(r, new ByteArrayOutputStream());
    }

    // javadoc inherited
    public final Writer transform(InputStream in) {
        return transform(in, new StringWriter());
    }

    // javadoc inherited
    public byte[] transformBack(String r) {
        throw new UnsupportedOperationException("transformBack is not supported for this transformer");
    }

    /**
     * An implementation for transform(Reader, Writer) based on transform(String).
     * These functions can be used by extensions to implement transform and transformBack
     */
    public Writer transform(InputStream in, Writer w)  {
        try {
            ByteArrayOutputStream sw = new ByteArrayOutputStream();
            IOUtil.copy(in, sw);
            String result = transform(sw.toByteArray());
            w.write(result);
        } catch (java.io.IOException e) {
            log.error(e.toString(), e);
        }
        return w;
    }

    public OutputStream transformBack(Reader in, OutputStream out)  {
        try {
            StringWriter sw = new StringWriter();
            IOUtil.copy(in, sw);
            out.write(transformBack(sw.toString()));
        } catch (java.io.IOException e) {
            log.error(e.toString(), e);
        }
        return out;
    }
}
