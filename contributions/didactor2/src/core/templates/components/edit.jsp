<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:content postprocessor="none" expires="0">
    <mm:cloud rank="basic user" >
      <mm:import externid="component" />
      <html>
        <head>
          <link rel="stylesheet" type="text/css" href="${mm:treelink('/css/base.css', includePath)}" />
        </head>
        <body>
          <div style="margin-left: 10px">
            <di:hasrole role="systemadministrator">
              <di:component number="${component}">
                <h1>${_component.name}</h1>
                <p>Implementing class: ${_component.class}</p>
                <c:choose>
                  <c:when test="${empty _component.templateBar}">
                    <p>This component is not shown in the navigation bars.</p>
                  </c:when>
                  <c:otherwise>
                    <p>
                      Cockpit bar: ${_component.templateBar} <br />
                      Position in cockpit bar: ${_component.barPosition}
                    </p>
                  </c:otherwise>
                </c:choose>
                <form method="post">
                  <div class="scope collapsable" id="settings_${_component.number}">
                    <di:settings scope="component"/>
                  </div>
                  <c:if test="${mm:contains(_component.scopes, 'providers')}">
                    <h2>Providers</h2>
                    <ul class="scope providers collapsable" id="component_${_component.number}">
                      <mm:listnodes type="providers">
                        <li>
                          <div class="settings collapsable" id="settings_${_component.number}">
                            <di:settings scope="providers" number="${_node}">
                              <mm:field name="name" />
                            </di:settings>
                          </div>
                          <c:if test="${mm:contains(_component.scopes, 'educations')}">
                            <ul class="scope educations collapsable" id="educations_${_component.number}">
                              <mm:relatednodes type="educations">
                                <li>
                                  <div class="settings collapsable" id="education_${_node}">
                                    <di:settings scope="educations" number="${_node}">
                                      <mm:field name="name" />
                                    </di:settings>
                                  </div>
                                  <c:if test="${mm:contains(_component.scopes, 'classes')}">
                                    <ul class="scope classes collapsable" id="classes_${_node}">
                                      <mm:relatednodes type="classes">
                                        <li>
                                          <div class="settings collapsable" id="class_${_node}">
                                            <di:settings scope="classes" number="${_node}">
                                              <mm:field name="name" />
                                            </di:settings>
                                          </div>
                                        </li>
                                      </mm:relatednodes>
                                    </ul>
                                  </c:if>
                                </li>
                              </mm:relatednodes>
                            </ul>
                          </c:if>
                        </li>
                      </mm:listnodes>
                    </ul>
                  </c:if>
                  <input type="submit" />
                </form>
              </di:component>
            </di:hasrole>
          </div>
        </body>
      </html>
    </mm:cloud>
  </mm:content>
</jsp:root>
