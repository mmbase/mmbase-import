<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="wwwuser" pwd="buggie90">
<mm:import externid="bugreport" jspvar="bugreport" />
<mm:import externid="user" />

	<mm:node id="bugnode" number="$bugreport" />
	<mm:node id="usernode" number="$user" />

    <mm:createrelation role="rolerel" source="bugnode" destination="usernode">
		<mm:setfield name="role">interested</mm:setfield>
    </mm:createrelation>

	<%response.sendRedirect("/bugtracker/jsp/fullview.jsp?bugreport="+bugreport);%>
</mm:cloud>
