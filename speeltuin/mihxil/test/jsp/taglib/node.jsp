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
(should see all field guinames with their values)
<h3>getting node by number (number attribute): </h3>
<mm:node number="$nodenumber">
  <mm:fieldlist> 
    <mm:fieldinfo type="name" /><mm:last inverse="true">, </mm:last>
    <mm:last><br /></mm:last>
  </mm:fieldlist>
</mm:node>
(should see all field names seperated by comma's (showing mm:last inverse))
<h3>listing certain fields, getting node by Node (referid attribute):</h3>
<mm:node referid="node">
  <mm:fieldlist type="edit"> 
    <mm:fieldinfo type="guiname" />: <mm:field /><br />
  </mm:fieldlist>
</mm:node>
(should see all editable fields)

<h3>listing specified  fields, getting node by Node (referid attribute). Showing value with fieldinfo.</h3>
<mm:node referid="node">
  <mm:fieldlist fields="title,subtitle"> 
    <mm:fieldinfo type="guiname" />: <mm:fieldinfo type="value" /><br />
  </mm:fieldlist>
</mm:node>
(should see title and subtitle fields)
<h3>relations</h3>
<mm:node referid="node">
  countrelations: <mm:countrelations /> (should be 1)<br />
  1 url: <mm:relatednodes type="urls">
     <mm:field name="url" />
  </mm:relatednodes>
</mm:node>

<h3>editing the node from session (from non-anonymous) cloud</h3>
<mm:node referid="node">
  <mm:setfield name="subtitle"><mm:field name="subtitle" />edited</mm:setfield>
   <!-- mm:createalias>default.mags</mm:createalias CANNOT catch bridgeexception in jsp in orion!!!--> 
  subtitle: <mm:field name="subtitle" /><br />
  <mm:createalias><mm:field name="subtitle" /></mm:createalias>
</mm:node>
<mm:node referid="node">
  subtitle: <mm:field name="subtitle" /><br />
  aliases:<mm:aliaslist>
   <mm:write /><mm:last inverse="true">, </mm:last>
  </mm:aliaslist>
</mm:node>
(should see twice the changed subtitle, one of the aliases must be equal to the subtitle)

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