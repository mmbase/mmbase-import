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
 * But it can be used more generally, to resolve 'URIs'.
 *
 * @author Michiel Meeuwissen.
 * @since  MMBase-1.6
 */

public class URIResolver implements javax.xml.transform.URIResolver {
    
    private static Logger log = Logging.getLoggerInstance(URIResolver.class.getName());

    private File    cwd;
    private File    extra_base_dir = null;
    private String  extra_prefix = null;

    /**
     * Create an URIResolver for a certain directory.
     * 
     */

    public URIResolver(File c) {
        this(c, null, null);
    }
    
    /**
     * Still experimental.
     */
    public URIResolver(File c, File b, String p) {
        log.debug("Creating URI Resolver for " + c);
        cwd                = c;
        extra_base_dir     = b;
        extra_prefix       = p;
    }
    /**
     * Returns the working directory which was supplied in the constructor.
     *
     */
    
    public File getCwd() {
        return cwd;
    }

    public File resolveToFile(String href, String base) {       
        if (log.isDebugEnabled()) {
            log.debug("Using resolver of " + cwd.toString() + " href: " + href + "   base: " + base);

        }
        File path;           
        if (base == null  // 'base' is often 'null', but happily, this object knows about cwd itself.
            || base.endsWith("javax.xml.transform.stream.StreamSource"))  {
            base = cwd.toString() + File.separator + "A_STREAM_SOURCE_NOT_A_FILE";
        }
        if (href.startsWith("mm:")) {
            path = new File(MMBaseContext.getConfigPath(), href.substring(3));
        } else if (extra_prefix != null && href.startsWith(extra_prefix)) {
            path = new File(extra_base_dir, href.substring(extra_prefix.length()));
        } else {
            if (href.startsWith("file:")) {
                href = href.substring(5);
            }            
            path = new File(href);
            
            if (! path.isAbsolute()) {
                if (base.startsWith("file://")) {
                    path = new File(new File(base.substring(7)).getParent(), href);
                } else {
                    path = new File(cwd, href); // look in cwd.
                }
                if (! path.isFile()) { // still no file?
                    if (log.isDebugEnabled()) log.debug(path.toString() + "does not exist, trying defaults");
                    if (extra_base_dir != null) {
                        path = new File(extra_base_dir, href);
                    }
                    if (! path.isFile()) { // even not found in extra dir? Try mmbase config dir.
                        path = new File(MMBaseContext.getConfigPath(), href);
                    }
                }
            }
        }
        if (log.isDebugEnabled()) log.debug("using " + path.toString());
        return path;
    }

    /**
     * Implementation of the resolve method.
     * 
     **/
    
    public Source resolve(String href,  String base) throws javax.xml.transform.TransformerException {
        return new javax.xml.transform.stream.StreamSource(resolveToFile(href,base));
    }

}
