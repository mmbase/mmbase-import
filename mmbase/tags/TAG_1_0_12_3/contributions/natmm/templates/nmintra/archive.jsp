<%@page import="nl.leocms.util.tools.SearchUtil" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<mm:log jspvar="log">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<%@include file="includes/calendar.jsp" %>
<%@include file="includes/header.jsp" %>
<%
// this template shows the available projects

if(!projectId.equals("")) {
   %><%@include file="includes/projectoverview.jsp" %><%
} else {
   int objectPerPage = 10;
   int thisOffset = 1;
   try{
       if(!offsetId.equals("")){
           thisOffset = Integer.parseInt(offsetId);
           offsetId ="";
       }
   } catch(Exception e) {} 
   
   boolean debug = false;
   
   String extTemplateQueryString = templateQueryString; 
   String allConstraint = "";
   String employeeConstraint = "";
   String typeConstraint = "";
   String groupConstraint = "";
   String allPath = "projects";
   
   SearchUtil su = new SearchUtil();
   long [] period = su.getPeriod(periodId);
   long fromTime = period[0];
   long toTime = period[1];
   int fromDay = (int) period[2]; int fromMonth = (int) period[3]; int fromYear = (int) period[4];
   int toDay = (int) period[5]; int toMonth = (int) period[6]; int toYear = (int) period[7];
   int thisYear = (int) period[10];
   int startYear = (int) period[11];
   boolean checkOnPeriod = (fromTime<toTime);
   
   if(checkOnPeriod) {
       extTemplateQueryString += "&d=" + periodId;
       if(!allConstraint.equals("")) allConstraint += " AND ";
       allConstraint += "(( projects.begindate > '" + fromTime + "') AND (projects.enddate < '" + toTime + "'))";
   }
   if(!projectNameId.equals("")) {
       extTemplateQueryString += "&projectname=" + projectNameId;
       if(!allConstraint.equals("")) allConstraint += " AND ";
       allConstraint += " UPPER(projects.titel) LIKE '%" + projectNameId.toUpperCase() + "%'";
   }
   if(!departmentId.equals("default")) {
       extTemplateQueryString += "&department=" + departmentId;
       if(!allConstraint.equals("")) allConstraint += " AND ";
       allConstraint += " afdelingen.number = '" + departmentId + "'";
       allPath += ",readmore,afdelingen";
   }
   if(!employeeId.equals("-1")) {
       employeeConstraint = "medewerkers.number= '" + employeeId + "'";
       extTemplateQueryString += "&employee=" + employeeId;
   }
   if(!typeId.equals("-1")) {
       extTemplateQueryString += "&type=" + typeId;
       typeConstraint = " projecttypes.number = '" + typeId + "'";
   }
   if(!groupId.equals("-1")) {
       extTemplateQueryString += "&group=" + groupId;
       groupConstraint = " readmore2.readmore = '" + groupId + "'";
   }
  int listSize = 0;
  TreeSet searchResultSet = new TreeSet();
  String searchResults = ""; 
   %><mm:list path="<%= allPath %>" constraints="<%= allConstraint %>" distinct="true" fields="projects.number"
      ><mm:field name="projects.number" jspvar="projects_number" vartype="String" write="false"><%
        searchResultSet.add(projects_number); 
      %></mm:field
   ></mm:list><%
   listSize = searchResultSet.size();
   searchResults = su.searchResults(searchResultSet);
   searchResultSet = new TreeSet();
   if(debug) { log.info("allConstraint " + allConstraint + " : " + searchResults); }
   
   if (!typeConstraint.equals("")) {
      %><mm:list nodes="<%= searchResults %>" path="projects,posrel,projecttypes"
            constraints="<%= typeConstraint %>" distinct="true" fields="projects.number"
         ><mm:field name="projects.number" jspvar="projects_number" vartype="String" write="false"><%
            searchResultSet.add(projects_number); 
         %></mm:field
         ></mm:list><%
      listSize = searchResultSet.size();
      searchResults = su.searchResults(searchResultSet); 
      searchResultSet = new TreeSet();
      if(debug) { log.info("typeConstraint " + typeConstraint + " : " + searchResults); }
   }
   if(!employeeId.equals("-1")) {
      %><mm:list nodes="<%= searchResults %>" path="projects,readmore,medewerkers"
         constraints="<%= employeeConstraint %>"
         ><mm:field name="projects.number" jspvar="projects_number" vartype="String" write="false"><%
            searchResultSet.add(projects_number);
         %></mm:field
      ></mm:list><%
      listSize = searchResultSet.size();
      searchResults = su.searchResults(searchResultSet);
      searchResultSet = new TreeSet();
      if(debug) { log.info("employeeConstraint " + employeeConstraint + " : " + searchResults); }
   }
   if (!groupConstraint.equals("")) { 
      %><mm:list nodes="<%= searchResults %>" path="projects,phaserel,phases"
         ><mm:field name="projects.number" jspvar="projects_number" vartype="String" write="false"
         ><mm:field name="phaserel.number" jspvar="phaserel_number" vartype="String" write="false"
            ><mm:list nodes="<%= phaserel_number %>" 
                  path="phaserel,readmore1,contentblocks,readmore2,attachments"
                  constraints="<%= groupConstraint %>"><%
                searchResultSet.add(projects_number); 
            %></mm:list
         ></mm:field
         ></mm:field
      ></mm:list><%
      listSize = searchResultSet.size();
      searchResults = su.searchResults(searchResultSet);
      searchResultSet = new TreeSet();
      if(debug) { log.info("groupConstraint " + groupConstraint + " : " + searchResults); }
   }
   %>
      <td><%@include file="includes/pagetitle.jsp" %></td>
      <td><%
         String rightBarTitle = "";
         %><mm:node number="<%= paginaID %>" jspvar="thisPage"><%
            rightBarTitle = "Zoek in " + thisPage.getStringValue("titel");
         %></mm:node
         ><%@include file="includes/rightbartitle.jsp" %></td>
   </tr>
   <tr>
      <td class="transperant">
      <div class="<%= infopageClass %>" id="infopage">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr><td colspan="3"><img src="media/spacer.gif" width="1" height="8"></td></tr>
        <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
        <td>
        <%@include file="includes/info/offsetlinks.jsp" %><%
        if(listSize>0) {
             %><mm:list nodes="<%= searchResults %>" path="projects" orderby="projects.titel" directions="UP" 
                 offset="<%= "" + (thisOffset-1)*10 %>" max="10" distinct="true" fields="projects.number"
                 ><mm:node element="projects"><%
                 String readmoreUrl = "archive.jsp";
                 readmoreUrl += "?p=" + paginaID + "&project=";
                 %><mm:field name="number" jspvar="project_number" vartype="String" write="false"><%
                     readmoreUrl += project_number; 
                 %></mm:field>
                 <a href="<%= readmoreUrl %>">
                    <div style="text-decoration:underline;" class="dark"><mm:field name="titel"/></div>
                    <span class="normal" style="text-decoration:none;">
                      <%@include file="includes/dateperiod.jsp" %><br/>
                      <% String summary = ""; 
                        %><mm:field name="goal" jspvar="projects_goal" vartype="String" write="false"
                            ><mm:isnotempty><%
                                summary += projects_goal + " ";
                            %></mm:isnotempty
                        ></mm:field
                        ><mm:field name="omschrijving" jspvar="projects_description" vartype="String" write="false"
                            ><mm:isnotempty><%
                                summary += projects_description;
                            %></mm:isnotempty
                        ></mm:field><%
                        summary = HtmlCleaner.cleanText(summary,"<",">");
                        int spacePos = summary.indexOf(" ",70); 
                        if(spacePos>-1) { 
                           summary =summary.substring(0,spacePos);
                        }
                        %>
                        <%= summary %>... >>
                     </span>
                  </a><br/><br/>
                  </mm:node
               ></mm:list><%
        } else {
            %><mm:listnodes type="projects" max="1">
                Er zijn geen projecten gevonden, die voldoen aan uw selectie criteria.
                <mm:import id="hasprojects"
             /></mm:listnodes
             ><mm:notpresent referid="hasprojects">
                Dit archief bevat geen projecten.
             </mm:notpresent><%
        }
        %></td>
        <td><img src="media/spacer.gif" width="10" height="1"></td>
      </tr>
      </table>
      </div>
      </td><td><%
      // *************************************** right bar *******************************
      %><%@include file="includes/archivesearch.jsp" 
      %></td><%
} 
%>
<%@include file="includes/footer.jsp" %>
</mm:log>
</mm:cloud>
