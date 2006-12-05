<mm:related path="readmore,contentblocks,readmore,attachments">
  <mm:field name="attachments.number" jspvar="sAttID" vartype="String" write="false">
    <% 
    if (hsetAttachmentsContentblocksNodes.contains(sAttID)) { 
      bHasAttachments = true; %>
      <%@include file="show_attachments.jsp" %>
      <%	
    } %>
  </mm:field>
</mm:related>
