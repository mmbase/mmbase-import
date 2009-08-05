<%@ page import="nl.ou.rdmc.stats.process.*" %>
<%@ page import="nl.ou.rdmc.stats.model.*" %>
<%@ page import="java.util.Iterator" %>
<%
ModelBuilder modelBuilder = (ModelBuilder)session.getValue("MODEL");
String type = request.getParameter("type");
FSubtraceType subtracetype = (FSubtraceType)modelBuilder.getConfig().types.get(type);
%>
<h3>Subtraces for <%=type%></h3>
<table cellspacing="0" border="1">
   <tr><td><b>user</b></td><td><b>start session</b></td><td><b>end session</b></td><td><b>length of session</b></td></tr>
   <%
   try {
           if (subtracetype!=null) {
               Iterator it2=subtracetype.getSubtracesIterator();
               if (it2!=null) {
                   while (it2.hasNext()) {
                       FSubtrace fSubtrace = (FSubtrace) it2.next();
                       out.println("<tr><td><a href='subtracesforuser.jsp?name=" + fSubtrace.getUser().getName() + "'>" + fSubtrace.getUser().getName() + "</a></td><td>" + fSubtrace.getStart() + "</td><td>" + fSubtrace.getEnd() +
                               "</td><td>" + fSubtrace.getLength() +"</td></tr>");
                         }
               }
   
           } else {
                 out.println("<tr><td colspan='4'>No subtracetype with this name</td></tr>");
           }
   } catch (Exception e) {
       e.printStackTrace();
   }
   %>
</table>
