<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ page import="java.awt.*" %>

<mm:import externid="username" from="parameters" />
<mm:cloud method="http" logon="$username" jspvar="cloud">

<% String iartId = request.getParameter("iart") ;
   if (iartId==null) { iartId = ""; }
   String imageId = request.getParameter("image") ;
   if (imageId!=null) {
%>

<mm:transaction id="post_delete" name="my_trans" commitonclose="true">
	<mm:node number="<%= imageId %>">
		<mm:deletenode number="<%= imageId %>" deleterelations="true" />
	</mm:node>
</mm:transaction>
<mm:remove referid="post_delete" />

<jsp:forward page="image_gallery.jsp">
	<jsp:param name="iart" value="<%= iartId %>" />
</jsp:forward>

<% } else { %>
<p><b><font color="#CC0000">Error:</font></b><br>image_delete.jsp: no image is selected.</p>
<% } %>


</mm:cloud>
