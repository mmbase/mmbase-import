<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="submitter" />
<mm:import externid="newissue" />
<mm:import externid="newbtype" />
<mm:import externid="newversion" />
<mm:import externid="newbpriority" />
<mm:import externid="newdescription" />
<mm:import externid="newarea" />
	<% int now=(int)(System.currentTimeMillis()/1000); %>
	<mm:node id="usernode" number="$submitter" />
	<mm:node id="poolnode" number="Bugtracker.Start" />
	
	<mm:listnodes type="bugreports" orderby="bugid" directions="down"  max="1">
		<mm:import id="oldid"><mm:field name="bugid" /></mm:import>
	</mm:listnodes>
	<% int newid=1; %>
	<mm:present referid="oldid">
		<mm:import id="tmpid" jspvar="tmpid"><mm:write referid="oldid" /></mm:import>
		<% try {
			newid=Integer.parseInt(tmpid)+1;
		   } catch(Exception e) {}
		%>
	</mm:present>

	<mm:createnode id="bugreportnode" type="bugreports">
		<mm:setfield name="issue"><mm:write referid="newissue" /></mm:setfield>
		<mm:setfield name="bugid"><%=newid%></mm:setfield>
		<mm:setfield name="bstatus">1</mm:setfield>
		<mm:setfield name="btype"><mm:write referid="newbtype" /></mm:setfield>
		<mm:setfield name="version"><mm:write referid="newversion" /></mm:setfield>
		<mm:setfield name="bpriority"><mm:write referid="newbpriority" /></mm:setfield>
		<mm:setfield name="description"><mm:write referid="newdescription" /></mm:setfield>
		<mm:setfield name="rationale"></mm:setfield>
		<mm:setfield name="time"><%=now%></mm:setfield>
	</mm:createnode>


    	<mm:createrelation role="related" source="bugreportnode" destination="poolnode" />


    	<mm:createrelation role="rolerel" source="bugreportnode" destination="usernode">
		<mm:setfield name="role">submitter</mm:setfield>
    	</mm:createrelation>
	
	<mm:node id="areanode" number="$newarea" />

    	<mm:createrelation role="related" source="bugreportnode" destination="areanode" />

</mm:cloud>
<%response.sendRedirect("../showMessage.jsp?message=newbug");%>
