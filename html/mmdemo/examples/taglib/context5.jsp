<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html>
<body>
<h1>context 5</h1>
Url node looks now:<br />
<mm:cloud method="asis"><br />
<mm:context type="session">
<mm:transaction id="trans" commitonclose="false">
logged on as: <%= trans.getUser().getIdentifier() %> <br />
<mm:node id="trans_node">
  <mm:field name="url" /> <br />
</mm:node>
But we cancel transaction. So, if you go back to the previous page,
you'll see the old value of the node again.
<mm:cancel />
</mm:transaction>
</mm:context>
<hr />
<a href="context4.jsp">previous page</a>, <a href="context.jsp">back</a>
</mm:cloud>
</body>
</html>