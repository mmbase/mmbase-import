<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:cloud logon="wwwuser" pwd="buggie90">
<mm:import externid="updater" />
<mm:import externid="bugreport" />
<mm:import externid="newissue" />
<mm:import externid="newbtype" />
<mm:import externid="newbstatus" />
<mm:import externid="newversion" />
<mm:import externid="newefixedin" />
<mm:import externid="newfixedin" />
<mm:import externid="newbpriority" />
<mm:import externid="newdescription" />
<mm:import externid="newrationale" />
<mm:import externid="newarea" />
<mm:import externid="oldarea" />
<mm:import externid="oldarearel" />
<% int now=(int)(System.currentTimeMillis()/1000); %>

	<mm:node id="usernode" number="$updater" />

	<!-- get all the vars for the history copy -->
	<mm:node id="bugreportnode" number="$bugreport">
		<mm:import id="oldissue"><mm:field name="issue" /></mm:import>
		<mm:import id="oldbpriority"><mm:field name="bpriority" /></mm:import>
		<mm:import id="oldbtype"><mm:field name="btype" /></mm:import>
		<mm:import id="oldversion"><mm:field name="version" /></mm:import>
		<mm:import id="oldefixedin"><mm:field name="efixedin" /></mm:import>
		<mm:import id="oldfixedin"><mm:field name="fixedin" /></mm:import>
		<mm:import id="oldbstatus"><mm:field name="bstatus" /></mm:import>
		<mm:import id="olddescription"><mm:field name="description" /></mm:import>
		<mm:import id="oldrationale"><mm:field name="rationale" /></mm:import>
		<mm:import id="oldtime"><mm:field name="time" /></mm:import>
	</mm:node>

	<mm:createnode id="updatenode" type="bugreportupdates">
		<mm:setfield name="issue"><mm:write referid="oldissue" /></mm:setfield>
		<mm:setfield name="bstatus"><mm:write referid="oldbstatus" /></mm:setfield>
		<mm:setfield name="btype"><mm:write referid="oldbtype" /></mm:setfield>
		<mm:setfield name="version"><mm:write referid="oldversion" /></mm:setfield>
		<mm:setfield name="efixedin"><mm:write referid="oldefixedin" /></mm:setfield>
		<mm:setfield name="fixedin"><mm:write referid="oldfixedin" /></mm:setfield>
		<mm:setfield name="bpriority"><mm:write referid="oldbpriority" /></mm:setfield>
		<mm:setfield name="description"><mm:write referid="olddescription" /></mm:setfield>
		<mm:setfield name="rationale"><mm:write referid="oldrationale" /></mm:setfield>
		<mm:setfield name="time"><mm:write referid="oldtime" /></mm:setfield>
	</mm:createnode>

	<mm:node number="$bugreport">
		<mm:related path="rolerel,users" constraints="rolerel.role='submitter'" max="1">
			<mm:import id="submitter"><mm:field name="users.number" /></mm:import>
			<mm:import id="submitterrel"><mm:field name="rolerel.number" /></mm:import>
		</mm:related>
	</mm:node>
	<mm:node number="$bugreport">
		<mm:related path="rolerel,users" constraints="rolerel.role='updater'" max="1">
			<mm:import id="oldupdater"><mm:field name="users.number" /></mm:import>
			<mm:import id="oldupdaterrel"><mm:field name="rolerel.number" /></mm:import>
		</mm:related>
	</mm:node>

    	<mm:createrelation role="related" source="bugreportnode" destination="updatenode" />

	<mm:node number="$bugreport">
		<mm:setfield name="issue"><mm:write referid="newissue" /></mm:setfield>
		<mm:setfield name="bstatus"><mm:write referid="newbstatus" /></mm:setfield>
		<mm:setfield name="btype"><mm:write referid="newbtype" /></mm:setfield>
		<mm:setfield name="version"><mm:write referid="newversion" /></mm:setfield>
		<mm:setfield name="efixedin"><mm:write referid="newefixedin" /></mm:setfield>
		<mm:setfield name="fixedin"><mm:write referid="newfixedin" /></mm:setfield>
		<mm:setfield name="bpriority"><mm:write referid="newbpriority" /></mm:setfield>
		<mm:setfield name="description"><mm:write referid="newdescription" /></mm:setfield>
		<mm:setfield name="rationale"><mm:write referid="newrationale" /></mm:setfield>
		<mm:setfield name="time"><%=now%></mm:setfield>
	</mm:node>

    <mm:present referid="oldupdater" inverse="true">
    <mm:present referid="submitter">
	<mm:node id="oldsubmitternode" number="$submitter" />
    	<mm:createrelation role="rolerel" source="updatenode" destination="oldsubmitternode">
		<mm:setfield name="role">submitter</mm:setfield>
    	</mm:createrelation>
    </mm:present>
    </mm:present>

    <mm:present referid="oldupdater">
	<mm:node id="oldupdaternode" number="$oldupdater" />
    	<mm:createrelation role="rolerel" source="updatenode" destination="oldupdaternode">
		<mm:setfield name="role">updater</mm:setfield>
    	</mm:createrelation>
	<mm:deletenode number="$oldupdaterrel" />
    </mm:present>

    <mm:createrelation role="rolerel" source="bugreportnode" destination="usernode">
		<mm:setfield name="role">updater</mm:setfield>
    </mm:createrelation>

    <!-- replace the link to a different area node if needed -->
    <mm:compare referid="oldarea" referid2="newarea" inverse="true">
	<mm:deletenode referid="oldarearel" />
	<mm:node id="newareanode" referid="newarea" />
    	<mm:createrelation role="related" source="bugreportnode" destination="newareanode" />
    </mm:compare>

</mm:cloud>
<%response.sendRedirect("/bugtracker/jsp/showMessage.jsp?message=newbug");%>
