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
 * @version $Id: MMBaseUrlConverter.java,v 1.13 2008-09-01 07:06:12 michiel Exp $
 * @since MMBase-1.9
 */
public class MMBaseUrlConverter extends DirectoryUrlConverter {

    private static final Logger log = Logging.getLoggerInstance(MMBaseUrlConverter.class);



    /**
     * MMBaseUrlConverter wants a 'category'.
     */
    public static final Parameter<String> CATEGORY  = new Parameter<String>("category", String.class);

    protected String renderJsp = "/mmbase/admin/index.jsp";

    public MMBaseUrlConverter(BasicFramework fw) {
        super(fw);
        setDirectory("/mmbase/");
    }


    public void setRenderJsp(String j) {
        renderJsp = j;
    }

    @Override public Parameter[] getParameterDefinition() {
        return new Parameter[] {Parameter.REQUEST, CATEGORY, Framework.COMPONENT, Framework.BLOCK};
    }

    @Override public Block getBlock(String path, Parameters frameworkParameters) throws FrameworkException {
        Block block = super.getBlock(path, frameworkParameters);
        if (block == null) {
            String categoryName = frameworkParameters.get(CATEGORY);
            if (categoryName != null) {
                boolean categoryOk = false;
                Block.Type[] mmbaseBlocks = ComponentRepository.getInstance().getBlockClassification("mmbase." + categoryName);
                if (mmbaseBlocks.length == 0) throw new FrameworkException("No such category mmbase." + categoryName);
                return mmbaseBlocks[0].getBlocks().get(0);
            }
            return null;
        } else {
            return block;
        }
    }

    protected String getNiceUrl(Block block,
                                Map<String, Object> parameters,
                                Parameters frameworkParameters, boolean escapeAmps, boolean action) throws FrameworkException {
        if (log.isDebugEnabled()) {
            log.debug("block '" + block  + "' parameters: " + parameters + " framework parameters " + frameworkParameters);
        }
        HttpServletRequest request = BasicUrlConverter.getUserRequest(frameworkParameters.get(Parameter.REQUEST));

        String category = frameworkParameters.get(CATEGORY);
        State state = State.getState(request);

        if (category == null && state.isRendering()) {
            category = state.getFrameworkParameters().get(CATEGORY);
        }

        Component component = block.getComponent();

        // @TODO
        // Stuff happening with map, and processorUrl and things like that, seems to have no place
        // here.
        // Refactor it away


        Map<String, Object> map = new TreeMap<String, Object>();
        if (log.isDebugEnabled()) {
            log.debug("Generating URL to " + block + " State " + state + " category " + category);
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
                if (k.equals(Framework.BLOCK.getName())) continue;
                if (k.equals(Framework.COMPONENT.getName())) continue;
                if (k.equals(CATEGORY.getName())) continue;
                log.debug("putting " + entry);
                map.put(k, entry.getValue());
            }
        } else {
            //log.debug("Now processing " + processor);
        }

        if (! processUrl) {
            Parameters blockParameters = block.createParameters();
            blockParameters.setAutoCasting(true);
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
            page = directory + (category == null ? "_" : category) + "/" + component.getName() + "/" + block.getName() ;
        }

        //path == null || subComponent ?

        String sb = BasicUrlConverter.getUrl(page, map , request, escapeAmps);
        return sb;
    }

    public String getFilteredInternalUrl(List<String> path, Map<String, Object> params, Parameters frameworkParameters) {
        if (path.size() == 0) {
            // nothing indicated after /mmbase/, don't know what to do, leaving unfiltered
            return null;
        }

        StringBuilder url = new StringBuilder(renderJsp);

        {   // dealing with the category part
            String category = path.get(0);
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

            url.append("?category=");
            url.append(category);
        }

        if (path.size() > 1) {
            // dealing with the component part

            Component comp = ComponentRepository.getInstance().getComponent(path.get(1));
            if (comp == null) {
                log.debug("No such component, ignoring this too");
                return null;
            }
            url.append("&component=").append(comp.getName());

            if (path.size() > 2) {
                // dealing with the block
                Block block = comp.getBlock(path.get(2));
                if (log.isDebugEnabled()) {
                    log.debug("Will try to display " + block);
                }
                if (block == null) {
                    log.debug("No block " + path.get(2) + " in component " + comp);
                    return null;

                }
                url.append("&block=").append(block.getName());
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("internal URL " + url);
        }
        return url.toString();
    }

}
