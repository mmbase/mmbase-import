<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html">
<%@page session="false" %>
<mm:cloud jspvar="cloud">
<html>
<head>
  <title>MMBase Demos</title>
  <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css" />
  <link rel="icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
  <link rel="shortcut icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
</head>

<body >      
  <table>
    <tr>
      <th class="main" colspan="3">MMBase Demos</th>
    </tr>
    <tr>
      <td colspan="3">
        <p>
          Here's a list of all working examples. Most of them require you to deploy an application, with
          the same name as the example.
        </p>
        <p>
          <a href="<mm:url page="install.jsp" />">Demo-application installation page</a> (default name/password is admin/admin2k)
        </p>
        <p>
          It is adviced to remove the complete /mmexamples directory, which contains these demos, from a production environment.
        </p>
      </td>
    </tr>
    
    
    <tr>
      <th class="main" colspan="3">Jsp/Taglib Demo's</th>
    </tr>
    <tr>
      <th>Name demo</th>
      <th colspan="2">Description</th>
    </tr>
    
    <tr>
      <td>My News</td>
      <td>
        Small example of a news/magazine system
      </td>
      <td class="link" >
        <a href="<mm:url page="mynews.jsp" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>

    <tr>
      <td>My Yahoo</td>
      <td>
        Small example of a yahoo-like system
      </td>
      <td class="link" >
        <a href="<mm:url page="myyahoo.jsp" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>
    
    <tr>
      <td>Taglib</td>
      <td>
        A lot of different examples for the MMBase taglib.
      </td>
      <td class="link" >
        <a href="<mm:url page="taglib/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />"  /></a>
      </td>
    </tr>
    
    <tr>
      <td>Editors</td>
      <td>
        All generic editors are of course very complex examples of jsp-pages.
      </td>
      <td class="link" >
        <a href="<mm:url page="/mmbase/admin/editors/basic.jsp" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>
    
    <tr>
      <td>Community (requires 'community' module and 'community' taglib from mmbase-community.jar)</td>
      <td>
        Example of the community-features of MMBase (forum<!-- &amp; chat-->)
      </td>
      <td class="link" >
        <a href="<mm:url page="community.jsp" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>
    
    <tr>
      <th class="header" colspan="3">Other Demo's</th>
    </tr>
    
    <tr>
      <th>Name demo</th>
      <th colspan="2">Description</th>
    </tr>
    
    <tr>
      <td>Editwizard</td>
      <td>
        Different editwizard-examples.
      </td>
      <td class="link" >
        <a href="<mm:url page="editwizard/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>
    

    <tr>
      <td>Codings</td>
      <td>
        Shows text in different encodings.
      </td>
      <td class="link" >
        <a href="<mm:url page="codings/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>
    
  </table>
  <div class="link">
    <a href="<mm:url page=".." />"><img alt="back" src="<mm:url page="/mmbase/style/images/back.gif" />" /></a>
  </div>
</body>
</html>
</mm:cloud>
</mm:content>