<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />
  <mm:cloud method="delegate" authenticate="asis">
    <jsp:directive.include file="../mode.include.jsp" />
    <di:has editcontext="contentelementen">
      <a href="javascript:clickNode('content_metadata_0')">
        <img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle' id='img_content_metadata_0' />
      </a>
      <jsp:text>&amp;nbsp;</jsp:text>
      <img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/>
      <jsp:text>&amp;nbsp;</jsp:text>
      <nobr>
        <a href="javascript:clickNode('content_metadata_0')"
           title="${di:translate('education.educationmenucontentmetadata')}">
          <di:translate key="education.educationmenucontentmetadata" />
        </a>
      </nobr>
      <br /> <!-- brs are stupid -->
      <mm:import jspvar="langLocale"><mm:write referid="language" /></mm:import>
      <div id='content_metadata_0' style='display: none'>
        <mm:import id="types" vartype="list">images,attachments,audiotapes,videotapes,urls</mm:import>
        <c:forEach items="${types}" var="type">
          <mm:hasnodemanager name="${type}">
            <table border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
                <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
                <mm:link referid="listjsp">
                  <mm:param name="wizard"><mm:property name="wizard" nodemanager="${type}" /></mm:param>
                  <mm:param name="nodepath">${type}</mm:param>
                  <mm:param name="search">yes</mm:param>
                  <mm:param name="orderby"><mm:property name="orderby" nodemanager="${type}" /></mm:param>
                  <mm:param name="metadata">yes</mm:param>
                  <mm:param name="path"></mm:param>
                  <!--<mm:write referid="forbidtemplate" escape="text/plain" /> -->
                  <td>
                    <nobr>
                      &amp;nbsp;
                      <a href="${_}" title="${di:translate('education.edit')}"
                         target="text"><mm:nodeinfo type="guinodemanager" nodetype="${type}" />
                      </a>
                    </nobr>
                  </td>
                </mm:link>
              </tr>
            </table>
          </mm:hasnodemanager>
        </c:forEach>

        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
            <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
            <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
            <mm:link referid="listjsp">
              <mm:param name="wizard">config/provider/providers</mm:param>
              <mm:param name="nodepath">providers</mm:param>
              <mm:param name="searchfields">name</mm:param>
              <mm:param name="fields">name</mm:param>
              <mm:param name="orderyby">name</mm:param>
              <mm:param name="path"></mm:param>
              <td>
                <nobr>
                  &amp;nbsp;
                  <a href="${_}" target="text">
                  Content paginas (CMS)</a>
                </nobr>
              </td>
            </mm:link>
          </tr>
        </table>

        <mm:hasnode number="component.portalpages">
          <mm:treeinclude debug="html" page="/portalpages/backoffice/add_portalpages.jsp" objectlist="" referids="listjsp,wizardjsp" />
        </mm:hasnode>

        <mm:hasnode number="component.cmshelp">
          <mm:treeinclude debug="html" page="/cmshelp/backoffice/add_help.jsp" objectlist="" referids="listjsp,wizardjsp" />
        </mm:hasnode>

        <mm:hasnode number="component.faq">
          <mm:treeinclude debug="html" page="/faq/backoffice/add_faq.jsp" objectlist="" referids="listjsp,wizardjsp" />
        </mm:hasnode>

        <mm:hasnode number="component.news">
          <mm:treeinclude debug="html" page="/news/backoffice/add_news.jsp" objectlist="" referids="listjsp,wizardjsp" />
        </mm:hasnode>

      </div>
    </di:has>
  </mm:cloud>
</jsp:root>
