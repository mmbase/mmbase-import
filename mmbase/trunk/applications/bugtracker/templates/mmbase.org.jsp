<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page language="java" contentType="text/html; charset=utf-8"

%>
<mm:cloud>

<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp"%>
<td colspan="2">

  <%@include file="login.jsp" %>

  <mm:import externid="template">main.jsp</mm:import>

  <mm:notpresent referid="parametersgiven">
    <mm:include debug="html" referids="project,page" page="$template">
      <mm:param name="parameters" value="project,page" />
    </mm:include>
  </mm:notpresent>
  <mm:present referid="parametersgiven">
    <mm:include debug="html" page="$template" />
  </mm:present>  
</td>
<%@include file="/includes/footer.jsp"%>
</mm:cloud>