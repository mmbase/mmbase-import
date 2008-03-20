<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <mm:cloud method="asis">

    <div class="rows">
      <div class="navigationbar">
        <div class="pathbar">
          <mm:node number="$provider">
            <mm:field name="name"/>
          </mm:node>
        </div>
        <div class="stepNavigator">
          <di:include page="/education/prev_next.jsp" />
        </div>
      </div>

      <div class="folders">
        <div class="folderLesBody">
          <di:include page="/portalpages/frontoffice/tree.jspx" />
        </div>

      </div>
    </div>
  </mm:cloud>
</jsp:root>
