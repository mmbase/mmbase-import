<%@page import="java.util.*,net.sf.mmapps.modules.config.*,net.sf.mmapps.modules.config.dumper.*"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud jspvar="cloud">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>Tabs</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="../../css/style.css" rel="stylesheet" type="text/css">
<!-- http://www.simplebits.com/tips/css_tabs.html -->
</head>

<body>
<%!
class NodeManagerConifguartionComparator implements Comparator {
  public int compare(Object o1, Object o2) {
    NodeManagerConfiguration a = (NodeManagerConfiguration) o1;
    NodeManagerConfiguration b = (NodeManagerConfiguration) o2;
    return a.getName().compareTo(b.getName());
  }
}
%>
<% Configuration configuration = Dumper.getConfiguration(cloud); %>
<% ApplicationConfigurations configs = configuration.getApplicationConfigurations() ; %>
<ul>
<% for (int x = 0 ; x < configs.size() ; x++ ){ %>
   <% ApplicationConfiguration appConfig = configs.getApplicationConfiguration(x) ; %>
   <li><%= appConfig.getName() %>
     <ul>
     <% NodeManagerConfigurations nodemanagers = appConfig.getNodeManagerConfigurations() ; %>
     <% Collections.sort(nodemanagers,new NodeManagerConifguartionComparator());%>
     <% for (int y = 0 ; y < nodemanagers.size() ; y++ ){ %>
        <% NodeManagerConfiguration nodeManagerConfiguration = nodemanagers.getNodeManagerConfiguration(y) ; %>
        <li><a target="content" href="nodemanager.jsp?nodemanagername=<%= nodeManagerConfiguration.getName() %>"><%= nodeManagerConfiguration.getName() %></a>
        </li>
     <% } %>
     </ul>
   </li>
<% } %>
</ul>
</body>
</html>
</mm:cloud>
