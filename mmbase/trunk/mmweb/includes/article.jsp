<!-- START FILE: /includes/article.jsp -->
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr>
  <td width="540" valign="top"> 
    <h2><mm:field name="title"/></h2>
    <mm:field name="subtitle"><mm:isnotempty><h3><mm:write /></h3></mm:isnotempty></mm:field>
  </td>
  <td>&nbsp;</td>
</tr><tr>
  <td width="540" valign="top"> 
    <mm:content escaper="links">
      <mm:field name="intro"><mm:isnotempty><p class="intro"><mm:write /></p></mm:isnotempty></mm:field>
    </mm:content>
    <mm:remove referid="news"/><mm:import id="news" /><%@ include file="/includes/urls.jsp" %>
    <mm:content escaper="links">
      <mm:field name="body"><mm:isnotempty><div><mm:write /></div></mm:isnotempty></mm:field>
    </mm:content>
  </td>
  <td valign="top">
    <mm:related path="posrel,images" fields="posrel.pos" orderby="posrel.pos">
        <mm:first><table border="0" cellspacing="4" cellpadding="0"></mm:first>
        <tr><td align="center" valign="top">
        <mm:node element="images">
        <a href="#" onClick="javascript:launchCenter('<mm:url page="includes/slideshow.jsp">
          <mm:param name="image"><mm:field name="number" /></mm:param></mm:url>', 'center', 550, 740)" title="Click to enlarge image"><img src="<mm:image template="s(120)" />" alt="<mm:field name="title" />" width="120" border="0" /></a>
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
<!-- END FILE: /includes/article.jsp -->
