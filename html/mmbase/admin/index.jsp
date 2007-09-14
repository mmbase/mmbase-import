<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:c="http://java.sun.com/jsp/jstl/core" 
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0">

  <!-- actually just to help some browsers: -->
  <jsp:output doctype-root-element="html"
              doctype-public="-//W3C//DTD XHTML 1.1 Strict//EN"
              doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml11-strict.dtd"/>

  <mm:content expires="0"
              type="application/xhtml+xml"
              unacceptable="CRIPPLE"
              postprocessor="none" language="client">

    <mm:cloud rank="administrator" >
      <mm:import externid="category">about</mm:import>
      <mm:import externid="subcategory"></mm:import>
      <mm:import externid="component">core</mm:import>
      <mm:import externid="block">welcome</mm:import>
      <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl">
        <mm:formatter xslt="xslt/framework/head.xslt" escape="none">
          <head>
            <title>MMBase<mm:present referid="category"> - <mm:write referid="category" /></mm:present><mm:present referid="block"> : <mm:write referid="block" /></mm:present></title>
            <mm:link page="/mmbase/admin/css/admin.css">
              <link rel="stylesheet" href="${_}" type="text/css" />
            </mm:link>
            <mm:link page="/mmbase/style/images/favicon.ico">
              <link rel="icon" href="${_}" type="image/x-icon" />
              <link rel="shortcut icon" href="${_}" type="image/x-icon" />
            </mm:link>
            <mm:component name="$component" block="$block" render="head" />
          </head>
        </mm:formatter>
        <body>
          <div id="header">
            <div id="logo"><a href="."><mm:link page="/mmbase/style/logo.png"><img src="${_}" alt="MMBase" width="40" height="50" /></mm:link></a></div>
            <div id="head">
              <h1>MMBase</h1>
              <p>
                You are logged in as: <mm:cloudinfo type="user" /> (rank: <mm:cloudinfo type="user" />) |
                <mm:link page="/mmbase/admin/logout.jsp"><a href="${_}">logout</a></mm:link>
              </p>
            </div>
          </div>
          <div id="wrap">
            <div id="navigation">
              <ul>
                <mm:functioncontainer>
                  <mm:param name="id">mmbase</mm:param>
                  <mm:listfunction set="components" name="blockClassification">
                    <mm:stringlist referid="_.subTypes" id="cat">
                      <mm:link  id="link"><mm:frameworkparam  name="category">${cat.name}</mm:frameworkparam></mm:link>
                      <li><a class="${category eq cat.name ? 'selected' : ''}" href="${link}">${cat.name}</a>
                      
                      <mm:compare referid="category" value="${cat.name}">
                        <ul>
                          <c:forEach var="subcat" items="${cat.blocks}">
                            <mm:link>
                              <mm:frameworkparam name="category">${category}</mm:frameworkparam>
                              <mm:frameworkparam name="component">${subcat.component.name}</mm:frameworkparam>
                              <mm:frameworkparam  name="block">${subcat.name}</mm:frameworkparam>
                              <li class="${subcat.name eq block and subcat.component.name eq component ? 'current' : ''}">
                                <a title="${subcat.description}" href="${_}">${subcat.name}
                                <span class="component">(${subcat.component.name})</span>
                                </a>
                              </li>
                            </mm:link>
                          </c:forEach>
                        </ul>
                      </mm:compare>
                      </li>
                    </mm:stringlist>
                  </mm:listfunction>
                </mm:functioncontainer>
              </ul>
            </div>
            <div id="content">
              <div class="padder">
                <mm:present referid="component">
                  <h2 class="top"><mm:write referid="block" /></h2>
                  <c:catch var="exception">
                    <mm:component debug="xml" name="$component" block="$block">
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
                </mm:present>
              </div>
            </div>
            <div id="footer">
              <ul>
                <li><a href="http://www.mmbase.org">www.mmbase.org</a></li>
                <li><a href="http://www.mmbase.org/license">license</a></li>
                <li><a href="http://www.mmbase.org/mmdocs">mmdocs</a></li>
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