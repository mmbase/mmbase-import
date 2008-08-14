/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;
import org.mmbase.framework.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The UrlConverter which deals in urls and filters links that start with '/mmbase/' (or whatever
 * was configured for this prefix).
 *
 * @author Michiel Meeuwissen
 * @version $Id: MMBaseUrlConverter.java,v 1.11 2008-08-14 20:27:08 michiel Exp $
 * @since MMBase-1.9
 */
public class MMBaseUrlConverter implements UrlConverter {

    private static final Logger log = Logging.getLoggerInstance(MMBaseUrlConverter.class);

    /**
     * MMBaseUrlConverter points to a jsp which renders 1 block. This parameter indicates of which component.
     */
    public static final Parameter<String> COMPONENT = new Parameter<String>("component", String.class);

    /**
     * MMBaseUrlConverter points to a jsp which renders 1 block. This parameter indicates its name.
     */
    public static final Parameter<String> BLOCK     = new Parameter<String>("block", String.class);

    /**
     * MMBaseUrlConverter wants a 'category'.
     */
    public static final Parameter<String> CATEGORY  = new Parameter<String>("category", String.class);


    private final BasicFramework framework;

    protected String dir = "/mmbase/";

    protected String renderJsp = "/mmbase/admin/index.jsp";

    public MMBaseUrlConverter(BasicFramework fw) {
        framework = fw;
    }

    public void setDir(String d) {
        dir = d;
    }

    public void setRenderJsp(String j) {
        renderJsp = j;
    }

    public Parameter[] getParameterDefinition() {
        return new Parameter[] {Parameter.REQUEST, CATEGORY, COMPONENT, BLOCK};
    }

    protected String getUrl(String path,
                            Map<String, Object> parameters,
                            Parameters frameworkParameters, boolean escapeAmps, boolean action) {
        if (log.isDebugEnabled()) {
            log.debug("path '" + path + "' parameters: " + parameters + " framework parameters " + frameworkParameters);
        }
        HttpServletRequest request = BasicUrlConverter.getUserRequest(frameworkParameters.get(Parameter.REQUEST));
        State state = State.getState(request);

        String category = frameworkParameters.get(CATEGORY);
        if (category == null && state.isRendering()) {
            category = state.getFrameworkParameters().get(CATEGORY);
        }

        // MMBase urls always shows only one block
        Component component  = ComponentRepository.getInstance().getComponent(frameworkParameters.get(COMPONENT));
        if (component == null) {
            // if no explicit component specified, suppose current component, if there is one:
            if (state.isRendering()) {
                component = state.getBlock().getComponent();
            } else {
                log.debug("No rendering state object found, so no current component.");
                if (category != null) {
                    log.debug("Found category " + category);
                    return dir + category;
                } else {
                    return null;
                }
            }
        }

        assert component != null;

        boolean filteredMode = FrameworkFilter.getPath(request).startsWith(dir);


        if (state.isRendering() && (! filteredMode || state.getDepth() > 0)) {
            log.debug("we are rendering a sub-component, deal with that as if  no mmbaseurlconverter. " + filteredMode);
            return null;
        }


        Block block;
        {  // determin the block:
            String blockParam = frameworkParameters.get(BLOCK);
            if (blockParam != null) {
                if (log.isDebugEnabled()) {
                    log.debug("found block " + blockParam + " trying it on " + component);
                }

                if (path != null && ! "".equals(path)) throw new IllegalArgumentException("Cannot use both 'path' argument and 'block' parameter");
                block = component.getBlock(blockParam);
                if (block == null) throw new IllegalArgumentException("No block '" + blockParam + "' found in component '" + component + "'");
            } else {
                block = component.getBlock(path);
                if (block != null) {
                    if (! filteredMode) {
                        path = null; // used, determin path with block name
                    }
                } else {
                    // no such block
                    if (path != null && ! "".equals(path)) {
                        log.debug("No block '" + path + "' found");
                        return null;
                    }
                }
                if (block == null) {
                    if(state.isRendering()) {
                        // current block
                        block = state.getRenderer().getBlock();
                    } else {
                        // default block
                        block = component.getDefaultBlock();
                    }
                }
            }
        }

        assert block != null;


        Map<String, Object> map = new TreeMap<String, Object>();
        if (log.isDebugEnabled()) {
            log.debug("Creating URL to component " + component + " generating URL to " + block + " State " + state + " category " + category);
        }
        boolean processUrl = frameworkParameters.get(BasicFramework.ACTION) != null;
        if (processUrl) {
            // get current components ids
            if (state.isRendering()) {
                map.put(BasicFramework.ACTION.getName(), state.getId());
            } else {
                map.put(BasicFramework.ACTION.getName(), state.getUpcomingId());
            }
        }


        if (! processUrl && state.isRendering()) {
            // copy all current parameters of the request.
            for (Object e : request.getParameterMap().entrySet()) {
                Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) e;
                String k = entry.getKey();
                if (k.equals(BLOCK.getName())) continue;
                if (k.equals(COMPONENT.getName())) continue;
                if (k.equals(CATEGORY.getName())) continue;
                log.debug("putting " + entry);
                map.put(k, entry.getValue());
            }
        } else {
            //log.debug("Now processing " + processor);
        }

        if (! processUrl) {
            Parameters blockParameters = block.createParameters();
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                blockParameters.set(entry.getKey(), entry.getValue());
            }
            map.putAll(framework.prefix(state, blockParameters.toMap()));
        }

        // TODO, if no category specified somehow, then guess when, using the avaiable
        // classifications for the specified block.

        if (category == null) {
            Block.Type[] classification = block.getClassification();
        }
        //boolean subComponent = state.getDepth() > 0;


        String page;
        if (state.isRendering() && state.getBlock().equals(block)) {
            page = FrameworkFilter.getPath(request);
        } else {
            page = dir + (category == null ? "_" : category) + "/" + component.getName() + "/" + block.getName() ;
        }

        //path == null || subComponent ?

        String sb = BasicUrlConverter.getUrl(page, map , request, escapeAmps);
        return sb;
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
    public String getInternalUrl(String page, Map<String, Object> params, Parameters frameworkParameters) {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        if (request == null) return null;
        if (page == null) throw new IllegalArgumentException();
        if (page.startsWith(dir)) {
            //String sp = FrameworkFilter.getPath(request); // I don't remember where this was for.
            String[] path = page.split("/"); // use to be sp.split("/")
            if (log.isDebugEnabled()) {
                log.debug("Going to filter " + Arrays.asList(path));
            }
            if (path.length >= 3) {
                assert path[0].equals("");
                assert path[1].equals(dir.split("/")[1]);
                String category = path[2];
                if (! category.equals("_")) {
                    boolean categoryOk = false;
                    Block.Type[] mmbaseBlocks = ComponentRepository.getInstance().getBlockClassification("mmbase");
                    if (mmbaseBlocks.length > 0) {
                        for (Block.Type rootType : mmbaseBlocks[0].getSubTypes()) {
                            categoryOk = rootType.getName().equals(category);
                            if (categoryOk) break;
                        }
                        if (mmbaseBlocks.length > 1) {
                            log.warn("odd");
                        }
                    }
                    if (! categoryOk) {
                        log.debug("No such component clasification, ignoring this");
                        return null;
                    }
                }

                StringBuilder url = new StringBuilder(renderJsp);
                url.append("?category=");
                url.append(category);

                if (path.length == 3) return url.toString();

                Component comp = ComponentRepository.getInstance().getComponent(path[3]);
                if (comp == null) {
                    log.debug("No such component, ignoring this too");
                    return null;
                }
                url.append("&component=").append(comp.getName());

                if (path.length == 4) return url.toString();

                Block block = comp.getBlock(path[4]);
                if (log.isDebugEnabled()) {
                    log.debug("Will try to display " + block);
                }
                if (block == null) {
                    log.debug("No block " + path[4] + " in component " + path[3]);
                    return null;

                }
                url.append("&block=").append(block.getName());
                if (log.isDebugEnabled()) {
                    log.debug("internal URL " + url);
                }
                return url.toString();
            } else {
                log.debug("path length " + path.length);
                return null;
            }
        } else {
            log.debug("Leaving unfiltered");
            return null;
        }
    }

    public String toString() {
        return dir;
    }

}
