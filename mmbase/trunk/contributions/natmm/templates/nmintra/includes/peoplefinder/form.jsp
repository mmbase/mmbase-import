<% 
// **************** people finder: right bar with the form *****************

if(!action.equals("print")) { 
    %><%@include file="../whiteline.jsp" 
    %><form method="POST" action="<%= requestURL %>smoelenboek.jsp<%= templateQueryString %>" name="smoelenboek" onSubmit="return postIt('submit');">
        <input type="hidden" name="name" value="<%= nameId %>">
    <table cellpadding="0" cellspacing="0"  align="center">
        <tr><td class="bold">&nbsp;<span class="light_<%= cssClassName %>">Voornaam:</span></td>
            <td class="bold"><input type="text" class="<%=  cssClassName %>" style="width:103px;" name="firstname" value="<%= firstnameId %>">
            &nbsp;<br><div align="right"><span class="light_<%= cssClassName %>"><% if(nameId.equals("")){ %>en<% } else { %>of<% } %></span></div></td></tr>
        <tr><td class="bold">&nbsp;<span class="light_<%= cssClassName %>">Achternaam:</span>&nbsp;</td>
            <td class="bold"><input type="text" class="<%=  cssClassName %>" style="width:103px;" name="lastname" value="<%= lastnameId %>">
            &nbsp;<br><div align="right"><span class="light_<%= cssClassName %>">en</span></div></td></tr><%
         
    if(thisPrograms.equals("")) {
        
        %><tr><td class="bold">&nbsp;<span class="light_<%= cssClassName %>">En verder:</span>&nbsp;</td>
                    <td class="bold"><input type="text" class="<%=  cssClassName %>"  style="width:103px;" name="description" size="13" value="<%= descriptionId %>">
            &nbsp;<br><div align="right"><span class="light_<%= cssClassName %>">en</span></div></td></tr>
        <tr><td colspan="2" class="bold"><select name="department" class="<%=  cssClassName %>" style="width:195px;">
                <option value="default" <%  if(departmentId.equals("default")) { %>SELECTED<% } 
                    %>>alle afdelingen en regio's
            <mm:list path="afdelingen" orderby="afdelingen.naam" directions="UP" constraints="afdelingen.omschrijving!='-1'"
                ><mm:field name="afdelingen.number" jspvar="departments_number" vartype="String" write="false"
                ><mm:field name="afdelingen.naam" jspvar="departments_name" vartype="String" write="false"
                ><option value="<%= departments_number %>" <%   if(departments_number.equals(departmentId))  { %>SELECTED<% } 
                        %>><%= departments_name 
                %></mm:field
                ></mm:field
            ></mm:list
        ></select>&nbsp;<br><div align="right"><span class="light_<%= cssClassName %>">en</span></div></td></tr>
        <tr><td colspan="2"><select name="program" class="<%=  cssClassName %>" style="width:195px;">
                <option value="default" <%  if(programId.equals("default")) { %>SELECTED<% } 
                    %>>alle lokaties
            <mm:list path="locations" orderby="locations.naam" directions="UP"
                ><mm:field name="locations.number" jspvar="locations_number" vartype="String" write="false"
                ><mm:field name="locations.naam" jspvar="locations_name" vartype="String" write="false"
                    ><mm:list nodes="<%= locations_number %>" path="locations,readmore,medewerkers" max="1"
                        ><option value="<%= locations_number %>" <%  if(locations_number.equals(programId))  { %>SELECTED<% } 
                            %>><%= locations_name
                    %></mm:list
                ></mm:field>
                </mm:field
            ></mm:list
        ></select></td></tr><%
        
     } else if(thisPrograms.indexOf(",")>-1) {

        %><tr><td colspan="2"><select name="program" class="<%=  cssClassName %>" style="width:195px;">
                <option value="default" <%  if(programId.equals("default")) { %>SELECTED<% } 
                    %>>alle teams
            <mm:list nodes="<%= thisPrograms %>" path="programs" orderby="programs.title" directions="UP"
                ><mm:field name="programs.number" jspvar="programs_number" vartype="String" write="false"
                ><mm:field name="programs.title" jspvar="programs_title" vartype="String" write="false"
                    ><mm:list nodes="<%= programs_number %>" path="programs,readmore,medewerkers" max="1"
                        ><option value="<%= programs_number %>" <%  if(programs_number.equals(programId))  { %>SELECTED<% } 
                            %>><%= programs_title
                    %></mm:list
                ></mm:field>
                </mm:field
            ></mm:list
        ></select></td></tr><%          
     }
        %><tr><td colspan="2"><img src="media/spacer.gif" width="1" height="20"></td></tr>
        <tr><td>
            <input type="reset" name="clear" value="Wis" class="<%=  cssClassName 
                    %>" style="text-align:center;font-weight:bold;width:42px;" onClick="postIt('clear');">
            </td>
            <td>
                <div align="right"><input type="submit" name="submit" value="Zoek" class="<%=  cssClassName 
                     %>"  style="text-align:center;font-weight:bold;width:42px;">&nbsp;</div>
            </td></tr>
    </table></form>
    <script>
    <!--
    function postIt(action) {
        var href = document.smoelenboek.action;
        if(action!='clear') {
            var name = escape(document.smoelenboek.elements["name"].value);
            var firstname = escape(document.smoelenboek.elements["firstname"].value);
            var lastname = escape(document.smoelenboek.elements["lastname"].value);
            <% if(thisPrograms.equals("")) { %>
                var description = escape(document.smoelenboek.elements["description"].value);
                var department = escape(document.smoelenboek.elements["department"].value);
                var program = escape(document.smoelenboek.elements["program"].value);
                href += "&name=" + name +"&firstname=" + firstname + "&lastname=" + lastname + "&description=" + description + "&department=" + department + "&program=" + program;<%
           } else if(thisPrograms.indexOf(",")>-1) {  %>
                var program = escape(document.smoelenboek.elements["program"].value);
                href += "&name=" + name +"&firstname=" + firstname + "&lastname=" + lastname + "&program=" + program;<%
           } else { %>
                href += "&name=" + name +"&firstname=" + firstname + "&lastname=" + lastname;<%
           } %>   
        }
        document.location = href; 
        return false; 
    }
    //-->
    </script><% 
} %>