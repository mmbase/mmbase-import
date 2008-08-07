<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />
  <mm:cloud>
    <mm:import externid="listjsp"   from="request" required="true"/>
    <mm:import externid="wizardjsp" from="request" required="true"/>
    <mm:import from="request" externid="referrer" />
    <mm:import id="kupu_back"><mm:url page="${referrer}" /></mm:import>

    <mm:import id="nodes_exist" reset="true">false</mm:import>
    <mm:listnodes type="portalpagesnodes">
      <mm:import id="nodes_exist" reset="true">true</mm:import>
    </mm:listnodes>

    <mm:listnodes type="portalpagescontainers">
      <mm:first>
        <!--- WTF WTF WTF. This stuff is HORRIBLE. Taking a random node. Perhaps there is only one of this type, or so? -->
        <mm:node id="containernode" />
      </mm:first>
    </mm:listnodes>

    <mm:present referid="containernode">
      <di:leaf
          branchPath="."
          click="portal_root">
          <mm:link referid="listjsp" referids="containernode@origin">
            <mm:param name="wizard">config/portalpages/rootportalpagesnodes</mm:param>
            <mm:param name="nodepath">portalpagesnodes</mm:param>
            <mm:param name="fields">name</mm:param>
            <mm:param name="searchfields">name</mm:param>
            <mm:param name="metadata">yes</mm:param>
            <td><nobr><a href="${_}" title="portal pagina's" target="text">Portal Pagina's</a></nobr></td> <!-- I18N ? -->
          </mm:link>
      </di:leaf>
      <div id="portal_root">
        <di:leaf
            icon="new_education"
            branchPath=".."
            >
          <mm:link referid="wizardjsp" referids="containernode@origin">
            <mm:param name="wizard">config/portalpages/rootportalpagesnodes</mm:param>
            <mm:param name="objectnumber">new</mm:param>
            <td><a href="${_}" title="nieuwe map" target="text">nieuwe map</a></td>
          </mm:link>
        </di:leaf>
        <mm:listnodes type="portalpagescontainers">
          <mm:relatednodescontainer type="portalpagesnodes" searchdirs="destination" >
            <mm:sortorder field="number" direction="up" />
            <mm:relatednodes varStatus="status">
              <di:leaf
                  click="portal_node_${_node}"
                  branchPath="..${status.last ? '.' : ' '}"
                  >
                <mm:link referid="wizardjsp" referids="_node@objectnumber">
                  <mm:param name="wizard">config/portalpages/portalpagesnodes</mm:param>
                  <td><a href="${_}" title="edit" target="text"><mm:field name="name"/></a></td>
                </mm:link>
              </di:leaf>
              <div id="portal_node_${_node}" style="display:none">

                 <di:getsetting setting="new_contents" component="portalpages" vartype="list" id="new_contents" write="false" />
                 <mm:stringlist referid="new_contents">
                   <c:choose>
                     <c:when test="${_ eq 'portalpagesnodes'}">
                       <di:leaf
                           icon="new_education"
                           branchPath="..${status.last ? '.' : ' '} "
                           >
                         <mm:link referid="wizardjsp" referids="_node@origin">
                           <mm:param name="wizard">config/portalpages/leafmapportalpages-origin</mm:param>
                           <mm:param name="objectnumber">new</mm:param>
                           <td><a href="${_}" title="nieuwe map" target="text">nieuwe map</a></td>
                         </mm:link>
                       </di:leaf>
                     </c:when>
                     <c:when test="${_ eq 'simplecontents'}">
                       <di:leaf
                           icon="new_education"
                           branchPath="..${status.last ? '.' : ' '} "
                           >
                         <mm:link referid="wizardjsp" referids="_node@origin">
                           <mm:param name="wizard">config/portalpages/newsimplecontents</mm:param>
                           <mm:param name="objectnumber">new</mm:param>
                           <a href="${_}" title="nieuwe content" target="text">nieuwe content</a>
                         </mm:link>
                       </di:leaf>
                     </c:when>
                     <c:when test="${_ eq 'simplexmlcontents'}">


                       <di:leaf
                           icon="kupu_icon"
                           branchPath="..${status.last ? '.' : ' '} "
                           >
                         <mm:link page="/mmbase/kupu/mmbase" referids="_node@origin,referrer,kupu_back">
                           <mm:param name="templates">/editwizards/data</mm:param>
                           <mm:param name="wizard">config/portalpages/simplexmlcontents</mm:param>
                           <mm:param name="link_nodetypes">${di:setting('richtext', 'link_nodetypes')}</mm:param>
                           <mm:param name="language">${locale.language}</mm:param>
                           <mm:param name="objectnumber">new</mm:param>
                           <a href="${_}" title="nieuwe content" target="text">nieuwe content</a>
                         </mm:link>
                       </di:leaf>
                     </c:when>
                     <c:otherwise>
                       <di:leaf
                           icon=""
                           branchPath="..${status.last ? '.' : ' '} "
                           >
                         UNKNONWN ${_}
                       </di:leaf>
                     </c:otherwise>
                   </c:choose>
                 </mm:stringlist>

                <mm:treeinclude page="/portalpages/backoffice/related_portalpagesnodes.jsp" objectlist="${includePath}">
                  <mm:param name="branchPath">..${status.last ? '.' : ' '}</mm:param>
                </mm:treeinclude>

              </div>
            </mm:relatednodes>
          </mm:relatednodescontainer>
        </mm:listnodes>
      </div>
    </mm:present>
  </mm:cloud>
</jsp:root>
