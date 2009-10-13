<%response.setHeader("Content-Type", "text/vnd.rn-realtext");
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><window bgcolor="blue" link="gray" width="360" height="100">
<mm:cloud>
<mm:import externid="fragment" required="true" />
<mm:node number="$fragment">
<font color="white" size="2"><b><mm:field name="title" /></b><br>
<mm:field name="subtitle" /><br>
<mm:field name="wrap_30(intro)" />
</font>
</mm:node>
</mm:cloud>
</window>
