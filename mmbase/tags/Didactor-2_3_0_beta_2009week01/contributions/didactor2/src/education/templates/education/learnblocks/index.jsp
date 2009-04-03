<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <mm:content
      type="application/xml" postprocessor="none">
    <div
        class="content">
      <mm:cloud rank="didactor user">

        <mm:import externid="learnobject" required="true"/>

        <mm:treeinclude page="/education/storebookmarks.jsp" objectlist="$includePath"
                        referids="$referids,learnobject">
          <mm:param name="learnobjecttype">learnblocks</mm:param>
        </mm:treeinclude>

        <mm:node number="$learnobject">
          <div class="subnavigationPage">

            <di:include debug="html" page="/education/learnblocks/subnavigation.jspx" />
          </div>

          <di:background>
            <di:include debug="xml" page="/education/learnblocks/node.jspx" />

            <di:blocks classification="after_learnblock" />

          </di:background>

          <di:include page="/education/prev_next.jsp" />

          <!-- hmm: -->
          <jsp:directive.include file="../includes/component_link.jsp" />


        </mm:node>
      </mm:cloud>
    </div>
  </mm:content>
</jsp:root>
