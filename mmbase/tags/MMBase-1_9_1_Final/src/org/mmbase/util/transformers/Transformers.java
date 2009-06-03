/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.Iterator;
import org.mmbase.util.functions.Parameters;
import org.mmbase.datatypes.processors.Processor;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Utitilies related to the tranformers of this package.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 */

public class Transformers {
    private static final Logger log = Logging.getLoggerInstance(Transformers.class);


    /**
     * This method instatiates a CharTransformer by use of reflection.
     * @param name   The class name for the CharTransformer to be returned
     * @param config A configuration string for this transformer. At the moment this can be parsed
     *               as an integer, or the name of a integer constant of the transformer's class.
     *               Likely, other ways to configure a transformer will be available.
     * @param errorId  If something goes wrong, an error message is logged, in which this String is
     *                 used, to clear things up.
     * @param back   If true, the Transformer will be wrapped in a InverseCharTransformer, so the
     *               transformation will do the inverse thing.
     * @return A CharTransformer instance or null in case of an error.
     */

    public static CharTransformer getCharTransformer(String name, String config, String errorId, boolean back) {
        Class<?> clazz;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException ex) {
            log.error("Class " + name + " specified for " + errorId + " could not be found");
            return null;
        }

        if (! Transformer.class.isAssignableFrom(clazz) &&
            ! ParameterizedTransformerFactory.class.isAssignableFrom(clazz) &&
            ! Processor.class.isAssignableFrom(clazz)) {
            log.error("The class " + clazz + " specified for "  + errorId + " is not a Transformer (or easily converted to that)");
            return null;
        }
        Object t;
        try {
            t = clazz.newInstance();
        } catch (Exception ex) {
            log.error("Error instantiating a " + clazz + ": " + ex.toString());
            return null;
        }
        if (t instanceof ParameterizedTransformerFactory) {
            ParameterizedTransformerFactory pt = (ParameterizedTransformerFactory) t;
            Parameters params = pt.createParameters();
            Iterator<String> it = org.mmbase.util.StringSplitter.split(config).iterator();
            int i = 0;
            while (it.hasNext()) {
                params.set(i++, it.next());
            }
            config = "";
            t = pt.createTransformer(params);
        } else if (t instanceof Processor) {
            t = new ProcessorCharTransformer((Processor) t);
        }
        CharTransformer ct;

        if (t instanceof CharTransformer) {
            ct = (CharTransformer) t;
        } else if (t instanceof ByteToCharTransformer) {
            log.debug("making a ByteCharTransformer");
            ct = new ByteCharTransformer((ByteToCharTransformer) t);
        } else {
            log.error("The class " + clazz + " specified for "  + errorId + " is not a CharTransformer or a ByteToCharTransformer");
            return null;
        }

        if (config == null) config = "";
        if (ct instanceof ConfigurableTransformer) {
            log.debug("Trying to configure with '" + config + "'");
            if (! config.equals("")) {
                int conf;
                try {
                    log.debug("With int");
                    conf = Integer.parseInt(config);
                } catch (NumberFormatException nfe) {
                    try {
                        log.debug("With static field");
                        conf = clazz.getField(config).getInt(null);
                    } catch (Exception nsfe) {
                        log.error("Type " + errorId + " is not well configured : " + nfe.toString() + " and " + nsfe.toString());
                        return null;
                    }
                }
                ((ConfigurableTransformer) ct).configure(conf);
            }
        } else {
            if (! config.equals("")) {
                log.warn("Tried to configure non-configurable transformer " + name);
            }
        }
        if (back) {
            ct = new InverseCharTransformer(ct);
        }
        return ct;
    }



    /**
     * @since MMBase-1.8
     */

    public static ParameterizedTransformerFactory getTransformerFactory(String name, String errorId) {
        Class<?> clazz;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException ex) {
            log.error("Class " + name + " specified for " + errorId + " could not be found");
            return null;
        }
        if (! ParameterizedTransformerFactory.class.isAssignableFrom(clazz)) {
            log.error("The class " + clazz + " specified for "  + errorId + " is not a ParamerizedTransformerFactory");
            return null;
        }
        ParameterizedTransformerFactory fact;
        try {
            fact = (ParameterizedTransformerFactory) clazz.newInstance();
        } catch (Exception ex) {
            log.error("Error instantiating a " + clazz + ": " + ex.toString());
            return null;
        }

        return fact;
    }

}
