<jsp:root version="2.0"
          xmlns:t="urn:jsptagdir:/WEB-INF/tags/mm/u"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <!--
      xmlns:di-t="urn:jsptagdir:/WEB-INF/tags/di/core"
  -->
  <di:html
      css="/register/css/register.css"
      type="text/html"
      rank="anonymous"
      title_key="register.registration"
      component="register">

    <div class="columns">
      <div class="columnLeft"><jsp:text> </jsp:text></div>

      <div class="columnMiddle">
        <h2><di:translate key="register.registration" /></h2>

        <mm:import id="path">
          <di:include page="/register/path.jsp" />
        </mm:import>

        <t:tabs
            styleClass="registerTable"
            directory="/register/"
            objectlist="${includePath}"
            path="${path}" />

      </div>

    </div>

  </di:html>
</jsp:root>
