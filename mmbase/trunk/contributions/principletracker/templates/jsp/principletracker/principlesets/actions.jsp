<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud rank="basic user">
<mm:import externid="action" />
<mm:compare value="updateprincipleset" referid="action">
	<mm:import externid="principleset" />
	<mm:import externid="newname" />
	<mm:import externid="newdescription" />
	<mm:import externid="newalias" />
	<mm:node referid="principleset">
		<mm:setfield name="name"><mm:write referid="newname" /></mm:setfield>
		<mm:setfield name="description"><mm:write referid="newdescription" /></mm:setfield>
		<mm:import id="oldalias"><mm:aliaslist><mm:write /></mm:aliaslist></mm:import>
		<mm:compare referid="newalias" value="">
			<mm:compare referid="oldalias" value="" inverse="true">
				<mm:deletealias name="$oldalias" />
			</mm:compare>
		</mm:compare>
		<mm:compare referid="newalias" value="" inverse="true">
			<mm:compare referid="oldalias" value="" inverse="true">
				<mm:deletealias name="$oldalias" />
				<mm:createalias><mm:write referid="newalias" /></mm:createalias>
			</mm:compare>
			<mm:compare referid="oldalias" value="">
				<mm:createalias><mm:write referid="newalias" /></mm:createalias>
			</mm:compare>
		</mm:compare>
	</mm:node>
</mm:compare>

<mm:compare value="createprincipleset" referid="action">
	<mm:import externid="newname" />
	<mm:import externid="newdescription" />
	<mm:import externid="newalias" />
	<mm:createnode id="newnode" type="principlesets">
		<mm:setfield name="name"><mm:write referid="newname" /></mm:setfield>
		<mm:setfield name="description"><mm:write referid="newdescription" /></mm:setfield>
	</mm:createnode>
	<mm:compare referid="newalias" value="" inverse="true">
	<mm:node referid="newnode">
		<mm:createalias><mm:write referid="newalias" /></mm:createalias>
	</mm:node>
	</mm:compare>
</mm:compare>


<mm:compare value="deleteprincipleset" referid="action">
	<mm:import externid="principleset" />
	<mm:import externid="deleteconfirm" />
	<mm:compare referid="deleteconfirm" value="yes">
	    *** delete principleset ***
	    <mm:node referid="principleset">
		<mm:relatednodes type="principle">
			<mm:deletenode deleterelations="true" />
		</mm:relatednodes>
		<mm:deletenode />
	    </mm:node>
	</mm:compare>
</mm:compare>

</mm:cloud>
