<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />
  <div
      xmlns="http://www.w3.org/1999/xhtml"
      class="content_metadata">
    <mm:cloud rank="basic user">
      <jsp:directive.include file="../mode.include.jsp" />

      <di:has editcontext="contentelementen">
        <nl class="filetree treeview">
          <a title="${di:translate('education.educationmenucontentmetadata')}">
            <di:translate key="education.educationmenucontentmetadata" />
          </a>
          <mm:import jspvar="langLocale"><mm:write referid="language" /></mm:import>
          <di:getsetting setting="metacontentobjects" component="core" vartype="list" id="types" write="false" />
          <c:forEach items="${types}" var="type">
            <mm:hasnodemanager name="${type}">
              <li>
                <di:icon name="learnblock" />
                <mm:link referid="listjsp">
                  <mm:param name="wizard">
                    <mm:property name="wizard" nodemanager="${type}" write="true"><mm:isempty>config/${type}/${type}</mm:isempty></mm:property>
                  </mm:param>
                  <mm:param name="nodepath">${type}</mm:param>
                  <mm:param name="search">yes</mm:param>
                  <mm:param name="orderby"><mm:property name="orderby" nodemanager="${type}" /></mm:param>
                  <mm:param name="metadata">yes</mm:param>
                  <mm:param name="path"></mm:param>
                  <!--<mm:write referid="forbidtemplate" escape="text/plain" /> -->
                  <a href="${_}" title="${di:translate('education.edit')}"
                     target="text"><mm:nodeinfo type="guinodemanager" nodetype="${type}" />
                  </a>
                </mm:link>
              </li>
            </mm:hasnodemanager>
          </c:forEach>

          <li>
            <di:icon name="learnblock" />
            <mm:link referid="listjsp">
              <mm:param name="wizard">config/provider/providers</mm:param>
              <mm:param name="nodepath">providers</mm:param>
              <mm:param name="searchfields">name</mm:param>
              <mm:param name="fields">name</mm:param>
              <mm:param name="orderyby">name</mm:param>
              <mm:param name="path"></mm:param>
              <a href="${_}" target="text">
              Content paginas (CMS)</a>
            </mm:link>
          </li>

          <mm:hasnode number="component.portalpages">
            <di:include  page="/portalpages/backoffice/add_portalpages.jsp" />
          </mm:hasnode>

          <di:blocks classification="didactor.metadata" />

          <mm:hasnode number="component.faq">
            <!-- sucks too much -->
            <!--
            <di:include  page="/faq/backoffice/add_faq.jsp" />
            -->
          </mm:hasnode>

          <!--

              follow a few more components which produce horribleness and must be cleaned.

              <mm:hasnode number="component.cmshelp">
              <mm:treeinclude debug="html" page="/cmshelp/backoffice/add_help.jsp" objectlist="" referids="listjsp,wizardjsp" />
              </mm:hasnode>


              <mm:hasnode number="component.news">
              <mm:treeinclude debug="html" page="/news/backoffice/add_news.jsp" objectlist="" referids="listjsp,wizardjsp" />
              </mm:hasnode>
          -->
        </nl>
      </di:has>
    </mm:cloud>
  </div>
</jsp:root>
