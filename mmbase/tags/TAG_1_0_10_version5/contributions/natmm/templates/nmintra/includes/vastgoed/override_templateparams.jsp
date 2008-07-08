<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:cloud>
<%! String rubriekParams; %>
<% // to keep rubriek style&place during kart application, we pass a request parameter that overrides the rubriekstyle from templateheader.jsp
if (request.getParameter("rb") != null) {
	iRubriekStyle = Integer.parseInt(request.getParameter("rb"));
}
if (request.getParameter("rbid") != null) {
	rubriekId = request.getParameter("rbid");
}
if (request.getParameter("pgid") != null) {
	paginaID = request.getParameter("pgid");
}
if (request.getParameter("ssid") != null) {
   subsiteID = request.getParameter("ssid");
}

breadcrumbs = ph.getBreadCrumbs(cloud, paginaID);
rubriekParams = "?rb=" + iRubriekStyle + "&rbid=" + rubriekId + "&pgid=" + paginaID + "&ssid=" + subsiteID;
%>

<%-- we override stylesheet with the value we get using the passed rubriek --%>
	<mm:node number="<%= (String) breadcrumbs.get(0) %>" jspvar="thisRubriek">
	<% styleSheet = thisRubriek.getStringValue("style"); %>
	</mm:node>

</mm:cloud>