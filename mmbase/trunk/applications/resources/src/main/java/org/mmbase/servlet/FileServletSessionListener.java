/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.util.*;

import javax.servlet.http.*;

import java.io.*;

import org.mmbase.util.logging.*;


/**

 *
 * @version $Id: FileServlet.java 38851 2009-09-25 08:28:12Z michiel $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.9.2
 */
public class FileServletSessionListener implements HttpSessionListener {
    private static final Logger log = Logging.getLoggerInstance(FileServletSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // nothing to
        log.debug("Created " + se.getSession());
    }
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // delete files associated with this session
        log.debug("Destroyed " + se.getSession());
        HttpSession session = se.getSession();
        Set<File> protectedFiles = (Set<File>) session.getAttribute(FileServlet.PROTECTED_FILES);
        if (protectedFiles != null) {
            for (File f : protectedFiles) {

                f.delete();
                File sessionFile = FileServlet.getSessionFile(f);
                sessionFile.delete();
                log.debug("Deleted " + f + " (" + sessionFile + ")");
            }
        }
    }

}


