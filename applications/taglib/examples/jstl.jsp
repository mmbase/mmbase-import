<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<head>
  <title>JSTL and Variables</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<%@ include file="menu.jsp"%>


<h1><a href="http://java.sun.com/products/jsp/jstl">JSTL</a> and variables</h1>

<p>
  MMBase 1.8 taglib variables are equivalent with JSTL taglib variables and EL. This was not the case in MMBase 1.7. A 1.7 compatiblity mode can be turned on by a property in web.xml:
  <pre>
  &lt;context-param&gt;
    &lt;param-name&gt;mmbase.taglib.isELIgnored&lt;/param-name&gt;
    &lt;param-value&gt;false&lt;/param-value&gt;
  &lt;/context-param&gt;
  </pre>
  This page assumes jstl to be installed, and this parameter to be <code>true</code>.
</p>

<mm:cloud>

<table>
  <tr><th colspan="2">You can also use JSTL tags to define variables, because these variables are equivalent.</th></tr>
  <tr valign="top">
    <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/jstl.set.jsp" /></mm:formatter></pre></td>
    <td width="50%"><%@include file="codesamples/jstl.set.jsp" %></td>
  </tr>

  <tr><th colspan="2">foreach, choose</th></tr>
  <tr valign="top">
    <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/jstl.foreachchoose.jsp" /></mm:formatter></pre></td>
    <td width="50%"><%@include file="codesamples/jstl.foreachchoose.jsp" %></td>
  </tr>

  <tr><th colspan="2">MMBase 'contexts' are also supported inside JSTL tags.</th></tr>
  <tr valign="top">
    <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/jstl.context.jsp" /></mm:formatter></pre></td>
    <td width="50%"><%@include file="codesamples/jstl.context.jsp" %></td>
  </tr>

  <tr><th colspan="2">lists</th></tr>
  <tr valign="top">
    <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/jstl.list.jsp" /></mm:formatter></pre></td>
    <td width="50%"><%@include file="codesamples/jstl.list.jsp" %></td>
  </tr>

  <tr><th colspan="2">
  <p>
    You can use the JSP2 expression language. If your application server does not
    support it, you can fall back to the (legacy) expression language supported by MMBase. The MMBase
    EL is triggered by a '+', and you have to use a $ on the variable-values (like in Perl).
  </p>
  <p>
    To avoid conflicts with the JSP EL syntax, MMBase taglib now uses [] in stead of {} (though {}
    is still supported, if those are not interpreted by EL of JSP2, e.g. if you have &lt;%@page
    isELIgnored="true" %&gt;).
  </p>
  </th></tr>
  <tr valign="top">
    <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/jstl.el.jsp" /></mm:formatter></pre></td>
    <td width="50%"><%@include file="codesamples/jstl.el.jsp" %></td>
  </tr>


</table>

</mm:cloud>
</body>
</html>
