/**

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.servlet.filter;

import javax.servlet.ServletOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Wrapper for the ServletOutputStream.
 *
 * For futher explanation, 
 *  see http://www.orionserver.com/tutorials/filters
 *
 * @author Marcel Maatkamp, VPRO Netherlands (marmaa_at_vpro.nl)
 * @version $Version$
 */

public class FilterServletOutputStream extends ServletOutputStream {
    static Logger log = Logging.getLoggerInstance(FilterServletOutputStream.class.getName());

    private DataOutputStream stream;

    public FilterServletOutputStream(OutputStream output) {
        stream = new DataOutputStream(output);
    }

    public void write(int b) throws IOException {
        stream.write(b);
    }

    public void write(byte[] b) throws IOException {
        stream.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        stream.write(b, off, len);
    }
}
