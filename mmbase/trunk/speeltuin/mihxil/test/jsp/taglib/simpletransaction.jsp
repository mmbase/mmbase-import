<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<body>
<h1>Testing taglib</h1>
<h2>cloud, transaction</h2>
<mm:import id="curtime"><%= System.currentTimeMillis()%></mm:import>
<mm:cloud method="http">

<mm:transaction name="mytranz" jspvar="trans" commitonclose="false">
  jspvar of type Transaction: <%= trans instanceof org.mmbase.bridge.Transaction %><br />
  <mm:createnode type="news" />
  <mm:cancel />
</mm:transaction>
</mm:cloud>
<hr />
<a href="<mm:url page="node.jsp" />">node.jsp</a>
<hr />
</body>
</html>