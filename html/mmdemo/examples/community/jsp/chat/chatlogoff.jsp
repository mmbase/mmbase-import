<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<html><body>
<mm:cloud>
<%
    Module community= LocalContext.getCloudContext().getModule("communityprc");
    Node channelNode = cloud.getNodeByAlias("Chat");
    int channelnr = channelNode.getNumber();
    NodeManager channels= cloud.getNodeManager("channel");
    channels.getInfo(channelnr+"-RECORD-STOP");
%>
</mm:cloud>
</body></html>
