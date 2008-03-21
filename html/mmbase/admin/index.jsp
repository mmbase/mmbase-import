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

    <mm:cloud rank="basic user">
      <mm:import externid="category">about</mm:import>
      <mm:import externid="subcategory"></mm:import>
      <mm:import externid="component" />
      <mm:import externid="block" />
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
            <mm:present referid="block">
              <mm:component name="$component" block="$block" render="head" />
            </mm:present>
            <mm:notpresent referid="block">
              <mm:functioncontainer>
                <mm:param name="id">mmbase.${category}</mm:param>
                <mm:function set="components" name="blockClassification">
                  <c:set var="component" value="${_[0].blocks[0].component.name}" />
                  <c:set var="block" value="${_[0].blocks[0].name}" />
                  <mm:component name="$component" block="${block}" render="head" />
                </mm:function>
              </mm:functioncontainer>
            </mm:notpresent>
          </head>
        </mm:formatter>
        <mm:function id="blockObject" set="components" name="block" referids="component,block" write="false" />
        <body>
          <div id="header">
            <div id="logo"><a href="."><mm:link page="/mmbase/style/logo_trans.png"><img src="${_}" alt="MMBase" width="40" height="50" /></mm:link></a></div>
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
                      <mm:link id="link"><mm:frameworkparam name="category">${cat.name}</mm:frameworkparam></mm:link>
                      <li class="weight_${cat.weight}"><a class="${category eq cat.name ? 'selected' : ''}" href="${link}">${mm:string(cat.title)}</a>
                      <mm:compare referid="category" value="${cat.name}">
                        <ul>
                          <c:forEach var="b" items="${cat.blocks}">
                            <mm:link>
                              <mm:frameworkparam name="category">${category}</mm:frameworkparam>
                              <mm:frameworkparam name="component">${b.component.name}</mm:frameworkparam>
                              <mm:frameworkparam name="block">${b.name}</mm:frameworkparam>
                              <li class="${b.name eq block and subcat.component.name eq component ? 'current' : ''}">
                                <a title="${mm:string(b.description)}" href="${_}">${mm:string(b.title)}
                                <span class="component">(${b.component.name})</span>
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
              <c:catch var="exception">
                <h2 class="top">${mm:string(blockObject.title)}</h2>
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
