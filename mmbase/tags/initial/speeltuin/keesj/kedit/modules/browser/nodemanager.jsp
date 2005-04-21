<%@page import="net.sf.mmapps.modules.config.*,net.sf.mmapps.modules.config.dumper.*,java.util.*"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud jspvar="cloud">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>Tabs</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="../../css/style.css" rel="stylesheet" type="text/css"> 
<!-- http://www.simplebits.com/tips/css_tabs.html -->
<SCRIPT LANGUAGE="JavaScript" src="windows.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript" src="wz_jsgraphics.js"></SCRIPT>
</head>
<mm:import externid="nodemanagername" jspvar="nodemanagername" required="true"/>

<body>
<div id="drawing" name="drawing" style="z-index : -2;"></div>
<script language="javascript">
var jg = new jsGraphics("drawing");
jg.setColor("#000000");
jg.setPrintable(true);
</script>
<style>
.header {
		  background-color: blue;
}
.content {
		  border: 3px solid;
		  background-color: white;
		  width: 200px;
		  height: 200px;
}
</style>
<% Configuration configuration = Dumper.getConfiguration(cloud); %>
<% ApplicationConfigurations configs = configuration.getApplicationConfigurations() ; %>
<% NodeManagerConfiguration nodeManagerConfiguration = configuration.getNodeManagerConfiguration(nodemanagername) ;%>
<% NodeManagerConfigurations list = new NodeManagerConfigurations();
   NodeManagerConfiguration current = nodeManagerConfiguration;
   do {
       list.add(current);
   } while ( ! current.getName().equals("object")  && (current = configuration.getNodeManagerConfiguration(current.getExtends())) != null);
%>


<%  int index = -1;
  for (int x = list.size() -1 ; x >= 0; x--){ %>
   <% index ++; %>
   <% current = list.getNodeManagerConfiguration(x); %>
   <div class="content" style="position: absolute; top: <%= index * 240%>px; left: 30px" id="nodeManager<%= current.getName() %>" onMouseOver="select(this);" onMouseOut="unselect();">
   <table width="100%">
     <% if (x != list.size() -1) { %> <%}  %>
     <tr><th class="header"><%= current.getName() %>(<%= current.getSingularGUIName("nl") %>)</th></tr>
     <%
       FieldConfigurations fieldConfigurations = current.getFieldConfigurations();
        for (int i = 0; i < fieldConfigurations.size(); i++) {
            FieldConfiguration fieldConfiguration = fieldConfigurations.getFieldConfiguration(i);
            String fieldType = fieldConfiguration.getType();
            String color = "#000000";
            if (fieldType.equals("STRING")) {
                color="#880000";
            } else if (fieldType.equals("INTEGER")) {
                color="#0000FF";
            } else if (fieldType.equals("XML")) {
                color="#0000EE";
            } else if (fieldType.equals("BYTE")) {
                color="#002233";
            } else if (fieldType.equals("NODE")) {
                color="#442233";
            } else {
                color="#448833";
            }
            %><tr><td bgcolor="#cccccc"><%= fieldConfiguration.getName()%></td></tr>
<%

        }
       %>
       </table>
       </div>
       <div>
            <%
               for (int j =0 ; j < configs.size() ; j++){
                   ApplicationConfiguration applicationConfiguration = configs.getApplicationConfiguration(j);
                   RelationManagerConfigurations relationManagerConfigurations = applicationConfiguration.getRelationManagerConfigurations();
                   int count =0;
                   for (int k =0 ; k < relationManagerConfigurations.size() ; k ++){
                      RelationManagerConfiguration relationManagerConfiguration = relationManagerConfigurations.getRelationManagerConfiguration(k);
                      if (relationManagerConfiguration.getSourceNodeManagerName().equals(current.getName()) || relationManagerConfiguration.getDestinationNodeManagerName().equals(current.getName())){
                          count ++;
                          String other = (relationManagerConfiguration.getSourceNodeManagerName().equals(current.getName()))?relationManagerConfiguration.getDestinationNodeManagerName():relationManagerConfiguration.getSourceNodeManagerName();
                          %>
<%
 int tx1 = (220 + 400) /2;
 int ty1 = ((120 + 240 * index) +(20 + 240 * index + (30 *count) ) )/ 2;
%>
<script>
jg.drawLine(220,<%= 120 + 240 * index %>,400,<%= 20 + 240 * index + (30 *count) %>);
jg.drawString('<%= relationManagerConfiguration.getRelationTypeName() %>',<%= tx1 %>,<%=  ty1 %>);
jg.drawLine(<%= tx1 %>,<%=  ty1 %>,<%= tx1 + 5 %>,<%=  ty1 + 5 %>);
jg.drawEllipse(400,<%= 10 + 240 * index + (30 *count) %>,100,20);
jg.drawString('<a href="nodemanager.jsp?nodemanagername=<%= other%>"><%= other %></a> <%= relationManagerConfiguration.getRelationTypeName() %>',410,<%= 10 + 240 * index + (30 *count) %>);
jg.paint();
</script>
<%--
<%= relationManagerConfiguration.getName() %>
--%>
<%
                      }
                   } 
               } 
            %>
</div>
<% }%>
<div id="monitor"></div>
<div id="monitor2"></div>
</body>
</html>
</mm:cloud>
