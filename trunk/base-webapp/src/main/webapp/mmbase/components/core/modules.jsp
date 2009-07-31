<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content>
<mm:cloud rank="administrator">
<div
  class="mm_c c_core b_modules ${requestScope['org.mmbase.componentClassName']}"
  id="${requestScope['org.mmbase.componentId']}">

  <h3>${mm:string(requestScope['org.mmbase.framework.state'].renderer.block.title)}</h3>
  <table summary="MMBase modules" border="0" cellspacing="0" cellpadding="3">
    <caption>
      ${mm:string(requestScope['org.mmbase.framework.state'].renderer.block.description)}
    </caption>
    <tr>
      <th scope="col">Name</th>
      <th scope="col">Version</th>
      <th scope="col">Installed</th>
      <th scope="col">Maintainer</th>
      <th scope="col" class="center">Manage</th>
    </tr>
    <mm:nodelistfunction module="mmadmin" name="MODULES">
    <tr>
      <td>
        <mm:link page="modules-actions">
          <mm:param name="module"><mm:field name="item1" /></mm:param>
          <a title="view module" href="${_}"><mm:field name="item1" /></a>
        </mm:link>
      </td>
      <td><mm:field name="item2" /></td>
      <td><mm:field name="item3" /></td>
      <td><mm:field name="item4" /></td>
      <td class="center">
        <mm:link page="modules-actions">
          <mm:param name="module"><mm:field name="item1" /></mm:param>
          <a title="view module" href="${_}"><img src="${mm:link('/mmbase/style/images/next.png')}" alt="view" width="21" height="20" /></a>
        </mm:link>
      </td>
    </tr>
    </mm:nodelistfunction>
  </table>

</div>
</mm:cloud>
</mm:content>
