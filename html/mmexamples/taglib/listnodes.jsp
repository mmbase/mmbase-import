<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<%@ include file="methods.jsp"%>

<mm:cloud name="mmbase">

<mm:import externid="type"        />
<mm:import externid="constraints" />
<mm:import externid="orderby"     />
<mm:import externid="directions"  /> 
<mm:import externid="max"        />
<mm:import externid="offset"     />

<html>

<head>
  <title>Taglib examples</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Using the listnodes tag</h1>

<form method="POST">
  <table >
    <tr>
      <td>type</td>
      <td><input type="text" size="60" name="type" value="<mm:write referid="type"/>"></td>
      <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
      <td>max</td>
      <td><input type="text" size="5" name="max" value="<mm:write referid="max"/>"></td>
    </tr>
    <tr>
      <td>constraints</td>
      <td><input type="text" size="60" name="constraints" value="<mm:write referid="constraints"/>"></td>
      <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
      <td>offset</td>
      <td><input type="text" size="5" name="offset" value="<mm:write referid="offset"/>"></td>
    </tr>
    <tr>
      <td>orderby</td>
      <td><input type="text" size="60" name="orderby" value="<mm:write referid="orderby"/>"></td>
      <td colspan="3">&nbsp;</td>
    </tr>
    <tr>
      <td>directions</td>
      <td><input type="text" size="60" name="directions" value="<mm:write referid="directions"/>"></td>
      <td colspan="3" align="center"><input type="submit" value="list"/></td>
    </tr>
  </table>
</form>

<br>

<mm:present referid="type">
  <table border="1">
    <mm:listnodes type="${type}" 
             constraints="${constraints}" orderby="${orderby}"
             directions="${directions}"
             max="${max}" offset="${offset}">
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

</body>

</html>

</mm:cloud>
