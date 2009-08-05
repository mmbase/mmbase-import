<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page errorPage="error.jsp" session="false" 
 %><mm:content type="text/html" postprocessor="reducespace,google">
<html>
<head>
  <title>Functions</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>

<%@ include file="menu.jsp"%>

<h1>Function tags</h1>
<p>
  Since MMBase 1.7 there are also 'function' tags available in the MMBase taglib.  Those tags
  provide access to the 'Function Framework' of mmbase (see API doc of
  org.mmbase.util.functions), and the <a href="<mm:url page="$taglibdoc/reference/functiontag.jsp" />">taglib reference documention</a>
  This page demonstrates by example the possibilities
</p>
<mm:cloud jspvar="cloud">
<table>
<tr><td colspan="2">
  The mm:function tag with only 'name' specified, executes the function on the current node.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/functionnode.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/functionnode.jsp" %></td>
</tr>
<tr><td colspan="2"> 
There are more types of functions, than only those on a Node. A simple example
can be based on a method in the current jsp. This is based on a keyword 'THISPAGE' for the
'set' attribute. Other examples would be based on 'set' ('set' means 'collection' and not
the opposite of 'get') attribute, but there are as yet no generally availabe such set-functions...
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/functionmethod.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/functionmethod.jsp" %></td>
</tr>

<tr><td colspan="2">
  Functions can have other return values. For example it can be a List. This is an example based on
  'THISPAGE', but the same goes for any type of function. There is also a 'nodelistfunction' for
  functions returing a list of Nodes.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/listfunction.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/listfunction.jsp" %></td>
</tr>

<tr><td colspan="2">
 Function with boolean return value work like 'condition' tags.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/booleanfunction.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/booleanfunction.jsp" %></td>
</tr>

<tr><td colspan="2">
  Void-functions are only for the side-effect. If you use it on a function with no side effect, you'll end up with another silly, but perhaps instructive example.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/voidfunction.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/voidfunction.jsp" %></td>
</tr>

<tr><td colspan="2">
Functions, of course, only get their real value if you use them with
parameters. For that the function-container can be used. That works on any kind of function. We
demonstrate it here based on 'BeanFunction' based on an inner bean of the JSP.

</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/functionbean.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/functionbean.jsp" %></td>
</tr>

<tr><td colspan="2">
  Defining inner-classes in your JSP may be well suited for hackery, but generally the functionality may be implemented in a decent java class.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/functionbean2.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/functionbean2.jsp" %></td>
</tr>

<tr><td colspan="2">
 The 'referids' feature of mm:urls is also supported.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/functionreferids.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/functionreferids.jsp" %></td>
</tr>

<tr><td colspan="2">
  A function an a node-manager. (To the 'news' builder of MyNews the ExampleBuilder for functions was assigned).
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/functionnodemanager.jsp" /></mm:formatter></pre></td>
  <td width="50%">
    <% try { %>
    <%@include file="codesamples/functionnodemanager.jsp" %> 
    <% } catch (Exception e) {out.println(e.getMessage());} %>
  </td>
</tr>


<tr><td colspan="2">
  Another function on a node, this time with a list argument. View ExampleBuilder for implementation.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/functionnode2.jsp" /></mm:formatter></pre></td>
  <td width="50%">
    <% try { %>
    <%@include file="codesamples/functionnode2.jsp" %> 
    <% } catch (Exception e) {out.println(e.getMessage());} %>
  </td>  
</tr>


<tr><td colspan="2">
  You can ask wich functions are available.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/functionnodeavailable.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/functionnodeavailable.jsp" %></td>
</tr>


<tr><td colspan="2">
  How to create parameters of more complicated types.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/functioncomplexparams.jsp" /></mm:formatter></pre></td>
  <td width="50%">
    <% try { %>
    <%@include file="codesamples/functioncomplexparams.jsp" %> 
    <% } catch (Exception e) {out.println(e.getMessage());} %>
  </td>  
</tr>

<tr><td colspan="2">
  More about function 'sets'
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/functionsets.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/functionsets.jsp" %></td>
</tr>


</table>
</mm:cloud>
</body>
</html>
</mm:content>