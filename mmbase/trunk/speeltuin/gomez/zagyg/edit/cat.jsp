<p><mm:node number="category_main" >
  <mm:relatednodes type="categories" role="posrel" orderby="posrel.pos">
    <h1><mm:field name="title" /></h1>
    <mm:tree type="categories" role="posrel" orderby="posrel.pos" searchdir="destination">
      <mm:grow>
        <ul>
        <mm:onshrink></ul></mm:onshrink>
      </mm:grow>
      <%@include file="cat.li.jsp" %>
      <mm:shrink/>
    </mm:tree>
  </mm:relatednodes>
</mm:node></p>

