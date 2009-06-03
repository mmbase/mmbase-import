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
 * A block url converter is an url converter which encoded in the URL precisely one block. Most
 * URLConverters would probably be like this, and can extend from this.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 * @todo EXPERIMENTAL
 */
public abstract class BlockUrlConverter implements UrlConverter {
    private static final Logger log = Logging.getLoggerInstance(BlockUrlConverter.class);

    protected final BasicFramework framework;
    protected Set<Component> components = null;

    public BlockUrlConverter(BasicFramework fw) {
        framework = fw;
    }

    /**
     * Block url converters add an explicit 'component' and 'block' framework parameters
     */
    public Parameter[] getParameterDefinition() {
        return new Parameter[] {Parameter.REQUEST, Framework.COMPONENT, Framework.BLOCK};
    }

    protected void addComponent(Component comp) {
        if (components == null) components = new HashSet<Component>();
        components.add(comp);
    }

    /**
     * The components for which this URLconverter can produce nice url. Or <code>null</code> if it
     * can do that for any component.
    */
    protected Set<Component> getComponents() {
        return components;
    }



    /**
     * This proposal implemention simply uses {@link Framework#COMPONENT} and {@link
     * Framework#BLOCK} framework parameters to determin the explicit block for {@link #getUrl},
     * which may often be what you want.
     */
    protected Block getExplicitBlock(String path, Parameters frameworkParameters) throws FrameworkException {
        String componentName = frameworkParameters.get(Framework.COMPONENT);
        if (componentName != null) {
            log.debug("Found component " + componentName);
            Component component = ComponentRepository.getInstance().getComponent(componentName);
            if (component == null) throw new FrameworkException("No such component " + componentName);
            String blockName = frameworkParameters.get(Framework.BLOCK);
            if (blockName == null) {
                log.debug("found explicit component " + component);
                if (path != null && ! "".equals(path)) {
                    Block block =  component.getBlock(path);
                    if (block == null) throw new FrameworkException("No such block '" + path + "' in component '" + component.getName() + "'");
                    return block;
                } else {
                    return component.getDefaultBlock();
                }
            } else {
                Block block = component.getBlock(blockName);
                if (path != null && ! "".equals(path)) throw new IllegalArgumentException("Cannot use both 'path' argument ('" + path + "') and 'block' parameter ('" + frameworkParameters + "' -> " + block + ")");
                if (block == null) throw new FrameworkException("No such block " + blockName);
                log.debug("found explicit block " + block);
                return block;
            }
        }
        return null;
    }


    /**
     * Determins for which block an URL will be generated. This is the explicit block, if that is
     * defined. If it is not defined then it will use the currently rendered block and the 'path' to
     * determin which one is meant now. That can be <code>null</code> if the current URL is not
     * managed by this URLConverter.
     */
    public Block getBlock(String path, Parameters frameworkParameters) throws FrameworkException {

        HttpServletRequest request = BasicUrlConverter.getUserRequest(frameworkParameters.get(Parameter.REQUEST));
        State state = State.getState(request);

        // First explore
        Block block = getExplicitBlock(path, frameworkParameters);
        if (block != null) {
            if (components != null && ! components.contains(block.getComponent())) {
                log.debug("Explicit block, but not mine one");
                return null;
            }
            return block;
        }

        boolean filteredMode = isFilteredMode(frameworkParameters);

        if (filteredMode) {
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
            log.debug("Not in filtering mode for " + this);
        }

        return null;
    }

    public int getDefaultWeight() {
        return 0;
    }

    /**
     * Whether the current request already is in the realm of this URL-converter; the url converter
     * must implement here how it recognizes itself.
     */
    public abstract boolean isFilteredMode(Parameters frameworkParameters) throws FrameworkException;


    protected State getState(Parameters frameworkParameters) {
        HttpServletRequest request = BasicUrlConverter.getUserRequest(frameworkParameters.get(Parameter.REQUEST));
        return State.getState(request);
    }

    /**
     * Parameterized proposal implementation for both {@link #getUrl} and {@link #getProcessUrl},
     * because they will probably be about the same.
     *
     */
    protected final Url getUrl(String path,
                               Map<String, ?> parameters,
                               Parameters frameworkParameters, boolean escapeAmps, boolean action) throws FrameworkException {
        Block block = getBlock(path, frameworkParameters);
        if (block != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            Url niceUrl;
            Parameters blockParameters = block.createParameters();
            {
                blockParameters.setAutoCasting(true);
                blockParameters.setAll(parameters);
                niceUrl = getNiceUrl(block, blockParameters, frameworkParameters, action);
            }
            if (log.isDebugEnabled()) {
                log.debug("Found " + niceUrl + " from " + this + " for " + block + " " + frameworkParameters);
            }

            HttpServletRequest request = BasicUrlConverter.getUserRequest(frameworkParameters.get(Parameter.REQUEST));
            if (niceUrl.getUrl().startsWith(request.getServletPath())) {
                log.debug("servlet path not changing, also conservering parameters");
                // conserve other parameters which happen to be on the request
                for (Object e : request.getParameterMap().entrySet()) {
                    Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) e;
                    map.put(entry.getKey(), entry.getValue());
                }
            } else {
                log.debug("" + niceUrl + " does not start with " + request.getServletPath());
            }

            try {
                Map<String, Object> params = action ?
                    blockParameters.toUndefaultMap() :
                    framework.prefix(getState(frameworkParameters), blockParameters.toUndefaultMap());
                if (log.isDebugEnabled()) {
                    log.debug("Prefixed params " + params);
                }
                map.putAll(params);
                if (action) map.put("_action", frameworkParameters.get("_action"));

                String u = BasicUrlConverter.getUrl(niceUrl.getUrl(), map, request, escapeAmps);
                log.debug("Returning actual url " + u);
                return new BasicUrl(this, u, niceUrl.getWeight());
            } catch (RuntimeException re) {
                log.error(re.getMessage(), re);
                throw re;
            }
        } else {
            return Url.NOT;
        }
    }



    public Url getUrl(String path,
                         Map<String, ?> parameters,
                         Parameters frameworkParameters, boolean escapeAmps) throws FrameworkException {
        return getUrl(path, parameters, frameworkParameters, escapeAmps, false);
    }

    public Url getProcessUrl(String path,
                             Map<String, ?> parameters,
                             Parameters frameworkParameters, boolean escapeAmps) throws FrameworkException {
        return getUrl(path, parameters, frameworkParameters, escapeAmps, true);
    }



    /**
     * When implementing this method, you can already assume that the url must be 'nice', iow that we
     * are actually rendering in the 'realm' of this UrlConverter, and you can straightforwardly,
     * withough any checking, produce the URL.
     *
     * @param block Block for which the produce the url for
     * @param blockParameters Parameters to use for this block. The implementation may set parameters
     * to <code>null</code> which were represented in the returning String.
     * @param frameworkParameters
     * @param action
     */
    protected abstract Url getNiceUrl(Block block,
                                      Parameters blockParameters,
                                      Parameters frameworkParameters,
                                      boolean action) throws FrameworkException;



    public  final Url getInternalUrl(String path, Map<String, ?> params, Parameters frameworkParameters) throws FrameworkException {
        if (isFilteredMode(frameworkParameters)) {
            return getFilteredInternalUrl(path, params, frameworkParameters);
        } else {
            return Url.NOT;
        }
    }

    /**
     * When implementing this method you can assume that you don't have to return
     * <code>null</code>. IOW it is certain that the current URL is 'nice' according to this URL
     * Converter.
    */
    protected  abstract Url getFilteredInternalUrl(String path, Map<String, ?> params, Parameters frameworkParameters) throws FrameworkException;


}
