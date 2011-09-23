<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*,java.util.*"
%><mm:escaper id="svg" type="graphviz">
     <mm:param name="command">dot</mm:param>
 </mm:escaper
><mm:content type="image/svg+xml" postprocessor="svg">
<mm:import externid="nodemanager" jspvar="nodemanager" vartype="list" />
<mm:import externid="maxdistance" jspvar="maxdistance" vartype="integer">2</mm:import>
<mm:import id="baseurl" jspvar="url"><mm:url page="model.svg.jsp" /></mm:import>
<mm:cloud jspvar="cloud" method="asis">

Digraph "MMBase<%= nodemanager == null ? "" : " " + nodemanager %>" {
  edge [fontsize=8.0];
  node [fontsize=14.0,shape=box];
  nodesep=1;
  rankdir=LR;

  <%
      Map<String, Integer> set = new TreeMap<String, Integer>();
      Set<RelationManager> rmset = new HashSet<RelationManager>();
      for (Object nm : nodemanager) {
         set.put((String) nm, new Integer(0));
      }
      //out.println("root=" + nodemanager + ";");
      int size = -1;
      int distance = 1;
      while (size < set.size()) {
         size = set.size();
         Set<String> add = new HashSet<String>();
         for (RelationManager rm : cloud.getRelationManagers()) {
            try {
            if (distance > maxdistance) {
               if (set.containsKey(rm.getSourceManager().getName()) && set.containsKey(rm.getDestinationManager().getName()) || nodemanager.size() == 0) {
                  rmset.add(rm);
               }
            } else {
               if (set.containsKey(rm.getSourceManager().getName()) || set.containsKey(rm.getDestinationManager().getName()) || nodemanager.size() == 0) {
                  rmset.add(rm);
                  add.add(rm.getSourceManager().getName());
                  add.add(rm.getDestinationManager().getName());

               }
            }
            } catch (NotFoundException nfe) {
            }
         }
         for (String a : add) {
           if (! set.containsKey(a)) {
              set.put(a, distance);
           }
         }
         distance++;
      }
      for (Map.Entry<String, Integer> entry : set.entrySet()) {
         String nm = entry.getKey();
         int dist = entry.getValue();
         String color ;
         if (dist == 0) {
            color = ",color=green,fontcolor=green";
         } else if (dist == maxdistance) {
            color = ",color=lightgray,fontcolor=gray";
         } else {
            color = "";
         }
         Set<String> newNodeManagers = new TreeSet<String>();
         newNodeManagers.addAll(nodemanager);
         if (newNodeManagers.contains(nm)) {
            newNodeManagers.remove(nm);
         } else {
            newNodeManagers.add(nm);
         }
         StringBuilder nms = new StringBuilder();
         for (String n : newNodeManagers) {
            if (nms.length() > 0) {
               nms.append(",");
            }
            nms.append(n);
         }
         String u = url + "?nodemanager=" + nms + "&amp;maxdistance=" + maxdistance;
         out.println(nm  + " [label=<" + nm + ">" + color + ",URL=\"" + u + "\"];");
      }
  %>

  splines=true;
  edge [style=dashed];

  <jsp:scriptlet>
	for (RelationManager rm : rmset) {
     try {
     if (set.containsKey(rm.getSourceManager().getName()) || set.containsKey(rm.getDestinationManager().getName())) {
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
