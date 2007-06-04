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
        <h1><mm:field name="name" /> - <di:translate key="links.title" /></h1>
        <mm:relatednodes type="pools" orderby="posrel.pos" role="posrel">
          <h2><mm:field name="name" /></h2>
          <mm:field name="description" escape="p" />
          <ul>
            <mm:relatednodes type="urls" role="posrel" orderby="posrel.pos">
              <li>
                <a href="${_node.url}">
                  <mm:field name="name" write="true">
                    <mm:isempty>
                      <mm:field name="url" />
                    </mm:isempty>
                  </mm:field>.
                </a>
              </li>
            </mm:relatednodes>
          </ul>
        </mm:relatednodes>
      </mm:node>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>

