<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="bugreport" jspvar="bugreport" />
<mm:import externid="maintainerrel" />

    <mm:deletenode referid="maintainerrel" />

    <mm:node id="bugnode" number="$bugreport" />
    <%response.sendRedirect("../fullview.jsp?bugreport="+bugreport);%>
</mm:cloud>
