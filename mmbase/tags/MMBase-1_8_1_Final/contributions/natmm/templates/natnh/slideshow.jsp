<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<% String previousImage = "-1";
   String nextImage = "-1";
   String thisImage = "";
   String otherImages = "";
%><%@include file="includes/splitimagelist.jsp" 
%><% String pageHref = "slideshow.jsp?r=" + rubriekId + "&p=" + paginaID + "&i="; 
%><mm:list nodes="<%= thisImage %>" path="images" 
><html>
<head>
<title><mm:node number="<%= rootId %>"><mm:field name="naam" /></mm:node
	> - <mm:node number="<%= rubriekId %>"><mm:field name="naam" /></mm:node
	> -	<mm:node number="<%= paginaID %>"><mm:field name="titel" /></mm:node>
</title>
<link rel="stylesheet" type="text/css" href="css/website.css">
<meta http-equiv="imagetoolbar" content="no">
</head>
<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td class="fotopopuptitel"><img src="media/spacer.gif" width="10" height="10"></td>
</tr>
<tr>
	<td><table width="600" border="0" align="center" cellpadding="0" cellspacing="0">
		<tr>
			<td colspan="2"><a href="#" onClick="window.close()" title="Klik op de foto om het venster te sluiten">
				<img src="<mm:node element="images"><mm:image template="s(600)" /></mm:node>" width="600" border="0" class="fotopopup"></a></td>
		</tr>
		<tr>
			<td colspan="2"><img src="media/spacer.gif" width="10" height="10" border="0"></td>
		</tr>
		<tr>
			<td colspan="2" class="small">
			<mm:field name="images.titel_zichtbaar"
			   ><mm:compare value="0" inverse="true"
			      ><mm:field name="images.titel" jspvar="images_titel" vartype="String" write="false"
					><% if(!images_titel.equals("")) {
						%><span class="fotopopupparkop"><%= images_titel %></span><br><%
					} %></mm:field
			   ></mm:compare
			></mm:field
			><mm:field name="images.description" /></td>
		</tr>
		<tr>
			  <td colspan="2">&nbsp;</td>
		</tr>
		<% if(!previousImage.equals("-1")&&!nextImage.equals("-1")) { 
			%><tr>
				<td class="small"><div align="left"><a href="<%= pageHref %><%= previousImage %>">[&nbsp;<<- vorige&nbsp;]</div></td>
				<td class="small"><div align="right"><a href="<%= pageHref %><%= nextImage %>">[&nbsp;volgende ->>&nbsp;]</div></td>
			</tr>
			<tr>
			 <td colspan="2">&nbsp;</td>
			</tr><%
		} %>		
	</table></td>
</tr>
</table>
</body>
</html>
</mm:list>
</cache:cache>
</mm:cloud>