<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="bugreport" jspvar="bugreport" />
<mm:import externid="newpriority" />

	<mm:node id="bugnode" referid="bugreport">
		<mm:setfield name="bpriority"><mm:write referid="newpriority" /></mm:setfield>
	</mm:node>

	<%response.sendRedirect("../fullview.jsp?bugreport="+bugreport);%>
</mm:cloud>
