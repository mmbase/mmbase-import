<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="bugreport" jspvar="bugreport" />

    <mm:node id="bugnode" number="$bugreport">
	// lets remove all related info
	<mm:relatednodes type="mmevents">
		<mm:deletenode deleterelations="true" />
	</mm:relatednodes>
	<mm:relatednodes type="bugreportupdates">
		<mm:deletenode deleterelations="true" />
	</mm:relatednodes>
	<mm:deletenode deleterelations="true" />
    </mm:node>
</mm:cloud>
<%response.sendRedirect("../showMessage.jsp?message=reportdeleted");%>
