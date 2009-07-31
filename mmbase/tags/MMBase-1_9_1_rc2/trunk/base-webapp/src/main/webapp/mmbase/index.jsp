<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0">

  <!-- actually just to help some browsers: -->
  <jsp:output doctype-root-element="html"
              doctype-public="-//W3C//DTD XHTML 1.1//EN"
              doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"/>

  <mm:content expires="0"
              type="application/xhtml+xml"
              unacceptable="CRIPPLE"
              postprocessor="none" language="client">

    <mm:cloud>
      <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl">
        <mm:formatter xslt="xslt/framework/head.xslt" escape="none">
          <head>
            <title>MMBase</title>
            <mm:link page="/mmbase/admin/css/admin.css">
              <link rel="stylesheet" href="${_}" type="text/css" />
            </mm:link>
            <mm:link page="/mmbase/style/images/favicon.ico">
              <link rel="icon" href="${_}" type="image/x-icon" />
              <link rel="shortcut icon" href="${_}" type="image/x-icon" />
            </mm:link>
          </head>
        </mm:formatter>
        <body>
          <div id="header">
            <div id="logo"><a href="."><mm:link page="/mmbase/style/logo_trans.png"><img src="${_}" alt="MMBase" width="40" height="50" /></mm:link></a></div>
            <div id="head">
              <h1>MMBase</h1>
              <p>Content Management System</p>
            </div>
           </div>
          <div id="wrap">
             <div id="navigation">
              <ul>
                <li class="weight_100">
                  <mm:link page="/mmbase"><a class="selected" href="${_}">Welcome</a></mm:link>
                </li>
                <li class="weight_100">
                  <mm:link page="/mmbase/edit/basic"><a href="${_}">Basic editors</a></mm:link>
                </li>
                <li class="weight_100">
                  <mm:link page="/mmbase/edit/my_editors"><a href="${_}">my_editors</a></mm:link>
                </li>
                <li class="weight_100">
                  <mm:link page="/mmbase/admin"><a href="${_}">Admin pages</a></mm:link>
                </li>
                <li class="weight_100">
                  <mm:link>
                    <mm:frameworkparam name="component">mmexamples</mm:frameworkparam>
                    <mm:frameworkparam name="category">examples</mm:frameworkparam>
                    <a href="${_}">Examples</a>
                  </mm:link>
                </li>
              </ul>
             </div>
             <div id="content">
               <mm:blocks classification="mmbase.index.default" />
              <c:catch var="exception">
                <mm:component debug="xml" name="core" block="welcome" />
              </c:catch>
              <c:if test="${! empty exception}">
                <pre>
                  ${mm:escape('text/xml', exception)}
                </pre>
                <pre>
                  ${mm:escape('text/xml', exception.cause.cause.cause)}
                  ${mm:escape('text/xml', exception.cause.cause)}
                  ${mm:escape('text/xml', exception.cause)}
                </pre>
              </c:if>
            </div>
            <div id="footer">
              <ul>
                <li><a href="http://www.mmbase.org">www.mmbase.org</a></li>
                <li><a href="http://www.mmbase.org/license">license</a></li>
                <li><a href="http://www.mmbase.org/mmdocs">documentation</a></li>
                <li><a href="http://www.mmbase.org/bugs">bugs</a></li>
                <li><a href="http://www.mmbase.org/contact">contact</a></li>
              </ul>
            </div>
          </div><!-- /#wrap -->
        </body>
      </html>
    </mm:cloud>
  </mm:content>
</jsp:root>
