<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
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
      <mm:listnodes type="typedef">
       <a href="<mm:url page="listdelete.jsp">
                   <mm:param name="nodemanager"><mm:field name="name"/></mm:param>
                </mm:url>"><mm:field name="name"/></a><br />
      </mm:listnodes>
    </td>
    <td valign="top">
      <mm:present referid="nodemanager">
        <table>
          <mm:listnodes type="${nodemanager}">
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
                <a href="<mm:url page="listdelete.jsp" referids="nodemanager">
							<mm:param name="deletenumber"><mm:field name="number" /></mm:param>
                         </mm:url>">delete</a>
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
