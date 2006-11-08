<% String account = null; %>
<mm:cloud method="http" rank="anonymous" jspvar="cloud">
  <% account = cloud.getUser().getIdentifier(); %>
</mm:cloud>
<%
if("anonymous".equals(account)) { account = "website_user"; }
String password = (String) com.finalist.mmbase.util.CloudFactory.getUserCredentials(account).get("password");
%>
