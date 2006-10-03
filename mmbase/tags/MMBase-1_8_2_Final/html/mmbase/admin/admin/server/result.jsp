<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@include file="../../settings.jsp" %>
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator">
<%  String server=request.getParameter("server"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Server <%=server%></title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<%
    String cmd=request.getParameter("cmd");
    Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");
    String msg="";
    try {
        mmAdmin.process(cmd,server,null,request,response);
        msg="<p>"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
    } catch (Exception e ) {
        e.printStackTrace();
        msg="<p> Error: "+e+"</p>";
    }
%>

<table summary="server results">
<tr>
  <th class="header" colspan="2">Results of your action on server <%=server%></th>
</tr>
<tr>
  <td class="multidata" colspan="2">
   <%=msg%>&nbsp;
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="<%="actions.jsp?server="+server%>" />"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="next" border="0" /></td>
<td class="data">Return to Server Administration</td>
</tr>

</table>
</body></html>
</mm:cloud>
