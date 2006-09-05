/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.taglib;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.jsp.PageContext;

import net.sf.mmapps.commons.util.XmlUtil;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.portalImpl.headerresource.HeaderResource;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.aggregation.Fragment;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;


public class MetaTag extends CmscTag {

    private boolean dublin;
    
    public void setDublin(String dublin) {
        this.dublin = Boolean.valueOf(dublin);
    }
    
    public void doTag() throws IOException {
        PageContext ctx = (PageContext) getJspContext();

        String path = getPath();
        Site site = SiteManagement.getSiteFromPath(path);
        Page page = SiteManagement.getPageFromPath(path);
        if (site != null) {
            String siteLanguage = site.getLanguage();
            
            StringBuffer header = new StringBuffer();
            
            addMeta(header, "description", page.getDescription(), siteLanguage, null);
            addMeta(header, "author", site.getCreator(), siteLanguage, null);
            addMeta(header, "copyright", site.getRights(), siteLanguage, null);
            addMeta(header, "language", siteLanguage, null, "language");
            
            ScreenTag container = (ScreenTag) findAncestorWithClass(this, ScreenTag.class);
            if (container != null) {
                Iterator portlets = container.getAllPortlets().iterator();
                while(portlets.hasNext()) {
                    Fragment fragment = (Fragment) portlets.next();
                    if (fragment instanceof PortletFragment) {
                        PortletFragment pf = (PortletFragment) fragment;
                        HeaderResource resource = pf.getHeaderResource();
                        header.append(resource.toString(true, false, false, false));
                    }
                }
            }
    //        addMeta(header, "rating" http-equiv="rating" content="general" />
    //        addMeta(header, "distribution" http-equiv="distribution" content="global" />
    //        addMeta(header, "robots" http-equiv="robots" content="all" />
    //        addMeta(header, "revisit-after" http-equiv="revisit-after" content="1 week" />
    //        addMeta(header, "country" http-equiv="country" content="netherlands" />
    //        addMeta(header, "date" content="" />
    //        addMeta(header, "generator" content="" />
    
    //        <meta name="keywords" lang="nl" content="" />
            
            if (dublin) {
                header.append("<link rel=\"schema.DC\" href=\"http://dublincore.org/documents/dces/\" />\n");
    
                addMeta(header, "DC.Format", "text/html", null, null);
                addMeta(header, "DC.Type", "text", null, null);
                addMeta(header, "DC.Language", siteLanguage, null, null);
                addMeta(header, "DC.Title", page.getTitle(), null, null);
                addMeta(header, "DC.Creator", site.getCreator(), null, null);
                addMeta(header, "DC.Publisher", site.getPublisher(), null, null);
                addMeta(header, "DC.Description", page.getDescription(), null, null);
                addMeta(header, "DC.Rights", site.getRights(), null, null);
    //            addMeta(header, "DC.Subject", "", null, null);
    //            addMeta(header, "DC.Date", "", null, null);
    //            addMeta(header, "DC.Identifier" scheme="URL" content="http://", null, null);
    //            addMeta(header, "DC.Source", "http://", null, null);
    //            addMeta(header, "DC.Relation.IsPartOf", "http://", null, null);
            }
            
            ctx.getOut().print(header.toString());
        }
    }
    
    private void addMeta(StringBuffer buffer, String name, String value, String lang, String header) {
        if (value != null) {
            buffer.append("<meta name=\"").append(name).append("\" content=\"").append(
                    XmlUtil.xmlEscape(value)).append("\" ");
            if (lang != null) {
                buffer.append("lang=\"").append(lang).append("\" ");
            }
            if (header != null) {
                buffer.append("http-equiv=\"").append(header).append("\" ");
            }
            buffer.append("/>\n");
        }
    }
    
}
