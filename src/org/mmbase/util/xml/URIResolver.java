/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import javax.xml.transform.Source;
import java.io.File;
import org.mmbase.module.core.MMBaseContext;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This URIResolver knows two things: The current dir under interest,
 * and the configuration directory of MMBase.
 *
 * It supports URI's beginning with 'mm:', which stands for the MMBase
 * configuration directory. Of course URI's relative to the current
 * working directory are working too.
 *
 * An URIResolver like this can e.g. be given to a
 * javax.xml.transform.TransformerFactory, and it knows how to resolve
 * these kinds of URI's, e.g. when 'xsl:import' is used.
 *
 * @author Michiel Meeuwissen.
 */

public class URIResolver implements javax.xml.transform.URIResolver {
    
    private static Logger log = Logging.getLoggerInstance(URIResolver.class.getName());

    private File  cwd;

    /**
     * Create an URIResolver for a certain directory.
     * 
     */

    public URIResolver(File c) {
        log.debug("Creating URI Resolver");
        cwd          = c;
    }

    /**
     * Returns the working directory which was supplied in the constructor.
     *
     */
    
    public File getCwd() {
        return cwd;
    }

    /**
     * Implementation of the resolve method.
     * 
     **/
    
    public Source resolve(String href,  String base) throws javax.xml.transform.TransformerException {
        if (log.isDebugEnabled()) {
            log.debug("Using resolver of " + cwd.toString() + " href: " + href + "   base: " + base);

        }
        File path;           
        if (base == null  // 'base' is often 'null', but happily, this object knows about cwd itself.
            || base.endsWith("javax.xml.transform.stream.StreamSource"))  {
            base = cwd.toString() + File.separator + "doesntmatter";
        }
        if (href.startsWith("mm:")) {
            path = new File(MMBaseContext.getConfigPath(), href.substring(3));
        } else {
            path = new File(href);
            if (! path.isAbsolute()) {
                if (base.startsWith("file://")) {
                    path = new File(new File(base.substring(7)).getParent(), href);
                } else {
                    path = new File(cwd, href);
                }
                if (! path.isFile()) {
                    if (log.isDebugEnabled()) log.debug(path.toString() + "does not exist, trying default");
                    path = new File(MMBaseContext.getConfigPath(), href);
                }
            }
        }
        if (log.isDebugEnabled()) log.debug("using " + path.toString());
        return new javax.xml.transform.stream.StreamSource(path);
    }
        
}
