<mm:related id="typedefs" path="editor,typedef" orderby="editor.pos">
  <mm:context>
    <mm:first >|</mm:first>
    <mm:field name="editor.orderby">
      <mm:isnotempty><mm:import id="orderby"><mm:field name="editor.orderby"/></mm:import></mm:isnotempty>
    </mm:field>
    <mm:field name="editor.fields">
      <mm:isnotempty><mm:import id="fields"><mm:field name="editor.fields" /></mm:import></mm:isnotempty>
    </mm:field>
    <mm:field name="editor.directions">
      <mm:isnotempty><mm:import id="directions"><mm:field name="editor.directions" /></mm:import></mm:isnotempty>
    </mm:field>
    <mm:field name="editor.nodepath">
      <mm:isnotempty><mm:import id="nodepath"><mm:field name="editor.nodepath" /></mm:import></mm:isnotempty>
    </mm:field>
    <mm:field name="editor.constraints">
      <mm:isnotempty><mm:import id="constraints"><mm:field name="editor.constraints" /></mm:import></mm:isnotempty>
    </mm:field>
    <mm:field name="editor.size">
      <%-- call a list with objects related to this category.
           New items are related to category but NOT to the template
      --%>
      <mm:compare value="-1">
        <mm:import id="wiz_start">list</mm:import>
        <mm:import id="wiz_type">_origin</mm:import>
        <mm:import id="origin"><mm:write referid="cat" /></mm:import>
        <mm:import id="startnodes"><mm:write referid="cat" /></mm:import>
        <mm:notpresent referid="nodepath">
          <mm:import id="nodepath">categories,related,<mm:field name="typedef.name" /></mm:import>
        </mm:notpresent>
      </mm:compare>
      <%-- call a wizard to edit this type of object, passing the category number.
           use to edit the category itself
      --%>
      <mm:compare value="0">
        <mm:import id="wiz_start">list</mm:import>
        <mm:import id="wiz_type"></mm:import>
        <mm:notpresent referid="nodepath">
          <mm:import id="nodepath"><mm:field name="typedef.name" /></mm:import>
        </mm:notpresent>
      </mm:compare>
      <%-- call a wizard to relate one object to this category.
           New items are related to category and to the template
      --%>
      <mm:compare value="1">
        <mm:import id="wiz_start">wizard</mm:import>
        <mm:import id="wiz_type">_template</mm:import>
        <mm:import id="origin"><mm:write referid="template" /></mm:import>
      </mm:compare>
      <%-- call a list with objects related to this category.
           New items are related to category and to the template
      --%>
      <mm:isgreaterthan value="1">
        <mm:import id="wiz_start">wizard</mm:import>
        <mm:import id="wiz_type">_templates</mm:import>
       <mm:import id="origin"><mm:write referid="template" /></mm:import>
      </mm:isgreaterthan>
      <mm:field node="typedefs" name="editor.name">
        <mm:isnotempty>
          <mm:import id="title"><mm:write /></mm:import>
        </mm:isnotempty>
      </mm:field>
      <mm:node element="typedef">
        <mm:import id="wizard">tasks/<mm:field node="typedefs" name="editor.wizard" write="true"><mm:isempty><mm:field name="name" />/<mm:field name="name" /><mm:write referid="wiz_type" /></mm:isempty></mm:field></mm:import>
        <a href="<mm:url referids="title?,cat@objectnumber,context?,startnodes?,origin?,wizard,language,fields?,constraints?,orderby?,directions?,nodepath?" page="${jsps}${wiz_start}.jsp">
          <mm:param name="referrer"><mm:write referid="referrer" escape="none" />#c<mm:write referid="cat" /></mm:param>
          <mm:param name="search">yes</mm:param>
          </mm:url>">
          <mm:field node="typedefs" name="editor.name"><mm:write /><mm:isempty><mm:nodeinfo type="gui" /></mm:isempty></mm:field>
        </a>
      </mm:node>
    </mm:field>
    <mm:last inverse="true">, </mm:last>
  </mm:context>
</mm:related>

