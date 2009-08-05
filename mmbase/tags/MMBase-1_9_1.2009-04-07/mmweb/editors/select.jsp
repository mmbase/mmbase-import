<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">


<%	String userconstraint="mmbaseusers.username='" + cloud.getUser().getIdentifier() + "'"; %>

<% String editwizardsId = request.getParameter("editwizard"); %>

<% if(editwizardsId == null) { %>

<%@include file="includes/selectOverview.jsp" %>

<% } else { %>

<%@include file="includes/selectForm.jsp" %>

<% } %>
</mm:cloud>

