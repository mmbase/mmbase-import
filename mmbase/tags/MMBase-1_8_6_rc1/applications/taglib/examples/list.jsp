<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page errorPage="error.jsp" %><mm:content expires="0" type="text/html">
<mm:cloud>

<mm:import externid="nodes" from="parameters">default.mags</mm:import>
<mm:import externid="path" vartype="list" from="parameters">mags,news</mm:import>
<mm:import externid="fields" vartype="list" from="parameters"/>
<mm:import externid="constraints" from="parameters"/>
<mm:import externid="orderby" from="parameters"/>
<mm:import externid="directions" from="parameters"/>
<mm:import externid="distinct" from="parameters"/>
<mm:import externid="max" from="parameters"/>
<mm:import externid="offset" from="parameters"/>
<mm:import externid="searchdir" from="parameters"/>

<html>

<head>
  <title>Taglib examples</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Using the list tag</h1>
<p>
  You can also query a 'path' of results from MMBase. This goes with the mm:list tag. This tag does not provide real nodes but so-called 'cluster nodes', which 
  are actually combination of more than one node. This means the a simple mm:relatendodes cannot be used inside an mm:list. You would need <a href="<mm:url page="$taglibdoc/reference/node.jsp#element" />">the element attribute of mm:node<a> then.
</p>

<form method="POST">
&lt;mm:list 
  <table>
    <tr>
      <td>nodes</td>
      <td><input type="text" size="60" name="nodes" value="<mm:write referid="nodes"/>"></td>
      <td>distinct</td>
      <td><input type="text" size="5" name="distinct" value="<mm:write referid="distinct"/>"></td>
    </tr>
    <tr>
      <td>path</td>
      <td><input type="text" size="60" name="path" value="<mm:write referid="path"/>"></td>
      <td>max</td>
      <td><input type="text" size="5" name="max" value="<mm:write referid="max"/>"></td>
    </tr>
    <tr>
      <td>fields</td>
      <td><input type="text" size="60" name="fields" value="<mm:write referid="fields"/>"></td>
      <td>offset</td>
      <td><input type="text" size="5" name="offset" value="<mm:write referid="offset"/>"></td>
    </tr>
    <tr>
      <td>constraints</td>
      <td><input type="text" size="60" name="constraints" value="<mm:write referid="constraints"/>"></td>
      <td>searchdir</td>
      <td><input type="text" size="5" name="searchdir" value="<mm:write referid="searchdir"/>"></td>
    </tr>
    <tr>
      <td>orderby</td>
      <td><input type="text" size="60" name="orderby" value="<mm:write referid="orderby"/>"></td>
      <td colspan="3">&nbsp;</td>
    </tr>
    <tr>
      <td>directions</td>
      <td><input type="text" size="60" name="directions" value="<mm:write referid="directions"/>"></td>
      <td colspan="3" align="center"><input type="submit" name="try" value="try" /></td>
    </tr>
 </table>

</form>


<mm:import externid="try" />
<mm:present referid="try">
  <table>
    <mm:list nodes="$nodes" path="$path" fields="$fields"
             constraints="$constraints" orderby="$orderby"
             directions="$directions" distinct="$distinct"
             max="$max" offset="$offset" searchdir="$searchdir">
         <%@include file="showclusternodes.trs.jsp" %>
      </tr>
    </mm:list>
  </table>
</mm:present>
<hr />
<a target="_new" href="<mm:url page="showanypage.jsp"><mm:param
name="page"><%=request.getServletPath()%></mm:param></mm:url>">source
of this page</a>.

</body>

</html>

</mm:cloud>
</mm:content>