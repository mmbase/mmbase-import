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
   <% //----------------------- Filemanagement comes from here ----------------------- %>
   <mm:import id="editcontextname" reset="true">filemanagement</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
      <a href='javascript:clickNode("filemanagement_0")'><img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle' id='img_filemanagement_0'/></a>&nbsp;<img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/>&nbsp;<nobr><a href='javascript:clickNode("filemanagement_0")'><di:translate key="education.filemanagement" /></a></nobr>
      <br>
      <%
         int iComponentsAvailable = 0;
      %>

      <mm:node number="component.scorm" notfound="skip">
         <%
            iComponentsAvailable++;
         %>
      </mm:node>

      <mm:import id="components_available" reset="true"><%= iComponentsAvailable %></mm:import>
      <div id='filemanagement_0' style='display: none'>
         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <mm:isgreaterthan  referid="components_available" value="0">
                  <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
               </mm:isgreaterthan>
               <mm:isgreaterthan  referid="components_available" value="0" inverse="true">
                  <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>
               </mm:isgreaterthan>
               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href='<mm:treefile write="true" page="/education/filemanagement/index.jsp" objectlist="$includePath" />' title="<di:translate key="education.ftpupload" />" target="text"><di:translate key="education.ftpupload" /></a></nobr></td>
            </tr>
         </table>


         <mm:node number="component.scorm" notfound="skip">
            <table border="0" cellpadding="0" cellspacing="0">
               <tr>
                  <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                  <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>
                  <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
                  <td><nobr>&nbsp;<a href='<mm:treefile write="true" page="/education/scorm/index.jsp" objectlist="$includePath" />' title="<di:translate key="education.ftpupload" />" target="text"><di:translate key="education.scormimport" /></a></nobr></td>
               </tr>
            </table>
         </mm:node>
      </div>
   </mm:islessthan>
</mm:cloud>
