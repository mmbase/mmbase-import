<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<title>Testing MMBase/taglib</title>
<body>
<h1>Testing MMBase/taglib</h1>
<h2>node</h2>
<mm:import id="node"       externid="testnode"       from="session" />
<mm:import id="nodenumber" externid="testnodenumber" from="session" />

<mm:notpresent referid="node">
  No testnode in session. Do first <a href="transaction.jsp">transaction.jsp</a>
</mm:notpresent>
<mm:present referid="node">
<mm:cloud method="anonymous" jspvar="cloud">
<h3>listing all fields, getting node by number (referid attribute): </h3>
<mm:node referid="nodenumber">
  <mm:fieldlist> 
    <mm:fieldinfo type="guiname" />: <mm:field /><br />
  </mm:fieldlist>
</mm:node>
<h3>getting node by number (number attribute): </h3>
<mm:node number="$nodenumber">
  <mm:fieldlist> 
    <mm:fieldinfo type="guiname" />: <mm:field /><mm:last inverse="true">, </mm:last>
  </mm:fieldlist>
</mm:node>
			 
<h3>listing certain fields, getting node by Node:</h3>
<mm:node referid="node">
  <mm:fieldlist type="edit"> 
    <mm:fieldinfo type="guiname" />: <mm:field /><br />
  </mm:fieldlist>
</mm:node>

<h3>editing the node from session (from non-anonymous) cloud</h3>
<mm:node referid="node">
  <mm:setfield name="subtitle"><mm:field name="subtitle" />edited</mm:setfield>
  subtitel: <mm:field name="subtitle" /><br />
</mm:node>

<%--
<h3>editing the node from session (from current anonymous) cloud</h3>
 <% try { %>
<mm:node referid="nodenumber">
  <mm:setfield name="subtitle"><mm:field name="subtitle" />edited</mm:setfield>
</mm:node>
 WRONG!! Should have thrown a securityexception!
<%} catch (org.mmbase.security.SecurityException e ) {} %>
<% { %>
 Ok, this throw an exception <br />
Btw, catching exceptions doesn't seem to work so nice in Orion. Test this page with Tomcat.
 
<% } %>
--%>
</mm:cloud>
</mm:present>
<hr />
<a href="<mm:url page="present.jsp"><mm:param name="a_param">a_param</mm:param></mm:url>">present.jsp</a><br />
<a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
</body>
</html>