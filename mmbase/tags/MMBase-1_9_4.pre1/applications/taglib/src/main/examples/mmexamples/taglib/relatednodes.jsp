<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page errorPage="error.jsp" %><mm:content type="text/html" expires="0">

<mm:cloud>

<mm:import externid="number" from="parameters">default.mags</mm:import>
<mm:import externid="type" from="parameters">news</mm:import>
<mm:import externid="constraints"  from="parameters"/>
<mm:import externid="orderby"  from="parameters"/>
<mm:import externid="directions"  from="parameters"/>
<mm:import externid="max" from="parameters">10</mm:import>
<mm:import externid="offset"  from="parameters"/>

<html>

<head>
  <title>Using the relatednodes tag</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Using the relatednodes tag</h1>

<p>
  This is the next thing you might want. If you are 'in' a certain node , you are going to want to find the 'related' nodes. The mm:relatednodes tag does provide this 
  functionality.
</p>

<table>
  <tr>
    <td valign="top">

<form method="POST">

<table>

<tr><td colspan="6">&lt;mm:node number=&quot;<input type="text" size="20" name="number" value="<mm:write referid="number"/>">&quot;&gt;</td></tr>
<tr><td></td><td>&lt;mm:relatednodes</td><td>type=</td><td>&quot;</td><td><input type="text" size="20" name="type" value="<mm:write referid="type"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>constraints=</td><td>&quot;</td><td><input type="text" size="20" name="constraints" value="<mm:write referid="constraints"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>orderby=</td><td>&quot;</td><td><input type="text" size="20" name="orderby" value="<mm:write referid="orderby"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>directions=</td><td>&quot;</td><td><input type="text" size="20" name="directions" value="<mm:write referid="directions"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>max=</td><td>&quot;</td><td><input type="text" size="20" name="max" value="<mm:write referid="max"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>offset=</td><td>&quot;</td><td><input type="text" size="20" name="offset" value="<mm:write referid="offset"/>"></td><td>&quot;</td></tr>
<tr><td></td><td colspan="5">&lt;/mm:relatednodes&gt;</td></tr>
<tr><td colspan="6">&lt;/mm:node&gt;</td></tr>
<tr><td colspan="6" align="right"><input type="submit" name="try" value="try"/></td></tr>

</table>

</form>

</td>
<td valign="top">
<mm:import externid="try" />
<mm:present referid="try">
  <table>
    <mm:node referid="number">
      <mm:relatednodes type="$type"
                       directions="$directions" constraints="$constraints"
                       orderby="$orderby" max="$max" offset="$offset">
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
      </mm:relatednodes>
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