<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" 
%>
<mm:cloud>

<table>
  <tr><th colspan="2">You can also use JSTL tags to define variables, because these variables are equivalent.</th></tr>
  <tr valign="top">
    <td width="50%"><pre><mm:include cite="true" page="codesamples/jstl.set.jsp" escape="text/xml" /></pre></td>
    <td width="50%"><%@include file="codesamples/jstl.set.jsp" %></td>
  </tr>

  <tr><th colspan="2">foreach, choose</th></tr>
  <tr valign="top">
    <td width="50%"><pre><mm:include cite="true" page="codesamples/jstl.foreachchoose.jsp" escape="text/xml" /></pre></td>
    <td width="50%"><%@include file="codesamples/jstl.foreachchoose.jsp" %></td>
  </tr>

  <tr><th colspan="2">MMBase 'contexts' are also supported inside JSTL tags.</th></tr>
  <tr valign="top">
    <td width="50%"><pre><mm:include cite="true" page="codesamples/jstl.context.jsp" escape="text/xml" /></pre></td>
    <td width="50%"><%@include file="codesamples/jstl.context.jsp" %></td>
  </tr>

  <tr><th colspan="2">lists. The 'loop' tags from JSTL can be used with mm-tags. Even mm:index and tags like mm:last work.</th></tr>
  <tr valign="top">
    <td width="50%"><pre><mm:include cite="true" page="codesamples/jstl.list.jsp" escape="text/xml" /></pre></td>
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
    <td width="50%"><pre><mm:include cite="true" page="codesamples/jstl.el.jsp" escape="text/xml" /></pre></td>
    <td width="50%"><%@include file="codesamples/jstl.el.jsp" %></td>
  </tr>
  <tr><th colspan="2">
  <p>
    The mm:locale (and mm:content) tag, also influence the fmt: standard tags.
  </p>
  </th></tr>
  <tr valign="top">
    <td width="50%"><pre><mm:include cite="true" page="codesamples/jstl.fmt.jsp" escape="text/xml" /></pre></td>
    <td width="50%"><%@include file="codesamples/jstl.fmt.jsp" %></td>
  </tr>
  <tr><th colspan="2">
  <p>
    MMBase lists also have the JSTL 'varStatus' attribute
  </p>
  </th></tr>
  <tr valign="top">
    <td width="50%"><pre><mm:include cite="true" page="codesamples/jstl.varstatus.jsp" escape="text/xml" /></pre></td>
    <td width="50%"><%@include file="codesamples/jstl.varstatus.jsp" %></td>
  </tr>


</table>

</mm:cloud>