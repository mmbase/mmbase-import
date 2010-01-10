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
 * @version $Id$
 * @since MMBase-1.8
 */

public class ChecksumGetProcessorFactory implements ParameterizedProcessorFactory, java.io.Serializable {

    private static final long serialVersionUID = 1L;

    protected static final Parameter[] PARAMS = new Parameter[] {
        new Parameter.Wrapper(ChecksumFactory.PARAMS),
        new Parameter("field", String.class, true)
    };

    private static final ParameterizedTransformerFactory factory = new ChecksumFactory();

    /**
     * Creates a parameterized processor.
     */
    public Processor createProcessor(Parameters parameters) {
        final ByteToCharTransformer transformer = (ByteToCharTransformer) factory.createTransformer(parameters);
        final String  sourceField = (String) parameters.get("field");
        return new Processor() {
                private static final long serialVersionUID = 1L;

                public Object process(Node node, Field field, Object value) {
                    if (value == null || "".equals(value) ) {
                        if (node.isNull(sourceField) || node.getSize(sourceField) == 0) {
							// set checksum null too.
							// node.setValue(field.getName(), null);
							return value;
						} else {
		                    StringWriter writer = new StringWriter();
                    		transformer.transform(node.getInputStreamValue(sourceField), writer);
                    		value = writer.toString();
                    		if (!field.isVirtual()) {
                        		node.setStringValue(field.getName(), (String) value);
    							node.commit();
                    		}
							return value;
						}
                    } else {
                    	return value;
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
