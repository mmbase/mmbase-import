<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />
<mm:import externid="id" />

<mm:compare value="setstylesheetproperty" referid="action">
	<mm:import externid="tid" />
	<mm:import externid="cssid" />
	<mm:import externid="name" />
	<mm:import externid="value" />
        <mm:booleanfunction set="thememanager" name="setStyleSheetProperty" referids="cssid,tid,id,name,value" />
</mm:compare>

</mm:cloud>
