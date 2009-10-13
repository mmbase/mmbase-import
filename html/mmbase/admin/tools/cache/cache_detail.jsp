<tr align="left">
  <th class="header" colspan="5"><span style="font-size: 120%"><%= cache.getName() %> </span>:<%= cache.getDescription() %> Cache</th>
  <th class="header" colspan="1">
  <% if(cache.isActive()) { %>
    <a href="<mm:url>
        <mm:param name="cache"><%=cache.getName()%></mm:param>
        <mm:param name="active">off</mm:param>
      </mm:url>" >Turn off</a> |
    <a href="<mm:url>
       <mm:param name="cache"><%=cache.getName()%></mm:param>
       <mm:param name="clear">clear</mm:param>
       </mm:url>">Clear</a>
        <% } else { %>
    <a href="<mm:url>
        <mm:param name="cache"><%=cache.getName()%></mm:param>
        <mm:param name="active">on</mm:param>
      </mm:url>" >Turn on</a>
  <% }  %>
  </th>
</tr>
<tr>
  <td class="data">Requests</td>
  <td class="data"><%= cache.getHits() + cache.getMisses() %></td>
  <td class="data">Hits</td>
  <td class="data"><%= cache.getHits() %></td>
  <td class="data">Misses</td>
  <td class="data"><%= cache.getMisses() %></td>
  <td class="data">Implementation</td>
  <td class="data"><%= cache.getImplementation() %></td>
</tr>
<tr>
  <td class="data">Size</td>
  <td class="data"><%= cache.size() %> / <%= cache.maxSize() %></td>
  <td class="data">Performance</td>
  <td class="data"><%= cache.getRatio() * 100 %> %</td>
  <td class="data">Show first 500 entry's of the cache</td>
  <td class="navigate">
    <a href="<mm:url page="cache/showcache.jsp"><mm:param name="cache"><%= cache.getName() %></mm:param></mm:url>" ><img src="<mm:url page="/mmbase/style/images/next.gif" />" alt="next" border="0" align="right"></a>
  </td>
</tr>
