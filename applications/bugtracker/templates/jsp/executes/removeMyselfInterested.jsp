<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="bugreport" jspvar="bugreport" />
<mm:import externid="user" />

    <mm:list nodes="$bugreport" path="bugreports,rolerel,users" constraints="users.number=$user and rolerel.role='interested'">
	<mm:node element="rolerel">
		<mm:deletenode />
	</mm:node>
    </mm:list>
</mm:cloud>
<%response.sendRedirect("../fullview.jsp?bugreport="+bugreport);%>
