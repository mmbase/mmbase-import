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

public class RegexpReplacerFactory implements ParameterizedTransformerFactory<CharTransformer>, java.io.Serializable {
    private static final Logger log = Logging.getLoggerInstance(RegexpReplacerFactory.class);

    protected static final Parameter<Collection> PATTERNS =
        new Parameter<Collection>("patterns", Collection.class, Collections.emptyList());
    protected static final Parameter<String> MODE = new Parameter<String>("mode", String.class, "WORDS");
    protected static final Parameter<String> FIRST_MATCH = new Parameter<String>("onlyFirstMatch", String.class);
    protected static final Parameter<String> FIRST_PATTERN = new Parameter<String>("onlyFirstPattern", String.class);

    protected static final Parameter[] PARAMS = new Parameter[] { PATTERNS, MODE, FIRST_MATCH, FIRST_PATTERN };

    public Parameters createParameters() {
        return new Parameters(PARAMS);
    }

    /**
     * Creates a parameterized transformer.
     */
    public CharTransformer createTransformer(final Parameters parameters) {
        parameters.checkRequiredParameters();
        if (log.isDebugEnabled()) {
            log.debug("Creating transformer, with " + parameters);
        }
        RegexpReplacer trans = new RegexpReplacer() {
                private Collection<Entry<Pattern,String>> patterns = new ArrayList<Entry<Pattern,String>>();
                {
                    addPatterns(parameters.get(PATTERNS), patterns);
                }
            @Override
                public Collection<Entry<Pattern,String>> getPatterns() {
                    return patterns;
                }
            };
        String mode = parameters.get(MODE);
        Config c = trans.transformers().get("REGEXPS_" + mode.toUpperCase());
        if (c == null) c = trans.transformers().get(mode);
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
        List<Map.Entry<String, String>> patterns = new ArrayList<Map.Entry<String, String>>();
        patterns.add(new Entry<String, String>("\\s+", " "));
        pars.set("patterns", patterns);
        CharTransformer reg = fact.createTransformer(pars);

        System.out.println(reg.transform(argv[0]));

    }



}
