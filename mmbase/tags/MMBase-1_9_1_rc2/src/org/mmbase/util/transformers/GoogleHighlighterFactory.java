/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.*;
import java.util.regex.Pattern;
import java.net.URL;

import org.mmbase.util.Entry;

import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * An example for parameterized transformers. The Google highlighter transformers have a REQUEST
 * parameter, which are used to explore the 'Referer' HTTP header and highlight the google search
 * words.
 * This can be used in taglib e.g. by &lt;mm:content postprocessor="google" expires="0" /&gt;
 *
 * Because you need expires=0, you need be reluctant to use this, because this means that your page
 * cannot be cached in front-proxies. Perhaps it is better to find some client-side solution.
 *
 * It produces instances of extensions of {@link RegexpReplacer}
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */

public class GoogleHighlighterFactory  implements ParameterizedTransformerFactory<CharTransformer> {
    private static final Logger log = Logging.getLoggerInstance(GoogleHighlighterFactory.class);

    private static final Parameter<String> FORMAT = new Parameter<String>("format", String.class, "<span class=\"google\">$1</span>");
    private static final Parameter<String> HOST   = new Parameter<String>("host",   String.class, "google");
    private static final Parameter[] PARAM = new Parameter[] { FORMAT, HOST, Parameter.REQUEST };

    public CharTransformer createTransformer(final Parameters parameters) {
        parameters.checkRequiredParameters();
        if (log.isDebugEnabled()) {
            log.debug("Creating transformer, with " + parameters);
        }
        URL referrer;
        String referer = (parameters.get(Parameter.REQUEST)).getHeader("Referer");
        if (referer == null) return CopyCharTransformer.INSTANCE;

        try {
            referrer = new URL(referer);
        } catch (java.net.MalformedURLException mfue) {
            log.warn(mfue.getMessage() + " for '" + referer + "'", mfue);
            return CopyCharTransformer.INSTANCE;
        }
        log.debug("Using referrer " + referrer);
        if (referrer.getHost().indexOf(parameters.get(HOST)) == -1) { // did not refer
                                                                                 // from google
            log.debug("Wrong host, returning COPY");
            return CopyCharTransformer.INSTANCE;
        }
        String queryString = referrer.getQuery();
        if (queryString == null) {
            // odd
            log.debug("No query, returning COPY");
            return CopyCharTransformer.INSTANCE;
        }
        String[] query = queryString.split("&");

        String s = null;
        for (String q : query) {
            if (q.startsWith("q=")) {
                try {
                    s = java.net.URLDecoder.decode(q.substring(2), "UTF-8");
                } catch (java.io.UnsupportedEncodingException uee) { // cannot happen
                    s = q.substring(2);
                }
                break;
            }
        }
        if (s == null) {
            // odd
            log.debug("No search, returning COPY");
            return CopyCharTransformer.INSTANCE;
        }
        final String search = s;
        log.debug("Using search " + search);

        RegexpReplacer trans = new RegexpReplacer() {
                private Collection<Entry<Pattern,String>> patterns = new ArrayList<Entry<Pattern,String>>();
                {
                    Pattern p        = Pattern.compile("(" + search.replace('+', '|') + ")");
                    patterns.add(new Entry<Pattern,String>(p, parameters.get(FORMAT)));
                }
                public Collection<Entry<Pattern,String>> getPatterns() {
                    return patterns;
                }
            };
        if (log.isDebugEnabled()) {
            log.debug("Using google transformer " + trans);
        }
        return trans;

    }
    public Parameters createParameters() {
        return new Parameters(PARAM);
    }

}
