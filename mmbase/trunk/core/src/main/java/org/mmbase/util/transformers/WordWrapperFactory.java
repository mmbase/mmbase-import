/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;
import org.mmbase.util.functions.*;

import org.mmbase.util.logging.*;


/**
 * Word wrapping as a transformer factory. Can be used in taglib e.g. like escape="wrap(20)"
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id$
 */

public class WordWrapperFactory implements ParameterizedTransformerFactory<CharTransformer> {
    private static final Logger log = Logging.getLoggerInstance(WordWrapperFactory.class);

    protected static final Parameter<Integer> LENGTH = new Parameter<Integer>("length", Integer.class, 80);
    protected static final Parameter[] PARAMS = new Parameter[] { LENGTH };

    public Parameters createParameters() {
        return new Parameters(PARAMS);
    }


    /**
     * Creates a parameterized transformer.
     */
    public CharTransformer createTransformer(final Parameters parameters) {
        if (log.isDebugEnabled()) {
            log.debug("Creating transformer, with " + parameters);
        }
        final int length = parameters.get(LENGTH);
        return new ReaderTransformer() {

            public Writer transform(Reader r, Writer w) {
                StringBuilder word = new StringBuilder();  // current word
                try {
                    log.trace("Starting wrapping words.");
                    int ll = 0; // 'line lenght'
                    while (true) {
                        int c = r.read();
                        if (c == -1) break;
                        if (Character.isWhitespace((char) c)) {
                            if (ll + word.length() > length) {
                                w.write('\n');
                                w.write(word.toString());
                                ll = 0;word.length();
                            } else {
                                w.write(word.toString());
                            }
                            if (c == '\n') {
                                ll = 0;
                                w.write(c);
                            } else {
                                ll += word.length();
                                if (ll < length) {
                                    w.write(c);
                                }
                                ll++;
                            }
                            word.setLength(0);
                        } else {
                            word.append((char) c);
                        }
                    }
                    // write last word
                    if (ll + word.length() > length) {
                        w.write('\n');
                    }
                    w.write(word.toString());
                    log.debug("Finished wrapping");
                } catch (java.io.IOException e) {
                    log.error(e.toString());
                }
                return w;
            };
        };

    }


}
