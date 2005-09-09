/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields;

import org.mmbase.util.functions.Parameters;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */

public interface ParameterizedProcessorFactory  {

    /**
     * Creates a parameterized processor.
     */
    Processor createProcessor(Parameters parameters);

    /**
     * Create  empty <code>Parameters</code> object for use with {@link #createProcessor}.
     */
    Parameters createParameters();

}
