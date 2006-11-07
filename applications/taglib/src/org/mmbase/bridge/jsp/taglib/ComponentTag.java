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
import java.io.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;
import org.mmbase.framework.*;
import org.mmbase.module.core.MMBase;

/**
 * Renders a certain block of an mmbase component
 *
 * @author Michiel Meeuwissen
 * @version $Id: ComponentTag.java,v 1.10 2006-11-07 20:24:11 michiel Exp $
 * @since MMBase-1.9
 */
public class ComponentTag extends CloudReferrerTag implements ParamHandler, Writer {
    private static final Logger log = Logging.getLoggerInstance(ComponentTag.class);
    private Attribute name   = Attribute.NULL;
    private Attribute render   = Attribute.NULL;
    private Attribute blockName  = Attribute.NULL;
    private Attribute referids  = Attribute.NULL;

    protected final List<Map.Entry<String, Object>> extraParameters = new ArrayList<Map.Entry<String, Object>>();

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

    private boolean used = false;
    protected void getContent(java.io.Writer w) throws JspTagException {
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
            Renderer.Type type = rt == null || "".equals(rt) ? Renderer.Type.BODY : Renderer.Type.valueOf(rt.toUpperCase());
            Renderer renderer = block.getRenderer(type);
            Parameters params = block.createParameters();
            fillStandardParameters(params);
            params.setAutoCasting(true);
            params.setAll(Referids.getReferids(referids, this));
            for (Map.Entry<String, Object> entry : extraParameters) {
                params.set(entry.getKey(), entry.getValue());
            }
            Framework fw = MMBase.getMMBase().getFramework();
            Parameters frameworkParams = fw.createFrameworkParameters();
            fillStandardParameters(frameworkParams);
            frameworkParams.setAutoCasting(true);
            fw.render(renderer, params, frameworkParams, w);
            used = true;
        } catch (IOException ioe) {
            throw new TaglibException(ioe);
        }
    }


    public int doStartTag() throws JspException{
        super.doStartTag();
        used = false;
        helper.setValue(new Object() {
                final ComponentTag t = ComponentTag.this;
                public String toString() {
                    try {
                        StringWriter w = new StringWriter();
                        t.getContent(w);
                        return w.toString();
                    } catch (JspException je) {
                        return je.getMessage();
                    }
                }
            });
        return EVAL_BODY; // lets try _not_ buffering the body.
    }


    // if EVAL_BODY == EVAL_BODY_BUFFERED
    public int doAfterBody() throws JspTagException {

        if (EVAL_BODY == EVAL_BODY_BUFFERED) {
            try {
                if (bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch (IOException ioe) {
                throw new TaglibException(ioe);
            }
        }
        return SKIP_BODY;
    }
    protected void initDoEndTag() throws JspTagException {

    }


    public int doEndTag() throws JspTagException {
        int s = super.doEndTag();
        if (! used) { // should consider also write-attribute here
            getContent(pageContext.getOut());
        }
        return s;
    }
}
