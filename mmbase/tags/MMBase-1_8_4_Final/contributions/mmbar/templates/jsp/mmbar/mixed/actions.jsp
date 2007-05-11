<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />
<mm:compare value="performmixedtest" referid="action">
	<mm:import externid="name" />
	<mm:import id="result"><mm:function set="mmbar" name="performMixedTest" referids="name" /></mm:import>
</mm:compare>

</mm:cloud>
