<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<title>Testing MMBase/taglib</title>
<body>
<h1>Testing MMBase/taglib</h1>
<h2>node</h2>
<mm:import id="node"       externid="testnode"       from="session" />
<mm:import id="nodenumber" externid="testnodenumber" from="session" />

<mm:import id="taglibdoc">/mmdocs/taglib</mm:import>

<mm:log>testing fieldlist</mm:log>
<mm:notpresent referid="node">
  No testnode in session. Do first <a href="transaction.jsp">transaction.jsp</a>
</mm:notpresent>
<mm:present referid="node">
<mm:cloud method="anonymous" jspvar="cloud">
<h3>listing all fields, getting node by number (referid attribute): </h3>
<em>see <a href="<mm:url page="${taglibdoc}/fieldlist.jsp" />">fieldlist</a></em><br />
<em>see <a href="<mm:url page="${taglibdoc}/fieldinfo.jsp" />">fieldinfo</a></em><br />
<mm:node referid="nodenumber">
  <mm:fieldlist type="create"> 
    <mm:fieldinfo type="guiname" />: <mm:field /><br />
  </mm:fieldlist>
</mm:node>
(should see all field guinames with their values)
<h3>getting node by number (number attribute): </h3>
<em>see <a href="<mm:url page="${taglibdoc}/last.jsp" />">last</a></em><br />
<mm:node number="$nodenumber">
  <mm:fieldlist type="create"> 
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


<mm:log>testing edit node</mm:log>
<h3>editing the node from session (from non-anonymous) cloud</h3>
<em>see <a href="<mm:url page="${taglibdoc}/setfield.jsp" />">setfield</a></em><br />
<em>see <a href="<mm:url page="${taglibdoc}/createalias.jsp" />">createalias</a></em><br />
<em>see <a href="<mm:url page="${taglibdoc}/aliaslist.jsp" />">aliaslist</a></em><br />
<mm:node referid="node">
  <mm:setfield name="subtitle"><mm:field name="subtitle" />edited</mm:setfield>
   <!-- mm:createalias default.mags mm:createalias CANNOT catch  bridgeexception in jsp in orion!!! --> 
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
<p>
Should see a node-number (trice): <mm:write value="$node" /> <mm:write referid="node" vartype="string" /> <mm:url page="$node" />
</p>

<%--
<h3>editing the node from session (from current anonymous) cloud</h3>
<em>see <a href="<mm:url page="${taglibdoc}/setfield.jsp" />">setfield</a></em><br />
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
<h3>Testing 'notfound' attribute</h3>
<em>see <a href="<mm:url page="${taglibdoc}/node.jsp#node.notfound" />">notfound attribute</a></em><br />
not using it:<br />
<% try { %>
<mm:node number="this_alias_does_not_exist_really" />
  WRONG!! Should have thrown exception (node not found)
<% } catch (Exception e) { %>
   Ok, this threw an exception <br /> 
<% } %>
notfound="skip"<br />
<% try { %>
<mm:node notfound="skip" number="this_alias_does_not_exist_really">
  WRONG!! You should not see this!, it went in the body of non-existing node!<br />
</mm:node>
  Ok, this didn't threw Exception.
<% } catch (Exception e) { %>
   WRONG!! Threw exception even though 'skip' was specified.<br /> 
<% } %>

<mm:log>testing relations lists</mm:log>
<h3>Relations, countrelations, listrelations, relatednode</h3>
<em>see <a href="<mm:url page="${taglibdoc}/countrelations.jsp" />">countrelations</a></em><br />
<em>see <a href="<mm:url page="${taglibdoc}/listrelations.jsp" />">listrelations</a></em><br />
<em>see <a href="<mm:url page="${taglibdoc}/relatednode.jsp" />">relatednode</a></em><br />
<mm:node referid="node">
   countrelations (not specified type): <mm:countrelations /> (should be 1)<br />
   countrelations (specified type): <mm:countrelations type="urls" /> (should be 1)<br />
   ountrelations (specified type, searchdir): <mm:countrelations type="urls" searchdir="destination" /> (should be 1)<br />
   countrelations (specified type, searchdir): <mm:countrelations type="urls" searchdir="source" /> (should be 0)<br />
   countrelations (specified searchdir): <mm:countrelations  searchdir="source" /> (should be 0)<br />
   countrelations (specified role): <mm:countrelations  role="posrel" /> (should be 1)<br />
   countrelations (specified role): <mm:countrelations  role="related" /> (should be 0)<br />
  gui of the relation node (with listrelations),
  should see gui of a relations (number -> number): 
  <mm:listrelations id="listrelations">
     <mm:field name="gui()" /><br />
     Should see url (with relatednode):
     <mm:relatednode>
          <mm:field name="url" />
      </mm:relatednode>
  </mm:listrelations>  
  <p>
    Reusing the listrelations (inside the node):
  </p>
  <mm:listrelations referid="listrelations">
     <mm:field name="gui()" />/<mm:relatednode><mm:field name="url" /></mm:relatednode>
  </mm:listrelations>
</mm:node>
  <p>
    Reusing the listrelations (outside the node):
  </p>
  <mm:listrelations referid="listrelations">
     <mm:field name="gui()" />/<mm:relatednode><mm:field name="url" /></mm:relatednode>
  </mm:listrelations>

<mm:log>element/related nodes</mm:log>
<h3>Testing 'element' attribute and list tags</h3>
<em>see <a href="<mm:url page="${taglibdoc}/node.jsp#node.element" />">element attribute</a></em><br />
<em>see <a href="<mm:url page="${taglibdoc}/list.jsp" />">list</a></em><br />
<em>see <a href="<mm:url page="${taglibdoc}/relatednodes.jsp" />">relatednodes</a></em><br />
<em>see <a href="<mm:url page="${taglibdoc}/related.jsp" />">related</a></em><br />
using list tag: <br />
<mm:list nodes="$nodenumber" path="news,urls" fields="news.title" >
   <em>all the following should have values</em>:<br />
   news.title:    <mm:field name="news.title" />   <br />
   news.subtitle: <mm:field name="news.subtitle" /><br />
   urls.url:      <mm:field name="urls.url" />     <br />
    <mm:log>1</mm:log>
   <mm:node element="news">
     <em>node element="news"</em>:<br />
    <mm:log>2</mm:log>
     title: <mm:field name="title" /><br />
     <em>should follow 6 times (numbered from 1 to 6) the related URL:</em><br />
    <mm:log>3</mm:log>
     <mm:relatednodes type="urls">
       1  related url (used relatednodes): <mm:field name="url" /><br />
     </mm:relatednodes>
     <mm:relatednodes type="urls" orderby="description">
       2  related url (used relatednodes): <mm:field name="url" /><br />
     </mm:relatednodes>
     <mm:relatednodes type="urls" role="posrel" searchdir="destination">
       3  related url (used relatednodes): <mm:field name="url" /><br />
     </mm:relatednodes>  
     <mm:relatednodes type="urls" searchdir="destination">
       4  related url (used relatednodes): <mm:field name="url" /><br />
     </mm:relatednodes>  
     <mm:relatednodes type="urls" searchdir="source">
         SHOULD NOT SEE THIS  (searchdir without role not honoured)<br />
     </mm:relatednodes>  
     <mm:relatednodes type="urls" role="related">
         SHOULD NOT SEE THIS (role is not 'related' but 'posrel') <br />
     </mm:relatednodes>  
     <mm:relatednodes type="urls" role="posrel" searchdir="source">
         SHOULD NOT SEE THIS (excplitiy asked for other direction)<br />
     </mm:relatednodes>  
     <mm:relatednodes type="urls" orderby="description" constraints="">
       5  related url (used relatednodes): <mm:field name="url" /><br />
     </mm:relatednodes>
     <mm:related path="urls" fields="urls.url">
       6  related url (used related): <mm:field name="urls.url" /><br />
     </mm:related>  

   </mm:node>
</mm:list>


<h3>List-tag with only one element</h3>
<mm:list nodes="$nodenumber" path="news" fields="news.title" jspvar="node" >

   <em>all the following should have values</em>:<br />
   news.title:    <mm:field name="news.title" />   <br />
   news.subtitle: <mm:field name="news.subtitle" /><br />

   <em>this not</em>:<br />
   news.title:    <mm:field name="title" />   <br />
   news.subtitle: <mm:field name="subtitle" /><br />
</mm:list>

</mm:cloud>
</mm:present>
<hr />
<a href="<mm:url page="present.jsp"><mm:param name="a_param">a_param</mm:param></mm:url>">present.jsp</a><br />
<a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
</body>
</html>