<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="usernumber" />
<mm:import externid="newarea" />

    <mm:compare referid="newarea" value="" inverse="true">
    <mm:node id="poolnode" number="BugTracker.Start" />
    <mm:createnode id="areanode" type="areas">
	<mm:setfield name="name"><mm:write referid="newarea" /></mm:setfield>
    </mm:createnode>

    <mm:createrelation role="related" source="poolnode" destination="areanode" />
    </mm:compare>

    <%response.sendRedirect("../index.jsp");%>
</mm:cloud>
