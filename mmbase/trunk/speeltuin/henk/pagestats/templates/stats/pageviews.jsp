
<%@ page import="nl.ou.rdmc.stats.process.*" %>
<%@ page import="nl.ou.rdmc.stats.model.*" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="nl.ou.rdmc.stats.process.Config" %>

<%
	ModelBuilder modelBuilder = (ModelBuilder)session.getValue("MODEL");
	Config fconf = modelBuilder.getConfig();
%>
	<table border="1">
        <tr><td colspan="10"><b>Pageviews</b></td></tr>
        <tr><td><b>nav</b></td><td><b>subnav</b></td><td><b>test</b></td><td><b>page</b></td><td><b>schooltype</b></td><td><b>competence</b></td><td><b>coretask</b></td><td><b>testlevel</b></td><td><b>testtype</b></td><td><b>pageviews</b></td></tr>

<%
try {

      		Iterator it2=modelBuilder.getPagesIterator();
      		if (it2!=null) {
        		while (it2.hasNext()) {
          			FPage fpage = (FPage) it2.next();
                                out.println("<tr>");
                                for (Iterator it3=fconf.getLogTagsIterator();it3.hasNext();) {
                                  String logtag = (String)it3.next();
                                  out.println("<td>" + fpage.getValueFor(logtag) + "</td>");
                                }
                               out.println("<td>" + fpage.getViewsNumber() + "</td></tr>");
                      }
      		} else {
              		out.println("<tr><td colspan='10'>No pages there</td></tr>");
    		}
} catch (Exception e) {
    e.printStackTrace();
}
%>
	</table>
