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
 * @version $Id: DirectoryUrlConverter.java,v 1.3 2008-09-01 18:36:04 michiel Exp $
 * @since MMBase-1.9
 */
public abstract class DirectoryUrlConverter implements UrlConverter {
    private static final Logger log = Logging.getLoggerInstance(DirectoryUrlConverter.class);

    protected String  directory = null;
    protected final BasicFramework framework;
    protected Set<Component> components = null;

    public DirectoryUrlConverter(BasicFramework fw) {
        framework = fw;
    }

    public void setDirectory(String d) {
        directory = d;
        if (! directory.endsWith("/")) directory += "/";
    }

    public Parameter[] getParameterDefinition() {
        return new Parameter[] {Parameter.REQUEST, Framework.COMPONENT, Framework.BLOCK};
    }


    protected void addComponent(Component comp) {
        if (components == null) components = new HashSet<Component>();
        components.add(comp);
    }

    /**
     * The components for which this URL converter can produces nice url. Or <code>null</code> if it
     * can do that for any component.
    */
    protected Set<Component> getComponents() {
        return components;
    }



    /**
     * This proposal implemention simply uses {@link Framework#COMPONENT} {@link Framework#BLOCK},
     * which may often be what you want.
     */
    protected Block getExplicitBlock(Parameters frameworkParameters) throws FrameworkException {
        String componentName = frameworkParameters.get(Framework.COMPONENT);
        if (componentName != null) {
            Component component = ComponentRepository.getInstance().getComponent(componentName);
            if (component == null) throw new FrameworkException("No such component " + componentName);
            String blockName = frameworkParameters.get(Framework.BLOCK);
            if (blockName == null) {
                log.debug("found explicit component " + component);
                return component.getDefaultBlock();
            } else {
                Block block = component.getBlock(blockName);
                if (block == null) throw new FrameworkException("No such block " + blockName);
                log.debug("found explicit block " + block);
                return block;
            }
        }
        return null;
    }

    public Block getBlock(String path, Parameters frameworkParameters) throws FrameworkException {

        // First explore
        Block block = getExplicitBlock(frameworkParameters);
        if (block != null) {
            if (components != null && ! components.contains(block.getComponent())) {
                log.debug("Explicit block, but not mine one");
                return null;
            }
            if (path != null && ! "".equals(path)) throw new IllegalArgumentException("Cannot use both 'path' argument and 'block' parameter");
            return block;
        }

        HttpServletRequest request = BasicUrlConverter.getUserRequest(frameworkParameters.get(Parameter.REQUEST));
        if (directory == null) throw new RuntimeException("Directory not set");

        // dealing with the case when we know that we're in 'nice' mode already.

        boolean filteredMode = FrameworkFilter.getPath(request).startsWith(directory);
        if (filteredMode) {
            State state = State.getState(request);
            if (state.isRendering() && state.getDepth() == 0) {
                Block stateBlock = state.getBlock();
                if (components == null || components.contains(stateBlock.getComponent())) {
                    if (path != null && ! "".equals(path)) {
                        return stateBlock.getComponent().getBlock(path);
                    } else {
                        return stateBlock;
                    }
                } else {
                    log.debug("Not a recognized component");
                }
            } else {
                log.debug("Not currently rendering");
            }
        } else {
            log.debug("Not in filtering mode for " + directory);
        }

        return null;
    }



    protected String getUrl(String path,
                             Map<String, Object> parameters,
                             Parameters frameworkParameters, boolean escapeAmps, boolean action) throws FrameworkException {
        Block block = getBlock(path, frameworkParameters);
        if (block != null) {
            return getNiceUrl(block, parameters, frameworkParameters, escapeAmps, action);
        } else {
            return null;
        }
    }

    public String getUrl(String path,
                         Map<String, Object> parameters,
                         Parameters frameworkParameters, boolean escapeAmps) throws FrameworkException {
        return getUrl(path, parameters, frameworkParameters, escapeAmps, false);
    }

    public String getProcessUrl(String path,
                                Map<String, Object> parameters,
                                Parameters frameworkParameters, boolean escapeAmps) throws FrameworkException {
        return getUrl(path, parameters, frameworkParameters, escapeAmps, true);
    }

    public String getInternalUrl(String page, Map<String, Object> params, Parameters frameworkParameters) throws FrameworkException {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        if (request == null) return null;
        if (page == null) throw new IllegalArgumentException();
        if (page.startsWith(directory)) {
            String sp = FrameworkFilter.getPath(request);
            String[] path = sp.split("/");
            assert path[0].equals("");
            assert path[1].equals(directory.split("/")[1]);
            List<String> p = Arrays.asList(path);
            return getFilteredInternalUrl(p.subList(2, p.size()), params, frameworkParameters);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Leaving unfiltered " + getClass() + " " + page + " not starting with " + directory);
            }
            return null;
        }
    }

    /**
     * When implemnting this method, you can already assume that the url must be 'nice', iow that we
     * are actually rendering in the 'realm' of this UrlConverter
     */
    protected abstract String getNiceUrl(Block block,
                                         Map<String, Object> parameters,
                                         Parameters frameworkParameters,
                                         boolean escapeAmps, boolean action) throws FrameworkException;


    /**
     * When implementing this method you can assume that you don't have to return
     * <code>null</code>. IOW it is certain that the current URL is 'nice' according to this URL
     * Converter.
    */
    protected  abstract String getFilteredInternalUrl(List<String> path, Map<String, Object> params, Parameters frameworkParameters) throws FrameworkException;


    public String toString() {
        return directory;
    }


}
