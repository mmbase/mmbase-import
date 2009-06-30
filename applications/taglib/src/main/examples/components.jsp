<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><html>
<head>
  <title>MMBase taglib and MMBase components</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <%@ include file="menu.jsp"%>
    <h1>Components</h1>
    
    <mm:cloud>
      <table>
        <tr>
          <th colspan="2">
            You can include mmbase 'components' using taglib. Default the 'core' component is
            available, so we can show blocks of it in this demo.
          </th>
        </tr>
        <tr>
          <td width="50%">
            <pre><mm:include cite="true" page="codesamples/components.jsp" escape="text/xml" /></pre>
          </td>
          <td width="50%">
            <jsp:directive.include  file="codesamples/components.jsp" />
          </td>
        </tr>
      </table>
    </mm:cloud>

</body>
</html>
