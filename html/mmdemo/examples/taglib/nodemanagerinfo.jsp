<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/request" prefix="req" %>
<mm:cloud name="mmbase">

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
      <req:existsparameter name="nodemanager">

        <% String where = "name='" + request.getParameter("nodemanager") + "'"; %>
        <mm:listnodes type="typedef" fields="name,description" where="<%= where %>">
          <p><b><%=name%></b></p>
          <p><%=description%></p>
        </mm:listnodes>

        <p><b>Names of all fields</b></p>
        <mm:fieldlist nodemanager='<%=request.getParameter("nodemanager")%>'>
          <mm:fieldinfo type="name"/>
        </mm:fieldlist>

        <p><b>GUI names of all fields</b></p>
        <mm:fieldlist nodemanager='<%=request.getParameter("nodemanager")%>'>
          <mm:fieldinfo type="guiname"/>
        </mm:fieldlist>

        <p><b>Names of all edit fields</b></p>
        <mm:fieldlist nodemanager='<%=request.getParameter("nodemanager")%>' type="edit">
          <mm:fieldinfo type="name"/>
        </mm:fieldlist>

        <p><b>GUI names of all edit fields</b></p>
        <mm:fieldlist nodemanager='<%=request.getParameter("nodemanager")%>' type="edit">
          <mm:fieldinfo type="guiname"/>
        </mm:fieldlist>

        <p><b>Names of all list fields</b></p>
        <mm:fieldlist nodemanager='<%=request.getParameter("nodemanager")%>' type="list">
          <mm:fieldinfo type="name"/>
        </mm:fieldlist>

        <p><b>GUI names of all list fields</b></p>
        <mm:fieldlist nodemanager='<%=request.getParameter("nodemanager")%>' type="list">
          <mm:fieldinfo type="guiname"/>
        </mm:fieldlist>

        <p><b>Names of all search fields</b></p>
        <mm:fieldlist nodemanager='<%=request.getParameter("nodemanager")%>' type="search">
          <mm:fieldinfo type="name"/>
        </mm:fieldlist>

        <p><b>GUI names of all search fields</b></p>
        <mm:fieldlist nodemanager='<%=request.getParameter("nodemanager")%>' type="search">
          <mm:fieldinfo type="guiname"/>
        </mm:fieldlist>

      </req:existsparameter>
    </td>
  </tr>
</table>

</body>

</html>

</mm:cloud>
