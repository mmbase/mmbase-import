<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<% String rnImageID = request.getParameter("rnimageid"); %>
<mm:cloud jspvar="cloud">
<% 
if(!rnImageID.equals("-1")) { 
   %><mm:node number="<%= rnImageID %>">background: url('<mm:image />') no-repeat bottom;padding-bottom:60px;</mm:node><%
} else { 
   %>padding-bottom:10px;<%
} %>
</mm:cloud>