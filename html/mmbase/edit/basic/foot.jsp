<hr />
<p class="foot">
<%=m.getString("foot.loggedas")%>: <mm:cloudinfo type="user" /> (<mm:cloudinfo type="rank" />, <mm:cloudinfo type="authenticate" />).<br />
<mm:locale jspvar="locale" language="$config.lang" country="$config.country">
<%=m.getString("foot.language")%>: <%= locale.getDisplayLanguage(locale) /*cloud.getLocale().getDisplayLanguage(cloud.getLocale())*/ %> (<%=locale.getDisplayCountry(locale)%>)<br />
</mm:locale>
</p>
<%@include file="footfoot.jsp" %>
