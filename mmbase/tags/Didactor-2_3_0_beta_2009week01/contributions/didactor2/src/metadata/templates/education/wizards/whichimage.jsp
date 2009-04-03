<%@page import = "nl.didactor.metadata.util.MetaDataHelper" %>
<mm:field name="number" jspvar="sCurrentNode" vartype="String" write="false">
<%
   if(MetaDataHelper.hasTheObjectValidMetadata(cloud.getNode(sCurrentNode), application, nl.didactor.security.Authentication.getCurrentUserNode(cloud)).size() == 0) {

      imageName = "gfx/metavalid.gif";
      sAltText = "metadata_correct";

   } else {

      imageName = "gfx/metaerror.gif";
      sAltText = "metadata_incorrect";
   }
%>
</mm:field>
