/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import javax.xml.transform.Source;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.SizeMeasurable;
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
   <li> MMBase configuration directory (prefix: 'mm:') </li>
 </ol>
 
 * Optionially you can add other dirs  between these two.
 *
 * When you start searching in the current working dir, and the URI
 * does not point to an existing file, it starts searching downwards in
 * this list, until it finds a file that does exist.
 *
 * @author Michiel Meeuwissen.
 * @since  MMBase-1.6
 * @version $Id: URIResolver.java,v 1.15 2003-02-11 18:51:04 michiel Exp $
 */

public class URIResolver implements javax.xml.transform.URIResolver, SizeMeasurable {
    
    private static Logger log = Logging.getLoggerInstance(URIResolver.class.getName());

    private EntryList     extraDirs;  // prefix -> File pairs
    private File          cwd;
    private int           hashCode;


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
        cwd      = c;
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
     * Create an URIResolver without support for a certain directory. (Will be taken the first root).
     */
    public URIResolver() {
        this(null, null); 
    }

    /**
     * Besides the current working directory you can also supply an
     * ordered list of URIResolver.Entry's. First in this list are the
     * directories which must be checked first, in case no prefix is
     * given.
     * @param extradirs A EntryList, containing 'extra' dirs with
     * prefixes.  If not specified or null, there will still be one
     * 'extra dir' available, namely the MMBase configuration
     * directory (with prefix mm:)
     */
    public URIResolver(File c, EntryList extradirs) {
        if (log.isDebugEnabled()) log.debug("Creating URI Resolver for " + c);
        if (c == null) {
            log.debug("No working directory specified, using filesystem root");
            File[] roots = File.listRoots();
            if (roots != null && roots.length > 0) {
                cwd = roots[0];
            } else {
                log.warn("No filesystem root available, trying with 'null'");
                cwd = null; 
                // will this result in anything useful? 
                // well, I don't think we will use mmbase on root-less systems anyway?
            }
        } else {
            cwd = c;
        }
        extraDirs = new EntryList();
        if (extradirs != null) {
            extraDirs.addAll(extradirs);
        }
        extraDirs.add(new Entry("mm:", new File(MMBaseContext.getConfigPath())));

        // URIResolvers  cannot be changed, the hashCode can already be calculated and stored.

        if (extraDirs.size() == 1) { // only mmbase config, cannot change
            if (log.isDebugEnabled()) log.debug("getting hashCode " + cwd.hashCode());
            hashCode = cwd.hashCode(); 
            // if only the cwd is set, then you alternatively use the cwd has hashCode is this way.
            // it this way in these case it is easy to avoid constructing an URIResolver at all.
        } else {
            hashCode = 31 * cwd.hashCode() + extraDirs.hashCode(); // see also javadoc of List
            if (log.isDebugEnabled()) log.debug("getting hashCode " + hashCode + " based on '" + extraDirs + "'");
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
     * @return A String which could be used as a shell's path.
     */
    public String getPath() {
        String result = cwd.toString();
        Iterator i = extraDirs.iterator();            
        while (i.hasNext()) {
            Entry entry = (Entry) i.next();
            result += File.pathSeparatorChar + entry.getDir().getAbsolutePath();
        }
        return result;        
    }

    /**
     * Creates a List of strings, every entry is a directory prefixed with its 'prefix'. Handy during debugging. 
     *
     * @return A List with prefix:path Strings.
     */
    public List getPrefixPath() {
        Vector result = new Vector();
        result.add(cwd.toString());
        Iterator i = extraDirs.iterator();            
        while (i.hasNext()) {
            Entry entry = (Entry) i.next();
            result.add(entry.getPrefix() + entry.getDir().getAbsolutePath());
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
     * @throws I
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
                if (base.startsWith("file:/")) {
                    try {
                        path = new File(new File(new URL(base).getFile()).getParent(), href); 
                        // would like java.net.URI, but only in 1.4
                    } catch (java.net.MalformedURLException e) {
                        throw new IllegalArgumentException("base: " + base + " is not a valid file: " + e.toString());
                    }
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
        if (! path.isFile()) {
            throw new IllegalArgumentException("Could not resolve '" + href + "'\n with path " + this + " and href: '" + href + "'   base: '" + base + "'");
        }
        if (! path.canRead()) {
            throw new IllegalArgumentException("Resolved to non-readable file ('" + path + "')\n with path " + this);
        }
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
            URIResolver res = (URIResolver) o;          
            return (extraDirs == null ? (res.extraDirs == null || res.extraDirs.size() == 1) : 
                                         extraDirs.equals(res.extraDirs)) && 
                   cwd.equals(res.cwd);
            // See java javadoc, lists compare every element, files equal if  point to same file
            // extraDirs == null?
            // -> created with first constructor.
        }
        return false;        
    }


    public int getByteSize() {
        return getByteSize(new org.mmbase.util.SizeOf());
    }

    public int getByteSize(org.mmbase.util.SizeOf sizeof) {
        return sizeof.sizeof(extraDirs);
    }
    public String toString() {
        return getPrefixPath().toString();
    }

    /**
     * This is a list of prefix/directory pairs which is used in the constructor of URIResolver.
     */

    static public class EntryList extends Vector {
        public EntryList() {
        }

        /**
         *
         * @throws ClassCastException If you don't add an Entry.
         */
        public boolean add(Object o) {
            return super.add((Entry) o);                 
        }

        /**
         * Adds an prefix/dir entry to the List. 
         * @return The list again, so you can easily 'chain' a few.
         * @throws IllegalArgumentException if d is not a directory.
         */
        public EntryList add(String p, File d) {
            add(new Entry(p, d));
            return this;
        }
    }

    /**
     * Objects of this type connect a prefix (must normally end in :)
     * with a File (which must be a Directory). A List of this type
     * (EntryList) can be fed to the constructor of URIResolver.
     * 
     */

    static class Entry {
        private String prefix;
        private File   dir;
        private int    prefixLength;

        Entry(String p, File d) {
            prefix = p;
            dir    = d;
            prefixLength = prefix.length(); // avoid calculating it again.
            if (! d.isDirectory()) {
                throw new IllegalArgumentException(d.toString() + " is not an existing directory");
            }
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
            return dir.toString();
        }
        public boolean equals(Object o) {            
            if (o instanceof File) {
                return dir.equals(o);
            } else if (o instanceof Entry) {
                Entry e = (Entry) o;
                return dir.equals(e.dir);                
            } else {
                return false;
            }
        }

        public int hashCode() {
            return dir.hashCode();
        }

    }

}
