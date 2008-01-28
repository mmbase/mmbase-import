/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.render;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.jsp.PageContext;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.portalImpl.headerresource.HeaderResource;
import com.finalist.cmsc.portalImpl.headerresource.LinkHeaderResource;
import com.finalist.cmsc.portalImpl.headerresource.MetaHeaderResource;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.CmscTag;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;

/**
 * meta types which stil can be added: "generator" content="" /> "rating"
 * http-equiv="rating" content="general" /> "distribution"
 * http-equiv="distribution" content="global" /> "robots" http-equiv="robots"
 * content="all" /> "revisit-after" http-equiv="revisit-after" content="1 week" />
 * "country" http-equiv="country" content="netherlands" />
 * 
 * @author freek
 */
public class HeaderContentTag extends CmscTag {

   private boolean dublin = false;


   public void setDublin(String dublin) {
      this.dublin = Boolean.valueOf(dublin);
   }


   @Override
   public void doTag() throws IOException {
      PageContext ctx = (PageContext) getJspContext();

      String path = getPath();
      Site site = SiteManagement.getSiteFromPath(path);
      NavigationItem item = SiteManagement.getNavigationItemFromPath(path);
      if (site != null) {
         String siteLanguage = site.getLanguage();

         ArrayList<HeaderResource> headerResources = new ArrayList<HeaderResource>();

         if (item != null) {
             headerResources.add(new MetaHeaderResource(false, "description", item.getDescription(), siteLanguage, null));
         }
         headerResources.add(new MetaHeaderResource(false, "author", site.getCreator(), siteLanguage, null));
         headerResources.add(new MetaHeaderResource(false, "copyright", site.getRights(), siteLanguage, null));
         headerResources.add(new MetaHeaderResource(false, "language", siteLanguage, null, "language"));
         headerResources.add(new MetaHeaderResource(false, "generator", "CMS Container", null, null));

         if (dublin) {
            headerResources
                  .add(new LinkHeaderResource(true, "schema.DC", "http://dublincore.org/documents/dces/", null));
         }

         ScreenTag container = (ScreenTag) findAncestorWithClass(this, ScreenTag.class);
         if (container != null) {
            Iterator<PortletFragment> portlets = container.getAllPortlets().iterator();
            while (portlets.hasNext()) {
               PortletFragment pf = portlets.next();
               Collection<HeaderResource> portletResources = pf.getHeaderResources();
               if (portletResources != null) {
                  headerResources.addAll(portletResources);
               }
            }
         }

         if (dublin) {
            headerResources.add(new MetaHeaderResource(true, "format", "text/html"));
            headerResources.add(new MetaHeaderResource(true, "type", "Collection"));
            headerResources.add(new MetaHeaderResource(true, "language", siteLanguage));
            if (item != null) {
                headerResources.add(new MetaHeaderResource(true, "title", item.getTitle()));
                headerResources.add(new MetaHeaderResource(true, "description", item.getDescription()));
            }
            headerResources.add(new MetaHeaderResource(true, "creator", site.getCreator()));
            headerResources.add(new MetaHeaderResource(true, "publisher", site.getPublisher()));
            headerResources.add(new MetaHeaderResource(true, "rights", site.getRights()));
            headerResources.add(new MetaHeaderResource(true, "source", site.getSource()));
         }

         String header = buildResponseHeader(dublin, headerResources);
         ctx.getOut().print(header);
      }
   }


   private String buildResponseHeader(boolean dublin, Collection<HeaderResource> headerResources) {
      StringBuffer header = new StringBuffer();

      HashSet<String> alreadyAdded = new HashSet<String>();
      for (HeaderResource resource : headerResources) {
         if (dublin || !resource.isDublin()) {
            String id = resource.toString();

            if (!alreadyAdded.contains(id)) {
               resource.render(header);
               header.append("\n");
               alreadyAdded.add(id);
            }
         }
      }

      return header.toString();
   }
}
