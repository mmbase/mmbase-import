<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase">
<%  String server=request.getParameter("server"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Server <%=server%></title>
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<%
    String cmd=request.getParameter("cmd");
    Module mmAdmin=LocalContext.getCloudContext().getModule("mmadmin");
    String msg="";
    try {
        mmAdmin.process(cmd,server,null,request,response);
        msg="<p>"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
    } catch (Exception e ) {
        msg="<p> Error: "+e.getMessage()+"</p>";
    }
%>

<table summary="server results" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
  <th class="header" colspan="2">Results of your action on server <%=server%></td>
</tr>
<tr>
  <td class="multidata" colspan="2">
   <%=msg%>&nbsp;
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="actions.jsp?server=<%=server%>"><img src="../../images/pijl2.gif" alt="next" border="0" align="left" /></td>
<td class="data">Return to Server Administration</td>
</tr>

</table>
</body></html>
</mm:cloud>
