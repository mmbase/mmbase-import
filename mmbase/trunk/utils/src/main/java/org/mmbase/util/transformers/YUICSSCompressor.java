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
 * CSS compressor based on <a href="http://yuilibrary.com/">YUI Library</a>
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 */

public class YUICSSCompressor extends  ReaderTransformer {
    private static final long serialVersionUID = 0L;
    private static final Logger LOG = Logging.getLoggerInstance(YUICSSCompressor.class);
    private int linebreakpos = -1;
    public YUICSSCompressor() {
    }

    public void setLineBreakPosition(int l) {
        linebreakpos = l;
    }

    @Override
    public Writer transform(Reader reader, Writer writer) {
        try {
            CssCompressor compressor = new CssCompressor(reader);
            compressor.compress(writer, linebreakpos);
        } catch (IOException ioe) {
            LOG.error(ioe);
        }
        return writer;

    }

}
