<hr />
<p class="foot">
<%=m.getString("foot.loggedas")%>: <%= cloud.getUser().getIdentifier() %>
(<%= cloud.getUser().getRank() %>).<br />
<%=m.getString("foot.coding")%>:   <%= cloud.getCloudContext().getDefaultCharacterEncoding() %>  <br />
<mm:locale jspvar="locale" language="$config.lang" country="$config.country">
<%=m.getString("foot.language")%>: <%= locale.getDisplayLanguage(locale) /*cloud.getLocale().getDisplayLanguage(cloud.getLocale())*/ %> (<%=locale.getDisplayCountry(locale)%>)<br />
</mm:locale>
</p>
<%@include file="footfoot.jsp" %>
