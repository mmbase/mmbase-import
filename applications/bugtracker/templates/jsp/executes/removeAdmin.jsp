<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="deleteadmin">none</mm:import>


    <mm:node number="BugTracker.Admins">
	<mm:related path="insrel,users" constraints="users.number=$deleteadmin">
		<mm:node element="insrel">
    			<mm:deletenode />
		</mm:node>
	</mm:related>
    </mm:node>
    <%response.sendRedirect("../index.jsp");%>
</mm:cloud>
