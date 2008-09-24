<%@page session="false" errorPage="mynews/error.jsp" language="java" contentType="text/html; charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content language="en" type="text/html" escaper="inline">
<mm:cloud>
<html>
<head>
  <title>MMExamples - MyYahoo</title>
  <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body>
  <table width="90%" align="center">
    <tr>
      <th  colspan="2">Description of MyYahoo</th>
    </tr>
    <tr>
      <td colspan="2" valign="top">
        <p>MyYahoo is an easy example that shows how to create a Yahoo-like system of categories in which URL's can be grouped. A small search-engine allows you to search for URL's.</p>
        <p>
          This page will give information about how a system like this can be setup. 
          On your filesystem in the directory /mmexamples/myyahoo you will find the templates that show the actual layout of the MyYahoo site.
        </p>
      </td>
    </tr>
    <tr>
      <th colspan="2">Location of MyYahoo</th>
    </tr>
    <tr>
      <td colspan="2" ><br />
      <mm:listnodes type="versions" constraints="[name]='MyYahoo' AND [type]='application'">
        <mm:first>
          <mm:import id="myyahooIsPresent" />
        </mm:first>
      </mm:listnodes>
      <mm:notpresent referid="myyahooIsPresent">
        The MyYahoo application is NOT installed. Please install before using it.
        You can install the MyYahoo application by going to <a href="<mm:url page="/mmbase/admin/default.jsp?category=admin&subcategory=applications" />">ADMIN -> APPLICATIONS</a>
      </mm:notpresent>
      <mm:present referid="myyahooIsPresent">
        <mm:url id="url" page="/mmexamples/myyahoo/" write="false" />
        This url will show the MyYahoo site: <a href="<mm:write referid="url" />" target="myyahoo"><mm:write referid="url" /></a>
      </mm:present>
      <br /><br />
    </td>
  </tr>
  <tr>
    <th>Cloud Design</th>
    <th>Picture</th>
  </tr>
  <tr valign="top">
    <td>
      Click on the image to get a good view of the MyNews Cloud Design.<br />
      The MyHaoo application consists of 3 builders (objects of a certain type), namely:
      jumpers, pools, urls. <br />
      In this example we will not use the jumpers builder. <p />
      A pool is a collection of URL's, so we have a relation between the 'pools' and
      'urls' builders.
    </td>
    <td>
      <a href="<mm:url page="share/images/myyahoo_cloud.jpg" />" target="img">
        <img src="share/images/myyahoo_cloud.jpg" width="300" />
      </a>
    </td>
  </tr>
  <tr>
    <th>Manual</th>
    <th>Picture</th>
  </tr>
  <tr valign="top">
    <td>
      With the object cloud design described above you can create webpages like the one you see on the right (By clicking the MyYahoo url you can see the MyYahoo site in real action). There is a small searchengine that allows you to search for URL's in all categories.
    </td>
    <td><a href="<mm:url page="share/images/myyahoo_manual.jpg" />" target="img"><img src="share/images/myyahoo_manual.jpg" width="300" /></a></td>
  </tr>
</table>
  <div class="link">
    <a href="<mm:url page="." />"><img alt="back" src="<mm:url page="/mmbase/style/images/back.gif" />" /></a>
  </div>
</body>
</html>
</mm:cloud>
</mm:content>
