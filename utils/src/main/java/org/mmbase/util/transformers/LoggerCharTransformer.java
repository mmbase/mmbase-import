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
 * This is the character transformer is like {@link CopyCharTranformer}, but it logs everything that it sees too.

 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 * @version $Id$
 */

public class LoggerCharTransformer extends ReaderTransformer implements CharTransformer {
    private static final long serialVersionUID = 0L;
    private static final Logger LOG = Logging.getLoggerInstance(LoggerCharTransformer.class);

    public static final LoggerCharTransformer INSTANCE = new LoggerCharTransformer();

    public LoggerCharTransformer() {
        super();
    }

    // implementation, javadoc inherited
    public Writer transform(Reader r, Writer w) {
        LOG.service("Logging " + r + " -> " + w);
        try {
            long size = 0;
            final char[] buffer = new char[1024];
            int n = 0;
            while (-1 != (n = r.read(buffer))) {
                w.write(buffer, 0, n);
                size += n;
                LOG.service(new String(buffer, 0, n));
            }
            LOG.service("Ready");
        } catch (java.io.IOException ie) {
            LOG.error(ie.getMessage(), ie);
        }
        return w;
    }


}
