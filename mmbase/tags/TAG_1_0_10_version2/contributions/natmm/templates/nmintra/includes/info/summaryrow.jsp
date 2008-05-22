<mm:remove referid="attachmentfound" />
<mm:remove referid="this_article" />
<mm:node element="artikel" id="this_article" jspvar="thisArticle">
  <mm:notpresent referid="ishome">
    <mm:related path="posrel1,paragraaf,posrel2,attachments"
       orderby="posrel1.pos,posrel2.pos,attachments.title" directions="UP,UP,UP">
       <mm:node element="attachments">
         <mm:field name="filename" jspvar="attachments_filename" vartype="String" write="false">
         <a href="<mm:attachment />" target="_blank"><% 
             if(attachments_filename.indexOf(".pdf")>-1){ 
                %><img src="media/icpdf.gif" alt="<mm:field name="title" />" border="0"><%
             } else if(attachments_filename.indexOf(".doc")>-1 || attachments_filename.indexOf(".dot")>-1 || attachments_filename.indexOf(".rtf")>-1 ){ 
                %><img src="media/icword.gif" alt="<mm:field name="title" />" border="0"><%
             } else if(attachments_filename.indexOf(".xls")>-1){
                %><img src="media/icexcel.gif" alt="<mm:field name="title" />" border="0"><%
             } else if(attachments_filename.indexOf(".ppt")>-1){ 
                %><img src="media/icppt.gif" alt="<mm:field name="title" />" border="0"><%
             } else { 
                %><img src="media/ictxt.gif" alt="<mm:field name="title" />" border="0"><%
             } 
         %></a>
         </mm:field>
       </mm:node>
    </mm:related>
   </mm:notpresent>
   <a href="<%= readmoreUrl %><% if(!postingStr.equals("")) { %>&pst=|action=noprint<% }%>">
      <span class="pageheader"><%= thisArticle.getStringValue("titel") %></span>
   </a><br/>
   <a href="<%= readmoreUrl %><% if(!postingStr.equals("")) { %>&pst=|action=noprint<% } %>" class="hover">
     <%@include file="../poolanddate.jsp" %><%
     String summary = thisArticle.getStringValue("intro"); 
     summary = HtmlCleaner.cleanText(summary,"<",">");
     int spacePos = summary.indexOf(" ",200); 
     if(spacePos>-1) { 
       summary =summary.substring(0,spacePos);
     } 
     %><span class="normal"><%= summary   %> ... >></span>
   </a><br/><br/>
</mm:node>
