<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page errorPage="error.jsp" %><mm:content expires="0" type="text/html">

<mm:cloud>

<mm:import externid="type"        from="parameters">news</mm:import>
<mm:import externid="constraints" from="parameters">[title] LIKE '%XML%'</mm:import>
<mm:import externid="orderby"     from="parameters" />
<mm:import externid="directions"  from="parameters" /> 
<mm:import externid="max"         from="parameters">10</mm:import>
<mm:import externid="offset"      from="parameters"/>

<html>

<head>
  <title>Taglib examples</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Using the listnodes tag</h1>

<p>
  One of the things you certainly would want to do. Here you can try how it works. Default something is filled in that will give results if 'MyNews' is installed.
</p>

<form method="POST">
&lt;mm:listnodes 
  <table >
    <tr>
      <td>type</td>
      <td><input type="text" size="60" name="type" value="<mm:write referid="type"/>"></td>
      <td>max</td>
      <td><input type="text" size="5" name="max" value="<mm:write referid="max"/>"></td>
    </tr>
    <tr>
      <td>constraints (deprecated, see <a href="query.jsp">query</a>)</td>
      <td><input type="text" size="60" name="constraints" value="<mm:write referid="constraints"/>"></td>
      <td>offset</td>
      <td><input type="text" size="5" name="offset" value="<mm:write referid="offset"/>"></td>
    </tr>
    <tr>
      <td>orderby</td>
      <td><input type="text" size="60" name="orderby" value="<mm:write referid="orderby"/>"></td>
      <td colspan="2" />
    </tr>
    <tr>
      <td>directions</td>
      <td><input type="text" size="60" name="directions" value="<mm:write referid="directions"/>"></td>
      <td colspan="2" align="center"><input type="submit" name="try" value="try"/></td>
    </tr>
  </table>
</form>


<mm:import externid="try" />
<mm:present referid="try">
  <table>
    <tr><th colspan="100">Results</th></tr>
    <mm:listnodes type="$type" 
             constraints="$constraints" orderby="$orderby"
             directions="$directions"
             max="$max" offset="$offset">
      <mm:first>
        <tr>
          <mm:fieldlist type="list">
            <th>
              <mm:fieldinfo type="guiname"/>
            </th>
          </mm:fieldlist>
        </tr>
      </mm:first>
      <tr>
        <mm:fieldlist type="list">
          <td>
            <mm:fieldinfo type="value"/>
          </td>
        </mm:fieldlist>
      </tr>
    </mm:listnodes>
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