/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers;

import java.util.*;
import org.mmbase.util.functions.Parameter;
import org.mmbase.bridge.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractRequest.java,v 1.2 2009-04-17 15:43:39 michiel Exp $
 * @since MMBase-1.9.1
 */

public abstract class AbstractRequest implements Request {

    private boolean valid = true;
    private final Map<Parameter<?>, Object> properties = new HashMap<Parameter<?>, Object>();

    public AbstractRequest() {
    }

    public void invalidate() {
        valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    public Cloud getCloud() {
        return ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
    }
    public Locale getLocale () {
        return getCloud().getLocale();
    }

    public <C> C setProperty(Parameter<C> name, C value) {
        return (C) properties.put(name, value);
    }

    public<C> C getProperty(Parameter<C> name) {
        return (C) properties.get(name);
    }
}
