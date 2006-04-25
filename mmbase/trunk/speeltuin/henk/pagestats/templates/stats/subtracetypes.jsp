<%@ page import="nl.ou.rdmc.stats.process.*" %>
<%@ page import="nl.ou.rdmc.stats.model.*" %>
<%@ page import="java.util.Iterator" %>
<%
ModelBuilder modelBuilder = (ModelBuilder)session.getValue("MODEL");
Config fconf = modelBuilder.getConfig();
%>
<h3>Number of subtraces found</h3>
<table cellspacing="0" border="1">
   <tr><td colspan="3"><b></b></td></tr>
   <tr><td><b>subtrace type</b></td><td><b>number of sessions</b></td><td><b>average length of session</b></td></tr>
   <%
    for (Iterator it=fconf.types.keySet().iterator();it.hasNext();) {
          FSubtraceType type = (FSubtraceType)fconf.types.get(it.next());
          out.print("<tr><td><a href=\"subtracesfortype.jsp?type="+type.toString()+"\">"+type.toString()+"</a></td><td>"+type.getSubtraceNumber()+"</td><td>"+type.getSubtraceAverageLength()+"</td></tr>");
    }
   %>
</table>
