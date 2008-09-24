/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import org.mmbase.util.functions.Parameters;

/**
 * ParameterizedTransformerFactories, can create {@link Transformer} instances, but can do that
 * 'parameterized'. Implementations of this define 'createParameters' which returns an empty {@link
 * org.mmbase.util.functions.Parameters} object which defines which parameters are accepted.
 *
 * The user can then fill this Parameters object and feed it to {@link #createTransformer(Parameters)}.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */

public interface ParameterizedTransformerFactory  {

    /**
     * Creates a parameterized transformer.
     */
    Transformer createTransformer(Parameters parameters);

    /**
     * Create  empty <code>Parameters</code> object for use with {@link #createTransformer(Parameters)}.
     */
    Parameters createParameters();

}
