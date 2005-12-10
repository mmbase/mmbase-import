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

/**
 * See {@link org.mmbase.util.transformers.ChecksumFactory}. This produces CommitProcessors meant
 * for fields that are a checksum of another field. Parameters for that are the parameters of the
 * Checksum 'processor', and the field for which this field is a checksum.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ChecksumProcessorFactory.java,v 1.3 2005-12-10 14:33:36 michiel Exp $
 * @since MMBase-1.8
 */

public class ChecksumProcessorFactory implements ParameterizedCommitProcessorFactory {

    protected static final Parameter[] PARAMS = new Parameter[] {
        new Parameter.Wrapper(ChecksumFactory.PARAMS),
        new Parameter("field", String.class, true)
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
                    if (node.isNull(sourceField)) return; // leave checksum null too.
                    StringWriter writer = new StringWriter();
                    transformer.transform(node.getInputStreamValue(sourceField), writer);
                    node.setStringValue(field.getName(), writer.toString());
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
