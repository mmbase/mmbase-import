<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<html><body>
<mm:cloud>
<%
    Module community= LocalContext.getCloudContext().getModule("communityprc");
    Node channelNode = cloud.getNodeByAlias("Chat");
    int channelnr = channelNode.getNumber();
    NodeManager channels= cloud.getNodeManager("channel");
    channels.getInfo(channelnr+"-RECORD-FILE-test.log");
%>
</mm:cloud>
</body></html>
