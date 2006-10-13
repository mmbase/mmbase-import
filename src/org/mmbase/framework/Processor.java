/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.io.*;
import org.mmbase.util.functions.Parameters;

/**
 * A View is a thing that can actually be rendered, and can be returned by a {@link Component}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Processor.java,v 1.2 2006-10-13 17:22:15 michiel Exp $
 * @since MMBase-1.9
 */
public interface Processor {

    public static final Processor EMPTY = new Processor() {
            public Parameters createParameters() { return Parameters.VOID; }
            public void process(Parameters parameters) { }
        };
    /**
     * Before rendering, it may have to be fed with certain parameters. Obtain a parameters
     * object which this method, fill it, and feed it back into {@link #render}.
     */
    Parameters createParameters();

    /**
     * Process. In case of e.g. a JSPProcessor, the parameters must also contain
     * the Http Servlet response and request, besided specific parameters for this component.
     */
    void process(Parameters parameters) throws IOException;
}
