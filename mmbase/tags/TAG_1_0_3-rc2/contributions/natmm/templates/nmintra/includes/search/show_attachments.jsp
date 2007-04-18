<mm:node element="attachments">
  <mm:field name="number" jspvar="attachments_number" vartype="String" write="false">
  <% 
  if(sQuery.equals("") || hsetAttachmentsNodes.contains(attachments_number)){
    bHasAttachments = true;
    %>
    <mm:field name="filename" jspvar="attachments_filename" vartype="String" write="false">
    <% 
    if (attachments_filename.equals("")) { %>
      <mm:field name="titel" jspvar="dummy" vartype="String" write="false">
        <% attachments_filename = dummy; %>
      </mm:field>
      <%	
    }
    String sExtension = "";
    int iDotIndex = attachments_filename.lastIndexOf(".");
    if (iDotIndex>-1){
      sExtension = attachments_filename.substring(iDotIndex).toLowerCase();
    }
    %>
    <p style="margin-bottom: -16px;margin-left: -16px">
      <%
      if(sExtension.equals(".pdf")){ 
        %><img src="media/icpdf.gif" alt="<mm:field name="titel" />" border="0"><%
      } else if(sExtension.equals(".doc")||sExtension.equals(".dot")||sExtension.equals(".rtf")){ 
         %><img src="media/icword.gif" alt="<mm:field name="tite," />" border="0"><%
      } else if(sExtension.equals(".xls")){
       %><img src="media/icexcel.gif" alt="<mm:field name="titel" />" border="0"><%
      } else if(sExtension.equals(".ppt")){ 
       %><img src="media/icppt.gif" alt="<mm:field name="titel" />" border="0"><%
      } else { 
       %><img src="media/ictxt.gif" alt="<mm:field name="titel" />" border="0"><%
      } %>
      <a href="<mm:attachment />" target="_blank"><%= attachments_filename %></a>
    </p>
    <br/>
    </mm:field>
    <%
  } %>
  </mm:field>
</mm:node>