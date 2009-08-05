<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <mm:content
      type="application/xml"
      expires="0"
      postprocessor="reducespace">

    <div class="content">
      <mm:cloud rank="didactor user">

        <mm:import externid="learnobject" required="true"/>

        <di:event eventtype="visit_page" eventvalue="${learnobject}" note="visit page"/>

        <!-- TODO Where to display images, audiotapes, videotapes and urls -->
        <!-- TODO How to display objects -->
        <div class="learnenvironment">
          <mm:treeinclude
              debug="html"
              page="/education/pages/content.jsp" objectlist="$includePath"
              referids="$referids,learnobject" />

          <mm:node number="$learnobject">
            <mm:treeinclude page="/education/paragraph/paragraph.jsp" objectlist="$includePath" referids="$referids,learnobject@node_id">
              <mm:param name="path_segment">${pageContext.request.contextPath}/education/</mm:param>
            </mm:treeinclude>
          </mm:node>

          <di:blocks classification="after_page" />

          <!--
          <mm:node number="$learnobject" jspvar="nodeLearnObject">
            <mm:hasrelationmanager sourcemanager="$_node" destinationmanager="components" role="rolerel">
              <jsp:directive.include file="../includes/component_link.jsp" />
            </mm:hasrelationmanager>
          </mm:node>
          -->


          <mm:treeinclude page="/education/prev_next.jsp" referids="includePath,provider" objectlist="$includePath" />

        </div>
      </mm:cloud>
    </div>
  </mm:content>
</jsp:root>
