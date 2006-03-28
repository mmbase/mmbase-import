/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.editor;

import java.io.*;
import java.util.*;

import org.mmbase.bridge.*;
import javax.servlet.jsp.PageContext;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is an example implementation of the EditTag. It extends Editor.
 * To create your own editor with the edittag you could or rather should also
 * extend Editor.<br />
 * BasicEditor accepts two parameters url and icon and returns a link with a nodenr
 * of the very first field the edittag encounters, with an icon to click on.
 *
 * @author Andr&eacute; van Toly
 * @version $Id: BasicEditor.java,v 1.5 2006-03-28 22:33:06 michiel Exp $
 * @see EditTag
 * @see Editor
 * @since MMBase-1.8
 */

public class BasicEditor extends Editor {

    private static final Logger log = Logging.getLoggerInstance(BasicEditor.class);

    private static final FunctionProvider patterns = PatternNodeFunctionProvider.getInstance();

    private static final Parameter[] PARAMS = new Parameter[] {
        new Parameter("url", String.class, "/mmbase/edit/basic/"),
        new Parameter("urlparams", Map.class, null),
        new Parameter("icon", String.class, ""),
        new Parameter("iconparams", Map.class, null),
        new Parameter("when", String.class, "always"),
        Parameter.CLOUD
    };


    protected Parameter[] getParameterDefinition() {
        return PARAMS;
    }


    /**
     * Makes html with a link to a node in an editor using the parameters url and icon.
     *
     * @param   context The PageContext
     */
    public void getEditorHTML(PageContext context) throws IOException {

        String nodenr = "";
        if (!nodenrList.isEmpty()) {    // get the first node from this list to edit
            nodenr = (String) nodenrList.get(0);
        }

        makeHTML(nodenr, context);

    }
    /**
     * Fills parameters of the parameters to be interpreted as PatternNodeFunctions
     */
    protected String getValue(String param, Cloud cloud, String nodenr, PageContext context) {
           Function urlFunction = patterns.getFunction(parameters.getString(param));
           Parameters urlParameters = urlFunction.createParameters();
           if (cloud != null) {
               Node node = cloud.getNode(nodenr);
               urlParameters.set(Parameter.NODE, node);
           }
           urlParameters.setAll((Map) parameters.get(param + "params"));
           urlParameters.setIfDefined(Parameter.REQUEST, context.getRequest());
           urlParameters.setIfDefined(Parameter.RESPONSE, context.getResponse());
           return (String) urlFunction.getFunctionValue(urlParameters);
    }
    /**
    * Creates a string with the link (and icon) to the editor
    *
    * @param url        An url to an editor
    * @param icon       An url to a graphic file
    * @param nodenr     The nodenumber to edit
    * @return           An HTML string with a link suitable for the editor yammeditor.jsp
    *
    */
    protected void makeHTML(String nodenr, PageContext context) throws IOException {
        String when =  parameters.getString("when");

        if ("always".equals(when) || "true".equals(context.getRequest().getParameter("edit"))) {
            Cloud cloud = (Cloud) parameters.get(Parameter.CLOUD);

            String url = getValue("url", cloud, nodenr, context);
            String icon = getValue("icon", cloud, nodenr, context);
            url = makeRelative(url, context);
            Writer html = context.getOut();
            html.write("<div class=\"et\"><a title=\"click to edit\" href=\"");
            html.write(url);
            html.write("\" onclick=\"window.open(this.href); return false;\">");
            if (! icon.equals("")) {
                icon = makeRelative(icon, context);

                html.write("<img src=\"");
                html.write(icon);
                html.write("\" alt=\"edit\">");
            } else {
                html.write("[edit]");
            }
            html.write("</a></div>");
        }
    }

    /**
     * Copied this method from the UrlTag.
     * If it would be nice that an URL starting with '/' would be generated relatively to the current request URL, then this method can do it.
     * If the URL is not used to write to (this) page, then you probably don't want that.
     *
     * The behaviour can be overruled by starting the URL with two '/'s.
     *
     */
    protected String makeRelative(String url, PageContext pageContext) {
        StringBuffer show = new StringBuffer(url);
        javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();
        if (show.charAt(0) == '/') { // absolute on servletcontex
            if (show.length() > 1 && show.charAt(1) == '/') {
                log.debug("'absolute' url, not making relative");
                show.deleteCharAt(0);
                show.insert(0, req.getContextPath());

            } else {
                log.debug("'absolute' url");
                String thisDir = new java.io.File(req.getServletPath()).getParent();
                show.insert(0,  org.mmbase.util.UriParser.makeRelative(thisDir, "/")); // makes a relative path to root.
            }
        }
        return show.toString();
    }

}
