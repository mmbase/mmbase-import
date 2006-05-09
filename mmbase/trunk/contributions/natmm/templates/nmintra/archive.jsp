<%@include file="/taglibs.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/templateheader.jsp" 
%><%@include file="includes/calendar.jsp" 
%><%@include file="includes/header.jsp" 
%><%
   String sTemplateUrl = "archief.jsp";
	if(!projectId.equals("")) {
		%><%@include file="includes/projectoverview.jsp" %><%
	} else {

      int thisOffset = 0;
      try{
          if(!offsetId.equals("")){
              thisOffset = Integer.parseInt(offsetId);
              offsetId ="";
          }
      } catch(Exception e) {} 
      
      int fromDay = 0; int fromMonth = 0; int fromYear = 0;
      int toDay = 0; int toMonth = 0; int toYear = 0;
      int thisDay = cal.get(Calendar.DAY_OF_MONTH);
      int thisMonth = cal.get(Calendar.MONTH)+1;
      int thisYear = cal.get(Calendar.YEAR);
      int startYear = 2004;
      long fromTime = 0;
      long toTime = 0;
      
      boolean checkOnPeriod = false;
      boolean periodExceedsMonth = true;
      
      String extTemplateQueryString = templateQueryString; 
      String allConstraint = "";
      String employeeConstraint = "";
      String typeConstraint = "";
      String groupConstraint = "";
      String allPath = "projects";
      
	  if(!periodId.equals("")) {
          try{
              fromDay = new Integer(periodId.substring(0,2)).intValue(); 
              fromMonth = new Integer(periodId.substring(2,4)).intValue();
              fromYear = new Integer(periodId.substring(4,8)).intValue();
      
              toDay = new Integer(periodId.substring(8,10)).intValue();
              toMonth = new Integer(periodId.substring(10,12)).intValue();
              toYear = new Integer(periodId.substring(12)).intValue();
              if((fromDay+fromMonth+fromYear+toDay+toMonth+toYear)>0)
              {   // if not set use defaults for day, month and year
                  if(fromDay==0) fromDay = 1;
                  if(fromMonth==0) fromMonth = 1;
                  if(fromYear==0) fromYear = startYear; 
                  if(toDay==0) toDay = thisDay;
                  if(toMonth==0) toMonth = thisMonth;
                  if(toYear==0) toYear = thisYear;
      
                  cal.set(fromYear,fromMonth-1,fromDay,0,0,0);
                  fromTime = (cal.getTime().getTime()/1000);
      
                  cal.set(toYear,toMonth-1,toDay,23,60,0);
                  toTime = (cal.getTime().getTime()/1000);    
                  checkOnPeriod = (fromTime<=toTime);
                  periodExceedsMonth = toTime > (fromTime + 31*24*3600);
              }
          } catch (Exception e) { }
      } else {
          periodId = "";
      }
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
      if(!medewerkerId.equals("")) {
          extTemplateQueryString += "&medewerker=" + medewerkerId;
      }
      if(!typeId.equals("-1")) {
          extTemplateQueryString += "&type=" + typeId;
          typeConstraint = " projecttypes.number = '" + typeId + "'";
      }
      if(!groupId.equals("-1")) {
          extTemplateQueryString += "&group=" + groupId;
          groupConstraint = " readmore2.readmore = '" + groupId + "'";
      }

      %><%@include file="includes/searchfunctions.jsp" %>
      <td><%@include file="includes/pagetitle.jsp" %></td>
      <td><%
         String rightBarTitle = "";
         rightBarTitle = "Zoek&nbsp;in&nbsp;archief";
         %><%@include file="includes/rightbartitle.jsp" %></td>
      </tr>
      <tr>
      <td class="transperant">
      <div class="<%= infopageClass %>">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr><td colspan="3"><img src="media/spacer.gif" width="1" height="8"></td></tr>
        <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
            <td><%
                int listSize = 0;
				TreeSet searchResultSet = new TreeSet();
                String searchResults = ""; 
				%><mm:list path="<%= allPath %>" constraints="<%= allConstraint %>"
                   	   distinct="true" fields="projects.number"
					><mm:field name="projects.number" jspvar="projects_number" vartype="String" write="false"
						><% searchResultSet.add(projects_number); 
					%></mm:field
	            ></mm:list><%
				listSize = searchResultSet.size();
				searchResults = searchResults(searchResultSet); 
				searchResultSet = new TreeSet();
				if (!typeConstraint.equals("")) {
					%><mm:list nodes="<%= searchResults %>" path="projects,posrel,projecttypes"
							constraints="<%= typeConstraint %>" distinct="true" fields="projects.number"
						><mm:field name="projects.number" jspvar="projects_number" vartype="String" write="false"
							><% searchResultSet.add(projects_number); 
						%></mm:field
		            ></mm:list><%
					listSize = searchResultSet.size();
					searchResults = searchResults(searchResultSet); 
					searchResultSet = new TreeSet();
				}%><%= employeeConstraint %><%
            if(!medewerkerId.equals("")) {
					%><mm:list nodes="<%= searchResults %>" path="projects,readmore,medewerkers"
						><mm:field name="projects.number" jspvar="projects_number" vartype="String" write="false"
						><mm:field name="medewerkers.firstname" jspvar="employee_firstname" vartype="String" write="false"
						><mm:field name="medewerkers.lastname" jspvar="employee_lastname" vartype="String" write="false"
							><% String employeeName = subSearchString(employee_firstname + " " + employee_lastname);
								if (employeeName.indexOf(subSearchString(medewerkerId))!=-1) {
									searchResultSet.add(projects_number);
								}
						%></mm:field
						></mm:field
						></mm:field
		            ></mm:list><%
					listSize = searchResultSet.size();
					searchResults = searchResults(searchResultSet);
					searchResultSet = new TreeSet();
				}
				if (!groupConstraint.equals("")) { 
					%><mm:list nodes="<%= searchResults %>" path="projects,phaserel,phases"
						><mm:field name="projects.number" jspvar="projects_number" vartype="String" write="false"
						><mm:field name="phaserel.number" jspvar="phaserel_number" vartype="String" write="false"
							><mm:list nodes="<%= phaserel_number %>" 
									path="phaserel,readmore1,contentblocks,readmore2,attachments"
									constraints="<%= groupConstraint %>"
								><% searchResultSet.add(projects_number); 
							%></mm:list
						></mm:field
						></mm:field
					></mm:list><%
					listSize = searchResultSet.size();
					searchResults = searchResults(searchResultSet);
					searchResultSet = new TreeSet();
				}
                %><%@include file="includes/offsetlinks.jsp" %><%
                if(listSize>0) {
                   %><mm:list nodes="<%= searchResults %>" path="projects" orderby="projects.titel" directions="UP" 
                       offset="<%= "" + thisOffset*10 %>" max="10" distinct="true" fields="projects.number"
                       ><mm:node element="projects"><%
                       String readmoreUrl = "archive.jsp";
                       if(isIPage) readmoreUrl = "ipage.jsp";
					        readmoreUrl += "?p=" + pageId + "&project=";
                       %><mm:field name="number" jspvar="project_number" vartype="String" write="false"><%
                           readmoreUrl += project_number; 
                       %></mm:field
                       ><a href="<%= readmoreUrl %>"><div style="text-decoration:underline;" class="dark_<%= cssClassName  
                            %>"><mm:field name="titel"/></div>
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
                              <span class="normal"><%= summary %>... >></span></a><br><br>
					         </mm:node
					      ></mm:list><%
               } else {
                  %><mm:listnodes type="projects" max="1"
				>Er zijn geen projecten gevonden, die voldoen aan uw selectie criteria.
				<mm:import id="hasprojects"
		        /></mm:listnodes
			  ><mm:notpresent referid="hasprojects"
				>Dit archief bevat geen projecten.
			  </mm:notpresent><%
               }
               %><%@include file="includes/offsetlinks.jsp" 
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
%><%@include file="includes/footer.jsp" 
%></mm:cloud>
