<mm:import id="number" reset="true" jspvar="jsp_number" vartype="Integer"><mm:field name="number"/></mm:import>
<mm:import id="firstname" reset="true" jspvar="jsp_firstname"><mm:field name="firstname"/></mm:import>
<mm:import id="lastname" reset="true" jspvar="jsp_lastname"><mm:field name="lastname"/></mm:import>
<mm:import id="username" reset="true" jspvar="jsp_username"><mm:field name="username"/></mm:import>
<mm:import id="email" reset="true" jspvar="jsp_email"><mm:field name="email"/></mm:import>
<mm:import id="lastactivity" reset="true" jspvar="jsp_lastactivity"><mm:field name="lastactivity"/></mm:import>
<%
  Object[] u = {jsp_number.toString(),jsp_firstname, jsp_lastname, jsp_username,jsp_email, jsp_lastactivity}; 
  emailUsers.put( jsp_number, u ); 
%>
