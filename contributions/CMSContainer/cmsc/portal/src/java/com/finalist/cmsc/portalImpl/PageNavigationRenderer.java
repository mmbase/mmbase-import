package com.finalist.cmsc.portalImpl;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.Layout;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Portlet;
import com.finalist.cmsc.beans.om.PortletDefinition;
import com.finalist.cmsc.beans.om.View;
import com.finalist.cmsc.navigation.NavigationInformationProvider;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.portalImpl.registry.PortalRegistry;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.aggregation.EmptyFragment;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.aggregation.ScreenFragment;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;
import com.finalist.util.module.ModuleUtil;

public class PageNavigationRenderer implements NavigationItemRenderer {

   protected static final String FEATURE_RSSFEED = "rssfeed";
   protected static final String FEATURE_PAGEWIZARD = "pagewizarddefinition";
   protected static final String FEATURE_WORKFLOW = "workflowitem";

   private static Log log = LogFactory.getLog(NavigationItemRenderer.class);


   public void render(NavigationItem item, HttpServletRequest request, HttpServletResponse response,
         ServletContext servletContext, ServletConfig sc, PortalRegistry registry) {
      if (item instanceof Page) {
         ScreenFragment screen = getScreen((Page) item, sc);

         registry.setScreen(screen);
         log.debug("===>SERVICE");
         try {
            screen.service(request, response);
         }
         catch (ServletException e) {
            throw new RenderException("ServletException while rendering", e);
         }
         catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RenderException("IOException while rendering", e);
         }
         log.debug("===>SERVICE DONE");
      }
      else {
         throw new IllegalArgumentException(
               "Got a wrong type in the PageNavigationRenderer (only wants Page or derived classes), was"
                     + item.getClass());
      }
   }


   protected ScreenFragment getScreen(Page page, ServletConfig sc) {
      try {
         Layout layout = SiteManagement.getLayout(page.getLayout());
         ScreenFragment sf = new ScreenFragment(sc, page, layout);
         // place portletfragments and emptyfragments in the screenfragment

         Set<String> names = layout.getNames();
         for (Iterator iter = names.iterator(); iter.hasNext();) {
            String layoutId = (String) iter.next();
            Integer portletId = page.getPortlet(layoutId);
            Portlet portlet = SiteManagement.getPortlet(portletId);
            if (portlet != null) {
               PortletDefinition definition = SiteManagement.getPortletDefinition(portlet.getDefinition());
               View view = SiteManagement.getView(portlet.getView());

               PortletFragment pf = new PortletFragment(sc, sf, layoutId, portlet, definition, view);
               if (pf != null) {
                  sf.addChild(pf);
               }
            }
            else {
               PortletFragment pf = createDefaultPortlet(sf, page, layoutId, sc);
               if (pf != null) {
                  sf.addChild(pf);
               }
            }
         }

         return sf;
      }
      catch (Exception e) {
         log.error("Error while constructing screen for page: '" + page.getId() + "'", e);
      }
      return null;
   }


   private PortletFragment createDefaultPortlet(ScreenFragment sf, Page page, String layoutId, ServletConfig sc) {
      try {
         Portlet empty = SiteManagement.getPortlet(-1);
         PortletDefinition definition = SiteManagement.getPortletDefinition(empty.getDefinition());
         page.addPortlet(layoutId, -1);
         EmptyFragment ef = new EmptyFragment(sc, sf, layoutId, empty, definition);
         return ef;
      }
      catch (Exception e) {
         log.error("cannot create default portlet");
         if (log.isDebugEnabled()) {
            log.debug(e);
         }
         return null;
      }
   }


   public TreeElement getTreeElement(NavigationInformationProvider renderer, Node parentNode, NavigationItem item,
         TreeModel model) {
      UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentNode, false);
      boolean secure = parentNode.getBooleanValue(PagesUtil.SECURE_FIELD);
      TreeElement element = renderer.createElement(item, role, renderer.getOpenAction(parentNode, secure));

      if (SecurityUtil.isEditor(role)) {
         element.addOption(renderer.createOption("edit_defaults.png", "site.page.edit", "PageEdit.do?number="
               + parentNode.getNumber()));
         element.addOption(renderer.createOption("new.png", "site.page.new", "PageCreate.do?parentpage="
               + parentNode.getNumber()));

         if (ModuleUtil.checkFeature(FEATURE_RSSFEED)) {
            element.addOption(renderer.createOption("rss_new.png", "site.rss.new",
                  "../rssfeed/RssFeedCreate.do?parentpage=" + parentNode.getNumber()));
         }

         if (SecurityUtil.isWebmaster(role)
               || (model.getChildCount(parentNode) == 0 && !Publish.isPublished(parentNode))) {
            element.addOption(renderer.createOption("delete.png", "site.page.remove", "PageDelete.do?number="
                  + parentNode.getNumber()));
         }

         if (NavigationUtil.getChildCount(parentNode) >= 2) {
            element.addOption(renderer.createOption("reorder.png", "site.page.reorder", "reorder.jsp?parent="
                  + parentNode.getNumber()));
         }

         if (SecurityUtil.isChiefEditor(role)) {
            element.addOption(renderer.createOption("cut.png", "site.page.cut", "javascript:cut('"
                  + parentNode.getNumber() + "');"));
            element.addOption(renderer.createOption("copy.png", "site.page.copy", "javascript:copy('"
                  + parentNode.getNumber() + "');"));
            element.addOption(renderer.createOption("paste.png", "site.page.paste", "javascript:paste('"
                  + parentNode.getNumber() + "');"));
         }

         if (ModuleUtil.checkFeature(FEATURE_PAGEWIZARD)) {
            element.addOption(renderer.createOption("wizard.png", "site.page.wizard",
                  "../pagewizard/StartPageWizardAction.do?number=" + parentNode.getNumber()));
         }

         if (SecurityUtil.isWebmaster(role) && ModuleUtil.checkFeature(FEATURE_WORKFLOW)) {
            element.addOption(renderer.createOption("publish.png", "site.page.publish",
                  "../workflow/publish.jsp?number=" + parentNode.getNumber()));
            element.addOption(renderer.createOption("masspublish.png", "site.page.masspublish",
                  "../workflow/masspublish.jsp?number=" + parentNode.getNumber()));
         }
      }
      element.addOption(renderer.createOption("rights.png", "site.page.rights",
            "../usermanagement/pagerights.jsp?number=" + parentNode.getNumber()));

      return element;
   }
}
