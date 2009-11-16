<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*,java.util.*"
%><mm:escaper id="svg" type="graphviz">
     <mm:param name="command">dot</mm:param>
 </mm:escaper
><mm:content type="image/svg+xml" postprocessor="svg">
<mm:import externid="nodemanager" jspvar="nodemanager" />
<mm:import id="baseurl" jspvar="url"><mm:url page="model.svg.jsp" /></mm:import>
<mm:cloud jspvar="cloud">

Digraph "MMBase<%= nodemanager == null ? "" : " " + nodemanager %>" {
  edge [fontsize=8.0];
  node [fontsize=14.0];
  nodesep=1;
  rankdir=LR;

  <%
      Set<String> set = new HashSet<String>();
      if (nodemanager != null) {
        set.add(nodemanager);
        out.println("root=" + nodemanager + ";");
      }
      int size = -1;
      while (size < set.size()) {
         size = set.size();
         for (RelationManager rm : cloud.getRelationManagers()) {
            try {
            if (set.contains(rm.getSourceManager().getName()) || set.contains(rm.getDestinationManager().getName()) || nodemanager == null) {
                set.add(rm.getSourceManager().getName());
                set.add(rm.getDestinationManager().getName());
            }
            } catch (NotFoundException nfe) {
            }
         }
      }
      for (String nm : set) {
         out.println(nm  + " [" + (nm.equals(nodemanager) ? "color=red,fontcolor=red," : "") + "URL=\"" + url + "?nodemanager=" + nm + "\"];");
      }
  %>

  splines=true;
  edge [style=dashed];

  <jsp:scriptlet>
	for (RelationManager rm : cloud.getRelationManagers()) {
     try {
     if (set.contains(rm.getSourceManager().getName()) || set.contains(rm.getDestinationManager().getName())) {
            out.print(rm.getSourceManager().getName() + "->" + rm.getDestinationManager().getName());
            String role = rm.getForwardRole();
            if (! "related".equals(role)) {
                out.print(" [label=" + role + "]");
            }
            out.println(";");
     }
     } catch (NotFoundException nfe) {
     }
	}
  </jsp:scriptlet>
}
  </mm:cloud>
</mm:content>
