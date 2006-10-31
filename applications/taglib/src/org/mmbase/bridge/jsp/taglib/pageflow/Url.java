/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.util.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.module.core.MMBase; // TODO
import org.mmbase.bridge.jsp.taglib.util.Referids;
import org.mmbase.bridge.jsp.taglib.TaglibException;
import org.mmbase.util.Casting;
import org.mmbase.util.functions.*;
import org.mmbase.framework.*;

/**
 * <p>
 * A lazy 'URL' creator. A container object that contains all necessary to construct an URL, but
 * will only do it on actual request (by the {@link #get}) method. This is also what is stored by an
 * url-tag with an id attribute.
 * </p>
 * <p>
 * The creation of the URL is delegated to the MMBase framework.
 * </p>
 * @version $Id: Url.java,v 1.2 2006-10-31 15:10:04 michiel Exp $;
 * @since MMBase-1.9
 */
public class Url implements Comparable {

    private final UrlTag tag;
    private final String page;
    private final Component component;
    private final List<Map.Entry<String, Object>> params;

    public Url(UrlTag t, String p, Component comp, List<Map.Entry<String, Object>> pars) {
        tag = t;
        page = p;
        params = pars;
        component = comp;
    }

    public String get(boolean writeamp) throws JspTagException {

        // TODO result of this could be cached.

        // TODO we should not use core.
        Framework framework = MMBase.getMMBase().getFramework();

        // perhaps this?
        //Framework is of course always only relevant for local cloud context.
        //Framework framework = LocalContext.getCloudContext().getFramework();


        Parameters frameworkParams = framework.createFrameworkParameters();
        tag.fillStandardParameters(frameworkParams);
        Parameters blockParams;
        if (component != null) {
            Block block = component.getBlock(page);
            if (block == null) throw new TaglibException("There is no block " + page + " in component " + component);
            blockParams = block.getRenderer(Renderer.Type.HEAD).createParameters();
            tag.fillStandardParameters(blockParams);
            blockParams.setAutoCasting(true);
            for (Map.Entry<String, ?> entry : params) {
                blockParams.set(entry.getKey(), entry.getValue());
            }

        } else {
            // no component, this is a normal 'link'. no compoments, so no block can be guessed.
            blockParams = new Parameters(params); // sad that this will make it needed to copy the  entire list again.
        }
        StringBuilder show = framework.getUrl(page, component, blockParams, frameworkParams, writeamp);
        /*
        if (show.charAt(0) == '/') {
            HttpServletRequest req =  (HttpServletRequest) tag.getPageContext().getRequest();
            show.insert(0, req.getContextPath());
        }
        */

        return show.toString();
    }

    public String toString() {
        try {
            String string = get(true);
            // this means that it is written to page by ${_} and that consequently there _must_ be a body.
            // this is needed when body is not buffered.
            tag.haveBody();
            return string;
        } catch (Throwable e){
            return e.toString();
        }
    }
    public int compareTo(Object o) {
        return toString().compareTo(Casting.toString(o));
    }
}