/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.stats;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.*;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.taglib.CmscTag;
import com.finalist.cmsc.util.EncodingUtil;

/**
 * Tag that parses a netstat string with the current location.
 *
 * @author Bas Piepers, Remco Bos
 */
public class NedstatTag extends CmscTag {

   /*
    * Sitestat supports and guarantees only the use of the following characters
    * for counter names, destination url's / redirects or labels
    */
   private static final String ALLOWED_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-._";
   private static final String ALLOWED_CHARS_FOR_URLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-._/:";

   /*
    * For nedstat to work, one must also place the sitestat.js script in the
    * root of the site. The user is responsible for placing the tag in the right
    * location of the document. For instance: a technical measurement should
    * only be placed at the homepage, as low as possible (just before the
    * </body> tag). For more information, please read the sitestat manual.
    */

   protected String label;
   protected String type;
   protected String externalUrl;
   protected String landCode;
   protected String customerName;
   protected String siteName;
   protected String prefix;
   protected String counterName;
   protected String urlText;
   protected int nodeNumber;
   // user for banners. for banners the nedstat tag is on a different page
   protected int pageNumber;
   protected String pagePos; // e.g. 'links2'

   // The types of tags.
   private static final String TYPE_NORMAL = "normal";
   private static final String TYPE_TECHNICAL = "technical";
   private static final String TYPE_LOADTIME1 = "loadtime1";
   private static final String TYPE_LOADTIME2 = "loadtime2";
   private static final String TYPE_CLICKIN = "clickin";
   private static final String TYPE_CLICKOUT = "clickout";
   private static final String TYPE_BOOKMARK = "bookmark";
   private static final String TYPE_CONTENT = "content";
   private static final String TYPE_BANNER = "banner"; // banner is a "custom"
                                                         // TYPE_CLICKOUT used
                                                         // in
                                                         // bannerRedirect.jsp


   @Override
   public void doTag() throws JspException, IOException {
      /*
       * Only construct nedstat tag if this is the live production environment.
       * The production environment to be added in the context file is
       * 'server/production' set to 'true' or 'false'.
       */
       if (ServerUtil.isProduction() && (ServerUtil.isLive() || ServerUtil.isSingle())) {

         /* Validations */
         if (StringUtils.isEmpty(getCustomerName())) {
            throw new JspTagException("No customername provided.");
         }
         if (StringUtils.isEmpty(getSiteName())) {
            throw new JspTagException("No sitename provided.");
         }
         if (StringUtils.isEmpty(getType())) {
            throw new JspTagException("No type provided.");
         }
         if (type.equalsIgnoreCase(TYPE_CLICKIN) || type.equalsIgnoreCase(TYPE_CLICKOUT)) {
            if (StringUtils.isEmpty(getExternalUrl())) {
               throw new JspTagException("No external url provided for type " + getType() + ".");
            }
         }

         /* Defaults */
         if (StringUtils.isEmpty(getLandCode())) {
            setLandCode("nl"); // Assume 'nl'
         }

         /* Generate countername if necessary */
         if (getType().equalsIgnoreCase(TYPE_CONTENT)) {
            /*
             * If type is 'content', create a countername based on the 'rubriek'
             * of the node.
             */
            if (getNodeNumber() == 0) {
               throw new JspTagException("No contentId provided for type " + getType() + ".");
            }
            Cloud cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
            Node node = cloud.getNode(getNodeNumber());
            setCounterName(createCounterNameForNode(node));
         }
         else if (getType().equalsIgnoreCase(TYPE_BANNER)) {
            if (getNodeNumber() == 0) {
               throw new JspTagException("No contentId provided for type " + getType() + ".");
            }
            if (getPageNumber() == 0) {
               throw new JspTagException("No pageId provided for type " + getType() + ".");
            }
            if (getPagePos() == null) {
               throw new JspTagException("No pagePos provided for type " + getType() + ".");
            }
            Cloud cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
            Node bannerNode = cloud.getNode(getNodeNumber());
            setCounterName(createCounterNameForBanner(bannerNode, getPageNumber()));
         }
         else {
            /*
             * Otherwise, if the countername is empty, generate a countername
             * based on the path.
             */
            if (StringUtils.isEmpty(counterName)) {
               setCounterName(createCounterNameBasedOnPath());
            }
         }

         /* Construct the string according to the type. */
         String nedstat = createNedstat(getType());

         /* Output the result */
         PageContext ctx = (PageContext) getJspContext();
         ctx.getOut().print(nedstat);
      }
   }


   private String createNedstat(String type) throws JspTagException {
      String nedstat = null;
      PageContext ctx = (PageContext) getJspContext();
      if (type.equalsIgnoreCase(TYPE_NORMAL)) {
         nedstat = createSitestat4Script();
      }
      else if (type.equalsIgnoreCase(TYPE_TECHNICAL)) {
         nedstat = "<!-- Begin Sitestat4 Technical code -->\n<script type=\'text/javascript\' src=\'"
               + getJavascriptFile(ctx) + "\'></script>\n<!-- End Sitestat4 Technical Code -->";
      }
      else if (type.equalsIgnoreCase(TYPE_LOADTIME1)) {
         // Place just underneath the <head> tag.
         nedstat = "<!-- Begin Sitestat4 Loadingtime1 code -->\n<script type=\'text/javascript\'>ns_loadingtime1=(new Date()).getTime()</script>\n<!-- End Sitestat4 Loadingtime1 code -->";
      }
      else if (type.equalsIgnoreCase(TYPE_LOADTIME2)) {
         // Place just before the </body> tag, after the technical measurement.
         nedstat = "<!-- Begin Sitestat4 Loadingtime2 code -->\n<script type=\'text/javascript\'>ns_loadingtime2=(new Date()).getTime()</script>\n<!-- End Sitestat4 Loadingtime2 code -->";
      }
      else if (type.equalsIgnoreCase(TYPE_CLICKIN)) {
         nedstat = "<!-- Begin Sitestat4 Clickin code -->\n<a href=\"http://" + getLandCode() + ".sitestat.com/"
               + getCustomerName() + "/" + getSiteName() + "/s?" + getCounterName() + "&ns_type=clickin&ns_url="
               + getExternalUrl() + "\">" + getUrlText() + "</a>\n<!-- End Sitestat4 Clickin code -->";
      }
      else if (type.equalsIgnoreCase(TYPE_CLICKOUT)) {
         nedstat = "<!-- Begin Sitestat4 Clickout code -->\n<a href=\"http://" + getLandCode() + ".sitestat.com/"
               + getCustomerName() + "/" + getSiteName() + "/s?" + getCounterName() + "&ns_type=clickout&ns_url="
               + getExternalUrl() + "\">" + getUrlText() + "</a>\n<!-- End Sitestat4 Clickout code -->";
      }
      else if (type.equalsIgnoreCase(TYPE_BOOKMARK)) {
         // Place in head section.
         nedstat = "<link rel=\"shortcut icon\" href=\"http://" + getLandCode() + ".sitestat.com/" + getCustomerName()
               + "/" + getSiteName() + "/s?" + getCounterName() + "&ns_class=bookmark\" />";
      }
      else if (type.equalsIgnoreCase(TYPE_CONTENT)) {
         nedstat = createSitestat4Script();
      }
      else if (type.equalsIgnoreCase(TYPE_BANNER)) {
         StringBuffer sb = new StringBuffer();
         sb.append("<!-- Begin Sitestat4 Clickout code -->\n<script type=\'text/javascript\'>\n");
         sb.append("<!--\nfunction sitestat(ns_l){ns_pixelUrl=ns_l;\n");
         sb.append("if(document.images){ns_1=new Image();ns_1.src=ns_l;}else\n");
         sb.append("document.write(\'<img src=\"\'+ns_l+\'\" width=\"1\" height=\"1\" alt=\"\">\');}");
         sb.append("sitestat(\"http://" + getLandCode() + ".sitestat.com/" + getCustomerName() + "/" + getSiteName()
               + "/s?" + getCounterName() + "&ns_type=clickout&ns_action=view&ns__t=\"+(new Date()).getTime()+\""
               + "\");//-->\n</script>");
         sb.append("<noscript>\n<img src=\"http://" + getLandCode() + ".sitestat.com/" + getCustomerName() + "/"
               + getSiteName() + "/s?" + getCounterName()
               + "&ns_type=clickout&ns_action=view&ns__t=\"+(new Date()).getTime()\""
               + "\" width=\"1\" height=\"1\" alt=\"\" /></noscript>\n");
         sb.append("<!-- End Sitestat4 Clickout code -->");
         nedstat = sb.toString();
      }
      else {
         throw new JspTagException("Unsupported type: " + type);
      }
      return nedstat;
   }


   /*
    * Generate countername by taking the current page location (path). If the
    * current location is the site (http://[sitename]/) then the countername
    * should be the sitename only. Futhermore, if the last character is a '/',
    * then this character is ommitted from the countername.
    */
   private String createCounterNameBasedOnPath() throws JspException {
      String url = super.getPath();
      if (StringUtils.isBlank(url)) {
         throw new JspException("CounterName is empty and url is empty");
      }

      if (url.endsWith("/")) {
         url = url.substring(0, url.length() - 1);
      }

      // Site is provided seperately present. Strip site urlfragment from url
      int siteIndex = url.indexOf("/");
      if (siteIndex > -1) {
         url = url.substring(siteIndex + 1);
      }
      else {
         url = "homepage";
      }

      if (StringUtils.isNotEmpty(getPrefix())) {
         url = getPrefix() + "/" + url + "/index";
      }
      return url;
   }


   private String createCounterNameForNode(Node node) {
      Node creationchannel = RepositoryUtil.getCreationChannel(node);

      String prefix = getPrefix();
      if (StringUtils.isEmpty(prefix)) {
         String nodeType = node.getNodeManager().getName();
         prefix = nodeType;
      }
      String fullpath = creationchannel.getStringValue("path");
      String path = StringUtils.removeStart(fullpath, "Repository/");
      String title = EncodingUtil.convertNonAscii(node.getStringValue("title"));
      title = filterTitle(title);

      StringBuffer contentCounterName = new StringBuffer();
      contentCounterName.append(prefix);
      contentCounterName.append("/");
      contentCounterName.append(path);
      contentCounterName.append("/");
      contentCounterName.append(Integer.toString(getNodeNumber()));
      contentCounterName.append("_");
      contentCounterName.append(title);
      return contentCounterName.toString();
   }


   private String createCounterNameForBanner(Node bannerNode, int pageId) {

      String prefix = getPrefix();
      if (StringUtils.isEmpty(prefix)) {
         prefix = "banner";
      }
      String path = SiteManagement.getPath(pageId, true);
      String title = EncodingUtil.convertNonAscii(bannerNode.getStringValue("title"));
      title = filterTitle(title);

      // Site is provided seperately present. Strip site urlfragment from url
      int siteIndex = path.indexOf("/");
      if (siteIndex > -1) {
         path = path.substring(siteIndex + 1);
      }
      else {
         path = "homepage";
      }

      StringBuffer contentCounterName = new StringBuffer();
      contentCounterName.append(prefix);
      contentCounterName.append("/");
      contentCounterName.append(path);
      contentCounterName.append("/");
      contentCounterName.append(Integer.toString(bannerNode.getNumber()));
      contentCounterName.append("_");
      contentCounterName.append(title);
      return contentCounterName.toString();
   }


   private String createSitestat4Script() {
      StringBuffer sb = new StringBuffer();
      sb.append("<!-- Begin Sitestat4 code -->\n<script type=\'text/javascript\'>\n");
      sb.append("<!--\nfunction sitestat(ns_l){ns_l+=\'&ns__t=\'+(new Date()).getTime();ns_pixelUrl=ns_l;\n");
      sb.append("ns_0=document.referrer;\n");
      sb.append("ns_0=(ns_0.lastIndexOf(\'/\')==ns_0.length-1)?ns_0.substring(ns_0.lastIndexOf(\'/\'),0):ns_0;\n");
      sb.append("if(ns_0.length>0)ns_l+='&ns_referrer='+escape(ns_0);\n");
      sb.append("if(document.images){ns_1=new Image();ns_1.src=ns_l;}else\n");
      sb.append("document.write(\'<img src=\"\'+ns_l+\'\" width=\"1\" height=\"1\" alt=\"\">\');}");
      sb.append("sitestat(\"http://" + getLandCode() + ".sitestat.com/" + getCustomerName() + "/" + getSiteName()
            + "/s?" + getCounterName() + "\");//-->\n</script>");
      sb.append("<noscript>\n<img src=\"http://" + getLandCode() + ".sitestat.com/" + getCustomerName() + "/"
            + getSiteName() + "/s?" + getCounterName() + "\" width=\"1\" height=\"1\" alt=\"\" /></noscript>\n");
      sb.append("<!-- End Sitestat4 code -->");
      return sb.toString();
   }


   private String filterTitle(String title) {
      // make sure the title will not break into different path nodes
      return title.replace('/', '_');
   }


   private String filter(String string) {
      // replace forward slashes
      String temp1 = string.replace('.', '_'); // www.l1.nl --> www_l1_nl
      String temp2 = temp1.replace('/', '.'); // www_l1_nl/L1NW -->
                                                // www_l1_nl.L1NWS
      return filter(temp2, ALLOWED_CHARS);
   }


   private String filterUrl(String string) {
      return filter(string, ALLOWED_CHARS_FOR_URLS);
   }


   /*
    * Sitestat supports and guarantees only the use of the some characters. This
    * method returns a filtered String. All accentued chars will be replaced
    * with equivalent chars, invalid characters will be replaced by a
    * underscore.
    */
   private String filter(String string, final String charactersAllowed) {
      // replace accentued characters
      String temp = EncodingUtil.convertNonAscii(string);
      // Only allow valid characters, replace everyting else with '_'
      StringBuffer result = new StringBuffer();
      for (int i = 0; i < temp.length(); i++) {
         char curChar = temp.charAt(i);
         if (charactersAllowed.indexOf(curChar) > -1) {
            result.append(curChar);
         }
         else {
            result.append("_");
         }
      }
      return result.toString();
   }


   /**
    * @param ctx
    * @return
    */
   private String getJavascriptFile(PageContext ctx) {
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
      String javascriptFile = request.getContextPath() + "/js/sitestat.js";
      return javascriptFile;
   }


   public String getPrefix() {
      return prefix;
   }


   public void setPrefix(String prefix) {
      // Make sure prefix contains only valid characters
      this.prefix = filter(prefix);
   }


   public String getCounterName() {
      return counterName;
   }


   public void setCounterName(String counterName) {
      // Make sure counterName contains only valid characters
      this.counterName = filter(counterName);
   }


   public String getExternalUrl() {
      return externalUrl;
   }


   public void setExternalUrl(String externalUrl) {
      // Make sure externalUrl contains only valid characters
      this.externalUrl = filterUrl(externalUrl);
   }


   public String getLabel() {
      return label;
   }


   public void setLable(String tempLabel) {
      if (tempLabel.toLowerCase().startsWith("category=")) {
         tempLabel = StringUtils.substringAfter(tempLabel, "category=");
      }
      // Make sure label contains only valid characters
      this.label = "category=" + filter(tempLabel).toLowerCase();
   }


   public String getType() {
      return type;
   }


   public void setType(String type) {
      this.type = type;
   }


   public String getCustomerName() {
      return customerName;
   }


   public void setCustomerName(String customerName) {
      // Make sure customerName contains only valid characters
      this.customerName = filter(customerName);
   }


   public String getLandCode() {
      return landCode;
   }


   public void setLandCode(String landCode) {
      this.landCode = landCode;
   }


   public String getSiteName() {
      return siteName;
   }


   public void setSiteName(String siteName) {
      // Make sure siteName contains only valid characters
      this.siteName = filter(siteName);
   }


   public String getUrlText() {
      return urlText;
   }


   public void setUrlText(String urlText) {
      this.urlText = urlText;
   }


   public int getNodeNumber() {
      return nodeNumber;
   }


   public void setNodeNumber(int nodeNumber) {
      this.nodeNumber = nodeNumber;
   }


   public int getPageNumber() {
      return pageNumber;
   }


   public void setPageNumber(int pageNumber) {
      this.pageNumber = pageNumber;
   }


   public String getPagePos() {
      return pagePos;
   }


   public void setPagePos(String pagePos) {
      this.pagePos = pagePos;
   }
}
