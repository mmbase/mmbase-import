<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="bugreport" jspvar="bugreport" />
<mm:import externid="submitter" />

	<mm:node id="bugnode" number="$bugreport" />
	<mm:node id="usernode" number="$submitter" />

    <mm:createrelation role="rolerel" source="bugnode" destination="usernode">
		<mm:setfield name="role">submitter</mm:setfield>
    </mm:createrelation>


	<%response.sendRedirect("../fullview.jsp?bugreport="+bugreport);%>
</mm:cloud>
