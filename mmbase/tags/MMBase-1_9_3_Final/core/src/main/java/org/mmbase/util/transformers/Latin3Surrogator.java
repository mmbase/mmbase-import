/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The ISO-8859-3 ('South European') unibyte encoding is used for languages like maltese and
 * esperanto. If characters from this set are missing on your presentation device, this Transformer can
 * provide (ASCII) surrogates for a bunch of characters.
 * 
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id$
 */
public class Latin3Surrogator extends ConfigurableReaderTransformer implements CharTransformer {
    private static final long serialVersionUID = 0L;
    private static Logger log = Logging.getLoggerInstance(Latin3Surrogator.class);

    // esperanto 
    public static final int XMETODO   = 1;  // faru iksojn
    public static final int HMETODO   = 2;  // faru hojn

    public Latin3Surrogator() {
        super();
    }
    public Latin3Surrogator(int c) {
        super(c);
    }
      
    protected Writer iksoj(Reader r, Writer w) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                switch(c) {
                case '\u0108': w.write("Cx"); break;
                case '\u0109': w.write("cx"); break;
                case '\u011C': w.write("Gx"); break;
                case '\u011D': w.write("gx"); break;
                case '\u0124': w.write("Hx"); break;
                case '\u0125': w.write("hx"); break;
                case '\u0134': w.write("Jx"); break;
                case '\u0135': w.write("jx"); break;
                case '\u015C': w.write("Sx"); break;
                case '\u015D': w.write("sx"); break;
                case '\u016C': w.write("Ux"); break;
                case '\u016D': w.write("ux"); break;
                default: w.write(c);
                }
            }
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }

    protected Writer hoj(Reader r, Writer w) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                switch(c) {
                case '\u0108': w.write("Ch"); break;
                case '\u0109': w.write("ch"); break;
                case '\u011C': w.write("Gh"); break;
                case '\u011D': w.write("gh"); break;
                case '\u0124': w.write("Hh"); break;
                case '\u0125': w.write("hh"); break;
                case '\u0134': w.write("Jh"); break;
                case '\u0135': w.write("jh"); break;
                case '\u015C': w.write("Sh"); break;
                case '\u015D': w.write("sh"); break;
                case '\u016C': w.write('U'); break;
                case '\u016D': w.write('u'); break;
                default: w.write(c);
                }
            }
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }

    public Writer transform(Reader r, Writer w) {
        switch(to){
        case XMETODO: return iksoj(r, w);
        case HMETODO: return hoj(r, w);
        default: throw new UnknownCodingException(getClass(), to);
        }
    }

    public String getEncoding() {
        switch(to){
        case XMETODO: return "xmetodo";
        case HMETODO: return "hmetodo";
        default: throw new UnknownCodingException(getClass(), to);
        }
    }


    public Map<String,Config> transformers() {
        Map<String,Config> h = new HashMap<String,Config>();
        h.put("xmetodo".toUpperCase(), new Config(getClass(), XMETODO));
        h.put("hmetodo".toUpperCase(), new Config(getClass(), HMETODO));
        return h;
    }

}

