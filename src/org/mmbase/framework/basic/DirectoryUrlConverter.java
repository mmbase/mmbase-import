/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;
import java.util.*;
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

    public DirectoryUrlConverter(BasicFramework fw) {
        super(fw);
    }

    public void setDirectory(String d) {
        directory = d;
        if (! directory.endsWith("/")) directory += "/";
        if (! directory.startsWith("/")) directory = "/" + directory;
    }

    /**
     * The 'directory' used for thie UrlConverter. A String which begins and ends with '/'.
     */
    public String getDirectory() {
        return directory;
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
        return path;
    }

    /**
     * This is the method you must implement. Append the nice URL to b. b already ends with &lt;directory&gt;/
     */
    protected abstract void getNiceDirectoryUrl(StringBuilder b, Block block,
                                         Parameters parameters,
                                         Parameters frameworkParameters,  boolean action) throws FrameworkException;

    @Override public boolean isFilteredMode(Parameters frameworkParameters) throws FrameworkException {
        if (directory == null) throw new RuntimeException("Directory not set");
        HttpServletRequest request = BasicUrlConverter.getUserRequest(frameworkParameters.get(Parameter.REQUEST));
        String path = FrameworkFilter.getPath(request);
        return path.startsWith(directory) || directory.equals(path + "/");
    }



    @Override final public Url getFilteredInternalUrl(String pa, Map<String, ?> params, Parameters frameworkParameters) throws FrameworkException {
        List<String> path = getPath(pa);
        if (path.size() < 2) {
            log.debug("pa " + pa + " -> " + path + " (Not long enough for " + this + ")");
            return Url.NOT;
        }
        return getFilteredInternalDirectoryUrl(path.subList(directory.length() > 1 ? 2 : 1, // should also work correctly if directory is empty
                                                            path.size()), params, frameworkParameters);
    }

    protected abstract Url getFilteredInternalDirectoryUrl(List<String> path, Map<String, ?> params, Parameters frameworkParameters) throws FrameworkException;



    @Override
    public String toString() {
        return directory;
    }


}
