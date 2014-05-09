<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8" pageEncoding="UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" 
%><html>
  <head>
    <title>MMBase Taglib and Locales</title>
    <link rel="stylesheet" type="text/css" href="style.css" />
  </head>
  <body>
    <%@ include file="menu.jsp"%>
    <h1>Locales</h1>
    <p>
      The support for Locales (language-related issues) is relatively good in MMBase and MMBase taglib.
    </p>
    <table>
      <tr><th colspan="2">If you don't specify anything, and use an MMBase tag which produces an internationalized message, the default MMBase locale is used</th></tr>
      <tr valign="top">
        <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/locale.nospec.jsp" cite="true" /></mm:formatter></pre></td>
        <td width="50%"><%@include file="codesamples/locale.nospec.jsp" %></td>
      </tr>
      <tr><th colspan="2">You can override this with a mm:locale or mm:content tag</th></tr>
      <tr valign="top">
        <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/locale.localetag.jsp" cite="true" /></mm:formatter></pre></td>
        <td width="50%"><%@include file="codesamples/locale.localetag.jsp" %></td>
      </tr>
      <tr><th colspan="2">That can also be done 'locally'</th></tr>
      <tr valign="top">
        <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/locale.localetag.local.jsp" cite="true" /></mm:formatter></pre></td>
        <td width="50%"><%@include file="codesamples/locale.localetag.local.jsp" %></td>
      </tr>
      <tr><th colspan="2">The settings also influence JSTL fmt-tags.</th></tr>
      <tr valign="top">
        <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/locale.fmt.jsp" cite="true" /></mm:formatter></pre></td>
        <mm:context>
          <td width="50%"><%@include file="codesamples/locale.fmt.jsp" %></td>
        </mm:context>
      </tr>

      <tr><th colspan="2">The settings also remain persistent in the request, which means that you don't have to worry when using mm:inlude, jsp:include or a tag-file</th></tr>
      <tr valign="top">
        <td width="50%">
        <pre><mm:formatter format="escapexml"><mm:include page="codesamples/locale.include.jsp" cite="true" /></mm:formatter></pre>
        with "codesamples/locale.included.jsp":
        <pre><mm:formatter format="escapexml"><mm:include page="codesamples/locale.included.jsp" cite="true" /></mm:formatter></pre>
        </td>
        <td width="50%"><%@include file="codesamples/locale.include.jsp" %></td>
      </tr>

      <tr><th colspan="2">If no locale or content-tag, and the locale-setting of the cloud is not default, it still is used.</th></tr>
      <tr valign="top">
        <td width="50%">
        <pre><mm:formatter format="escapexml"><mm:include page="codesamples/locale.cloud.jsp" cite="true" /></mm:formatter></pre>
        with "codesamples/locale.node.jsp":
        <pre><mm:formatter format="escapexml"><mm:include page="codesamples/locale.node.jsp" cite="true" /></mm:formatter></pre>
        </td>
        <td width="50%"><%@include file="codesamples/locale.cloud.jsp" %></td>
      </tr>
    </table>
  </body>
</html>
