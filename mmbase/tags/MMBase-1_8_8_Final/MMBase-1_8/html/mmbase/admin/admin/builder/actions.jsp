<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud rank="administrator" jspvar="cloud">
  <mm:import externid="builder" required="true" />
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
<table summary="builder actions">
  <mm:log jspvar="log">

<%

   Module mmAdmin = ContextProvider.getDefaultCloudContext().getModule("mmadmin");

   String cmd = request.getParameter("cmd");
   String msg="";
   if (cmd != null) {
    try {
        Map params=new Hashtable();
        params.put("BUILDER",builder);
        params.put("CLOUD", cloud);

        if (cmd.equals("BUILDER-REMOVEFIELD")) {
            params.put("FIELDNAME",request.getParameter("field"));
            params.put("SURE",request.getParameter("confirm"));
        } else if (cmd.equals("BUILDER-ADDFIELD")) {
            params.put("dbname",request.getParameter("dbname"));
            params.put("mmbasetype",request.getParameter("mmbasetype"));
            params.put("guitype",request.getParameter("datatype"));
            params.put("dbstate",request.getParameter("dbstate"));
            params.put("dbnotnull",request.getParameter("dbnotnull"));
            params.put("dbkey",request.getParameter("dbkey"));
            params.put("dbsize",request.getParameter("dbsize"));
        }
        mmAdmin.process(cmd,builder,params,request,response);
        msg="<p>"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
    } catch (Exception e ) {
       Throwable ec = e;
       String message = ec.getMessage();
       while (message == null && ec.getCause() != null) {
        ec = ec.getCause();
        message = ec.getMessage();
       }
        msg="<p> Error: "+ message + "</p>";
        log.error(message, ec);
    }
   }
%>
<tr>
 <th class="header" colspan="5">Description of <mm:write referid="builder" /></th>
</tr>
<tr>
 <td class="multidata" colspan="5">
   <p><mm:function referids="builder" module="mmadmin" name="BUILDERDESCRIPTION" /></p>
   <%=msg%>&nbsp;
 </td>
</tr>
<tr>
  <td>
    <jsp:expression>builder</jsp:expression>
    <jsp:expression>cloud.getNodeManager(builder).getDescendants()</jsp:expression>
  </td>
</tr>


<tr><td>&nbsp;</td></tr>

<tr>
<th class="header">Setting</th>
  <th class="header" colspan="2">Value</th>
  <th class="navigate" colspan="2">Change</th>
</tr>
<tr>
 <td class="data">Class</td>
 <td class="data" colspan="2"><mm:function referids="builder" module="mmadmin" name="BUILDERCLASSFILE" /></td>
 <td class="linkdata" colspan="2">
    Not Available
</td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
<th class="header">Field</th>
  <th class="header">Name</th>
  <th class="header">Type</th>
  <th class="header">Size</th>
  <th class="navigate">More</th>
</tr>
<mm:nodelistfunction referids="builder" module="mmadmin" name="FIELDS">
  <tr>
    <td class="data"><mm:field name="item1" /></td>
    <td class="data"><mm:field name="item2" id="field" /></td>
    <td class="data"><mm:field name="item3" /></td>
    <td class="data"><mm:field name="item4" /></td>
    <td class="navigate">
      <a href="<mm:url referids="builder,field" page="field.jsp" />"><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
    </td>
  </tr>
</mm:nodelistfunction>
<tr>
 <td class="data">add new</td>
 <td class="data">&nbsp;</td>
 <td class="data">&nbsp;</td>
 <td class="data">&nbsp;</td>
 <td class="navigate">
    <a href="<mm:url referids="builder" page="newfield.jsp" />"><img src="<mm:url page="/mmbase/style/images/create.gif" />" alt="add new" border="0" /></a>
</td>
</tr>

<tr><td>&nbsp;</td></tr>

<form action="<mm:url page="result.jsp" />" method="POST">
<tr>
<th class="header">Action</th>
  <th class="header" colspan="2">Path</th>
  <th class="navigate" colspan="2">Confirm</th>
</tr>
<tr>
 <td class="data">Save</td>
 <td class="data" colspan="2"><input name="path" value="/tmp/<%=builder%>.xml" size="80" /></td>
 <td class="linkdata" colspan="2">
   <input type="hidden" name="builder" value="<%=builder%>" />
   <input type="hidden" name="cmd" value="BUILDERSAVE" />
   <input type="image" src="<mm:url page="/mmbase/style/images/ok.gif" />" alt="OK" border="0"  />
 </td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="../builders.jsp"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="4">Return to Builder Overview</td>
</tr>
</mm:log>
</table>
</body></html>
</mm:cloud>
