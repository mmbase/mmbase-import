<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html>
<body>
<h1>context</h1>
<p>
Showing several ways to use the value of a context parameter. 
</p>
<mm:context id="new">
<mm:import id="hoi" externid="haj" required="true" jspvar="groet"/>
<mm:write referid="hoi" />, 
<mm:write referid="new.hoi" />,
<mm:write context="new" referid="hoi" />,
<mm:write referid="hoi" jspvar="greet"><%= greet %></mm:write>,
<%= groet %>
<br />
<a href='<mm:url page="context2\.jsp" referids="hoi">
           <mm:param name="hello">saluton</mm:param>
         </mm:url>'>next page</a>
</mm:context>
</body>
</html>