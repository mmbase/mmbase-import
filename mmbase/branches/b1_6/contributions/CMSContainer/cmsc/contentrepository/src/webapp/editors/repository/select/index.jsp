<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp" %>
<%@page import="com.finalist.cmsc.repository.RepositoryUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<mm:import externid="channel" from="parameters" />
<mm:import externid="contentnumber" from="parameters" />
<mm:import externid="action" from="parameters" />

<mm:cloud loginpage="../../login.jsp">

<mm:present referid="channel">
	<mm:url page="/editors/repository/select/SelectorContent.do" id="channelsurl" write="false" >
		<mm:param name="channel" value="${channel}"/>
	</mm:url>
	<mm:url page="/editors/repository/select/Content.do" id="contenturl" write="false" >
		<mm:param name="parentchannel" value="${channel}"/>
	</mm:url>
</mm:present>

<mm:present referid="contentnumber">
	<mm:compare referid="contentnumber" value="" inverse="true">
	<mm:node number="$contentnumber" jspvar="node">
	<% if (RepositoryUtil.hasCreationChannel(node)) { %>
		<% Node cc = RepositoryUtil.getCreationChannel(node); %>
		<% if (cc != null) { %>
			<mm:url page="/editors/repository/select/SelectorContent.do" id="channelsurl" write="false" >
				<mm:param name="channel" value="<%= String.valueOf(cc.getNumber()) %>"/>
			</mm:url>
			<mm:url page="/editors/repository/select/content.jsp" id="contenturl" write="false" >
				<mm:param name="parentchannel" value="<%= String.valueOf(cc.getNumber()) %>"/>
			</mm:url>
		<% } %>
	<% } %>
	</mm:node>
	</mm:compare>
</mm:present>

<mm:present referid="action">
	<mm:url page="/editors/repository/SearchInitAction.do" id="contenturl" write="false" >
		<mm:param name="action" value="${action}" />
	</mm:url>
</mm:present>


<mm:notpresent referid="channelsurl">
	<mm:url page="/editors/repository/select/SelectorContent.do" id="channelsurl" write="false" />
</mm:notpresent>
<mm:notpresent referid="contenturl">
	<mm:url page="/editors/repository/SearchInitAction.do?action=select&mode=advanced" id="contenturl" write="false" />
</mm:notpresent>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html:html xhtml="true">
	<head>
		<title><fmt:message key="repository.title" /></title>
		<script>
			window.onresize= resizeTree;

			function resizeTree() {
			  if(window.frames["selectchannels"].resizeTreeDiv) {
			  	window.frames["selectchannels"].resizeTreeDiv();
			  }
			}
		</script>

	</head>
	<frameset cols="250,*" framespacing="0" border="0">
		<frame src="<mm:url referid="channelsurl"/>" name="selectchannels" frameborder="0" scrolling="no"/>
		<frame src="<mm:url referid="contenturl"/>" name="selectcontent" frameborder="0" />
	</frameset>
</html:html>
</mm:cloud>
</mm:content>