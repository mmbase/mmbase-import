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

 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 * @version $Id$
 */

public abstract class BufferedReaderTransformer extends ReaderTransformer implements CharTransformer {

    private static Logger log = Logging.getLoggerInstance(BufferedReaderTransformer.class);

    /**
     * Override {@link ReaderTransformer#transform(Reader, Writer)}
     */
    public final Writer transform(Reader r, Writer w) {
        try {
            BufferedReader br = new BufferedReader(r);
            PrintWriter bw = new PrintWriter(new BufferedWriter(w));

            Status status = createNewStatus();
            String line = br.readLine();
            while (line != null) {
                boolean nl = transform(bw, line, status);
                line = br.readLine();
                if (nl && line != null) bw.write('\n');
            }
            br.close();
            bw.flush();
        } catch (java.io.IOException e) {
            log.error(e.toString(), e);
        }
        return w;
    }

    /**
     * @param bw the writer to direct the output to
     * @param line the input
     * @param status this object could hold transformation status information. Or <code>null</code>
     * @return <code>true</code> when line is transformed
     */
    protected abstract boolean transform(PrintWriter bw, String line, Status status);

    protected abstract Status createNewStatus();

    abstract static class Status{
    }


}
