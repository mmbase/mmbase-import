<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />
<mm:import externid="id" />
<mm:import externid="version" />
<mm:import externid="provider" />

<mm:compare value="changesettings" referid="action">
	<mm:import externid="providername" />
	<mm:import externid="callbackurl" />
         <mm:booleanfunction set="mmpm" name="changeSettings" referids="providername,callbackurl">
        </mm:booleanfunction>
</mm:compare>

</mm:cloud>
