<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.Iterator" %>
<%@ page import = "java.util.HashSet" %>
<%@ page import = "java.util.TreeMap" %>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>

<mm:import id="wizardjsp"><mm:treefile write="true" page="/editwizards/jsp/wizard.jsp" objectlist="$includePath" /></mm:import>

<%
   String sProfileID = request.getParameter("profile");
%>
<html>
<head>
   <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />' />
</head>

<body>
<mm:node number="<%= sProfileID %>">
   <%
      String sConstraints = "profiles.number=" + sProfileID;
      HashSet hsetCoreTasks = new HashSet();
      HashSet hsetCoreAssignments = new HashSet();
      TreeMap mapCoreTasks = new TreeMap();
      TreeMap mapCoreAssignments = new TreeMap();
   %>

   <mm:list path="profiles,insrel,competencies" constraints="<%= sConstraints%>" >
      <%
         String sCompetenceID = "";
      %>
      <mm:node element="competencies">
         <mm:field name="number" jspvar="sID" vartype="String">
            <%
               sCompetenceID = sID;
            %>
         </mm:field>
      </mm:node>
      <mm:node element="insrel">
         <mm:relatednodes type="coretasks">
            <mm:field name="number" jspvar="sID" vartype="String">
               <%
                  hsetCoreTasks.add(sID);

                  if(mapCoreTasks.get(sID) == null)
                  {
                     ArrayList arliTemp = new ArrayList();
                     arliTemp.add(sCompetenceID);
                     mapCoreTasks.put(sID, arliTemp);
                  }
                  else
                  {
                     ((ArrayList) mapCoreTasks.get(sID)).add(sCompetenceID);
                  }
               %>
            </mm:field>
         </mm:relatednodes>
         <mm:relatednodes type="coreassignments">
            <mm:field name="number" jspvar="sID" vartype="String">
               <%
                  hsetCoreAssignments.add(sID);

                  if(mapCoreAssignments.get(sID) == null)
                  {
                     ArrayList arliTemp = new ArrayList();
                     arliTemp.add(sCompetenceID);
                     mapCoreAssignments.put(sID, arliTemp);
                  }
                  else
                  {
                     ((ArrayList) mapCoreAssignments.get(sID)).add(sCompetenceID);
                  }
               %>
            </mm:field>
         </mm:relatednodes>
      </mm:node>
   </mm:list>


   <table border="1" cellpadding="0" cellspacing="0" style="border:0px" class="titlefield2">
      <% // Let's paint the header of the table %>
      <tr>
         <td colspan="2" style="border-color:#000000; background:#BDBDBD"><di:translate key="competence.competencematrixcompetences" /></td>
         <td style="border:0px;">&nbsp;&nbsp;&nbsp;</td>
         <td colspan="<%= hsetCoreTasks.size() %>" style="border-color:#000000; border-right:0px">&nbsp;<di:translate key="competence.competencematrixcoretasks" />&nbsp;</td>
         <td style="border-color:#000000;border-top:0px; border-right:0px;">&nbsp;&nbsp;&nbsp;</td>
         <td colspan="<%= hsetCoreAssignments.size() %>" style="border-color:#000000;border-right:0px">&nbsp;<di:translate key="competence.competencematrixcoreassignments" />&nbsp;</td>
         <td style="border-color:#000000">&nbsp;<di:translate key="competence.competencematrixratings" />&nbsp;</td>
      </tr>
      <tr>
         <%
            String sProfileName = "";
         %>
         <mm:import id="profile_name_template"><di:translate key="competence.competencematrixprofile" /></mm:import>
         <mm:write referid="profile_name_template" write="false" jspvar="sTemplate" vartype="String">
            <mm:field name="name" jspvar="sProfile" vartype="String">
               <%
                  sProfileName = sTemplate.replaceAll("\\{\\$\\$\\$\\}", sProfile);
               %>
            </mm:field>
         </mm:write>
         <td colspan="2" valign="top" style="border-color:#000000; border-top:0px; background:#BDBDBD"><a href="<mm:write referid="wizardjsp"/>?wizard=config/profile/profiles&objectnumber=<mm:field name="number"/>" style="text-decoration:none;" class="titlefield2"><%= sProfileName %></a></td>
         <td style="border-top:0px; border-left:0px; border-right:0px">&nbsp;</td>
         <%// Core tasks header
            if(hsetCoreTasks.size() > 0)
            {
               for(Iterator it = hsetCoreTasks.iterator(); it.hasNext();)
               {
                  String sCoreTaskID = (String) it.next();
                  %>
                     <mm:node number="<%= sCoreTaskID %>">
                        <mm:field name="name" jspvar="sName" vartype="String">
                           <mm:node number="progresstextbackground">
                              <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(12)+gravity(NorthEast)+text(20,20,<%= sName.replaceAll("\\s+","_").replaceAll("\"","''") %>)+rotate(270)</mm:import>
                              <td style="border-color:#000000;border-right:0px;border-top:0px"><a href="<mm:write referid="wizardjsp"/>?wizard=config/core/coretasks&objectnumber=<%= sCoreTaskID %>"><img border="0" src="<mm:image template="$template" />"/></a></td>
                           </mm:node>
                        </mm:field>
                     </mm:node>
                  <%
               }
            }
            else
            {//Header: click here to create a new one
               %>
                     <td align="center" style="border-color:#000000; border-top:0px; border-right:0px"><di:translate key="competence.competencematrixaddnewcoretask" /></td>
               <%
            }
         %>
         <td style="border-color:#000000; border-top:0px; border-right:0px; border-bottom:0px">&nbsp;</td>
         <%//Core Assignments header
            if(hsetCoreAssignments.size() > 0)
            {
               for(Iterator it = hsetCoreAssignments.iterator(); it.hasNext();)
               {
                  String sCoreAssignmentID = (String) it.next();
                  %>
                     <mm:node number="<%= sCoreAssignmentID %>">
                        <mm:field name="name" jspvar="sName" vartype="String">
                           <mm:node number="progresstextbackground">
                              <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(12)+gravity(NorthEast)+text(20,20,<%= sName.replaceAll("\\s+","_").replaceAll("\"","''") %>)+rotate(270)</mm:import>
                              <td style="border-color:#000000; border-right:0px; border-top:0px"><a href="<mm:write referid="wizardjsp"/>?wizard=config/core/coreassignments&objectnumber=<%= sCoreAssignmentID %>"><img border="0" src="<mm:image template="$template" />"/></a></td>
                           </mm:node>
                        </mm:field>
                     </mm:node>
                  <%
               }
            }
            else
            {//Header: click here to create a new one
               %>
                     <td align="center" style="border-color:#000000;border-top:0px;border-right:0px"><di:translate key="competence.competencematrixaddnewcoreassignment" /></td>
               <%
            }
         %>
         <td style="border-color:#000000;border-top:0px">&nbsp;</td>
      </tr>

      <%
         int iNumber = 1;
      %>
      <mm:list path="profiles,insrel,competencies" constraints="<%= sConstraints%>" >
         <%
            String sCompetenceID = "";
         %>
         <mm:node element="competencies">
            <mm:field name="number" jspvar="sID" vartype="String">
               <%
                  sCompetenceID = sID;
               %>
            </mm:field>
         </mm:node>
         <%
            String sInsrelID = "";
         %>
         <mm:node element="insrel">
            <mm:field name="number" jspvar="sID" vartype="String">
               <%
                  sInsrelID = sID;
               %>
            </mm:field>
         </mm:node>

         <mm:import jspvar="sCompetenceName"><mm:field name="competencies.name"/></mm:import>


         <tr>
            <td style="border-color:#000000; border-top:0px; border-right:0px"><%= iNumber %>.</td>
            <td style="border-color:#000000; border-top:0px;"><a href="<mm:write referid="wizardjsp"/>?wizard=config/competency/competencies&objectnumber=<mm:field name="competencies.number"/>" style="text-decoration:none;" title="<di:translate key="competence.competencematrix_edit_competence_help" arg0="<%= sCompetenceName %>" />"><%= sCompetenceName %></a></td>
            <td style="border-top:0px; border-left:0px; border-right:0px">&nbsp;</td>
            <mm:node element="competencies" jspvar="nodeCompetence">
            <%
               if(hsetCoreTasks.size() > 0)
               {
                  for(Iterator it = hsetCoreTasks.iterator(); it.hasNext();)
                  {
                     String sCoreTaskID = (String) it.next();

                     %><td align="center" style="border-color:#000000;border-right:0px;border-top:0px"><a href="<mm:write referid="wizardjsp"/>?wizard=config/insrel/insrel&objectnumber=<%=sInsrelID %>" title="<di:translate key="competence.competencematrix_edit_coretask_help" arg0="<%= sCompetenceName %>" />" style="text-decoration:none; width:100%"><%

                     if ((mapCoreTasks.get(sCoreTaskID) != null) && (((ArrayList) mapCoreTasks.get(sCoreTaskID)).contains(sCompetenceID)))
                     {
                        %>X<%
                     }
                     else
                     {
                        %>&nbsp;&mdash;&nbsp;<%
                     }

                     %><a></td><%
                  }
               }
               else
               {//click here to create a new one
                  %><td align="center" style="border-color:#000000; border-top:0px; border-right:0px"><a href="<mm:write referid="wizardjsp"/>?wizard=config/insrel/insrel&objectnumber=<%=sInsrelID %>" style="text-decoration:none; width:100%">&nbsp;</a></td><%
               }
               %>
                  <mm:last inverse="true">
                     <td style="border-color:#000000; border-top:0px; border-bottom:0px; border-right:0px">&nbsp;</td>
                  </mm:last>
                  <mm:last>
                     <td style="border-color:#000000; border-top:0px; border-right:0px">&nbsp;</td>
                  </mm:last>
               <%
               if(hsetCoreAssignments.size() > 0)
               {
                  for(Iterator it = hsetCoreAssignments.iterator(); it.hasNext();)
                  {
                     String sCoreAssignmentID = (String) it.next();

                     %><td align="center" style="border-color:#000000;border-right:0px;border-top:0px"><a href="<mm:write referid="wizardjsp"/>?wizard=config/insrel/insrel&objectnumber=<%=sInsrelID %>" title="<di:translate key="competence.competencematrix_edit_coreassignment_help" arg0="<%= sCompetenceName %>"/>" style="text-decoration:none; width:100%"><%

                     if ((mapCoreAssignments.get(sCoreAssignmentID) != null) && (((ArrayList) mapCoreAssignments.get(sCoreAssignmentID)).contains(sCompetenceID)))
                     {
                        %>X<%
                     }
                     else
                     {
                        %>&nbsp;&mdash;&nbsp;<%
                     }

                     %><a></td><%
                  }
               }
               else
               {//click here to create a new one
                  %><td align="center" style="border-color:#000000;border-top:0px;border-right:0px"><a href="<mm:write referid="wizardjsp"/>?wizard=config/insrel/insrel&objectnumber=<%=sInsrelID %>" style="text-decoration:none; width:100%">&nbsp;</a></td><%
               }
            %>
            </mm:node>
            <td style="border-color:#000000;border-top:0px">
               <mm:node element="insrel">
                  <mm:import id="rating_is_empty" reset="true">true</mm:import>


                  <nobr>
                     <mm:relatednodes type="ratings" orderby="ratings.pos" directions="down">
                        <mm:import id="rating_is_empty" reset="true">false</mm:import>
                        <mm:import jspvar="sRatingName"><mm:field name="name"/></mm:import>
                        <mm:first inverse="true">,</mm:first>
                        <a href="<mm:write referid="wizardjsp"/>?wizard=config/rating/ratings&objectnumber=<mm:field name="number"/>" style="text-decoration:none;" title="<di:translate key="competence.competencematrix_edit_success_criteria_help" arg0="<%= sRatingName %>"/>"><mm:field name="name"/></a>
                     </mm:relatednodes>
                  </nobr>

                  <mm:compare referid="rating_is_empty" value="true">
                     <a href="<mm:write referid="wizardjsp"/>?wizard=config/insrel/insrel&objectnumber=<%=sInsrelID %>" style="text-decoration:none; width:100%">&nbsp;</a>
                  </mm:compare>
               </mm:node>
            </td>
         </tr>
         <%
            iNumber++;
         %>
      </mm:list>
   </table>

   <di:translate key="competence.competencematrixaddnewexplanation" />

</mm:node>
</body>
</html>
</mm:cloud>
