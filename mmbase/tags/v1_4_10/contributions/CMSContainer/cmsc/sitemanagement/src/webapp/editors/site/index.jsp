<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp" %>
<%@page import="com.finalist.cmsc.navigation.*" %>
<mm:cloud loginpage="../login.jsp">
<mm:content type="text/html" encoding="UTF-8" expires="0">

 <mm:import externid="channel" from="parameters" />
 <mm:present referid="channel">
	<mm:url page="/editors/site/Navigator.do" id="channelsurl" write="false" >
		<mm:param name="channel" value="${channel}"/>
	</mm:url>
	<mm:compare referid="channel" value="notfound" inverse="true">
	 	<mm:node referid="channel" jspvar="node">
		
 			<mm:import id="pagepath">../../<%= NavigationUtil.getPathToRootString(node, !ServerUtil.useServerName()) %></mm:import>
			<mm:url page="${pagepath}" id="contenturl" write="false" />
		</mm:node>
	</mm:compare>
</mm:present>

<mm:notpresent referid="channelsurl">
	<mm:url page="/editors/site/Navigator.do" id="channelsurl" write="false" />
</mm:notpresent>
<mm:notpresent referid="contenturl">
	<mm:url page="../empty.html" id="contenturl" write="false" />
</mm:notpresent>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html:html xhtml="true">
	<head>
		<title><fmt:message key="site.title" /></title>
		<script type="text/javascript" src="portalcontrols.js.jsp"></script>
		<script type="text/javascript" src="<cmsc:staticurl page='/js/window.js' />"></script>
	</head>
    <frameset cols="250,*" onload="window.top.toppane.initMenu();" framespacing="0" border="0">
		<frame scrolling="auto" frameborder="0"  src="<mm:url referid="channelsurl"/>" name="pages"/>
		<frame frameborder="0" src="<mm:url referid="contenturl"/>" name="content" />
	</frameset>
</html:html>

</mm:content>
</mm:cloud>

