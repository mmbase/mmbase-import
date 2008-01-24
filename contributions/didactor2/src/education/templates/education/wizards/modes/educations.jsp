<%@page contentType="application/xml;charset=UTF-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><jsp:directive.page import="java.util.*,nl.didactor.component.education.utils.*" />
<div class="educations">
<jsp:scriptlet>
  String imageName = "";
  String sAltText = "";
</jsp:scriptlet>
<mm:content type="application/xml">
<mm:cloud rank="basic user" jspvar="cloud">
  <jsp:directive.include file="/shared/setImports.jsp" />
  <jsp:directive.include file="../mode.include.jsp" />
  <jsp:directive.include file="/education/wizards/roles_defs.jsp" />
   <!--
       Educations come from here
   -->
   <mm:import id="editcontextname" reset="true">opleidingen</mm:import>
   <jsp:directive.include file="/education/wizards/roles_chk.jsp" />
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
      <a href='javascript:clickNode("node_0")'>
        <img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle' id='img_node_0'/></a>
        <img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/>
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
        <mm:node number="$user" jspvar="node">
          <mm:hasrank value="administrator">
            <mm:listnodes id="educations" type="educations" />
          </mm:hasrank>
          <mm:hasrank value="administrator" inverse="true">
            <mm:nodelistfunction id="educations" name="educations" />
          </mm:hasrank>
        </mm:node>
        <mm:import id="number_of_educations" reset="true">${fn:length(educations)}</mm:import>

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
            <mm:link referid="wizardjsp" referids="user@origin">
              <mm:param name="wizard">config/education/educations-origin</mm:param>
              <mm:param name="objectnumber">new</mm:param>
              <mm:param name="path"></mm:param>
              <td>
                <nobr>
                  <a href="${_}"
                     title="${di:translate('education.createneweducationdescription')}"
                     target="text"><di:translate key="education.createneweducation" /></a>
              </nobr></td>
            </mm:link>
          </tr>
        </table>

        <mm:import id="educationId" externid="e">${education}</mm:import>


        <mm:isgreaterthan referid="number_of_educations" value="0">
          <%// -------------------------------------------- The Education starts from here -------------------------------------------- %>
          <mm:node number="${educationId}">
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
                    <a href="<mm:write referid="wizardjsp"/>&amp;wizard=config/education/educations&amp;objectnumber=<mm:field name="number" />&amp;path=" title="<di:translate key="education.editeducation" />" target="text"><mm:field name="name" /></a>
                    <mm:import id="eduname" jspvar="eduname"><mm:field name="name" /></mm:import>
                         <% session.setAttribute("eduname",eduname); %>
                         <mm:present referid="pdfurl">
                           <a href="<mm:write referid="pdfurl"/>&amp;number=<mm:field name="number"/>" target="text"><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                         </mm:present>
                         <mm:node number="component.metadata" notfound="skip">
                           <a href="metaedit.jsp?number=${educationId}" target="text"><img id="img_${educationId}" src="<%= imageName %>" border="0" title="<%= sAltText %>" alt="<%= sAltText %>" /></a>
                         </mm:node>
                         <mm:node number="component.drm" notfound="skip">
                           <a target="text" href="<mm:write referid="wizardjsp"/>&amp;wizard=educationslicense&amp;objectnumber=${educationId}" title="Bewerk licentie" style="font-size: 1em; text-decoration: none">©</a>
                         </mm:node>
                         <mm:node number="component.versioning" notfound="skip">
                           <a href="versioning.jsp?nodeid=${educationId}" target="text"><img src="gfx/versions.gif" border="0" /></a>
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
                  <%-- Registration --%>
                  <mm:node number="component.register" notfound="skipbody">
                  <table border="0" cellpadding="0" cellspacing="0">
                     <tr>
                        <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
                        <td><img src='gfx/tree_vertline-leaf.gif' border='0' align='center' valign='middle' id='img_node_0_1_2'/></td>
                        <td><img src='gfx/new_education.gif' width="16" border='0' align='middle' /></td>
                        <td><nobr> <a href="<mm:treefile write="true" page="/register/wizards/register.jsp" referids="$referids" objectlist="$includePath"><mm:param name="educationid">${educationId}</mm:param></mm:treefile>" title="<di:translate key="register.registrations" />" target="text"><di:translate key="register.registrations" /></a></nobr></td>
                     </tr>
                  </table>
                  </mm:node>

                  <%-- I think it is hackery --%>
                  <mm:node number="component.portal" notfound="skipbody">
                  <table border="0" cellpadding="0" cellspacing="0">
                     <tr>
                        <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
                        <td><img src='gfx/tree_vertline-leaf.gif' border='0' align='center' valign='middle' id='img_node_0_1_2'/></td>
                        <td><img src='gfx/new_education.gif' width="16" border='0' align='middle' /></td>
                        <td><nobr> <a href="<mm:treefile write="true" page="/portal/wizards/index.jspx" referids="language,$referids" objectlist="$includePath"><mm:param name="educationid">${educationId}</mm:param></mm:treefile>" title="<di:translate key="portal.portal" />" target="text"><di:translate key="portal.portal" /></a></nobr></td>
                     </tr>
                  </table>
                  </mm:node>

                  <mm:relatednodes path="posrel,learnblocks" element="posrel" orderby="posrel.pos" directions="down" max="1">
                    <mm:field name="pos" id="maxpos" write="false" />
                  </mm:relatednodes>

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
                      <mm:link referid="wizardjsp" referids="_node@origin">
                        <mm:param name="wizard">config/learnblocks/learnblocks-origin</mm:param>
                        <mm:param name="objectnumber">new</mm:param>
                        <mm:param name="newpos">${maxpos + 1}</mm:param>
                        <td>
                          <nobr>
                            <a href="${_}" title="${di:translate('education.createnewlearnblockdescription')}" target="text">
                            <di:translate key="education.createnewlearnblock" /></a>
                          </nobr>
                        </td>
                      </mm:link>
                    </tr>
                  </table>


                  <% //All learnblocks for current education %>
                  <%
                     int iLearnblockCounter = 0;
                  %>

                  <mm:relatednodes role="posrel" orderby="posrel.pos"
                                   directions="up"
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
                              <a href="<mm:write referid="wizardjsp"/>&amp;wizard=config/<mm:nodeinfo type="type" />/<mm:nodeinfo type="type" />&amp;objectnumber=<mm:field name="number" />" title="<di:translate key="education.editexisting" /> <%= dummyName.toLowerCase() %>" target="text"><mm:field name="name" /></a>
                              <mm:present referid="pdfurl">
                                <mm:compare referid="this_node_type" value="pages">
                                  <a href="<mm:write referid="pdfurl" />&amp;number=<mm:field name="number"/>" target="text"><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                                </mm:compare>
                                <mm:compare referid="this_node_type" value="learnblocks">
                                  <a href="<mm:write referid="pdfurl"/>&amp;number=<mm:field name="number"/>" target="text"><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                                </mm:compare>
                              </mm:present>
                              <mm:field name="number" id="node_number" write="false" />
                              <mm:node number="component.metadata" notfound="skip">
                                <a href="metaedit.jsp?number=<mm:write referid="node_number" />" target="text"><img id="img_${_node}" src="<%= imageName %>" border="0" title="<%= sAltText %>" alt="<%= sAltText %>" /></a>
                              </mm:node>
                              <mm:node number="component.versioning" notfound="skip">
                                <a href="versioning.jsp?nodeid=<mm:write referid="node_number" />" target="text"><img src="gfx/versions.gif" border="0" /></a>
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
</mm:content>
</div>
