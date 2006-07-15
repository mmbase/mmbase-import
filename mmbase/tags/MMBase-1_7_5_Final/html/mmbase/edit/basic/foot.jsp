<hr />
<p class="foot">
<%=m.getString("foot.loggedas")%>: <%= cloud.getUser().getIdentifier() %>
(<%= cloud.getUser().getRank() %>).<br />
<%=m.getString("foot.coding")%>:   <%= cloud.getCloudContext().getDefaultCharacterEncoding() %>  <br />
<mm:write referid="config.lang" jspvar="lang" vartype="string">
<%=m.getString("foot.language")%>: <%= locale.getDisplayLanguage(locale) /*cloud.getLocale().getDisplayLanguage(cloud.getLocale())*/ %> <br />
</mm:write>
</p>
<%@include file="footfoot.jsp" %>
