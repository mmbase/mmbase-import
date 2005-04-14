<%@page language="java" contentType="text/html;charset=utf-8" session="false"%>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content type="text/xml">
<%@page import="org.mmbase.bridge.*"%>
<%@page import="org.mmbase.util.*"%>
<result>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
<nodemanagers>
<%
  NodeManagerList nodeManagers = cloud.getNodeManagers();
  out.println("<!-- " + nodeManagers.size() + "node managers -->");
  for (int x = 0 ; x < nodeManagers.size() ; x ++){
    NodeManager nm = nodeManagers.getNodeManager(x);
    String stringConfiguration = nm.getStringValue("config");
    %><%= stringConfiguration %><%
  }
%>
</nodemanagers>
<relationmanagers>
<%
  NodeList list = cloud.getNodeManager("reldef").getList(null,null,null);
  for (int x = 0 ; x < list.size(); x++){
    Node node = null;
    try {
    node = list.getNode(x);
    String sname = node.getStringValue("sname");
    String sguiname = node.getStringValue("sguiname");
    String dguiname = node.getStringValue("dguiname");
    String dir = (node.getIntValue("dir") ==2)?"BIDIRECTIONAL":"UNIDIRECTIONAL";
    String builder  = cloud.getNodeManager(node.getIntValue("builder")).getName();
    %><relationmanager sname="<%= sname %>" sguiname="<%= Encode.encode("ESCAPE_XML",sguiname )%>" duigname="<%= Encode.encode("ESCAPE_XML",dguiname )%>" dir="<%= dir %>" builder="<%= builder %>"/><%
    } catch (Throwable t){
              out.write("<!-- ERROR WHILE LOADING NODE " + node + ":" + t.getMessage() + " -->");
              System.err.println("BBB "  + t.getClass().getName() + " " + t.getMessage() + " " +  org.mmbase.util.logging.Logging.stackTrace(t));
    }
  }

%>
</relationmanagers>
<relations>
<%
  NodeManager typerel = cloud.getNodeManager("typerel");
  NodeList nodeList = typerel.getList(null,null,null);
  for (int r=0 ; r < nodeList.size() ; r++){
    Node node = null;
    try {
      node = nodeList.getNode(r);
      if(! cloud.hasNode(node.getIntValue("snumber")) ){
      continue;
      }
      if(! cloud.hasNode(node.getIntValue("dnumber")) ){
      continue;
      }
      if(! cloud.hasNode(node.getIntValue("rnumber")) ){
      continue;
      }
      try {
      String from = cloud.getNodeManager(node.getIntValue("snumber")).getStringValue("name");
      String to = cloud.getNodeManager(node.getIntValue("dnumber")).getStringValue("name");
      String role = node.getNodeValue("rnumber").getStringValue("sname");
      %>
      <relation from="<%= from %>" to="<%= to %>" role="<%= role %>"/>
      <%
      } catch (NotFoundException nnfe){};
    }  catch (Throwable t){
              out.write("<!-- ERROR WHILE LOADING TYPEREL NODE " + node + ":" + t.getMessage() + " -->");
              System.err.println("AAAA "  + t.getClass().getName() + " " + t.getMessage() + " " +  org.mmbase.util.logging.Logging.stackTrace(t));
     }
  }
%>
</relations>
</mm:cloud>
</result>
</mm:content>
