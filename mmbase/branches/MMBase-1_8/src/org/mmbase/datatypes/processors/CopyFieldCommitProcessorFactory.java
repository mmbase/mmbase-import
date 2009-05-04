/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.functions.*;

import org.mmbase.util.logging.*;
/**
 * This factory produces commit-processors which simply the copy the value of another field of the
 * same node, to the field to which this CommitProcessor was associated.
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.6
 */

public class CopyFieldCommitProcessorFactory implements ParameterizedCommitProcessorFactory, java.io.Serializable {

    private static final Logger log = Logging.getLoggerInstance(CopyFieldCommitProcessorFactory.class);
    private static final long serialVersionUID = 1L;

    protected static final Parameter[] PARAMS = new Parameter[] {
        new Parameter("field", String.class, true)
    };

    /**
     * Creates a parameterized processor.
     */
    public CommitProcessor createProcessor(Parameters parameters) {
        final String  sourceField = (String) parameters.get("field");
        return new CommitProcessor() {
            private static final long serialVersionUID = 1L;

            public void commit(Node node, Field field) {
                log.debug("Committing for " + field + " " + node.getChanged());
                if (node.isChanged(sourceField) && ! node.isChanged(field.getName())) {
                    node.setValue(field.getName(), node.getValue(sourceField));
                }
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
