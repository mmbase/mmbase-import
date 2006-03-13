<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page import="org.mmbase.bridge.*,org.mmbase.module.core.MMBase" 
%><mm:content type="text/html" expires="0">
<mm:cloud rank="administrator">

<html xmlns="http://www.w3.org/TR/xhtml">
<head>
  <title>Administrate Servers</title>
  <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body class="basic" >
  <table summary="servers">
    <tr>
      <th class="header" colspan="7">Server Overview</th>
    </tr>
    <tr>
      <td class="multidata" colspan="7">
        <p>
          This overview describes all MMBase servers running on this MMBase system
        </p>
      </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr>
      <th class="header">Machine</th>
      <th class="header">State</th>
      <th class="header">Last Seen</th>
      <th class="header">Host</th>
      <th class="header">OS</th>
      <th class="navigate">Manage</th>
    </tr>
    <mm:listnodes type="mmservers" >
      <tr>
        <td class="data"><mm:field name="name" /></td>
        <td class="data"><mm:field name="state"><mm:fieldinfo type="guivalue" /></mm:field></td>
        <td class="data"><mm:field name="atime"><mm:time format=":MEDIUM.MEDIUM" /></mm:field></td>
        <td class="data"><mm:field name="host" /></td>
        <td class="data"><mm:field name="os" /></td>
        <td class="navigate">
          <mm:field name="name">
            <mm:compare value="<%=MMBase.getMMBase().getMachineName()%>">
              <a href="<mm:url referids="_@server" page="server/actions.jsp" />"><img src="<mm:url page="/mmbase/style/images/next.gif" />" alt="next" border="0" /></a>
            </mm:compare>
           </mm:field>
        </td>
      </tr>
    </mm:listnodes>
    <tr class="footer">
      <td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
      <td class="data" colspan="6">Return to home page</td>
    </tr>
  </table>
</body>
</html>
</mm:cloud>
</mm:content>
