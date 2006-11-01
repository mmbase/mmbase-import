<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>  
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<jsp:directive.page import="java.util.*,nl.didactor.component.education.utils.*" />
<jsp:scriptlet>
  String imageName = "";
  String sAltText = "";
</jsp:scriptlet>
<mm:cloud rank="basic user" jspvar="cloud">
  <jsp:directive.include file="/shared/setImports.jsp" />
  <jsp:directive.include file="../mode.include.jsp" />
  <jsp:scriptlet> EducationPeopleConnector educationPeopleConnector = new EducationPeopleConnector(cloud);</jsp:scriptlet>
  <jsp:directive.include file="/education/wizards/roles_defs.jsp" />
   <!--
       Educations come from here 
   -->
   <mm:import id="editcontextname" reset="true">opleidingen</mm:import>
   <jsp:directive.include file="/education/wizards/roles_chk.jsp" />
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
      <a href='javascript:clickNode("node_0")'>
        <img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle' id='img_node_0'/></a>
        &nbsp;<img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/>
        <span style='width:100px; white-space: nowrap'>
          <mm:link referid="listjsp${forbidtemplate}">
            <mm:param name="wizard">config/education/educations</mm:param>
            <mm:param name="nodepath">educations</mm:param>
            <mm:param name="searchfields">name</mm:param>
            <mm:param name="fields">name</mm:param>
            <mm:param name="orderby">name</mm:param>
            <a href="${_}" target="text">
              <di:translate key="education.educationmenueducations" />
            </a>
          </mm:link>
        </span>
      <br />
      <div id='node_0' style='display: none'>

         <% //We go throw all educations for CURRENT USER
            HashSet hsetEducationsForUser = null;
         %>
         <mm:node number="$user" jspvar="node">
           <jsp:scriptlet>
               hsetEducationsForUser = educationPeopleConnector.relatedEducations("" + node.getNumber());
           </jsp:scriptlet>
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
               <td><nobr>&nbsp;<a href="<mm:write referid="wizardjsp"/>&wizard=config/education/educations-origin&objectnumber=new&origin=<mm:write referid="user"/>&path=" title="<di:translate key="education.createneweducationdescription" />" target="text"><di:translate key="education.createneweducation" /></a></nobr></td>
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
            <%// -------------------------------------------- The Education starts from here -------------------------------------------- %>
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
                         <a href="<mm:write referid="wizardjsp"/>&wizard=config/education/educations&objectnumber=<mm:field name="number" />&path=" title="<di:translate key="education.editeducation" />" target="text"><mm:field name="name" /></a>
                         <mm:import id="eduname" jspvar="eduname"><mm:field name="name" /></mm:import>
                         <% session.setAttribute("eduname",eduname); %>
                         <mm:present referid="pdfurl">
                           <a href="<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>" target="text"><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                         </mm:present>
                         <mm:node number="component.metadata" notfound="skip">
                           <a href="metaedit.jsp?number=<%=sEducationID%>" target="text"><img id="img_<%= sEducationID %>" src="<%= imageName %>" border="0" title="<%= sAltText %>" alt="<%= sAltText %>"></a>
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
               <mm:relatednodescontainer role="posrel" type="learnobjects" searchdirs="destination">
                  <mm:size id="number_of_learnblocks" write="false"/>
               </mm:relatednodescontainer>


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
                        <td>&nbsp;<nobr><a href='<mm:write referid="wizardjsp"/>&wizard=config/learnblocks/learnblocks-origin&objectnumber=new&origin=<mm:field name="number"/>&path=<%=eduname %>' title="<di:translate key="education.createnewlearnblockdescription" />" target="text"><di:translate key="education.createnewlearnblock" /></a></nobr></td>
                     </tr>
                  </table>


                  <% //All learnblocks for current education %>
                  <%
                     int iLearnblockCounter = 0;
                  %>
                  <mm:relatednodes role="posrel" orderby="posrel.pos" directions="up"
                                   searchdir="destination" type="learnobjects">
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
                                <a href="metaedit.jsp?number=<mm:write referid="node_number" />" target="text"><img id="img_${_node}" src="<%= imageName %>" border="0" title="<%= sAltText %>" alt="<%= sAltText %>"></a>
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
                  <%
                  iLearnblockCounter++;
                  %>
                </mm:relatednodes>
              </div>
            </mm:node>
         </mm:isgreaterthan>
      </div>
   </mm:islessthan>
</mm:cloud>