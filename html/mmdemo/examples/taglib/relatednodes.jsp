<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<%@ include file="methods.jsp"%>

<mm:cloud name="mmbase">

<mm:import externid="number" jspvar="number"/>
<mm:import externid="type" jspvar="type"/>
<mm:import externid="fields" jspvar="fields"/>
<mm:import externid="constraints" jspvar="constraints"/>
<mm:import externid="orderby" jspvar="orderby"/>
<mm:import externid="directions" jspvar="directions"/>
<mm:import externid="max" jspvar="max"/>
<mm:import externid="offset" jspvar="offset"/>

<html>

<head>
  <title>Taglib examples</title>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Using the relatednodes tag</h1>

<table cellpadding="5">
  <tr>
    <td valign="top" bgcolor="EEEEEE">

<form method="POST">

<table>

<tr><td colspan="6">&lt;node number=&quot;<input type="text" size="20" name="number" value="<mm:write referid="number"/>">&quot;&gt;</td></tr>
<tr><td></td><td>&lt;related</td><td>type=</td><td>&quot;</td><td><input type="text" size="20" name="type" value="<mm:write referid="type"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>fields=</td><td>&quot;</td><td><input type="text" size="20" name="fields" value="<mm:write referid="fields"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>constraints=</td><td>&quot;</td><td><input type="text" size="20" name="constraints" value="<mm:write referid="constraints"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>orderby=</td><td>&quot;</td><td><input type="text" size="20" name="orderby" value="<mm:write referid="orderby"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>directions=</td><td>&quot;</td><td><input type="text" size="20" name="directions" value="<mm:write referid="directions"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>max=</td><td>&quot;</td><td><input type="text" size="20" name="max" value="<mm:write referid="max"/>"></td><td>&quot;</td></tr>
<tr><td></td><td></td><td>offset=</td><td>&quot;</td><td><input type="text" size="20" name="offset" value="<mm:write referid="offset"/>"></td><td>&quot;</td></tr>
<tr><td></td><td colspan="5">&lt;/related&gt;</td></tr>
<tr><td colspan="6">&lt;/node&gt;</td></tr>
<tr><td colspan="6" align="right"><input type="submit" value="try"/></td></tr>

</table>

</form>

    </td>
    <td valign="top">

<mm:present referid="type">
  <table border="1">
    <mm:node number="<%=number%>">
      <mm:relatednodes type="<%=type%>" fields="<%=fields%>"
                       directions="<%=directions%>" constraints="<%=constraints%>"
                       orderby="<%=orderby%>" max="<%=max%>" offset="<%=offset%>">
        <tr>
          <mm:compare referid="fields" value="">
            <mm:fieldlist type="list">
              <td>
                <mm:fieldinfo type="value"/>
              </td>
            </mm:fieldlist>
          </mm:compare>
          <mm:compare referid="fields" value="" inverse="true">
            <% for (Enumeration e = convertToEnumeration(fields); e.hasMoreElements();) { %>
              <td>
                <mm:field name="<%=(String)e.nextElement()%>"/>
              </td>
            <% } %>
          </mm:compare>
        </tr>
      </mm:relatednodes>
    </mm:node>
  </table>
</mm:present>

    </td>
  </tr>
</table>

</body>

</html>

</mm:cloud>
