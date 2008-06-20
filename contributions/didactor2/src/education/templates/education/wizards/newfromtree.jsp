<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />

  <mm:import externid="wizardjsp" from="request" />
  <mm:import externid="branchPath" required="true" />

  <di:getsetting setting="new_learnobjects" component="core" vartype="list" id="new_learnobjects" write="false" />

  <mm:stringlist referid="new_learnobjects">

    <mm:treehaspage
        page="/education/wizards/new/${_}.jspx"  objectlist="$includePath">

      <mm:treeinclude page="/education/wizards/new/${_}.jspx"
                      objectlist="$includePath"
                      debug="html"
                      referids="$referids,branchPath">
      </mm:treeinclude>
    </mm:treehaspage>
    <mm:treehaspage page="/education/wizards/new/${_}.jspx" objectlist="$includePath" inverse="true">
      <di:leaf>
        NO /education/wizards/new/${_}.jspx
      </di:leaf>
    </mm:treehaspage>
  </mm:stringlist>


</jsp:root>
