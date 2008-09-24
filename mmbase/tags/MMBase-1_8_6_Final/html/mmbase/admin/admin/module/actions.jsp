<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<%@include file="../../settings.jsp" %>
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator" jspvar="cloud">
<% String module = request.getParameter("module"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Module <%=module%></title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="module actions">
<%
   Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");

   String cmd = request.getParameter("cmd");
   String msg="";
   if (cmd!=null) {
    try {
        Hashtable params=new Hashtable();
        params.put("MODULE",module);
        params.put("CLOUD", cloud);
        if (cmd.equals("MODULE-SETPROPERTY")) {
            String property=request.getParameter("property").trim();
            if (property.length()==0) {
                throw new Exception("Property name should be specified");
            }
            params.put("PROPERTYNAME",request.getParameter("property"));
            params.put("VALUE",request.getParameter("value"));
        }
        mmAdmin.process(cmd,module,params,request,response);
//        msg="<p>"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
    } catch (Exception e ) {
        msg="<p> Error: "+e.getMessage()+"</p>";
    }
   }
%>
<tr>
 <th class="header" colspan="5">Description of <%=module%></th>
</tr>
<tr>
 <td class="multidata" colspan="5">
        <p><%=mmAdmin.getInfo("MODULEDESCRIPTION-"+module,request,response)%></p>
        <%=msg%>&nbsp;
 </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
<th class="header">Setting</th>
  <th class="header" colspan="3">Value</th>
  <th class="navigate" >Change</th>
</tr>
<tr>
 <td class="data">Class</td>
 <td class="data" colspan="3"><%=mmAdmin.getInfo("MODULECLASSFILE-"+module,request,response)%></td>
 <td class="linkdata" >
    Not Available
</td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
<th class="header">Property</th>
  <th class="header" colspan="3">Value</th>
  <th class="navigate">Change</th>
</tr>
<%
   java.util.Map params = new java.util.Hashtable();
   params.put("CLOUD", cloud);
    NodeList props=mmAdmin.getList("MODULEPROPERTIES-"+module, params,request,response);
    for (int i=0; i<props.size(); i++) {
        Node prop=props.getNode(i);
%>
<tr>
 <td class="data"><%=prop.getStringValue("item1")%></td>
 <td class="data" colspan="3"><%=prop.getStringValue("item2")%>&nbsp;</td>
 <td class="navigate">
    <a href="<mm:url page="<%="setproperty.jsp?module="+module+"&property="+prop.getStringValue("item1")%>" />"><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
</td>
</tr>

<%  } %>

<tr>
 <td class="data">add new</td>
 <td class="data" colspan="3">&nbsp;</td>
 <td class="navigate">
    <a href="<mm:url page="<%="newproperty.jsp?module="+module%>"/>"><img src="<mm:url page="/mmbase/style/images/create.gif" />" alt="add" border="0" /></a>
</td>
</tr>

<tr><td>&nbsp;</td></tr>

  <form action="<mm:url page="result.jsp"/>" method="POST">
<tr>
<th class="header">Action</th>
  <th class="header" colspan="3">Path</th>
  <th class="navigate">Confirm</th>
</tr>
<tr>
 <td class="data">Save</td>
 <td class="data" colspan="3"><input name="path" value="/tmp/<%=module%>.xml" size="80" /></td>
 <td class="linkdata" >
   <input type="hidden" name="module" value="<%=module%>" />
   <input type="hidden" name="cmd" value="MODULESAVE" />
   <input type="image" src="<mm:url page="/mmbase/style/images/ok.gif" />" alt="OK" border="0"  />
 </td>
</tr>
  </form>

<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="../modules.jsp" />"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="4">Return to Module Overview</td>
</tr>
</table>
</body></html>
</mm:cloud>
