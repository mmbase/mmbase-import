<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8" session="false"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html">
<html>
  <head>
    <title>Welcome to MMBase</title>
    <link rel="stylesheet" href="mmbase/style/css/mmbase.css" type="text/css" />
  </head>
<body >
<table>
  <tr>
    <th class="main" colspan="3">Welcome to MMBase</th>
  </tr>
  <tr>
    <td colspan="3">
      <p>
        You are running <a href="<mm:url page="/version" />"><%=org.mmbase.Version.get() %></a>
      </p>
      <p>
        This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source Initiative.
      </p>
      <p>
        MMBase has been build under the <a href="mmbase/mpl-1.0.html">Mozilla Public License, Version 1.0</a>
      </p>
    </td>
  </tr>
  <tr>
    <th>Section</th>
    <th colspan="2">Description</th>
  </tr>  
  <tr>
    <td>MMBase Demos Installation</td>
    <td>
      <p>
        Install the applications for MMBase examples. Go here <strong>FIRST</strong> if you run MMBase for the first time, and if you want
        to use or view the MMBase examples.
      </p>
      <p>
        You need to log on using the administrator password (default: admin / admin2k).
      </p>
    </td>
    <td class="link" >
      <a href="<mm:url page="mmexamples/install.jsp" />"><img alt="&gt;" src="mmbase/style/images/next.gif" /></a>
    </td>
  </tr>
  
  <tr>
    <td>MMBase Demos</td>
    <td>
      <p>
        Examples of MMBase functionality. Includes among other things a news application, an alternate editor,
        and the MMBase editwizards.
      </p>
    </td>
    <td class="link" >
      <a href="<mm:url page="mmexamples/" />"><img alt="&gt;" src="mmbase/style/images/next.gif"  /></a>
    </td>
  </tr>
  
  <tr>
    <td>Taglib Demo</td>
    <td>
      Demonstrates the use of the MMBase taglib, a library of useful tags with which you can
      retrieve data form MMBase in your web pages.
    </td>
    <td class="link" >
      <a href="<mm:url page="mmexamples/taglib/whatistaglib.jsp" />"><img alt="&gt;" src="mmbase/style/images/next.gif" /></a>
    </td>
  </tr>
  
  <tr>
    <td>MMBase Documentation</td>
    <td>
      An overview of available MMBase documentation, listed by target group.<br />
    </td>
    <td class="link" >
      <a href="<mm:url page="mmdocs/" />"><img alt="&gt;" src="mmbase/style/images/next.gif" /></a>
    </td>
  </tr>
  
  <tr>
    <td>JSP Editors</td>
    <td>
      <p>
        Generic editors for adding and changing objects in MMBase.
        These editors are mostly intended for experienced users.
      </p>
    </td>
    <td class="link" >
      <a href="<mm:url page="mmbase/edit/" />"><img alt="&gt;" src="mmbase/style/images/next.gif" /></a>
    </td>
  </tr>
  
  <tr>
    <td>Admin pages</td>
    <td>
      <p>
        The administrator's pages of MMBase allow you to retrieve info and configure modules, builders, the cache,
        servers, and databases.
      </p>
    </td>
    <td class="link" >
      <a href="<mm:url page="mmbase/admin/" />"><img alt="&gt;" src="mmbase/style/images/next.gif" /></a>
    </td>
  </tr>

  <tr>
    <td>www.mmbase.org</td>
    <td>
      Link to the MMBase website.
    </td>
    <td class="link" >
      <a href="http://www.mmbase.org"><img alt="&gt;" src="mmbase/style/images/next.gif" /></a>
    </td>
  </tr> 
</table>
</body>
</html>
</mm:content>