<p><mm:node number="category_main" >
  <mm:relatednodes type="categories" role="posrel" orderby="posrel.pos">
    <h1><mm:field name="title" /></h1>
    <mm:import id="depth">1</mm:import>
    <mm:tree type="categories" role="posrel" orderby="posrel.pos" searchdir="destination">
      <mm:first>
        <ul>
      </mm:first>
      <%@include file="cat.li.jsp" %>
      <mm:last>
        </ul>
      </mm:last>
    </mm:tree>
  </mm:relatednodes>
</mm:node></p>

