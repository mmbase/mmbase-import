<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:cloud method="http" rank="basic user" jspvar="cloud" username="admin">
  <%= cloud.getUser().getIdentifier() %> / <%= cloud.getUser().getRank() %>
</mm:cloud>
