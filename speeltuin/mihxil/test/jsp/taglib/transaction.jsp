<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<body>
<h1>Testing taglib</h1>
<h2>cloud, transaction</h2>
<mm:import id="curtime"><%= System.currentTimeMillis()%></mm:import>
<mm:cloud method="http" jspvar="cloud">

<h3>Canceling transaction</h3>
<mm:transaction name="mytrans" commitonclose="false">
  <mm:createnode type="news">
    <mm:setfield name="title">Test node, created in transaction, canceled</mm:setfield>
    <mm:setfield name="subtitle"><mm:write referid="curtime" /></mm:setfield>
  </mm:createnode>
  <mm:cancel />
</mm:transaction>
Transaction was canceled, following should not result anything:
<mm:listnodes id="l" type="news" constraints="subtitle = '$curtime'">
  <mm:field name="gui()" />
</mm:listnodes>
<br />
<h3>Committing transaction</h3>
<mm:transaction name="mytranz" jspvar="trans">
  jspvar of type Transaction: <%= trans instanceof org.mmbase.bridge.Transaction %><br />
  <mm:createnode type="news">
    <mm:setfield name="title">Test node, created in transaction, commited</mm:setfield>
    <mm:setfield name="subtitle"><mm:write referid="curtime" /></mm:setfield>
  </mm:createnode>
</mm:transaction>
transaction was commited, following should result anything:
<mm:listnodes id="node" type="news" constraints="subtitle = '$curtime'" max="1">
  <mm:field name="subtitle">
    <mm:compare referid2="curtime">
        YES (created node was <mm:field id="nodenumber" name="number" />)
        <mm:write referid="nodenumber" session="testnodenumber" />         
        <mm:write referid="node" session="testnode" />         
    </mm:compare>
  </mm:field>
   <mm:field name="title" />
</mm:listnodes>
<br />
<h3>Creating relation in transaction</h3>
<mm:transaction name="mytranc">
  <mm:node id="node1" number="$nodenumber" />
  <mm:createnode id="node2" type="urls">
     <mm:setfield name="description">Test node2, created in transaction, made relation to it</mm:setfield>
	   <mm:setfield name="url">http://<mm:write referid="curtime" /></mm:setfield>
  </mm:createnode>
  <mm:createrelation source="node1" destination="node2" role="related" />
</mm:transaction>
<hr />
logged on as: <%= cloud.getUser().getIdentifier() %><br />
</mm:cloud>
You should see this.
<hr />
<a href="<mm:url page="node.jsp" />">node.jsp</a><br />
   <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
<hr />
</body>
</html>