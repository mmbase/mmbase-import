<%@page language="java" contentType="text/html; charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0"   prefix="mm"
%><%@include file="import.jsp"
%><%@include file="settings.jsp"
%><mm:content language="$language" postprocessor="reducespace">
<mm:cloud loginpage="login.jsp" rank="$rank">
<%@include file="index_users_body.jsp" %>
</mm:cloud>
</mm:content>
