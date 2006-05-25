<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/header.jsp" %>
<script language="JavaScript" type="text/javascript">
<!--
function postIt() {
	var website = document.selectform.elements["website"].value;
	document.location = "index.jsp?r=" + website;
}
//-->
</script>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" orderby="contentrel.pos" max="1"
	><%@include file="includes/relatedarticle.jsp" 
%></mm:list
><div align="right">
<%	String websiteConstraint = "parent.pos = -1";%>
<form name="selectform" method="post" action="">
        Lees informatie over&nbsp
	<select name="website" onChange="javascript:postIt();">
	<option selected>Natuurherstelproject...
	<mm:list nodes="<%= rootId %>" path="rubriek1,parent,rubriek2"
		orderby="rubriek2.naam" directions="UP"
		constraints="<%= websiteConstraint %>"
	 		><option value="<mm:field name="rubriek2.number" />"><mm:field name="rubriek2.naam" /></mm:list
	></select>
</form>
</div>
<mm:list nodes="<%= rootId %>" path="rubriek1,parent,rubriek2"
	orderby="rubriek2.naam" directions="UP"
	constraints="<%= websiteConstraint %>"
	><mm:first><table width="100%" border="0" cellpadding="0" cellspacing="0" class="body"></mm:first>
	<tr>
		<td width="75" valign="top"><a href="index.jsp?r=<mm:field name="rubriek2.number" 
			/>"><mm:node element="rubriek2"
					><mm:relatednodes type="images"
						><img src="<mm:image template="s(75)" />" border="0"></mm:relatednodes
				></mm:node
				></a></td>
		<td><img src="media/spacer.gif" width="15" height="1"></td>
		<td valign="top">
			<mm:node element="rubriek2" jspvar="rubriek2">
				<mm:related path="posrel,pagina" orderby="posrel.pos" directions="UP" max="1">
					<mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
					<span class="pageheader">
						<a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>"><%= rubriek2.getStringValue("naam") %>></a>
					</span><br/>
					</mm:field>
					<mm:field name="pagina.omschrijving" jspvar="pagina_omschrijving" vartype="String" write="false">
						<mm:isnotempty>
							<%= HtmlCleaner.cleanText(pagina_omschrijving,"<",">") %>
						</mm:isnotempty>	
					</mm:field>
				</mm:related>
			</mm:node>
		</td>
	</tr>
	<tr>
		<td colspan="3"><img src="media/spacer.gif" width="1" height="17"></td>
	</tr>
	<mm:last></table></mm:last>
</mm:list>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>