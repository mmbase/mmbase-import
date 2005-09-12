/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.Vector;
import org.w3c.dom.DOMException;

import org.mmbase.module.core.MMBase;
import org.mmbase.util.xml.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.StringBufferLogger;

/**
 * @javadoc
 * @deprecated-now use {@link org.mmbase.util.xml.ApplicationWriter}
 * @author Pierre van Rooden
 * @version $Id: XMLApplicationWriter.java,v 1.28 2005-09-12 14:07:39 pierre Exp $
 */
public class XMLApplicationWriter extends ApplicationWriter {

    /**
     * Constructs the document writer.
     */
     public XMLApplicationWriter(ApplicationReader reader, MMBase mmbase) throws DOMException {
         super(reader, mmbase);
     }

    /**
     * @deprecated use ApplicationWriter.writeAll()
     */
    public static Vector writeXMLFile(ApplicationReader reader, String targetPath, String goal, MMBase mmbase) {
        ApplicationWriter appOut = new ApplicationWriter(reader, mmbase);
        appOut.setIncludeComments(true);
        StringBufferLogger logger = new StringBufferLogger();
        try {
            appOut.writeToPath(targetPath, logger);
        } catch (Exception e) {
            logger.error("Application export went wrong",e);
        }
        Vector messages = new Vector();
        messages.add(logger.getStringBuffer().toString());
        return messages;
    }

}
