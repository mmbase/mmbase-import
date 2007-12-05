<tr align="left">
  <th colspan="4">
    <%= cache.getName() %>:<%= cache.getDescription() %> Cache
  </th>
  <th colspan="2">
  <% if(cache.isActive()) { %>
    <mm:link>
      <mm:param name="cache"><%= cache.getName() %></mm:param>
      <mm:param name="active">off</mm:param>
      <a href="${_}">Turn off</a>
    </mm:link> | 
    <mm:link>
      <mm:param name="cache"><%= cache.getName() %></mm:param>
      <mm:param name="clear">clear</mm:param>
      <a href="${_}">Clear</a>
    </mm:link>    
  <% } else { %>
    <mm:link>
      <mm:param name="cache"><%= cache.getName() %></mm:param>
      <mm:param name="active">on</mm:param>
      <a href="${_}">Turn on</a>
    </mm:link>
  <% } %>
  </th>
</tr><tr>
  <td>Requests</td>
  <td><%= cache.getHits() + cache.getMisses() %></td>
  <td>Hits</td>
  <td><%= cache.getHits() %></td>
  <td>Misses</td>
  <td><%= cache.getMisses() %></td>
</tr><tr>
  <td>Size</td>
  <td><%= cache.size() %> / <%= cache.maxSize() %></td>
  <td>Performance</td>
  <td><%= cache.getRatio() * 100 %> %</td>
  <td>Show first 500 entry's of the cache</td>
  <td>
    <mm:link page="showcache">
      <mm:param name="cache"><%= cache.getName() %></mm:param>
      <a href="${_}"><mm:link page="/mmbase/style/images/next.gif"><img src="${_}" alt="next" /></a></mm:link>
    </mm:link>
  </td>
</tr>
