<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content expires="0" type="text/html">

<mm:cloud>

<mm:import externid="number">default.mags</mm:import>
<mm:import externid="nodes"     />
<mm:import externid="path" vartype="list">news,urls</mm:import>
<mm:import externid="fields" vartype="list">news.title,urls.url</mm:import>
<mm:import externid="constraints" />
<mm:import externid="orderby" />
<mm:import externid="directions" />
<mm:import externid="distinct" />
<mm:import externid="max" />
<mm:import externid="offset" />
<mm:import externid="searchdir" />

<html>

<head>
  <title>Taglib examples</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Using the related tag</h1>
<p>
  The related tag is very much like the <a href="list.jsp">list</a> tag. The only difference being that it takes the first element of the path and the first element of 'nodes' from the 
  surrounding node, listnodes  or relatednodes (or other provider of 'real' nodes).
</p>

<table cellpadding="5">
  <tr>
    <td valign="top">

<form method="POST">

<table>

<tr><td colspan="6">&lt;node number=&quot;<input type="text" size="20" name="number" value="<mm:write referid="number"/>">&quot;&gt;</td></tr>
<tr><td></td><td>&lt;related</td><td>nodes=</td><td>&quot;</td><td><input type="text" size="20" name="nodes" value="<mm:write referid="nodes"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>path=</td><td>&quot;</td><td><input type="text" size="20" name="path" value="<mm:write referid="path"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>fields=</td><td>&quot;</td><td><input type="text" size="20" name="fields" value="<mm:write referid="fields"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>constraints=</td><td>&quot;</td><td><input type="text" size="20" name="constraints" value="<mm:write referid="constraints"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>orderby=</td><td>&quot;</td><td><input type="text" size="20" name="orderby" value="<mm:write referid="orderby"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>directions=</td><td>&quot;</td><td><input type="text" size="20" name="directions" value="<mm:write referid="directions"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>distinct=</td><td>&quot;</td><td><input type="text" size="20" name="distinct" value="<mm:write referid="distinct"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>max=</td><td>&quot;</td><td><input type="text" size="20" name="max" value="<mm:write referid="max"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>offset=</td><td>&quot;</td><td><input type="text" size="20" name="offset" value="<mm:write referid="offset"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>searchdir=</td><td>&quot;</td><td><input type="text" size="20" name="searchdir" value="<mm:write referid="searchdir"/>"></td><td>&quot;&gt;</td></tr>
<tr><td></td><td colspan="5">&lt;/related&gt;</td></tr>
<tr><td colspan="6">&lt;/node&gt;</td></tr>
<tr><td colspan="6" align="right"><input type="submit" name="try" value="try"/></td></tr>

</table>

</form>

    </td>
    <td valign="top">

<mm:import externid="try" />
<mm:present referid="try">
  <table style="width: auto;" >
    <tr><th colspan="100">Search Result</th></tr>
    <mm:node number="$number">

      <mm:related nodes="$nodes" path="$path" fields="$fields"
                  constraints="$constraints" orderby="$orderby"
                  directions="$directions" distinct="$distinct"
                  max="$max" offset="$offset"
                  searchdir="$searchdir">

         <%@include file="showclusternodes.trs.jsp" %>
      </mm:related>
    </mm:node>
  </table>
</mm:present>

    </td>
  </tr>
</table>
<hr />
<a target="_new" href="<mm:url page="showanypage.jsp"><mm:param
name="page"><%=request.getServletPath()%></mm:param></mm:url>">source
of this page</a>.

</body>

</html>

</mm:cloud>
</mm:content>