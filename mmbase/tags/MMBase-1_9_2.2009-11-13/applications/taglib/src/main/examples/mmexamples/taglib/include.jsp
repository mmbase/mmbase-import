<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
<head>
  <title>MMBase taglib and including</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <%@ include file="menu.jsp"%>
    <h1>Including</h1>
    
    <mm:cloud>
      <table>
        <tr>
          <th colspan="2">
            The 'mm:include' tag is similar to jsp:include. It's a bit handier, because it knows about mmbase-variables.
            See also what happens with <a href="<mm:url><mm:param name="hoi" value="Tot ziens" /></mm:url>">this url</a>
          </th>
        </tr>
        <tr valign="top">
          <td width="50%">
            <pre><mm:include cite="true" page="codesamples/include.jsp" escape="text/xml" /></pre>
            included.jsp: <pre><mm:include cite="true" page="codesamples/included.jsp" escape="text/xml" /></pre>
          </td>
          <td width="50%"><%@include file="codesamples/include.jsp" %></td>
        </tr>
        <tr><th colspan="2">
          When including, you may want to use the request.
        </th></tr>
        <tr valign="top">
          <td width="50%">
            <pre><mm:include cite="true" page="codesamples/include.request.jsp" escape="text/xml" /></pre>
            included.request.jsp: <pre><mm:include cite="true" page="codesamples/included.request.jsp" escape="text/xml" /></pre>
          </td>
          <td width="50%"><%@include file="codesamples/include.request.jsp" %></td>
        </tr>
      </table>      
    </mm:cloud>
</body>
</html>
