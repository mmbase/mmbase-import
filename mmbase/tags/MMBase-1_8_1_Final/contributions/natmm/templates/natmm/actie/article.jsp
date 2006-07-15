<%@include file="../includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../includes/top1_params.jsp" %>
<%@include file="../includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="../includes/top4_head.jsp" %>
<div style="position:absolute"><%@include file="/editors/paginamanagement/flushlink.jsp" %></div>
<table cellspacing="0" cellpadding="0" width="100%" align="center" border="0" valign="top">
   <%@include file="../includes/top5b_pano.jsp" %>
</table>
<mm:node number="<%= paginaID %>">
  <% 
  if(artikelID.equals("-1")) { 
    %>
    <mm:relatednodes type="artikel" path="contentrel,artikel" orderby="contentrel.pos" directions="UP" max="1">
       <mm:field name="number" jspvar="artikel_number" vartype="String" write="false">
          <% artikelID = artikel_number;%>
       </mm:field>
    </mm:relatednodes>
    <%   
  } 
  %>
  <table cellspacing="0" cellpadding="0" width="744" align="center" border="0" valign="top">
    <tr>
      <td style="padding:10px 0px 10px 4px;vertical-align:top">
        <%@include file="includes/homelink.jsp" %>
        <jsp:include page="../includes/teaser.jsp">
            <jsp:param name="s" value="<%= paginaID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
            <jsp:param name="sr" value="0" />
        </jsp:include>
        <%@include file="includes/mailtoafriend.jsp" %>
      </td>
   	 <td style="vertical-align:top;width:100%;padding:16px 10px 10px 10px;text-align:right;">
   	   <jsp:include page="includes/artikel_12_column.jsp">
          <jsp:param name="r" value="<%= rubriekID %>" />
          <jsp:param name="rs" value="<%= styleSheet %>" />
          <jsp:param name="lnr" value="<%= lnRubriekID %>" />
          <jsp:param name="rnimageid" value="<%= rnImageID %>" />
          <jsp:param name="p" value="<%= paginaID %>" />
          <jsp:param name="a" value="<%= artikelID %>" />
          <jsp:param name="showpageintro" value="true" />
        </jsp:include>
   	 </td>
    </tr>
  </table>
</mm:node>
<%@include file="includes/footer.jsp" %>
</body>
<%@include file="../includes/sitestatscript.jsp" %>
</html>
</cache:cache>
</mm:cloud>
