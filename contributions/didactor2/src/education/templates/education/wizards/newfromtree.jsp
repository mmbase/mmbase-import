<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di-t="urn:jsptagdir:/WEB-INF/tags/di/core"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />

  <mm:import externid="wizardjsp" from="request" />
  <mm:import externid="branchPath" />

  <di:getsetting setting="new_learnobjects" component="core" vartype="list" id="new_learnobjects" write="false" />
  <mm:stringlist referid="new_learnobjects">

    <mm:haspage page="/education/wizards/new/${_}.jspx">
      <mm:treeinclude page="/education/wizards/new/${_}.jspx"
                      objectlist="$includePath"
                      referids="$referids,depth?">
      </mm:treeinclude>
    </mm:haspage>
  </mm:stringlist>


</jsp:root>
