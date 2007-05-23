<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>  
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<jsp:directive.page import="java.util.*,nl.didactor.component.education.utils.*" />
<jsp:scriptlet>
  String imageName = "";
  String sAltText = "";
</jsp:scriptlet>
<mm:cloud method="delegate" authenticate="asis" jspvar="cloud">
  <jsp:directive.include file="/shared/setImports.jsp" />
  <jsp:directive.include file="../mode.include.jsp" />
  <jsp:directive.include file="/education/wizards/roles_defs.jsp" />
  <% //----------------------- virtualclassroom come from here ----------------------- %>
  <mm:node number="component.proactivemail" notfound="skipbody">
    <mm:import id="editcontextname" reset="true">proactivemail</mm:import>
    <%@include file="/education/wizards/roles_chk.jsp" %>
    <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
      <a href='javascript:clickNode("proactivemail_0")'><img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle' id='img_proactivemail_0'/></a>&nbsp;<img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/>&nbsp;<nobr><a href='javascript:clickNode("proactivemail_0")' title="<di:translate key="proactivemail.proactivemail" />" ><di:translate key="proactivemail.proactivemail" /></a></nobr>
      <br>
      <mm:import jspvar="langLocale" reset="true"><mm:write referid="language" /></mm:import>
      <div id='proactivemail_0' style='display: none'>
      
         <%
            String[][] arrstrContentMetadataConfig = new String[4][4];
            java.util.Locale loc = new java.util.Locale(langLocale);
            int singular = NodeManager.GUI_SINGULAR;

            arrstrContentMetadataConfig[0][0]  = cloud.getNodeManager("proactivemailtemplates").getGUIName(singular, loc);
            arrstrContentMetadataConfig[1][0]  = cloud.getNodeManager("proactivemailtemplatetypes").getGUIName(singular, loc);
            arrstrContentMetadataConfig[2][0]  = cloud.getNodeManager("proactivemailscheduler").getGUIName(singular, loc);
            arrstrContentMetadataConfig[3][0]  = cloud.getNodeManager("proactivemailscripts").getGUIName(singular, loc);

            arrstrContentMetadataConfig[0][1] = "config/proactivemail/proactivemailtemplates";
            arrstrContentMetadataConfig[1][1] = "config/proactivemail/proactivemailtemplatetypes";
            arrstrContentMetadataConfig[2][1] = "config/proactivemail/proactivemailscheduler";
            arrstrContentMetadataConfig[3][1] = "config/proactivemail/proactivemailscripts";

            arrstrContentMetadataConfig[0][2] = "proactivemailtemplates";
            arrstrContentMetadataConfig[1][2] = "proactivemailtemplatetypes";
            arrstrContentMetadataConfig[2][2] = "proactivemailscheduler";
            arrstrContentMetadataConfig[3][2] = "proactivemailscripts";

            arrstrContentMetadataConfig[0][3] = "name";
            arrstrContentMetadataConfig[1][3] = "name";
            arrstrContentMetadataConfig[2][3] = "name";
            arrstrContentMetadataConfig[3][3] = "name";


            session.setAttribute("content_metadata_names", arrstrContentMetadataConfig);
          
            int levelVisible = 0;
            %>
            <di:hasrole role="teacher">
              <% levelVisible = 1; %>
            </di:hasrole>
            <di:hasrole role="systemadministrator">
              <% levelVisible = 4; %>
            </di:hasrole>
            <%
            for (int f = 0; f < levelVisible; f++)
            {
               %>
               <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                     <% if ( f == levelVisible-1 ) { %>
                       <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>
                     <%} else { %>
                       <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
                     <%}%>
                     <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
                     <mm:import id="template" reset="true"><mm:write referid="listjsp"/>&wizard=<%= arrstrContentMetadataConfig[f][1] %>&nodepath=<%= arrstrContentMetadataConfig[f][2] %>&searchfields=<%= arrstrContentMetadataConfig[f][3] %>&fields=<%= arrstrContentMetadataConfig[f][3] %>&search=yes&orderby=<%= arrstrContentMetadataConfig[f][3] %>&metadata=yes<mm:write referid="forbidtemplate" escape="text/plain" /></mm:import>
                     <td><nobr>&nbsp;<a href='<mm:write referid="template" escape="text/plain" />&path=' title='<di:translate key="education.edit" /> <%= arrstrContentMetadataConfig[f][0] %>' target="text"><%= arrstrContentMetadataConfig[f][0] %></a></nobr></td>
                  </tr>
               </table>
               <%
            }
         %>
      </div>
    </mm:islessthan>
  </mm:node>
</mm:cloud>
