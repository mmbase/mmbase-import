<hr />
<%=m.getString("foot.loggedas")%>: <%= cloud.getUser().getIdentifier() %>
(<%= cloud.getUser().getRank() %>).<br />
<%=m.getString("foot.coding")%>:   <%= cloud.getCloudContext().getDefaultCharacterEncoding() %>  <br />
<mm:write referid="config.lang" jspvar="lang" vartype="string">
<%=m.getString("foot.language")%>: <%= locale.getDisplayLanguage(locale) /*cloud.getLocale().getDisplayLanguage(cloud.getLocale())*/ %> <br />
</mm:write>
<%@include file="footfoot.jsp" %>
