<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<%@include file="../../settings.jsp" %>
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
  <head>
    <mm:import externid="builder" />
    <mm:import externid="path" />
    <mm:import externid="cmd" />
    <title>Administrate Builder <mm:nodeinfo nodetype="$builder" type="gui" /></title>
    <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
    <meta http-equiv="pragma" value="no-cache" />
    <meta http-equiv="expires" value="0" />
  </head>
  <body class="basic" >
    <table summary="builder results">
      <tr>
        <th class="header" colspan="5" >Results of your action on builder <mm:nodeinfo nodetype="$builder" type="gui" /></th>
      </tr>
      <tr>
        <td class="multidata" colspan="5" >
          <mm:functioncontainer module="mmadmin">
            <mm:nodefunction name="$cmd" referids="builder,path" >
              <mm:field name="RESULT" escape="p" />
            </mm:nodefunction>
          </mm:functioncontainer>
        </td>
      </tr>
      <tr class="footer">
        <td class="navigate"><a href="<mm:url page="actions.jsp" referids="builder" />"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
        <td class="data" colspan="4">Return to Builder Administration</td>
      </tr>
    </table>
  </body>
</html>
</mm:cloud>
