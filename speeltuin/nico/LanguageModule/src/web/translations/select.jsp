<% String editId = request.getParameter("editid"); %>
<% if(editId == null) { %>
	<%@include file="includes/selectOverview.jsp" %>
<% } else { %>
	<%@include file="includes/selectForm.jsp" %>
<% } %>


