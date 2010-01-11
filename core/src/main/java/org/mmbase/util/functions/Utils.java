package org.mmbase.util.functions;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Several utility methods.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public final class Utils {

    private static final Logger log = Logging.getLoggerInstance(Utils.class);


    public static String getFileItemName(@Name("fileName") String fileName) {
        if (fileName == null) return null;
        // some browers provide directory information. Take that away.
        int pos = fileName.lastIndexOf("\\");
        if (pos > 0) {
            fileName = fileName.substring(pos + 1);
        }
        pos = fileName.lastIndexOf("/");
        if (pos > 0) {
            fileName = fileName.substring(pos + 1);
        }
        return fileName;

    }

}
