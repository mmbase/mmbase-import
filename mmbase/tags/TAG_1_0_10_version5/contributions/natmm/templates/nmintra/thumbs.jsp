<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<%@include file="includes/header.jsp" %>
<td><%@include file="includes/pagetitle.jsp" %></td>
<td><% String rightBarTitle = "Zoek een collega";
%><%@include file="includes/rightbartitle.jsp"
%></td>
</tr>
<tr>
<td class="transperant" valign="top">
<div class="<%= infopageClass %>" id="infopage">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <td style="padding:10px;padding-top:18px;width:100%" width="100%">
    <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td width="100%"><mm:list nodes="<%= paginaID %>" path="pagina,posrel,images"
            constraints="posrel.pos='1'"> <%--default image is related to this page with 1 pos--%>
            <% imageTemplate = "s(300)";
            %><div align="center"><img src=<%@include file="includes/imagessource.jsp" %> alt="" border="0" ></div>
        </mm:list
        ></td>
    </tr>
    <tr>
        <td width="100%"><%@include file="includes/relatedteaser.jsp" %><br><br></td>
    </tr>
    </table><%

String otherGroupsId = request.getParameter("othergroups"); if(otherGroupsId==null){ otherGroupsId="default"; }    
    
if(departmentId.equals("default")&&programId.equals("default")&&otherGroupsId.equals("default")) { // *** select a random department ***
    Vector departments = new Vector();
    %><mm:list path="afdelingen" constraints="afdelingen.importstatus!='-1' AND afdelingen.importstatus!='inactive'"
        ><mm:field name="afdelingen.number" jspvar="departments_number" vartype="String" write="false"><%
            departments.add(departments_number);
        %></mm:field
    ></mm:list><%
    int selectedDepartment = (int) Math.floor(departments.size()*Math.random());
    if(selectedDepartment>departments.size()-1) { selectedDepartment = departments.size()-1; }
    departmentId = (String) departments.get(selectedDepartment);
}

// *************  listing of employees  ********************
SearchUtil su = new SearchUtil();
String employeeConstraint = su.sEmployeeConstraint;
String departmentNodes = departmentId;
String employeePath = "medewerkers";
if(!departmentId.equals("default")) {
    %><mm:node number="<%= departmentId %>"
        ><mm:field name="descendants" jspvar="dummy" vartype="String" write="false"
          ><mm:isnotempty><%
            departmentNodes = dummy;
          %></mm:isnotempty
        ></mm:field
    ></mm:node><%
    employeePath = "afdelingen,readmore," + employeePath;
}
if(!programId.equals("default")) {
    employeeConstraint += " AND locations.number = '" + programId + "'";
    employeePath += ",readmore,locations";
}
String defaultThumb = "";
%><mm:list nodes="<%= paginaID %>" path="pagina,posrel,images" constraints="posrel.pos='2'" max="1"
><mm:field name="images.number" jspvar="images_number" vartype="String" write="false"><%
   defaultThumb = images_number;
%></mm:field
></mm:list><%
// = employeeConstraint
%><%

boolean employeeFound = false;
int numberInRow = 0;
int maxInRow = 3;

if(departmentNodes.equals("default")) {
   departmentNodes = "";  
}
   %><mm:list nodes="<%= departmentNodes %>" path="<%= employeePath %>"
       orderby="medewerkers.firstname,medewerkers.lastname" directions="UP,UP" constraints="<%= employeeConstraint %>"
       fields="medewerkers.number" distinct="true"
       ><mm:field name="medewerkers.number" jspvar="employees_number" vartype="String" write="false">

       <%
       boolean matchProgram = true;
       if (!otherGroupsId.equals("default")) {
          matchProgram = false;
          %><mm:listcontainer nodes="<%= employees_number %>"  path="medewerkers,readmore,programs">
                <mm:constraint field="programs.number" operator="=" value="<%= otherGroupsId %>" />
                <mm:list max="1"><%
                  matchProgram = true;
                %></mm:list
             ></mm:listcontainer>
          <%
       }

       if (matchProgram) {
          if(!employeeFound) {
              employeeFound = true;
              %><table cellpadding="0" cellspacing="0" align="center"><tr><%
          }
          %><td>
          <a href="smoelenboek.jsp?p=wieiswie&employee=<%= employees_number %>&department=<%= departmentId %>&program=<%= programId
              %>&pst=|action=back"><img width="80px" height="108px" src="<mm:remove referid="imagefound"
                  /><mm:list nodes="<%= employees_number %>" path="medewerkers,posrel,images" max="1"
                      ><mm:node element="images"><mm:image template="s(80x108)" /></mm:node
                      ><mm:import id="imagefound"
                  /></mm:list
                  ><mm:notpresent referid="imagefound"
                      ><mm:node number="<%= defaultThumb %>" notfound="skipbody"><mm:image template="s(80x108)" /></mm:node
                  ></mm:notpresent>" alt="<mm:field name="medewerkers.firstname" /> <mm:field name="medewerkers.suffix" /> <mm:field name="medewerkers.lastname" />" border="0" /></a></td><%
          numberInRow ++;
          if(numberInRow>maxInRow) {
              %></tr><tr><%
              numberInRow=0;
          }
       }
       %>

       </mm:field
   ></mm:list><%

if(employeeFound) {
    while(numberInRow<maxInRow) {
        %><td><img src="media/spacer.gif" width="1" height="1"></td><%
        numberInRow++;
    }
    %></tr></table><%
} else {
    %><div class="pageheader"><span class="dark">Je hebt gezocht op:<ul><%
        boolean isFirst = true;
        if(!departmentId.equals("default")) {
            %><mm:node number="<%= departmentId %>"><li>de afdeling <mm:field name="naam" /></li></mm:node><%
            isFirst = false;
        }
        if(!programId.equals("default")) {
           %><mm:node number="<%= programId %>"><li><%if(!isFirst) { %> en<% }%> de lokatie <mm:field name="naam" /></li></mm:node><%
        }
        if(!otherGroupsId.equals("default")) {
           %><mm:node number="<%= otherGroupsId %>"><li><%if(!isFirst) { %> en<% }%> de groep <mm:field name="title" /></li></mm:node><%
        } %></ul></span>              
      <p>Er zijn geen medewerkers gevonden die voldoen aan je selectie.</p>
    </div><%
}
%></td></tr>
</table>
</div>
</td><%

// *************************************** right bar with the form *******************************
%><td valign="top">
    <%@include file="includes/whiteline.jsp"
    %><form method="POST" action="<%= javax.servlet.http.HttpUtils.getRequestURL(request) + templateQueryString
          %>" name="smoelenboek" onSubmit="return postIt(this);">
<table cellpadding="0" cellspacing="0"  align="center">
    <tr><td><select name="department" style="width:195px;">
        <option value="default" <%  if(departmentId.equals("default")) { %>SELECTED<% }
            %>>alle regios en afdelingen
    <mm:list path="afdelingen" orderby="afdelingen.naam" directions="UP" constraints="afdelingen.importstatus!='-1' AND afdelingen.importstatus!='inactive'"
            ><mm:field name="afdelingen.number" jspvar="departments_number" vartype="String" write="false"
            ><mm:field name="afdelingen.naam" jspvar="departments_name" vartype="String" write="false"
            ><option value="<%= departments_number %>" <%   if(departments_number.equals(departmentId))  { %>SELECTED<% }
                    %>><%= departments_name
            %></mm:field
            ></mm:field
        ></mm:list
    ></select>&nbsp;<br><div align="right"><span class="light">en</span></div></td></tr>
    <tr><td><select name="program" style="width:195px;">
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
    ></select>&nbsp;<br><div align="right"><span class="light">en</span></div></td></tr>
    <tr><td>

    <select name="othergroups" style="width:195px;">
     <option value="default" <%  if(otherGroupsId.equals("default")) { %>SELECTED<% }%>>alle overige groepen
     <mm:list path="programs" orderby="programs.title" directions="UP"
        ><mm:field name="programs.number" jspvar="programs_number" vartype="String" write="false"
        ><mm:field name="programs.title" jspvar="programs_title" vartype="String" write="false"
           ><mm:list nodes="<%= programs_number %>" path="programs,readmore,medewerkers" max="1"
              ><option value="<%= programs_number %>" <%  if(programs_number.equals(otherGroupsId))  { %>SELECTED<% }
                 %>><%= programs_title
           %></mm:list
        ></mm:field>
        </mm:field
     ></mm:list>
   </select>

    </td></tr>
    <tr><td><img src="media/spacer.gif" width="1" height="20"></td></tr>
    <tr><td>
    <table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
      <tr>
         <td>
            <input type="reset" name="clear" value="Wis" style="text-align:center;font-weight:bold;width:50px;" onClick="postIt('clear');">
         </td>
         <td style="text-align:right;padding-right:10px;">
            <input type="submit" name="Submit" value="Zoek" style="text-align:center;font-weight:bold;">
         </td>
      </tr>
      </table>
    </td></tr>
</table></form>
<script>
<!--
function postIt(el) {
   var href = document.smoelenboek.action;
   if(el!='clear') {
      var department = escape(document.smoelenboek.elements["department"].value);
      var program = escape(document.smoelenboek.elements["program"].value);
      href += "&department=" + department + "&program=" + program + "&othergroups=" + othergroups;
   }
   document.location = href;
   return false;
}
//-->
</script>
<%@include file="includes/whiteline.jsp" %>
</td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>