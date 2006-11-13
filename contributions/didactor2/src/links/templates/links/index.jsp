<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud rank="basic user">
  <jsp:directive.include file="/shared/setImports.jsp" />
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids" />
  <div class="rows">
    <div class="navigationbar">
      <div class="titlebar">
        <di:translate key="links.title" />
      </div>
    </div>
    <div class="contentBody">
      <mm:node referid="education">
        Education <mm:field name="name" />
        <mm:relatednodes type="urls">
          <p>
            <a href="${_node.url}"><mm:field name="name" /></a>
          </p>
        </mm:relatednodes>
      </mm:node>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>

