/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.*;
import java.io.*;

import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;


/**
 * Factories new chartransformer based on {@link java.util.ResourceBundle}. The result transformers
 * are based on {@link ChunkedTransformer}, so using the 'mode' argument it can be controlled what
 * excactly must correspond to the resource bundle keys (e.g. words, lines, or the entire
 * string). Defaults to words.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id$
 */

public class ResourceBundleReplacerFactory implements ParameterizedTransformerFactory<CharTransformer> {
    private static final Logger log = Logging.getLoggerInstance(ResourceBundleReplacerFactory.class);


    protected static final Parameter[] PARAMS = new Parameter[] {
        new Parameter<String>("basename", String.class, true),
        new Parameter<String>("mode", String.class),
        Parameter.LOCALE
    };

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
        String baseName = (String) parameters.get("basename");
        final ResourceBundle bundle = ResourceBundle.getBundle(baseName, parameters.get(Parameter.LOCALE));
        return new ResourceBundleReplacer(1, bundle, baseName);
    }

}

class ResourceBundleReplacer extends ChunkedTransformer {
    private final ResourceBundle bundle;
    private final String name;
    ResourceBundleReplacer(int mode, ResourceBundle bundle, String name) {
        super(mode);
        this.bundle = bundle;
        this.name = name;
    }
    protected boolean replace(final String word, final Writer w, final Status status) throws IOException  {
         if (onlyFirstPattern && status.used.contains(word)) {
            w.write(word);
            return false;
        }
        try {
            w.write("" + bundle.getObject(word));
            status.replaced++;
            if (onlyFirstMatch) status.used.add(word);
            return true;
        } catch (MissingResourceException mre) {
            w.write(word);
            return false;
        }
    }
    protected String base() {
        return name;
    }
};
