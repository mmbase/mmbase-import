<%@page import="nl.didactor.component.scorm.packages.Publisher"%>


<%
   fileStoreDir = new File(directory, requestDeletePackageID);
   fileTempDir  = new File(directory, requestDeletePackageID + "_");
%>

<mm:node number="<%= requestPublishPackageID %>">
   <mm:relatednodes path="educations" jspvar="nodeEducation">
     <%
     Publisher publisher = new Publisher(cloud);
     publisher.savePackage(nodeEducation);
     %>
   </mm:relatednodes>
</mm:node>
