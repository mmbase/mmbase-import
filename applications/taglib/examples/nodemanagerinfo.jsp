<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase">
<mm:import externid="nodemanager" from="parameters" />

<html>

<head>
  <title>Node manager info</title>
  <link href="style.css" rel="stylesheet" type="text/css" media="screen"  />
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Nodemanager info</h1>

<table>
  <tr>
    <td valign="top">
      <mm:listnodes type="typedef">
        <a href="<mm:url page="nodemanagerinfo.jsp">
                     <mm:param name="nodemanager"><mm:field name="name" /></mm:param>
                 </mm:url>"><mm:field name="name" /></a><br />
      </mm:listnodes>
    </td>
    <td valign="top">
      <mm:present referid="nodemanager">
        <mm:listnodes type="typedef" constraints="where name='${nodemanager}'">
          <p><b><mm:field name="name" /></b></p>
          <p><mm:field name="description" /></p>
        </mm:listnodes>

        <p><b>Names of all fields</b></p>
        <mm:fieldlist nodetype="${nodemanager}">
          <mm:fieldinfo type="name"/>
        </mm:fieldlist>

        <p><b>GUI names of all fields</b></p>
        <mm:fieldlist nodetype="${nodemanager}">
          <mm:fieldinfo type="guiname"/>
        </mm:fieldlist>

        <p><b>Names of all edit fields</b></p>
        <mm:fieldlist nodetype="${nodemanager}" type="edit">
          <mm:fieldinfo type="name"/>
        </mm:fieldlist>

        <p><b>GUI names of all edit fields</b></p>
        <mm:fieldlist nodetype="${nodemanager}" type="edit">
          <mm:fieldinfo type="guiname"/>
        </mm:fieldlist>

        <p><b>Names of all list fields</b></p>
        <mm:fieldlist nodetype="${nodemanager}" type="list">
          <mm:fieldinfo type="name"/>
        </mm:fieldlist>

        <p><b>GUI names of all list fields</b></p>
        <mm:fieldlist nodetype="${nodemanager}" type="list">
          <mm:fieldinfo type="guiname"/>
        </mm:fieldlist>

        <p><b>Names of all search fields</b></p>
        <mm:fieldlist nodetype="${nodemanager}" type="search">
          <mm:fieldinfo type="name"/>
        </mm:fieldlist>

        <p><b>GUI names of all search fields</b></p>
        <mm:fieldlist nodetype="${nodemanager}" type="search">
          <mm:fieldinfo type="guiname"/>
        </mm:fieldlist>

      </mm:present>
    </td>
  </tr>
</table>

</body>

</html>

</mm:cloud>
