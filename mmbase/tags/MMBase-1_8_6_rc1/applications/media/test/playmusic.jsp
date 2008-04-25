<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud>

<mm:import externid="mediafragment" required="true" />

<mm:node referid="mediafragment">
	<mm:field name="contenttype" />
	<mm:field name="contenttype" jspvar="contenttype" vartype="string">
		<% response.setContentType("audio/x-pn-realaudio"); %>
	</mm:field>
	<mm:field name="showurl" />
</mm:node>
</mm:cloud>
