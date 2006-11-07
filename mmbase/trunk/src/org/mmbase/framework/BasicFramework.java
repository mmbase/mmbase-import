/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.util.*;
import org.mmbase.util.*;
import java.io.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.util.functions.*;
import org.mmbase.util.transformers.Url;
import org.mmbase.util.transformers.CharTransformer;

/**
 * The framework that does nothing, besides adding the block-parameters to the URL. No support for
 * conflicting block parameters.
 *
 * @author Michiel Meeuwissen
 * @version $Id: BasicFramework.java,v 1.8 2006-11-07 18:55:03 michiel Exp $
 * @since MMBase-1.9
 */
public class BasicFramework implements Framework {
    private static final CharTransformer paramEscaper = new Url(Url.ESCAPE);

    public String getName() {
        return "BASIC";
    }

    /**
     * General utility function to create an Url
     */
    public static StringBuilder getUrl(String page, Map<String, ? extends Object> params, HttpServletRequest req, boolean writeamp) {
        StringBuilder show = new StringBuilder();
        if (writeamp) {
            page = page.replaceAll("&", "&amp;");
        }
        if (page.equals("")) { // means _this_ page
            String requestURI = req.getRequestURI();
            if (requestURI.endsWith("/")) {
                page = ".";
            } else {
                page = new File(requestURI).getName();
            }
        }
        show.append(page);

        if (params != null && ! params.isEmpty()) {
            // url is now complete up to query string, which we are to construct now
            String amp = (writeamp ? "&amp;" : "&");
            String connector = (show.indexOf("?") == -1 ? "?" : amp);

            Writer w = new StringBuilderWriter(show);
            for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                if (value != null && Casting.isStringRepresentable(value.getClass())) { // if not string representable, that suppose it was an 'automatic' parameter which does need presenting on url
                    show.append(connector).append(entry.getKey()).append("=");
                    paramEscaper.transform(new StringReader(Casting.toString(value)), w);
                    connector = amp;
                }
            }
        }
        return show;
    }

    public StringBuilder getUrl(String page, Renderer renderer, Component component, Parameters blockParameters, Parameters frameworkParameters) {
        return getUrl(page, component, blockParameters, frameworkParameters, false);
    }

    public StringBuilder getUrl(String page, Processor processor, Component component, Parameters blockParameters, Parameters frameworkParameters) {
        return getUrl(page, component, blockParameters, frameworkParameters, false);
    }


    public StringBuilder getUrl(String page, Component component, Parameters blockParameters, Parameters frameworkParameters, boolean writeamp) {
        if (component == null) {
            // just generate the URL
            HttpServletRequest req = frameworkParameters.get(Parameter.REQUEST);
            StringBuilder sb = getUrl(page, blockParameters.toMap(), req, writeamp);
            return sb;
        } else {
        }
    }

    public Block getBlock(Component component, String blockName) {
        return component.getBlock(blockName);
    }

    public Parameters createFrameworkParameters() {
        return new Parameters(Parameter.REQUEST);
    }

    public boolean makeRelativeUrl() {
        return false;
    }
}
