/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.render.tiles;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.taglib.CmscTag;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.aggregation.ScreenFragment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

/**
 * Container tag for a Screen
 *
 * @author Wouter Heijke
 */
public class ScreenTag extends CmscTag {

    private static Log log = LogFactory.getLog(ScreenTag.class);

    private ScreenFragment screenFragment;
    private Set<String> registeredLayout = new HashSet<String>();


    @Override
    public void doTag() throws JspException, IOException {

        getScreenFragment();

        getJspContext().setAttribute("container",this, PageContext.REQUEST_SCOPE);

        // handle body, call any nested tags
        JspFragment frag = getJspBody();
        if (frag != null) {
            frag.invoke(null);
        }

    }


    void registerLayout(String layoutId) throws JspException {

        if(registeredLayout.contains(layoutId)){
            throw new JspException("A layout id already registered:"+layoutId);
        }

        registeredLayout.add(layoutId);


    }
    protected void getScreenFragment() throws JspException {

        screenFragment = (ScreenFragment) getJspContext().getAttribute(PortalConstants.FRAGMENT, PageContext.REQUEST_SCOPE);

        if (null == screenFragment) {
            throw new JspException("Couldn't find Screen(Fragment)");
        }


        log.debug("ScreenTag uses screen:" + screenFragment);
    }

    protected PortletFragment getPortlet(String id) {


        for (PortletFragment portletFragment : screenFragment.getChildFragments()) {
            if (portletFragment.getKey().equals(id)) {
                return portletFragment;
            }
        }

        return null;
    }


}
