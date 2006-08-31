<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud jspvar="cloud">
<html>
<head>
<title>MMBase Demos</title>
<link rel="stylesheet" href="../css/mmbase.css" type="text/css">
</head>

<body >

<table align="center" width="97%" cellspacing="1" cellpadding="3" border="0">
<tr>
    <th class="main" colspan="3">SCAN demo's</th>
</tr>
<%
    if (!cloud.getCloudContext().hasModule("scanparser")) {
%>
<tr>
    <td colspan="3">
     SCAN is an old scripting langauge that is shipped with MMBase.<br />
     In the default installation, it is not active. If you want to make SCAN active and available for use, you need to do the following:
     <ul>
        <li>Make the following MMBase modules active: <br />
            <ul>
              <li>calc (math)</li>
              <li>info (system info commands)</li>
              <li>mmbase (basic SCAN commands)</li>
              <li>mmedit (for the SCAN editors)</li>
              <li>mmlanguage (prompts for the SCAN editors)</li>
              <li>scanparser (essential parser)</li>
              <li>session (maintain user info)</li>
            </ul>
            Optionally, you can activate the modules: <br />
            <ul>
              <li>scancache (caching of SCAN pages)</li>
              <li>transactionhandler (transactions through xml)</li>
            </ul>
            To make a module active, find the modulename.xml file in the modules directory of the MMBase configuration. In the file, change the 'status' from inactive to active, and save the file.
          </li>
      <li>In the web.xml, add the SCAN servlet (scanserv). Also add the mapping for SCAN pages (*.shtml).<br />
            The default web.xml of MMBase contains example code which you can uncomment.
      </li>
      <li>Restart your server.
      </li>
       </ul>
    </td>
</tr>
<%
    } else {
%>
<tr>
    <th>Name demo</th>
    <th colspan="2">Description</th>
</tr>

<tr>
    <td>Community</td>
    <td>
        Example of the community-features of MMBase (forum &amp; chat)
    </td>
    <td class="link">
        <a href="scan/community.shtml"><img src="../mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
    </td>
</tr>


<tr>
    <td>MyYahoo</td>
    <td>
        Small example of a yahoo like topic/url system
    </td>
    <td class="link">
        <a href="scan/myyahoo.shtml"><img src="../mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
    </td>
</tr>

<tr>
    <td>MyNews</td>
    <td>
        Small example of a news/magazine system
    </td>
    <td class="link">
        <a href="scan/mynews.shtml"><img src="../mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
    </td>
</tr>

<tr>
    <td>Codings</td>
    <td>
        Shows text in different encodings.
    </td>
    <td class="link">
        <a href="scan/codings/index.shtml"><img src="../mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
    </td>
</tr>

<tr>
    <td>BugTracker</td>
    <td>
        An application to track bugs.
    </td>
    <td class="link" >
        <a href="scan/bugtracker.shtml"><img src="../mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
    </td>
</tr>
<%
    }
%>


<tr><td colspan="3">&nbsp;</td></tr>

</table>
<a href="<mm:url page=".." />"> back</a>
</body>
</html>
</mm:cloud>
