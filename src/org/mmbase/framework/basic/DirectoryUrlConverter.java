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
 * @author Michiel Meeuwissen
 * @version $Id: DirectoryUrlConverter.java,v 1.1 2008-08-30 09:20:19 michiel Exp $
 * @since MMBase-1.9
 */
public abstract class DirectoryUrlConverter implements UrlConverter {
    private static final Logger log = Logging.getLoggerInstance(DirectoryUrlConverter.class);

    protected String  directory = "/magazine";
    protected final Framework framework;

    public DirectoryUrlConverter(Framework fw) {
        framework = fw;
    }

    public void setDirectory(String d) {
        directory = d;
    }

    public Parameter[] getParameterDefinition() {
        return new Parameter[] {Parameter.REQUEST};
    }

    /**
     * The components for which this URL converter can produces nice url. Or <code>null</code> if it
     * can do that for any component.
    */
    protected List<Component> getComponents() {
        return null;
    }

    protected String getUrl(String path,
                             Map<String, Object> parameters,
                             Parameters frameworkParameters, boolean escapeAmps, boolean action) {
        HttpServletRequest request = BasicUrlConverter.getUserRequest(frameworkParameters.get(Parameter.REQUEST));
        State state = State.getState(request);

        boolean filteredMode = FrameworkFilter.getPath(request).startsWith(directory);

        /*
        Block block = framework.getBlock(frameworkParameters);

            if (block != null && "mynews".equals(block.getComponent().getName())) {
        */

        if (state.isRendering() || ! filteredMode) {
            return null;
        } else {
            return getNiceUrl(path, parameters, frameworkParameters, escapeAmps, action);
        }
    }

    public String getUrl(String path,
                         Map<String, Object> parameters,
                         Parameters frameworkParameters, boolean escapeAmps) {
        return getUrl(path, parameters, frameworkParameters, escapeAmps, false);
    }

    public String getProcessUrl(String path,
                                Map<String, Object> parameters,
                                Parameters frameworkParameters, boolean escapeAmps) {
        return getUrl(path, parameters, frameworkParameters, escapeAmps, true);
    }

    public String getInternalUrl(String page, Map<String, Object> params, Parameters frameworkParameters) throws FrameworkException {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        if (page == null) throw new IllegalArgumentException();
        if (page.startsWith(directory)) {
            String sp = FrameworkFilter.getPath(request);
            String[] path = sp.split("/");
            return getFilteredInternalUrl(path, params, frameworkParameters);
        } else {
            log.debug("Leaving unfiltered");
            return null;
        }
    }

    /**
     * When implemnting this method, you can already assume that the url must be 'nice', iow that we
     * are actually rendering in the 'realm' of this UrlConverter
     */
    protected abstract String getNiceUrl(String path, Map<String, Object> parameters,
                               Parameters frameworkParameters, boolean escapeAmps, boolean action);


    /**
     * When implementing this method you can assume that you don't have to return
     * <code>null</code>. IOW it is certain that the current URL is 'nice' according to this URL
     * Converter.
    */
    protected  abstract String getFilteredInternalUrl(String[] path, Map<String, Object> params, Parameters frameworkParameters) throws FrameworkException;


    public String toString() {
        return directory;
    }


}
