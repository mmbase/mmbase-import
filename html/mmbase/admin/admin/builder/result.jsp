<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<%@include file="../../settings.jsp" %>
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator">
<% String builder = request.getParameter("builder"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Builder <%=builder%></title>
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
        Hashtable params=new Hashtable();
        params.put("BUILDER",builder);
        params.put("PATH",request.getParameter("path"));
        mmAdmin.process(cmd,builder,params,request,response);
        msg="<p>"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
    } catch (Exception e ) {
        msg="<p> Error: "+e+"</p>";
    }
%>

<table summary="builder results">
<tr>
  <th class="header" colspan="5" >Results of your action on builder <%=builder%></th>
</tr>
<tr>
  <td class="multidata" colspan="5" >
   <%=msg%>&nbsp;
  </td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr class="footer">
<td class="navigate"><a href="<mm:url page="<%="actions.jsp?builder="+builder%>" />"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="4">Return to Builder Administration</td>
</tr>

</table>
</body></html>
</mm:cloud>
