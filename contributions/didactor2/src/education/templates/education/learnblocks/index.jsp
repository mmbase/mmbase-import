<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <mm:content
      type="application/xml" postprocessor="none">
    <div class="content">
      <mm:cloud rank="anonymous">

        <mm:import externid="learnobject" required="true"/>

        <mm:treeinclude page="/education/storebookmarks.jsp" objectlist="$includePath" referids="$referids,learnobject">
          <mm:param name="learnobjecttype">learnblocks</mm:param>
        </mm:treeinclude>

        <div class="learnenvironment">
          <mm:node number="$learnobject">
            <mm:nodeinfo type="type">
              <mm:treehaspage objectlist="${includePath}" page="/education/pages/${_}/index.jspx">
                <mm:treeinclude page="/education/pages/${_}/index.jspx"
                                objectlist="$includePath"
                                referids="$referids,learnobject" />
              </mm:treehaspage>
              <mm:treehaspage objectlist="${includePath}" page="/education/pages/${_}/index.jspx" inverse="true">
                <mm:treeinclude page="/education/pages/content.jsp"
                                objectlist="$includePath"
                                referids="$referids,learnobject" />
                <mm:treeinclude page="/education/paragraph/paragraph.jsp" objectlist="$includePath" referids="$referids,learnobject@node_id">
                  <mm:param name="path_segment">${pageContext.request.contextPath}/education/</mm:param>
                </mm:treeinclude>
              </mm:treehaspage>
            </mm:nodeinfo>
          </mm:node>

          <jsp:directive.include file="../includes/descriptionrel_link.jsp" />
          <mm:treeinclude page="/education/prev_next.jsp" referids="includePath" objectlist="$includePath" />

        </div>
        <mm:node number="$learnobject">
          <jsp:directive.include file="../includes/component_link.jsp" />
        </mm:node>
      </mm:cloud>
    </div>
  </mm:content>
</jsp:root>
