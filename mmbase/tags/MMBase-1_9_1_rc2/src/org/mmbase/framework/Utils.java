/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.io.*;
import java.util.*;
import java.net.URL;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import javax.xml.transform.Result;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Static utilitiy methods which are related to (combine functionality of)  other classes in the packages.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public abstract class Utils {
    private static final Logger log = Logging.getLoggerInstance(Utils.class);

    private Utils() {
        // this class has no instances
    }

    /**
     * Renders a {@link Renderer} into a new {@link Document}. This assumes that the renderer
     * indeed produces XML. This is e.g. used to include blocks into editwizard task XML's.
     */
    public static Document renderToXml(Framework fw, Renderer renderer, Parameters blockParameters, Parameters frameworkParameters, WindowState state,
                                       Class<?> baseClass) throws FrameworkException {
        boolean validation = true;
        boolean xsd = false;
        Writer w = new StringWriter();
        fw.render(renderer, blockParameters, frameworkParameters, w, state);

        String xml = w.toString();
        if (log.isDebugEnabled()) {
            log.debug("Parsing " + xml + " of " +  renderer + " of " + renderer.getBlock());
        }
        InputSource source = new InputSource(new StringReader(xml));
        java.net.URI uri = renderer.getUri();
        if (uri != null) {
            source.setSystemId(uri.toString());
        }
        EntityResolver resolver = new org.mmbase.util.xml.EntityResolver(true, baseClass);
        DocumentBuilder dbuilder = org.mmbase.util.xml.DocumentReader.getDocumentBuilder(validation, xsd,
                                                                                         null/* default error handler */, resolver);
        if(dbuilder == null) throw new RuntimeException("failure retrieving document builder");
        try {
            Document doc = dbuilder.parse(source);
            return  doc;
        } catch (IOException ioe) {
            throw new FrameworkException(uri + ": " + ioe.getMessage(), ioe);
        } catch (SAXException se) {
            throw new FrameworkException(uri + ": " + se.getMessage(), se);
        }

    }

    /**
     * @since MMBase-1.9.1
     */
    public static void xslTransform(Parameters blockParameters, URL in, InputStream inputStream, Writer w, URL xsl) throws javax.xml.transform.TransformerException {
        /// convert using the xsl and spit out that.
        Source xml = new StreamSource(inputStream, in.toString());
        Result res = new StreamResult(w);

        Map<String, Object> params = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : blockParameters.toMap().entrySet()) {
            if (entry.getValue() != null) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Using from " + blockParameters + " " + params);
        }

        HttpServletRequest request = blockParameters.get(Parameter.REQUEST);
        if (request != null) {
            params.put("formatter_requestcontext",  request.getContextPath());
            Locale locale = (Locale) request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request");
            if (locale != null) {
                params.put("formatter_language", locale.getLanguage());
            }
            params.put("request", request);
        }
        XSLTransformer.transform(xml, xsl, res, params);
    }


    /**
     * @since MMBase-1.9.1
     */
    public static Parameters fixateParameters(Parameters parameters) {
        final Parameters myParameters = new Parameters(parameters);
        // CLone parameters, because after time-out they can otherwise be changed by client.
        // This stuff should problbably be moved to a method of Framework, stince Framework specific
        // stuff is happening.
        log.debug("" + parameters + " -> " + myParameters);
        HttpServletRequest req = myParameters.get(Parameter.REQUEST);//BasicUrlConverter.getUserRequest(myParameters.get(Parameter.REQUEST));
        if (req != null) {
            HttpServletRequest myreq = new LocalHttpServletRequest(org.mmbase.module.core.MMBaseContext.getServletContext(), "", req.getServletPath());
            log.debug("" + req.getServletPath());
            for (Object attrName : Collections.list(req.getAttributeNames())) {
                String a = (String) attrName;
                if (a.equals(org.mmbase.framework.basic.State.KEY)) {
                    new org.mmbase.framework.basic.State(myreq, (org.mmbase.framework.basic.State) req.getAttribute(org.mmbase.framework.basic.State.KEY));
                } else {
                    myreq.setAttribute(a, req.getAttribute(a));
                }
            }

            myreq.getParameterMap().putAll(req.getParameterMap());
            log.debug("atts " + Collections.list(myreq.getAttributeNames()));
            log.debug("params " + myreq.getParameterMap());
            myParameters.set(Parameter.REQUEST, myreq);
        }
        return myParameters;
    }

}
