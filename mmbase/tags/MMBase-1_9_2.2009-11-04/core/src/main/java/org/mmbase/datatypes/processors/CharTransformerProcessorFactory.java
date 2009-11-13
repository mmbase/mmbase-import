/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.util.functions.Parameters;
import org.mmbase.util.transformers.*;


/**
 * Wraps a {@link org.mmbase.util.transformers.ParameterizedTransformerFactory} (it <em>must</em> produce
 * a CharTransformer), to be a ParameterizedProcessorFactory. Of course based on {@link CharTransformerProcessor}.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class CharTransformerProcessorFactory implements ParameterizedProcessorFactory {

    private ParameterizedTransformerFactory<CharTransformer> factory;

    public CharTransformerProcessorFactory(ParameterizedTransformerFactory<CharTransformer> f) {
        factory = f;
    }

    public Processor createProcessor(Parameters parameters) {
        CharTransformer ct = factory.createTransformer(parameters);
        return new CharTransformerProcessor(ct);
    }


    public Parameters createParameters() {
        return factory.createParameters();
    }

}
