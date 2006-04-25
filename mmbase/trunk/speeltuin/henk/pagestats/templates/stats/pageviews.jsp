<%@page import="nl.ou.rdmc.stats.process.*" %>
<%@page import="nl.ou.rdmc.stats.model.*" %>
<%@page import="java.util.Iterator" %>
<h3>Pageviews</h3>
<%
ModelBuilder modelBuilder = (ModelBuilder)session.getValue("MODEL");
Config conf = modelBuilder.getConfig();
boolean isFirst = true;
for (Iterator logTagIterator=conf.getLogTagsIterator();logTagIterator.hasNext();) {
   String logtag = (String)logTagIterator.next();
   if(isFirst) {
      %>
      <table cellspacing="0" border="1">
      <tr>
      <%
      isFirst = false;
   }
   %>
   <td><b><%= logtag %></b></td>
   <%
}
if(!isFirst) {
   isFirst = true;
   %>
   <td><b>Pageviews</b></td></tr>
   <%
   try {

		Iterator pageIterator=modelBuilder.getPagesIterator();
		if (pageIterator!=null) {
     		while (pageIterator.hasNext()) {
            FPage fpage = (FPage) pageIterator.next();
            out.println("<tr>");
            for (Iterator logTagIterator=conf.getLogTagsIterator();logTagIterator.hasNext();) {
               String logtag = (String)logTagIterator.next();
               %><td><%= fpage.getValueFor(logtag) %></td><%
            }
            %><td><%= fpage.getViewsNumber() %></td></tr><%
            isFirst = false;
         }
	   }
   } catch (Exception e) {
      e.printStackTrace();
   }
   %>
   </table>
   <% 
   if(isFirst) {
      %>No pages have been found in the log files.<br/><%
   }
   
} else {
   %>No tags have been defined in the pagestats.xml configuration file.<br/><%
}
%>