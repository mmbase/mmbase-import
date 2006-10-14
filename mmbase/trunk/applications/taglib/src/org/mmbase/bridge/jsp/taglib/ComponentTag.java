/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import org.mmbase.bridge.jsp.taglib.util.*;
import javax.servlet.jsp.*;
import java.util.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;
import org.mmbase.framework.*;
import org.mmbase.module.core.MMBase;

/**
 * Renders a certain block of an mmbase component
 *
 * @author Michiel Meeuwissen
 * @version $Id: ComponentTag.java,v 1.4 2006-10-14 09:46:16 michiel Exp $
 * @since MMBase-1.9
 */
public class ComponentTag extends CloudReferrerTag implements ParamHandler {
    private static final Logger log = Logging.getLoggerInstance(ComponentTag.class);
    private Attribute name   = Attribute.NULL;
    private Attribute render   = Attribute.NULL;
    private Attribute blockName  = Attribute.NULL;
    private Attribute referids  = Attribute.NULL;

    protected final List<Map.Entry<String, ?>> extraParameters = new ArrayList<Map.Entry<String, ?>>();

    /**
     */
    public void setName(String c) throws JspTagException {
        name = getAttribute(c);
    }

    public void setRender(String r) throws JspTagException {
        render = getAttribute(r);
    }
    public void setBlock(String b) throws JspTagException {
        blockName = getAttribute(b);
    }

    public void addParameter(String key, Object value) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("adding parameter " + key + "/" + value);
        }
        extraParameters.add(new Entry<String, Object>(key, value));
    }



    public int doStartTag() throws JspTagException{
        try {
            ComponentRepository rep = ComponentRepository.getInstance();
            Component component = rep.getComponent(name.getString(this));
            if (component == null) {
                throw new TaglibException("There is no component " + name.getString(this) + ". Known components are " + rep.getComponents());
            }
            String bn = blockName.getString(this);
            Block block = bn.equals("") ? component.getDefaultBlock() : component.getBlock(bn);
            if (block == null) {
                throw new TaglibException("There is no block " + blockName.getString(this) + " in component " + component + ". Known blocks are " + component.getBlocks());
            }
            String rt = render.getString(this);
            Renderer.Type type = rt == null || "".equals(rt) ? Renderer.Type.BODY : Renderer.Type.valueOf(rt);
            Renderer renderer = block.getRenderer(type);
            Parameters params = renderer.createParameters();
            fillStandardParameters(params);
            params.setAutoCasting(true);
            params.setAll(Referids.getReferids(referids, this));

            Parameters frameworkParams = MMBase.getMMBase().getFramework().createFrameworkParameters();
            fillStandardParameters(frameworkParams);
            frameworkParams.setAutoCasting(true);

            renderer.render(params, frameworkParams, pageContext.getOut());
        } catch (java.io.IOException ioe) {
            throw new TaglibException(ioe);
        }
        return SKIP_BODY;
    }

}
