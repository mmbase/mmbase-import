/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.render;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.taglib.CmscTag;
import com.finalist.pluto.portalImpl.aggregation.*;

/**
 * Container tag for a Screen
 * 
 * @author Wouter Heijke
 */
public class ScreenTag extends CmscTag {
   private static Log log = LogFactory.getLog(ScreenTag.class);

   private ScreenFragment screenFragment;

   private Page page;

   private Layout layout;


   @Override
   public void doTag() throws JspException, IOException {
      screenFragment = getScreenFragment();
      if (screenFragment != null) {
         page = screenFragment.getPage();
         layout = screenFragment.getLayout();

         log.debug("ScreenTag uses screen: '" + page.getTitle() + " (" + page.getId() + ") layout='" + layout.getId()
               + "'");

         // handle body, call any nested tags
         JspFragment frag = getJspBody();
         if (frag != null) {
            frag.invoke(null);
         }
         layout = null;
         page = null;
      }
      else {
         throw new JspException("Couldn't find Screen(Fragment)");
      }
      screenFragment = null;
   }


   protected ScreenFragment getScreenFragment() {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
      return (ScreenFragment) request.getAttribute(PortalConstants.FRAGMENT);
   }


   public Layout getLayout() {
      return layout;
   }


   public Page getPage() {
      return page;
   }


   protected PortletFragment getPortlet(String id) {
      Iterator<PortletFragment> portlets = screenFragment.getChildFragments().iterator();
      while (portlets.hasNext()) {
         PortletFragment pf = portlets.next();
         if (pf.getKey().equals(id)) {
            return pf;
         }
      }
      return null;
   }


   public Collection<PortletFragment> getAllPortlets() {
      return screenFragment.getChildFragments();
   }
}
