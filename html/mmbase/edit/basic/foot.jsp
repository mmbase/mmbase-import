<hr />
Logged on as: <%= cloud.getUser().getIdentifier() %>
(<%= cloud.getUser().getRank() %>).<br />
coding:   <%= cloud.getCloudContext().getDefaultCharacterEncoding() %>  <br />
language: <%= java.util.Locale.getDefault().getLanguage() /*cloud.getLocale().getDisplayLanguage(cloud.getLocale())*/ %> <br />
<%@include file="footfoot.jsp" %>
