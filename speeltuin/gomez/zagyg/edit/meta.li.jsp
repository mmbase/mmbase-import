<mm:context>
  <li>
    <span class="editintro">
      <mm:field name="title" />:
    </span>
    <span class="editlinks">
      <mm:maywrite>
        <a href="<mm:url referids="referrer,language,wizard" page="${jsps}wizard.jsp">
          <mm:param name="context">admin</mm:param>
          <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
          </mm:url>">Wijzig categorietype
        </a>
      </mm:maywrite>
    </span>
    <ul><li>Sjabloon:
    <mm:relatednodescontainer type="templates" role="related">
      <mm:relatednodes>
        <em><mm:field name="name" /></em>
        <mm:last inverse="true">|</mm:last>
      </mm:relatednodes><br />
    </mm:relatednodescontainer></li>
    <li>Containers:
    <mm:relatednodescontainer type="templates" role="posrel">
      <mm:sortorder field="posrel.pos" />
      <mm:relatednodes>
        <em><mm:field name="name" /></em>
        <mm:last inverse="true">|</mm:last>
      </mm:relatednodes>
    </mm:relatednodescontainer></li>
    </ul>
  </li>
</mm:context>