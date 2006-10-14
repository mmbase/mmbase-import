/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import org.mmbase.util.functions.*;

/**
 * Abstract view implementation which implements getType and creates the 'essential' parameters
 * request and response.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractRenderer.java,v 1.2 2006-10-14 09:46:48 michiel Exp $
 * @since MMBase-1.9
 */
abstract public class AbstractRenderer implements Renderer {

    protected final Type type;
    protected Parameter.Wrapper specific;

    public AbstractRenderer(String t) {
        type = Type.valueOf(t);
    }

    public Type getType() {
        return type;
    }

    void addParameters(Parameter<?>... params) {
        List<Parameter> help = new ArrayList<Parameter>();
        if (specific != null) {
            help.addAll(Arrays.asList(specific.getArguments()));
        }
        for (Parameter p : params) {
            help.add(p);
        }
        specific = new Parameter.Wrapper(help.toArray(Parameter.EMPTY));
    }


    public Parameters createParameters() {
        if (specific == null) {
            return new AutodefiningParameters();
        } else {
            return new Parameters(specific, new Parameter.Wrapper(getEssentialParameters()));
        }
    }

    protected Parameter[] getEssentialParameters() {
        return Parameter.EMPTY;
    }

    /**
     *<p>
     * Returns a Parameter.Wrapper with 'specific' parameters. This can be <code>null</code> which
     * means 'undefined', and no parameter checking will be done, and every parameter will be
     * acceptable.
     * </p>
     * <p>
     * An actual implementation of Renderer
     *
     */

    protected Parameter.Wrapper getSpecificParameters() {
        return null;
    }


}
