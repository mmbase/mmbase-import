<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" pwd="admin2k">
        <mm:import externid="bugreport" jspvar="bugreport" />
        <mm:import externid="newtitle" />
        <mm:import externid="newtext" />
        <mm:import externid="newuser" />


	<mm:node referid="bugreport" id="bugreportnode" />

	<mm:node referid="newuser" id="usernode" />

	<mm:createnode id="commentnode" type="comments">
		<mm:setfield name="title"><mm:write referid="newtitle" /></mm:setfield>
		<mm:setfield name="body"><mm:write referid="newtext" /></mm:setfield>
	</mm:createnode>
	
   	<mm:createrelation role="rolerel" source="bugreportnode" destination="commentnode">
		<mm:setfield name="role">regular</mm:setfield>
	</mm:createrelation>

   	<mm:createrelation role="related" source="usernode" destination="commentnode" />

	<%response.sendRedirect("../fullview.jsp?flap=comments&bugreport="+bugreport);%>
</mm:cloud>
