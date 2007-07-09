<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@page import="org.mmbase.bridge.*" 
%><%@page import="java.util.*" 
%><%@include file="../../settings.jsp" 
%><mm:content expires="0">
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator">
<% String app = request.getParameter("application"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Application <%=app%></title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body class="basic" >
<%
   String cmd = request.getParameter("cmd");
   Module mmAdmin = ContextProvider.getDefaultCloudContext().getModule("mmadmin");
   String msg="";
   if (cmd != null) {
    try {
        Hashtable params=new Hashtable();
        params.put("APPLICATION",app);
        if (cmd.equals("SAVE")) {
            params.put("PATH",request.getParameter("path"));
        }
        mmAdmin.process(cmd,app,params,request,response);
        msg="<p>"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
    } catch (java.lang.reflect.UndeclaredThrowableException ute) {
      Throwable t = ute;
      while (t.getCause() != null) {
      t = t.getCause();
      }
      msg="<p style=\"white-space:pre;\"> Error: "+ t + "</p>";
      } catch (Throwable e ) {
      msg="<p style=\"white-space:pre;\"> Error: " + e + "</p>";
      }

   }
%>

<table summary="application results">
<tr>
  <th class="header" colspan="2" >Results of your action on application <%=app%></th>
</tr>
<tr>
  <td class="multidata" colspan="2">
   <%=msg%>&nbsp;
  </td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr class="footer">
<td class="navigate"><a href="<mm:url page="<%="actions.jsp?application="+app%>" />"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data">Return to Application Administration</td>
</tr>

</table>
</body></html>
</mm:cloud>
</mm:content>