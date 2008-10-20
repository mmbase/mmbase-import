<%  // *************  peoplefinder: listing of employees  ********************
if(!action.equals("print")) { 
  
  if(!(nameId.equals("")
      &&firstnameId.equals("")
      &&lastnameId.equals("")
      &&tasksId.equals("")
      &&descriptionId.equals("")
      &&departmentId.equals("default")
      &&locationId.equals("default")
      &&(programId.equals("default") && !onlyProgramSelect)
      &&keywordId.equals(""))){
        
        boolean debug = false;
        
        String employeeConstraint = "";
        if(!onlyProgramSelect) {
          // in the general who-is-who only people which are active or have special externid 'extern'
          SearchUtil su = new SearchUtil();
          employeeConstraint = su.sEmployeeConstraint;
        } else { 
          // if onlyProgramSelect all employees are selected, use a dummy clause to create a correct SQL statement
          employeeConstraint = "( medewerkers.importstatus = 'active')";
        }
        if(!firstnameId.equals("")) {
            employeeConstraint += " AND ";
            if(!nameId.equals("")) employeeConstraint += " ( ";
            employeeConstraint += "( UPPER(medewerkers.firstname) LIKE '%" + firstnameId.toUpperCase() + "%')";
        }
        if(!lastnameId.equals("")) {
            if(!nameId.equals("")) { 
                 employeeConstraint += " OR ";
            } else {
                 employeeConstraint += " AND ";
            }
            employeeConstraint += "( UPPER(medewerkers.lastname) LIKE '%" + lastnameId.toUpperCase() + "%')";
            if(!nameId.equals("")) employeeConstraint += " ) ";
        }
        if(!tasksId.equals("")) {
           employeeConstraint += " AND (UPPER(CAST(medewerkers.omschrijving_de AS CHAR)) LIKE '%" + tasksId.toUpperCase() + "%')";
        }        
        if(!descriptionId.equals("")) {
            employeeConstraint += " AND (UPPER(CAST(medewerkers.omschrijving AS CHAR)) LIKE '%" + descriptionId.toUpperCase() + "%')";
        }
        // ****** start the search by including all employees, which fit the employeeConstraint ****** 
        TreeSet searchResultSet = new TreeSet();
        SearchUtil su = new SearchUtil();
        String searchResults = "";        
        %><mm:list nodes="" path="medewerkers" constraints="<%= employeeConstraint %>"
            ><mm:field name="medewerkers.number" jspvar="employees_number" vartype="String" write="false"><%
            	searchResultSet.add(employees_number);
            %></mm:field
        ></mm:list><%
        searchResults = su.searchResults(searchResultSet);
        if(debug) { log.info(employeeConstraint + " : " + searchResults); }

        if(!departmentId.equals("default")&&!searchResults.equals("")) { // ****** add the department to the search ****** 
            searchResultSet.clear();
            String departmentConstraint = "afdelingen.number = '" + departmentId + "'";
            %><mm:list nodes="<%= searchResults %>" path="medewerkers,readmore,afdelingen" constraints="<%= departmentConstraint %>"
                ><mm:field name="medewerkers.number" jspvar="employees_number" vartype="String" write="false"><%
                  searchResultSet.add(employees_number);
                %></mm:field
            ></mm:list><%
            searchResults = su.searchResults(searchResultSet);
            if(debug) { log.info(departmentConstraint + " : " + searchResults); }
        }
        
        if(!locationId.equals("default")&&!searchResults.equals("")) { // ****** add the location to the search ****** 
            searchResultSet.clear();
            String locationConstraint = "locations.number = '" + locationId + "'";
            %><mm:list nodes="<%= searchResults %>" path="medewerkers,readmore,locations" constraints="<%= locationConstraint %>"
                ><mm:field name="medewerkers.number" jspvar="employees_number" vartype="String" write="false"><%
                  searchResultSet.add(employees_number);
                %></mm:field
            ></mm:list><%
            searchResults = su.searchResults(searchResultSet);
            if(debug) { log.info(locationConstraint + " : " + searchResults); }
        }
        
        if(onlyProgramSelect && "".equals(thisPrograms) ) {
          searchResults = "";
        }
        
        if((!programId.equals("default") || onlyProgramSelect ) &&!searchResults.equals("")) { // ****** add the program to the search ****** 
            if(!programId.equals("default")) { thisPrograms = programId; }
            thisPrograms = "," + thisPrograms + ",";
            searchResultSet.clear();
            %><mm:list nodes="<%= searchResults %>" path="medewerkers,readmore,programs" 
                ><mm:field name="programs.number" jspvar="programs_number" vartype="String" write="false"><%
                    if(thisPrograms.indexOf("," + programs_number + ",")>-1) { 
                        %><mm:field name="medewerkers.number" jspvar="employees_number" vartype="String" write="false"><%
                          searchResultSet.add(employees_number);
                        %></mm:field><%
                    }
                %></mm:field
            ></mm:list><%
            searchResults = su.searchResults(searchResultSet);
            if(debug) { log.info("programs " + thisPrograms + " : " + searchResults); }
        }
        
        if(!keywordId.equals("")) { // ****** add the keyword to the search ****** 
            searchResultSet.clear();
            %><mm:list nodes="<%= searchResults %>" path="medewerkers,related,keywords" constraints="<%= "keywords.number='" + keywordId + "'" %>"
                ><mm:field name="medewerkers.number" jspvar="employees_number" vartype="String" write="false"><%
                    searchResultSet.add(employees_number);
                %></mm:field
            ></mm:list><%
            searchResults = su.searchResults(searchResultSet);
            if(debug) { log.info("keywords " + keywordId + " : " + searchResults); }
        }
        
        if(!searchResults.equals("")) { 
            %><mm:list nodes="<%= searchResults %>" path="medewerkers" orderby="medewerkers.firstname,medewerkers.lastname" directions="UP,UP"
                fields="medewerkers.number,medewerkers.firstname,medewerkers.lastname,medewerkers.suffix">
            <%String slistSize = ""; %>
            <mm:size jspvar="listSize" vartype="String" write="false"><%slistSize = listSize; %></mm:size>
            <% 
               if (slistSize.equals("1") && (request.getParameter("employee") == null)) { 
            %>
               <mm:field name="medewerkers.number" jspvar="employees_number" vartype="String" write="false">
               <%
                  String toUrl = "/nmintra/smoelenboek.jsp" + templateQueryString 
                        + "&department=" +  departmentId 
                        + "&program=" +  programId
                        + "&name=" +  java.net.URLEncoder.encode(nameId) 
                        + "&firstname=" +  java.net.URLEncoder.encode(firstnameId) 
                        + "&lastname=" +  java.net.URLEncoder.encode(lastnameId)
                        + "&description=" +  java.net.URLEncoder.encode(descriptionId)
                        + "&employee=" +  employees_number; 
                  
                  response.sendRedirect(toUrl);
               %>
               </mm:field>
    
            <% } else { %>
            <mm:field name="medewerkers.number" jspvar="employees_number" vartype="String" write="false"
            ><mm:first>
                <div class="smoelenboeklist" id="smoelenboeklist"><table cellpadding="0" cellspacing="0" align="left">
                <tr>
					 	<td colspan="2" style="padding-bottom:10px;padding-left:19px;">
					 		<span class="light"><span class="pageheader">resultaten</span><br>klik op een naam voor details</span>
						</td>
                </tr>
            </mm:first>
                <tr>
                <td style="padding-bottom:5px;padding-left:18px;" style="color:white;"><li></td>
                <td style="padding-bottom:5px;padding-left:2px;">
                <a href="<%= "smoelenboek.jsp" + templateQueryString 
                            + "&department=" +  departmentId 
                            + "&program=" +  programId
                            + "&name=" +  java.net.URLEncoder.encode(nameId) 
                            + "&firstname=" +  java.net.URLEncoder.encode(firstnameId) 
                            + "&lastname=" +  java.net.URLEncoder.encode(lastnameId)
                            + "&description=" +  java.net.URLEncoder.encode(descriptionId)
                            + "&employee=" +  employees_number 
                     %>" class="hover"><span class="light"><mm:field name="medewerkers.firstname" /> <mm:field name="medewerkers.suffix" /> <mm:field name="medewerkers.lastname" />
            <mm:last>
                </table></div>
            </mm:last
        ></mm:field
        >
            <% } %>
        
        </mm:list><%

        } else { 

           %>
           <div class="smoelenboeklist" id="smoelenboeklist">
              <table cellpadding="0" cellspacing="0" align="left">
                <tr>
                  <td colspan="2" style="padding-bottom:10px;padding-left:19px;">
                    <span class="light"><span class="pageheader">resultaten</span></span>
                  </td>
                </tr>
                <tr>
                  <td style="padding-bottom:5px;padding-left:18px;"><span class="light"><li></span></td>
                  <td style="padding-bottom:5px;padding-left:2px;padding-right:10px;">
                    <span class="light">Er zijn geen medewerkers gevonden die aan je selectie voldoen.</span>
                  </td>
                </tr>
              </table>
           </div>
           <%
        }
    }
} 
%>