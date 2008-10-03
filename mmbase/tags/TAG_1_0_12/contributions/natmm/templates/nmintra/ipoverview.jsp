<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%
if(!articleId.equals("-1")) { 

   String articleTemplate = "article.jsp" + templateQueryString;
   articleTemplate += (articleTemplate.indexOf("?")==-1 ? "?" : "&" ) + "showteaser=false";
	response.sendRedirect(articleTemplate);

} else {

   %>
   <%@include file="includes/cacheparams.jsp" %>
   <cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
   <%@include file="includes/header.jsp" %>
   <%
   isPreview = false; // *** surpress representation of clickable area's ***
   %>
   <td colspan="2"><%@include file="includes/pagetitle.jsp" %></td>
   </tr>
   <tr>
   <td colspan="2" class="transperant" valign="top">
   <div class="<%= infopageClass %>" id="infopage">
      <table border="0" cellpadding="0" cellspacing="0">
         <tr>
           <td style="width:550px;">
             <table border="0" cellpadding="0" cellspacing="0">
               <tr>
                 <td style="padding:10px;padding-top:18px;">
                   <%@include file="includes/relatedteaser.jsp" %>
                   <% String readmoreUrl = "ipoverview.jsp?p=" + paginaID + "&article="; %>
                   <%@include file="includes/imap/relatedimap.jsp" %>
                 </td>
               </tr>
             </table>
             <mm:node number="<%= paginaID %>">
                <%@include file="includes/contentblocks.jsp" %>
             </mm:node>
           </td>
         </tr>
      </table>
   </div>
   </td>
   <%@include file="includes/footer.jsp" %>
   </cache:cache>
   <%
} %>
</mm:cloud>
