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

<mm:compare value="addstylesheetproperty" referid="action">
	<mm:import externid="tid" />
	<mm:import externid="cssid" />
	<mm:import externid="name" />
	<mm:import externid="value" />
        <mm:booleanfunction set="thememanager" name="addStyleSheetProperty" referids="cssid,tid,id,name,value" />
</mm:compare>


<mm:compare value="addstylesheetclass" referid="action">
	<mm:import externid="cssid" />
	<mm:import externid="name" />
        <mm:booleanfunction set="thememanager" name="addStyleSheetClass" referids="cssid,id@tid,name" />
</mm:compare>


<mm:compare value="copytheme" referid="action">
	<mm:import externid="copytheme" />
	<mm:import externid="newtheme" />
        <mm:booleanfunction set="thememanager" name="copyTheme" referids="copytheme,newtheme" />
</mm:compare>


<mm:compare value="removestylesheetclass" referid="action">
	<mm:import externid="cssid" />
	<mm:import externid="confirm" />
	<mm:import externid="name" />
	<mm:compare referid="confirm" value="Yes">
        <mm:booleanfunction set="thememanager" name="removeStyleSheetClass" referids="cssid,id@tid,name" />
	</mm:compare>
</mm:compare>


<mm:compare value="removestylesheetproperty" referid="action">
	<mm:import externid="tid" />
	<mm:import externid="cssid" />
	<mm:import externid="name" />
        <mm:booleanfunction set="thememanager" name="removeStyleSheetProperty" referids="cssid,tid,id,name" />
</mm:compare>

</mm:cloud>
