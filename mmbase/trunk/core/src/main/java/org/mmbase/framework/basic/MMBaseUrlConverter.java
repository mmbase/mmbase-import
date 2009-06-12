/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;
import org.mmbase.framework.*;
import java.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The UrlConverter which deals in urls and filters links that start with '/mmbase/' (or whatever
 * was configured for this prefix).
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class MMBaseUrlConverter extends DirectoryUrlConverter {
    private final static long serialVersionUID = 0L;

    private static final Logger log = Logging.getLoggerInstance(MMBaseUrlConverter.class);



    /**
     * MMBaseUrlConverter wants a 'category'.
     */
    public static final Parameter<String> CATEGORY  = new Parameter<String>("category", String.class);

    protected String renderJsp = "/mmbase/admin/index.jsp";

    public MMBaseUrlConverter(BasicFramework fw) {
        super(fw);
        setDirectory("mmbase");
    }


    @Override public int getDefaultWeight() {
        int q = super.getDefaultWeight();
        return Math.max(q, q + 1000);
    }
    public void setRenderJsp(String j) {
        renderJsp = j;
    }

    @Override public Parameter<?>[] getParameterDefinition() {
        return new Parameter<?>[] {Parameter.REQUEST, CATEGORY, Framework.COMPONENT, Framework.BLOCK};
    }

    @Override public Block getBlock(String path, Parameters frameworkParameters) throws FrameworkException {
        Block block = super.getBlock(path, frameworkParameters);
        if (block == null) {
            String categoryName = frameworkParameters.get(CATEGORY);
            if (categoryName != null) {
                boolean categoryOk = false;
                Block.Type[] mmbaseBlocks = ComponentRepository.getInstance().getBlockClassification("mmbase." + categoryName);
                if (mmbaseBlocks.length == 0) {
                    throw new FrameworkException("No such category mmbase." + categoryName);
                }
                return mmbaseBlocks[0].getBlocks().get(0);
            }
            return null;
        } else {
            return block;
        }
    }

    @Override protected void getNiceDirectoryUrl(StringBuilder b, Block block, Parameters blockParameters, Parameters frameworkParameters,  boolean action) throws FrameworkException {
        if (log.isDebugEnabled()) {
            log.debug("block '" + block  + "'  framework parameters " + frameworkParameters);
        }
        State state = getState(frameworkParameters);
        String category = frameworkParameters.get(CATEGORY);

        if (category == null && state.isRendering()) {
            category = state.getFrameworkParameters().get(CATEGORY);
        }
        b.append((category == null ? "_" : category));
        b.append('/');
        b.append(block.getComponent().getName());
        b.append('/');
        b.append(block.getName());

    }


    @Override protected Url getFilteredInternalDirectoryUrl(List<String> path, Map<String, ?> blockParameters, Parameters frameworkParameters) {
        if (path.size() == 0) {
            // nothing indicated after /mmbase/, don't know what to do, leaving unfiltered
            return Url.NOT;
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
                    return Url.NOT;
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
                return Url.NOT;
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
                    return Url.NOT;

                }
                url.append("&block=").append(block.getName());
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("internal URL " + url);
        }
        return new BasicUrl(this, url.toString());
    }

}
