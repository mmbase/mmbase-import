<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%><mm:content language="$language">
<mm:import externid="context" required="true" />

<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
  <mm:node id="context" referid="context">
    Deleted context <mm:field name="gui()" />
    <mm:deletenode deleterelations="true" />
  </mm:node>
  <hr />
  <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">index_contexts.jsp</mm:param></mm:url>"><%=getPrompt(m,"back")%></a>
</mm:cloud>
</mm:content>
