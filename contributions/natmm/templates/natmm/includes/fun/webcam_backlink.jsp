<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%
String callingPageID =request.getParameter("cp"); //for back to webcam functionality
if (callingPageID !=null) {
   PaginaHelper pHelper = new PaginaHelper(cloud);
   %>
   <a href="<%= pHelper.createPaginaUrl(callingPageID,request.getContextPath()) %>"><img src="media/arrowright_fun.gif" alt="" border="0" /></a>
   <a href="<%= pHelper.createPaginaUrl(callingPageID,request.getContextPath()) %>"><b>back to webcam</b></a><br/><br/>
	<%
	}
%>
</mm:cloud>	      	