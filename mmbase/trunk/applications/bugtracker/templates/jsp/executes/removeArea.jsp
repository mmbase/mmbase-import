<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="usernumber" />
<mm:import externid="deletearea" />

    <mm:deletenode referid="deletearea" deleterelations="true" />

    <%response.sendRedirect("../index.jsp");%>
</mm:cloud>
