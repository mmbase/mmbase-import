<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<mm:cloud name="mmbase" method="http">
	<mm:import externid ="deletenumber" />
	<mm:import externid ="nodemanager" />
	

<html>

<head>
  <title>Taglib examples</title>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>List &amp; delete</h1>


<mm:present referid="nodemanager">
  <mm:present referid="deletenumber">
    <mm:deletenode referid="deletenumber"/>
  </mm:present>
</mm:present>

<table>
  <tr>
    <td valign="top">
      <mm:listnodes type="typedef" fields="name,description">
	<A HREF="listdelete.jsp?nodemanager=<mm:field name="name"/>"><mm:field name="name"/></A><BR>
      </mm:listnodes>
    </td>
    <td valign="top">
      <mm:present referid="nodemanager">
        <table>
          <mm:listnodes type="${nodemanager}" fields="number">
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
                <a href='listdelete.jsp?nodemanager=<mm:write referid="nodemanager"/>&deletenumber=<%=number%>'>delete</a>
              </td>
            </tr>
          </mm:listnodes>
        </table>
      </mm:present>
    </td>
  </tr>
</table>

</body>

</html>

</mm:cloud>
