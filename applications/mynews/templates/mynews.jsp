<%@page session="false" errorPage="mynews/error.jsp" language="java" contentType="text/html; charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content language="en" type="text/html" escaper="inline">
<mm:cloud>
<html>
<head>
  <title>MMExamples - MyNews</title>
  <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body>
  <table width="90%" align="center">
    <tr>
      <th  colspan="2">Description of MyNews</th>
    </tr>
    <tr>
      <td colspan="2" valign="top">
        <p>MyNews is an easy example that show how to create a small magazine with news articles.</p>
        <p>
          This page will give information about how a magazine like this MyNews magazine is structured.
          On your filesystem in the directory /mmexamples/jsp/mynews you will find the templates that show the actual layout of the MyNews magazine.
        </p>
      </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr>
      <th colspan="2">Location of MyNews</th>
    </tr>
    <tr>
      <td colspan="2" ><br />
      <mm:listnodescontainer type="versions">
          <mm:constraint field="name" value="MyNews" />
          <mm:constraint field="type" value="application" />
          <mm:size>
            <mm:compare value="0">
               The MyNews application is NOT installed. Please install before using it.<br />
               You can install the MyNews application by going to <a href="<mm:url page="/mmbase/admin/default.jsp?category=admin&subcategory=applications" />">ADMIN -> APPLICATIONS</a>
            </mm:compare>
            <mm:compare value="0" inverse="true">
               <mm:url id="url" page="mynews/" write="false" />
               This url will show the MyNews magazine: <a href="<mm:write referid="url" />"><mm:write referid="url" /></a>
            </mm:compare>
          </mm:size>
      </mm:listnodescontainer>
      <br /><br />
    </td>
  </tr>
  <tr><td>&nbsp;</td></tr>
  <tr>
    <th>Cloud Design</th>
    <th>Picture</th>
  </tr>
  <tr valign="top">
    <td>
      Click on the image to get a good view of the MyNews Cloud Design.<br />
      The MyNews application consists of 7 builders (objects of a certain type), namely:
      attachments, mags, images, news, mmevents, urls, and people.<br />
      In this example we will not use the attachments, images and mmevent builders.<p />
      A magazine is a collection of articles or 'news' items.
      The line between mags and news indicates that a magazine can have relations with news articles.
      Articles can be ordered using the 'postion' field of the 'posrel' relation.
      A news article can have relations with attachments, images and urls (all possibly ordered), and
      with mmevents (a publication date) and people (such as the article's author).
    </td>
    <td>
      <a href="<mm:url page="share/images/mynews_cloud.jpg" />" target="img">
        <img src="share/images/mynews_cloud.jpg" width="300" />
      </a>
    </td>
  </tr>
  <tr><td>&nbsp;</td></tr>
  <tr>
    <th>Manual</th>
    <th>Picture</th>
  </tr>
  <tr valign="top">
    <td>
      With the object cloud design described above you can create webpages like the one you see on the right (By clicking the MyNews url you can see the MyNews magazine in real action). The MyNews magazine consists of a couple of news items. 
      The Title of the newsitems are visualized in this picture. If you click on one of the 'next' images, you will be taken to a new template that shows the complete article.
    </td>
    <td><a href="<mm:url page="share/images/mynews_manual.jpg" />" target="img"><img src="share/images/mynews_manual.jpg" width="300" /></a></td>
  </tr>
</table>
  <div class="link">
    <a href="<mm:url page="." />"><img alt="back" src="<mm:url page="/mmbase/style/images/back.gif" />" /></a>
  </div>
</body>
</html>
</mm:cloud>
</mm:content>
