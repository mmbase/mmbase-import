/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.*;

import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;


/**

 * @author Michiel Meeuwissen 
 * @since MMBase-1.8
 */

public class RegexpReplacerFactory implements ParameterizedTransformerFactory {
    private static final Logger log = Logging.getLoggerInstance(RegexpReplacerFactory.class);

    public void setInverse(boolean inverse) {
        if (inverse) throw new IllegalArgumentException("Regexp-replacers cannot be inverted");
    }

    protected static final Parameter[] PARAMS = new Parameter[] {
        new Parameter("patterns", Collection.class, true)
    };

    public Parameters createParameters() {
        return new ParametersImpl(PARAMS);
    }

    /**
     * Creates a parameterized transformer.
     */
    public Transformer createTransformer(final Parameters parameters) {
        parameters.checkRequiredParameters();
        if (log.isDebugEnabled()) {
            log.debug("Creating transformer, with " + parameters);
        }
        RegexpReplacer trans = new RegexpReplacer() {                
                private Collection patterns = new ArrayList();
                {
                    addPatterns((Collection) parameters.get("patterns"), patterns);
                }
                public Collection getPatterns() {
                    return patterns;
                }
            };

        return trans;
    }





}
