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
	document.location = "<%= "/" + ph.getSubDir(cloud,paginaID) %>index.jsp?r=" + website;
}
//-->
</script>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" orderby="contentrel.pos" max="1"
	><%@include file="includes/relatedarticle.jsp" 
%></mm:list>
<mm:node number="<%= rubriekId %>">
	<div align="right">
		<form name="selectform" method="post" action="">Lees informatie over&nbsp
			<select name="website" onChange="javascript:postIt();">
			<option selected>Natuurherstelproject...
			<mm:related path="parent,rubriek"
				orderby="rubriek.naam" directions="UP" searchdir="destination"
				constraints="rubriek.isvisible=='1'"
					><option value="<mm:field name="rubriek.number" />"><mm:field name="rubriek.naam" /></mm:related
			></select>
		</form>
	</div>
	<mm:related path="parent,rubriek"
		orderby="rubriek.naam" directions="UP" searchdir="destination"
		constraints="rubriek.isvisible=='1'"
		><mm:node element="rubriek" jspvar="rubriek"
			><mm:first><table width="100%" border="0" cellpadding="0" cellspacing="0" class="body"></mm:first>
      <mm:related path="posrel,pagina" orderby="posrel.pos" directions="UP" max="1">
        <mm:node element="pagina">
        <tr>
          <td width="75" valign="top"><a href="index.jsp?r=<%= rubriek.getStringValue("number") %>"
              ><mm:relatednodes type="images" path="posrel,images" constraints="posrel.pos=='1'"
                  ><img src="<mm:image template="s(75)" />" border="0"></mm:relatednodes
              ></a></td>
          <td><img src="media/spacer.gif" width="15" height="1"></td>
          <td valign="top">
              <mm:field name="number" jspvar="pagina_number" vartype="String" write="false">
              <span class="pageheader">
                <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>"><%= rubriek.getStringValue("naam") %></a>
              </span><br/>
              </mm:field>
              <mm:field name="omschrijving" jspvar="pagina_omschrijving" vartype="String" write="false">
                <mm:isnotempty>
                  <%= HtmlCleaner.cleanText(pagina_omschrijving,"<",">") %>
                </mm:isnotempty>	
              </mm:field>
          </td>
        </tr>
        <tr>
          <td colspan="3"><img src="media/spacer.gif" width="1" height="17"></td>
        </tr>
        </mm:node>
			</mm:related>
			<mm:last></table></mm:last
		></mm:node>
	</mm:related>
</mm:node>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>