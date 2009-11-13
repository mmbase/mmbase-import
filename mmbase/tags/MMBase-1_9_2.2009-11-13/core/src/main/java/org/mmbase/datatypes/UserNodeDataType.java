/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;
import org.mmbase.framework.*;
import org.mmbase.bridge.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * This 'Node' datatypes uses the framework to determin the currently logged in user object. It
 * depends on the actual implementation of authentication if this is possible and hence not null.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class UserNodeDataType extends NodeDataType {

    private static final Logger log = Logging.getLoggerInstance(UserNodeDataType.class);

    /**
     * Constructor for node field.
     */
    public UserNodeDataType(String name) {
        super(name);
    }

    public Node getDefaultValue(Locale locale, Cloud cloud, Field field) {
        Framework fw = Framework.getInstance();
        Parameters params = fw.createParameters();
        params.setIfDefined(Parameter.CLOUD, cloud);
        return fw.getUserNode(params);
    }

}
