/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.transformers.*;
import org.mmbase.util.functions.*;
import java.io.StringWriter;
import org.mmbase.util.logging.*;


/**
 * See {@link org.mmbase.util.transformers.ChecksumFactory}. This produces CommitProcessors meant
 * for fields that are a checksum of another field. Parameters for that are the parameters of the
 * Checksum 'processor', and the field for which this field is a checksum.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class ChecksumProcessorFactory implements ParameterizedCommitProcessorFactory, java.io.Serializable {

    private static final Logger log = Logging.getLoggerInstance(ChecksumProcessorFactory.class);

    private static final long serialVersionUID = 1L;

    protected static final Parameter[] PARAMS = new Parameter[] {
        new Parameter.Wrapper(ChecksumFactory.PARAMS),
        new Parameter<String>("field", String.class, true)
    };

    private static final ParameterizedTransformerFactory factory = new ChecksumFactory();

    /**
     * Creates a parameterized processor.
     */
    public CommitProcessor createProcessor(Parameters parameters) {
        final ByteToCharTransformer transformer = (ByteToCharTransformer) factory.createTransformer(parameters);
        final String  sourceField = (String) parameters.get("field");
        return new CommitProcessor() {
            private static final long serialVersionUID = 1L;

            public void commit(Node node, Field field) {
                if (!field.isVirtual()) {
                    if (node.getChanged().contains(sourceField)) {
                        if (node.isNull(sourceField)) {
                            log.debug("Source field is null");
                            // set checksum null too.
                            node.setValue(field.getName(), null);
                            return;
                        }
                        StringWriter writer = new StringWriter();
                        transformer.transform(node.getInputStreamValue(sourceField), writer);
                        String checksum = writer.toString();
                        if (log.isDebugEnabled()) {
                            log.debug("Setting checksum field '" + field.getName() + "' to " + checksum);
                        }
                        node.setStringValue(field.getName(), checksum);
                    } else {
                        log.debug("Ignoring because '" + sourceField + "' is not changed");
                    }
                } else {
                    log.debug("Ignoring because '" + field + "' is virtual");
                }
            }
            public String toString() {
                return transformer.toString() + " on " + sourceField;
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
