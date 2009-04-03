<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>

<mm:import id="lonumber" jspvar="lonumber" vartype="String" reset="true"><mm:write referid="learnobjectnumber"/></mm:import>
<mm:import jspvar="username" vartype="String" reset="true"><mm:write referid="user"/></mm:import>

<mm:cloud jspvar="cloud">
  <%
  Node lo = cloud.getNode(lonumber);
  
  
  NodeList developComp = lo.getRelatedNodes("competencies", "developcomp", "destination");
  Node user = cloud.getNode(Integer.parseInt(username));
  NodeList hasPop = user.getRelatedNodes("pop", "related", "destination");
  Node pop = (Node) hasPop.get(0);
  NodeList popHasComp = pop.getRelatedNodes("competencies", "havecomp", "destination");
  NodeList popDevelopComp = pop.getRelatedNodes("competencies", "developcomp", "destination");
  int retval = 1;
  if (developComp.size() == 0 || popHasComp.size() + popDevelopComp.size() == 0 ) {
  retval = 2;
  } else {
  for (int i=0; i<developComp.size(); i++) {
  if (!popHasComp.contains(developComp.get(i)) && !popDevelopComp.contains(developComp.get(i))) {
  retval = 2;
  }
  }
  }
  if (retval==1) { %><img src="<mm:treefile page="/pop/gfx/checked.gif" objectlist="$includePath" referids="$referids"/>" border="0"/><% }
  %>
</mm:cloud>
