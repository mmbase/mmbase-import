<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<%@ include file="methods.jsp"%>

<mm:cloud name="mmbase">

<mm:import externid="nodes" />
<mm:import externid="path" />
<mm:import externid="fields" />
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
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Using the list tag</h1>

<form method="POST">
  <table>
    <tr>
      <td>nodes</td>
      <td><input type="text" size="60" name="nodes" value="<mm:write referid="nodes"/>"></td>
      <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
      <td>distinct</td>
      <td><input type="text" size="5" name="distinct" value="<mm:write referid="distinct"/>"></td>
    </tr>
    <tr>
      <td>path</td>
      <td><input type="text" size="60" name="path" value="<mm:write referid="path"/>"></td>
      <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
      <td>max</td>
      <td><input type="text" size="5" name="max" value="<mm:write referid="max"/>"></td>
    </tr>
    <tr>
      <td>fields</td>
      <td><input type="text" size="60" name="fields" value="<mm:write referid="fields"/>"></td>
      <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
      <td>offset</td>
      <td><input type="text" size="5" name="offset" value="<mm:write referid="offset"/>"></td>
    </tr>
    <tr>
      <td>constraints</td>
      <td><input type="text" size="60" name="constraints" value="<mm:write referid="constraints"/>"></td>
      <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
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
      <td colspan="3" align="center"><input type="submit" value="list"/></td>
    </tr>
 </table>
</form>

<br>

<mm:present referid="path">
  <table border="1">
    <mm:list nodes="${nodes}" path="${path}" fields="${fields}"
             constraints="${constraints}" orderby="${orderby}"
             directions="${directions}" distinct="${distinct}"
             max="${max}" offset="${offset}" searchdir="${searchdir}">
      <tr>
        <mm:compare referid="fields" value="">
          <mm:write referid="path" vartype="String" jspvar="path">
          <% for (Enumeration e = convertToEnumeration(path); e.hasMoreElements();) { %>
            <mm:node element="<%=(String)e.nextElement()%>">
               <mm:fieldlist type="list">
                  <td>
                    <mm:fieldinfo type="value"/>
                  </td>
               </mm:fieldlist>
            </mm:node>
          <% } %>
		  </mm:write>
        </mm:compare>
        <mm:compare referid="fields" value="" inverse="true">
          <mm:write referid="fields" vartype="String" jspvar="fields">
          <% for (Enumeration e = convertToEnumeration(fields); e.hasMoreElements();) { %>
            <td>
              <mm:field name="<%=(String)e.nextElement()%>"/>
            </td>
          <% } %>
          </mm:write>
        </mm:compare>
      </tr>
    </mm:list>
  </table>
</mm:present>

</body>

</html>

</mm:cloud>
