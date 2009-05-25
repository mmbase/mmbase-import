/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.logging;
import java.io.Writer;

/**
 * A Logger which writes everything logged to it to a given Writer. The Writer can e.g. be a {@link
 * java.io.StringWriter} if you want to create one String.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */

public class WriterLogger extends AbstractSimpleImpl {

    protected final  Writer writer;

    public WriterLogger(Writer w) {
        this(w, Level.INFO);
    }

    public WriterLogger(Writer w, Level l) {
        level = l.toInt();
        writer = w;
    }

    protected void log(String s, Level level) {
        try {
            writer.write(s); writer.write('\n');
        } catch (java.io.IOException ioe) {
            // should not happen
        }
    }
}
