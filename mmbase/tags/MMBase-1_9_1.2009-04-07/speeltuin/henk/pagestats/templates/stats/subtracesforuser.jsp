<%@ page import="nl.ou.rdmc.stats.process.*" %>
<%@ page import="nl.ou.rdmc.stats.model.*" %>
<%@ page import="java.util.Iterator" %>
<%
ModelBuilder modelBuilder = (ModelBuilder)session.getValue("MODEL");
String name = request.getParameter("name");
%>
<h3>Subtraces for user <%=name%></h3>
<table cellspacing="0" border="1">
   <tr><td><b>subtrace type</b></td><td><b>start session</b></td><td><b>end session</b></td><td><b>length of session</b></td></tr>
   <%
   try {
        FUser user = modelBuilder.getUser(name);
        if (user!=null) {
      		Iterator it2=user.getSessionsIterator();
      		if (it2!=null) {
        		while (it2.hasNext()) {
          			FSession fsession = (FSession) it2.next();
          			Iterator it3 = fsession.getSubtracesIterator();
                              	if (it3!=null) {
			            while (it3.hasNext() ) {
			              FSubtrace fSubtrace = (FSubtrace) it3.next();
                                      	out.println("<tr><td><a href='traces.jsp?name=" + name + "&sid=" + fSubtrace.getId() + "&ttype="  + fSubtrace.getTestType() + "'>"  + fSubtrace.getTestType() + "</a></td>");
                                        out.println("<td>" + fSubtrace.getStart() + "</td>");
                                        out.println("<td>" + fSubtrace.getEnd() + "</td>");
                                        out.println("<td>" + fSubtrace.getLength() + "</td></tr>\n");
                                    }
          			}
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
