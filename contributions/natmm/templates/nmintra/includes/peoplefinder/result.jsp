<%  // *************  peoplefinder: listing of employees  ********************
if(!action.equals("print")) { 
  
  if(!(nameId.equals("")&&firstnameId.equals("")&&lastnameId.equals("")&&descriptionId.equals("")
                &&departmentId.equals("default")&&programId.equals("default"))||!thisPrograms.equals("")){
        
        String debugStr = "";
        
        String employeeConstraint = "";
        if(thisPrograms.equals("")) {
            // *** in the general who-is-who only people which are active or have special externid 'wieiswie' ***
            employeeConstraint = "( medewerkers.importstatus != 'inactive' ) OR ( medewerkers.externid LIKE 'extern' )";
        } else { 
            // *** dummy constraint to create valid query, note: <>'0' omits all updated employees ***
            employeeConstraint = "( medewerkers.importstatus != '1')";
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
        if(!descriptionId.equals("")) {
            employeeConstraint += " AND ( UPPER(medewerkers.omschrijving) LIKE '%" + descriptionId.toUpperCase() + "%')";
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
        debugStr += employeeConstraint + " : " + searchResults + "\n";

        if(!departmentId.equals("default")&&!searchResults.equals("")){ // ****** add the department to the search ****** 
            searchResultSet.clear();
            String departmentConstraint = "afdelingen.number = '" + departmentId + "'";
            %><mm:list nodes="<%= searchResults %>" path="medewerkers,readmore,afdelingen" constraints="<%= departmentConstraint %>"
                ><mm:field name="medewerkers.number" jspvar="employees_number" vartype="String" write="false"><%
                  searchResultSet.add(employees_number);
                %></mm:field
            ></mm:list><%
            searchResults = su.searchResults(searchResultSet);
            debugStr += departmentConstraint + " : " + searchResults + "\n";
        }
        
        if(thisPrograms.equals("")) {
            if(!programId.equals("default")&&!searchResults.equals("")){ // ****** add the location to the search ****** 
                searchResultSet.clear();
                String locationConstraint = "locations.number = '" + programId + "'";
                %><mm:list nodes="<%= searchResults %>" path="medewerkers,readmore,locations" constraints="<%= locationConstraint %>"
                    ><mm:field name="medewerkers.number" jspvar="employees_number" vartype="String" write="false"><%
                      searchResultSet.add(employees_number);
                    %></mm:field
                ></mm:list><%
                searchResults = su.searchResults(searchResultSet);
                debugStr += locationConstraint + " : " + searchResults + "\n";
            }
        } else {
            if(!searchResults.equals("")){ // ****** add the program to the search ****** 
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
                debugStr += "programs " + thisPrograms + " : " + searchResults + "\n";
            }        
        }
        %><%-- = debugStr --%><%
        if(!searchResults.equals("")) { 
            %><mm:list nodes="<%= searchResults %>" path="medewerkers" orderby="medewerkers.firstname,medewerkers.lastname" directions="UP,UP"
                fields="medewerkers.number,medewerkers.firstname,medewerkers.lastname,medewerkers.suffix"
            ><mm:field name="medewerkers.number" jspvar="employees_number" vartype="String" write="false"
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
        ></mm:list><%

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