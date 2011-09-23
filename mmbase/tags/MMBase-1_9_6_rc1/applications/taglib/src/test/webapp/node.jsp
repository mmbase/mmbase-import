<%@page language="java"
       pageEncoding="UTF-8" contentType="text/html;charset=utf-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
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
<mm:cloud username="admin" password="admin2k" jspvar="cloud">
<h3>listing all fields, getting node by number (referid attribute): </h3>
<em>see <a href="<mm:url page="${taglibdoc}/fieldlist.jsp" />">fieldlist</a></em><br />
<em>see <a href="<mm:url page="${taglibdoc}/fieldinfo.jsp" />">fieldinfo</a></em><br />
<mm:node referid="nodenumber">
  <mm:fieldlist>
    <mm:fieldinfo type="guiname" />: <mm:field /><br />
  </mm:fieldlist>
</mm:node>
(should see all field guinames with their values)
<h3>getting node by number (number attribute): </h3>
<em>see <a href="<mm:url page="${taglibdoc}/last.jsp" />">last</a></em><br />
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
  [<mm:nodeinfo type="query" jspvar="q"><%= q.getClass().getName() %> XXXX <mm:write /></mm:nodeinfo>]<br >
</mm:node>
(should see all editable fields)

<h3>listing specified  fields, getting node by Node (referid attribute). Showing value with fieldinfo.</h3>
<mm:node referid="node">
  <mm:fieldlist fields="title,subtitle">
    <mm:fieldinfo type="guiname" />: <mm:fieldinfo type="value" /><br />
  </mm:fieldlist>
</mm:node>
(should see title and subtitle fields)
<h3>field-function wrap, html</h3>
<mm:node referid="node">
  <%--
  <mm:field name="html(wrap(title, 10))" />
  <mm:field name="wrap(title, 10)" escape="p" />
  <pre><mm:field name="wrap(title,10)" /></pre>
  --%>
  <mm:field name="info()" escape="text/html" />
</mm:node>


<mm:log>testing edit node</mm:log>
<h3>editing the node from session (from non-anonymous) cloud</h3>
<em>see <a href="<mm:url page="${taglibdoc}/setfield.jsp" />">setfield</a></em><br />
<em>see <a href="<mm:url page="${taglibdoc}/createalias.jsp" />">createalias</a></em><br />
<em>see <a href="<mm:url page="${taglibdoc}/aliaslist.jsp" />">aliaslist</a></em><br />
<mm:node referid="node">
  <mm:setfield name="subtitle"><mm:field name="subtitle" />edited</mm:setfield>
   <!-- mm:createalias default.mags mm:createalias CANNOT catch  bridgeexception in jsp in orion!!! -->
  subtitle: <mm:field id="subtitle" name="subtitle" /><br />
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
   countrelations (not specified type): <mm:countrelations /> (should be 3)<br />
   countrelations (specified type): <mm:countrelations type="urls" /> (should be 2)<br />
   ountrelations (specified type, searchdir): <mm:countrelations type="urls" searchdir="destination" /> (should be 2)<br />
   countrelations (specified type, searchdir): <mm:countrelations type="urls" searchdir="source" /> (should be 0)<br />
   countrelations (specified searchdir): <mm:countrelations  searchdir="source" /> (should be 0)<br />
   countrelations (specified role): <mm:countrelations  role="posrel" /> (should be 1)<br />
   countrelations (specified role): <mm:countrelations  role="related" /> (should be 0)<br />
  gui of the relation node (with listrelations),
  should see gui of a relations (number -> number): <br />
  <mm:listrelations id="listrelations" type="urls">
     Relation: <mm:field name="gui()" /> /  Should see url (with relatednode):
     <mm:relatednode>
       <mm:field name="number" /> <mm:field name="url" />
     </mm:relatednode>
     <br />
  </mm:listrelations>
  <p>
    Reusing the listrelations (inside the node):
  </p>
  <mm:listrelations referid="listrelations">
     <mm:field name="gui()" />/<mm:relatednode><mm:field name="url" /></mm:relatednode>
     <br />
  </mm:listrelations>
</mm:node>
  <p>
    Reusing the listrelations (outside the node):
  </p>
  <mm:listrelations referid="listrelations">
     <mm:field name="gui()" />/<mm:relatednode><mm:field name="url" /></mm:relatednode>
     <br />
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
   <mm:node element="news">
     <em>node element="news"</em>:<br />
     title: <mm:field name="title" /><br />
     <em>should follow 9 times (numbered from 1 to 9) the related URL:</em><br />
     <mm:relatednodes type="urls">
       1  related url (used relatednodes): <mm:field id="url" name="url" /> (<mm:index />/<mm:size />)<br />
       <mm:last>(should be two ('sorted' and 'posrel'))<br /></mm:last>
  [<mm:nodeinfo type="query" />]<br >
     </mm:relatednodes>
     <mm:relatednodes type="urls" orderby="description">
       2  related url (used relatednodes): <mm:field name="url" /><br />
     </mm:relatednodes>
     <mm:relatednodes type="urls" role="posrel" searchdir="destination" orderby="posrel.pos">
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
     <mm:related path="related,urls" fields="urls.url">
         SHOULD NOT SEE THIS (role is not 'related' but 'posrel') <br />
     </mm:related>
     <mm:related path="posrel,urls" fields="urls.url">
       7  related url (used related, with role): <mm:field name="urls.url" /><br />
     </mm:related>
       <%-- CONTAINER TESTS --%>
     <mm:relatedcontainer path="posrel,urls">
       relatedcontainer: size  <mm:size /> (should be 1) <br />
       <mm:related>
          8  related url (used related): <mm:field name="urls.url" /><br />
          <mm:node element="urls">
            8.1  related url (used element): <mm:field name="url" /><br />
          </mm:node>
          <mm:node element="posrel">
            8.2  related url (used element): <mm:field name="pos" /><br />
          </mm:node>
       </mm:related>
     </mm:relatedcontainer>
     <% try  { %>
     <mm:relatedcontainer path="related,urls">
       relatedcontainer: size  <mm:size /> (should be 0) <br />
       <mm:related>
         SHOULD NOT SEE THIS (role is not 'related' but 'posrel') <br />
       </mm:related>
     </mm:relatedcontainer>
     <% } catch (Exception e) { %>
        OK, threw an exception with role is 'related' (news->urls does not exist)<br />
     <% } %>
     <mm:relatedcontainer path="urls2">
       <mm:constraint field="urls2.url" value="$url" /> <!-- testing also constraint -->
       relatedcontainer: size  <mm:size /> (should be 2) <br />
       <mm:related>
          9.<mm:index />  related url (used related): <mm:field name="urls2.url" /><br />
       </mm:related>
     </mm:relatedcontainer>
     <mm:relatedcontainer path="urls" searchdirs="source">
       relatedcontainer: size  <mm:size /> (should be 0) <br />
       <mm:related>
         SHOULD NOT SEE THIS (excplitiy asked for other direction)<br />
       </mm:related>
     </mm:relatedcontainer>
     <mm:relatedcontainer path="urls" searchdirs="destination">
       relatedcontainer: size  <mm:size /> (should be 2) <br />
       <mm:related>
          10.<mm:index /> related url (used related): <mm:field name="urls.url" /><br />
       </mm:related>
     </mm:relatedcontainer>
     <mm:relatedcontainer path="posrel,urls" searchdirs="destination">
       relatedcontainer: size  <mm:size /> (should be 1) <br />
       <mm:related>
          11  related url (used related): <mm:field name="urls.url" /><br />
       </mm:related>
     </mm:relatedcontainer>
     <mm:relatedcontainer path="posrel232,urls3" searchdirs="destination">
       <mm:sortorder field="posrel232.pos" />
       relatedcontainer: size  <mm:size /> (should be 1) <br />
       <mm:related>
          12  related url (testing postfixed digits): <mm:field name="urls3.url" /><br />
       </mm:related>
     </mm:relatedcontainer>
     <mm:relatednodescontainer path="posrel232,urls3" searchdirs="destination">
       <mm:sortorder field="posrel232.pos" />
       <mm:sortorder field="number" />
       relatedcontainer: size  <mm:size /> (should be 1) <br />
       <mm:relatednodes>
          13  related url (relatednodescontainer): <mm:field name="url" /><br />
       </mm:relatednodes>
     </mm:relatednodescontainer>
     <mm:relatednodescontainer path="posrel232,urls3" searchdirs="destination" element="posrel232">
       <mm:sortorder field="posrel232.pos" />
       relatedcontainer: size  <mm:size /> (should be 1) <br />
       <mm:relatednodes>
          14  position (relatednodescontainer): <mm:field name="pos" /> (should be 10)<br />
       </mm:relatednodes>
     </mm:relatednodescontainer>
     <mm:relatednodes type="object" role="posrel" orderby="posrel.pos" jspvar="rnode">
       <mm:log><%=rnode%></mm:log>
       15  actual type (listing object): <mm:nodeinfo type="nodemanager" /> (should be 'urls'): <mm:field name="url" /> (should see the url)<br />
     </mm:relatednodes>
     <em>Testing with 'sorted' role (a posrel)</em><br />
     <mm:relatednodescontainer type="urls" role="sorted">
       16 using 'sorted' role: size <mm:size /> (should be 1)<br />
       <mm:constraint field="sorted.pos" value="100" />
       17 using 'sorted' role (and constraint): size <mm:size /> (should be 1)<br />
     </mm:relatednodescontainer>
     <mm:relatednodescontainer path="sorted,urls" element="urls">
       18 using 'sorted' path: size <mm:size /> (should be 1)<br />
       <mm:constraint field="sorted.pos" value="100" />
       19 using 'sorted' path (and constraint): size <mm:size /> (should be 1)<br />
     </mm:relatednodescontainer>
     <mm:relatednodescontainer path="sorted,news" element="news">
       20 using 'sorted' path: size <mm:size /> (should be 1)<br />
       <mm:constraint field="sorted.pos" value="100" />
       21 using 'sorted' path (and constraint): size <mm:size /> (should be 1)<br />
       <mm:relatednodes>
         <mm:field name="title" /> (should be 'Another news article (...)')<br />
       </mm:relatednodes>
     </mm:relatednodescontainer>
     <mm:relatednodescontainer path="sorted,news" element="news">
       <mm:constraint field="sorted.pos" value="100" operator="IN" />
       22 using 'sorted' path (and constraint with operator="IN"): size <mm:size /> (should be 1)<br />
     </mm:relatednodescontainer>
     <mm:relatednodescontainer path="sorted,news" element="news">
       <mm:constraint field="sorted.pos" value="100,101,102" operator="IN" />
       23 using 'sorted' path (and constraint with operator="IN"): size <mm:size /> (should be 1)<br />
     </mm:relatednodescontainer>

     <mm:field name="number">
       <mm:list nodes="$_"  path="news,sorted,urls" fields="urls.url">
         24  related url (used list with nodes): <mm:field name="urls.url" /><br />
       </mm:list>
     </mm:field>

     <mm:relatednodes role="sorted" type="news" constraints="title LIKE '%Another%' and sorted.pos=100">
       25 using containts attribute: <mm:field name="title" /> (Another node..)  size <mm:size /> (should be 1)<br />
     </mm:relatednodes>

     <mm:relatednodes path="sorted,news" orderby="sorted.pos" element="news" searchdirs="destination">
       26 using path/element on mm:relatednodes <mm:size /> (should be 1)<br />
     </mm:relatednodes>

     Last number should be 26 (and no number should be missing).
     <hr />
   </mm:node>
</mm:list>


<em>Using constraint tags to refind the url</em><br />
<mm:listnodescontainer type="urls">
   <mm:constraint field="url" value="$url" />
   listnodescontainer: size <mm:size /> (should be 1) <br />
   <mm:listnodes>
    1 url: <mm:field name="url" /><br />
    <mm:field id="urlnumber" name="number" write="false" />
  [<mm:nodeinfo type="query" />]<br >
   </mm:listnodes>
</mm:listnodescontainer>


<mm:listnodescontainer path="urls,news" element="news">
   <mm:constraint field="urls.url" value="$url" />
   listnodescontainer: size <mm:size /> (should be 2) <br />
   <mm:listnodes>
    2.<mm:index /> news: <mm:field name="title" /><br />
   </mm:listnodes>
</mm:listnodescontainer>

<mm:listnodescontainer path="urls,news" element="news" nodes="$urlnumber">
   listnodescontainer with nodes=: size <mm:size /> (should be 2) <br />
   <mm:listnodes>
    3.<mm:index /> news: <mm:field name="title" /><br />
   </mm:listnodes>
</mm:listnodescontainer>

<mm:listnodescontainer path="news,urls" element="news" nodes="urls.$urlnumber">
   listnodescontainer with nodes=: size <mm:size /> (should be 2) <br />
   <mm:listnodes>
    4.<mm:index /> news: <mm:field name="title" /><br />
   </mm:listnodes>
</mm:listnodescontainer>

<mm:listnodescontainer path="news,object" element="news" nodes="$urlnumber">
   listnodescontainer with nodes=: size <mm:size /> (should be 2) <br />
   <mm:listnodes>
    5.<mm:index /> news: <mm:field name="title" /><br />
   </mm:listnodes>
</mm:listnodescontainer>

mm:listnodes with path/element (should show twice the article):<br />
<mm:listnodes path="news,urls" element="news">
  6 news: <mm:field name="title" /><br />
</mm:listnodes>

<mm:listcontainer path="news,object"  nodes="$urlnumber" searchdirs="destination">
   listcontainer with searchdirs=destination: size <mm:size /> (should be 2) <br />
   <mm:list>
    7.<mm:index /> news: <mm:field name="news.title" /><br />
   </mm:list>
</mm:listcontainer>

<mm:listcontainer path="news,object"  nodes="$urlnumber" searchdirs="source">
  8. listnodescontainer with searchdirs=source: size <mm:size /> (should be 0) <br />
   <mm:list>
      SHOULD NOT SEE 8.<mm:index /> news: <mm:field name="news.title" /><br />
   </mm:list>
</mm:listcontainer>

<mm:listcontainer path="news,object"   nodes="$urlnumber" searchdirs="both">
  9. listnodescontainer with searchdirs=both: size <mm:size /> (should be 2) <br />
   <mm:list>
       9.<mm:index /> news: <mm:field name="news.title" /><br />
   </mm:list>
</mm:listcontainer>

<mm:listcontainer path="news,object"  nodes="$urlnumber" searchdirs="all">
  10. listnodescontainer with searchdirs=all: size <mm:size /> (should be 2) <br />
   <mm:list>
       10.<mm:index /> news: <mm:field name="news.title" /><br />
   </mm:list>
</mm:listcontainer>

<br />

<mm:log>mm:list</mm:log>
<em>Using mm:distinct on mm:listcontainer</em><br />
<mm:listcontainer path="news,urls" fields="urls.number">
  <mm:constraint field="urls.url" value="$url" />
  <mm:distinct />
  listcontainer: size <mm:size /> (should be 1) <br />
  Should follow the url:<br />
  <mm:list nodes="-1" fields="urls.number,urls.url">
    <mm:index /> url: <mm:field name="urls.url" /><br />
  </mm:list>
</mm:listcontainer>


<mm:listcontainer path="news,urls" fields="urls.number,urls.url">
  <mm:constraint field="urls.url" value="$url" />
  <mm:distinct />
  listcontainer: size <mm:size /> (should be 1) <br />
  Should follow the url:<br />
  <mm:list>
    <mm:index /> url: <mm:field name="urls.url" /><br />
  </mm:list>
</mm:listcontainer>



<mm:log>distinct on mm:listnodescontainer</mm:log>
<em>Using mm:distinct on mm:listnodescontainer</em><br />

<mm:listnodescontainer path="news,urls" element="urls">
  <mm:constraint field="url" value="$url" />
  <mm:distinct />
  listnodescontainer: size <mm:size /> (should be 1) <br />
  Should follow the url: <br />
  <mm:listnodes>
    <mm:index /> url: <mm:field name="url" /><br />
  </mm:listnodes>
</mm:listnodescontainer>





<mm:log>mm:list with one element</mm:log>

<h3>List-tag with only one element</h3>
<mm:list nodes="$nodenumber" path="news" fields="news.title"  constraints="[news.number] > 10/10">
cd
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
