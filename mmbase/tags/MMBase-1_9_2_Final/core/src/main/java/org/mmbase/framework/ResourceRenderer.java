/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.net.*;
import java.io.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A {@link Renderer} implementation based on an MMBase's {@link org.mmbase.util.ResourceLoader}. Blocks rendered with this,
 * cannot have parameters.

 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class ResourceRenderer extends AbstractRenderer {
    private static final Logger log = Logging.getLoggerInstance(ResourceRenderer.class);


    protected String resource;
    protected String resourceType = "web";
    protected String xsl = null;
    protected boolean decorate = false;


    public ResourceRenderer(Type t, Block parent) {
        super(t, parent);
    }

    public void setResource(String r) {
        resource = r;
    }


    public void setType(String t) {
        resourceType = t;
    }

    public void setXslt(String x) throws MalformedURLException {
        xsl = x;
    }

    public void setDecorate(boolean d) {
        decorate = d;
    }


    private String getResource() {
        if (resourceType.equals("web")) {
            return resource.charAt(0) == '/' ? resource : JspRenderer.JSP_ROOT + getBlock().getComponent().getName() + '/' + resource;
        } else {
            return resource;
        }
    }

    @Override public void render(Parameters blockParameters,
                                 Writer w, RenderHints hints) throws FrameworkException {


        if (decorate) {
            try {
                decorateIntro(hints, w, null);
            } catch (IOException ioe) {
                throw new FrameworkException(ioe);
            }
        }
        String name = getResource();
        ResourceLoader loader = ResourceLoader.Type.valueOf(resourceType.toUpperCase()).get();
        try {
            InputStream is = loader.getResourceAsStream(name);
            if (is == null) throw new FrameworkException("No such resource " +  loader.getResource(name) + " in " + loader);
            if (xsl == null) {
                Reader r = loader.getReader(is, name);
                char[] buf = new char[1000];
                int c;
                while ((c = r.read(buf, 0, 1000)) > 0) {
                    w.write(buf, 0, c);
                }
            } else {
                /// convert using the xsl and spit out that.
                URL x = ResourceLoader.getConfigurationRoot().getResource(xsl);
                Utils.xslTransform(blockParameters, loader.getResource(name), is, w, x);
            }
        } catch (IOException ioe) {
            throw new FrameworkException(ioe);
        } catch (javax.xml.transform.TransformerException te) {
            throw new FrameworkException(te.getMessage(), te);
        } catch (RuntimeException e) {
            log.debug(e.getMessage(), e);
            throw e;
        } finally {
            if (decorate) {
                try {
                    decorateOutro(hints, w);
                } catch (IOException ioe) {
                    throw new FrameworkException(ioe);
                }
            }
        }
    }


    @Override
    public String toString() {
        return resource;
    }

    @Override
    public URI getUri() {
        try {
            ResourceLoader loader = ResourceLoader.Type.valueOf(resourceType.toUpperCase()).get();
            return loader.getResource(getResource()).toURI();
        } catch (URISyntaxException use) {
            log.warn(use);
            return null;
        }
    }

}
