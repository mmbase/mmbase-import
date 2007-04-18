<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />
<mm:import externid="id" />

<mm:compare value="changeassign" referid="action">
	<mm:import externid="newtheme" />
	<mm:booleanfunction set="thememanager" name="changeAssign" referids="id,newtheme" />
</mm:compare>

<mm:compare value="addassign" referid="action">
	<mm:import externid="newid" />
	<mm:import externid="newtheme" />
	<mm:booleanfunction set="thememanager" name="addAssign" referids="newid,newtheme" />
</mm:compare>

<mm:compare value="removeassign" referid="action">
	<mm:import externid="removeid" />
	<mm:booleanfunction set="thememanager" name="removeAssign" referids="removeid" />
</mm:compare>

</mm:cloud>
