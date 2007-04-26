<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%
String callingPageID =request.getParameter("cp"); //for back to webcam functionality
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