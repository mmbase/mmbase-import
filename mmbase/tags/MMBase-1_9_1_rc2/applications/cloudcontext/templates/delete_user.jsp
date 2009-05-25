<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%><mm:content language="$language" expires="0">
<mm:cloud loginpage="login.jsp"  rank="$rank">

<mm:context>
  <mm:import externid="deleteuser" required="true" />


  <mm:node referid="deleteuser">
    <h1><mm:field name="gui()" /></h1>
    <%= getPrompt(m, "deleteduser")%> <mm:field name="gui()" />
    <mm:deletenode deleterelations="true" />
  </mm:node>
  
  <hr />
</mm:context>

<%@include file="index_users_body.jsp" %>

</mm:cloud>
</mm:content>
