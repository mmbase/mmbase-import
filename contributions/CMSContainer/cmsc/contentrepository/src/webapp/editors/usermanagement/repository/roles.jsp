<%@page language="java" contentType="text/html;charset=utf-8" %>
<%@page import="com.finalist.tree.html.*"%>
<%@page import="com.finalist.cmsc.repository.*"%>
<%@page import="com.finalist.cmsc.security.forms.*"%>
<%@include file="../globals.jsp"  %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
<link href="../style.css" type="text/css" rel="stylesheet"/>
<title><fmt:message key="contentroles.title" /></title>
</head>
<body style="overflow: auto">
<mm:cloud jspvar="cloud" rank='administrator'>
<style>
	input.select { font-height: 4px;}
</style>
<html:form action="/editors/usermanagement/ContentRolesAction">
<input type="hidden" name="savetree" id="savetree" value="true">
<input type="hidden" name="channel" id="channel">
<input type="hidden" name="expand" id="expand">

<div id="rollen">
<p>
	<bean:write name="RolesForm" property="name"/>
	(<bean:write name="RolesForm" property="description"/>)
</p>
<p><b><fmt:message key="contentroles.description" /></b></p>
<p style="padding: 0px; margin: 0px; bottom: 0px;">

<mm:import externid="channel" from="parameters"><%= RepositoryUtil.ALIAS_ROOT %></mm:import>
<mm:import externid="expand" jspvar="expand">true</mm:import> 
<mm:node number="${channel}" jspvar="channel"> 

<%
	RolesForm form = (RolesForm) session.getAttribute("RolesForm");
	RolesInfo info = form.getRolesInfo();
    List openChannels = RepositoryUtil.getPathToRoot(channel);
    for (Iterator iter = openChannels.iterator(); iter.hasNext();) {
        Node node = (Node) iter.next();
        info.expand(node);
    }
    if (!expand.equals("true")) {
    	info.collapse(channel);
    }
    
	RepositoryTreeModel model = new RepositoryTreeModel(cloud);
	ContentRolesRenderer chr = new ContentRolesRenderer(cloud, form);
	ServerHTMLTree t = new ServerHTMLTree(model, chr, info, "javascript");
	t.setImgBaseUrl("../img/");
	t.render(out);
%>
</mm:node>
</p>
</div>
<br>
<table>
<tr>
	<td><html:submit style="width:90"><fmt:message key="contentroles.submit"/></html:submit></td>
	<td><html:cancel style="width:90"><fmt:message key="contentroles.cancel"/></html:cancel></td>
</tr>
</table>
</html:form>
</mm:cloud>
</body>
</html:html>
</mm:content>