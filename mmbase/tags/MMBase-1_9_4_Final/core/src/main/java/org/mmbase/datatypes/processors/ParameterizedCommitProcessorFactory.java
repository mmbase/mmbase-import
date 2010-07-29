/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.util.functions.Parameters;

/**
 * A parameterized commitprocessor factory creates {@link CommitProcessor}s, using {@link Parameters}.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */

public interface ParameterizedCommitProcessorFactory  {

    /**
     * Creates a parameterized processor.
     * @param parameters parameters for the processor
     * @return Datatype Processor
     */
    CommitProcessor createProcessor(Parameters parameters);

    /**
     * Create  empty <code>Parameters</code> object for use with {@link #createProcessor(Parameters)}.
     * @return Datatype Processor parameters
     */
    Parameters createParameters();

}
