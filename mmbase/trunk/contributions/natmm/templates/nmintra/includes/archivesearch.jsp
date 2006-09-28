<%@include file="../includes/whiteline.jsp" %>
<table cellpadding="0" cellspacing="0"  align="center" border="0">
<form method="POST" name="infoform" action="<%= javax.servlet.http.HttpUtils.getRequestURL(request) + templateQueryString %>" onSubmit="return postIt();">
<tr><td class="bold"><span class="light">Naam van het project</span></td></tr>
<tr><td><input type="text" name="projectname" style="width:172px;" value="<%= projectNameId %>"></td></tr>
<mm:list path="projects,posrel,projecttypes" orderby="projecttypes.name" directions="UP" 
     distinct="yes" fields="projecttypes.number"
     ><mm:first
         ><tr><td class="bold"><span class="light">Projecttype</span></td></tr>
         <tr><td>
         <select name="type" style="width:172px;">
     </mm:first
     ><mm:field name="projecttypes.number" jspvar="thistype" vartype="String" write="false"
         ><option value="<%= thistype %>" <% if (typeId.equals(thistype)) { %> selected <% } 
             %>><mm:field name="projecttypes.name"/></option>
     </mm:field
     ><mm:last
         ><option value="-1" <% if (typeId.equals("-1")) { %> selected <% } %>>Alles</option>
         </select>
          </td></tr>
     </mm:last
></mm:list
><%-- not used by M. Driessen 10.08.2005
<tr><td class="bold"><span class="light">Doelgroep</span></td></tr>
<tr><td>
     <select name="group" style="width:172px;">
         <option value="1" <% if (groupId.equals("1")) { %> selected <% } %>>Opdrachtgever</option>
         <option value="2" <% if (groupId.equals("2")) { %> selected <% } %>>Projectleider</option>
         <option value="3" <% if (groupId.equals("3")) { %> selected <% } %>>Projectmedewerker</option>
         <option value="-1" <% if (groupId.equals("-1")) { %> selected <% } %>>Alles</option>
     </select>
</td></tr> --%>
<mm:list path="projects,readmore,medewerkers" orderby="medewerkers.lastname" directions="UP" 
     distinct="yes" fields="medewerkers.number"
     ><mm:first
       ><tr><td class="bold"><span class="light">Medewerker</span></td></tr>
         <tr><td>
         <select name="employee" style="width:172px;">
     </mm:first>
     <mm:field name="medewerkers.number" jspvar="thisemployee" vartype="String" write="false"
         ><option value="<%= thisemployee %>" <% if(employeeId.equals(thisemployee)) { %> selected <% } 
             %>><mm:field name="medewerkers.titel" /></option>
     </mm:field
     ><mm:last>
         <option value="-1" <% if (employeeId.equals("-1")) { %> selected <% } %>>Alles</option>
         </select>
         </td></tr>
     <mm:import id="employeefound" 
     /></mm:last
></mm:list
><mm:list path="projects,readmore,afdelingen" orderby="afdelingen.naam" directions="UP" 
     distinct="yes" fields="afdelingen.number"
     ><mm:first
         ><tr><td class="bold"><span class="light">Afdeling</span></td></tr>
         <tr><td>
         <select name="department" style="width:172px;">
     </mm:first
     ><mm:field name="afdelingen.number" jspvar="thisdepartment" vartype="String" write="false"
         ><option value="<%= thisdepartment %>" <% if (departmentId.equals(thisdepartment)) { %> selected <% } 
             %>><mm:field name="afdelingen.naam" /></option>
     </mm:field
     ><mm:last
         ><option value="default" <% if (departmentId.equals("default")) { %> selected <% } %>>Alles</option>
         </select>
          </td></tr>
     <mm:import id="departmentfound" 
     /></mm:last
></mm:list
><tr><td>
   <table cellspacing="0" cellpadding="0" border="0">
   <tr>
      <td colspan="5" class="bold"><span class="light">Vanaf</span></td>
   </tr>
   <tr>
      <td><select name="from_day"><option value="00">...<%
              for(int i=1; i<=31; i++) { 
                  %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                  if(fromDay==i) { %>SELECTED<% } %>><%= i %><% 
              } %></select></td>
      <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
      <td><select name="from_month"><option value="00">...<%
              for(int i=1; i<=12; i++) { 
                  %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                  if(fromMonth==i) { %>SELECTED<% } %>><%= months_lcase[i-1] %><% 
              } %></select></td>
      <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
      <td><select name="from_year"><option value="00">...<%
              for(int i=startYear; i<=thisYear; i++) { 
                  %><option value="<%= i %>" <% 
                  if(fromYear==i) { %>SELECTED<% } %>><%=  i %><% 
              } %></select></td>
   </tr>
   </table>
</td></tr>
<tr><td>
   <table cellspacing="0" cellpadding="0" border="0">
   <tr>
      <td colspan="5" class="bold"><span class="light">Tot en met</span></td>
   </tr>
   <tr>
      <td><select name="to_day"><option value="00">...<%
              for(int i=1; i<31; i++) { 
                  %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                  if(toDay==i) { %>SELECTED<% } %>><%= i %><% 
              } %></select></td>
      <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
      <td><select name="to_month"><option value="0000">...<%
              for(int i=1; i<=12; i++) { 
                  %><option value="<% if(i<10){ %>0<% } %><%= i %>" <% 
                  if(toMonth==i) { %>SELECTED<% } %>><%= months_lcase[i-1] %><% 
              } %></select></td>
      <td><img src="media/spacer.gif" alt="" border="0" width="2" height="1"></td>
      <td><select name="to_year"><option value="0000">...<%
              for(int i=startYear; i<=thisYear; i++) { 
                  %><option value="<%= i %>" <% 
                  if(toYear==i) { %>SELECTED<% } %>><%= i %><% 
              } %></select></td>
   </tr>
   </table>
   <br/>
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
   </form>
   </table>
   <%@include file="../includes/whiteline.jsp" %>
   <script language="JavaScript" type="text/javascript">
   <%= "<!--" %>
   function postIt(el) {
       var href = document.infoform.action;
       if(el!='clear') {
          var projectname = document.infoform.elements["projectname"].value;
          if(projectname != '') href += "&projectname=" + projectname;
          var type = document.infoform.elements["type"].value;
          if(type != '') href += "&type=" + type;
          //var group = document.infoform.elements["group"].value;
          //if(group != '') href += "&group=" + group;
          <mm:present referid="employeefound">
          var employee = document.infoform.elements["employee"].value;
          if(employee != '') href += "&employee=" + employee;
		    </mm:present>
          <mm:present referid="departmentfound">
          var department = document.infoform.elements["department"].value;
          if(department != '') href += "&department=" + department;
          </mm:present>
          var period = "";
          var v = document.infoform.elements["from_day"].value;
          if(v != '') { period += v; } else { period += '00'; }
          v = document.infoform.elements["from_month"].value;
          if(v != '') { period += v; } else { period += '00'; }
          v = document.infoform.elements["from_year"].value;
          if(v != '') { period += v; } else { period += '0000'; }
          v = document.infoform.elements["to_day"].value;
          if(v != '') { period += v; } else { period += '00'; }
          v = document.infoform.elements["to_month"].value;
          if(v != '') { period += v; } else { period += '00'; }
          v = document.infoform.elements["to_year"].value;
          if(v != '') { period += v; } else { period += '0000'; }
          if(period != '0000000000000000') href += "&d=" + period;
       }
       document.location = href;
       return false;
   }
   <%= "//-->" %>
   </script>