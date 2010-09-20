package org.mmbase.util.functions;

import java.util.*;
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

    /**
     * @since MMBase-2.0
     */
    public static List<String> parse(String fields) {
        int commapos =  0;
        int nested =  0;
        List<String> v = new ArrayList<String>();
        int i;
        if (log.isDebugEnabled()) {
            log.debug("Fields=" + fields);
        }
        for(i = 0; i < fields.length(); i++) {
            if ((fields.charAt(i) == ',') || (fields.charAt(i) == ';')){
                if(nested == 0) {
                    v.add(fields.substring(commapos,i).trim());
                    commapos = i + 1;
                }
            }
            if (fields.charAt(i) == '(') {
                nested++;
            }
            if (fields.charAt(i) == ')') {
                nested--;
            }
        }
        if (i > 0) {
            v.add(fields.substring(commapos).trim());
        }
        return v;
    }

}
