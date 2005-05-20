<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.Iterator" %>
<%@ page import = "java.util.HashSet" %>
<%@ page import = "java.util.TreeMap" %>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>

<mm:import id="wizardjsp"><mm:treefile write="true" page="/editwizards/jsp/wizard.jsp" objectlist="$includePath" /></mm:import>
<mm:import id="listjsp"><mm:treefile write="true" page="/editwizards/jsp/list.jsp" objectlist="$includePath" /></mm:import>

<%
   String sProfileID = request.getParameter("profile");
%>

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


   <table border="1" cellpadding="0" cellspacing="0">
      <% // Let's paint the header of the table %>
      <tr>
         <td colspan="2"><b>Competenties</b></td>
         <%
            if(hsetCoreTasks.size() > 0)
            {
               %>
                  <td style="border-top:0px;border-bottom:0px;">&nbsp;&nbsp;&nbsp;</td>
                  <td colspan="<%= hsetCoreTasks.size() %>"><b>&nbsp;Kerntaken&nbsp;</b></td>
               <%
            }
            if(hsetCoreAssignments.size() > 0)
            {
               %>
                  <td style="border-top:0px;border-bottom:0px;">&nbsp;&nbsp;&nbsp;</td>
                  <td colspan="<%= hsetCoreAssignments.size() %>"><b>&nbsp;Kerntaken&nbsp;</b></td>
               <%
            }
         %>
         <td><b>&nbsp;Succes criteria&nbsp;</b></td>
      </tr>
      <tr>
         <td colspan="2"><a href="<mm:write referid="wizardjsp"/>?wizard=profiles&objectnumber=<mm:field name="number"/>" style="text-decoration:none;"><mm:field name="name"/></a></td>
         <%// Core tasks header
            if(hsetCoreTasks.size() > 0)
            {
               %>
                  <td style="border-top:0px;border-bottom:0px;">&nbsp;</td>
               <%
               for(Iterator it = hsetCoreTasks.iterator(); it.hasNext();)
               {
                  String sCoreTaskID = (String) it.next();
                  %>
                     <mm:node number="<%= sCoreTaskID %>">
                        <mm:field name="name" jspvar="sName" vartype="String">
                           <mm:node number="progresstextbackground">
                              <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(12)+gravity(NorthEast)+text(20,20,<%= sName %>)+rotate(270)</mm:import>
                              <td><a href="<mm:write referid="wizardjsp"/>?wizard=coretasks&objectnumber=<%= sCoreTaskID %>"><img border="0" src="<mm:image template="$template" />"/></a></td>
                           </mm:node>
                        </mm:field>
                     </mm:node>
                  <%
               }
            }
         %>
         <%//Core Assignments header
            if(hsetCoreAssignments.size() > 0)
            {
               %>
                  <td style="border-top:0px;border-bottom:0px;">&nbsp;</td>
               <%
               for(Iterator it = hsetCoreAssignments.iterator(); it.hasNext();)
               {
                  String sCoreAssignmentID = (String) it.next();
                  %>
                     <mm:node number="<%= sCoreAssignmentID %>">
                        <mm:field name="name" jspvar="sName" vartype="String">
                           <mm:node number="progresstextbackground">
                              <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(12)+gravity(NorthEast)+text(20,20,<%= sName %>)+rotate(270)</mm:import>
                              <td><a href="<mm:write referid="wizardjsp"/>?wizard=coreassignments&objectnumber=<%= sCoreAssignmentID %>"><img border="0" src="<mm:image template="$template" />"/></a></td>
                           </mm:node>
                        </mm:field>
                     </mm:node>
                  <%
               }
            }
         %>
         <td>&nbsp;</td>
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


         <tr>
            <td><%= iNumber %>.</td>
            <td><a href="<mm:write referid="wizardjsp"/>?wizard=competencies&objectnumber=<mm:field name="competencies.number"/>" style="text-decoration:none;"><mm:field name="competencies.name"/></a></td>
            <%
               if(hsetCoreTasks.size() > 0)
               {
                  %>
                     <td style="border-top:0px;border-bottom:0px;">&nbsp;</td>
                  <%
                  for(Iterator it = hsetCoreTasks.iterator(); it.hasNext();)
                  {
                     String sCoreTaskID = (String) it.next();
                     if ((mapCoreTasks.get(sCoreTaskID) != null) && (((ArrayList) mapCoreTasks.get(sCoreTaskID)).contains(sCompetenceID)))
                     {
                        %>
                           <td align="center"><a href="<mm:write referid="wizardjsp"/>?wizard=insrel&objectnumber=<%=sInsrelID %>" style="text-decoration:none;">X<a></td>
                        <%
                     }
                     else
                     {
                        %>
                           <td>&nbsp;</td>
                        <%
                     }
                  }
               }
               if(hsetCoreAssignments.size() > 0)
               {
                  %>
                     <td style="border-top:0px;border-bottom:0px;">&nbsp;</td>
                  <%
                  for(Iterator it = hsetCoreAssignments.iterator(); it.hasNext();)
                  {
                     String sCoreAssignmentID = (String) it.next();
                     if ((mapCoreAssignments.get(sCoreAssignmentID) != null) && (((ArrayList) mapCoreAssignments.get(sCoreAssignmentID)).contains(sCompetenceID)))
                     {
                        %>
                           <td align="center"><a href="<mm:write referid="wizardjsp"/>?wizard=insrel&objectnumber=<%=sInsrelID %>" style="text-decoration:none;">X<a></td>
                        <%
                     }
                     else
                     {
                        %>
                           <td>&nbsp;</td>
                        <%
                     }
                  }
               }
            %>
            <td>
               <mm:node element="insrel">
                  <mm:import id="rating_is_empty" reset="true">true</mm:import>

                  <mm:relatednodes type="ratings">
                     <mm:import id="rating_is_empty" reset="true">false</mm:import>
                     <a href="<mm:write referid="wizardjsp"/>?wizard=ratings&objectnumber=<mm:field name="number"/>" style="text-decoration:none;"><mm:field name="name"/></a>
                  </mm:relatednodes>

                  <mm:compare referid="rating_is_empty" value="true">
                     &nbsp;
                  </mm:compare>
               </mm:node>
            </td>
         </tr>
         <%
            iNumber++;
         %>
      </mm:list>
   </table>

</mm:node>

</mm:cloud>
