<!-- 'cluster' nodes -->
<mm:listcontainer path="news,posrel,urls">
  Total number of nodes before constraint: <mm:size /><br />
  <mm:constraint field="news.title" operator="LIKE" value="%xml%" />
  Total number of found nodes: <mm:size /><br />
  <mm:list>
    <mm:field name="news.title" />: <mm:field name="urls.url" /><br />
  </mm:list>
</mm:listcontainer>

