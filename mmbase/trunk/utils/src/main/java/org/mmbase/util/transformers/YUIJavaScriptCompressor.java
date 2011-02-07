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
 * Javascript compressor based on <a href="http://yuilibrary.com/">YUI Library</a>

 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 */

public class YUIJavaScriptCompressor extends  ReaderTransformer {
    private static final long serialVersionUID = 0L;
    private static final Logger LOG = Logging.getLoggerInstance(YUIJavaScriptCompressor.class);

    private static final boolean WORKS;

    static {
        boolean w = true;
        try {
            JavaScriptCompressor compressor = new JavaScriptCompressor(new StringReader("function a() {}"),
                                                                       new JavaScriptErrorReporter(LOG));
            compressor.compress(new StringWriter(), 0, false, false, false, false);

            LOG.service("It seams that there are no problems with rhino artifacts, so YUI Compressor can be used.");
        } catch (IOException ieo) {
            LOG.warn(ieo.getMessage(), ieo);
            w = false;
        } catch (StringIndexOutOfBoundsException sie) {
            LOG.info("Javascript compression not working. See e.g. http://yuilibrary.com/forum/viewtopic.php?f=94&t=3345&p=20085#p20085. " + sie.getMessage());
            w = false;
        } catch (NoClassDefFoundError ncdfe) {
            LOG.info(ncdfe.getClass().getName() + " " + ncdfe.getMessage() + ". Javascript and CSS compression will not work.");
            w = false;
        } catch (Throwable re) {
            LOG.warn(re.getMessage(), re);
            w = false;
        }
        WORKS = w;
    }

    private boolean munge = true;
    private boolean preserveAllSemiColons = false;
    private boolean disableOptimizations = false;
    private boolean initialNewline = true;

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
    public void setInitialNewline(boolean i) {
        initialNewline = i;
    }

    @Override
    public Writer transform(Reader reader, Writer writer) {
        try {
            if (initialNewline) {
                writer.write("\n");
            }

            if (WORKS) {
                LOG.service("Compressing javascript from " + reader + " -> " + writer);
                JavaScriptCompressor compressor = new JavaScriptCompressor(reader,
                                                                           new JavaScriptErrorReporter(LOG));
                compressor.compress(writer, linebreakpos, munge, false,
                                    preserveAllSemiColons, disableOptimizations);
                LOG.debug("Ready");
            } else {
                CopyCharTransformer.INSTANCE.transform(reader, writer);
            }
        } catch (IOException ioe) {
            LOG.warn(ioe.getMessage(), ioe);
        } finally {
            LOG.debug(".");
        }
        return writer;

    }

}
