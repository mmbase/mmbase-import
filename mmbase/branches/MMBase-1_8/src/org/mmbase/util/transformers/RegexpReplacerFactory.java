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
 * Factories new instances of extensions of {@link RegexpReplacer}, for which the replace patterns
 * are parameterized (using the 'patterns' parameter).
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */

public class RegexpReplacerFactory implements ParameterizedTransformerFactory {
    private static final Logger log = Logging.getLoggerInstance(RegexpReplacerFactory.class);


    protected static final Parameter[] PARAMS = new Parameter[] {
        new Parameter("patterns", Collection.class, Collections.EMPTY_LIST),
        new Parameter("mode", String.class),
        new Parameter("replacefirst", String.class)
    };

    public Parameters createParameters() {
        return new Parameters(PARAMS);
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
        String mode = (String) parameters.get("mode");
        if (mode == null) mode = "WORDS";
        Config c = (Config)trans.transformers().get("REGEXPS_" + mode.toUpperCase());
        if (c == null) c = (Config)trans.transformers().get(mode);
        if (c == null) throw new IllegalArgumentException("" + mode + " cannot be found in " + trans.transformers());
        String firstParam = (String) parameters.get("replacefirst");
        boolean replaceFirst = "true".equals(firstParam);
        boolean replaceFirstAll = "all".equals(firstParam);
        trans.configure(c.config + 
                        (replaceFirst ? RegexpReplacer.REPLACE_FIRST : 0) + 
                        (replaceFirstAll ? RegexpReplacer.REPLACE_FIRST_ALL : 0)
                        );
        return trans;
    }





}
