<!--
    Simpel mmbase test using JSP instead of SCAN
-->
<%@ page import="java.util.*,org.mmbase.module.*,org.mmbase.module.core.*,org.mmbase.module.gui.jsp.*" %>

<body bgcolor="white">
<font size="4">
<HR>
GetNode test : <%= MMJsp.MMGetNode(5) %>
<HR>
GetField test int : <%= MMJsp.MMGetField(5,"otype") %><BR>
GetField test var : <%= MMJsp.MMGetField(5,"owner") %><BR>
<HR>
<% for (Enumeration list=MMJsp.MMGetList("urls","");list.hasMoreElements();)
    {
        MMObjectNode node=(MMObjectNode)list.nextElement();
        %>
        <%= node.getStringValue("url") %><BR>
        <%
    }%>
<HR>
