<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:cloud logon="wwwuser" pwd="buggie90">
<mm:import externid="bugreport" jspvar="bugreport" />
<mm:import externid="interestedrel" />

    <mm:deletenode referid="interestedrel" />

    <mm:node id="bugnode" number="$bugreport" />

    <%response.sendRedirect("/bugtracker/jsp/fullview.jsp?bugreport="+bugreport);%>
</mm:cloud>
