<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%><mm:content language="$language" expires="0">
<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">

<mm:context>
  <mm:import externid="user" required="true" />


  <mm:node id="user" referid="user">
    <h1><mm:field name="gui()" /></h1>
    <%= getPrompt(m, "deleteduser")%> <mm:field name="gui()" />
    <mm:deletenode deleterelations="true" />
  </mm:node>
  
  <hr />
</mm:context>

<%@include file="index_users_body.jsp" %>

</mm:cloud>
</mm:content>
