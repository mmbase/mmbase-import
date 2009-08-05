/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.applications.editwizard.action;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.applications.editwizard.session.SessionData;
import org.mmbase.applications.editwizard.util.HttpUtil;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.URIResolver;

public class ActionUtils {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(ActionUtils.class.getName());

    /**
     * get stacktrace information of exception
     * @param throwable
     * @return
     */
    public static String getStackTrace(Throwable throwable) {
        CharArrayWriter caw = new CharArrayWriter();
        throwable.printStackTrace(new PrintWriter(caw));

        caw.flush();
        String stacktrace = caw.toString();
        caw.close();

        return stacktrace;
    }

    /**
     * convert string into escaped HTML code��
     * '&amp;'->'&amp;amp'
     * '&lt;'->'&amp;lt;'
     * '&gt;'->'&amp;gt;'
     * '&quot;'->'&amp;quot;'
     * '\r\n'->'&lt;br /&gt'
     * @param value the string to be convert
     * @return the string converted��
     */
    public static String escapeForHTML(String value) {
        if (value == null) {
            return value;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i=0;i<value.length();i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"': // '"'-->'&quot;' (&#34;)
                    buffer.append("&quot;");
                    break;
                case '&': // '&'-->'&amp;'  (&#38;)
                    buffer.append("&amp;");
                    break;
                case '<': // '<'-->'&lt;'   (&#60;)
                    buffer.append("&lt;");
                    break;
                case '>': // '>'-->'&gt;'   (&#62;)
                    buffer.append("&gt;");
                    break;
                case (char)0x0D : //return '\r\n'->'<br/>'
                    if (value.charAt(i+1) == (char)0x0A) {
                        i++;
                    }
                case (char)0x0A : //new line
                    buffer.append("<br/>");
                    break;
                default :
                    buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    private final static String PROTOCOL = "http://";

    public static String getBackPage(HttpServletRequest request) {
        String referrer = request.getParameter("referrer");
        if (referrer == null) {
            referrer = "";
        }
        referrer = referrer.replace('\\', '/');

        // this translations seems to be needed by some windows setups
        String backpage = org.mmbase.util.Encode.decode("ESCAPE_URL_PARAM", referrer);

        if ("".equals(backpage)) {
            log.debug("No backpage getting from header");
            backpage = request.getHeader("Referer");
        }
        if (backpage == null) {
            log.debug("No backpage, setting to ''");
            backpage = "";
        }
        return backpage;
    }

    public static URIResolver getURIResolver(HttpServletRequest request, SessionData sessionData) throws MalformedURLException, WizardException {

        if (sessionData==null) {
            sessionData = new SessionData();
        }

        String backpage = sessionData.getBackPage();
        if (backpage==null) {
            backpage = getBackPage(request);
        }

        String language = HttpUtil.getParam(request,"language",sessionData.getLanguage());
        URIResolver.EntryList extraDirs = new URIResolver.EntryList();

        /* Determin the 'referring' page, and add its directory to the URIResolver.
           That means that xml can be placed relative to this page, and xsl's int xsl-dir.
         */
        URL ref;
        // capture direct reference of http:// and https:// referers
        int protocolPos= backpage.indexOf(PROTOCOL);

        if (protocolPos >=0 ) { // given absolutely
            String path = new URL(backpage).getPath();
            ref = new URL(getResource(path.substring(request.getContextPath().length())), ".");
            // TODO: What if it happened to be not from the same server?
        } else {
            // Was given relatively, that's trickie, because cannot use URL object to take of query.
            String bp = backpage;
            int questionPos = bp.indexOf('?');
            if (questionPos != -1) {
                bp = bp.substring(0, questionPos);
            }
            URL path = getResource(bp);

            if (path != null) {
                ref = new URL(path, ".");
            } else {
                ref = null;
            }
        }
        if (ref != null) {
            if (! language.equals("")) {
                URL refi18n = new URL(ref, "i18n/" + language);
                if (getResource(refi18n.getPath()) != null) {
                    extraDirs.add("refi18n:", refi18n);
                }
            }
            extraDirs.add("ref:", ref);
        } else {
            log.warn("" + ref + " does not exist");
        }

        String templates = sessionData.getTemplates();
        if (templates != null) {
            URL templatesDir = getResource(templates);
            if (templatesDir == null) {
                throw new WizardException("" +  templates + " does not exist");
            }
            extraDirs.add("templates:", templatesDir);
        }

        /**
         * Then of course also the directory of editwizard installation must be added. This will allow for the 'basic' xsl's to be found,
         * and also for 'library' editors.
         */

        URL jspFileDir = new URL(getResource(request.getServletPath()), "."); // the directory of this jsp (list, wizard)
        URL basedir    = new URL(jspFileDir, "../data/");                      // ew default data/xsls is in ../data then

        if (! language.equals("")) {
            URL i18n = new URL(basedir, "i18n/" + language + "/");
            if (i18n == null) {
                if (! "en".equals(language)) { // english is default anyway
                    log.warn("Tried to internationalize the editwizard for language " + language + " for which support is lacking (" + i18n + " is not an existing directory)");
                }
            } else {
                extraDirs.add("i18n:", i18n);
            }
        }

        extraDirs.add("ew:", basedir);
        return new URIResolver(jspFileDir, extraDirs);
    }

    public static URL getResource(String path) {
        return ResourceLoader.getWebRoot().getResource(path);
    }

}
