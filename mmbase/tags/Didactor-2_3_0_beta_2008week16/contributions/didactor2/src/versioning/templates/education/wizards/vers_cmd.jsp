<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="nl.didactor.versioning.VersioningController" %> 

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="command">-1</mm:import>
<mm:import externid="archiveid" jspvar="archiveId" vartype="String" reset="true">-1</mm:import>
<mm:import externid="nodeid" jspvar="nodeId" vartype="String">-1</mm:import>
<mm:compare referid="command" value="delete">
   <mm:node number="$archiveid" notfound="skip">
      <mm:deletenode deleterelations="true"/>
   </mm:node>
</mm:compare>

<mm:compare referid="command" value="restore">
   <mm:node number="$archiveid" notfound="skip">1
      <%
         VersioningController.restoreVersion( cloud.getNode(archiveId) );
      %>
   </mm:node>
</mm:compare>


<jsp:forward page="versioning.jsp">
   <jsp:param name="nodeid" value="<%= nodeId %>"/>
</jsp:forward>
</mm:cloud>
</mm:content>
