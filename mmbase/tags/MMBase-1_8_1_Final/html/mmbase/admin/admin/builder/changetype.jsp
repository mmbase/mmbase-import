<%@page   contentType="text/html;charset=utf-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*,java.util.*,java.io.*,org.mmbase.util.*,java.net.*,org.mmbase.datatypes.*" %>
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
  <script type="text/javascript" src="datatypes.js">
    
  </script>
  <h1>Builder: <%=builder %>, field: <%=field%></h1>
  <mm:import externid="submit" />
  <mm:present referid="submit">
    <h2>Changing field</h2>
    <mm:import externid="mmbasetype" jspvar="mmbasetype" />

      
  </mm:present>
  <table summary="builder field property data">
    <form  action="<mm:url page="field.jsp" />" method="POST">

      <tr>
        <td class="data">Type</td>
        <td class="data">
          <jsp:directive.include file="properties/dbmmbasetype.jsp" />      
        </td>
        <td class="navigate"><a href="<mm:url page="/mmdocs/informationanalysts/builders.html#field_type" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
      </tr>
      
      <tr>
        <td class="data">          
          Data Type (changing not yet supported, please edit builder XML)
        </td>
        <td class="data">
          <jsp:directive.include file="properties/datatype.jsp" />      
        </td>
        <td class="navigate"><a href="<mm:url page="/mmdocs/informationanalysts/builders.html#field_datatype" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
      </tr>

      <tr>
        <td class="data" colspan="2">
          <p>Make sure all the settings are valid and what you want before changing this field.</p>
          <input type="hidden" name="builder" value="<%=builder%>" />
          <input type="hidden" name="field"   value="<%=field%>" />
          <input type="hidden" name="cmd"   value="dbmmbasetype" />
        </td>
        <td class="linkdata"><input id="submit" name="submit" type="submit" value="Change Field" /></td>
      </tr>


    </form>
  </table>

  <tr class="footer">
    <td class="navigate"><a href="<mm:url page="<%="field.jsp?builder="+builder+"&field="+field%>"/>"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
    <td class="data" colspan="3">Return to Builder Field Administration</td>
  </tr>
</table>
</body></html>
</mm:cloud>
</mm:content>