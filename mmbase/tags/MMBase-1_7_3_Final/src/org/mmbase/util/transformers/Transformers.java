/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Utitilies.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 */

public class Transformers {
    private static final Logger log = Logging.getLoggerInstance(Transformers.class);


    /**
     * This method instatiates a CharTransformer by use of reflection.
     */

    public static CharTransformer getCharTransformer(String name, String config, String errorId, boolean back) {
        Class clazz;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException ex) {
            log.error("Class " + name + " speficifed for " + errorId + " could not be found");
            return null;
        }
        if (! CharTransformer.class.isAssignableFrom(clazz)) {
            log.error("The class " + clazz + " specified for "  + errorId + " is not a CharTransformer");
            return null;
        }
        CharTransformer ct;
        try {
            ct = (CharTransformer) clazz.newInstance();
        } catch (Exception ex) {
            log.error("Error instantiating a " + clazz + ": " + ex.toString());
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
                        conf = clazz.getDeclaredField(config).getInt(null);
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

}
