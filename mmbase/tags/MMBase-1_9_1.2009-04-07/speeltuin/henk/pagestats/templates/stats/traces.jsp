<%@ page import="nl.ou.rdmc.stats.process.*" %>
<%@ page import="nl.ou.rdmc.stats.model.*" %>
<%@ page import="java.util.*" %>
<%
try {
	ModelBuilder modelBuilder = (ModelBuilder)session.getValue("MODEL");
   String name = request.getParameter("name");
   String sid = request.getParameter("sid");
   String ttype = request.getParameter("ttype");
   FEvent fevent = null;
   boolean isSubtrace = (ttype!=null);
   if (isSubtrace) {
   FSubtraceType fType = (FSubtraceType)modelBuilder.getConfig().types.get(ttype);
   if (fType!=null)
   	fevent = (FEvent)fType.getSubtrace(new Long(Long.parseLong(sid)));
   } else {
    FUser user = modelBuilder.getUser(name);
    if (user!=null)
    	fevent = (FEvent)user.getSession(new Long(Long.parseLong(sid)));
   }
   
   if (fevent!=null) {
      %>
      <h3>Trace of user <%=name%> for session <%=fevent.getStart()%> - <%=fevent.getEnd()%></h3>
	   <table cellspacing="0" border="1">
         <tr><td>
         <%
		   Map pages = fevent.getPages();
   		Iterator it2=pages.keySet().iterator();
   		if (it2!=null) {
        		while (it2.hasNext()) {
                 Date pdate = (Date)it2.next();
                 FPage fpage = (FPage)pages.get(pdate);
                 out.println(fpage.toStringWith(name, FEvent.dateToString(pdate))+"<br>");
             }
         }
         %>
         </td></tr>
	   </table>
      <%
	}
} catch (Exception e) {
    e.printStackTrace();
}
%>