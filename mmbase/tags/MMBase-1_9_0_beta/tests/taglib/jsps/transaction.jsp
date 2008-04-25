<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<body>
<h1>Testing taglib</h1>
<h2>cloud, transaction</h2>
<mm:import id="curtime"><%= System.currentTimeMillis()%></mm:import>
<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud">

This number must increase on reload: <mm:write referid="curtime" />
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
<mm:listnodes id="node" type="news" constraints="subtitle = '$curtime'" max="1" jspvar="node">
  <mm:log><%=node %></mm:log>
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
<h3>Creating relations in transaction</h3>
<mm:transaction name="mytranc">
  <mm:node id="node1" number="$nodenumber" />
  Creating an URL node
  <mm:createnode id="node2" type="urls" jspvar="node2">
     <mm:setfield name="description">Test node2, created in transaction, made relation to it</mm:setfield>
	   <mm:setfield name="url">http://<mm:write referid="curtime" /></mm:setfield>
     (using jspvar <%= node2.getStringValue("url") %>)
     <br />
  </mm:createnode>
  Creating news-node ---posrel(pos=10)---> URL-node
  <mm:createrelation source="node1" destination="node2" role="posrel" jspvar="relation">
   <mm:setfield name="pos">10</mm:setfield>
   (using jspvar: <%= relation.getStringValue("pos") %>)
   <br />
  </mm:createrelation>
  Creating news-node ---sorted(pos=100)---> URL-node<br />
  <mm:createrelation source="node1" destination="node2" role="sorted">
   <mm:setfield name="pos">100</mm:setfield>
  </mm:createrelation>
  Creating another news-node.<br />
  <mm:createnode id="node3" type="news">
     <mm:setfield name="subtitle">Test node3, created in transaction, made relation to it</mm:setfield>
	   <mm:setfield name="title">Another news article (<mm:write referid="curtime" />)</mm:setfield>
  </mm:createnode>
  Creating news-node ---sorted(pos=100)---> other news-node<br />
  <mm:createrelation source="node1" destination="node3" role="sorted">
   <mm:setfield name="pos">100</mm:setfield>
  </mm:createrelation>
  
</mm:transaction>
<hr />
logged on as: <%= cloud.getUser().getIdentifier() %><br />
</mm:cloud>
You should see this.
<hr />
We have a small cloud starting with an news-node, to which an url is related on 2 ways (sorted and posrel). Also another news node is related to it. The next page will use these nodes in further taglib tests: <a href="<mm:url page="node.jsp" />">node.jsp</a><br />
   <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
<hr />
</body>
</html>