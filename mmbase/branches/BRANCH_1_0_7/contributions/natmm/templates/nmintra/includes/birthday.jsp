<mm:present referid="ishome"><%
cal.setTime(new Date());
String now = cal.get(Calendar.DAY_OF_MONTH) + " " + (cal.get(Calendar.MONTH) + 1);

String [] employees = new String[30];
int number_of_birthdays = 0;
int smoelWidth = 120;
String oalias_number = "";
String first_shown = "";
String last_shown = "";
String next_shown = "";

// an object alias with name homepagesmoel and destination an employee should exist in the cloud
%><mm:list path="oalias" fields="oalias.destination" constraints="oalias.name='homepagesmoel'"
	><mm:field name="oalias.destination" jspvar="oalias_destination" vartype="String" write="false"><% 
    last_shown = oalias_destination; 
	%></mm:field
	><mm:field name="oalias.number" jspvar="dummy" vartype="String" write="false"><%
    oalias_number = dummy;
	%></mm:field
></mm:list><%

String employeeConstraint = (new SearchUtil()).sEmployeeConstraint;

// if the object alias dissappears because of unknown reasons
if("".equals(oalias_number)) {
	%><mm:list path="medewerkers,images" max="1" constraints="<%= employeeConstraint %>">
      <mm:node element="medewerkers">
        <mm:createalias>homepagesmoel</mm:createalias>
      </mm:node>
	</mm:list><%
} 
// find birthdays of today, first_shown, next_shown

%>
<mm:list path="medewerkers,images" constraints="<%= employeeConstraint %>"
  ><mm:node element="medewerkers"
    ><mm:field name="number" jspvar="employees_number" vartype="String" write="false"
    ><mm:field name="dayofbirth" jspvar="dayofbirth" vartype="String" write="false"><%
      
        long td = Integer.parseInt(dayofbirth); td = 1000 * td; Date dd = new Date(td); cal.setTime(dd);
        String birthday_string =  cal.get(Calendar.DAY_OF_MONTH)+ " " + (cal.get(Calendar.MONTH)+1); 
        if(birthday_string.equals(now)) { 
          employees[number_of_birthdays] = employees_number;
          number_of_birthdays++; 
        } 
        if(first_shown.equals("")) { first_shown = employees_number; }	
        if(next_shown.equals("-1")) { next_shown = employees_number; }
        if(last_shown.equals(employees_number)) { next_shown = "-1"; } // set trigger to initialize next_shown
        
    %></mm:field
    ></mm:field
 ></mm:node
></mm:list>
<!--
oalias_number <%= oalias_number %><br/>
first_shown <%= first_shown %><br/>
next_shown <%= next_shown %><br/>
last_shown <%= last_shown %><br/>
number_of_birthdays <%= number_of_birthdays %><br/>
-->
<%

String employees_number = "";
if(number_of_birthdays>0) {
  int selectedEmployee = (int) Math.floor(number_of_birthdays*Math.random());
  employees_number = employees[selectedEmployee];
} else if(iRubriekLayout!=NMIntraConfig.SUBSITE1_LAYOUT) {
  // no birthday today
  if(next_shown.equals("")||next_shown.equals("-1")) {
    if(!first_shown.equals("")) {
      next_shown = first_shown;
    } else {
      next_shown = last_shown;
    }
  }
  employees_number = next_shown; 
  %><mm:node number="<%= oalias_number %>" notfound="skipbody"
    ><mm:setfield name="destination"><%= next_shown %></mm:setfield
  ></mm:node><%	
}
%>
<mm:node number="<%= employees_number %>" notfound="skipbody">
  <table width="220px" cellpadding="0" cellspacing="0" background="media/smoel_homepage_bg.gif" style="margin-left:20px;margin-bottom:20px;">
    <tr>
      <td>
        <a href="<%= ph.createPaginaUrl("wieiswie",request.getContextPath()) + "?employee=" +  employees_number %>">
          <img src="<mm:relatednodes type="images"><mm:image template="<%= "s(" + smoelWidth + ")+sharpen(5)" 
                        %>"/></mm:relatednodes>" alt="<mm:field name="titel" />" border="0" ></a></td>
    </tr>
    <tr>
      <td class="white" style="text-align:center;">
        <%
        if(number_of_birthdays>0) {
          %>
          <a href="mailto:<mm:field name="email" />?subject=Van harte gefeliciteerd !" >
            van harte gefeliciteerd!
          </a>
          <%
        } else {
          %>
          <a href="mailto:<mm:field name="email" />?subject=Smile - je staat op de homepage van Intranet" >
            smile!
          </a>
          <%
        } %>
      </td>
    </tr>
  </table>
</mm:node>
</mm:present>
