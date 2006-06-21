<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="unlinkcreation.title" /></title>
  <link rel="stylesheet" type="text/css" href="../style.css" />
  <script src="content.js" type="text/javascript"></script>
  <script src="../utils/window.js" type="text/javascript"></script>
  <script src="../utils/rowhover.js" type="text/javascript"></script>
</head>
<body>

<mm:cloud jspvar="cloud" rank="basic user" method='http'>

<mm:import externid="content" vartype="Node"/>
<mm:import externid="creationchannel" vartype="Node"/>
<mm:import externid="contentchannels" vartype="List"/>
<mm:import externid="trashchannel" vartype="Node"/>

<mm:import externid="returnurl" />

<mm:node referid="creationchannel">
	<mm:import id="creationnumber"><mm:field name="number"/></mm:import>
</mm:node>

<p>
<mm:node referid="content">
	<mm:import id="contentnumber"><mm:field name="number"/></mm:import>

	<fmt:message key="unlinkcreation.message">
		<fmt:param><mm:nodeinfo type="type" /></fmt:param>
		<fmt:param><mm:field name="title"/></fmt:param>
	</fmt:message>
</mm:node> 
</p>
<p>
<fmt:message key="unlinkcreation.selectchannel" />
</p>
<ul>
<mm:list referid="contentchannels">
	<mm:import id="channelnumber"><mm:field name="number"/></mm:import>
	
	<mm:compare referid="channelnumber" referid2="creationnumber" inverse="true">
        <mm:url page="LinkToChannelAction.do" id="url" write="false" >
           <mm:param name="action" value="unlink"/>
           <mm:param name="channelnumber" value="$creationnumber"/>
           <mm:param name="objectnumber" value="$contentnumber"/>
           <mm:param name="destionationchannel" value="$channelnumber"/>
           <mm:present referid="returnurl">
	           <mm:param name="returnurl" value="$returnurl"/>
           </mm:present>
        </mm:url>
        <li>
        <a href="<mm:write referid="url"/>">
			<mm:field name="path"/>
		</a>
		</li>
	</mm:compare>
</mm:list>
</ul>

<mm:node referid="trashchannel">
	<mm:import id="trashnumber"><mm:field name="number"/></mm:import>
	<mm:url page="LinkToChannelAction.do" id="trashurl" write="false" >
		<mm:param name="action" value="unlink"/>
		<mm:param name="channelnumber" value="$creationnumber"/>
		<mm:param name="objectnumber" value="$contentnumber"/>
		<mm:param name="destionationchannel" value="$trashnumber"/>
	</mm:url>
	<p>
	<a href="<mm:write referid="trashurl"/>">
		<fmt:message key="unlinkcreation.remove" />
		<img src="../img/trashcan.gif" width="15" height="15" alt="<fmt:message key="unlinkcreation.remove" />"/>
	</a>
	</p>
</mm:node>

</mm:cloud>
</body>
</html:html>
</mm:content>