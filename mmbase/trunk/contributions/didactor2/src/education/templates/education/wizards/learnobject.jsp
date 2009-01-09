<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di-t="urn:jsptagdir:/WEB-INF/tags/di/core"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />
  <mm:import externid="startnode" required="true" />
  <mm:import externid="branchPath" required="true" />

  <mm:content postprocessor="none">
    <mm:cloud method="delegate">
      <mm:node id="sn" number="${startnode}">
        <li>

          <mm:write request="b" value="" />
          <mm:include page="newfromtree.jsp">
            <mm:param name="branchPath">${branchPath}</mm:param>
          </mm:include>


          <mm:treecontainer
              type="learnobjects"
              role="posrel"
              searchdirs="destination"
              >
            <mm:sortorder field="posrel.pos" direction="up" />
            <mm:typeconstraint name="questions" inverse="true" />
            <mm:tree
                varBranchStatus="status"
                maxdepth="6">
              <mm:first inverse="true">
                <mm:nodeinfo type="type">
                  <mm:treehaspage page="/education/wizards/show/${_}.jspx" objectlist="$includePath">
                    <li>
                      <mm:treeinclude page="/education/wizards/show/${_}.jspx"
                                      objectlist="$includePath"
                                      attributes="status@b"
                                      referids="branchPath"
                                      debug="html"
                                      />
                    </li>
                  </mm:treehaspage>
                  <mm:treehaspage page="/education/wizards/show/${_}.jspx"
                                  objectlist="$includePath"
                                  inverse="true">
                    <mm:treeinclude page="/education/wizards/showlearnobject.jsp"
                                    objectlist="$includePath"
                                attributes="status@b"
                                debug="html"
                                referids="branchPath"
                                >
                    </mm:treeinclude>
                  </mm:treehaspage>
                </mm:nodeinfo>
              </mm:first>
              <mm:shrink />
            </mm:tree>
          </mm:treecontainer>
        </li>
      </mm:node>
    </mm:cloud>
  </mm:content>
</jsp:root>
