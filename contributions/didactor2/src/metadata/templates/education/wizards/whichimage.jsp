<%@page import = "nl.didactor.metadata.util.MetaDataHelper" %>
<mm:field name="number" jspvar="sCurrentNode" vartype="String" write="false">
<%
   if((new MetaDataHelper()).hasValidMetadata(cloud,sCurrentNode)) {
   
      imageName = "gfx/metavalid.gif";
      sAltText = "metadata_correct";
   
   } else {
   
      imageName = "gfx/metaerror.gif";
      sAltText = "metadata_incorrect";
   }

%>
</mm:field>
