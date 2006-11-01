<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>  
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<jsp:scriptlet>
  String imageName = "";
  String sAltText = "";
</jsp:scriptlet>
<mm:cloud rank="basic user">

  <jsp:directive.include file="/shared/setImports.jsp" />
  <jsp:directive.include file="../mode.include.jsp" />
  <jsp:directive.include file="../roles_defs.jsp" />
  <mm:import id="editcontextname" >rollen</mm:import><!-- why is this in dutch -->
  <jsp:directive.include file="../roles_chk.jsp" />

  
  <mm:islessthan referid="rights" referid2="RIGHTS_RW">
    ${rights} &lt; ${RIGHTS_RW}
  </mm:islessthan>
   <mm:islessthan inverse="true"
                  referid="rights" referid2="RIGHTS_RW">
      <a href='javascript:clickNode("persons_0")'><img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle'  id='img_persons_0' /></a>&nbsp;<img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/><nobr>&nbsp;<a href='javascript:clickNode("persons_0")'><di:translate key="education.personstab" /></nobr></a>
      <br>
      <div id='persons_0' style='display: none'>
         <%// edit people,rolerel, education %>
         <%-- doesn't work properly, so commented it out for the moment
         <mm:log>KOMTIEHIER TOEVALLIG</mm:log>
            rolestree.addItem("<di:translate key="education.editpeoplerolereleducation" />",
                              "<mm:treefile write="true" page="/education/wizards/roles.jsp" objectlist="$includePath" />",
                              null,
                              "<di:translate key="education.editpeoplerolereleducationdescription" />",
                              "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         --%>
         <%// create new role %>

         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>

               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/people/people&nodepath=people&fields=firstname,suffix,lastname,username,externid&orderby=lastname&searchfields=firstname,suffix,lastname,username,externid&orderby=lastname&search=yes<mm:write referid="forbidtemplate" escape="text/plain" />' title='<di:translate key="education.persons" />' target="text"><di:translate key="education.persons" /></a></nobr></td>
            </tr>
         </table>
         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>

               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/class/classes&nodepath=classes&orderby=name&fields=name&searchfields=name&search=yes<mm:write referid="forbidtemplate" escape="text/plain" />' title="<di:translate key="education.classes" />" target="text"><di:translate key="education.classes" /></a></nobr></td>
            </tr>
         </table>
         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>

               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/workgroup/workgroups&nodepath=workgroups&orderby=name&fields=name&searchfields=name&search=yes<mm:write referid="forbidtemplate" escape="text/plain" />' title="<di:translate key="education.workgroups" />" target="text"><di:translate key="education.workgroups" /></a></nobr></td>
            </tr>
         </table>
         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>

               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href='roles.jsp' title='<di:translate key="education.roles" />' target="text"><di:translate key="education.roles" /></a></nobr></td>
            </tr>
         </table>
         <mm:node number="component.report" notfound="skip">
            <%-- <di:hasrole role="teacher"> --%>
               <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                     <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>

                     <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
                     <td><nobr>&nbsp;<a href='../../report/index.jsp' target="text">Rapport</a></nobr></td>
                  </tr>
               </table>
            <%-- </di:hasrole> --%>
         </mm:node>

         <mm:node number="component.isbo" notfound="skip">
            <%-- <di:hasrole role="systemadministrator"> --%>
               <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                     <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>

                     <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
                     <td><nobr>&nbsp;<a href='../../isbo/index.jsp' title='<di:translate key="education.isboimport" />' target="text"><di:translate key="education.isboimport" /></a></nobr></td>
                  </tr>
               </table>
            <%-- </di:hasrole> --%>
         </mm:node>


         <mm:node number="component.assessment" notfound="skip">
            <%-- <di:hasrole role="systemadministrator"> --%>
               <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                     <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>

                     <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
                     <td><nobr>&nbsp;<a href='../../assessment/email_notification/index.jsp' title='<di:translate key="education.wizard_people_assessment" />' target="text"><di:translate key="education.wizard_people_assessment" /></a></nobr></td>
                  </tr>
               </table>
            <%-- </di:hasrole> --%>
         </mm:node>
         
      </div>
   </mm:islessthan>
</mm:cloud>
