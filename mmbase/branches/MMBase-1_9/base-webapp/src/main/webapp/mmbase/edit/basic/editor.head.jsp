<mm:functioncontainer>
  <mm:param name="id">mmbase-genericeditors.head</mm:param>
  <mm:listfunction set="components" name="blockClassification">
    <mm:stringlist referid="_.blocks">
      <mm:component  name="${_.component.name}" block="${_.name}" render="head" />
    </mm:stringlist>
  </mm:listfunction>
</mm:functioncontainer>