package com.finalist.cmsc.portlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.mmbase.RelationUtil;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.util.HttpUtil;

public abstract class AbstractLoginPortlet extends CmscPortlet{
   protected String DEFAULT_EMAIL_CONFIRM_TEMPLATE_DIR = "../templates/view/login/confirmation.txt";
   protected static final String EMAIL_SUBJECT = "emailSubject";
   protected static final String EMAIL_TEXT = "emailText";
   protected static final String EMAIL_FROMEMAIL = "emailFromEmail";
   protected static final String EMAIL_FROMNAME = "emailFromName";
   
   protected static final String DEFAULT_EMAILREGEX = "^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$";

   
   private static final Log log = LogFactory.getLog(AbstractLoginPortlet.class);
   
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException,
   PortletException {
      PortletPreferences preferences = req.getPreferences();
      setAttribute(req, EMAIL_SUBJECT, preferences.getValue(EMAIL_SUBJECT,""));
     // setAttribute(req, EMAIL_TEXT, preferences.getValue(EMAIL_TEXT,getConfirmationTemplate()));
      setAttribute(req, EMAIL_TEXT, preferences.getValue(EMAIL_TEXT,getConfirmationTemplate()));
      setAttribute(req, EMAIL_FROMEMAIL, preferences.getValue(EMAIL_FROMEMAIL,""));
      setAttribute(req, EMAIL_FROMNAME, preferences.getValue(EMAIL_FROMNAME,""));
      
      super.doEditDefaults(req, res);
   }
   
   @Override
   public void processEditDefaults(ActionRequest request,
         ActionResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
      setPortletParameter(portletId, EMAIL_SUBJECT, request.getParameter(EMAIL_SUBJECT));
      setPortletParameter(portletId, EMAIL_TEXT, request.getParameter(EMAIL_TEXT));
      setPortletParameter(portletId, EMAIL_FROMEMAIL, request.getParameter(EMAIL_FROMEMAIL));
      setPortletParameter(portletId, EMAIL_FROMNAME, request.getParameter(EMAIL_FROMNAME));

      super.processEditDefaults(request, response);
   }
   protected String getEmailBody(String emailText,ActionRequest request,
         Authentication authentication, Person person) {
      Cloud cloud = getCloudForAnonymousUpdate(false);
      String pageId = request.getParameter("page");
      String url = getConfirmationLink(cloud,pageId);
      String confirmUrl = HttpUtil.getWebappUri((HttpServletRequest) request)
            + "login/confirm.do?s=" + authentication.getId() + url;
      
      return String.format(emailText == null?getConfirmationTemplate():emailText, authentication
            .getUserId(), authentication.getPassword(), person.getFirstName(),
            person.getInfix(), person.getLastName(), confirmUrl);
   }
   
   protected Cloud getCloudForAnonymousUpdate(boolean isRemote) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      if (isRemote) {
         return Publish.getRemoteCloud(cloud);
      }
      return cloud;
   }

   protected String getConfirmationLink(Cloud cloud,String pageId) {
      String link = null;
      NodeList portletDefinations = SearchUtil.findNodeList(cloud,
            "portletdefinition", "definition", this.getPortletName());
      Node regiesterPortletDefination = portletDefinations.getNode(0);
      if (portletDefinations.size() > 1) {
         log.error("found " + portletDefinations.size()
               + " regiesterPortlet nodes; first one will be used");
      }
      NodeList portlets = regiesterPortletDefination.getRelatedNodes("portlet",
            "definitionrel", SearchUtil.SOURCE);
      Node portlet = null;
      Relation relation = null;
      Node page = cloud.getNode(Integer.parseInt(pageId));
      NodeList nodeList = page.getRelatedNodes("portlet", "portletrel", SearchUtil.DESTINATION);
      for (int i =0 ; i < nodeList.size() ; i++) {
         for (int j = 0 ; j < portlets.size() ; j++) {
            if (nodeList.getNode(i).getNumber() == portlets.getNode(j).getNumber()) {
               portlet = nodeList.getNode(i);
            }
         }
      }
      relation = RelationUtil.getRelation(cloud.getRelationManager("portletrel"), page.getNumber(), portlet.getNumber());

      link = "&pn=" + page.getNumber();  
      if (relation != null) {
         String name = relation.getStringValue("name");
         if (name != null) {
            link += "&nm=" + name;
         }
      }
      
      return link;
   }
   
   protected String getConfirmationTemplate() {
      InputStream is = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(DEFAULT_EMAIL_CONFIRM_TEMPLATE_DIR);
      if (is == null) {
         throw new NullPointerException(
               "The confirmation template file confirmation.txt in directory 'templates/view/login' does't exist.");
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();
      String strLine;
      try {
         while ((strLine = reader.readLine()) != null) {
            sb.append(strLine + "\n");
         }
      } catch (IOException e) {
         log.error("error happen when reading email template", e);
      }
      
      return sb.toString();
   }
   
   protected boolean isEmailAddress(String emailAddress) {
      if (emailAddress == null) {
         return false;
      }
      if (StringUtils.isBlank(emailAddress)) {
         return false;
      }

      String emailRegex = getEmailRegex();
      return emailAddress.trim().matches(emailRegex);
   }

   protected String getEmailRegex() {
      String emailRegex = PropertiesUtil.getProperty("email.regex");
      if (StringUtils.isNotBlank(emailRegex)) {
         return emailRegex;
      }
      return DEFAULT_EMAILREGEX;
   }
}
