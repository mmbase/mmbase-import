<%@page import="nl.didactor.component.scorm.packages.Publisher"%>


<%
   fileStoreDir = new File(CommonUtils.fixPath(directory + File.separator + requestDeletePackageID));
   fileTempDir  = new File(CommonUtils.fixPath(directory + File.separator + requestDeletePackageID + "_"));
%>

<mm:node number="<%= requestPublishPackageID %>">
   <mm:related path="educations">
      <mm:node element="educations" jspvar="nodeEducation">
         <%
            Publisher publisher = new Publisher(cloud);
            publisher.savePackage(nodeEducation);
         %>
      </mm:node>
   </mm:related>
</mm:node>