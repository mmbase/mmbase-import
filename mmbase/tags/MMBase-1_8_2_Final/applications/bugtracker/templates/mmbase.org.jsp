<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page language="java" contentType="text/html; charset=utf-8"

%>
<mm:cloud>

<%--
<mm:import id="portal">199</mm:import>
<mm:import id="page">546</mm:import>
--%>

<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp"%>

<%--
 <%@include file="showMessage.jsp" %>
--%>

<mm:import id="parameterspresent" externid="parameters" />
<mm:import externid="parameters">portal,page</mm:import>

 <%@include file="actions.jsp" %>

<td colspan="2">


  <%@include file="login.jsp" %>

  <mm:import externid="btemplate">main.jsp</mm:import>

  <mm:notpresent referid="parameterspresent">
    <mm:include debug="html" referids="parameters,$parameters" page="$btemplate" />
  </mm:notpresent>
  <mm:present referid="parameterspresent">
    <mm:include debug="html"  page="$btemplate" />
  </mm:present>


</td>
</tr>
<tr>
<td>
</td>
<td collspan="2">
 <%@include file="whoami.jsp"%>
</td>
</tr>


<%@include file="/includes/footer.jsp"%>
</mm:cloud>
