<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*" %>
<html>
<title>Testing MMBase/taglib</title>
<body>
<h1>Testing MMBase/taglib</h1>
<h2>changed</h2>
<mm:import id="max">20</mm:import>
<mm:cloud>
<mm:listnodes type="object" max="$max">
  <mm:field name="owner" />
  <mm:changed>*</mm:changed>
  <mm:last inverse="true">, </mm:last>  
</mm:listnodes>
<hr />
<mm:listnodes type="object" max="$max" orderby="owner" directions="down">
  <mm:field name="owner" />
  <mm:changed>*</mm:changed>
  <mm:last inverse="true">, </mm:last>  
</mm:listnodes>
<hr />
<mm:listnodescontainer type="object">
  <mm:maxnumber value="$max" />
  <mm:sortorder field="owner" direction="down" />
  <mm:listnodes>
    <mm:field name="owner" />
    <mm:changed>*</mm:changed>
    <mm:last inverse="true">, </mm:last>  
  </mm:listnodes>
</mm:listnodescontainer>
<hr />
<mm:listcontainer path="news,object">
  <mm:maxnumber value="$max" />
  <mm:sortorder field="news.owner" direction="down" />
  <mm:list>
    <mm:field name="news.owner" />
    <mm:changed>*</mm:changed>
    <mm:last inverse="true">, </mm:last>  
  </mm:list>
</mm:listcontainer>
</mm:cloud>

</body>
</html>