/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import javax.xml.transform.Source;
import java.io.File;
import java.util.*;
import org.mmbase.module.core.MMBaseContext;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This URIResolver can be used to resolve URI's, also in TransformerFactory's. 
 * 
 * It has knowledge of a kind of path (as used by shells). Every entry
 * of this path is labeled with a 'prefix'.
 *
 * This path always has at least (and on default) two entries:

 <ol>
   <li> Current working directory (prefix: none or 'file:')</li>
   <li> MMBase configuration direcotry (prefix: 'mm:') </li>
 </ol>
 
 * Optionially you can add other dirs  between these two.
 *
 * When you start searching in the current working dir, and the URI
 * does not point to an existing file, it start searching downwards in
 * this list, until it finds a file that does exist.
 *
 * @author Michiel Meeuwissen.
 * @since  MMBase-1.6
 */

public class URIResolver implements javax.xml.transform.URIResolver {
    
    private static Logger log = Logging.getLoggerInstance(URIResolver.class.getName());

    private List     extraDirs;  // prefix -> File pairs
    private File     cwd;
    private int      hashCode;


    /**
     * This constructor does not create an actual object that can be
     * used. Only the hashCode is filled. This is because I liked it
     * possible a URIResolver to be equal to a File. But 'equals' must
     * be symmetric, and only a File can be equal to a File. It seemed
     * stupid to extend URIResolver from File, only for this. If you
     * want to compare a File to to an URIResolver (in Maps), you
     * could wrap the file in such an empty URIResolver, and avoid all
     * further overhead.
     *
     * @param cwd       The directory for which this URIResolver must (not) be created.
     * @param overhead  A boolean. It is ignored. It serves only to distinct this constructor from the other one.
     * @see org.mmbase.cache.xslt.FactoryCache
     */

    public URIResolver(File c, boolean overhead) {
        hashCode = c.hashCode();
    }
    /**
     * Create an URIResolver for a certain directory.
     * @param cwd  The directory for which this URIResolver must be created.     
     * 
     */

    public URIResolver(File c) {
        this(c, null);
    }

    /**
     * Besides the current working directory you can also supply an
     * ordered list of URIResolver.Entry's. First in this list are the
     * directories which must be checked first, in case no prefix is
     * given.
     * @param extradirs A List of URIResolver.Entry's, containing
     *                 'extra' dirs with prefixes.  If not specified or null, there will still
     *                  be one 'extra dir' available, namely the MMBase configuration
     *                  directory (with prefix mm:)
     */
    public URIResolver(File c, List extradirs) {
        if (log.isDebugEnabled()) log.debug("Creating URI Resolver for " + c);
        cwd = c;
        extraDirs = new Vector();
        if (extradirs != null) {
            // XXX: perhaps should throw an exception if not all content of ed are URIResolver.Entry's.
            extraDirs.addAll(extradirs);
        }
        extraDirs.add(new Entry("mm:", new File(MMBaseContext.getConfigPath())));

        // URIResolvers  cannot be changed, the hashCode can already be calculated and stored.

        if (extraDirs.size() == 1) { // only mmbase config, cannot change
            log.debug("getting hashCode " + cwd.hashCode());
            hashCode = cwd.hashCode(); 
            // if only the cwd is set, then you alternatively use the cwd has hashCode is this way.
            // it this way in these case it is easy to avoid constructing an URIResolver at all.
        } else {
            hashCode = (cwd.getAbsolutePath() + extraDirs.toString()).hashCode();
        }
    }

    /**
     * Returns the working directory which was supplied in the
     * constructor.
     *
     */
    
    public File getCwd() {
        return cwd;
    }

    /**
     * Resolves a given string to a File. 
     * 
     * @param href A string, which can be a relative Path or an URI
     *             starting with mm: or one of the other configured prefixes.
     * @return A File
     */

    public File resolveToFile(String href) {
        return resolveToFile(href, null);
    }

    /**
     * Creates a 'path' string, which is a list of directories. Mainly usefull for debugging, of course.
     * 
     * @return A String
     */
    public String getPath() {
        String result = ".";
        Iterator i = extraDirs.iterator();            
        while (i.hasNext()) {
            Entry entry = (Entry) i.next();
            result += File.pathSeparatorChar + entry.getDir().getAbsolutePath();
        }
        return result;        
    }

    /**
     * Resolves the string href (possible with use of base directory
     * 'base') to a File.  If href is a relative Path (without
     * prefix), and cannot be found in the cwd or 'base', then all
     * 'extra' dirs are tried, until it finds a file that exists. It
     * starts with first entry in these 'extradirs' List and ends with
     * the MMBase configuration directory.
     * 
     * @param href
     * @param base
     * @return A File
     * @see #resolveToFile
     */
    public File resolveToFile(String href, String base) {       
        if (log.isDebugEnabled()) {
            log.debug("Using resolver of " + cwd.toString() + " href: " + href + "   base: " + base);

        }
        File path = null;
        if (base == null  // 'base' is often 'null', but happily, this object knows about cwd itself.
            || base.endsWith("javax.xml.transform.stream.StreamSource"))  {
            base = cwd.toString() + File.separator + "A_STREAM_SOURCE_NOT_A_FILE";
        }

        { // check all known prefixes
            Iterator i = extraDirs.iterator();            
            while (i.hasNext()) {
                Entry entry = (Entry) i.next();
                if (href.startsWith(entry.getPrefix())) {
                    path = new File(entry.getDir(), href.substring(entry.getPrefixLength()));
                }            
            }
        }

        if (path == null) { // still not found
            if (href.startsWith("file:")) {
                href = href.substring(5);
            }            
            path = new File(href);
            
            if (! path.isAbsolute()) { // an opportunity to use base of cwd
                if (base.startsWith("file://")) {
                    path = new File(new File(base.substring(7)).getParent(), href);
                } else { // I don't know what is in base, but I do know that I don't know how to use it. Use cwd.
                    path = new File(cwd, href); // look in cwd.
                }

                if (! path.isFile()) { // still no file? Try searching it in all dir which are configured.
                    if (log.isDebugEnabled()) log.debug(path.toString() + "does not exist, trying defaults");
                    Iterator i = extraDirs.iterator();
                    while (i.hasNext() && ! path.isFile()) {
                        Entry entry = (Entry) i.next();
                        path = new File(entry.getDir(), href);
                    }
                }
            }
        }
        if (log.isDebugEnabled()) log.debug("using " + path.toString());
        return path;
    }

    /**
     * Implementation of the resolve method of javax.xml.transform.URIResolver.
     * 
     * @see javax.xml.transform.URIResolver
     **/
    
    public Source resolve(String href,  String base) throws javax.xml.transform.TransformerException {
        return new javax.xml.transform.stream.StreamSource(resolveToFile(href, base));
    }


    /**
     *  URIResolver can be used as a key in Maps (Caches).
     */
    public int hashCode() {
        return hashCode;
    }

    /**
     *  URIResolver can be used as a key in Maps (Caches).
     */
    public boolean equals(Object o) {
        if (o != null && (o instanceof URIResolver)) {
            return hashCode == o.hashCode();
        }
        return false;        
    }

    /**
     * Objects of this type connect a prefix (must normally end in :)
     * with a File (which must be a Directory). A List of this type
     * can be fed to the constructor of URIResolver.
     * 
     */

    static public class Entry {
        private String prefix;
        private File   dir;
        private int    prefixLength;

        public Entry(String p, File d) {
            prefix = p;
            dir    = d;
            prefixLength = prefix.length(); // avoid calculating it again.
        }
    
        String getPrefix() {
            return prefix;
        }
        File getDir() {
            return dir;
        }
        int getPrefixLength() {
            return prefixLength;
        }

        public String toString() {
            return prefix + "|" + dir.toString();
        }
    }

}
