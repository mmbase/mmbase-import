/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.*;
import java.util.regex.Pattern;

import org.mmbase.util.Entry;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;


/**
 * Factories new instances of extensions of {@link RegexpReplacer}, for which the replace patterns
 * are parameterized (using the 'patterns' parameter).
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id$
 */

public class RegexpReplacerFactory implements ParameterizedTransformerFactory{
    private static final Logger log = Logging.getLoggerInstance(RegexpReplacerFactory.class);

    protected static final Parameter PATTERNS =
        new Parameter("patterns", Collection.class, Collections.EMPTY_LIST);
    protected static final Parameter MODE = new Parameter("mode", String.class, "WORDS");
    protected static final Parameter FIRST_MATCH = new Parameter("onlyFirstMatch", String.class);
    protected static final Parameter FIRST_PATTERN = new Parameter("onlyFirstPattern", String.class);

    protected static final Parameter[] PARAMS = new Parameter[] { PATTERNS, MODE, FIRST_MATCH, FIRST_PATTERN };

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
                    addPatterns((Collection)parameters.get(PATTERNS), patterns);
                }
                public Collection getPatterns() {
                    return patterns;
                }
            };
        String mode = (String) parameters.get(MODE);
        Config c = (Config) trans.transformers().get("REGEXPS_" + mode.toUpperCase());
        if (c == null) c = (Config) trans.transformers().get(mode);
        if (c == null) throw new IllegalArgumentException("" + mode + " cannot be found in " + trans.transformers());
        boolean firstMatch = "true".equals(parameters.get(FIRST_MATCH));
        boolean firstPattern = "true".equals(parameters.get(FIRST_PATTERN));
        int i =  c.config +
            (firstMatch ? ChunkedTransformer.ONLY_REPLACE_FIRST_MATCH : 0) +
            (firstPattern ? ChunkedTransformer.ONLY_USE_FIRST_MATCHING_PATTERN : 0);
        trans.configure(i);

        return trans;
    }

    public static void main(String[] argv) {
        RegexpReplacerFactory fact = new RegexpReplacerFactory();
        Parameters pars = fact.createParameters();
        pars.set("mode", "ENTIRE");
        List patterns = new ArrayList();
        patterns.add(new Entry("\\s+", " "));
        pars.set("patterns", patterns);
        CharTransformer reg = (CharTransformer) fact.createTransformer(pars);

        System.out.println(reg.transform(argv[0]));

    }



}
