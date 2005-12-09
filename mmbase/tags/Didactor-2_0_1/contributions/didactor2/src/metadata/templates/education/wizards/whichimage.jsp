<%@page import = "nl.didactor.metadata.util.MetaDataHelper" %>
<mm:field name="number" jspvar="sCurrentNode" vartype="String" write="false">
<%
   if((new MetaDataHelper()).hasValidMetadata(cloud,sCurrentNode)) {
   
      imageName = "gfx/metavalid.gif";
      sAltText = "Metadata is correct, bewerk metadata voor dit object";
   
   } else {
   
      imageName = "gfx/metaerror.gif";
      sAltText = "Metadata is niet correct, bewerk metadata voor dit object";
   }

%>
</mm:field>
