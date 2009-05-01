/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import org.mmbase.util.functions.*;
import java.io.Reader;
import java.io.Writer;

import org.mmbase.util.logging.*;

/**
 * Wraps a text in paragraph (p) tags, unless it is empty or that text starts with a p tag (in which case it is already wrapped).
 * Use this for fields that contain HTML, but for which you are unsure if they have paragraph tags already
 * @author Pierre van Rooden
 * @since MMBase-1.9
 * @version $Id$
 */

public class ParagraphTransformerFactory implements ParameterizedTransformerFactory {
    private static Logger log = Logging.getLoggerInstance(ParagraphTransformerFactory.class);

    protected final static Parameter[] PARAMS = {
        new Parameter("class", String.class, "")
    };

    public Transformer createTransformer(Parameters parameters) {
        return new ParagraphTransformer( (String) parameters.get("class") );
    }
    public Parameters createParameters() {
        return new Parameters(PARAMS);
    }

    public class ParagraphTransformer extends StringTransformer {

        private String className = null;

        ParagraphTransformer(String c) {
             className = c;
        }

        public String transform(String r) {
            if (r != null && !r.equals("")) {
                if (r.startsWith("<p>")) {
                    if (className != null && !className.equals("")) {
                      r = "<p class=\"" + className + "\" >" + r.substring(3);
                    }
                } else if (!r.startsWith("<p ") ) {
                    if (className != null && !className.equals("")) {
                        r = "<p class=\"" +className + "\" >" + r + "</p>";
                    } else {
                        r = "<p>" + r + "</p>";
                    }
                }
            }
            return r;
        }

        public String toString() {
            return "PARAGRAPH";
        }
    }
}
