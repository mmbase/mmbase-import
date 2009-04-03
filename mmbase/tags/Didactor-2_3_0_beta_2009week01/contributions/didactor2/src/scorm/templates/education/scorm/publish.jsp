<%@page import="nl.didactor.component.scorm.packages.Publisher"
%>
<mm:node number="<%= requestPublishPackageID %>">
<mm:relatednodes path="educations" jspvar="nodeEducation">
  <%
  Publisher publisher = new Publisher(cloud);
  publisher.savePackage(nodeEducation);
  %>
</mm:relatednodes>
</mm:node>
