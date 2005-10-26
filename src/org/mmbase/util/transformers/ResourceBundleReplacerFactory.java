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
 * Factories new chartransformer base on {@link java.util.ResourceBundler}. Search for words (which
 * must consist of letters and digits) and searches that in the resource-bundle, and if found,
 * replaces with the value.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id: ResourceBundleReplacerFactory.java,v 1.1 2005-10-26 21:58:34 michiel Exp $
 */

public class ResourceBundleReplacerFactory implements ParameterizedTransformerFactory {
    private static final Logger log = Logging.getLoggerInstance(ResourceBundleReplacerFactory.class);


    protected static final Parameter[] PARAMS = new Parameter[] {
        new Parameter("basename", String.class, true),
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
        final ResourceBundle bundle = ResourceBundle.getBundle((String) parameters.get("basename"), (Locale) parameters.get(Parameter.LOCALE));
        return new ReaderTransformer() {
                public boolean replaceWord(String word, Writer w) throws IOException  {
                    try {
                        w.write("" + bundle.getObject(word));
                        return true;
                    } catch (MissingResourceException mre) {
                        w.write(word);
                        return false;
                    }
                    
                }
                public Writer transform(Reader r, Writer w)  {
                    int replaced = 0;
                    StringBuffer word = new StringBuffer();  // current word
                    try {
                        log.trace("Starting regexp replacing");
                        while (true) {
                            int c = r.read();
                            if (c == -1) break;
                            if (! Character.isLetterOrDigit((char) c)) {
                                if (replaceWord(word.toString(), w)) replaced++;
                                word.setLength(0);
                                w.write(c);
                            } else {
                                word.append((char) c);
                            }
                        }
                        // write last word
                        if (replaceWord(word.toString(), w)) replaced++;
                        log.debug("Finished regexp replacing. Replaced " + replaced + " words");
                    } catch (java.io.IOException e) {
                        log.error(e.toString());
                    }
                    return w;
                }
            };
    }
    
    



}
