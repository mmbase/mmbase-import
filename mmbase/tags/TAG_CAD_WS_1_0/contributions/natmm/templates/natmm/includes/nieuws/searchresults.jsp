<table width="100%" cellspacing="0" cellpadding="0"><%
while(articles.size()>0) {
   PaginaHelper pHelper = new PaginaHelper(cloud);
   Long thisArticle = (Long) articles.lastKey();
   String article_number = (String) articles.get(thisArticle);
   String params = (dossierID.equals("-1") ? "" : "d="+ dossierID );
   %><mm:node number="<%= article_number %>">
   	<mm:field name="begindatum" jspvar="artikel_begindatum" vartype="String" write="false">
			<tr>
        <mm:present referid="showdate">
          <td width="65" valign="top"><mm:time time="<%=artikel_begindatum%>" format="dd-MM-yyyy"/></td>
          <td width="3" valign="top">&nbsp;&nbsp;|&nbsp;&nbsp;</td>
        </mm:present>
				<td>
				  <strong><a href="<%= pHelper.createItemUrl(article_number,paginaID,params,request.getContextPath()) %>"><mm:field name="titel" /></a></strong><br>
					<mm:field name="intro" jspvar="intro" vartype="String" write="false">
         			<% if(intro!=null&&!HtmlCleaner.cleanText(intro,"<",">","").trim().equals("")) { %><mm:write /><% } %>
   		      </mm:field>
			   </td>
			</tr>
			<tr><td colspan="3"><table class="dotline"><tr><td height="3"></td></tr></table></td></tr>
	   </mm:field>
   </mm:node>
   <%
   articles.remove(thisArticle);
} %>
</table>
   