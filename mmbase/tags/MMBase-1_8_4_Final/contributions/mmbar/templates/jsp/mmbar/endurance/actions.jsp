<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />
<mm:compare value="performendurancetest" referid="action">
	<mm:import externid="name" />
	<mm:import id="result"><mm:function set="mmbar" name="performEnduranceTest" referids="name" /></mm:import>
</mm:compare>

</mm:cloud>
