<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:import externid="tree" from="parameters" vartype="list" />
<mm:import externid="mode" />
<mm:write session="tree_${mode}" referid="tree" />
<ok />
