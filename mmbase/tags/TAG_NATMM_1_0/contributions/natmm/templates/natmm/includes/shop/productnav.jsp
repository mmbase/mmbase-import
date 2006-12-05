<%@include file="/taglibs.jsp" %>
<%@include file="../../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
  <table cellspacing="0" cellpadding="0" width="100%">
    <tr><td class="internalnav"><bean:message bundle="LEOCMS" key="productnav.item_overview" /></td></tr>
    <tr><td style="padding:4px;padding-bottom:14px;">
      <mm:present referid="body"><a href="#body" class="subtitle"><mm:write referid="body" /></a><br></mm:present>
      <mm:present referid="thumbs"><a href="#thumbs" class="subtitle"><mm:write referid="thumbs" /></a><br></mm:present>
      <mm:list nodes="<%= shop_itemId %>" path="products,posrel,artikel" orderby="posrel.pos" directions="UP">
        <a href="#<mm:field name="articles.number" />" class="subtitle"><mm:field name="articles.titel_fra" /><br>
      </mm:list>
    </td></tr>
    <tr><td class="titlebar"><img src="media/spacer.gif" height="1" width="1" alt="" border="0" alt=""></td></tr>
  </table>
</mm:cloud>
