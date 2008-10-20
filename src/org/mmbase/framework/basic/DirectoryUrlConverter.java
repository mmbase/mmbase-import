/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.util.transformers.*;
import org.mmbase.util.DynamicDate;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.framework.*;
import org.mmbase.framework.basic.UrlConverter;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * A directory URL converter is a URL-converter which arranges to work in just one subdirectory. In
 * stead of {@link #getUrl} and {@link #getInternalUrl} you override {@link #getNiceUrl} and {@link
 * #getFilteredInternalUrl}.
 *
 * It is also assumed that the niceness of the URL's is basicly about one block.
 *
 * @author Michiel Meeuwissen
 * @version $Id: DirectoryUrlConverter.java,v 1.5 2008-10-20 16:45:11 michiel Exp $
 * @since MMBase-1.9
 * @todo EXPERIMENTAL
 */
public abstract class DirectoryUrlConverter extends BlockUrlConverter {
    private static final Logger log = Logging.getLoggerInstance(DirectoryUrlConverter.class);

    protected String  directory = null;

    public DirectoryUrlConverter(BasicFramework fw) {
        super(fw);
    }

    public void setDirectory(String d) {
        directory = d;
        if (! directory.endsWith("/")) directory += "/";
    }


    @Override public boolean isFilteredMode(Parameters frameworkParameters) throws FrameworkException {
        if (directory == null) throw new RuntimeException("Directory not set");
        HttpServletRequest request = BasicUrlConverter.getUserRequest(frameworkParameters.get(Parameter.REQUEST));
        return FrameworkFilter.getPath(request).startsWith(directory);
    }



    @Override final public String getFilteredInternalUrl(String pa, Map<String, Object> params, Parameters frameworkParameters) throws FrameworkException {
        List<String> path = new ArrayList<String>();
        for (String p: pa.split("/")) {
            path.add(p);
        }
        return getFilteredInternalDirectoryUrl(path.subList(2, path.size()), params, frameworkParameters);
    }

    protected abstract String getFilteredInternalDirectoryUrl(List<String> path, Map<String, Object> params, Parameters frameworkParameters) throws FrameworkException;



    public String toString() {
        return directory;
    }


}
