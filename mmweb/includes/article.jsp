<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr>
  <td width="540" valign="top">	
	<h2><!-- <mm:field name="number" /> --><mm:field name="title"/></h2>
	<mm:field name="subtitle"><mm:isnotempty><h3><mm:write /></h3></mm:isnotempty></mm:field>
  </td>
  <td>&nbsp;</td>
</tr><tr>
  <td width="540" valign="top">	
	<mm:field name="intro"><mm:isnotempty>
		<p class="intro"><mm:write /><br><br></p>
	</mm:isnotempty></mm:field>
	<mm:field name="body"><mm:isnotempty>
		<mm:remove referid="news"/><mm:import id="news"/>
		<p><%@ include file="/includes/urls.jsp" %>
		<mm:write /></p>
	</mm:isnotempty></mm:field>
  </td>
  <td valign="top">
	<mm:related path="posrel,images" fields="posrel.pos" orderby="posrel.pos">
		<mm:first><table border="0" cellspacing="4" cellpadding="0"></mm:first>
		<tr><td align="center" valign="top">
		<mm:node element="images">
		<a href="#" onClick="javascript:launchCenter('/includes/slideshow.jsp?image=<mm:field name="number" />', 'center', 550, 740)" title="Click to enlarge image"><img src="<mm:image template="s(120)" />" alt="<mm:field name="alt" />" width="120" border="0" /></a>
		<mm:field name="title"><mm:isnotempty><br /><span class="imgtitle"><mm:write /> </span></mm:isnotempty></mm:field>
		</mm:node>
		</td></tr>
		<mm:last></table></mm:last>
	</mm:related>
	<%-- @include file="/includes/images.jsp" --%>
	<%@include file="/includes/attachment.jsp" %>
	<%@include file="/includes/readmorepages.jsp" %>
  </td>
</tr>
</table>
