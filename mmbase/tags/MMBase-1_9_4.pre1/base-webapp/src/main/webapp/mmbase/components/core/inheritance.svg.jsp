<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*,java.util.*"
%><mm:escaper id="svg" type="graphviz">
     <mm:param name="command">fdp</mm:param>
 </mm:escaper
><mm:content type="image/svg+xml" postprocessor="svg">
<mm:import externid="nodemanager" jspvar="nodemanager" />
<mm:import id="baseurl" jspvar="url"><mm:url page="inheritance.svg.jsp" /></mm:import>
<mm:cloud jspvar="cloud">

Digraph MMBase {
	edge [fontsize=2];
	node [fontsize=8.0];
	nodesep=1;

  <%
      Set<String> set = new HashSet<String>();
      if (nodemanager != null) {
        set.add(nodemanager);
        NodeManager nm = cloud.getNodeManager(nodemanager);
        while(true) {
        try {
          NodeManager p  = nm.getParent();
          out.println(p.getName()  + " [URL=\"" + url + "?nodemanager=" + p.getName() + "\"];");
          out.println(nm.getName() + "->" + p.getName() + ";");
          nm = p;
        } catch (NotFoundException nfe) {
          break;
        }
        }
      }
      int size = -1;
      while (size < set.size()) {
         size = set.size();
         for (NodeManager nm : cloud.getNodeManagers()) {
         try {
            NodeManager parent = nm.getParent();
            if (!set.contains(nm.getName()) && (set.contains(parent.getName()) || nodemanager == null)) {
               out.println(nm.getName()  + " [URL=\"" + url + "?nodemanager=" + nm.getName() + "\"];");
               out.println(nm.getName() + "->" + parent.getName() + ";");
               set.add(nm.getName());
            }
         } catch(NotFoundException nfe) {
         }
        }
     }
  %>
}
  </mm:cloud>
</mm:content>
