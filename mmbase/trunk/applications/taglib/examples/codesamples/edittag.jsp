<mm:edit type="yammeditor">
  <%--
      optional parameters

  <mm:param name="url" value="/yammeditor/yammeditor.jsp" />
  <mm:param name="icon">/mmbase/edit/my_editors/img/mmbase-edit.gif</mm:param>
  --%>
  <mm:node number="default.mags" notfound="skip">
    <h2><mm:field name="title" /></h2>
    <mm:related path="posrel,news"
      fields="news.number,news.title,posrel.pos" orderby="posrel.pos">
      <mm:node element="news">
        <strong><mm:field name="title" /></strong><br />
        <mm:related path="people" 
          fields="people.email">
          by <mm:field name="people.email" /><br />
        </mm:related>
      </mm:node>
    </mm:related>
  </mm:node>
</mm:edit>
