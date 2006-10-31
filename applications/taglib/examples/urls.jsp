<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
<head>
  <title>MMBase taglib and URLS</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <%@ include file="menu.jsp"%>
    <h1>URLs</h1>
    
    <mm:cloud>
      <table>
        <tr>
          <th colspan="2">
            Basicly Url tags can have referid, absolute, and page attributes.
          </th>
        </tr>
        <tr>
          <td width="50%">
            <pre><mm:include cite="true" page="codesamples/urls.jsp" escape="text/xml" /></pre>
          </td>
          <td width="50%">
            <jsp:directive.include  file="codesamples/urls.jsp" />
          </td>
        </tr>
      </table>
    </mm:cloud>
    <%--
            <hr />
            <mm:listnodes type="images" max="1">
              <mm:node id="node" />
            </mm:listnodes>
            Image: ${node}
            <hr />
            <p><mm:leaffile objectlist="$node" page="leafincluded.jsp" /></p>
            <p><mm:leaffile objectlist="$node" page="leafincluded.jsp" absolute="server" /></p>
            <p><mm:leaffile objectlist="$node" page="leafincluded.jsp" absolute="context" /></p>
            <p><mm:leaffile objectlist="$node" page="leafincluded.jsp" absolute="true" /></p>
            <hr />
            <p><mm:leaffile objectlist="$node" page="/" /></p>
            <p><mm:leaffile objectlist="$node" page="/" absolute="server" /></p>
            <p><mm:leaffile objectlist="$node" page="/" absolute="context" /></p>
            <p><mm:leaffile objectlist="$node" page="/" absolute="true" /></p>
            <hr />
            <p><mm:leafinclude objectlist="$node" page="leafincluded.jsp" /></p>
            <hr />
            <p><mm:leafinclude objectlist="$node" page="/" /></p>
            --%>
</body>
</html>
