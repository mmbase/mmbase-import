<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%><mm:content language="$language">
  <mm:import id="url">edit_user.jsp</mm:import>
  <mm:import externid="user" required="true" />

  <mm:cloud rank="$rank">
    <mm:node referid="user">
      <mm:import id="current">users</mm:import>
      <%@include file="navigate.div.jsp" %>
      <%@include file="you.div.jsp" %>

      <h1><mm:function name="gui" /></h1>
      <%@include file="edit_user.form.jsp" %>
    </mm:node>
  </mm:cloud>
</mm:content>
