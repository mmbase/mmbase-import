<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="bugreport" jspvar="bugreport" />
<mm:import externid="rationale" />

	<mm:node referid="bugreport" id="bugnode">
		<mm:setfield name="rationale"><mm:write referid="rationale" /></mm:setfield>
	</mm:node>

	<%response.sendRedirect("../fullview.jsp?bugreport="+bugreport);%>
</mm:cloud>
