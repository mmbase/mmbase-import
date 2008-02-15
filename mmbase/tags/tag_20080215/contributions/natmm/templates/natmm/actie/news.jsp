<%@include file="../includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../includes/top1_params.jsp" %>
<%@include file="../includes/top2_cacheparams.jsp" %>
<mm:import externid="show_one" jspvar="show_one">false</mm:import>
<% boolean bShowOne = !show_one.equals("false"); %>
<% if(bShowOne) { cacheKey += "~showone"; } %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="../includes/top4_head.jsp" %>
<div style="position:absolute"><%@include file="/editors/paginamanagement/flushlink.jsp" %></div>
<table cellspacing="0" cellpadding="0" width="100%" align="center" border="0" valign="top">
   <%@include file="../includes/top5b_pano.jsp" %>
</table>
<%
PaginaHelper pHelper = new PaginaHelper(cloud);
Calendar cal = Calendar.getInstance();
cal.setTime(now);
cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0);
long nowDay = cal.getTime().getTime()/1000; // the begin of today
long oneDay = 24*60*60;
String articleConstraint = (new SearchUtil()).articleConstraint(nowSec, quarterOfAnHour);
%>
<mm:node number="<%= paginaID %>">
  <%@include file="includes/navsettings.jsp" %>
  <% String navArtikelID = artikelID;
	  if(artikelID.equals("-1")) {  %>
		  <mm:relatednodes type="artikel" path="contentrel,artikel" max="1" orderby="begindatum" directions="DOWN" constraints="<%= articleConstraint %>">
   		  <mm:field name="number" jspvar="artikel_number" vartype="String" write="false">
      		  <% navArtikelID = artikel_number; %>
		     </mm:field>
		  </mm:relatednodes>
  <% } %>
  <table cellspacing="0" cellpadding="0" width="744px;" align="center" border="0" valign="top">
    <tr>
      <td style="padding-right:0px;padding-left:10px;padding-bottom:10px;vertical-align:top;padding-top:4px">
         <%@include file="includes/homelink.jsp" %>
         <%@include file="includes/mailtoafriend.jsp" %>
         <br/>
         <jsp:include page="../includes/teaser.jsp">
            <jsp:param name="s" value="<%= paginaID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
            <jsp:param name="sr" value="0" />
            <jsp:param name="tl" value="asis" />
         </jsp:include>
      </td>
		<td style="padding-right:0px;padding-left:10px;padding-bottom:10px;vertical-align:top;padding-top:10px">
			<% String sNodes = paginaID;
            String sPath = "pagina,contentrel,artikel";
            objectPerPage = 12;
            int thisOffset = 1;
            try{
               if(!offsetID.equals("0")){
                  thisOffset = Integer.parseInt(offsetID);
                  offsetID ="";
               }
            } catch(Exception e) {} 
				if (bShowOne){
					%>
					<%@include file="includes/artikel_show.jsp" %>
   		      <br/>
         		<br/>
		         <jsp:include page="../includes/shorty.jsp">
      		      <jsp:param name="s" value="<%= artikelID %>" />
            		<jsp:param name="r" value="<%= rubriekID %>" />
		            <jsp:param name="rs" value="<%= styleSheet %>" />
      		      <jsp:param name="sp" value="artikel,rolerel" />
		            <jsp:param name="sr" value="1" />
      		   </jsp:include>
					<%	
				} else { 
					%>
					<div class="colortitle" style="font:bold 110%;"><mm:field name="titel"/></div>
					<div style="padding-bottom:5px;"><b><mm:field name="kortetitel"/></b></div>
					<mm:list nodes="<%= sNodes %>" path="<%= sPath %>" constraints="<%= articleConstraint %>"
						offset="<%= "" + (thisOffset-1)*objectPerPage %>" max="<%= "" +  objectPerPage %>" 
						orderby="artikel.begindatum" directions="DOWN">
						<mm:first><table cellspacing="0" cellpadding="0" style="vertical-align:top;width:350px"></mm:first>
						<mm:node element="artikel">
							<mm:field name="number" jspvar="article_number" vartype="String" write="false">
							<mm:field name="begindatum" jspvar="artikel_begindatum" vartype="String" write="false">
								<tr>
									<td width="65" valign="top"><mm:time time="<%=artikel_begindatum%>" format="dd-MM-yyyy"/></td>
									<td width="3" valign="top">&nbsp;&nbsp;<span style="font:bold 110%;color:red">></span>&nbsp;&nbsp;</td>
									<td>
										<strong><a href="<%= pHelper.createItemUrl(article_number,paginaID,"offset="+thisOffset+"&show_one=true",request.getContextPath()) %>" class="maincolor_link"><mm:field name="titel" /></a></strong><br>
										<mm:field name="intro" jspvar="intro" vartype="String" write="false">
											<% if(intro!=null&&!HtmlCleaner.cleanText(intro,"<",">","").trim().equals("")) { %><mm:write /><% } %>
										</mm:field>
									</td>
								</tr>
								<mm:last inverse="true">
									<tr><td colspan="3"><table class="dotline"><tr><td height="3"></td></tr></table></td></tr>
								</mm:last>	
							</mm:field>
							</mm:field>
						</mm:node>
						<mm:last></table></mm:last>
					</mm:list>
					<%
				} %>
      </td>
      <td style="padding-right:10px;padding-left:10px;padding-bottom:10px;padding-top:10px;vertical-align:top;width:190px;">
         <jsp:include page="includes/nav.jsp">
            <jsp:param name="a" value="<%= navArtikelID %>" />
            <jsp:param name="p" value="<%= paginaID %>" />
            <jsp:param name="object_type" value="artikel" />
            <jsp:param name="object_intro" value="titel" />
            <jsp:param name="object_date" value="begindatum" />
            <jsp:param name="extra_constraint" value="<%= articleConstraint %>" />
            <jsp:param name="show_links" value="false" />
				<jsp:param name="object_per_page" value="<%= "" + objectPerPage %>" />
         </jsp:include>
			<% if (bShowOne){ %>
				<%@include file="includes/meernieuwslink.jsp" %>
			<% } %>	
   		<jsp:include page="../includes/shorty.jsp">
   	      <jsp:param name="s" value="<%= paginaID %>" />
   	      <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
   	      <jsp:param name="sr" value="2" />
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