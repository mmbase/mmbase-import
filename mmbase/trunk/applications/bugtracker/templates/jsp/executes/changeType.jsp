<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="bugreport" jspvar="bugreport" />
<mm:import externid="newtype" />

	<mm:node id="bugnode" referid="bugreport">
		<mm:setfield name="btype"><mm:write referid="newtype" /></mm:setfield>
	</mm:node>

	<%response.sendRedirect("../fullview.jsp?bugreport="+bugreport);%>
</mm:cloud>
