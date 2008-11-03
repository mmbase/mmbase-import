<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0">

  <!-- actually just to help some browsers: -->
  <jsp:output doctype-root-element="html"
              doctype-public="-//W3C//DTD XHTML 1.1 Strict//EN"
              doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml11-strict.dtd"/>


  <!--
       During debugging of a certain block, you may want to use this render jsp for the MMBaseUrlConverter.
       It avoids all clutter, and you can keep the log as clean as possible.
  -->

  <mm:content expires="0"
              type="application/xhtml+xml"
              unacceptable="CRIPPLE"
              postprocessor="none" language="client">

    <mm:cloud rank="basic user">
      <mm:import externid="category">about</mm:import>
      <mm:import externid="subcategory"></mm:import>
      <mm:import externid="component" />
      <mm:import externid="block" />
      <html xmlns="http://www.w3.org/1999/xhtml" >
        <body>

          <div id="content">
            <c:catch var="exception">
              <mm:component debug="xml" name="$component" block="${block}">
                <mm:frameworkparam name="category">${category}</mm:frameworkparam>
              </mm:component>
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
        </body>
      </html>
    </mm:cloud>
  </mm:content>
</jsp:root>
