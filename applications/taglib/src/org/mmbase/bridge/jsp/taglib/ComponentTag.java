/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import org.mmbase.bridge.jsp.taglib.util.*;
import javax.servlet.jsp.*;

import org.mmbase.util.functions.*;
import org.mmbase.framework.*;

/**
 * Renders a certain block of an mmbase component
 *
 * @author Michiel Meeuwissen
 * @version $Id: ComponentTag.java,v 1.1 2006-10-13 15:27:45 michiel Exp $
 * @since MMBase-1.9
 */
public class ComponentTag extends ContextReferrerTag {

    private Attribute name   = Attribute.NULL;
    private Attribute type   = Attribute.NULL;
    private Attribute blockName  = Attribute.NULL;

    /**
     */
    public void setName(String c) throws JspTagException {
        name = getAttribute(c);
    }

    public void setType(String t) throws JspTagException {
        type = getAttribute(t);
    }
    public void setBlock(String b) throws JspTagException {
        blockName = getAttribute(b);
    }


    public int doStartTag() throws JspTagException{
        try {
            ComponentRepository rep = ComponentRepository.getInstance();
            Component component = rep.getComponent(name.getString(this));
            if (component == null) {
                throw new TaglibException("There is no component " + name.getString(this) + ". Known components are " + rep.getComponents());
            }
            Block block = component.getBlocks().get(blockName.getString(this));
            if (block == null) {
                throw new TaglibException("There is no block " + blockName.getString(this) + " in component " + component + ". Known blocks are " + component.getBlocks());
            }
            Renderer renderer = block.getRenderer(Renderer.Type.valueOf(type.getString(this).toUpperCase()));
            Parameters params = renderer.createParameters();
            fillStandardParameters(params);
            renderer.render(params, pageContext.getOut());
        } catch (java.io.IOException ioe) {
            throw new TaglibException(ioe);
        }
        return SKIP_BODY;
    }

}
