<mm:node element="urls">
  <mm:field name="url">
    <mm:compare regexp="http://.*">
     <img src="${path_segment}gfx/http_url.gif" /> 
     <di:translate key="education.websites" />: <a href="${_}" class="urls"><mm:field name="name"><mm:isempty><mm:field name="url" /></mm:isempty></mm:field></a><%-- a class=urls, duh... --%>
    </mm:compare>
    <mm:compare regexp="mailto://.*">
      <img src="${path_segment}gfx/email_url.gif"/> 
      <di:translate key="education.email" />: <a href="${_}${empty _node.name ? '' : '?subject='}${_node.name}" class="urls"><mm:field name="name"><mm:isempty><mm:field name="url" /></mm:isempty></mm:field></a>
    </mm:compare>
  </mm:field>
</mm:node>
