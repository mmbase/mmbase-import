<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <di:html
      styleClass="configuration"
      expires="0" component="core"
      title_key="core.configuration">
    <div class="rows">
      <div class="navigationbar">
        <div class="titlebar">
          <img src="${mm:treelink('/gfx/icon_settings.gif', includePath)}" width="25" height="13"
               border="0"
               title="${di:translate('core.configuration')}"
               alt="${di:translate('core.configuration')}"
               />
          <di:translate key="core.configuration" />
        </div>
      </div>
      <div class="folders">
        <div class="folderHeader">
          <jsp:text> </jsp:text>
        </div>
        <div class="folderBody">
          <jsp:text> </jsp:text>
        </div>
      </div>
      <div class="mainContent">
        <div class="contentHeader">
          <jsp:text> </jsp:text>
        </div>
        <div class="contentBodywit">
          <di:include page="/admin/body.jsp" />
        </div>
      </div>
    </div>
  </di:html>
</jsp:root>
