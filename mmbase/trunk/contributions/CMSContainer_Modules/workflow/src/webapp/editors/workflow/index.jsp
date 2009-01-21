<%
String type = request.getParameter("workflowType");
if (type == null || "".equals(type)) {
	type = (String) session.getAttribute("workflowType");
}
String nodetype = request.getParameter("workflowNodetype");
if (nodetype == null || "".equals(nodetype)) {
   nodetype = (String) session.getAttribute("workflowNodetype");
}
String status = request.getParameter("workflow.status");
if (status == null || "".equals(status)) {
	status = (String) session.getAttribute("workflow.status");
}

String redirectTo = "AllcontentWorkflowAction.do";
if ("allcontent".equals(type)) {
   redirectTo = "AllcontentWorkflowAction.do";
}
if ("content".equals(type)) {
    redirectTo = "ContentWorkflowAction.do";
}
if ("asset".equals(type)) {
   redirectTo = "AssetWorkflowAction.do";
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
if (nodetype != null && !"".equals(nodetype)) {
    redirectTo += "&nodetype=" + nodetype;
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
redirectTo += "?fromIndex=yes";
redirectTo = response.encodeRedirectURL(redirectTo);
response.sendRedirect(redirectTo);
%>