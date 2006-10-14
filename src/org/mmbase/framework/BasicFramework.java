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
import org.mmbase.util.functions.*;
import org.mmbase.util.transformers.Url;
import org.mmbase.util.transformers.CharTransformer;

/**
 * The framework that does nothing, besides adding the block-parameters to the URL. No support for
 * conflicting block parameters.
 *
 * @author Michiel Meeuwissen
 * @version $Id: BasicFramework.java,v 1.2 2006-10-14 16:08:06 johannes Exp $
 * @since MMBase-1.9
 */
public class BasicFramework implements Framework {
    private static final CharTransformer paramEscaper = new Url(Url.ESCAPE);

    public String getName() {
        return "BASIC";
    }


    public String getUrl(String page, Component component, Parameters blockParameters, Parameters frameworkParameters, boolean writeamp, boolean encodeUrl) {
        return getUrl(page, null, component, blockParameters, frameworkParameters, writeamp, encodeUrl);
    }

    public String getUrl(String page, Renderer renderer, Component component, Parameters blockParameters, Parameters frameworkParameters, boolean writeamp, boolean encodeUrl) {
        StringBuffer show = new StringBuffer();
        Writer w = new StringBufferWriter(show);
        if (writeamp) {
            page = page.replaceAll("&", "&amp;");
        }
        javax.servlet.http.HttpServletRequest req = frameworkParameters.get(Parameter.REQUEST);
        if (page.equals("")) { // means _this_ page
            String requestURI = req.getRequestURI();
            if (requestURI.endsWith("/")) {
                page = ".";
            } else {
                page = new File(requestURI).getName();
            }
        }
        show.append(page);

        // url is now complete up to query string, which we are to construct now
        String amp = (writeamp ? "&amp;" : "&");
        String connector = (show.toString().indexOf('?') == -1 ? "?" : amp);

        for (Map.Entry<String, ? extends Object> entry : blockParameters.toMap().entrySet()) {
            show.append(connector).append(entry.getKey()).append("=");
            paramEscaper.transform(new StringReader(Casting.toString(entry.getValue())), w);
            connector = amp;
        }
        if (encodeUrl) {
            javax.servlet.http.HttpServletResponse response = frameworkParameters.get(Parameter.RESPONSE);
            return response.encodeURL(show.toString());
        } else {
            return show.toString();
        }
    }
    public Parameters createFrameworkParameters() {
        return new Parameters(Parameter.RESPONSE, Parameter.REQUEST);
    }

    public boolean makeRelativeUrl() {
        return false;
    }
}
