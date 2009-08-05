<jsp:root
    version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />

  <mm:import externid="listjsp"   from="request" required="true"/>
  <mm:import externid="wizardjsp" from="request" required="true"/>
  <mm:import from="request" externid="referrer" />

  <mm:listnodes id="relatedcontainers" path="educations,posrel,portalpagescontainers" element="portalpagescontainers" />

  <mm:listnodescontainer type="portalpagescontainers" id="q">
    <mm:constraint field="number" operator="IN" inverse="true"  value="${relatedcontainers}" />
    <mm:maxnumber value="1" />
    <mm:listnodes>
      <li>
        <span class="folder">
          <mm:link referid="listjsp" referids="containernode@origin,referrer">
            <mm:param name="wizard">config/portalpages/rootportalpagesnodes</mm:param>
            <mm:param name="nodepath">portalpagesnodes</mm:param>
            <mm:param name="fields">name</mm:param>
            <mm:param name="searchfields">name</mm:param>
            <mm:param name="metadata">yes</mm:param>
            <a href="${_}" title="portal pagina's" target="text">Portal Pagina's</a> <!-- I18N ? -->
          </mm:link>
        </span>
        <mm:include page="leaf.jspx" />
      </li>
    </mm:listnodes>
  </mm:listnodescontainer>

  <mm:listnodes id="e" type="educations" varStatus="status">
    <li>
      <span class="folder">
        <mm:link referid="wizardjsp" referids="_node@objectnumber,referrer">
          <mm:param name="wizard">config/portalpages/posrel-portalpagescontainers</mm:param>
          <a href="${_}" target="text">Portal pages for <mm:nodeinfo type="gui" /></a>
        </mm:link>
      </span>
      <ul class="filetree">
        <mm:relatednodes role="posrel" type="portalpagescontainers">
          <li>
            <span class="folder">
              <mm:field name="name" />
            </span>
            <mm:include page="leaf.jspx" />
          </li>
        </mm:relatednodes>
      </ul>
    </li>
  </mm:listnodes>
</jsp:root>
