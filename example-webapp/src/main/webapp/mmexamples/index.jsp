<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page session="false"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:content type="text/html">
<mm:cloud>
<html>
<head>
  <title>MMBase Demos</title>
  <link rel="stylesheet"    href="<mm:url page="/mmbase/style/css/mmbase.css" />"     type="text/css" />
  <link rel="icon"          href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
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
          This page is deprecated. Please refer to
          <mm:link>
            <mm:frameworkparam name="component">mmexamples</mm:frameworkparam>
            <mm:frameworkparam name="category">examples</mm:frameworkparam>
            <a href="${_}">the new Examples page</a>.
          </mm:link>
          since most of them are rewritten to be used as components for the MMBase Component Framework.
          The links to the older JSP examples on this page, which are as of yet not rewritten, still work.
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
      <td>Taglib</td>
      <td>
        A lot of different examples for the MMBase taglib.
      </td>
      <td class="link" >
        <a href="<mm:url page="taglib/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.png" />"  /></a>
      </td>
    </tr>

    <tr>
      <td>Editors</td>
      <td>
        All generic editors are of course very complex examples of jsp-pages.
      </td>
      <td class="link" >
        <a href="<mm:url page="/mmbase/admin/editors/basic.jsp" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.png" />" /></a>
      </td>
    </tr>

    <tr>
      <td>Example editors</td>
      <td>
        Real generic editors are often a bit pragmaticly ('dirty') implemented though. Here are very simple
        edit pages, which did no compromises whatsoever. This results in clean and easy-to-understand
        JSPX-code. On the other hand they do e.g. not work in Internet Explorer, and lack all kind of features.
      </td>
      <td class="link" >
        <a href="<mm:url page="/mmbase/edit/x/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.png" />" /></a>
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
      <td>Codings</td>
      <td>
        Shows text in different encodings.
      </td>
      <td class="link" >
        <a href="<mm:url page="codings/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.png" />" /></a>
      </td>
    </tr>


   </table>
  <div class="link">
    <a href="<mm:url page=".." />"><img alt="back" src="<mm:url page="/mmbase/style/images/back.png" />" /></a>
  </div>
</body>
</html>
</mm:cloud>
</mm:content>
