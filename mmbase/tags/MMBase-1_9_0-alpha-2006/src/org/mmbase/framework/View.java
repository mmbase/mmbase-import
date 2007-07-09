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
 * @version $Id: View.java,v 1.3 2006-09-25 14:00:16 michiel Exp $
 * @since MMBase-1.9
 */
public interface View {

    enum Type {
        ADMIN, FRONTEND
    }

    /**
     * A View can have a certain type, which is in indication off in which environment it could be used.
     */
    Type getType();

    /**
     * Before rendering a View, it may have to be fed with certain parameters. Obtain a parameters
     * object which this method, fill it, and feed it back into {@link #render}.
     */
    Parameters createParameters();

    /**
     * Renders the views to a writer. In case of e.g. a JSPView, the parameters must also contain
     * the Http Servlet response and request, besided specific parameters for this component.
     */
    void render(Parameters parameters, Writer w) throws IOException;
}
