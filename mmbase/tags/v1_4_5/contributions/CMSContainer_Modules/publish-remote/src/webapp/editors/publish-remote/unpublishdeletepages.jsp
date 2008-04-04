<%@ page import="net.sf.mmapps.modules.cloudprovider.CloudProvider" %>
<%@ page import="net.sf.mmapps.modules.cloudprovider.CloudProviderFactory" %>
<%@ page import="org.mmbase.bridge.Cloud" %>
<%@ page import="org.mmbase.bridge.Node" %>
<%@ page import="org.mmbase.bridge.NodeList" %>
<%@ page import="org.mmbase.bridge.util.SearchUtil" %>
<%@ page import="org.mmbase.remotepublishing.CloudManager" %>
<%@ page import="org.mmbase.remotepublishing.PublishManager" %>
<%@ page import="org.mmbase.remotepublishing.util.PublishUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Removing pages from live</title></head>
<body>
<h1>Removing pages from live</h1>
<%
   CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
   Cloud cloud = cloudProvider.getCloud();
   Cloud remoteCloud = CloudManager.getCloud(cloud, "live.server"); // 2
   NodeList pageList = SearchUtil.findNodeList(remoteCloud, "page");
   out.write("Number of pages: " + pageList.size() + "<br/>");

   String noremove = request.getParameter("noremove");
   String debug = request.getParameter("debug");
   int count = 0;
   for (Object o : pageList) {
      Node node = (Node) o;
      Node publishInfo = null;
      if ((publishInfo = PublishManager.getPublishInfoNode(cloud, node.getNumber(), remoteCloud)) != null) {
         int sourceNumber = publishInfo.getIntValue("sourcenumber");
         if (!cloud.hasNode(sourceNumber)) {
            if ("true".equals(debug)) {
               out.write("Removeable page : " + node.getNumber() + "<br/>");
            }
            if (noremove == null) {
               PublishUtil.removeNode(cloud, sourceNumber);
            }
            count++;
         }
      }
      out.flush();
   }
   if (noremove == null) {
      out.write("Number of pages put in the publishqueue (action = remove): " + count);
   }
   else {
      out.write("Number of removeablePages : " + count);
   }
%>
</body>
</html>