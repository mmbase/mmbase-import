<jsp:root
    version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />
  <mm:import externid="startnode" required="true" />

  <mm:content postprocessor="none">
    <mm:cloud method="delegate">
      <mm:node id="sn" number="${startnode}">
        <mm:write request="b" value="" />
        <mm:include page="newfromtree.jsp" />


        <mm:relatednodescontainer
            type="learnobjects"
            role="posrel"
            searchdirs="destination"
            >
          <mm:sortorder field="posrel.pos" direction="up" />
          <mm:typeconstraint name="questions" inverse="true" />
          <mm:relatednodes>
            <li>
              <mm:nodeinfo type="type">
                <mm:treehaspage page="/education/wizards/show/${_}.jspx" objectlist="$includePath">
                  <mm:treeinclude page="/education/wizards/show/${_}.jspx"
                                  objectlist="$includePath"
                                  debug="html"
                                  />
                </mm:treehaspage>
                <mm:treehaspage page="/education/wizards/show/${_}.jspx"
                                objectlist="$includePath"
                                inverse="true">
                  <mm:treeinclude page="/education/wizards/showlearnobject.jsp"
                                  objectlist="$includePath"
                                  debug="html"
                                  >
                  </mm:treeinclude>
                </mm:treehaspage>
              </mm:nodeinfo>
            </li>
          </mm:relatednodes>
        </mm:relatednodescontainer>
      </mm:node>
    </mm:cloud>
  </mm:content>
</jsp:root>
