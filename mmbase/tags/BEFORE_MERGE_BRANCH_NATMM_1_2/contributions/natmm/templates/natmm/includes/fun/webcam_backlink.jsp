<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<%
if (callingPageID !=null) {
 %>
 <mm:node number="<%=callingPageID%>" notfound="skipbody">
 <%
   PaginaHelper pHelper = new PaginaHelper(cloud);
   %>
   <a href="<%= pHelper.createPaginaUrl(callingPageID,request.getContextPath()) %>"><img src="media/arrowright_fun.gif" alt="" border="0" style="vertical-align:bottom" /></a>
   &nbsp;<a href="<%= pHelper.createPaginaUrl(callingPageID,request.getContextPath()) %>"><b>terug naar <mm:field name="titel" vartype="String" /></b></a><br/><br/>
  </mm:node>
	<%
	}
%>
</mm:cloud>