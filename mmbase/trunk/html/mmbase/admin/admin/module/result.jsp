<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud name="mmbase">
<% String module = request.getParameter("module"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Module <%=module%></title>
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
        Hashtable params=new Hashtable();
        params.put("module",module);
        params.put("PATH",request.getParameter("path"));
        mmAdmin.process(cmd,module,params,request,response);
        msg="<p>"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
    } catch (Exception e ) {
        msg="<p> Error: "+e.getMessage()+"</p>";
    }
%>

<table summary="module results" width="93%" cellspacing="1" cellpadding="3" >
<tr align="left">
  <th class="header" colspan="5" >Results of your action on module <%=module%></td>
</tr>
<tr>
  <td class="multidata" colspan="5" >
   <%=msg%>&nbsp;
  </td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
<td class="navigate"><a href="actions.jsp?module=<%=module%>"><img src="../../images/pijl2.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="4">Return to Module Administration</td>
</tr>

</table>
</body></html>
</mm:cloud>
