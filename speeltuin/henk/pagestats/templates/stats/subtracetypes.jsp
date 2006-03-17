
<%@ page import="nl.ou.rdmc.stats.process.FileParser" %>
<%@ page import="nl.ou.rdmc.stats.process.ModelBuilder" %>
<%@ page import="nl.ou.rdmc.stats.process.Config" %>
<%@ page import="nl.ou.rdmc.stats.model.*" %>
<%@ page import="java.util.Iterator" %>

<%
    ModelBuilder modelBuilder = (ModelBuilder)session.getValue("MODEL");
        Config fconf = modelBuilder.getConfig();
%>
    <table border="1">
        <tr><td colspan="3"><b>Number of subtraces found</b></td></tr>
        <tr><td><b>subtrace type</b></td><td><b>number of sessions</b></td><td><b>average length of session</b></td></tr>
<%
    for (Iterator it=fconf.types.keySet().iterator();it.hasNext();) {
          FSubtraceType type = (FSubtraceType)fconf.types.get(it.next());
          out.print("<tr><td><a href=\"subtracesfortype.jsp?type="+type.toString()+"\">"+type.toString()+"</a></td><td>"+type.getSubtraceNumber()+"</td><td>"+type.getSubtraceAverageLength()+"</td></tr>");
    }
%>
    </table>
