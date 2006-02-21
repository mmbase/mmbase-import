<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.util.HashSet"%>

<%@page import = "nl.didactor.component.education.utils.EducationPeopleConnector" %>

<%@page import="org.mmbase.bridge.*,org.mmbase.bridge.util.*,javax.servlet.jsp.JspException"%>

<%
  String imageName = "";
  String sAltText = "";
%>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
   <%@include file="/shared/setImports.jsp"%>
   <%@include file="/education/wizards/roles_defs.jsp" %>

   <%//education-people connector
      EducationPeopleConnector educationPeopleConnector = new EducationPeopleConnector(cloud);
   %>
   <mm:import id="wizardlang">en</mm:import>
   <mm:compare referid="language" value="nl">
     <mm:import id="wizardlang" reset="true">nl</mm:import>
   </mm:compare>

   <mm:import externid="showcode">false</mm:import>
   <mm:import id="wizardjsp"><mm:treefile write="true" page="/editwizards/jsp/wizard.jsp" objectlist="$includePath" />?referrer=/education/wizards/ok.jsp&language=<mm:write referid="wizardlang" /></mm:import>
   <mm:import id="listjsp"><mm:treefile write="true" page="/editwizards/jsp/list.jsp" objectlist="$includePath" />?language=<mm:write referid="wizardlang" /></mm:import>
   <mm:import externid="mode">components</mm:import>
      <mm:node number="component.pdf" notfound="skip">
         <mm:relatednodes type="providers" constraints="providers.number=$provider">
            <mm:import id="pdfurl"><mm:treefile write="true" page="/pdf/pdfchooser.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
         </mm:relatednodes>
      </mm:node>

      <script type="text/javascript">
         function saveCookie(name,value,days) {
            if (days) {
               var date = new Date();
               date.setTime(date.getTime()+(days*24*60*60*1000))
               var expires = '; expires='+date.toGMTString()
            } else expires = ''
            document.cookie = name+'='+value+expires+'; path=/'
         }
         function readCookie(name) {
            var nameEQ = name + '='
            var ca = document.cookie.split(';')
            for(var i=0;i<ca.length;i++) {
               var c = ca[i];
               while (c.charAt(0)==' ') c = c.substring(1,c.length)
               if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length)
            }
            return null
         }
         function deleteCookie(name) {
            saveCookie(name,'',-1)
         }
         function restoreTree() {
            for(var i=1; i<10; i++) {
               var lastclicknode = readCookie('lastnodepagina'+i);
               if(lastclicknode!=null) { clickNode(lastclicknode); }
            }
         }
         function clickNode(node) {
            var level = node.split('_').length;
            saveCookie('lastnodepagina'+level,node,1);
            el=document.getElementById(node);
            img = document.getElementById('img_' + node);
            img2 = document.getElementById('img2_' + node);
            if (el!=null && img != null)
            {
               if (el.style.display=='none')
               {
                  el.style.display='inline';
                  if (img2 != null) img2.src = 'gfx/folder_open.gif';
                  if (img.src.indexOf('last.gif')!=-1 )
                  {
                     img.src='gfx/tree_minlast.gif';
                  }
                  else
                  {
                     img.src='gfx/tree_min.gif';
                  }
               }
               else
               {
                  el.style.display='none';
                  if (img2 != null) img2.src = 'gfx/folder_closed.gif';
                  if (img.src.indexOf('last.gif')!=-1)
                  {
                     img.src='gfx/tree_pluslast.gif';
                  }
                  else
                  {
                     img.src='gfx/tree_plus.gif';
                  }
               }
            }
         }
      </script>
<% int treeCount = 0; %>
<% int metatreeCount = 0; %>
<% int comptreeCount = 0; %>



<mm:compare referid="mode" value="components">
   <% //----------------------- Components come from here ----------------------- %>
   <mm:import id="editcontextname" reset="true">componenten</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <mm:listnodes type="components" orderby="name">
       &nbsp;&nbsp;&nbsp; <a target="text" href="<mm:treefile page="/components/edit.jsp" objectlist="$includePath" />?component=<mm:field name="number" />"><mm:field name="name" /></a> <br />
     </mm:listnodes>
   </mm:islessthan>
</mm:compare>




<mm:compare referid="mode" value="roles">
   <% //----------------------- Roles come from here ----------------------- %>
   <mm:import id="editcontextname" reset="true">rollen</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
      <a href='javascript:clickNode("persons_0")'><img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle'  id='img_persons_0' /></a>&nbsp;<img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/><nobr>&nbsp;<a href='javascript:clickNode("persons_0")'><di:translate key="education.personstab" /></nobr></a>
      <br>
      <div id='persons_0' style='display: none'>
         <%// edit people,rolerel, education %>
         <%-- doesn't work properly, so commented it out for the moment
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
            <di:hasrole role="teacher">
               <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                     <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>

                     <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
                     <td><nobr>&nbsp;<a href='../../report/index.jsp' target="text">Rapport</a></nobr></td>
                  </tr>
               </table>
            </di:hasrole>
         </mm:node>

         <mm:node number="component.isbo" notfound="skip">
            <di:hasrole role="systemadministrator">
               <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                     <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>

                     <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
                     <td><nobr>&nbsp;<a href='../../isbo/index.jsp' title='<di:translate key="education.isboimport" />' target="text"><di:translate key="education.isboimport" /></a></nobr></td>
                  </tr>
               </table>
            </di:hasrole>
         </mm:node>
      </div>
   </mm:islessthan>
</mm:compare>

<mm:compare referid="mode" value="content_metadata">
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
                     <td><nobr>&nbsp;<a href='<mm:write referid="template" escape="text/plain" />' title='<di:translate key="education.edit" /> <%= arrstrContentMetadataConfig[f][0] %>' target="text"><%= arrstrContentMetadataConfig[f][0] %></a></nobr></td>
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
                     <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/provider/providers&nodepath=providers&searchfields=name&fields=name&orderby=name' target="text">Content paginas (CMS)</a></nobr></td>
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
</mm:compare>

<mm:compare referid="mode" value="filemanagement">
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
</mm:compare>

<mm:compare referid="mode" value="competence">
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
</mm:compare>

<mm:compare referid="mode" value="metadata">
   <% //----------------------- Metadata comes from here ----------------------- %>
   <mm:import id="editcontextname" reset="true">metadata</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <mm:treeinclude page="/metadata/tree/tree.jsp" objectlist="$includePath" referids="$referids">
       <mm:param name="locale"><%= pageContext.getAttribute("t_locale") %></mm:param>
       <mm:param name="wizardjsp"><mm:write referid="wizardjsp"/></mm:param>
       <mm:param name="listjsp"><mm:write referid="listjsp"/></mm:param>
     </mm:treeinclude>
   </mm:islessthan>
</mm:compare>

<mm:compare referid="mode" value="virtualclassroom">
  <% //----------------------- virtualclassroom come from here ----------------------- %>
  <mm:node number="component.virtualclassroom" notfound="skipbody">
    <mm:import id="editcontextname" reset="true">virtualclassroom</mm:import>
    <%@include file="/education/wizards/roles_chk.jsp" %>
    <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
      <a href='javascript:clickNode("virtualclassroom_0")'><img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle' id='img_virtualclassroom_0'/></a>&nbsp;<img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/><span style='width:100px; white-space: nowrap'><a href='<mm:write referid="listjsp"/>&wizard=config/virtualclassroom/virtualclassroomsessions&nodepath=virtualclassroomsessions&fields=name&searchfields=name&orderby=name<mm:write referid="forbidtemplate" escape="text/plain" />' target="text"><di:translate key="virtualclassroom.virtualclassroomsession" /></a></span>
      <br>
      <div id='virtualclassroom_0' style='display: none'>
        <mm:treeinclude page="/virtualclassroom/backoffice/index.jsp" objectlist="$includePath" referids="$referids">
          <mm:param name="wizardjsp"><mm:write referid="wizardjsp"/></mm:param>
        </mm:treeinclude>
      </div>
    </mm:islessthan>
  </mm:node> 
</mm:compare>

<mm:compare referid="mode" value="tests">
   <% //----------------------- Tests come from here ----------------------- %>
   <mm:import id="editcontextname" reset="true">toetsen</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
      <a href='javascript:clickNode("tests_0")'><img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle' id='img_tests_0'/></a>&nbsp;<img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/> <span style='width:100px; white-space: nowrap'><a href='<mm:write referid="listjsp"/>&wizard=config/tests/tests&nodepath=tests&fields=name&searchfields=name&orderby=name<mm:write referid="forbidtemplate" escape="text/plain" />' target="text"><di:translate key="education.tests" /></a></span>
      <br>
      <div id='tests_0' style='display: none'>

         <mm:import id="number_of_tests" reset="true">0</mm:import>
         <mm:listnodes type="tests">
            <mm:import id="number_of_tests" reset="true"><mm:size /></mm:import>
         </mm:listnodes>

         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                  <%// We have to detect the last element %>
                  <mm:isgreaterthan referid="number_of_tests" value="0">
                     <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
                  </mm:isgreaterthan>

                  <mm:islessthan    referid="number_of_tests" value="1">
                     <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>
                  </mm:islessthan>

               <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/tests/tests&objectnumber=new' title='<di:translate key="education.createnewtestdescription" />' target="text"><di:translate key="education.createnewtest" /></a></nobr></td>
            </tr>
         </table>

         <mm:listnodes type="tests" orderby="tests.name">

            <table border="0" cellpadding="0" cellspacing="0">
               <tr>
                  <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                     <%// We have to detect the last element %>
                     <mm:last inverse="true">
                        <td><a href='javascript:clickNode("<mm:field name="number"/>")'><img src="gfx/tree_plus.gif" border="0" align="middle" id='img_<mm:field name="number"/>'/></a></td>
                     </mm:last>

                     <mm:last>
                        <td><a href='javascript:clickNode("<mm:field name="number"/>")'><img src="gfx/tree_pluslast.gif" border="0" align="middle" id='img_<mm:field name="number"/>'/></a></td>
                     </mm:last>

                  <td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_<mm:field name="number"/>'/></td>
                  <td><nobr><a href='<mm:write referid="wizardjsp"/>&wizard=config/tests/tests&objectnumber=<mm:field name="number" />' title='<di:translate key="education.treattest" />' target="text"><mm:field name="name" /></a></nobr></td>
               </tr>
            </table>

            <mm:import id="the_last_parent" reset="true">false</mm:import>
            <mm:last>
               <mm:import id="the_last_parent" reset="true">true</mm:import>
            </mm:last>

            <div id='<mm:field name="number"/>' style="display:none">

               <mm:field name="number" jspvar="sID" vartype="String">
                  <mm:write referid="wizardjsp" jspvar="sWizardjsp" vartype="String" write="false">
                     <mm:write referid="the_last_parent" jspvar="sTheLastParent" vartype="String">
                        <jsp:include page="newfromtree_tests.jsp">
                           <jsp:param name="node" value="<%= sID %>" />
                           <jsp:param name="wizardjsp" value="<%= sWizardjsp %>" />
                           <jsp:param name="the_last_parent" value="<%= sTheLastParent %>" />
                        </jsp:include>
                     </mm:write>
                  </mm:write>
               </mm:field>



               <mm:remove referid="questionamount" />
               <mm:import id="mark_error" reset="true"></mm:import>
               <mm:field name="questionamount" id="questionamount">
                  <mm:isgreaterthan value="0">
                     <mm:countrelations type="questions">
                        <mm:islessthan value="$questionamount">
                           <mm:import id="mark_error" reset="true">Er zijn minder vragen ingevoerd dan er gesteld moeten worden.</mm:import>
                        </mm:islessthan>
                     </mm:countrelations>
                  </mm:isgreaterthan>
                  <mm:remove referid="requiredscore" />
                  <mm:field name="requiredscore" id="requiredscore">
                     <mm:countrelations type="questions">
                        <mm:islessthan value="$requiredscore">
                           <mm:import id="mark_error" reset="true">Er zijn minder vragen ingevoerd dan er goed beantwoord moeten worden.</mm:import>
                        </mm:islessthan>
                     </mm:countrelations>
                     <mm:isgreaterthan referid="questionamount" value="0">
                        <mm:islessthan referid="questionamount" value="$requiredscore">
                           <mm:import id="mark_error" reset="true">Er worden minder vragen gesteld dan er goed beantwoord moeten worden.</mm:import>
                        </mm:islessthan>
                     </mm:isgreaterthan>
                  </mm:field>
               </mm:field>

               <mm:relatednodes type="questions" orderby="title">
                  <table border="0" cellpadding="0" cellspacing="0">
                     <tr>
                        <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                        <mm:compare referid="the_last_parent" value="true" inverse="true">
                           <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
                        </mm:compare>
                        <mm:compare referid="the_last_parent" value="true">
                           <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                        </mm:compare>
                        <mm:last inverse="true">
                           <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
                        </mm:last>
                        <mm:last>
                           <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
                        </mm:last>
                        <td><img src="gfx/edit_learnobject.gif" width="16" border="0" align="middle" /></td>


                        <mm:remove referid="type_of_node"/>
                        <mm:nodeinfo id="type_of_node" type="type" jspvar="dummyName" vartype="String">

                            <mm:compare referid="type_of_node" value="mcquestions">
                               <mm:import id="mark_error" reset="true">Een multiple-choice vraag moet minstens 1 goed antwoord hebben</mm:import>
                               <mm:relatednodes type="mcanswers" constraints="mcanswers.correct > '0'" max="1">
                                  <mm:import id="mark_error" reset="true"></mm:import>
                               </mm:relatednodes>

                               <td>&nbsp;<nobr><a href='<mm:write referid="wizardjsp"/>&wizard=config/question/mcquestions&objectnumber=<mm:field name="number"/>' title='<di:translate key="education.edit" /> <%= dummyName.toLowerCase() %>' target="text"><mm:field name="title" /><mm:isnotempty referid="mark_error"></a> <a style='color: red; font-weight: bold' href='javascript:alert(&quot;<mm:write referid="mark_error"/>&quot;);'>!</mm:isnotempty></a></nobr></td>
                            </mm:compare>
                            <mm:compare referid="type_of_node" valueset="couplingquestions,dropquestions,hotspotquestions,openquestions,rankingquestions,valuequestions,fillquestions,fillselectquestions,essayquestions">
                               <td>&nbsp;<nobr><a href='<mm:write referid="wizardjsp"/>&wizard=config/question/<mm:write referid="type_of_node"/>&objectnumber=<mm:field name="number"/>' title='<di:translate key="education.edit" /> <%= dummyName.toLowerCase() %>' target="text"><mm:field name="title" /></a></nobr></td>
                            </mm:compare>
                        </mm:nodeinfo>
                     </tr>
                  </table>
               </mm:relatednodes>
            </div>
         </mm:listnodes>
      </div>
   </mm:islessthan>
</mm:compare>



<mm:compare referid="mode" value="educations">
   <% //----------------------- Educations come from here ----------------------- %>
   <mm:import id="editcontextname" reset="true">opleidingen</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
      <a href='javascript:clickNode("node_0")'><img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle' id='img_node_0'/></a>&nbsp;<img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/> <span style='width:100px; white-space: nowrap'><a href="<mm:write referid="listjsp"/>&wizard=config/education/educations&nodepath=educations&searchfields=name&fields=name&orderby=name<mm:write referid="forbidtemplate" escape="text/plain" />" target="text"><di:translate key="education.educationmenueducations" /></a></span>
      <br>
      <div id='node_0' style='display: none'>

         <% //We go throw all educations for CURRENT USER
            HashSet hsetEducationsForUser = null;
         %>
         <mm:node number="$user" jspvar="node">
            <%
               hsetEducationsForUser = educationPeopleConnector.relatedEducations("" + node.getNumber());
            %>
         </mm:node>
         <mm:import id="number_of_educations" reset="true"><%= hsetEducationsForUser.size() %></mm:import>


         <% //new education item %>
         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <%// We have to detect the last element %>
               <mm:isgreaterthan referid="number_of_educations" value="0">
                  <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
               </mm:isgreaterthan>

               <mm:islessthan    referid="number_of_educations" value="1">
                  <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>
               </mm:islessthan>

               <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
               <td><nobr>&nbsp;<a href="<mm:write referid="wizardjsp"/>&wizard=config/education/educations-origin&objectnumber=new&origin=<mm:write referid="user"/>" title="<di:translate key="education.createneweducationdescription" />" target="text"><di:translate key="education.createneweducation" /></a></nobr></td>
            </tr>
         </table>

         <%
            String sEducationID = null;
            if(request.getParameter("education_topmenu_course") != null)
            {
               sEducationID = (String) request.getParameter("education_topmenu_course");
            }
            else
            {
               if (hsetEducationsForUser.iterator().hasNext())
               {
                  sEducationID = (String) hsetEducationsForUser.iterator().next();
               }
            }
         %>

         <mm:isgreaterthan referid="number_of_educations" value="0">
            <mm:node number="<%= sEducationID %>">
               <%@include file="whichimage.jsp"%>
               <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                     <td>
                        <a href='javascript:clickNode("education_0")'><img src="gfx/tree_pluslast.gif" border="0" align="center" valign="middle" id="img_education_0"/></a>
                     </td>
                     <td><img src="gfx/folder_closed.gif" border="0" align="middle" id="img2_education_0"/></td>
                     <td>
                       <nobr>
                         <a href="<mm:write referid="wizardjsp"/>&wizard=config/education/educations&objectnumber=<mm:field name="number" />" title="<di:translate key="education.editeducation" />" target="text"><mm:field name="name" /></a>
                         <mm:present referid="pdfurl">
                           <a href="<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>" target="text"><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                         </mm:present>
                         <mm:node number="component.metadata" notfound="skip">
                           <a href="metaedit.jsp?number=<%=sEducationID%>" target="text"><img id="img_<mm:field name="number"/>" src="<%= imageName %>" border="0" title="<%= sAltText %>" alt="<%= sAltText %>"></a>
                         </mm:node>
                         <mm:node number="component.drm" notfound="skip">
                           <a target="text" href="<mm:write referid="wizardjsp"/>&wizard=educationslicense&objectnumber=<%= sEducationID %>" title="Bewerk licentie" style="font-size: 1em; text-decoration: none">&copy;</a>
                         </mm:node>
                         <mm:node number="component.versioning" notfound="skip">
                           <a href="versioning.jsp?nodeid=<%=sEducationID%>" target="text"><img src="gfx/versions.gif" border="0"></a>
                         </mm:node>
                       </nobr>
                     </td>
                  </tr>
               </table>

               <% // We have to count all learnblocks %>
               <mm:remove referid="number_of_learnblocks"/>
               <mm:import id="number_of_learnblocks">0</mm:import>
               <mm:related path="posrel,learnobjects" orderby="posrel.pos" directions="up" searchdir="destination">
                  <mm:remove referid="number_of_learnblocks"/>
                  <mm:import id="number_of_learnblocks"><mm:size /></mm:import>
               </mm:related>


               <div id="education_0" style='display: none'>
                  <% // Registration %>
                  <mm:node number="component.register" notfound="skipbody">
                  <table border="0" cellpadding="0" cellspacing="0">
                     <tr>
                        <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
                        <td><img src='gfx/tree_vertline-leaf.gif' border='0' align='center' valign='middle' id='img_node_0_1_2'/></td>
                        <td><img src='gfx/new_education.gif' width="16" border='0' align='middle' /></td>
                        <td>&nbsp;<nobr><a href="<mm:treefile write="true" page="/register/wizards/register.jsp" referids="$referids" objectlist="$includePath"><mm:param name="educationid"><%=sEducationID%></mm:param></mm:treefile>" title="<di:translate key="register.registrations" />" target="text"><di:translate key="register.registrations" /></a></nobr></td>
                     </tr>
                  </table>
                  </mm:node>

                  <%// create new learnblock item %>
                  <table border="0" cellpadding="0" cellspacing="0">
                     <tr>
                        <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
                        <mm:isgreaterthan referid="number_of_learnblocks" value="0">
                           <td><img src='gfx/tree_vertline-leaf.gif' border='0' align='center' valign='middle' id='img_node_0_1_2'/></td>
                        </mm:isgreaterthan>
                        <mm:islessthan    referid="number_of_learnblocks" value="1">
                           <td><img src='gfx/tree_leaflast.gif' border='0' align='center' valign='middle' id='img_node_0_1_2'/></td>
                        </mm:islessthan>
                        <td><img src='gfx/new_education.gif' width="16" border='0' align='middle' /></td>
                        <td>&nbsp;<nobr><a href='<mm:write referid="wizardjsp"/>&wizard=config/learnblocks/learnblocks-origin&objectnumber=new&origin=<mm:field name="number"/>' title="<di:translate key="education.createnewlearnblockdescription" />" target="text"><di:translate key="education.createnewlearnblock" /></a></nobr></td>
                     </tr>
                  </table>


                  <% //All learnblocks for current education %>
                  <%
                     int iLearnblockCounter = 0;
                  %>
                  <mm:related path="posrel,learnobjects" orderby="posrel.pos" directions="up" searchdir="destination">
                     <mm:node element="learnobjects">
                        <%@include file="whichimage.jsp"%>
                        <mm:nodeinfo type="type" id="this_node_type">
                           <mm:import id="mark_error" reset="true"></mm:import>
                           <mm:compare referid="this_node_type" value="tests">
                              <mm:field name="questionamount" id="questionamount">
                                 <mm:isgreaterthan value="0">
                                    <mm:countrelations type="questions">
                                       <mm:islessthan value="$questionamount">
                                          <mm:import id="mark_error" reset="true">Er zijn minder vragen ingevoerd dan er gesteld moeten worden.</mm:import>
                                       </mm:islessthan>
                                    </mm:countrelations>
                                 </mm:isgreaterthan>

                                 <mm:field name="requiredscore" id="requiredscore">
                                    <mm:countrelations type="questions">
                                       <mm:islessthan value="$requiredscore">
                                          <mm:import id="mark_error" reset="true">Er zijn minder vragen ingevoerd dan er goed beantwoord moeten worden.</mm:import>
                                       </mm:islessthan>
                                    </mm:countrelations>
                                    <mm:isgreaterthan referid="questionamount" value="0">
                                       <mm:islessthan referid="questionamount" value="$requiredscore">
                                          <mm:import id="mark_error" reset="true">Er worden minder vragen gesteld dan er goed beantwoord moeten worden.</mm:import>
                                       </mm:islessthan>
                                    </mm:isgreaterthan>
                                 </mm:field>
                              </mm:field>
                           </mm:compare>

                           <table border="0" cellpadding="0" cellspacing="0">
                              <tr>
                                 <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
                                 <td><mm:last inverse="true"><a href='javascript:clickNode("node_0_0_<%= iLearnblockCounter %>")'><img src="gfx/tree_plus.gif" border="0" align="center" valign="middle" id="img_node_0_0_<%= iLearnblockCounter %>"/></a></mm:last><mm:last><a href='javascript:clickNode("node_0_0_<%= iLearnblockCounter %>")'><img src="gfx/tree_pluslast.gif" border="0" align="center" valign="middle" id="img_node_0_0_<%= iLearnblockCounter %>"/></a></mm:last></td>
                                 <td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_node_0_0_<%= iLearnblockCounter %>'/></td>
                                 <td>
                                   <nobr>
                                     <mm:import id="dummyname" jspvar="dummyName" vartype="String" reset="true"><mm:nodeinfo type="guitype"/></mm:import>
                                     <a href="<mm:write referid="wizardjsp"/>&wizard=config/<mm:nodeinfo type="type" />/<mm:nodeinfo type="type" />&objectnumber=<mm:field name="number" />" title="<di:translate key="education.editexisting" /> <%= dummyName.toLowerCase() %>" target="text"><mm:field name="name" /></a>
                                     <mm:present referid="pdfurl">
                                       <mm:compare referid="this_node_type" value="pages">
                                         <a href="<mm:write referid="pdfurl" />&number=<mm:field name="number"/>" target="text"><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                                       </mm:compare>
                                       <mm:compare referid="this_node_type" value="learnblocks">
                                         <a href="<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>" target="text"><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                                       </mm:compare>
                                     </mm:present>
                                     <mm:field name="number" id="node_number" write="false" />
                                     <mm:node number="component.metadata" notfound="skip">
                                       <a href="metaedit.jsp?number=<mm:write referid="node_number" />" target="text"><img id="img_<mm:field name="number"/>" src="<%= imageName %>" border="0" title="<%= sAltText %>" alt="<%= sAltText %>"></a>
                                     </mm:node>
                                     <mm:node number="component.versioning" notfound="skip">
                                       <a href="versioning.jsp?nodeid=<mm:write referid="node_number" />" target="text"><img src="gfx/versions.gif" border="0"></a>
                                     </mm:node>
                                     <mm:remove referid="node_number" />
                                   </nobr>
                                 </td>
                              </tr>
                           </table>
                        </mm:nodeinfo>

                        <div id="node_0_0_<%= iLearnblockCounter %>" style="display:none">
                           <mm:treeinclude write="true" page="/education/wizards/learnobject.jsp" objectlist="$includePath" referids="wizardjsp">

                              <mm:param name="startnode"><mm:field name="number" /></mm:param>
                              <mm:param name="depth">10</mm:param>
                              <mm:last>
                                 <mm:param name="the_last_parent">true</mm:param>
                              </mm:last>
                              <mm:last inverse="true">
                                 <mm:param name="the_last_parent">false</mm:param>
                              </mm:last>
                           </mm:treeinclude>
                        </div>
                     </mm:node>
                     <%
                        iLearnblockCounter++;
                     %>
                  </mm:related>
               </div>
            </mm:node>
         </mm:isgreaterthan>
      </div>
   </mm:islessthan>
</mm:compare>
</mm:cloud>
</mm:content>
