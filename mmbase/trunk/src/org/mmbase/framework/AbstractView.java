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
 * Abstract view implementation which implements getType and the specific parameters.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractView.java,v 1.1 2006-09-12 19:25:59 michiel Exp $
 * @since MMBase-1.9
 */
abstract public class AbstractView implements View {

    protected final Type type;
    protected final List<Parameter> specific = new ArrayList<Parameter>();

    public AbstractView(String t) {
        type = Type.valueOf(t);
    }
    public Type getType() {
        return type;
    }

    public void addParameters(Parameter... params) {
        for (Parameter p : params) {
            specific.add(p);
        }
    }

    protected Parameter.Wrapper getSpecificParameters() {
        return new Parameter.Wrapper(specific.toArray(Parameter.EMPTY)); 
    }


}
