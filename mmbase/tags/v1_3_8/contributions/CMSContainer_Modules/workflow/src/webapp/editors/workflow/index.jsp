<%
String type = request.getParameter("workflow.type");
if (type == null || "".equals(type)) {
	type = (String) session.getAttribute("workflow.type");
}
String status = request.getParameter("workflow.status");
if (status == null || "".equals(status)) {
	status = (String) session.getAttribute("workflow.status");
}

String redirectTo = "ContentWorkflowAction.do";
if ("content".equals(type)) {
    redirectTo = "ContentWorkflowAction.do";
}
if ("link".equals(type)) {
    redirectTo = "LinkWorkflowAction.do";
}
if ("page".equals(type)) {
    redirectTo = "PageWorkflowAction.do";
}
if (status != null && !"".equals(status)) {
    redirectTo += "?status=" + status;
}


String offset = request.getParameter("offset");
if (offset != null && !"".equals(offset)) {
	if (status != null && !"".equals(status)) {
	    redirectTo += "&offset=" ;
	}
	else {
	    redirectTo += "?offset=" ;
	}
	redirectTo += offset;
}
String orderby = request.getParameter("orderby");
if (orderby != null && !"".equals(orderby)) {
	if (offset != null && !"".equals(offset)) {
	    redirectTo += "&orderby=" ;
	}
	else {
	    redirectTo += "?orderby=" ;
	}
	redirectTo += orderby;
}
redirectTo = response.encodeRedirectURL(redirectTo);
response.sendRedirect(redirectTo);
%>