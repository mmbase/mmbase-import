<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="usernumber" />
<mm:import externid="newcommitor" />

    <mm:node id="groupnode" number="BugTracker.Commitors" />
    <mm:node id="usernode" referid="newcommitor" />

    <mm:createrelation role="related" source="groupnode" destination="usernode" />

    <%response.sendRedirect("../index.jsp");%>
</mm:cloud>
