<%@ page import="nl.ou.rdmc.stats.process.*" %>
<%@ page import="nl.ou.rdmc.stats.model.*" %>
<%@ page import="java.util.Iterator" %>
<%
ModelBuilder modelBuilder = (ModelBuilder)session.getValue("MODEL");
     String name = request.getParameter("name");
%>
<h3>Length of user sessions for user <%=name%></h3>
<table cellspacing="0" border="1">
   <tr><td><b>user</b></td><td><b>start session</b></td><td><b>end session</b></td><td><b>length of session</b></td></tr>
   <%
   try {
           FUser user = modelBuilder.getUser(name);
           if (user!=null) {
         		Iterator it2=user.getSessionsIterator();
         		if (it2!=null) {
           		while (it2.hasNext()) {
             				FSession fsession = (FSession) it2.next();
                                           out.println("<tr><td><a href='traces.jsp?name=" + name + "&sid=" + fsession.getId() + "'>"  + name + "</a></td>");
             				out.println("<td>" + fsession.getStart() + "</td>");
                                   	out.println("<td>" + fsession.getEnd() +"</td>");
                                   	out.println("<td>" + fsession.getLength() +"</td></tr>");
                         	}
         		}
   
       	} else {
                 out.println("<tr><td colspan='4'>No user with this name</td></tr>");
       	}
   } catch (Exception e) {
       e.printStackTrace();
   }
   %>
</table>
