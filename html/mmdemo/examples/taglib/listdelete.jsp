<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/request" prefix="req" %>
<mm:cloud name="mmbase">

<html>

<head>
  <title>Taglib examples</title>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>List &amp; delete</h1>

<req:existsparameter name="nodemanager">
  <req:existsparameter name="deletenumber">
    <mm:deletenode number='<%= request.getParameter("deletenumber") %>'/>
  </req:existsparameter>
</req:existsparameter>

<table>
  <tr>
    <td valign="top">
      <mm:listnodes type="typedef" fields="name,description">
        <a href="listdelete.jsp?nodemanager=<%=name%>"><%=name%></a><br>
      </mm:listnodes>
    </td>
    <td valign="top">
      <req:existsparameter name="nodemanager">
        <table>
          <mm:listnodes type='<%=request.getParameter("nodemanager")%>' fields="number">
            <mm:first>
              <tr>
                <mm:fieldlist type="list">
                  <td>
                    <mm:fieldinfo type="name"/>
                  </td>
                </mm:fieldlist>
              </tr>
            </mm:first>
            <tr>
              <mm:fieldlist type="list">
                <td>
                  <mm:fieldinfo type="value"/>
                </td>
              </mm:fieldlist>
              <td>
                <a href='listdelete.jsp?nodemanager=<%=request.getParameter("nodemanager")%>&deletenumber=<%=number%>'>delete</a>
              </td>
            </tr>
          </mm:listnodes>
        </table>
      </req:existsparameter>
    </td>
  </tr>
</table>

</body>

</html>

</mm:cloud>
