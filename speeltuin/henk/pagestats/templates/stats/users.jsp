
<%@ page import="nl.ou.rdmc.stats.process.FileParser" %>
<%@ page import="nl.ou.rdmc.stats.process.ModelBuilder" %>
<%@ page import="nl.ou.rdmc.stats.model.*" %>
<%@ page import="java.util.Iterator" %>


<%
	ModelBuilder modelBuilder = (ModelBuilder)session.getValue("MODEL");
%>
	<table border="1">
        <tr><td colspan="4"><b>Number of user sessions</b></td></tr>
        <tr><td><b>user</b></td><td><b>first login</b></td><td><b>last login</b></td><td><b>number of sessions</b></td></tr>
<%
try {
        for (Iterator it=modelBuilder.getModelIterator();it.hasNext();) {
        	FUser user = (FUser)it.next();
                out.println("<tr><td><a href='sessions.jsp?name="+user.getName()+"'>"+user.getName()+"</a></td>");
                out.println("<td>"+user.getFirstSessionDate() + "</td>");
                out.println("<td>"+user.getLastSessionDate() + "</td>");
                out.println("<td>"+user.getSessionsNumber() + "</td></tr>");

    	}
} catch (Exception e) {
    e.printStackTrace();
}
%>
	</table>
