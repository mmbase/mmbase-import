<%@include file="includes/top0.jsp" %>
<mm:import externid="title"></mm:import>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
   <%@include file="includes/top5b_pano.jsp" %>
</table>
<table width="565" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
   <tr>
     <td style="vertical-align:top;width:165px;padding:2px;">
           <mm:node number="<%= rubriekID %>">
             <mm:relatednodes type="images" path="contentrel,images" constraints="contentrel.pos='1'">
               <img src="<mm:image template="s(125)+part(0,0,125,125)" />" border="0" />
             </mm:relatednodes>
           </mm:node>
     </td>
     <td style="vertical-align:top;width:400px;padding:2px;">
        <span class="colortitle"><mm:write referid="title" /></span><br/>
        <mm:node number="<%= paginaID %>"><mm:field name="omschrijving" /></mm:node>
     </td>
   </tr>
   <tr>
     <td style="vertical-align:top;width:165px;padding:2px;">  
       <jsp:include page="includes/teaser.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
         <jsp:param name="sr" value="0" />
       </jsp:include>
     </td>
     <td style="vertical-align:top;width:400px;padding:2px;">
       <jsp:include page="includes/portal/middle_top.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
       </jsp:include>
       <jsp:include page="includes/teaser.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
         <jsp:param name="sr" value="1" />
       </jsp:include>
     </td>
   </tr>
   <tr><td colspan="2" style="height:3px;"><div style="
      border-top-width: 1px;
      border-top-style: solid;
      border-color: #3370AF; 
      margin: 0px 0px 0px 0px;"></div></td></tr>
   <tr>
      <td colspan="2" style="text-align:center;">
         <mm:node number="<%= rubriekID %>">
            <mm:field name="naam_de" />
         </mm:node>
      </td>
   </tr>
</table>
</body>
</html>
</mm:cloud>


