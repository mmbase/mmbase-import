/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;
import com.yahoo.platform.yui.compressor.*;
import org.mmbase.util.logging.*;


/**
 * Javascript compressor based on http://yuilibrary.com/

 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 */

public class YUIJavaScriptCompressor extends  ReaderTransformer {
    private static final long serialVersionUID = 0L;
    private static final Logger LOG = Logging.getLoggerInstance(YUIJavaScriptCompressor.class);

    private boolean munge = true;
    private boolean preserveAllSemiColons = false;
    private boolean disableOptimizations = false;
    private int linebreakpos = -1;
    public YUIJavaScriptCompressor() {
    }

    public void setMunge(boolean m) {
        munge = m;
    }
    public void setPreserveAllSemiColons(boolean s) {
        preserveAllSemiColons = s;
    }
    public void setDisableOptimizations(boolean s) {
        disableOptimizations = s;
    }

    public void setLineBreakPosition(int l) {
        linebreakpos = l;
    }

    @Override
    public Writer transform(Reader reader, Writer writer) {
        try {
            JavaScriptCompressor compressor = new JavaScriptCompressor(reader, new JavascriptErrorReporter(LOG));
            compressor.compress(writer, linebreakpos, munge, false,
                                preserveAllSemiColons, disableOptimizations);
        } catch (IOException ioe) {
            LOG.error(ioe);
        }
        return writer;

    }

}
