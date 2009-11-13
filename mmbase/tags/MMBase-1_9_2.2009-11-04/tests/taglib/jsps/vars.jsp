<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*" %>
<html>
<title>Testing MMBase/taglib</title>
<body>
<h1>Testing MMBase/taglib</h1>
<h2>vars</h2>

<mm:import id="a">AAA</mm:import>

<mm:import id="list" vartype="list">1,2,3,4</mm:import>

<mm:context>
  <mm:import externid="a" from="parent" required="true"/>
</mm:context>

<mm:write referid="a" />, ${a}

<mm:context>
  <mm:stringlist referid="list">    
    <mm:first>
      <mm:import externid="a" from="parent" required="true"/>
    </mm:first>
  </mm:stringlist>
</mm:context>



</body>
</html>