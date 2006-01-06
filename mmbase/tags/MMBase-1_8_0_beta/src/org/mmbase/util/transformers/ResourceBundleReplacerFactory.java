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
 * @version $Id: ResourceBundleReplacerFactory.java,v 1.3 2005-11-01 23:36:02 michiel Exp $
 */

public class ResourceBundleReplacerFactory implements ParameterizedTransformerFactory {
    private static final Logger log = Logging.getLoggerInstance(ResourceBundleReplacerFactory.class);


    protected static final Parameter[] PARAMS = new Parameter[] {
        new Parameter("basename", String.class, true),
        new Parameter("mode", String.class),
        Parameter.LOCALE
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
        String baseName = (String) parameters.get("basename");
        final ResourceBundle bundle = ResourceBundle.getBundle(baseName, (Locale) parameters.get(Parameter.LOCALE));
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
    protected boolean replace(String word, Writer w) throws IOException  {
        try {
            w.write("" + bundle.getObject(word));
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
