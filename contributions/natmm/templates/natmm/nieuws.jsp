<% // *** article + dossiers and list of articles, which link to article *** %>
<%@include file="includes/top0.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%
if(NatMMConfig.hasClosedUserGroup) {

   %><%@include file="/editors/mailer/util/memberid_get.jsp" %><%
   if(memberid != null) {
      PoolUtil.addPools(cloud,paginaID,memberid,"" );
      if(!dossierID.equals("-1")) {
        PoolUtil.addPools(cloud,dossierID,memberid,"" );
      }
   }
}
%>
<%@include file="includes/top2_cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<%-- Fix for NMCMS-261. Any template calling others need to pass isNaardermeer as PaginaHelper/mm:import fails--%>
<%request.setAttribute("isNaardermeer", isNaardermeer);%>

<mm:locale language="nl"><% // used in <mm:time time=".." format="dd-MM-yyyy"/> %>
<%

TreeMap articles = new TreeMap();
if(artikelID.equals("-1")){ 
   %><mm:node number="<%=paginaID%>">
      <%@include file="includes/nieuws/articlessearch.jsp" %>
   </mm:node><%
}
int artCnt = articles.size();
if(artCnt==1&&artikelID.equals("-1")) { // *** select the unique article related to this page
   Long thisArticle = (Long) articles.firstKey();
   artikelID = (String) articles.get(thisArticle);
} %>
  <% if (isNaardermeer.equals("true")) { %>		
   	<div style="position:absolute; left:681px; width:70px; height:216px; background-image: url(media/natmm_logo_rgb2.gif); background-repeat:no-repeat;"></div>
  <% } %>
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
   <td style="vertical-align:top;padding:10px;padding-top:0px;width:185px;">
   <br/>
   <%@include file="includes/navleft.jsp" %>
   <br>
   <jsp:include page="includes/teaser.jsp">
      <jsp:param name="s" value="<%= paginaID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
      <jsp:param name="sr" value="0" />
   </jsp:include>
   </td>
   <% 
   if(!artikelID.equals("-1")&&artCnt<2) { // *** show the selected article, or the unique article related to this page
      %><td style="vertical-align:top;width:75%;padding:10px;padding-top:0px;">
		<br/>
         <mm:list nodes="<%= artikelID %>" path="artikel,posrel,dossier" orderby="dossier.naam">
            <mm:first>Dossier: </mm:first>
            <mm:first inverse="true">, </mm:first>
            <a href="nieuws.jsp?id=<mm:field name="dossier.number" />" style="margin-bottom:3px;"><mm:field name="dossier.naam" /></a>
         </mm:list>
         <% String showdate = "true"; %>
         <mm:node number="<%= dossierID %>" notfound="skipbody" jspvar="sourceDossier">
          <% showdate = ("yes".equals(sourceDossier.getStringValue("showdate")) ? "true" : "false"); %>
         </mm:node>
         <jsp:include page="includes/artikel_12_column.jsp">
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
            <jsp:param name="lnr" value="<%= lnRubriekID %>" />
            <jsp:param name="rnimageid" value="<%= rnImageID %>" />
            <jsp:param name="p" value="<%= paginaID %>" />
            <jsp:param name="a" value="<%= artikelID %>" />
            <jsp:param name="showdate" value="<%= showdate %>" />
         </jsp:include>
      </td><%
   } else {  // *** show the dossiers if there are dossiers related to this page
      %>
	      <% if (isNaardermeer.equals("true")) { %>			
   			<td style="vertical-align:top;width:100%;padding-left:10px;padding-right:66px;">
		  <% } else { %>
		    <td style="vertical-align:top;width:100%;padding-left:10px;padding-right:10px;">
		  <% } %>	
			  <br/>
         <%@include file="includes/page_intro.jsp" %>
         <%@include file="includes/dossier_form.jsp" %>
         <% 
         if(artikelID.equals("-1")&&!dossierID.equals("-1")){  // *** if there is no article selected, show the selected dossiers and its list of articles
         
            %><jsp:include page="includes/nieuws/showdossier.jsp">
               <jsp:param name="d" value="<%= dossierID %>" />
            </jsp:include><% 
                     
         } else if(artCnt > 1){  // *** if no dossier is selected and there are more than one articles related to the page, show the list of articles
         
            %><mm:node number="<%=paginaID%>">
               <mm:import id="showdate" />
               <%@include file="includes/nieuws/searchresults.jsp" %>
            </mm:node><%
            
         } %>
      </td><%
   } %>
</tr>
</table>
<%@include file="includes/footer.jsp" %>
</mm:locale>
</cache:cache>
</mm:cloud>
