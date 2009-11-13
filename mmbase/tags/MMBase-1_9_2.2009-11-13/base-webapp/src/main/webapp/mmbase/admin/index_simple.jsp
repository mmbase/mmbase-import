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
              type="text/html"
              unacceptable="CRIPPLE"
              postprocessor="none" language="client">

    <mm:cloud rank="basic user">
      <html xmlns="http://www.w3.org/1999/xhtml" >
        <body>

          <div id="content">
            <p>${param.component} / ${param.block} / ${param.category}</p>
            <mm:component debug="xml" name="${param.component}" block="${param.block}">
              <mm:frameworkparam name="category">${param.category}</mm:frameworkparam>
            </mm:component>
          </div>
        </body>
      </html>
    </mm:cloud>
  </mm:content>
</jsp:root>
