<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html>
<body>
<h1>context 4</h1>
Putting a transaction in the context.
<mm:cloud method="http"><br />
<mm:context type="session">
logged on as: <%= cloud.getUser().getIdentifier() %> <br />
<mm:remove key="trans" />
<mm:transaction name="testje10" id="trans" commitonclose="false">
logged on as: <%= trans.getUser().getIdentifier() %> <br />
<mm:remove key="trans_node" />
<mm:list type="urls" max="1">
<mm:node id="trans_node">
  looks now:<mm:field name="url" /> but will be set to 'abc'. On the
  next page you will see 'abc'.
  <mm:setfield name="url">abc</mm:setfield>
</mm:node>
</mm:list>
</mm:transaction>
<hr />
<a href="context5.jsp">next page</a>
</mm:context>
</mm:cloud>
</body>
</html>