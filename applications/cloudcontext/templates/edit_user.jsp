<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="settings.jsp"
%><%@include file="import.jsp" %>
<mm:import id="url">edit_user.jsp</mm:import>
<mm:import externid="user" required="true" />

<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
<mm:node referid="user">
<%@include file="navigate.div.jsp" %>
 <%@include file="you.div.jsp" %>

 <h1><mm:field name="gui()" /></h1>
  <%@include file="edit_user.form.jsp" %>
   </mm:node>
  </mm:cloud>
