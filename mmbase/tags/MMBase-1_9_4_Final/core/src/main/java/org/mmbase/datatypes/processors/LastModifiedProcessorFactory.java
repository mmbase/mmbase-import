/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.functions.*;

/**
 * This factory creates processors which don't actually change the value, but only have a
 * side-effect, namely updating another field with the current time. The other field is on default
 * 'lastmodified', but it also the only parameter of this processor factory.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class LastModifiedProcessorFactory implements ParameterizedProcessorFactory, java.io.Serializable {

    private static final long serialVersionUID = 1L;

    protected static final Parameter[] PARAMS = new Parameter[] {
        new Parameter<String>("field", String.class, "lastmodified")
    };

    /**
     * Creates a parameterized processor.
     */
    public Processor createProcessor(Parameters parameters) {
        final String  destField = (String) parameters.get("field");
        return new Processor() {
            private static final long serialVersionUID = 1L;

            public Object process(Node node, Field field, Object value) {
                node.setDateValue(destField, new java.util.Date());
                return value;
            }
        };
    }

    /**
     * Create  empty <code>Parameters</code> object for use with {@link #createProcessor}.
     */
    public Parameters createParameters() {
        return new Parameters(PARAMS);
    }

}
