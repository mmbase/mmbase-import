/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import java.net.*;
import java.io.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import org.mmbase.util.functions.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A Renderer implementation based on an external connection.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ConnectionRenderer.java,v 1.2 2008-02-20 18:10:33 michiel Exp $
 * @since MMBase-1.9
 */
public class ConnectionRenderer extends AbstractRenderer {
    private static final Logger log = Logging.getLoggerInstance(ConnectionRenderer.class);


    protected URL url;
    protected int timeOut = 2000;
    protected String xsl = null;

    public ConnectionRenderer(String t, Block parent) {
        super(t, parent);
    }

    public void setUrl(String u) throws MalformedURLException {
        url = new URL(u);
    }

    public void setXslt(String x) throws MalformedURLException {
        xsl = x;
    }
    public void setTimeOut(int t) {
        timeOut = t;
    }


    public  Parameter[] getParameters() {
        return new Parameter[] {};
    }

    protected void output(InputStream inputStream, Writer w) {
    }


    public void render(Parameters blockParameters, Parameters frameworkParameters,
                       Writer w, WindowState state) throws FrameworkException {


        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeOut);
            connection.setReadTimeout(timeOut);
            int responseCode = connection.getResponseCode();
            String contentType = connection.getContentType();
            InputStream inputStream = connection.getInputStream();
            if (responseCode == 200) {
                if (xsl == null) {
                    String encoding = GenericResponseWrapper.getEncoding(contentType);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, encoding));
                    char[] buf = new char[1000];
                    int c;
                    while ((c = r.read(buf, 0, 1000)) > 0) {
                        w.write(buf, 0, c);
                    }
                } else {
                    /// convert using the xsl and spit out that.
                    Source xml = new StreamSource(inputStream);
                    URL x = ResourceLoader.getConfigurationRoot().getResource(xsl);

                    Result res = new StreamResult(w);
                    XSLTransformer.transform(xml, x, res, new HashMap<String, Object>());
                }


            } else {
                throw new FrameworkException("" + responseCode);
            }
        } catch (java.net.ConnectException ce) {
            throw new FrameworkException(ce.getMessage(), ce);
        } catch (java.net.SocketTimeoutException ste) {
            throw new FrameworkException(ste.getMessage(), ste);
        } catch (IOException ioe) {
            throw new FrameworkException(ioe.getMessage(), ioe);
        } catch (javax.xml.transform.TransformerException te) {
            throw new FrameworkException(te.getMessage(), te);
        }

    }


    public String toString() {
        return url.toString();
    }

    public java.net.URI getUri() {
        try {
            return url.toURI();
        } catch (URISyntaxException use) {
            log.warn(use);
            return null;
        }
    }
}
