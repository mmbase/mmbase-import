<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase">
	<mm:import externid="nodemanager" jspvar="nodemanager"/>

<html>

<head>
  <title>Taglib examples</title>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Nodemanager info</h1>

<table>
  <tr>
    <td valign="top">
      <mm:listnodes type="typedef" fields="name,description">
        <a href="nodemanagerinfo.jsp?nodemanager=<%=name%>"><%=name%></a><br>
      </mm:listnodes>
    </td>
    <td valign="top">
      <mm:present referid="nodemanager">

        <% String where = "name='" + nodemanager + "'"; %>
        <mm:listnodes type="typedef" fields="name,description" constraints="<%= where %>">
          <p><b><%=name%></b></p>
          <p><%=description%></p>
        </mm:listnodes>

        <p><b>Names of all fields</b></p>
        <mm:fieldlist nodetype='<%=nodemanager%>'>
          <mm:fieldinfo type="name"/>
        </mm:fieldlist>

        <p><b>GUI names of all fields</b></p>
        <mm:fieldlist nodetype='<%=nodemanager%>'>
          <mm:fieldinfo type="guiname"/>
        </mm:fieldlist>

        <p><b>Names of all edit fields</b></p>
        <mm:fieldlist nodetype='<%=nodemanager%>' type="edit">
          <mm:fieldinfo type="name"/>
        </mm:fieldlist>

        <p><b>GUI names of all edit fields</b></p>
        <mm:fieldlist nodetype='<%=nodemanager%>' type="edit">
          <mm:fieldinfo type="guiname"/>
        </mm:fieldlist>

        <p><b>Names of all list fields</b></p>
        <mm:fieldlist nodetype='<%=nodemanager%>' type="list">
          <mm:fieldinfo type="name"/>
        </mm:fieldlist>

        <p><b>GUI names of all list fields</b></p>
        <mm:fieldlist nodetype='<%=nodemanager%>' type="list">
          <mm:fieldinfo type="guiname"/>
        </mm:fieldlist>

        <p><b>Names of all search fields</b></p>
        <mm:fieldlist nodetype='<%= nodemanager%>' type="search">
          <mm:fieldinfo type="name"/>
        </mm:fieldlist>

        <p><b>GUI names of all search fields</b></p>
        <mm:fieldlist nodetype='<%=nodemanager%>' type="search">
          <mm:fieldinfo type="guiname"/>
        </mm:fieldlist>

      </mm:present>
    </td>
  </tr>
</table>

</body>

</html>

</mm:cloud>
