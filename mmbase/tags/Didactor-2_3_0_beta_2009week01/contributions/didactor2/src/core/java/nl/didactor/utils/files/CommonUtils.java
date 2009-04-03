package nl.didactor.utils.files;

import java.io.File;

/**
 * @javadoc
 * @version $Id: CommonUtils.java,v 1.2 2007-04-24 16:09:01 michiel Exp $
 */
public class CommonUtils {
    /**
     * @javadoc
     */
    public static String fixPath(String sPath) {
        if (File.separator.equals("\\")) {
            //Something like Windows
            return sPath.replaceAll("/", File.separator + File.separator);
        } else { 
            //Something like Unix
            return sPath.replaceAll("\\\\", File.separator);
        }
        
    }
}
