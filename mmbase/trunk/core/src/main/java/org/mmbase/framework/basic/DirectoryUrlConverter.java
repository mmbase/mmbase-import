/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;
import java.util.*;
import java.util.regex.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.framework.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * A directory URL converter is a URL-converter which arranges to work in just one subdirectory. In
 * stead of {@link #getUrl} and {@link #getInternalUrl} you override {@link #getNiceDirectoryUrl} and {@link
 * #getFilteredInternalDirectoryUrl}.
 *
 * It is also assumed that the niceness of the URL's is basicly about one block.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 * @todo EXPERIMENTAL
 */
public abstract class DirectoryUrlConverter extends BlockUrlConverter {
    private static final Logger log = Logging.getLoggerInstance(DirectoryUrlConverter.class);

    private String  directory = null;

    private Pattern domain = Pattern.compile(".*");

    public DirectoryUrlConverter(BasicFramework fw) {
        super(fw);
    }

    public void setDirectory(String d) {
        directory = d;
        if (! directory.endsWith("/")) directory += "/";
        if (! directory.startsWith("/")) directory = "/" + directory;
    }

    /**
     * @since MMBase-1.9.2
     */
    public void setDomain(String d) {
        domain = Pattern.compile(d);
    }

    /**
     * The 'directory' used for thie UrlConverter. A String which begins and ends with '/'.
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * A regular expression witch must match 'getLocalName' of the request for this UrlConverter to
     * match. This defaults to .*.
     * @since MMBase-1.9.2
     */
    public Pattern getDomain() {
        return domain;
    }

    @Override public int getDefaultWeight() {
        int q = super.getDefaultWeight();
        return Math.max(q, q + 1000);
    }

    @Override protected final Url getNiceUrl(Block block,
                                                Parameters parameters,
                                                Parameters frameworkParameters,  boolean action) throws FrameworkException {
        StringBuilder b = new StringBuilder(directory);
        getNiceDirectoryUrl(b, block, parameters, frameworkParameters, action);
        return new BasicUrl(this, b.toString());
    }


    protected List<String> getPath(String pa) {
        List<String> path = new ArrayList<String>();
        for (String p: pa.split("/")) {
            path.add(p);
        }
        if (path.size() == 0) {
            path.add("");
        }
        return path;
    }



    /**
     * This is the method you must implement. Append the nice URL to b. b already ends with &lt;directory&gt;/
     */
    protected abstract void getNiceDirectoryUrl(StringBuilder b, Block block,
                                         Parameters parameters,
                                         Parameters frameworkParameters,  boolean action) throws FrameworkException;

    @Override
    public boolean isFilteredMode(Parameters frameworkParameters) throws FrameworkException {
        if (directory == null) {
            throw new RuntimeException("Directory not set");
        }
        HttpServletRequest request = BasicUrlConverter.getUserRequest(frameworkParameters.get(Parameter.REQUEST));
        if (! domain.matcher(request.getLocalName()).matches()) {
            return false;
        }
        String path = FrameworkFilter.getPath(request);
        log.debug("Found path from request " + path);
        return path.startsWith(directory) || directory.equals(path + "/");
    }



    @Override final public Url getFilteredInternalUrl(String pa, Map<String, ?> params, Parameters frameworkParameters) throws FrameworkException {
        HttpServletRequest request = BasicUrlConverter.getUserRequest(frameworkParameters.get(Parameter.REQUEST));

        if (! domain.matcher(request.getLocalName()).matches()) {
            return Url.NOT;
        }
        List<String> path = getPath(pa);
        if (directory.length() > 1) {
            if (path.size() < 2) {
                log.debug("pa " + pa + " -> " + path + " (Not long enough for " + this + ")");
                return Url.NOT;
            }
        } else {
            if (path.size() < 1) {
                log.debug("pa " + pa + " -> " + path + " (Not long enough for " + this + ")");
                return Url.NOT;
            }
        }
        if (directory.length() > 1) {
            if(! ("/" + path.get(1) + "/").equals(directory)) {
                if (log.isDebugEnabled()) {
                    log.debug("" + path + " does not start with " + directory + " hence this urconvertor does not handly this path");
                }
                return Url.NOT;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("" + path + " from " + pa + " directory" + directory);
        }
        List<String> subPath;
        if (directory.length() > 1) {
            subPath = path.subList(2, path.size());
        } else {
            subPath = path.subList(1, path.size());

        }
        log.debug("'" + pa + "' -> " + subPath + " " + subPath.size());
        return getFilteredInternalDirectoryUrl(subPath, params, frameworkParameters);
    }

    protected abstract Url getFilteredInternalDirectoryUrl(List<String> path, Map<String, ?> params, Parameters frameworkParameters) throws FrameworkException;



    @Override
    public String toString() {
        return directory;
    }


    public static void main(String[] argv) {
        for (String s : new String[] { "", "/", "//", "/bla/", "/bla"}) {
            String[] l = s.split("/", -1);
            System.out.println("'" + s + "': " + Arrays.asList(l) + " " + l.length);
            String[] m = s.split("/");
            System.out.println("'" + s + "': " + Arrays.asList(m) + " " + m.length);
        }
    }

}
