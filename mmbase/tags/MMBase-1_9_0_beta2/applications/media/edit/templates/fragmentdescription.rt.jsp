<%@page session="false" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/vnd.rn-realtext" encoding="">
<window bgcolor="blue" link="gray" width="360" height="90">
  <mm:cloud method="asis">
    <mm:import externid="fragment" required="true" />
    <mm:node number="$fragment">
<font color="white" size="2">
<b><mm:field name="title" /></b><br>
<mm:field name="subtitle" /><br>
<mm:field name="wrap_30(intro)" />
<mm:relatednodes type="videofragments" role="posrel" searchdir="destination" orderby="posrel.pos">
<mm:first>sub-items:</mm:first> <mm:index />: <mm:field name="title" /> <mm:last inverse="true">, </mm:last>          
</mm:relatednodes>
</font>
</mm:node>
</mm:cloud>
</window>
</mm:content>