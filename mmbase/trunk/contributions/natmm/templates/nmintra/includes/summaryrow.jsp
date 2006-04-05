<mm:remove referid="attachmentfound" />
<mm:node element="artikel" id="this_article"
><mm:field name="number" jspvar="article_number" vartype="String" write="false"
><mm:field name="titel" jspvar="article_title" vartype="String" write="false"
><mm:field name="intro" jspvar="article_introduction" vartype="String" write="false"
><mm:relatednodes type="attachments">
     <mm:first><table border="0" cellpadding="0" cellspacing="0"></mm:first>
     <tr><td><mm:field name="filename" jspvar="attachments_filename" vartype="String" write="false">
        <a href="<mm:attachment />" target="_blank">
            <% if(attachments_filename.indexOf(".pdf")>-1){ 
               %><img src="media/pdf.gif" alt="<mm:field name="title" />" border="0"><%
            } else if(attachments_filename.indexOf(".doc")>-1){ 
               %><img src="media/word.gif" alt="<mm:field name="title" />" border="0"><%
            } else if(attachments_filename.indexOf(".xls")>-1){
               %><img src="media/xls.gif" alt="<mm:field name="title" />" border="0"><%
            } else if(attachments_filename.indexOf(".ppt")>-1){ 
               %><img src="media/ppt.gif" alt="<mm:field name="title" />" border="0"><%
            } else { 
               %><img src="media/txt.gif" alt="<mm:field name="title" />" border="0"><%
            } %>
        </a>
        </mm:field></td>
    <mm:first>
    <td rowspan="<mm:size/>">
    <a href="<mm:attachment />" target="_blank"><div class="pageheader"><%= article_title %></div>
    <%@include file="../includes/poolanddate.jsp" %>
    <span class="normal"><%= article_introduction %></span></a>
    <mm:import id="attachmentfound" />
    </td>
    </mm:first>
    <tr>
    <mm:last></table><br><br></mm:last> 
</mm:relatednodes>
<mm:notpresent referid="attachmentfound">
   <a href="<%= readmoreUrl %><% if(!postingStr.equals("")) { %>&pst=|action=noprint<% } 
       %>"><div class="pageheader"><%= article_title %></div>
   <%@include file="../includes/poolanddate.jsp" %><%
   String summary = ""; 
   if(article_introduction!=null) {
           summary = article_introduction; 
           summary = HtmlCleaner.cleanText(summary,"<",">");
           int spacePos = summary.indexOf(" ",200); 
           if(spacePos>-1) { 
               summary =summary.substring(0,spacePos);
           } 
   }
   %><span class="normal"><%= summary   %> ... >></span></a><br><br>
</mm:notpresent>
</mm:field>
</mm:field>
</mm:field>
</mm:node>
