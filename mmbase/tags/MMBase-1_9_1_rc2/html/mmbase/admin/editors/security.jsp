<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:cloud><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>MMBase Security Editors</title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body class="basic" >

  <table summary="security editors">
    <tr>
      <th class="header" colspan="2">MMBase Security Editors</th>
    </tr>
    <tr>
      <td class="multidata" colspan="2">
        <p>
          MMBase has a pluggable security implementation structure.
        </p>
        <p>
          Implementations can be based on MMBase objects itself. This is done e.g. in the <em>Cloud
          Context Security</em> implementation. This is similar to the defaultly configured <em>Context
          Security</em> which is configured using an XML in WEB-INF/config/security/context/config.xml.
        </p>
        <p>
          If the implemention is based on MMBase objects, then specialed security editors are possible,
          and you may find a link to those here.
        </p>
      </td>
    </tr>
    <mm:haspage page="/mmbase/security">
      <tr><th class="header" colspan="2">Security editors</th></tr>
      <tr>
        <td class="linkdata" colspan="2"><a href="<mm:url page="/mmbase/security/" />">Security editors (of cloud context security) : <mm:url page="/mmbase/security/" absolute="true" /></a></td>
      </tr>
    </mm:haspage>
    
    <tr class="footer">
      <td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="<mm:url page="/mmbase/style/images/back.gif" />" /></td>
      <td class="data">Return to home page</td>
    </tr>
  </table>
</body>
</html>
</mm:cloud>
