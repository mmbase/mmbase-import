<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>  
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<jsp:directive.page import="java.util.*,nl.didactor.component.education.utils.*,org.mmbase.bridge.*" />
<jsp:scriptlet>
  String imageName = "";
  String sAltText = "";
</jsp:scriptlet>
<mm:cloud method="delegate" authenticate="asis" jspvar="cloud">
  <jsp:directive.include file="/shared/setImports.jsp" />
  <jsp:directive.include file="../mode.include.jsp" />
  <jsp:directive.include file="/education/wizards/roles_defs.jsp" />
   <% //----------------------- Metadata for components comes from here ----------------------- %>
   <mm:import id="editcontextname" reset="true">contentelementen</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
      <a href='javascript:clickNode("content_metadata_0")'><img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle' id='img_content_metadata_0'/></a>&nbsp;<img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/>&nbsp;<nobr><a href='javascript:clickNode("content_metadata_0")' title="<di:translate key="education.educationmenucontentmetadata" />"><di:translate key="education.educationmenucontentmetadata" /></a></nobr>
      <br>
      <mm:import jspvar="langLocale"><mm:write referid="language" /></mm:import>
      <div id='content_metadata_0' style='display: none'>
         <%
            String[][] arrstrContentMetadataConfig = new String[5][4];
            java.util.Locale loc = new java.util.Locale(langLocale);
            int singular = NodeManager.GUI_SINGULAR;

            arrstrContentMetadataConfig[0][0]  = cloud.getNodeManager("images").getGUIName(singular, loc);
            arrstrContentMetadataConfig[1][0]  = cloud.getNodeManager("attachments").getGUIName(singular, loc);
            arrstrContentMetadataConfig[2][0]  = cloud.getNodeManager("audiotapes").getGUIName(singular, loc);
            arrstrContentMetadataConfig[3][0]  = cloud.getNodeManager("videotapes").getGUIName(singular, loc);
            arrstrContentMetadataConfig[4][0]  = cloud.getNodeManager("urls").getGUIName(singular, loc);

            arrstrContentMetadataConfig[0][1] = "config/image/image";
            arrstrContentMetadataConfig[1][1] = "config/attachment/attachment";
            arrstrContentMetadataConfig[2][1] = "config/audiotape/audiotapes";
            arrstrContentMetadataConfig[3][1] = "config/videotape/videotapes";
            arrstrContentMetadataConfig[4][1] = "config/url/urls";

            arrstrContentMetadataConfig[0][2] = "images";
            arrstrContentMetadataConfig[1][2] = "attachments";
            arrstrContentMetadataConfig[2][2] = "audiotapes";
            arrstrContentMetadataConfig[3][2] = "videotapes";
            arrstrContentMetadataConfig[4][2] = "urls";

            arrstrContentMetadataConfig[0][3] = "title";
            arrstrContentMetadataConfig[1][3] = "title";
            arrstrContentMetadataConfig[2][3] = "title";
            arrstrContentMetadataConfig[3][3] = "title";
            arrstrContentMetadataConfig[4][3] = "name";


            session.setAttribute("content_metadata_names", arrstrContentMetadataConfig);

            for (int f = 0; f < arrstrContentMetadataConfig.length; f++)
            {
               %>
               <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                     <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
                     <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
                     <mm:import id="template" reset="true"><mm:write referid="listjsp"/>&wizard=<%= arrstrContentMetadataConfig[f][1] %>&nodepath=<%= arrstrContentMetadataConfig[f][2] %>&searchfields=<%= arrstrContentMetadataConfig[f][3] %>&fields=<%= arrstrContentMetadataConfig[f][3] %>&search=yes&orderby=<%= arrstrContentMetadataConfig[f][3] %>&metadata=yes<mm:write referid="forbidtemplate" escape="text/plain" /></mm:import>
                     <td><nobr>&nbsp;<a href='<mm:write referid="template" escape="text/plain" />&path=' title='<di:translate key="education.edit" /> <%= arrstrContentMetadataConfig[f][0] %>' target="text"><%= arrstrContentMetadataConfig[f][0] %></a></nobr></td>
                  </tr>
               </table>
               <%
            }
         %>
               <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                     <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
                     <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
                     <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/provider/providers&nodepath=providers&searchfields=name&fields=name&orderby=name&path=' target="text">Content paginas (CMS)</a></nobr></td>
                  </tr>
               </table>
                <% //////////////////////////////////////////////// CMS ///////////////////////////////////////////////// %>

            <% // add portalpages %>
            <mm:node number="component.portalpages" notfound="skip">
              <mm:treeinclude page="/portalpages/backoffice/add_portalpages.jsp" objectlist="" referids="listjsp,wizardjsp" />
            </mm:node>

            <% // add help %>
            <mm:node number="component.cmshelp" notfound="skip">
              <mm:treeinclude page="/cmshelp/backoffice/add_help.jsp" objectlist="" referids="listjsp,wizardjsp" />
            </mm:node>

            <% // add faq %>
            <mm:node number="component.faq" notfound="skip">
              <mm:treeinclude page="/faq/backoffice/add_faq.jsp" objectlist="" referids="listjsp,wizardjsp" />
            </mm:node>

            <% // add news %>
            <mm:node number="component.news" notfound="skip">
              <mm:treeinclude page="/news/backoffice/add_news.jsp" objectlist="" referids="listjsp,wizardjsp" />
            </mm:node>
         <% //////////////////////////////////////////////// CMS ///////////////////////////////////////////////// %>
      </div>
   </mm:islessthan>
</mm:cloud>
