<%@page   contentType="text/html;charset=utf-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<%@include file="../../settings.jsp" %>
<mm:content expires="0">
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator">
<% String builder = request.getParameter("builder");
   String field = request.getParameter("field");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
  <title>Administrate Builder <%=builder%> of Field <%=field%></title>
  <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body class="basic" >
  <table summary="builder field property data">
    <form  method="POST">
      <tr>
      </tr>
    </form>
  </table>

  <tr><td>&nbsp;</td></tr>

  <tr class="footer">
    <td class="navigate"><a href="<mm:url page="<%="field.jsp?builder="+builder+"&field="+field%>"/>"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
    <td class="data" colspan="3">Return to Builder Field Administration</td>
  </tr>
</table>
</body></html>
</mm:cloud>
</mm:content>