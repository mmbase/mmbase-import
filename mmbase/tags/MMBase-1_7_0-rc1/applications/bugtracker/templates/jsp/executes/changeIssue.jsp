<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="bugreport" jspvar="bugreport" />
<mm:import externid="newissue" />

	<mm:node id="bugnode" referid="bugreport">
		<mm:setfield name="issue"><mm:write referid="newissue" /></mm:setfield>
	</mm:node>

	<%response.sendRedirect("../fullview.jsp?bugreport="+bugreport);%>
</mm:cloud>
