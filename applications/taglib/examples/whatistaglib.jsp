<%@page session="false" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html" language="en" expires="600">
<html>
<head>
  <title>What is MMBase taglib</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
  <mm:timer>
  <mm:import externid="color">green</mm:import><%-- stupid example for xsl arguments --%>

      <section>
        <h>What is MMBase Taglib?</h>
        <p>
          This question can be split up in two sub-questions.
        </p>
        <section>
          <h>What is MMBase?</h>
          <p>
            I think we can assume that we know what is MMBase, but here are some definitions:
          </p>
          <ul>
            <li>MMBase is an open source Content Managment System</li>
            <li>MMBase is a program that connects a database, which is filled by somebody else, to your webpages</li>
          </ul>  
        </section>
        <section>
          <h>What is a taglib?</h>
          <p>
            A taglib is a library of prefabricated functionality wrapped in an XML syntax, which can
            be used easily in other documents using XML (like) syntaxes like HTML.
          </p>
          <p>
            This is what we need, because we need to `enrich' our HTML with syntax to import the database data.
          </p>
          <p>
            The MMBase taglib is based on the MMBase 'bridge' (MMCI). This is java interface, which
            provides a clear and relatively simple way to use MMBase.
          </p>
          <p>
            Taglibs are very much associated with Java Server Pages (JSP)
          </p>
        </section>
      </section>
</mm:timer>
<hr />
<a href="<mm:url page="showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
<a href="http://java.sun.com/products/jsp/syntax/1.2/syntaxref12.html">JSP-syntax</a><br />
<a href="<mm:url page="index.jsp" />">Taglib examples</a><br />
<a href="<mm:url page="/mmdocs/frontenddevelopers/taglib/toc.html" />">Taglib documentation</a><br />

</body>
</html>
</mm:content>