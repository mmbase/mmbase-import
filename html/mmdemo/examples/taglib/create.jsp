<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/request" prefix="req" %>
<mm:cloud name="mmbase">

<html>

<head>
  <title>Taglib examples</title>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Create</h1>

<req:existsparameter name="nodemanager">
  <req:existsparameter name="create">
    <mm:createnode nodemanager='<%= request.getParameter("nodemanager") %>'>
      <mm:fieldlist nodemanager='<%=request.getParameter("nodemanager")%>' type="edit">
        <mm:setfield><%=request.getParameter(fieldname)%></mm:setfield>
      </mm:fieldlist>
    </mm:createnode>
  </req:existsparameter>
</req:existsparameter>

<table>
  <tr>
    <td valign="top">
      <mm:listnodes type="typedef" fields="name,description">
        <a href="create.jsp?nodemanager=<%=name%>"><%=name%></a><br>
      </mm:listnodes>
    </td>
    <td valign="top">
      <req:existsparameter name="nodemanager">
        <form>
          <input type="hidden" name="nodemanager" value='<%=request.getParameter("nodemanager")%>'>
          <input type="hidden" name="create" value="true">
          <table>
            <mm:fieldlist nodemanager='<%=request.getParameter("nodemanager")%>' type="edit">
              <tr>
                <td valign="top">
                  <mm:fieldinfo type="name"/>
                </td>
                <td valign="top">
                  <mm:fieldinfo type="input"/>
                </td>
              </tr>
            </mm:fieldlist>
          </table>
          <input type="submit" value="create"/>
        </form>
      </req:existsparameter>
    </td>
  </tr>
</table>

</body>

</html>

</mm:cloud>
