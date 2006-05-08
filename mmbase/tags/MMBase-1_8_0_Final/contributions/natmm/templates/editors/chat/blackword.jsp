<%@include file="/taglibs.jsp" %>
<%@ page import="org.mmbase.bridge.Node,nl.leocms.util.PublishUtil"%>

<mm:import externid="blackwordlist" jspvar="blackwordlist"/>

<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<title>Scheldwoordfilter</title>
</head>
<body>
<mm:cloud method="http" rank="administrator" jspvar="cloud">
    <mm:present referid="blackwordlist">
      <mm:compare referid="blackwordlist" value="" inverse="true">
        <mm:import externid="id" jspvar ="id"/>
<%
    Node chatServer = cloud.getNode(id);
   chatServer.setStringValue("blackwordlist", blackwordlist);
   chatServer.commit();
   PublishUtil.PublishOrUpdateNode(chatServer);
%>
      </mm:compare>
    </mm:present>
   <h2>Scheldwoordfilter chat</h2>
    <mm:listnodes type="chatservers" max="1">
   <form action="blackword.jsp" method="POST">
        <input type="hidden" name="id" value="<mm:field name="number"/>"/>
        <textarea name="blackwordlist" rows="20" cols="20"><mm:field name="blackwordlist"/></textarea><br />
        <input type="submit" value="Bewaar" />
   </form>
    </mm:listnodes>

</mm:cloud>
</body>
</html>
