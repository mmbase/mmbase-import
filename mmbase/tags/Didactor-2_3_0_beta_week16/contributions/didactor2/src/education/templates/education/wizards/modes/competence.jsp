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
   <% //----------------------- Competence comes from here ----------------------- %>
   <mm:import id="editcontextname" reset="true">competentie</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
      <a href='javascript:clickNode("competence_0")'><img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle' id='img_competence_0'/></a>&nbsp;<img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/><nobr>&nbsp;<a href='javascript:clickNode("competence_0")' title="<di:translate key="competence.competencetreerootdescription" />"><di:translate key="competence.competencetreeroot" /></nobr></a>
      <br>
      <div id='competence_0' style='display: none'>
         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href='<%= request.getContextPath() %>/competence/competence_by_type.jsp' title="<di:translate key="competence.competencetreeitemcompetencesdescription" />" target="text"><di:translate key="competence.competencetreeitemcompetences" /></a></nobr></td>
            </tr>
         </table>

         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/core/coretasks&nodepath=coretasks&searchfields=name&fields=name<mm:write referid="forbidtemplate" escape="text/plain" />' title="<di:translate key="competence.competencetreeitemcoretasksdescription" />" target="text"><di:translate key="competence.competencetreeitemcoretasks" /></a></nobr></td>
            </tr>
         </table>

         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/core/coreassignments&nodepath=coreassignments&searchfields=name&fields=name<mm:write referid="forbidtemplate" escape="text/plain" />' title="<di:translate key="competence.competencetreeitemcoreassignmentsdescription" />" target="text"><di:translate key="competence.competencetreeitemcoreassignments" /></a></nobr></td>
            </tr>
         </table>

         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/assessment/preassessments&nodepath=preassessments&searchfields=name&fields=name<mm:write referid="forbidtemplate" escape="text/plain" />' title="<di:translate key="competence.competencetreeitempregradesdescription" />" target="text"><di:translate key="competence.competencetreeitempregrades" /></a></nobr></td>
            </tr>
         </table>

         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/assessment/postassessments&nodepath=postassessments&searchfields=name&fields=name<mm:write referid="forbidtemplate" escape="text/plain" />' title="<di:translate key="competence.competencetreeitempostgradesdescription" />" target="text"><di:translate key="competence.competencetreeitempostgrades" /></a></nobr></td>
            </tr>
         </table>

         <% //----------------------- PROFILES ----------------------- %>
         <mm:import id="profiles_exist" reset="true">false</mm:import>
         <mm:listnodes type="profiles">
            <mm:import id="profiles_exist" reset="true">true</mm:import>
         </mm:listnodes>

         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><a href='javascript:clickNode("profiles_root")'><img src="gfx/tree_plus.gif" border="0" align="middle" id='img_profiles_root'/></a></td>
               <td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_profiles_root"/>'/></td>
               <td><nobr><a href='<mm:write referid="listjsp"/>&wizard=config/profile/profiles&nodepath=profiles&searchfields=name&fields=name<mm:write referid="forbidtemplate" escape="text/plain" />' title="<di:translate key="competence.competencetreeitemprofilesdescription" />" target="text"><di:translate key="competence.competencetreeitemprofiles" /></a></nobr></td>
            </tr>
         </table>
         <div id="profiles_root" style="display:none">
            <table border="0" cellpadding="0" cellspacing="0">
               <tr>
                  <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                  <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
                  <mm:compare referid="profiles_exist" value="true">
                     <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
                  </mm:compare>
                  <mm:compare referid="profiles_exist" value="true" inverse="true">
                     <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
                  </mm:compare>
                  <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
                  <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/profile/profiles&objectnumber=new' title="<di:translate key="competence.competencetreeitemcreatenewprofiledescription" />" target="text"><di:translate key="competence.competencetreeitemcreatenewprofile" /></a></nobr></td>
               </tr>

               <mm:listnodes type="profiles">
                  <tr>
                     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                     <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
                     <mm:last inverse="true">
                        <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
                     </mm:last>
                     <mm:last>
                        <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
                     </mm:last>
                     <td><img src="gfx/edit_learnobject.gif" width="16" border="0" align="middle" /></td>
                     <td><nobr>&nbsp;<a href="<%= request.getContextPath() %>/competence/competence_matrix.jsp?profile=<mm:field name="number"/>" target="text"><mm:field name="name"/></a></nobr></td>
                  </tr>
               </mm:listnodes>
            </table>
         </div>

         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>
               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/pop/pop&nodepath=pop&searchfields=name&fields=name<mm:write referid="forbidtemplate" escape="text/plain" />' title="<di:translate key="competence.competencetreeitempepdescription" />" target="text"><di:translate key="competence.competencetreeitempep" /></a></nobr></td>
            </tr>
         </table>

      </div>
   </mm:islessthan>
</mm:cloud>
